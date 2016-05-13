package net.sourceforge.seqware.pipeline.plugins.deletion;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import io.seqware.pipeline.SqwKeys;
import io.seqware.webservice.client.SeqWareWebServiceClient;
import io.seqware.webservice.controller.ModelAccessionIDTuple;
import io.seqware.webservice.generated.model.WorkflowRun;
import joptsimple.OptionSpec;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.type.TypeReference;
import org.openide.util.lookup.ServiceProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A workflow run deletion tool for your SeqWare metadb
 * 
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public final class DeletionDB extends Plugin {

    private OptionSpec<Integer> workflowRunSpec;
    private OptionSpec<String> inKeyFileSpec;
    private OptionSpec<String> outKeyFileSpec;

    private Integer workflow_run_target = null;
    private File inKeyFile = null;
    private String adminUrl = null;
    private File outKeyFile = null;

    /**
     * <p>
     * Constructor for HelloWorld.
     * </p>
     */
    public DeletionDB() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        workflowRunSpec = parser
                .acceptsAll(Arrays.asList("workflowrun", "r"),
                        "Give a sequencer run, lane, or workflow run SWID in order to determine which workflow runs (processings,files) should be deleted.")
                .withRequiredArg().ofType(Integer.class).required();
        inKeyFileSpec = parser
                .acceptsAll(Arrays.asList("key", "k"), "An existing key file will be used to guide an actual deletion process")
                .withRequiredArg().ofType(String.class);
        outKeyFileSpec = parser.acceptsAll(Arrays.asList("out", "o"), "The filename of the output key file").withRequiredArg()
                .ofType(String.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public final ReturnValue init() {
        try {
            HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
            this.adminUrl = settings.get(SqwKeys.SW_ADMIN_REST_URL.getSettingKey());
        } catch (Exception e) {
            ReturnValue ret = new ReturnValue();
            ret.setExitStatus(ReturnValue.SETTINGSFILENOTFOUND);
            return ret;
        }
        if (this.adminUrl == null) {
            ReturnValue ret = new ReturnValue();
            System.out.println("This utility requires access to the admin web service. Configure "
                    + SqwKeys.SW_ADMIN_REST_URL.getSettingKey() + " in your .seqware/setttings");
            ret.setExitStatus(ReturnValue.SETTINGSFILENOTFOUND);
            return ret;
        }
        return new ReturnValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
     */
    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_test() {
        // TODO Auto-generated method stub
        return new ReturnValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public final ReturnValue do_run() {
        workflow_run_target = options.valueOf(workflowRunSpec);
        if (options.has(inKeyFileSpec)) {
            inKeyFile = new File(options.valueOf(inKeyFileSpec));
        }
        if (options.has(outKeyFileSpec)) {
            outKeyFile = new File(options.valueOf(outKeyFileSpec));
        }
        SeqWareWebServiceClient client = null;
        SeqWareWebServiceClient fileClient = null;
        try {
            ReturnValue ret = new ReturnValue();
            ModelAccessionIDTuple tuple = translateToID(workflow_run_target);
            client = new SeqWareWebServiceClient("workflowrun", adminUrl + "/webresources");
            fileClient = new SeqWareWebServiceClient("file", adminUrl + "/webresources");
            if (inKeyFile == null) {
                // create a key file
                Set<ModelAccessionIDTuple> findJSONrdelete = client.find_JSON_rdelete(Class.forName(tuple.getAdminModelClass()),
                        String.valueOf(tuple.getId()));
                // add to a sorted set for easy viewing
                SortedSet<ModelAccessionIDTuple> sortedSet = new TreeSet<>();
                sortedSet.addAll(findJSONrdelete);
                // output information for informational purposes
                outputSummaryInformation(sortedSet, fileClient);

                ObjectMapper mapper = new ObjectMapper();
                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                if (outKeyFile == null) {
                    outKeyFile = File.createTempFile("deletion", ".keyFile");
                } else {
                    boolean createOutput = outKeyFile.createNewFile();
                    if (!createOutput) {
                        ret.setExitStatus(ReturnValue.FILENOTWRITABLE);
                        return ret;
                    }
                }
                writer.writeValue(outKeyFile, sortedSet);
                System.out.println("Key File written to " + outKeyFile.getAbsolutePath());
            } else {
                ObjectMapper mapper = new ObjectMapper();
                Set<ModelAccessionIDTuple> matchSet;
                Set<String> filesToBeDeleted = new HashSet<>();
                try {
                    matchSet = mapper.readValue(inKeyFile, new TypeReference<Set<ModelAccessionIDTuple>>() {
                    });
                    for (ModelAccessionIDTuple t : matchSet) {
                        if (t.getAdminModelClass().equals(io.seqware.webservice.generated.model.File.class.getName())) {
                            io.seqware.webservice.generated.model.File file = fileClient.find_JSON(
                                    io.seqware.webservice.generated.model.File.class, String.valueOf(t.getId()));
                            filesToBeDeleted.add(file.getFilePath());
                        }
                    }
                    client.remove_rdelete(Class.forName(tuple.getAdminModelClass()), String.valueOf(tuple.getId()), matchSet);
                    client.close();
                } catch (IOException ex) {
                    System.out.println("Invalid data in provided key file");
                    ret.setExitStatus(ReturnValue.INVALIDFILE);
                    return ret;
                } catch (UniformInterfaceException ex) {
                    if (ex.getResponse().getClientResponseStatus().equals(Status.NOT_FOUND)) {
                        System.out.println("Accession not found");
                        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                        return ret;
                    } else if (ex.getResponse().getClientResponseStatus().equals(Status.CONFLICT)) {
                        System.out.println("KeyFile does not match current server content, could not delete.");
                        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                        return ret;
                    } else {
                        System.out.println("ClientResponseStatus: " + ex.getResponse().getClientResponseStatus());
                        System.out.println("ClientResponse: " + ex.getResponse().toString());
                        ret.setExitStatus(ReturnValue.FAILURE);
                        return ret;
                    }
                }
                System.out.println("Successful deletion of entities listed in " + inKeyFile.getAbsolutePath());
                // output files as candidates for deletion
                File fileListing = File.createTempFile("file", ".listing");
                for (String path : filesToBeDeleted) {
                    FileUtils.write(fileListing, path + '\n',StandardCharsets.UTF_8, true);
                }
                System.out.println("File records deleted for files listed in " + fileListing.getAbsolutePath());
            }
            return ret;
        } catch (UniformInterfaceException ex) {
            if (ex.getResponse().getClientResponseStatus().equals(Status.NOT_FOUND)) {
                System.out.println("Accession not found");
                ReturnValue ret = new ReturnValue();
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return ret;
            } else {
                System.out.println("Exception message: " + ex.getMessage());
                try {
                    String entity1 = ex.getResponse().getEntity(String.class);
                    System.out.println(entity1);
                    ReturnValue ret = new ReturnValue();
                    ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                    return ret;
                } catch (Exception e) {
                    /**
                     * ignore
                     */
                }
                ReturnValue ret = new ReturnValue();
                ret.setExitStatus(ReturnValue.FAILURE);
                return ret;
            }
        } catch (ClientHandlerException ex) {
            ReturnValue ret = new ReturnValue();
            System.out.println("Error connecting to admin web service, check that you have access to SW_ADMIN_REST_URL");
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        } catch (Exception ex) {
            throw new RuntimeException("Client malfunction", ex);
        } finally {
            if (client != null) client.close();
            if (fileClient != null) fileClient.close();
        }
    }

    /**
     * <p>
     * get_description.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    @Override
    public final String get_description() {
        return ("A database deletion tool for your SeqWare metadb.");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        DeletionDB mp = new DeletionDB();
        mp.init();
        List<String> arr = new ArrayList<>();
        mp.setParams(arr);
        mp.parse_parameters();
        mp.do_run();
    }

    @Override
    public final ReturnValue clean_up() {
        return new ReturnValue();
    }

    private ModelAccessionIDTuple translateToID(int targetAccession) {
        SeqWareWebServiceClient client = new SeqWareWebServiceClient("workflowrun", adminUrl + "/webresources");
        ModelAccessionIDTuple tuple = client.findTupleByAccession(String.valueOf(targetAccession));
        if (tuple != null) {
            return tuple;
        }
        throw new RuntimeException("Could not locate target, please double-check your SWID");
    }

    /**
     * Output various convenient statistics and information on files for the end-user
     * 
     * @param sortedSet
     * @param fileClient
     * @throws UniformInterfaceException
     */
    private void outputSummaryInformation(SortedSet<ModelAccessionIDTuple> sortedSet, SeqWareWebServiceClient fileClient)
            throws UniformInterfaceException {
        // we can output some friendly summary statistics here
        int workflowRunCount = 0;
        Map<String, Integer> fileTypeCounts = new HashMap<>();
        for (ModelAccessionIDTuple t : sortedSet) {
            if (t.getAdminModelClass().equals(WorkflowRun.class.getName())) {
                workflowRunCount++;
            } else if (t.getAdminModelClass().equals(io.seqware.webservice.generated.model.File.class.getName())) {
                io.seqware.webservice.generated.model.File file = fileClient.find_JSON(io.seqware.webservice.generated.model.File.class,
                        String.valueOf(t.getId()));
                String metaType = file.getMetaType();
                if (!fileTypeCounts.containsKey(metaType)) {
                    fileTypeCounts.put(metaType, 0);
                }
                fileTypeCounts.put(metaType, fileTypeCounts.get(metaType) + 1);
            }
        }
        System.out.println("Key file contains " + workflowRunCount + " workflow runs.");
        for (Entry<String, Integer> e : fileTypeCounts.entrySet()) {
            System.out.println(" \t" + e.getValue() + " file" + (e.getValue() > 1 ? "s" : "") + " of type " + e.getKey());
        }
        if (fileTypeCounts.size() > 0) {
            System.out.println("File paths for files to be deleted: ");
            for (ModelAccessionIDTuple t : sortedSet) {
                if (t.getAdminModelClass().equals(io.seqware.webservice.generated.model.File.class.getName())) {
                    io.seqware.webservice.generated.model.File file = fileClient.find_JSON(
                            io.seqware.webservice.generated.model.File.class, String.valueOf(t.getId()));
                    System.out.println("\t" + file.getFilePath());
                }
            }
        }
    }
}
