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
package io.seqware.pipeline.plugins;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.seqware.common.model.ProcessingStatus;
import io.seqware.common.model.WorkflowRunStatus;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionSpecBuilder;
import net.sourceforge.seqware.common.model.FileProvenanceParam;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugins.fileprovenance.ProvenanceUtility;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * Pulls back a file provenance report, runs an arbitrarily complex SQL query on the results, and saves the results as a protobuf binary for
 * use as a part of interpreted language deciders.
 * 
 * @author dyuen
 * @version 1.1.0
 */
@ServiceProvider(service = PluginInterface.class)
public class FileProvenanceQueryTool extends Plugin {

    private static final String TABLE_NAME = "FILE_REPORT";
    private final ArgumentAcceptingOptionSpec<String> outFileSpec;
    private final ArgumentAcceptingOptionSpec<String> querySpec;
    private final ArgumentAcceptingOptionSpec<String> inFileSpec;
    private final OptionSpecBuilder useH2InMemorySpec;

    public FileProvenanceQueryTool() {
        ProvenanceUtility.configureFileProvenanceParams(parser);
        this.inFileSpec = parser.accepts(
                "in",
                "The tab separated file that will be used instead of pulling back a " + "fresh file provenance report. "
                        + "Must be a tab separated file with a fixed number of columns with "
                        + "a provided header (that will be used for column names). ").withRequiredArg();
        this.outFileSpec = parser.accepts("out", "The tab separated file into which the results will be written.").withRequiredArg()
                .required();
        this.useH2InMemorySpec = parser.accepts("H2mem", "Use H2 in-memory database for better performance (but with memory limits)");
        this.querySpec = parser.accepts("query", "The standard SQL query that should be run. Table queried should be " + TABLE_NAME)
                .withRequiredArg().required();
    }

    @Override
    public String get_description() {
        return "Pulls back a file provenance report (or a previous tab-separated file), runs an arbitrarily complex SQL query on the results and saves the results as a tab separated file for use as a part of interpreted language deciders.";
    }

    @Override
    public ReturnValue init() {
        if (!options.has(inFileSpec) && !ProvenanceUtility.checkForValidOptions(options)) {
            println("One of the various contraints or '--all' must be specified.");
            println(this.get_syntax());
            return new ReturnValue(ReturnValue.INVALIDPARAMETERS);
        }

        return new ReturnValue();
    }

    @Override
    public ReturnValue do_test() {
        return new ReturnValue();
    }

    @Override
    public ReturnValue do_run() {
        Path randomTempDirectory = null;
        Path originalReport = null;
        Path bulkImportFile = null;
        try {
            if (options.has(this.inFileSpec)) {
                originalReport = FileSystems.getDefault().getPath(options.valueOf(inFileSpec));
            } else {
                originalReport = populateOriginalReportFromWS();
            }

            List<String> headers;
            List<Boolean> numericDataType;
            // construct column name and datatypes
            // convert file provenance report into derby bulk load format
            try (BufferedReader originalReader = Files.newBufferedReader(originalReport, Charset.defaultCharset())) {
                // construct column name and datatypes
                String headerLine = originalReader.readLine();
                headers = Lists.newArrayList();
                numericDataType = Lists.newArrayList();
                for (String column : headerLine.split("\t")) {
                    String editedColumnName = StringUtils.lowerCase(column).replaceAll(" ", "_").replaceAll("-", "_");
                    headers.add(editedColumnName);
                    // note that Parent Sample SWID is a silly column that has colons in it
                    numericDataType.add(!editedColumnName.contains("parent_sample") && (editedColumnName.contains("swid")));
                }
                bulkImportFile = Files.createTempFile("import", "txt");
                try (BufferedWriter derbyImportWriter = Files.newBufferedWriter(bulkImportFile, Charset.defaultCharset())) {
                    Log.debug("Bulk import file written to " + bulkImportFile.toString());
                    while (originalReader.ready()) {
                        String line = originalReader.readLine();
                        StringBuilder builder = new StringBuilder();
                        int i = 0;
                        for (String colValue : line.split("\t")) {
                            if (i != 0) {
                                builder.append("\t");
                            }
                            if (numericDataType.get(i)) {
                                if (!colValue.trim().isEmpty()) {
                                    builder.append(colValue);
                                }
                            } else {
                                // assume that this is a string
                                // need to double quotes to preserve them, see
                                // https://db.apache.org/derby/docs/10.4/tools/ctoolsimportdefaultformat.html
                                builder.append("\"").append(colValue.replaceAll("\"", "\"\"")).append("\"");
                            }
                            i++;
                        }
                        derbyImportWriter.write(builder.toString());
                        derbyImportWriter.newLine();
                    }
                }
            }
            randomTempDirectory = Files.createTempDirectory("randomFileProvenanceQueryDir");

            // try using in-memory for better performance
            String protocol = "jdbc:h2:";
            if (options.has(useH2InMemorySpec)) {
                protocol = protocol + "mem:";
            }
            Connection connection = spinUpEmbeddedDB(randomTempDirectory, "org.h2.Driver", protocol);

            // drop table if it exists already (running in IDE?)
            Statement dropTableStatement = null;
            try {
                dropTableStatement = connection.createStatement();
                dropTableStatement.executeUpdate("DROP TABLE " + TABLE_NAME);
            } catch (SQLException e) {
                Log.debug("Report table didn't exist (normal)");
            } finally {
                DbUtils.closeQuietly(dropTableStatement);
            }

            // create table creation query
            StringBuilder tableCreateBuilder = new StringBuilder();
            // tableCreateBuilder
            tableCreateBuilder.append("CREATE TABLE " + TABLE_NAME + " (");
            for (int i = 0; i < headers.size(); i++) {
                if (i != 0) {
                    tableCreateBuilder.append(",");
                }
                if (numericDataType.get(i)) {
                    tableCreateBuilder.append(headers.get(i)).append(" INT ");
                } else {
                    tableCreateBuilder.append(headers.get(i)).append(" VARCHAR ");
                }
            }
            tableCreateBuilder.append(")");

            bulkImportH2(tableCreateBuilder, connection, bulkImportFile);

            // query the database and dump the results to
            try (BufferedWriter outputWriter = Files.newBufferedWriter(Paths.get(options.valueOf(outFileSpec)), Charset.defaultCharset(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                // query the database and dump the results to
                QueryRunner runner = new QueryRunner();
                List<Map<String, Object>> mapList = runner.query(connection, options.valueOf(querySpec), new MapListHandler());
                // output header
                if (mapList.isEmpty()) {
                    Log.fatal("Query had no results");
                    System.exit(-1);
                }
                StringBuilder builder = new StringBuilder();
                for (String columnName : mapList.get(0).keySet()) {
                    if (builder.length() != 0) {
                        builder.append("\t");
                    }
                    builder.append(StringUtils.lowerCase(columnName));
                }
                outputWriter.append(builder);
                outputWriter.newLine();
                for (Map<String, Object> rowMap : mapList) {
                    StringBuilder rowBuilder = new StringBuilder();
                    for (Entry<String, Object> e : rowMap.entrySet()) {
                        if (rowBuilder.length() != 0) {
                            rowBuilder.append("\t");
                        }
                        rowBuilder.append(e.getValue());
                    }
                    outputWriter.append(rowBuilder);
                    outputWriter.newLine();
                }
            }
            DbUtils.closeQuietly(connection);
            Log.stdoutWithTime("Wrote output to " + options.valueOf(outFileSpec));
            return new ReturnValue();
        } catch (IOException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (originalReport != null) {
                FileUtils.deleteQuietly(originalReport.toFile());
            }
            if (bulkImportFile != null) {
                FileUtils.deleteQuietly(bulkImportFile.toFile());
            }
            if (randomTempDirectory != null && randomTempDirectory.toFile().exists()) {
                FileUtils.deleteQuietly(randomTempDirectory.toFile());
            }

        }
    }

    private void bulkImportH2(StringBuilder tableCreateBuilder, Connection connection, Path importFile) throws SQLException {
        tableCreateBuilder.append("AS SELECT * FROM CSVREAD('").append(importFile.toString()).append("', null, 'fieldSeparator=\t')");
        Log.debug("Table creation query is: " + tableCreateBuilder.toString());
        Statement createTableStatement = null;
        try {
            createTableStatement = connection.createStatement();
            createTableStatement.executeUpdate(tableCreateBuilder.toString());
        } finally {
            DbUtils.closeQuietly(createTableStatement);
        }
    }

    private Connection spinUpEmbeddedDB(Path randomTempDirectory, String driver, String protocol) throws IllegalAccessException,
            SQLException, ClassNotFoundException, InstantiationException {
        Class.forName(driver).newInstance();
        Connection connection = DriverManager.getConnection(protocol + randomTempDirectory.toString() + "/tempDB;create=true");
        return connection;
    }

    private Path populateOriginalReportFromWS() throws IOException {
        Map<FileProvenanceParam, List<String>> map = ProvenanceUtility.convertOptionsToMap(options, metadata);
        // specify some standard filters that are required for filters
        map.put(FileProvenanceParam.skip, new ImmutableList.Builder<String>().add("false").build());
        map.put(FileProvenanceParam.workflow_run_status, new ImmutableList.Builder<String>().add(WorkflowRunStatus.completed.toString())
                .build());
        map.put(FileProvenanceParam.processing_status, new ImmutableList.Builder<String>().add(ProcessingStatus.success.toString()).build());
        Path originalReport = Files.createTempFile("file_provenance", "txt");
        Log.debug("Original report written to " + originalReport.toString());
        try (BufferedWriter originalWriter = Files.newBufferedWriter(originalReport, Charset.defaultCharset())) {
            metadata.fileProvenanceReport(map, originalWriter);
        }
        return originalReport;
    }

    @Override
    public ReturnValue clean_up() {
        return new ReturnValue();
    }
}
