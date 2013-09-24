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

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDBPluginInterface;
import net.sourceforge.seqware.pipeline.plugins.checkdb.SelectQueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.openide.util.lookup.ServiceProvider;

/**
 * Checks the metadb for orphans that are not connected to any other entities
 * @author dyuen
 */
@ServiceProvider(service=CheckDBPluginInterface.class)
public class OrphanCheckerPlugin implements CheckDBPluginInterface {

    @Override
    public void check(SelectQueryRunner qRunner, SortedMap<Level, Set<String>> result) throws SQLException {

        // orphans when nothing references a particular entity
        List<Integer> executeQuery = qRunner.executeQuery("SELECT sw_accession FROM experiment WHERE experiment_id NOT IN (SELECT experiment_id FROM sample);", new ColumnListHandler<Integer>());
        if (executeQuery.size() > 0) result.get(Level.TRIVIAL).add("Unreferenced Experiments: " + executeQuery.toString());
        executeQuery = qRunner.executeQuery("SELECT sw_accession FROM sample WHERE sample_id NOT IN (select sample_id from ius UNION select sample_id from lane);", new ColumnListHandler<Integer>());
        if (executeQuery.size() > 0) result.get(Level.TRIVIAL).add("Unreferenced samples: "  + executeQuery.toString());
        executeQuery = qRunner.executeQuery("SELECT sw_accession FROM ius WHERE ius_id NOT IN (select ius_id from processing_ius);", new ColumnListHandler<Integer>());
        if (executeQuery.size() > 0) result.get(Level.TRIVIAL).add("Unreferenced IUSes: " + executeQuery.toString());
        executeQuery = qRunner.executeQuery("SELECT sw_accession FROM file WHERE file_id NOT IN (select file_id from processing_files);", new ColumnListHandler<Integer>());
        if (executeQuery.size() > 0) result.get(Level.TRIVIAL).add("Unreferenced Files: " + executeQuery.toString());
        executeQuery = qRunner.executeQuery("SELECT sw_accession FROM workflow WHERE workflow_id NOT IN (select workflow_id from workflow_run);", new ColumnListHandler<Integer>());
        if (executeQuery.size() > 0) result.get(Level.TRIVIAL).add("Unreferenced Workflows: " + executeQuery.toString());       
        
        // orphans when what should really be a not-null foreign key is null
        executeQuery = qRunner.executeQuery("SELECT sw_accession FROM sample WHERE experiment_id IS NULL;", new ColumnListHandler<Integer>());
        if (executeQuery.size() > 0) result.get(Level.SEVERE).add("Samples not attached to experiments: " + executeQuery.toString());
        executeQuery = qRunner.executeQuery("SELECT sw_accession FROM lane WHERE sequencer_run_id IS NULL;", new ColumnListHandler<Integer>());
        if (executeQuery.size() > 0) result.get(Level.SEVERE).add("Lanes not attached to sequencer runs: " + executeQuery.toString());
      
        // processing, which is just weird
        executeQuery = qRunner.executeQuery("SELECT sw_accession FROM processing WHERE workflow_run_id IS NULL AND ancestor_workflow_run_id IS NULL;", new ColumnListHandler<Integer>());
        if (executeQuery.size() > 0) result.get(Level.SEVERE).add("Processings attached to no workflow runs: " + executeQuery.toString());
    }
    
}
