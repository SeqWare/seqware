package ${package};

import java.util.*;

/**
 * Launches a workflow with an input BAM file.
 * 
 * For more information on writing custom deciders, please see the documentation at 
 * <a href="http://seqware.github.io/docs/6-pipeline/custom_deciders/">SeqWare Pipeline Custom Deciders</a>.<p/>
 *
 * Quick reference for the order of execution of the methods:<p/>
 * 1. Constructor : no command-line arguments are available yet<p/>
 * 2. init() : check and set command-line arguments as variables<p/>
 * 3. BasicDecider.separateFiles(List, String) : separates total list of files into groups to be processed together<p/>
 * 4. handleGroupByAttribute(String) : modify the attribute used to group files together<p/>
 * 5. checkFileDetails(ReturnValue, FileMetadata) : iterate over all files in a group to check whether they should be included<p/>
 * 6. doFinalCheck(String, String) : check all of the files that will be included in a run and make a final decision whether to launch<p/>
 * 7. modifyIniFile(String, String) : configure the INI file used for launching a workflow for a particular set of files<p/>
 * 8. Launch or schedule workflow. Repeat steps 5-7 until all files are processsed or maximum runs are launched.<p/>
 *
 * @author mtaschuk@oicr.on.ca
 */
public class ${workflow-name}Decider extends BasicDecider {
    //If you need access to the information stored in the ReturnValue for each file, uncomment
    //the next line and the line in {@link #checkFileDetails(ReturnValue, FileMetadata)}.
    //private Map<String, ReturnValue> pathToAttributes = new HashMap<String, ReturnValue>();

    public ${workflow-name}Decider() {
        super();
	parser.accepts("verbose", "Optional: prints debug and logging information during execution");
    }

    @Override
    public ReturnValue init() {
        Log.debug("INIT");
	//By default, the decider runs on a per-file basis (one file -> one workflow run)
        //this.setHeader(Header.IUS_SWA);

	//Launches only on BAM files
        this.setMetaType(Arrays.asList("application/bam"));

	//By calling super.init() last, anything defined on the command line at run-time 
	//overrides the defaults set here.
        ReturnValue val = super.init();
        return val;

    }

    @Override
    protected String handleGroupByAttribute(String attribute) {
	Log.debug("GROUP BY ATTRIBUTE: "+getHeader().getTitle()+ " " + attribute);
        return attribute;
    }

    @Override
    protected boolean checkFileDetails(ReturnValue returnValue, FileMetadata fm) {
	Log.debug("CHECK FILE DETAILS:" + fm);
	boolean isValid = super.checkFileDetails(returnValue, fm);
	if (isValid) { 
	    //If you need access to the information stored in the ReturnValue for each file, uncomment
	    //the next line and the line that instantiates the pathToAttributes map before the constructor
	    //pathToAttributes.put(fm.getFilePath(), returnValue);
	}
        return isValid;
    }

    @Override
    protected Map<String, String> modifyIniFile(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
	Log.debug("INI FILE:" + commaSeparatedFilePaths);
	Map<String, String> iniFileMap = super.modifyIniFile(commaSeparatedFilePaths, commaSeparatedParentAccessions);

	iniFileMap.put("input_file", commaSeparatedFilePaths);

        return iniFileMap;
    }

    /**
     * Allows a shortened command line. :
     * <code>java -jar Decider_${project.version}_${project.workflow-name}_${workflow-version}_SeqWare_${seqware-version}-jar-with-dependencies.jar --wf-accession 000000 --study Test --test</code> 
     */
    public static void main(String args[]){
  
        List<String> params = new ArrayList<String>();
	if (ArrayUtils.contains(args,"--verbose")){
	    params.add("--verbose");
	}
        params.add("--plugin");
        params.add(${workflow-name}Decider.class.getCanonicalName());
        params.add("--");
        params.addAll(Arrays.asList(args));
        System.out.println("Parameters: " + Arrays.deepToString(params.toArray()));
        net.sourceforge.seqware.pipeline.runner.PluginRunner.main(params.toArray(new String[params.size()]));
          
    }

}
