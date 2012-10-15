package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus;

import java.util.Map;

import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Pfn;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.WorkflowExecutable;

public class WorkflowExecutableUtils {
    private static String URL_JAVA = "file:///${basedir}/bin/jre1.6.0_29/bin/java";
    private static String URL_PERL = "file:///${basedir}/bin/perl-5.14.1/perl";
    private static String URL_DIRMANAGER = "file:///${basedir}/bin/globus/pegasus-dirmanager";

    public static WorkflowExecutable getDefaultJavaExcutable(AbstractWorkflowDataModel datamodel) {
	WorkflowExecutable ex_java = new WorkflowExecutable();
	ex_java.setNamespace("seqware");
	ex_java.setName("java");
	ex_java.setInstalled(true);
	ex_java.setArch("x86_64");
	ex_java.setOs("linux");
	ex_java.setVersion("1.6.0");

	Pfn pfn_java = new Pfn();
	String siteObj = datamodel.getEnv().getSwCluster();
	pfn_java.setSite(siteObj == null ? "local" : siteObj);
	String url = StringUtils.replace(URL_JAVA, datamodel.getConfigs());
	pfn_java.setUrl(url);
	ex_java.setPfn(pfn_java);

	return ex_java;
    }

    public static WorkflowExecutable getLocalJavaExcutable(AbstractWorkflowDataModel datamodel) {
	WorkflowExecutable ex_java = new WorkflowExecutable();
	ex_java.setNamespace("seqware");
	ex_java.setName("java_local");
	ex_java.setInstalled(true);
	ex_java.setArch("x86_64");
	ex_java.setOs("linux");
	ex_java.setVersion("1.6.0");

	Pfn pfn_java = new Pfn();

	pfn_java.setSite("local");
	String url = StringUtils.replace(URL_JAVA, datamodel.getConfigs());
	pfn_java.setUrl(url);
	ex_java.setPfn(pfn_java);

	return ex_java;
    }
    
    public static WorkflowExecutable getBashExcutable(AbstractWorkflowDataModel datamodel) {
    	WorkflowExecutable ex_java = new WorkflowExecutable();
    	ex_java.setNamespace("seqware");
    	ex_java.setName("bash");
    	ex_java.setInstalled(true);
    	ex_java.setArch("x86_64");
    	ex_java.setOs("linux");
    	ex_java.setVersion("1.6.0");

    	Pfn pfn_java = new Pfn();

    	String siteObj = datamodel.getEnv().getSwCluster();
    	pfn_java.setSite(siteObj == null ? "local" :  siteObj);
    	String url = StringUtils.replace(URL_JAVA, datamodel.getConfigs());
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

	Pfn pfn_perl = new Pfn();
	String siteObj = datamodel.getEnv().getSwCluster();
	pfn_perl.setSite(siteObj == null ? "local" : siteObj);
	String url = StringUtils.replace(URL_PERL, datamodel.getConfigs());
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

	Pfn pfn_dirmanager = new Pfn();
	String siteObj = datamodel.getEnv().getSwCluster();
	pfn_dirmanager.setSite(siteObj == null ? "local" :  siteObj);
	String url = StringUtils.replace(URL_DIRMANAGER, datamodel.getConfigs());
	pfn_dirmanager.setUrl(url);
	ex_dirmanager.setPfn(pfn_dirmanager);
	return ex_dirmanager;
    }

    public static WorkflowExecutable getDefaultSeqwareExecutable(AbstractWorkflowDataModel datamodel) {
	WorkflowExecutable ex_seqware = new WorkflowExecutable();
	ex_seqware.setNamespace("seqware");
	ex_seqware.setName("seqware");
	ex_seqware.setInstalled(true);
	ex_seqware.setArch("x86_64");
	ex_seqware.setOs("linux");
	ex_seqware.setVersion("0.12.5");

	Pfn pfn_java = new Pfn();
	String siteObj = datamodel.getEnv().getSwCluster();
	pfn_java.setSite(siteObj == null ? "local" : siteObj);
	String url = StringUtils.replace(URL_JAVA, datamodel.getConfigs());
	pfn_java.setUrl(url);
	ex_seqware.setPfn(pfn_java);

	return ex_seqware;
    }
}
