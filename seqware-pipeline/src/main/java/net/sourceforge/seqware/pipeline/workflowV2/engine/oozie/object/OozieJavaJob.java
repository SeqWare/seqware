package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Element;

public class OozieJavaJob extends OozieJob {

  public OozieJavaJob(AbstractJob job, String name, String oozie_working_dir) {
    super(job, name, oozie_working_dir, false, null);
  }

  /** 
   * FIXME: this is untested, not sure if it will work! 
   */
  protected Element getJavaElement() {
    Element javaE = new Element("java", WorkflowApp.NAMESPACE);
    Element jobTracker = new Element("job-tracker", WorkflowApp.NAMESPACE);
    jobTracker.setText("${jobTracker}");
    javaE.addContent(jobTracker);

    Element nameNode = new Element("name-node", WorkflowApp.NAMESPACE);
    nameNode.setText("${nameNode}");
    javaE.addContent(nameNode);

    Element config = new Element("configuration", WorkflowApp.NAMESPACE);
    Element p0 = new Element("property", WorkflowApp.NAMESPACE);
    config.addContent(p0);
    Element name0 = new Element("name", WorkflowApp.NAMESPACE);
    name0.setText("mapred.job.queue.name");
    p0.addContent(name0);
    Element value0 = new Element("value", WorkflowApp.NAMESPACE);
    value0.setText("${queueName}");
    p0.addContent(value0);
    
    // map memory
    p0 = new Element("property", WorkflowApp.NAMESPACE);
    config.addContent(p0);
    name0 = new Element("name", WorkflowApp.NAMESPACE);
    name0.setText("oozie.launcher.mapred.job.map.memory.mb");
    p0.addContent(name0);
    value0 = new Element("value", WorkflowApp.NAMESPACE);
    value0.setText(this.getJobObject().getMaxMemory());
    p0.addContent(value0);
    
    p0 = new Element("property", WorkflowApp.NAMESPACE);
    config.addContent(p0);
    name0 = new Element("name", WorkflowApp.NAMESPACE);
    name0.setText("oozie.launcher.mapred.job.reduce.memory.mb");
    p0.addContent(name0);
    value0 = new Element("value", WorkflowApp.NAMESPACE);
    value0.setText(this.getJobObject().getMaxMemory());
    p0.addContent(value0);
    
    p0 = new Element("property", WorkflowApp.NAMESPACE);
    config.addContent(p0);
    name0 = new Element("name", WorkflowApp.NAMESPACE);
    name0.setText("oozie.launcher.mapreduce.map.memory.physical.mb");
    p0.addContent(name0);
    value0 = new Element("value", WorkflowApp.NAMESPACE);
    value0.setText(this.getJobObject().getMaxMemory());
    p0.addContent(value0);
    
    p0 = new Element("property", WorkflowApp.NAMESPACE);
    config.addContent(p0);
    name0 = new Element("name", WorkflowApp.NAMESPACE);
    name0.setText("oozie.launcher.mapreduce.reduce.memory.physical.mb");
    p0.addContent(name0);
    value0 = new Element("value", WorkflowApp.NAMESPACE);
    value0.setText(this.getJobObject().getMaxMemory());
    p0.addContent(value0);
    
    // add configuration
    javaE.addContent(config);

    Element mainClass = new Element("main-class", WorkflowApp.NAMESPACE);
    mainClass.setText(this.jobObj.getMainClass());
    javaE.addContent(mainClass);

    this.buildMetadataString(javaE);

    for (String arg : this.jobObj.getCommand().getArguments()) {
      Element cmdArg = new Element("arg", WorkflowApp.NAMESPACE);
      cmdArg.setText(arg);
      javaE.addContent(cmdArg);
    }

    return javaE;
  }
}
