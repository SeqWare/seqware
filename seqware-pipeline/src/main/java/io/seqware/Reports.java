package io.seqware;


import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

public class Reports {

  public static void triggerProvenanceReport() {
    Metadata md = MetadataFactory.get(ConfigTools.getSettings());
    md.fileProvenanceReportTrigger();
  }
}
