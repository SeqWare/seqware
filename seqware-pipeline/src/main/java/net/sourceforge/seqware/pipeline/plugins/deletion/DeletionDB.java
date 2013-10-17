package net.sourceforge.seqware.pipeline.plugins.deletion;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import io.seqware.webservice.client.SeqWareWebserviceClient;
import io.seqware.webservice.model.File;
import io.seqware.webservice.model.Lane;
import io.seqware.webservice.model.Processing;
import io.seqware.webservice.model.ProcessingFiles;
import io.seqware.webservice.model.SequencerRun;
import io.seqware.webservice.model.WorkflowRun;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;

import org.openide.util.lookup.ServiceProvider;

/**
 * A workflow run deletion tool for your SeqWare metadb
 *
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public final class DeletionDB extends Plugin {

    /**
     * <p>Constructor for HelloWorld.</p>
     */
    public DeletionDB() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        parser.acceptsAll(Arrays.asList("target", "t", "?"), 
                "Give a sequencer run, lane, or workflow run SWID in order to determine which workflow runs (processings, files) should be deleted.")
                .withRequiredArg().ofType(Integer.class).isRequired();

    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public final ReturnValue init() {
        return new ReturnValue();
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
        return new ReturnValue();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public final ReturnValue do_run() {
        ReturnValue ret = new ReturnValue();
        int target = 6669; //TODO - pass this in as a parameter
        ModelAccessionIDTuple tuple = translateToID(target);
        
        //TODO - hook this up to .seqware/settings
        String url = "http://localhost:38080/seqware-admin-webservice/webresources";
        SeqWareWebserviceClient client = new SeqWareWebserviceClient(tuple.getAdminModelClass().getSimpleName().toLowerCase(), url);
        ClientResponse response = client.find_XML(ClientResponse.class, String.valueOf(tuple.getId()));
        if (tuple.getAdminModelClass() == WorkflowRun.class) {
            WorkflowRun data = response.getEntity(WorkflowRun.class);
            reportDeletionOfWorkflowRun(data);
        } else if (tuple.getAdminModelClass() == Lane.class){
            Lane data = response.getEntity(Lane.class);
        } else if (tuple.getAdminModelClass() == SequencerRun.class){
            SequencerRun data = response.getEntity(SequencerRun.class);
        } 
        client.close();
        return ret;
    }


    /**
     * <p>get_description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public final String get_description() {
        return ("A database deletion tool for your SeqWare metadb.");
    }   

    public static void main(String[] args) throws IOException, URISyntaxException {
        DeletionDB mp = new DeletionDB();
        mp.setMetadata(MetadataFactory.getWS(ConfigTools.getSettings()));
        mp.init();
        List<String> arr = new ArrayList<String>();
        mp.setParams(arr);
        mp.parse_parameters();
        ReturnValue do_run = mp.do_run();
    }

    @Override
    public final ReturnValue clean_up() {
        return new ReturnValue();
    }

    private ModelAccessionIDTuple translateToID(int targetAccession) {
        ModelAccessionIDTuple tuple = new ModelAccessionIDTuple();
        tuple.setAccession(targetAccession);
        if (metadata.getWorkflowRun(targetAccession) != null){
            tuple.setId(metadata.getWorkflowRun(targetAccession).getWorkflowRunId());
            tuple.setAdminModelClass(WorkflowRun.class);
        } else if (metadata.getSequencerRun(targetAccession) != null){
            tuple.setId(metadata.getSequencerRun(targetAccession).getSequencerRunId());
            tuple.setAdminModelClass(SequencerRun.class);
        } else if(metadata.getLane(targetAccession) != null){
            tuple.setId(metadata.getLane(targetAccession).getLaneId());
            tuple.setAdminModelClass(Lane.class);
        } else{
            throw new RuntimeException("Could not locate target, please double-check your SWID");
        }
        return tuple;
    }

    private void reportDeletionOfWorkflowRun(WorkflowRun data) {
        Set<Processing> affectedProcessing = new HashSet<Processing>();
        // workflow_run
        if (data.getProcessingCollection() != null){
            affectedProcessing.addAll(data.getProcessingCollection());
        }
        // ancestor_workflow_run
        if (data.getProcessingCollection1() != null){
            affectedProcessing.addAll(data.getProcessingCollection1());
        }
        Set<File> affectedFile = new HashSet<File>();
        for(Processing p : affectedProcessing){
            Collection<ProcessingFiles> processingFilesCollection = p.getProcessingFilesCollection();
            if (processingFilesCollection == null) { continue;}
            for(ProcessingFiles pf : processingFilesCollection){
                affectedFile.add(pf.getFileId());
            }
        }
        // list all affected resources
        for(Processing p : affectedProcessing){
            System.out.println("Processing SWID: " + p.getSwAccession());
        }
        for(File f : affectedFile){
            System.out.println("File SWID: " + f.getSwAccession());
        }
    }
    
    private class ModelAccessionIDTuple{
        private int accession;
        private int id;
        private Class adminModelClass;

        /**
         * @return the accession
         */
        public int getAccession() {
            return accession;
        }

        /**
         * @param accession the accession to set
         */
        public void setAccession(int accession) {
            this.accession = accession;
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * @return the adminModelClass
         */
        public Class getAdminModelClass() {
            return adminModelClass;
        }

        /**
         * @param adminModelClass the adminModelClass to set
         */
        public void setAdminModelClass(Class adminModelClass) {
            this.adminModelClass = adminModelClass;
        }
                
        
    }
}
