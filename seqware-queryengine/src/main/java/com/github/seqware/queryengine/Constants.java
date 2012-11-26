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
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.apache.log4j.Logger;

/**
 * Defaults for our Constants are defined here and can be overridden by entries
 * in the ~/.seqware/settings file.
 *
 * An example of the contents of such a file follows (update QE_DEVELOPMENT_DEPENDENCY for your version):
 *
 * #
 * # SEQWARE QUERY ENGINE SETTINGS # QE_NAMESPACE=IRON_MAN
 * QE_DEVELOPMENT_DEPENDENCY=file:/home/dyuen/seqware_github/seqware-distribution/target/seqware-queryengine-0.13.3-full.jar
 * QE_PERSIST=true QE_HBASE_REMOTE_TESTING=true # Connect to either HBOOT,
 * SQWDEV, or an implicit localhost QE_HBASE_PROPERTIES=HBOOT
 *
 * # PROPERTIES FOR HBOOT QE_HBOOT_HBASE_ZOOKEEPER_QUORUM=hboot.res.oicr.on.ca
 * QE_HBOOT_HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT=2181
 * QE_HBOOT_HBASE_MASTER=hboot.res.oicr.on.ca:60000
 * QE_HBOOT_MAPRED_JOB_TRACKER=hboot.res.oicr.on.ca:8021
 * QE_HBOOT_FS_DEFAULT_NAME=hdfs://hboot.res.oicr.on.ca:8020
 * QE_HBOOT_FS_DEFAULTFS=hdfs://hboot.res.oicr.on.ca:8020
 * QE_HBOOT_FS_HDFS_IMPL=org.apache.hadoop.hdfs.DistributedFileSystem
 *
 * # PROPERTIES FOR SQWDEV
 * QE_SQWDEV_HBASE_ZOOKEEPER_QUORUM=sqwdev.res.oicr.on.ca
 * QE_SQWDEV_HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT=2181
 * QE_SQWDEV_HBASE_MASTER=sqwdev.res.oicr.on.ca:60000
 * QE_SQWDEV_MAPRED_JOB_TRACKER=sqwdev.res.oicr.on.ca:8021
 * QE_SQWDEV_FS_DEFAULT_NAME=hdfs://sqwdev.res.oicr.on.ca:8020
 * QE_SQWDEV_FS_DEFAULTFS=hdfs://sqwdev.res.oicr.on.ca:8020
 * QE_SQWDEV_FS_HDFS_IMPL=org.apache.hadoop.hdfs.DistributedFileSystem
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class Constants {

    /**
     * Regular Expression for suitable rowKeys
     */
    public static final String refRegex = "[a-zA-Z_0-9]+";
    /**
     * This map, if set from something like a .settings file or plug-in
     * parameters will override hard-coded constants
     */
    private static Map<String, String> SETTINGS_MAP = null;

    static {
        try {
            SETTINGS_MAP = ConfigTools.getSettings();
        } catch (Exception e) {
            Logger.getLogger(Constants.class.getName()).fatal(e);
            Logger.getLogger(Constants.class.getName()).info("Unable to read settings file: " + e.getMessage() + " expected for plug-ins");
        }
    }

    public enum Term {

        /**
         * Sets a prefix for your tables in at least the HBaseStorage back-end,
         * change this when developing to avoid name collisions with other
         * developers
         */
        NAMESPACE("QE_NAMESPACE", "batman", String.class),
        /**
         * Properly set this task if you want your compiled jar files to
         * automatically load in the cluster when performing map/reduce tasks
         */
        DEVELOPMENT_DEPENDENCY("QE_DEVELOPMENT_DEPENDENCY", "file:/home/dyuen/seqware_github/seqware-distribution/target/seqware-queryengine-0.12.0-full.jar", String.class),
        /**
         * Back-end storage implementations will reference this to decide
         * whether to persist objects permanently and/or wipe out existing ones
         */
        PERSIST("QE_PERSIST", true, Boolean.class),
        /**
         * Use the properties defined for HBase to connect to a remote HBase
         * instance.
         *
         * Important: for local development, set the variable to false.
         */
        HBASE_REMOTE_TESTING("QE_HBASE_REMOTE_TESTING", false, Boolean.class),
        /**
         * Properties used when connecting to a remote instance of HBase.
         *
         * Use SQWDEV to configure HBase to use the SeqWare development cluster,
         * use HBOOT to configure HBase to use OICR's HBase cluster, use LOCAL
         * to configure HBase for use on a local machine.
         */
        HBASE_PROPERTIES("QE_HBASE_PROPERTIES", LOCAL, Map.class);
        private final String term_name;
        private final Object term_default;
        private Class type;

        Term(String term_name, Object term_default, Class type) {
            this.term_name = term_name;
            this.term_default = term_default;
            this.type = type;
        }

        public String getTerm_name() {
            return term_name;
        }

        public <T> T getTermValue(Class<T> type) {
            if (Constants.SETTINGS_MAP != null && Constants.SETTINGS_MAP.containsKey(term_name)) {
                String value = Constants.SETTINGS_MAP.get(term_name);
                if (type.equals(Boolean.class)) {
                    Boolean parseBoolean = Boolean.parseBoolean(value);
                    return (T) parseBoolean;
                } else if (type.equals(Map.class)) {
                    if (value.equalsIgnoreCase("LOCAL") || value.equalsIgnoreCase("localhost")) {
                        return (T) LOCAL;
                    }
                    return (T) Maps.newHashMap(
                            ImmutableMap.<String, String>builder().
                            put("hbase.zookeeper.quorum", Constants.SETTINGS_MAP.get("QE_" + value + "_HBASE_ZOOKEEPER_QUORUM")).
                            put("hbase.zookeeper.property.clientPort", Constants.SETTINGS_MAP.get("QE_" + value + "_HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT")).
                            put("hbase.master", Constants.SETTINGS_MAP.get("QE_" + value + "_HBASE_MASTER")).
                            put("mapred.job.tracker", Constants.SETTINGS_MAP.get("QE_" + value + "_MAPRED_JOB_TRACKER")).
                            put("fs.default.name", Constants.SETTINGS_MAP.get("QE_" + value + "_FS_DEFAULT_NAME")).
                            put("fs.defaultFS", Constants.SETTINGS_MAP.get("QE_" + value + "_FS_DEFAULTFS")).
                            put("fs.hdfs.impl", Constants.SETTINGS_MAP.get("QE_" + value + "_FS_HDFS_IMPL")).
                            //put("hadoop.security.token.service.use_ip", Constants.SETTINGS_MAP.get("QE_"+value+"_USE_IP")).
                            build());
                } else{
                    return (T)value;
                }
            }
            return (T) term_default;
        }
    }
    /**
     * examples of Hadoop settings used to connect to a Hadoop/HBase cluster
     * follow
     */
    /**
     * Configuration for using HBase on a local machine.
     */
    private final static Map<String, String> LOCAL = Maps.newHashMap(ImmutableMap.<String, String>builder().build());
    /**
     * Configuration for using HBase on the SeqWare development cluster.
     */
    private final static Map<String, String> SQWDEV = Maps.newHashMap(
            ImmutableMap.<String, String>builder().
            put("hbase.zookeeper.quorum", "sqwdev.res.oicr.on.ca").
            put("hbase.zookeeper.property.clientPort", "2181").
            put("hbase.master", "sqwdev.res.oicr.on.ca:60000").
            put("mapred.job.tracker", "sqwdev.res.oicr.on.ca:8021").
            put("fs.default.name", "hdfs://sqwdev.res.oicr.on.ca:8020").
            put("fs.defaultFS", "hdfs://sqwdev.res.oicr.on.ca:8020").
            put("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem").
            //put("hadoop.security.token.service.use_ip", "false").
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
            put("fs.defaultFS", "hdfs://hboot.res.oicr.on.ca:8020").
            put("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem").
            //put("hadoop.security.token.service.use_ip", "false").
            build());

    /**
     * Set the settings map, overriding hard-coded values for our constants.
     *
     * @param SETTINGS_MAP a {@link java.util.Map} object.
     */
    public static void setSETTINGS_MAP(Map<String, String> SETTINGS_MAP) {
        Constants.SETTINGS_MAP = SETTINGS_MAP;
        Logger.getLogger(Constants.class.getName()).info("Constants configured with: host: " + Constants.Term.HBASE_PROPERTIES.getTermValue(Map.class).toString() + " namespace: " +  Constants.Term.NAMESPACE.getTermValue(String.class));
    }

    /**
     * Get the settings map.
     *
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, String> getSETTINGS_MAP() {
        return SETTINGS_MAP;
    }
}
