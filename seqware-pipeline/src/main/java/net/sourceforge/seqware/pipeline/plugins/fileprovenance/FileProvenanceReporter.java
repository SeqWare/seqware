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
package net.sourceforge.seqware.pipeline.plugins.fileprovenance;

import com.google.common.collect.ImmutableList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import joptsimple.OptionSpec;
import net.sourceforge.seqware.common.model.FileProvenanceParam;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.lookup.ServiceProvider;

/**
 * Lightweight wrapper for file provenance report
 *
 * @author dyuen
 */
@ServiceProvider(service = PluginInterface.class)
public class FileProvenanceReporter extends Plugin {

    private Map<String, OptionSpec> configureFileProvenanceParams;

    public FileProvenanceReporter() {
        configureFileProvenanceParams = ProvenanceUtility.configureFileProvenanceParams(parser);
        parser.accepts("out", "The file into which the report will be written.").withRequiredArg();
    }

    @Override
    public String get_description() {
        return "Generates a tab-delimited report of all output files "
                + "(and their relationships and metadata) from a specified study or from all studies.";
    }
    private File outfile = null;
    private Writer out = null;

    @Override
    public ReturnValue init() {
        String filename;
        if (options.has("out")) {
            filename = (String) options.valueOf("out");
        } else if (options.has("all")) {
            filename = (new Date() + "__all.tsv").replace(" ", "_");
        } else if (!ProvenanceUtility.checkForValidOptions(options)) {
            println("One of the various contraints or '--all' must be specified.");
            println(this.get_syntax());
            return new ReturnValue(ReturnValue.INVALIDPARAMETERS);
        } else {
            filename = (new Date() + ".tsv").replace(" ", "_");
        }

        outfile = new File(filename);
        try {
            out = new BufferedWriter(new FileWriter(outfile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ReturnValue();
    }

    @Override
    public ReturnValue do_test() {
        return new ReturnValue();
    }

    @Override
    public ReturnValue do_run() {
        Map<FileProvenanceParam, List<String>> map = ProvenanceUtility.convertOptionsToMap(options, metadata);
        map.put(FileProvenanceParam.skip, new ImmutableList.Builder<String>().add("false").build());
        metadata.fileProvenanceReport(map, out);
        return new ReturnValue();
    }

    @Override
    public ReturnValue clean_up() {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }

        if (outfile != null && outfile.exists() && outfile.length() == 0) {
            // created via opening the FileWriter
            outfile.delete();
        }

        return new ReturnValue();
    }
}
