/*
 * Copyright (C) 2014 SeqWare
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
package io.seqware.pipeline.plugins.sanity.checks;

import io.seqware.pipeline.plugins.sanity.QueryRunner;
import io.seqware.pipeline.plugins.sanity.SanityCheckPluginInterface;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.openide.util.lookup.ServiceProvider;

/**
 * Checks to ensure that you are connected to hdfs by creating a file and
 * deleting it on exit
 *
 * @author Raunaq Suri
 */
@ServiceProvider(service = SanityCheckPluginInterface.class)
public class HDFS_Check implements SanityCheckPluginInterface {

    @Override
    public boolean isTutorialTest() {
        return false;
    }

    @Override
    public boolean isMasterTest() {
        return true;
    }

    @Override
    public boolean isDBTest() {
        return false;
    }

    @Override
    public boolean check(QueryRunner qRunner, Metadata metadataWS) throws SQLException {

        HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
        if (settings.isEmpty()) {
            return false;
        } else if (!settings.containsKey("FS.DEFAULTFS") || !settings.containsKey("FS.HDFS.IMPL")) {
            return false;
//        } else if (!settings.containsKey("HBASE.ZOOKEEPER.QUORUM") || !settings.containsKey("HBASE.ZOOKEEPER.PROPERTY.CLIENTPORT") || !settings.containsKey("HBASE.MASTER") || !settings.containsKey("MAPRED.JOB.TRACKER")){
//            return false;
        }

        try {
            Path path = new Path(System.getProperty("user.home") + "/" + "test");
            Configuration conf = new Configuration();
            conf.set("fs.defaultfs", settings.get("FS.DEFAULTFS"));
            conf.set("fs.hdfs.impl", settings.get("FS.HDFS.IMPL"));
            FileSystem filesystem;
            filesystem = FileSystem.get(conf);
            filesystem.create(path);
            filesystem.deleteOnExit(path);

        } catch (IOException ex) {
            System.err.println("Error connecting to hdfs" + ex.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Cannot access hdfs filesystem";
    }

    @Override
    public int getPriority() {
        return 10;
    }

}
