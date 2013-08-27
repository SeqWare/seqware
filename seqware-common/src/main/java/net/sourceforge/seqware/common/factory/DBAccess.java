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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

/**
 * <p>DBAccess class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class DBAccess {
  
  private static final ThreadLocal<MetadataDB> metadataDBWrapper = new ThreadLocal<MetadataDB>() {
    @Override
    protected synchronized MetadataDB initialValue() {
      DataSource ds = null;
      try {
        InitialContext initCtx = new InitialContext();
        ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/SeqWareMetaDB");
      } catch (NamingException ex) {
        Log.info("Could not lookup database via context", ex);
      }

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
  };

  public synchronized static MetadataDB get() {
    MetadataDB mdb = metadataDBWrapper.get();

    boolean cleanup = true;
    try {
      cleanup = mdb.getDb().isClosed();
    } catch (SQLException e) {
    }

    if (cleanup) {
      mdb.clean_up();
      metadataDBWrapper.remove();
      return metadataDBWrapper.get();
    } else {
      return mdb;
    }
  }

    /**
     * <p>close.</p>
     */
    public synchronized static void close() {
        if (metadataDBWrapper.get() != null) {
            Log.debug(metadataDBWrapper.get().toString() + " was closed ");
            metadataDBWrapper.get().clean_up();
            metadataDBWrapper.set(null);
            //db = null;
        }
        metadataDBWrapper.remove();
    }

    private DBAccess() {
    }
}
