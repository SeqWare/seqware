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
package com.github.seqware.queryengine;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;

/**
 * Constants will be defined here until we move them out to a properties file
 *
 * @author dyuen
 */
public class Constants {

    /**
     * Back-end storage implementations will reference this to decide whether to
     * persist objects permanently and/or wipe out existing ones
     */
    public final static boolean PERSIST = true;
    /**
     * Use the properties defined for HBase to connect to a remote HBase
     * instance
     */
    public final static boolean HBASE_REMOTE_TESTING = false;
    private final static Map<String, String> SQWDEV = Maps.newHashMap(
            ImmutableMap.<String, String>builder().
            put("hbase.zookeeper.quorum", "sqwdev.res").
            put("hbase.zookeeper.property.clientPort", "2181").
            put("hbase.master", "sqwdev.res:60000").
            build());
    private final static Map<String, String> HBOOT = Maps.newHashMap(
            ImmutableMap.<String, String>builder().
            put("hbase.zookeeper.quorum", "hboot.res").
            put("hbase.zookeeper.property.clientPort", "2181").
            put("hbase.master", "hboot.res:60000").
            build());
    /**
     * Properties used when connecting to a remote instance of HBase
     */
    public final static Map<String, String> HBASE_PROPERTIES = HBOOT;
}
