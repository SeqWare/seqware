package net.sourceforge.seqware.pipeline.plugins.deletion;

import com.sun.jersey.api.client.UniformInterfaceException;
import io.seqware.webservice.client.SeqWareWebServiceClient;
import io.seqware.webservice.controller.ModelAccessionIDTuple;
import io.seqware.webservice.generated.model.Lane;
import io.seqware.webservice.generated.model.SequencerRun;
import io.seqware.webservice.generated.model.WorkflowRun;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import joptsimple.OptionSpec;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import org.openide.util.lookup.ServiceProvider;

/**
 * A workflow run deletion tool for your SeqWare metadb
 *
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public final class DeletionDB extends Plugin {

    private OptionSpec<Integer> workflowRunSpec;
    private OptionSpec<String> keyFileSpec;
    private Integer workflow_run_target = null;
    private File keyFile = null;

    /**
     * <p>Constructor for HelloWorld.</p>
     */
    public DeletionDB() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        workflowRunSpec = parser.acceptsAll(Arrays.asList("workflowrun", "r"),
                "Give a sequencer run, lane, or workflow run SWID in order to determine which workflow runs (processings,files) should be deleted.")
                .withRequiredArg().ofType(Integer.class).required();
        keyFileSpec = parser.acceptsAll(Arrays.asList("key", "k"),
                "An existing key file will be used to guide an actual deletion process")
                .withRequiredArg().ofType(String.class);
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
        workflow_run_target = options.valueOf(workflowRunSpec);
        if (options.has(keyFileSpec)) {
            keyFile = new File(options.valueOf(keyFileSpec));
        } 

        try {
            ReturnValue ret = new ReturnValue();
            ModelAccessionIDTuple tuple = translateToID(workflow_run_target);

            //TODO - hook this up to .seqware/settings
            String url = "http://localhost:38080/seqware-admin-webservice/webresources";
            SeqWareWebServiceClient client = new SeqWareWebServiceClient("workflowrun", url);
            if (keyFile == null) {
                Set<ModelAccessionIDTuple> find_JSON_rdelete = client.find_JSON_rdelete(Class.forName(tuple.getAdminModelClass()), String.valueOf(tuple.getId()));
                // add to a sorted set for easy viewing
                SortedSet<ModelAccessionIDTuple> sortedSet = new TreeSet<ModelAccessionIDTuple>();
                sortedSet.addAll(find_JSON_rdelete);
                ObjectMapper mapper = new ObjectMapper();
                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                keyFile = File.createTempFile("deletion",".keyFile");
                writer.writeValue(keyFile, sortedSet);
                System.out.println("Key File written to " + keyFile.getAbsolutePath());
            } else {
                ObjectMapper mapper = new ObjectMapper();
                Set<ModelAccessionIDTuple> matchSet = mapper.readValue(keyFile, Set.class);
                try {
                    client.remove_rdelete(Class.forName(tuple.getAdminModelClass()), String.valueOf(tuple.getId()), matchSet);
                    client.close();
                } catch (UniformInterfaceException ex) {
                    ret.setExitStatus(ReturnValue.FAILURE);
                    return ret;
                }
            }
            client.close();
            return ret;
        } catch (Exception ex) {
            throw new RuntimeException("Client malfunction", ex);
        }
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
        try {
            if (metadata.getWorkflowRun(targetAccession) != null) {
                tuple.setId(metadata.getWorkflowRun(targetAccession).getWorkflowRunId());
                tuple.setAdminModelClass(WorkflowRun.class.getName());
                return tuple;
            }
        } catch (Exception ex) {
            /**at net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB.translateToID(DeletionDB.java:157)
             * ignore and move on
             */
        }
        try {
            if (metadata.getSequencerRun(targetAccession) != null) {
                tuple.setId(metadata.getSequencerRun(targetAccession).getSequencerRunId());
                tuple.setAdminModelClass(SequencerRun.class.getName());
                return tuple;
            }
        } catch (Exception ex) {
            /**
             * ignore and move on
             */
        }
        try {
            if (metadata.getLane(targetAccession) != null) {
                tuple.setId(metadata.getLane(targetAccession).getLaneId());
                tuple.setAdminModelClass(Lane.class.getName());
                return tuple;
            }
        } catch (Exception ex) {
            /**
             * ignore and move on
             */
        }
        throw new RuntimeException("Could not locate target, please double-check your SWID");
    }
}
