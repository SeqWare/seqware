package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus;

import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Pfn;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.WorkflowExecutable;

/**
 * a util class to generate Java, Perl, and DirManager for dax
 * @author yliang
 *
 */
public class WorkflowExecutableUtils {

    public static WorkflowExecutable getDefaultJavaExcutable(AbstractWorkflowDataModel datamodel) {
		WorkflowExecutable ex_java = new WorkflowExecutable();
		ex_java.setNamespace("seqware");
		ex_java.setName("java");
		ex_java.setInstalled(true);
		ex_java.setArch("x86_64");
		ex_java.setOs("linux");
		ex_java.setVersion("1.6.0");
	
		String BASE_DIR = datamodel.getWorkflowBaseDir();
		Pfn pfn_java = new Pfn();
		String siteObj = datamodel.getEnv().getSwCluster();
		pfn_java.setSite(siteObj == null ? "local" : siteObj);
		String url = "file:///" + BASE_DIR + "/bin/jre1.6.0_29/bin/java";
		pfn_java.setUrl(url);
		ex_java.setPfn(pfn_java);
	
		return ex_java;
    }

    

    public static WorkflowExecutable getDefaultPerlExcutable(AbstractWorkflowDataModel datamodel) {
		WorkflowExecutable ex_perl = new WorkflowExecutable();
		ex_perl.setNamespace("seqware");
		ex_perl.setName("perl");
		ex_perl.setInstalled(true);
		ex_perl.setArch("x86_64");
		ex_perl.setOs("linux");
		ex_perl.setVersion("5.14.1");
	
		String BASE_DIR = datamodel.getWorkflowBaseDir();
		Pfn pfn_perl = new Pfn();
		String siteObj = datamodel.getEnv().getSwCluster();
		pfn_perl.setSite(siteObj == null ? "local" : siteObj);
		String url = "file:///" + BASE_DIR + "/bin/perl-5.14.1/perl";
		pfn_perl.setUrl(url);
		ex_perl.setPfn(pfn_perl);
		return ex_perl;
    }

    public static WorkflowExecutable getDefaultDirManagerExcutable(AbstractWorkflowDataModel datamodel) {
		WorkflowExecutable ex_dirmanager = new WorkflowExecutable();
		ex_dirmanager.setNamespace("pegasus");
		ex_dirmanager.setName("dirmanager");
		ex_dirmanager.setInstalled(true);
		ex_dirmanager.setArch("x86_64");
		ex_dirmanager.setOs("linux");
		ex_dirmanager.setVersion("1");
	
		String BASE_DIR = datamodel.getWorkflowBaseDir();
		Pfn pfn_dirmanager = new Pfn();
		String siteObj = datamodel.getEnv().getSwCluster();
		pfn_dirmanager.setSite(siteObj == null ? "local" :  siteObj);
		String url = "file:///" + BASE_DIR + "/bin/globus/pegasus-dirmanager";
		pfn_dirmanager.setUrl(url);
		ex_dirmanager.setPfn(pfn_dirmanager);
		return ex_dirmanager;
    }

}
