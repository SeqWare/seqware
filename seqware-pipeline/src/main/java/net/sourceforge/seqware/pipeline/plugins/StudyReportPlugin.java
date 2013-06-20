package net.sourceforge.seqware.pipeline.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;

import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = PluginInterface.class)
public class StudyReportPlugin extends Plugin {

  public StudyReportPlugin() {
    parser.accepts("all", "Generate a report of all studies. Or use '--title'.");
    parser.accepts("title",
                   "The title of the study whose report will be generated. Or use '--all'.").withRequiredArg();
    parser.accepts("out", "The file into which the report will be written.").withRequiredArg();
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
    if (options.has("title")) {
      String title = (String) options.valueOf("title");
      metadata.studyReport(title, writer(title));
      return new ReturnValue();
    } else if (options.has("all")) {
      metadata.allStudiesReport(writer("all"));
      return new ReturnValue();
    } else {
      println("Missing 'title' parameter");
      println(this.get_syntax());
      return new ReturnValue(ReturnValue.INVALIDPARAMETERS);
    }
  }

  private Writer writer(String label) {
    try {
      if (options.has("out")) {
        return new BufferedWriter(new FileWriter((String) options.valueOf("out")));
      } else {
        String filename = (new Date() + "__" + label+".tsv").replace(" ", "_");
        return new BufferedWriter(new FileWriter(filename));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ReturnValue clean_up() {
    return new ReturnValue();
  }

}
