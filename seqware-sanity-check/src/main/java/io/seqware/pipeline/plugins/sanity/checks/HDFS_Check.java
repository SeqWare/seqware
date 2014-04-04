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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.common.metadata.Metadata;
import static net.sourceforge.seqware.common.util.Rethrow.rethrow;
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
        FileSystem fileSystem = null;

        HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
        if (settings.isEmpty()) {
            return false;
        } else if (!settings.containsKey("FS.DEFAULTFS") || !settings.containsKey("FS.HDFS.IMPL")) {
            return false;
        } else if (!settings.containsKey("HBASE.ZOOKEEPER.QUORUM") || !settings.containsKey("HBASE.ZOOKEEPER.PROPERTY.CLIENTPORT") || !settings.containsKey("HBASE.MASTER") || !settings.containsKey("MAPRED.JOB.TRACKER")) {
            return false;
        } else if (!settings.containsKey("OOZIE_APP_ROOT")) {
            return false;
        }

        try {
            Configuration conf = new Configuration();

            conf.set("hbase.zookeeper.quorum", settings.get("HBASE.ZOOKEEPER.QUORUM"));
            conf.set("hbase.zookeeper.property.clientPort", settings.get("HBASE.ZOOKEEPER.PROPERTY.CLIENTPORT"));
            conf.set("hbase.master", settings.get("HBASE.MASTER"));
            conf.set("mapred.job.tracker", settings.get("MAPRED.JOB.TRACKER"));
            conf.set("fs.default.name", settings.get("FS.DEFAULTFS"));
            conf.set("fs.defaultfs", settings.get("FS.DEFAULTFS"));
            conf.set("fs.hdfs.impl", settings.get("FS.HDFS.IMPL"));
            fileSystem = FileSystem.get(conf);
            Path path = new Path(settings.get("OOZIE_APP_ROOT") + "/" + "test");
            fileSystem.mkdirs(path);
            System.out.println(fileSystem.getFileStatus(path).getPath());

        } catch (IOException ex) {
            System.err.println("Error connecting to hdfs" + ex.getMessage());
            return false;
        } finally {
            try {
                if (fileSystem != null) {
                    fileSystem.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(HDFS_Check.class.getName()).log(Level.SEVERE, null, ex);
            }
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
