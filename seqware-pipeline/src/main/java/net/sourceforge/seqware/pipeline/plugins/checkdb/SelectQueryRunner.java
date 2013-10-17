/*
 * Copyright (C) 2013 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins.checkdb;

import java.sql.SQLException;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * A class that allows plugin writers to only do select queries
 * without any risk of leaving open connections or other unfriendliness
 * @author dyuen
 */
public final class SelectQueryRunner {
    private final MetadataDB mdb;
    
    protected SelectQueryRunner(MetadataDB mdb){
        this.mdb = mdb;
    }
    
    public final <T> T executeQuery(String s, ResultSetHandler<T> h) throws SQLException {
        return this.mdb.executeQuery(s, h);
    }
}
