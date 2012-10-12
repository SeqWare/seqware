/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.common.factory;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

/**
 * <p>DBAccess class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class DBAccess {

    private static MetadataDB db;

    /**
     * <p>get.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.metadata.MetadataDB} object.
     */
    public static MetadataDB get() {
        try {
            if (db!=null &&
                    ((db.getDb()!=null && db.getDb().isClosed()))) 
                    //|| db.getSql()!=null && db.getSql().isClosed()))
            {
                db.clean_up();
                db = null;
            }
        } catch (SQLException ex) {
            Log.error("Closing DB connection and creating a new one failed", ex);
        }
        if (db == null) {
            db = new MetadataDB();
            //Log.debug("DBAccess is using " + user + " " + pass + " on " + connection);
            DataSource ds = null;
            try {
                InitialContext initCtx = new InitialContext();
                ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/SeqWareMetaDB");
            } catch (NamingException ex) {
                Log.fatal(ex);
                ex.printStackTrace();
            }
            if (ds != null) {
                db.init(ds);
            } else {
                Map<String, String> settings = null;
                try {
                    settings = ConfigTools.getSettings();
                    String connection = "jdbc:postgresql://" + settings.get("SW_DB_SERVER") + "/" + settings.get("SW_DB");
                    String user = settings.get("SW_DB_USER");
                    String pass = settings.get("SW_DB_PASS");
                    Log.debug("DBAccess is using " + user + " " + pass + " on " + connection);
                    db.init(connection, user, pass);
                } catch (Exception ex) {
                    Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return db;
    }

    /**
     * <p>close.</p>
     */
    public static void close() {
        if (db != null) {
            db.clean_up();
            db = null;
        }
    }

    private DBAccess() {
    }
}
