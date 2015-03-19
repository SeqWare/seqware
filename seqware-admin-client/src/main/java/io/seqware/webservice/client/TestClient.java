package io.seqware.webservice.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class TestClient {
    private static String URL = "http://localhost:38080/seqware-admin-webservice/webresources";

    private static Map<String, SeqWareWebServiceClient> clients = new HashMap<String, SeqWareWebServiceClient>();
    private static Map<ClientKeys, SeqWareWebServiceClient> _clients = new HashMap<ClientKeys, SeqWareWebServiceClient>();

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
        try {
            // Calling the default (generated) create_* methods doesn't return anything, so you won't know what the ID
            // of the newly created object is. Not terribly useful.
            experimentClient.create_XML(experiment);
            System.out.println("Experiment ID: " + experiment.getExperimentId());
            System.out.println("attributes: " + experiment.getExperimentAttributeCollection());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // Custom "create" method, will return the updated object.
            experiment = experimentClient.createExperiment(experiment);
            System.out.println("Experiment ID: " + experiment.getExperimentId());
            System.out.println("attributes: " + experiment.getExperimentAttributeCollection());
        } catch (Exception e) {
            e.printStackTrace();
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
        laneAttrib2.setTag("some tag 1");
        laneAttrib2.setValue("some value 1");

        laneAttributes.add(laneAttrib1);
        laneAttributes.add(laneAttrib2);

        lane.setLaneAttributeCollection(laneAttributes);

        Lane newLane = laneClient.createLane(lane);
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
        Study genStudy = studyClient.createStudy(s);
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

        List<Sample> samples = (List<Sample>) sampleClient.getSamplesByName("Fifty");
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
        createExperiment();
    }
}
