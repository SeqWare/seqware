/**
 *
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionException;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.util.Log;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * <p>This plugin does the initial population of workflow run files in order to
 * track input files</p>
 *
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowRunFilesInitialPopulationPlugin extends Plugin {

    private ReturnValue ret = new ReturnValue();

    /**
     * <p>Constructor for HelloWorld.</p>
     */
    public WorkflowRunFilesInitialPopulationPlugin() {
        super();
        parser.acceptsAll(Arrays.asList("skip", "s"), "Optional: comma separated list of module/plugin names to skip").requiresArgument();
        parser.acceptsAll(Arrays.asList("modules", "m"), "Optional: if provided will list out modules instead of plugins.");
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setConfig(java.util.Map)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfig(Map<String, String> config) {
        /**
         * explicitly no nothing
         */
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setParams(java.util.List)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void setParams(List<String> params) {
        this.params = params.toArray(new String[0]);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setMetadata(net.sourceforge.seqware.pipeline.metadata.Metadata)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetadata(Metadata metadata) {
        //println("Setting Metadata: " + metadata);
        this.metadata = metadata;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#get_syntax()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public String get_syntax() {

        try {
            parser.printHelpOn(System.err);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.fatal(e);
        }
        return ("");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#parse_parameters()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue parse_parameters() {

        try {
            options = parser.parse(params);
        } catch (OptionException e) {
            get_syntax();
            ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue init() {
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue do_test() {
        // TODO Auto-generated method stub
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue do_run() {
        ResultSet rs = null;
        MetadataDB mdb = null;
        try {
            String query = new StringBuilder()
            .append("select w.sw_accession, w.workflow_run_id, w.status, f.* FROM workflow_run w LEFT OUTER JOIN workflow_run_input_files f ")
            .append("ON w.workflow_run_id = f.workflow_run_id WHERE (status = 'completed' OR status= 'failed') AND ")
            .append("f.workflow_run_id IS NULL ORDER BY w.sw_accession;").toString();

            Log.info("Executing query: " + query);
            mdb = DBAccess.get();
            
            List<int[]> ids = mdb.executeQuery(query, new ResultSetHandler<List<int[]>>(){
              @Override
              public List<int[]> handle(ResultSet rs) throws SQLException {
                List<int[]> ids = new ArrayList<int[]>();
                while(rs.next()){
                  ids.add(new int[]{rs.getInt("sw_accession"), rs.getInt("workflow_run_id")});
                }
                return ids;
              }
            });
            
            mdb.getDb().setAutoCommit(false);
            PreparedStatement prepareStatement = mdb.getDb().prepareStatement("INSERT INTO workflow_run_input_files (workflow_run_id, file_id) VALUES(?,?)");
            for (int[] i : ids){
              int workflowSWID = i[0];
              int workflowRunID = i[1];
              Log.stdout("Working on workflow_run " + workflowSWID);

              // populate input files
              List<Integer> listOfFiles = this.getListOfFiles(workflowSWID);
              Log.stdout("Found " + listOfFiles.size() + " input files for workflow_run " + workflowSWID);
              // insert into new workflow_run_input_files table
              for (Integer fSWID : listOfFiles) {
                  Integer file_id = this.metadata.getFile(fSWID).getFileId();
                  prepareStatement.setInt(1, workflowRunID);
                  prepareStatement.setInt(2, file_id);
                  prepareStatement.executeUpdate();
              }
            }
            Log.stdout("Success!");
            mdb.getDb().commit();
            return ret;
        } catch (SQLException ex) {
            Log.fatal("Population failed, aborting.");
            Logger.getLogger(WorkflowRunFilesInitialPopulationPlugin.class.getName()).log(Level.SEVERE, null, ex);
            ret.setExitStatus(ReturnValue.FAILURE);
        } finally {
            if (mdb != null) {
                DbUtils.closeQuietly(mdb.getDb(), mdb.getSql(), rs);
            }
            DBAccess.close();
        }
        ret.setExitStatus(ReturnValue.FAILURE);
        return ret;
    }

    /**
     * Given a workflowRunAcc returns a list of file paths that were used in
     * that run using effectively, the workflow run reporter.
     *
     * Hopefully, this is the last we'll use this approach!
     *
     * @param workflowRunAcc
     * @param filesToRun
     * @return
     */
    private List<Integer> getListOfFiles(int workflowRunAcc) {
        Map<String, String> map = generateWorkflowRunMap(workflowRunAcc);
        List<Integer> indices = new ArrayList<Integer>();
        String ranOnString = map.get("Immediate Input File SWIDs");
        String[] ranOnArr = ranOnString.split(",");
        List<Integer> ranOnList = new ArrayList<Integer>();
        if (ranOnString.isEmpty()) {
            return ranOnList;
        }
        for (String i : ranOnArr) {
            ranOnList.add(Integer.valueOf(i.trim()));
            Log.trace("Adding item: " + i);
        }
        Log.debug("Got list of files: " + StringUtils.join(ranOnList, ','));
        return ranOnList;
    }

    /**
     * Report an actual launch of a workflow for testing purpose
     *
     * @return false iff we don't actually want to launch
     */
    protected boolean reportLaunch() {
        return true;
    }

    private Map<String, String> generateWorkflowRunMap(int workflowRunAcc) {
        String report = metadata.getWorkflowRunReport(workflowRunAcc);
        String[] lines = report.split("\n");
        String[] reportHeader = lines[0].split("\t");
        String[] data = lines[1].split("\t");
        Map<String, String> map = new TreeMap<String, String>();
        for (int i = 0; i < reportHeader.length; i++) {
            map.put(reportHeader[i].trim(), data[i].trim());
        }
        return map;
    }


    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#clean_up()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue clean_up() {
        // TODO Auto-generated method stub
        return ret;
    }

    /**
     * <p>get_description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String get_description() {
        return ("This plugin does the initial population of workflow run files in order to track input files.");
    }

    public static void main(String[] args) {
        WorkflowRunFilesInitialPopulationPlugin mp = new WorkflowRunFilesInitialPopulationPlugin();
        mp.init();
        List<String> arr = new ArrayList<String>();
        mp.setParams(arr);
        mp.parse_parameters();
        mp.do_run();
    }
}
