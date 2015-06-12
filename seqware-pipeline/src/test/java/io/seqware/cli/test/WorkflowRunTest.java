package io.seqware.cli.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.seqware.cli.Main;
import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.plugins.WorkflowLifecycle;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.FileTools.LocalhostPair;
import net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import net.sourceforge.seqware.pipeline.tools.RunLock;

import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.WorkflowJob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest({ io.seqware.cli.Main.class, net.sourceforge.seqware.pipeline.runner.PluginRunner.class,
        io.seqware.pipeline.plugins.WorkflowLifecycle.class, net.sourceforge.seqware.common.util.configtools.ConfigTools.class,
        net.sourceforge.seqware.common.metadata.Metadata.class, net.sourceforge.seqware.common.metadata.MetadataFactory.class,
        FileTools.class, WorkflowStatusChecker.class, RunLock.class})
@RunWith(PowerMockRunner.class)
public class WorkflowRunTest {
    private String errBuff;
    
    @Mock
    private OptionSet options;
    
    @Mock
    Metadata mockMD;

    @Before
    public void setUp() {
        errBuff = "";
        PowerMockito.mockStatic(MetadataFactory.class);
        PowerMockito.mockStatic(ConfigTools.class);
        PowerMockito.when(ConfigTools.getSettings()).thenReturn(new HashMap<String, String>());

    }

    class WriteFormattedMessageToBufferAnswer implements Answer<Object> {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            errBuff = String.format((String) invocation.getArguments()[0],
                    Arrays.copyOfRange(invocation.getArguments(), 1, invocation.getArguments().length));
            throw new Exception(errBuff);
        }
    }

    @Test
    public void testWorkflowRunINI() throws Exception {
        String[] mainArgs = { "workflow-run", "ini", "--accession", "12345", "--out", "test.out" };
        String[] runnerArgs = { "--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--list-workflow-params",
                "--workflow-accession", "12345" };
        Path path = FileSystems.getDefault().getPath("src/test/resources/bad.ini");

        String ini = new String(Files.readAllBytes(path));

        testWithSuccess(mainArgs, runnerArgs, ini);

    }

    @Test
    public void testWorkflowRunININoOut() throws Exception {
        String[] mainArgs = { "workflow-run", "ini", "--accession", "12345" };
        String[] runnerArgs = { "--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--list-workflow-params",
                "--workflow-accession", "12345" };
        Path path = FileSystems.getDefault().getPath("src/test/resources/bad.ini");

        String ini = new String(Files.readAllBytes(path));

        testWithSuccess(mainArgs, runnerArgs, ini);

    }
    
    @Test
    public void testCancelKilledJob() throws Exception {
       String[] mainArgs = { "workflow-run", "cancel", "--accession", "12345" };
        
       mockupFakeRuns();
       
       testWithError(mainArgs, "some error");
    }
    
    private void mockupFakeRuns() throws Exception {
        // mock up some fake workflow_runs so that their status can be checked
        List<WorkflowRun> wrList = new ArrayList<>();
            WorkflowRun wr = new WorkflowRun();
            wr.setOwnerUserName("user");
            wr.setWorkflowAccession(12345);
            wr.setWorkflowRunId(12345);
            wr.setSwAccession(12345);
            wr.setCommand("dummyValue");
            wr.setTemplate("dummyValue");
            wr.setCurrentWorkingDir("dummyValue");
            wr.setDax("dummyValue");
            wr.setIniFile("dummyValue");
            wr.setWorkflowEngine("oozie");
            wr.setHost("localhost");
            wr.setStatusCmd("pegasus-status -l /home/seqware/pegasus-dax/seqware/pegasus/FastqQualityReportAndFilter_0.10.0/run0012345" );
            wrList.add(wr);
        PowerMockito.mockStatic(FileTools.class);
        when(FileTools.getLocalhost(options)).thenReturn(new LocalhostPair("localhost", new ReturnValue(ReturnValue.SUCCESS)));
        when(FileTools.isFileOwner(anyString())).thenReturn(true);
        final OozieClient oozieClient = mock(OozieClient.class);
        PowerMockito.whenNew(OozieClient.class).withAnyArguments().thenReturn(oozieClient);

        final WorkflowJob workflowJob = mock(WorkflowJob.class);
        when(oozieClient.getJobInfo(anyString())).thenReturn(workflowJob);
        when(workflowJob.getStatus()).thenReturn(WorkflowJob.Status.KILLED);

/*        ReturnValue fakeReturn = new ReturnValue(ReturnValue.SUCCESS);
        fakeReturn.setAttribute("currStep", "1");
        fakeReturn.setAttribute("totalSteps", "1");
*/
        when(mockMD.getWorkflowRunsByStatus(any(WorkflowRunStatus.class))).thenReturn(wrList);
    }
    
    @Test
    public void testWorkflowRunINIExtraArgs() throws Exception {
        String[] mainArgs = { "workflow-run", "ini", "--accession", "12345", "--extra-arg" };

        testWithError(mainArgs, "seqware: unexpected arguments to 'workflow-run ini': --extra-arg");

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

    private void testWithSuccess(String[] mainArgs, String[] runnerArgs, String ini) throws Exception {

        WorkflowRun wf = new WorkflowRun();
        wf = new WorkflowRun();
        wf.setIniFile(ini);
        PowerMockito.when(mockMD.getWorkflowRun(anyInt())).thenReturn(wf);
        PowerMockito.when(MetadataFactory.get(anyMapOf(String.class, String.class))).thenReturn(mockMD);

        PluginRunner spiedRunner = PowerMockito.spy(new PluginRunner());
        PowerMockito.spy(Main.class);
        WorkflowLifecycle wfPlugin = PowerMockito.mock(WorkflowLifecycle.class);
        PowerMockito.when(wfPlugin.do_run()).thenReturn(new ReturnValue());
        PowerMockito.whenNew(WorkflowLifecycle.class).withAnyArguments().thenReturn(wfPlugin);

        WorkflowRunnerAnswer answer = new WorkflowRunnerAnswer();
        answer.setRunnerArgs(runnerArgs);
        PowerMockito.doAnswer(answer).when(spiedRunner, "run", any());

        PowerMockito.whenNew(PluginRunner.class).withNoArguments().thenReturn(spiedRunner);

        Main.main(mainArgs);
    }
}
