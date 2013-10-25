---

title:                 "Check Database Plugin"
toc_includes_sections: true
markdown:              advanced 
is_dynamic:		true

---

The Check Database plugin validates that the content of the seqware meta db follows certain recommended conventions. You can also write additional checks in order to check that conventions for your particular install of SeqWare are being properly followed.

## Requirements

In order to run the plugin, you must have the following available to you:

* SeqWare script (1.0.7 or higher)
* SeqWare settings file set up to contact the SeqWare meta db (contact your local SeqWare admin to get the path)


##Usage

The base plugin is accessible via the CLI. 
	
	seqware checkdb --help

	Usage: seqware checkdb --help
       	       seqware checkdb

	Description:
  	  Using a direct database connection, check whether the meta db contains any content that deviates from recommended conventions.

## Included Checks

The following checks are built-in:

* attributes are stored with one value for each key (i.e. attributes can be considered a map of key-values)
* Orphan checks (note that a few of these can be expected, for example, if workflows have not run yet on new data)
    * Experiments contain samples
    * IUSes contain processing events 
    * Files are hooked up to processing events
    * Workflows have been run at least once
    * All samples and lanes are associated with experiments and sequencer runs respectively
    * All processings are attached to workflow runs
*  Samples contain one parent (i.e. the sample hierarchy is a tree)
* Workflow run checks
    * All 'Completed' workflow runs are eventually connected to at least one study
    * All 'Completed' workflow runs reachable by ius_workflow_runs are reachable via the processing hierarchy
    * Workflow runs that contain input files via workflow_run_input_files should also contain (a superset of them) via the processing hierarchy

In addition, we also list the following information for debug purposes

* The number of 'Completed' workflow runs connected to multiple studies (a possible error)
* Workflow runs with input files via the processing hierarchy but not via workflow_run_input_files (this is expected since input files via the direct table are a subset of those in the processing hierarchy) 


## Additional Checks

Certain checks are inherent to every install of SeqWare, these are currently provided with the base checkdb plugin as the SampleHierarchyPlugin, the OrphanCheckerPlugin, the AttributePlugin, and the WorkflowRunConventionsPlugin. You can use these as models for new checks that are specific to your particular institution by implementing the CheckDBPluginInterface.


## Tutorial

As a silly example, here is an example of a new plugin that reports every SWID of every workflow as an error.


	import java.sql.SQLException;
	import java.util.List;
	import java.util.Set;
	import java.util.SortedMap;
	import net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDB;
	import net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDBPluginInterface;
	import net.sourceforge.seqware.pipeline.plugins.checkdb.SelectQueryRunner;
	import org.apache.commons.dbutils.handlers.ColumnListHandler;
	import org.openide.util.lookup.ServiceProvider;

	/**
	 * Does a silly check 
	 * @author dyuen
	 */
	@ServiceProvider(service = CheckDBPluginInterface.class)
	public class TestPlugin implements CheckDBPluginInterface {

	    @Override
	    public void check(SelectQueryRunner qRunner, SortedMap<Level, Set<String>> result) throws SQLException {
		String query = "select sw_accession from workflow";
		List<Integer> executeQuery = qRunner.executeQuery(query, new ColumnListHandler<Integer>());
		CheckDB.processOutput(result, Level.SEVERE,  "Silly workflows that we want to report: " , executeQuery);
	    }
	    
	}


Note that the CheckDB.processOutput is merely a static convenience method that generates warning messages based on a list of integers. All you really are required to do is to run queries using qRunner and insert them into the result map by level. Each level maps to a set of arbitrary warnings. After creating a new plugin, you can use the following procedure to run it alongside the pre-created plugins. 

	~/seqware$ javac -cp seqware-distribution/target/seqware-distribution-1.0.7-SNAPSHOT-full.jar TestPlugin.java
	Note: TestPlugin to be registered as a net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDBPluginInterface
	~/seqware$ mkdir -p META-INF/services
	~/seqware$ mv net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDBPluginInterface META-INF/services/
	~/seqware$ jar cf test.jar  TestPlugin.class META-INF
	~/seqware$ java -cp seqware-distribution/target/seqware-distribution-1.0.7-SNAPSHOT-full.jar:test.jar net.sourceforge.seqware.pipeline.runner.PluginRunner -p net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDB
	Printed report to /tmp/report1568888261692505189.html
	~/seqware$ chromium-browser /tmp/report1568888261692505189.html

The above procedure compiles the plugin, packages it into a jar along with a ServiceProvider registration, runs the check utility along with your new plugin, and opens the resulting report. 
