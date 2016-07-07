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

import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * <p>
 * DBAccess class.
 * </p>
 * 
 * @author mtaschuk
 * @version $Id: $Id
 */
public class DBAccess {

    private static final ThreadLocal<MetadataDB> METADATA_DB_WRAPPER = new ThreadLocal<>();

    public static MetadataDB get() {
        MetadataDB mdb = METADATA_DB_WRAPPER.get();

        if (mdb == null) {
            mdb = create();
            METADATA_DB_WRAPPER.set(mdb);
            return mdb;
        }

        boolean cleanup = true;
        try {
            cleanup = mdb.getDb().isClosed();
        } catch (SQLException e) {
        }

        if (cleanup) {
            close();
            return get();
        } else {
            return mdb;
        }
    }

    private static MetadataDB create() {
        DataSource ds = getDataSource();

        if (ds != null) {
            Log.debug("Instantiate MetadataDB via datasource " + ds.getClass());
            try {
                return new MetadataDB(ds);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            Log.debug("Obtain MetadataDB via MetadataFactory");
            Map<String, String> settings = ConfigTools.getSettings();
            return MetadataFactory.getDB(settings);
        }
    }

    public static DataSource getDataSource() {
        DataSource ds = null;
        try {
            InitialContext initCtx = new InitialContext();
            ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/SeqWareMetaDB");
        } catch (NamingException ex) {
            Log.info("Could not lookup database via context", ex);
        }
        return ds;
    }

    /**
     * <p>
     * close.
     * </p>
     */
    public static synchronized void close() {
        MetadataDB mdb = METADATA_DB_WRAPPER.get();
        if (mdb != null) {
            Log.debug(METADATA_DB_WRAPPER.get().toString() + " was closed ");
            mdb.clean_up();
        }
        METADATA_DB_WRAPPER.remove();
    }

    private DBAccess() {
    }

    @Override
    public void finalize(){
        METADATA_DB_WRAPPER.remove();
    }
}
