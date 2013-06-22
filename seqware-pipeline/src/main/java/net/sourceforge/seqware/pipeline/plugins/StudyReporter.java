package net.sourceforge.seqware.pipeline.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import net.sourceforge.seqware.common.err.NotFoundException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;

import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = PluginInterface.class)
public class StudyReporter extends Plugin {

  public StudyReporter() {
    parser.accepts("all", "Generate a report of all studies. Or use '--title'.");
    parser.accepts("title", "The title of the study whose report will be generated. Or use '--all'.").withRequiredArg();
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
    } else if (options.has("title")) {
      filename = (new Date() + "__" + options.valueOf("title") + ".tsv").replace(" ", "_");
    } else if (options.has("all")) {
      filename = (new Date() + "__all.tsv").replace(" ", "_");
    } else {
      println("One of '--title <study-title>' or '--all' must be specified.");
      println(this.get_syntax());
      return new ReturnValue(ReturnValue.INVALIDPARAMETERS);
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
    if (options.has("title")) {
      String title = (String) options.valueOf("title");
      try {
        metadata.studyReport(title, out);
        return new ReturnValue();
      } catch (NotFoundException e) {
        println("No study found with title: " + title);
        return new ReturnValue();
      }
    } else {
      metadata.allStudiesReport(out);
      return new ReturnValue();
    }
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
