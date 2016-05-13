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

import net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDB;
import net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDBPluginInterface;
import net.sourceforge.seqware.pipeline.plugins.checkdb.SelectQueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.io.IOUtils;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Checks the metadb for orphans that are not connected to any other entities.
 * 
 * This plugin demonstrates queries that use common table expressions and recursion.
 * 
 * @author dyuen
 */
@ServiceProvider(service = CheckDBPluginInterface.class)
public class WorkflowRunConventionsPlugin implements CheckDBPluginInterface {

    @Override
    public void check(SelectQueryRunner qRunner, SortedMap<Level, Set<String>> result) throws SQLException {
        try {
            /**
             * May not be true for downsteam workflow runs List<Integer> executeQuery = qRunner.executeQuery(
             * "select sw_accession from workflow_run WHERE workflow_run_id NOT IN (select workflow_run_id FROM ius_workflow_runs);", new
             * ColumnListHandler<Integer>()); CheckDB.processOutput(result, Level.TRIVIAL,
             * "Workflow runs not connected to an IUS via ius_workflow_runs: " , executeQuery);
             **/
            // workflow runs not connected to a study
            String query = IOUtils.toString(AttributePlugin.class.getResourceAsStream("workflow_runs_not_connected_to_study.sql"),
                    StandardCharsets.UTF_8);
            List<Object[]> workflowRunStudyPairs = qRunner.executeQuery(query, new ArrayListHandler());

            List<Integer> unreachableByStudy = new ArrayList<>();
            // number studies -> workflow runs
            SortedMap<Integer, SortedSet<Integer>> reachableByMultipleStudies = new TreeMap<>();

            for (Object[] pair : workflowRunStudyPairs) {
                int studyCount = Integer.parseInt(pair[1].toString());
                if (pair[0] == null) {
                    continue;
                }
                int swAccession = Integer.parseInt(pair[0].toString());
                if (studyCount == 0) {
                    unreachableByStudy.add(swAccession);
                } else if (studyCount > 1) {
                    if (!reachableByMultipleStudies.containsKey(studyCount)) {
                        reachableByMultipleStudies.put(studyCount, new TreeSet<>());
                    }
                    reachableByMultipleStudies.get(studyCount).add(swAccession);
                }
            }
            CheckDB.processOutput(result, Level.SEVERE, "'Completed' Workflow runs not reachable by studies: ", unreachableByStudy);
            // workflow runs connected to more than one study
            if (reachableByMultipleStudies.size() > 0) {
                for (Entry<Integer, SortedSet<Integer>> e : reachableByMultipleStudies.entrySet()) {
                    CheckDB.processOutput(result, Level.WARNING, "'Completed' Workflow runs reachable by " + e.getKey() + " studies: ",
                            new ArrayList<>(e.getValue()));
                }
            }
            query = IOUtils.toString(AttributePlugin.class.getResourceAsStream("workflow_runs_not_connected_in_hierarchy.sql"),StandardCharsets.UTF_8);
            List<Integer> executeQuery = qRunner.executeQuery(query, new ColumnListHandler<>());
            CheckDB.processOutput(result, Level.SEVERE,
                    "'Completed' Workflow runs reachable by ius_workflow_runs but not via the processing_hierarchy: ", executeQuery);

            query = IOUtils.toString(AttributePlugin.class.getResourceAsStream("new_input_files_versus_old.sql"),StandardCharsets.UTF_8);
            executeQuery = qRunner.executeQuery(query, new ColumnListHandler<>());
            CheckDB.processOutput(result, Level.TRIVIAL,
                    "Workflow runs with input files via workflow_run_input_files but not via the processing hierarchy: ", executeQuery);

            query = IOUtils.toString(AttributePlugin.class.getResourceAsStream("old_input_files_versus_new.sql"),StandardCharsets.UTF_8);
            executeQuery = qRunner.executeQuery(query, new ColumnListHandler<>());
            CheckDB.processOutput(result, Level.TRIVIAL,
                    "Workflow runs with input files via the processing hierarchy but not via workflow_run_input_files: ", executeQuery);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

}
