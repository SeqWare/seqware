package io.seqware.cli.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import io.seqware.cli.Main;
import io.seqware.pipeline.plugins.WorkflowLifecycle;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;

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
        net.sourceforge.seqware.common.metadata.Metadata.class, net.sourceforge.seqware.common.metadata.MetadataFactory.class })
@RunWith(PowerMockRunner.class)
public class WorkflowRunTest {
    private String errBuff;

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
