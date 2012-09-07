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
     * Sets a prefix for your tables in at least the HBaseStorage back-end, 
     * change this when developing to avoid name collisions with other developers
     */
    public static final String NAMESPACE = "batman";

    /**
     * Properly set this task if you want your compiled jar files to automatically load in the cluster when
     * performing map/reduce tasks
     */
    public static final String DEVELOPMENT_DEPENDENCY = "file:/home/dyuen/seqware_github/seqware-distribution/target/seqware-queryengine-0.12.0-full.jar";


    /**
     * Show the rather verbose map/reduce messages.
     * This functionality moved out to the more standard log4j.properties files in the resources directories
     */
    // public final static boolean MAP_REDUCE_LOGGING = true;
    
    /**
     * Back-end storage implementations will reference this to decide whether to
     * persist objects permanently and/or wipe out existing ones
     */
    public final static boolean PERSIST = true;

    /**
     * Use the properties defined for HBase to connect to a remote HBase
     * instance.
     *
     * Important: for local development, set the variable to false.
     */
    public final static boolean HBASE_REMOTE_TESTING = true;


    /**
     * Configuration for using HBase on a local machine.
     */
    private final static Map<String, String> LOCAL = Maps.newHashMap(ImmutableMap.<String, String>builder().build());

    /**
     * Configuration for using HBase on the Seqware development cluster.
     */
    private final static Map<String, String> SQWDEV = Maps.newHashMap(
            ImmutableMap.<String, String>builder().
            put("hbase.zookeeper.quorum", "sqwdev.res.oicr.on.ca").
            put("hbase.zookeeper.property.clientPort", "2181").
            put("hbase.master", "sqwdev.res.oicr.on.ca:60000").
            put("mapred.job.tracker", "sqwdev.res.oicr.on.ca:8021").
            put("fs.default.name", "hdfs://sqwdev.res.oicr.on.ca:8020").
            build());

    /**
     * Configuration for using HBase on OICR's HBase cluster.
     */
    private final static Map<String, String> HBOOT = Maps.newHashMap(
            ImmutableMap.<String, String>builder().
            put("hbase.zookeeper.quorum", "hboot.res.oicr.on.ca").
            put("hbase.zookeeper.property.clientPort", "2181").
            put("hbase.master", "hboot.res.oicr.on.ca:60000").
            put("mapred.job.tracker", "hboot.res.oicr.on.ca:8021").
            put("fs.default.name", "hdfs://hboot.res.oicr.on.ca:8020").
            build());
    /**
     * Properties used when connecting to a remote instance of HBase.
     *
     * Use SQWDEV to configure HBase to use the Seqware development cluster,
     * use HBOOT to configure HBase to use OICR's HBase cluster,
     * use LOCAL to configure HBase for use on a local machine.
     */
    public final static Map<String, String> HBASE_PROPERTIES = HBOOT;
    
    /**
     * Regular Expression for suitable rowKeys
     */
    public static final String refRegex = "[a-zA-Z_0-9]+";
}
