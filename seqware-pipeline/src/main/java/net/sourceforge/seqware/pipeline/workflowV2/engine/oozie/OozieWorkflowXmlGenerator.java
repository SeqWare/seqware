package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object.WorkflowApp;

import org.apache.hadoop.fs.Path;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class OozieWorkflowXmlGenerator {
  /**
   * generate a dax file from the object model
   * 
   * @param wfdm
   * @param output
   * @return
   */
  public ReturnValue generateWorkflowXml(AbstractWorkflowDataModel wfdm, String output, String nfsWorkDir, Path hdfsWorkDir, boolean useSge,
                                         File seqwareJar, String threadsSgeParamFormat,
                                         String maxMemorySgeParamFormat) {
    ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
    File dax = new File(output);
    // write to dax
    Document doc = new Document();
    try {
      OutputStream out = new FileOutputStream(dax);
      XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
      WorkflowApp adag = new WorkflowApp(wfdm, nfsWorkDir, hdfsWorkDir, useSge, seqwareJar, threadsSgeParamFormat,
                                         maxMemorySgeParamFormat);
      doc.setRootElement(adag.serializeXML());
      serializer.output(doc, out);

      out.flush();
      out.close();
    } catch (IOException e) {
      Log.error(e);
      ret.setExitStatus(ReturnValue.FAILURE);
    }
    return ret;
  }
}