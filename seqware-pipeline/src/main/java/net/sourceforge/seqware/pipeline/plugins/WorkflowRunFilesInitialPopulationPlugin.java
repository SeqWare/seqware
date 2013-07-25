/**
 *
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import joptsimple.OptionException;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.dbutils.DbUtils;

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
            WorkflowRunService ss = BeanFactory.getWorkflowRunServiceBean();
            StringBuilder query = new StringBuilder();
            query.append("select w.sw_accession, status, f.* FROM workflow_run w LEFT OUTER JOIN workflow_run_files f ");
            query.append("ON w.workflow_run_id = f.workflow_run_id WHERE (status = 'completed' OR status= 'failed') AND ");
            query.append("f.workflow_run_id IS NULL ORDER BY w.sw_accession;");

            Log.info("Executing query: " + query);
            mdb = DBAccess.get();
            mdb.getDb().setAutoCommit(false);
            rs = mdb.executeQuery(query.toString());
            while (rs.next()) {
                int workflowSWID = rs.getInt("sw_accession");
                WorkflowRun workflowRun = (WorkflowRun) ss.findBySWAccession(workflowSWID);
                if (workflowRun.getInputFiles().isEmpty()) {
                    // populate input files
                }
            }
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
