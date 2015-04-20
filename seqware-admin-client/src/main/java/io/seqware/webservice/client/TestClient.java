package io.seqware.webservice.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jersey.api.client.UniformInterfaceException;

import io.seqware.webservice.client.SeqWareWebServiceClient;
import io.seqware.webservice.generated.model.Experiment;
import io.seqware.webservice.generated.model.ExperimentAttribute;
import io.seqware.webservice.generated.model.Lane;
import io.seqware.webservice.generated.model.LaneAttribute;
import io.seqware.webservice.generated.model.Registration;
import io.seqware.webservice.generated.model.Sample;
import io.seqware.webservice.generated.model.Study;
import io.seqware.webservice.generated.model.StudyAttribute;
import io.seqware.webservice.generated.model.StudyType;
import io.seqware.webservice.generated.model.Workflow;
import io.seqware.webservice.generated.model.WorkflowRun;

public class TestClient {
    private static String URL = "http://localhost:38081/seqware-admin-webservice/webresources";

    private static Map<String, SeqWareWebServiceClient> clients = new HashMap<String, SeqWareWebServiceClient>();
    private static Map<ClientKeys, SeqWareWebServiceClient> _clients = new HashMap<ClientKeys, SeqWareWebServiceClient>();

    /*
     * TODO Examples:
     *  - Function: Get All Samples for a Study (via Study -> Experiment -> Sample )
     *  - Function: Get all Files for a Study (or all Samples in a Study)
     *  - Create Workflow, Workflow Run with associated Processing records; chain of connected Workflow Runs
     *  - Authentication for "risky" operations (create/update/deletion) - in the server side. 
     *  - Validate entities for null (and other) issues before sending to service? 
     */
    
    enum ClientKeys {
        lane, experiment, workflow, sample, study, studytype, registration
    }

    static {
        clients.put("workflow", new SeqWareWebServiceClient("workflow", URL));
        clients.put("sample", new SeqWareWebServiceClient("sample", URL));
        clients.put("study", new SeqWareWebServiceClient("study", URL));
        clients.put("studytype", new SeqWareWebServiceClient("studytype", URL));
        clients.put("registration", new SeqWareWebServiceClient("registration", URL));
        clients.put("lane", new SeqWareWebServiceClient("lane", URL));
        clients.put("experiment", new SeqWareWebServiceClient("experiment", URL));
        clients.put("workflowrun", new SeqWareWebServiceClient("workflowrun", URL));
    }

    private static List<Sample> getSamplesForStudy(String studyId)
    {
        List<Sample> samples = null;
        
        SeqWareWebServiceClient studyClient = clients.get("study");
        SeqWareWebServiceClient sampleClient = clients.get("sample");
        SeqWareWebServiceClient experimentClient = clients.get("experiment");
        
        List<Study> studies = studyClient.getEntitiesWhereFieldMatchesValue(Study.class, "studyId", studyId);
        for (Study study : studies)
        {
            List<Experiment> experiments = experimentClient.getEntitiesWhereFieldMatchesValue(Experiment.class, "studyId", study.getStudyId().toString());
            for (Experiment experiment : experiments)
            {
                List<Sample> experimentSamples = sampleClient.getEntitiesWhereFieldMatchesValue(Sample.class, "experimentId", experiment.getExperimentId().toString());
                if (samples == null)
                {
                    samples = experimentSamples;
                }
                else
                {
                    samples.addAll(experimentSamples);
                }
            }
        }
        
        return samples;
    }
    
    private static void createSamples()
    {
        Sample rootSample = new Sample();
        Sample childSample1 = new Sample();
        Sample childSample2 = new Sample();
        Sample grandchildSample = new Sample();
        
        rootSample.setName("Root Sample");
        rootSample.setCreateTstmp(new Date());
        rootSample.setAlias("The Root Sample Alias!");
        rootSample.setCommonName("common name");
        rootSample.setAnonymizedName("kjsdkjawe983298rvhq3");
        rootSample.setDescription("Testing creation of sample hierarchies");

        childSample1.setName("Child Sample 1");
        childSample1.setCreateTstmp(new Date());
        childSample2.setName("Child Sample 2");
        childSample2.setCreateTstmp(new Date());
        grandchildSample.setName("Grand child sample");
        grandchildSample.setCreateTstmp(new Date());
        
        Collection<Sample> grandchildren = new ArrayList<Sample>(2);
        grandchildren.add(grandchildSample);
        
        //Build up the relationships.
        childSample2.setChildSamples(grandchildren);

        Collection<Sample> children = new ArrayList<Sample>(2);
        children.add(childSample1);
        children.add(childSample2);
        
        rootSample.setChildSamples(children);
        
        Sample newSample = clients.get("sample").createAndReturn(Sample.class, rootSample);
        System.out.println("Sample ID: "+newSample.getSampleId());
    }

    private static void createExperiment() {
        Experiment experiment = new Experiment();
        experiment.setName("TestExperiment");
        experiment.setCreateTstmp(new Date());
        Study study = clients.get("study").find_XML(Study.class, "40");
        experiment.setStudyId(study);
        

        Collection<ExperimentAttribute> experimentAttributes = new ArrayList<ExperimentAttribute>(2);
        ExperimentAttribute eAttr1 = new ExperimentAttribute();
        eAttr1.setTag("experiment tag 1");
        eAttr1.setValue("asdfasdfs");
        eAttr1.setExperimentId(experiment);
        ExperimentAttribute eAttr2 = new ExperimentAttribute();
        eAttr2.setTag("experiment tag 2");
        eAttr2.setValue("ds0934j");
        eAttr2.setExperimentId(experiment);
        experimentAttributes.add(eAttr1);
        experimentAttributes.add(eAttr2);
        experiment.setExperimentAttributeCollection(experimentAttributes);

        SeqWareWebServiceClient experimentClient = clients.get("experiment");
       /* try {
            // Calling the default (generated) create_* methods doesn't return anything, so you won't know what the ID
            // of the newly created object is. Not terribly useful.
            experimentClient.create_XML(experiment);
            System.out.println("Experiment ID: " + experiment.getExperimentId());
            System.out.println("attributes: " + experiment.getExperimentAttributeCollection());
        } catch (UniformInterfaceException e) {
            System.out.println("message: "+e.getMessage()+"\n\n");
            System.out.println("entity: "+e.getResponse().getEntity(String.class));
            e.printStackTrace();
        }*/
        try {
            // Custom "create" method, will return the updated object.
            if (experiment.getStudyId()==null)
                throw new RuntimeException("Should not be null");
            experiment = experimentClient.createAndReturn(Experiment.class, experiment);
            System.out.println("Experiment ID: " + experiment.getExperimentId());
            System.out.println("attributes: " + experiment.getExperimentAttributeCollection());
            for (ExperimentAttribute ea : experiment.getExperimentAttributeCollection())
            {
                System.out.println(ea + " tag: "+ea.getTag() + " val: "+ea.getValue());
            }
        } catch (UniformInterfaceException e) {
            System.out.println("message: "+e.getMessage()+"\n\n");
            System.out.println("entity: "+e.getResponse().getEntity(String.class));
            e.printStackTrace();
        }
        
        experiment.setName("Updated Name");
        experiment.setAlias("Updated Alias");
        // To update child entities:
        // 1) move them to a new data structure (if you can update the Collection in-place, that would probably work too, though I'm not sure it's possible). 
        ExperimentAttribute[] tmpAttributeCollection = new ExperimentAttribute[experiment.getExperimentAttributeCollection().size()-1] ;
        tmpAttributeCollection = experiment.getExperimentAttributeCollection().toArray(tmpAttributeCollection);
        // 2) modify them
        for (ExperimentAttribute ea : tmpAttributeCollection)
        {
            ea.setValue(ea.getValue() + " - updated");
        }
        // 3) move the new structure back into the main entity
        experiment.setExperimentAttributeCollection( Arrays.asList(tmpAttributeCollection) );
        System.out.println("Experiment attributes updated: ");
        for (ExperimentAttribute ea : experiment.getExperimentAttributeCollection())
        {
            System.out.println(ea + " tag: "+ea.getTag() + " val: "+ea.getValue());
        }
        // 4) Update the entity
        Experiment updatedExperiment = experimentClient.updateAndReturn(Experiment.class, experiment);
        // 5) verification is below:
        System.out.println(updatedExperiment.getName());
        System.out.println(updatedExperiment.getAlias());
        System.out.println(updatedExperiment.getExperimentId());
        System.out.println(updatedExperiment.getExperimentAttributeCollection());
        for (ExperimentAttribute ea : updatedExperiment.getExperimentAttributeCollection())
        {
            System.out.println(ea + " tag: "+ea.getTag() + " val: "+ea.getValue());
        }
        
        Experiment retrievedExperiment = experimentClient.find_XML(Experiment.class, String.valueOf(experiment.getExperimentId()));
        System.out.println(retrievedExperiment.getName());
        System.out.println(retrievedExperiment.getAlias());
        System.out.println(retrievedExperiment.getExperimentId());
        System.out.println(retrievedExperiment.getExperimentAttributeCollection());
        for (ExperimentAttribute ea : retrievedExperiment.getExperimentAttributeCollection())
        {
            System.out.println(ea + " tag: "+ea.getTag() + " val: "+ea.getValue());
        }

    }

    private static void createLane() {
        Lane lane = new Lane();
        SeqWareWebServiceClient laneClient = clients.get("lane");
        // I'm pretty sure that to create a new Lane, not even the Alias or Name are required.
        // Most of the fields are nullable.
        lane.setAlias("Lane Alias");
        lane.setCreateTstmp(new Date());
        lane.setName("The Lane");

        // Attributes are optional, but since that seems to be a major need, let's demonstrate creating
        // a lange with attributes attached.

        Collection<LaneAttribute> laneAttributes = new ArrayList<LaneAttribute>(2);

        LaneAttribute laneAttrib1 = new LaneAttribute();
        laneAttrib1.setTag("some tag 1");
        laneAttrib1.setValue("some value 1");

        LaneAttribute laneAttrib2 = new LaneAttribute();
        laneAttrib2.setTag("some tag 2");
        laneAttrib2.setValue("some value 2");

        laneAttributes.add(laneAttrib1);
        laneAttributes.add(laneAttrib2);

        lane.setLaneAttributeCollection(laneAttributes);

        Lane newLane = laneClient.createAndReturn(Lane.class, lane);//createLane(lane);
        System.out.println("new lane ID: " + newLane.getLaneId());

        System.out.println("# New Lane attributes: " + newLane.getLaneAttributeCollection());

        testSkipLane(newLane);
    }

    private static void testSkipLane(Lane lane) {
        SeqWareWebServiceClient laneclient = clients.get("lane");

        System.out.println("Lane is skipped: " + lane.getSkip());
        lane = laneclient.find_XML(Lane.class, String.valueOf(lane.getLaneId()));

        // laneclient.skipLane(lane.getLaneId());
        laneclient.skip(String.valueOf(lane.getLaneId()));
        lane = laneclient.find_XML(Lane.class, String.valueOf(lane.getLaneId()));
        System.out.println("Lane is skipped: " + lane.getSkip());

        laneclient.unskip(String.valueOf(lane.getLaneId()));
        lane = laneclient.find_XML(Lane.class, String.valueOf(lane.getLaneId()));
        System.out.println("Lane is skipped: " + lane.getSkip());
    }

    private static void createStudy() {
        Study s = new Study();
        SeqWareWebServiceClient studyTypeClient = clients.get("studytype");
        StudyType existingType = studyTypeClient.find_XML(StudyType.class, "1");
        s.setExistingType(existingType);
        SeqWareWebServiceClient registrationClient = clients.get("registration");
        Registration ownerId = registrationClient.find_XML(Registration.class, "1");
        s.setOwnerId(ownerId);
        s.setTitle("Test Title");
        s.setCenterName("Center name");
        s.setCenterProjectName("Center Project Name");
        s.setProjectId(123);
        s.setCreateTstmp(new Date());

        Collection<StudyAttribute> studyAttributeCollection = new ArrayList<StudyAttribute>(3);
        StudyAttribute sa1 = new StudyAttribute();
        sa1.setTag("tag 1");
        sa1.setValue("value 1");
        // sa1.setStudyId(s); //It's not actually necessary to connect the child-to-parent relationship because the custom endpoint will
        // handle that anyway.
        StudyAttribute sa2 = new StudyAttribute();
        sa2.setTag("tag two");
        sa2.setValue("value two");
        // sa2.setStudyId(s);
        StudyAttribute sa3 = new StudyAttribute();
        sa3.setTag("tag iii");
        sa3.setValue("value iii");
        // sa3.setStudyId(s);
        studyAttributeCollection.add(sa1);
        studyAttributeCollection.add(sa2);
        studyAttributeCollection.add(sa3);

        s.setStudyAttributeCollection(studyAttributeCollection);
        SeqWareWebServiceClient studyClient = clients.get("study");
        Study genStudy = studyClient.createAndReturn(Study.class,s);//Study(s);
        System.out.println("New study ID: " + genStudy.getStudyId());
        System.out.println("number of attributes: " + genStudy.getStudyAttributeCollection().size());
        // print the study's own Id and the study's Id, according to the attribute
        System.out.println(genStudy.getStudyId());
        System.out.println((new ArrayList<StudyAttribute>(genStudy.getStudyAttributeCollection())).get(0).getStudyId().getStudyId());

        if (genStudy.getStudyId() != (new ArrayList<StudyAttribute>(genStudy.getStudyAttributeCollection())).get(0).getStudyId()
                .getStudyId()) throw new RuntimeException("the persisted study Id does not match the Id of the study's attributes!");

        Study newStudy = studyClient.find_XML(Study.class, String.valueOf(genStudy.getStudyId()));
        System.out.println(newStudy.getStudyAttributeCollection());
        System.out.println("studies are the same: " + newStudy.equals(genStudy));
    }

    public static void main(String[] args) {
        SeqWareWebServiceClient workflowClient = clients.get("workflow");
        String numClientsJSON = workflowClient.countREST();
        System.out.println("Number of workflows: " + numClientsJSON);
        SeqWareWebServiceClient sampleClient = clients.get("sample");
        String numSamples = sampleClient.countREST();
        System.out.println("Number of samples: " + numSamples);

        List<Sample> samples = (List<Sample>) getSamplesByName("Fifty");
        System.out.println(samples.size());
        System.out.println("Sample ID: " + samples.get(0).getSampleId());

        List<Sample> samples2 = sampleClient.<Sample>getEntitiesWhereFieldMatchesValue(Sample.class,"name", "Fifty");
        System.out.println(samples2.size());
        System.out.println("Sample ID: " + samples2.get(0).getSampleId());

        List<Sample> samples3 = sampleClient.<Sample>getEntitiesWhereFieldMatchesValue(Sample.class,"title", "FiftyFifty");
        System.out.println(samples3.size());
        System.out.println("Sample ID: " + samples3.get(0).getSampleId());

        
        createStudy();
        createLane();
        //createExperiment();
        
        String studyId = "13";
        List<Sample> studySamples = getSamplesForStudy(studyId);
        System.out.println("# samples in study "+studyId+": "+studySamples.size());
        System.out.println(studySamples);
        
        //Demonstrate an error when you try to skip an entity that is not skippable.
        try
        {
            clients.get("studytype").skip("1");
        }
        catch (UniformInterfaceException e)
        {
            System.out.println("Exception Message: "+e.getMessage());
            System.out.println("Exception Response Entity: "+e.getResponse().getEntity(String.class));
        }
        
        createSamples();
        createWorkflowRun();
        
    }

    private static void createWorkflowRun()
    {
        Workflow wf = new Workflow();
        List<Workflow> wfList = new ArrayList<Workflow>();
        SeqWareWebServiceClient workflowClient = clients.get("workflow");
        wfList = (List<Workflow> )workflowClient.getEntitiesWhereFieldMatchesValue(Workflow.class, "name", "GATKRecalibrationAndVariantCalling");
        System.out.println("# workflows with name GATKRecalibrationAndVariantCalling: "+wfList.size());
        wf = wfList.get(0);
        System.out.println("Workflow ID: "+wf.getWorkflowId());
        
        WorkflowRun wfRun = new WorkflowRun();
        wfRun.setWorkflowId(wf);
        wfRun.setCreateTstmp(new Date());
        wfRun.setHost("myHost");
        wfRun.setName("test workflowRun creation");
        //wf.getWorkflowRunCollection().add(wfRun);
        try
        {
            SeqWareWebServiceClient workflowRunClient = clients.get("workflowrun");
            wfRun = workflowRunClient.createAndReturn(WorkflowRun.class, wfRun);
            System.out.println("Workflow Run ID is: "+wfRun.getWorkflowRunId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private static List<Sample> getSamplesByName(String string)
    {
        List<Sample> samples = clients.get("sample").getEntitiesWhereFieldMatchesValue(Sample.class, "name", "Fifty");
        return samples;
    }
}
