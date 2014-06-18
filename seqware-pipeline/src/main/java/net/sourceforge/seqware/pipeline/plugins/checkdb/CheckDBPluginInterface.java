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
import java.util.Set;
import java.util.SortedMap;

/**
 * Specifies an interface for database checks that can be run against the seqware database
 * 
 * @author dyuen
 */
public interface CheckDBPluginInterface {

    /**
     * @param qRunner
     *            the query runner allows for the running of read-only SQL statements with an arbitrary ResultHandler
     * @param results
     * @throws SQLException
     */
    public void check(SelectQueryRunner qRunner, SortedMap<Level, Set<String>> results) throws SQLException;

    public enum Level {
        SEVERE, WARNING, TRIVIAL
    }

}
