package net.sourceforge.seqware.pipeline.plugins;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.module.ReturnValue.ExitStatus;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.Rethrow;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = PluginInterface.class)
public class ProcessingDataStructure2Dot extends Plugin {
    private final ArgumentAcceptingOptionSpec<String> processingSWIDSpec;
    private final ArgumentAcceptingOptionSpec<String> outputFileSpec;

    public ProcessingDataStructure2Dot() {
        super();
        this.processingSWIDSpec = parser.accepts("parent-accession", "The SWID of the processing").withRequiredArg().ofType(String.class)
                .required().describedAs("The SWID of the processing.");
        this.outputFileSpec = parser.accepts("output-file", "Optional: file name").withRequiredArg().ofType(String.class)
                .describedAs("Output File Name").defaultsTo("output.dot");

    }

    @Override
    public ReturnValue init() {
        return new ReturnValue();
    }

    @Override
    public ReturnValue do_test() {
        return new ReturnValue();
    }

    @Override
    public ReturnValue do_run() {
        String swAccession = options.valueOf(processingSWIDSpec);
        String outputFile = options.valueOf(outputFileSpec);
        String output = metadata.getProcessingRelations(swAccession);
        if (output == null) {
            Log.stderr("Could not find processing event, please check that this is a valid processing SWID");
            return new ReturnValue(ExitStatus.INVALIDPARAMETERS);
        }
        Log.stdout("Writing dot file to " + outputFile);
        try (Writer writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(output);
        } catch (IOException e) {
            throw Rethrow.rethrow(e);
        }

        return new ReturnValue();
    }

    @Override
    public String get_description() {
        return "This plugin will take in a sw_accession of a processing, and translate the hierarchy of the processing relationship into dot format";
    }

    @Override
    public ReturnValue clean_up() {
        return new ReturnValue();
    }

    private class DotNode {
        private final List<DotNode> children;
        private final String processingId;

        public DotNode(String pid) {
            this.children = new ArrayList<>();
            this.processingId = pid;
        }

        public void addChild(DotNode node) {
            if (!this.children.contains(node)) this.children.add(node);
        }

        @Override
        public String toString() {
            return this.processingId;
        }

        public List<DotNode> getChildren() {
            return this.children;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DotNode == false) return false;
            if (obj == this) return true;
            DotNode rhs = (DotNode) obj;
            return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.processingId, rhs.processingId).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(this.processingId).toHashCode();
        }
    }

}
