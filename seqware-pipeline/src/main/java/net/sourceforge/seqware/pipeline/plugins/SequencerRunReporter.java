/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.util.Log;
import org.openide.util.lookup.ServiceProvider;

/**
 * <p>SequencerRunReporter class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class SequencerRunReporter extends Plugin {

    ReturnValue ret = new ReturnValue();
    private BufferedWriter writer;

    /**
     * <p>Constructor for SequencerRunReporter.</p>
     */
    public SequencerRunReporter() {
        super();
        parser.acceptsAll(Arrays.asList("output-filename", "output"), "Name of the output tab-delimited file.").withRequiredArg();
        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue init() {

        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_test() {
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_run() {
        String report = this.metadata.getSequencerRunReport();
        if (this.options.has("output-filename")) {
            try {
                File output = new File((String) options.valueOf("output-filename"));
                writer = new BufferedWriter(new FileWriter(output));
                if (report == null) {
                    writer.write("Result was null, either the resource has no entries or it does not support this report");
                } else {
                    writer.write(report);
                }
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            println("Combination of parameters not recognized!");
            println(this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue clean_up() {
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public String get_description() {
        return "Prints a tab-delimited file describing the sequencer run, lane, "
                + "sample, and algorithms run on every IUS in the database. For more "
                + "information, see see http://seqware.github.com/docs/20-sequencer-run-reporter/";
    }
    
    
    
}
