package io.seqware.cli.test;

import io.seqware.cli.Main;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@PrepareForTest({ io.seqware.cli.Main.class, net.sourceforge.seqware.pipeline.runner.PluginRunner.class })
@RunWith(PowerMockRunner.class)
public class CreateTest {

    private String errBuff = "";
    private String outBuff = "";

    @Before
    public void setUp() {
        errBuff = "";
        outBuff = "";
    }

    class WriteFormattedMessageToBufferAnswer implements Answer<Object> {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            errBuff = String.format((String) invocation.getArguments()[0],
                    Arrays.copyOfRange(invocation.getArguments(), 1, invocation.getArguments().length));
            throw new Exception(errBuff);
        }
    }

    class WorkflowRunnerAnswer implements Answer<Object> {
        private String[] runnerArgs;

        public void setRunnerArgs(String[] args) {
            this.runnerArgs = args;
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            System.out.println("\nArgs to PluginRunner.run: ");
            for (String s : (String[]) invocation.getArguments()[0]) {
                System.out.print(s + " ");
            }
            String[] givenArgs = ((String[]) invocation.getArguments()[0]);
            for (int i = 0; i < givenArgs.length; i++) {
                assertEquals("mismatch on arg #" + i, runnerArgs[i], givenArgs[i]);
            }

            return new ReturnValue();
        }
    }

    private void testWithError(String[] mainArgs, String errorMessage) throws Exception {
        PowerMockito.spy(Main.class);
        PowerMockito.doAnswer(new WriteFormattedMessageToBufferAnswer()).when(Main.class, "kill", anyString(),
                Matchers.<Object> anyVararg());
        try {
            Main.main(mainArgs);
        } catch (Exception e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }

    @Test
    public void createWithErrorInvalidFileSeparator() throws Exception {

        String[] mainArgs = new String[] { "create", "workflow-run", "--workflow-accession", "123", "--parent-accession", "321", "--file",
                "a:b:c" };
        testWithError(mainArgs, "seqware: improper number of separator :: in 'a:b:c'.");
    }

    @Test
    public void createFileError() throws Exception {

        String[] mainArgs = new String[] { "create", "file", "--workflow-accession", "123", "--parent-accession", "321", "--file", "a:b:c" };
        testWithError(mainArgs, "seqware: 'create file' is not a seqware command. See 'seqware create --help'.");
    }

    @Test
    public void createWithErrorNoParentAccession() throws Exception {

        String[] mainArgs = new String[] { "create", "workflow-run", "--workflow-accession", "123" };
        testWithError(mainArgs,
                "seqware: by convention, workflow runs should be hooked up to parent accessions for metadata tracking and deciders.");
    }

    @Test
    public void createExperimentTest() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "experiment", "--description", "SomeDescription", "--platform-id", "PLATFORM-123",
                "--study-accession", "12345", "--title", "SomeExperiment" };
        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
                "experiment", "--create", "--field", "description::SomeDescription", "--field", "platform_id::PLATFORM-123", "--field",
                "study_accession::12345", "--field", "title::SomeExperiment" };
        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    @Test
    public void createIUSTest() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "ius", "--barcode", "BarcodeValue", "--description", "TestDescription",
                "--lane-accession", "6789", "--name", "testName", "--sample-accession", "12345", "--skip", "false" };

        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table", "ius",
                "--create", "--field", "barcode::BarcodeValue", "--field", "description::TestDescription", "--field",
                "lane_accession::6789", "--field", "name::testName", "--field", "sample_accession::12345", "--field", "skip::false" };

        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    @Test
    public void createLaneTest() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "lane", "--cycle-descriptor", "SomeCycleDescriptor", "--description",
                "TestDescription", "--lane-number", "6789", "--library-selection-accession", "1234", "--library-source-accession", "4567",
                "--library-strategy-accession", "7890", "--name", "LaneName", "--sequencer-run-accession", "4631", "--skip", "false",
                "--study-type-accession", "1122" };

        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
                "lane", "--create", "--field", "cycle_descriptor::SomeCycleDescriptor", "--field", "description::TestDescription",
                "--field", "lane_number::6789", "--field", "library_selection_accession::1234", "--field",
                "library_source_accession::4567", "--field", "library_strategy_accession::7890", "--field", "name::LaneName", "--field",
                "sequencer_run_accession::4631", "--field", "skip::false", "--field", "study_type_accession::1122" };

        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    @Test
    public void createSampleTest() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "sample", "--experiment-accession", "123", "--description", "TestDescription",
                "--organism-id", "234", "--title", "SomeTitle", "--parent-sample-accession", "789" };

        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
                "sample", "--create", "--field", "description::TestDescription", "--field", "experiment_accession::123",
                "--field", "organism_id::234", "--field", "title::SomeTitle", "--field", "parent_sample_accession::789" };

        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    @Test
    public void createSampleErrorNoParentAccessionTest() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "sample", "--experiment-accession", "123", "--description", "TestDescription",
                "--organism-id", "234", "--title", "SomeTitle"};

        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
                "sample", "--create", "--field", "description::TestDescription", "--field", "experiment_accession::123",
                "--field", "organism_id::234", "--field", "title::SomeTitle" };

        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    @Test
    public void createSequencerRunTest() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "sequencer-run", "--description", "BLAHBLAHBLAH", "--file-path", "somePath", "--name",
                "SomeName", "--paired-end", "pairedEnd", "--platform-accession", "555", "--skip", "false" };

        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
                "sequencer_run", "--create", "--field", "description::BLAHBLAHBLAH", "--field", "file_path::somePath", "--field",
                "name::SomeName", "--field", "paired_end::pairedEnd", "--field", "platform_accession::555", "--field", "skip::false" };

        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    @Test
    public void createStudyTest() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "study", "--description", "BLAHBLAHBLAH", "--accession", "123", "--center-name",
                "SomeName", "--center-project-name", "CenterProjectName", "--study-type", "aType", "--title", "SomeTitle" };

        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
                "study", "--create", "--field", "accession::123", "--field", "center_name::SomeName", "--field",
                "center_project_name::CenterProjectName", "--field", "description::BLAHBLAHBLAH", "--field", "study_type::aType",
                "--field", "title::SomeTitle" };

        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    @Test
    public void createWorkflowTest() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "workflow", "--name", "WFName", "--version", "1", "--description", "WFDescription" };

        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
                "workflow", "--create", "--field", "name::WFName", "--field", "version::1", "--field", "description::WFDescription" };

        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    @Test
    public void createWorkflowRunTest() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "workflow-run", "--workflow-accession", "123", "--parent-accession", "321", "--file",
                "a::b::c" };

        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
                "workflow_run", "--create", "--field", "workflow_accession::123", "--field", "status::completed", "--file", "a::b::c",
                "--parent-accession", "321" };

        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    @Test
    public void createWorkflowRunTestNoFile() throws Exception {
        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());

        String[] mainArgs = new String[] { "create", "workflow-run", "--workflow-accession", "123", "--parent-accession", "321" };

        final String[] runnerArgs = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
                "workflow_run", "--create", "--field", "workflow_accession::123", "--field", "status::completed", "--parent-accession",
                "321" };

        testWithSuccess(spiedRunner, mainArgs, runnerArgs);
    }

    private void testWithSuccess(PluginRunner spiedRunner, String[] mainArgs, final String[] runnerArgs) throws Exception {
        WorkflowRunnerAnswer answer = new WorkflowRunnerAnswer();
        answer.setRunnerArgs(runnerArgs);
        PowerMockito.doAnswer(answer).when(spiedRunner, "run", any());
        PowerMockito.whenNew(PluginRunner.class).withNoArguments().thenReturn(spiedRunner);
        Main.main(mainArgs);
    }
}
