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
package net.sourceforge.seqware.pipeline.plugins.checkdb.plugins;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDB;
import net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDBPluginInterface;
import net.sourceforge.seqware.pipeline.plugins.checkdb.SelectQueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.io.FileUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * Checks the metadb for issues with attributes
 * @author dyuen
 */
@ServiceProvider(service = CheckDBPluginInterface.class)
public class AttributePlugin implements CheckDBPluginInterface {

    @Override
    public void check(SelectQueryRunner qRunner, SortedMap<Level, Set<String>> result) throws SQLException {
        try {
            String path = AttributePlugin.class.getResource("duplicate_attribute_keys.sql").getPath();
            String query = FileUtils.readFileToString(new File(path));
            List<Integer> executeQuery = qRunner.executeQuery(query, new ColumnListHandler<Integer>());
            CheckDB.processOutput(result, Level.SEVERE,  "Entities with duplicate attribute keys in non-sample tables: " , executeQuery);
            path = WorkflowRunConventionsPlugin.class.getResource("duplicate_sample_attribute_keys.sql").getPath();
            query = FileUtils.readFileToString(new File(path));
            executeQuery = qRunner.executeQuery(query, new ColumnListHandler<Integer>());
            CheckDB.processOutput(result, Level.SEVERE,  "Samples with duplicate attribute keys: " , executeQuery);
            
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
    
}
