/*
 * Copyright (C) 2013 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.pipeline.plugins;

import it.sauronsoftware.junique.JUnique;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.FileTools.LocalhostPair;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowTools;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author dyuen
 */
@PrepareForTest({WorkflowTools.class, FileTools.class, WorkflowStatusChecker.class})
public class WorkflowStatusCheckerTest extends PowerMockTestCase{
   
    
    @Mock 
    private Map<String, String> config;
    
    @Mock
    private OptionSet options;
    
    @Mock
    private net.sourceforge.seqware.common.metadata.Metadata metadata;
    
    @InjectMocks
    private WorkflowStatusChecker workflowStatusChecker;
    
    @BeforeMethod
    public void initMocks() throws Exception{
        reset(config, options, metadata);
        workflowStatusChecker = new WorkflowStatusChecker(); // this is kind of hacky
        // apparantly testNG retains the state of mocks and statuschecker from test to test, so we need to rebuild everything
        MockitoAnnotations.initMocks(this);
        when(options.has("force-host")).thenReturn(true);
        when(options.valueOf("force-host")).thenReturn("localhost");
        when(config.get("SW_REST_USER")).thenReturn("user");
    }
    
    @AfterMethod
    public void cleanMocks(){
        JUnique.releaseLock(WorkflowStatusChecker.appID);
    }
    
    @Test
    public void testShouldInjectMocks(){
        Assert.assertNotNull(metadata);
        Assert.assertNotNull(workflowStatusChecker);
        Assert.assertNotNull(workflowStatusChecker.getMetadata());
    }
    
    @Test
    public void testInitLocking(){
        final ReturnValue ret1 = workflowStatusChecker.init();
        Assert.assertTrue(ret1.getExitStatus() == ReturnValue.SUCCESS, "workflowStatusChecker could not init");
        final ReturnValue ret2 = workflowStatusChecker.init();
        Assert.assertTrue(ret2.getExitStatus() == ReturnValue.FAILURE, "workflowStatusChecker not properly locked in init");
    }
    
    @Test 
    public void testEmptyRun(){
        final ReturnValue ret1 = workflowStatusChecker.init();
        Assert.assertTrue(ret1.getExitStatus() == ReturnValue.SUCCESS, "workflowStatusChecker could not init");
        final ReturnValue ret2 = workflowStatusChecker.do_run();
        Assert.assertTrue(ret2.getExitStatus() == ReturnValue.SUCCESS, "workflowStatusChecker ran properly");
        verify(metadata).getWorkflowRunsByStatus(metadata.RUNNING);
        verify(metadata).getWorkflowRunsByStatus(metadata.PENDING);
        verifyNoMoreInteractions(metadata);
    }
    
    @Test 
    public void testNormalRun() throws Exception{       
        final ReturnValue ret1 = workflowStatusChecker.init();
        Assert.assertTrue(ret1.getExitStatus() == ReturnValue.SUCCESS, "workflowStatusChecker could not init");
        mockupFakeRuns();
        final ReturnValue ret2 = workflowStatusChecker.do_run();
        verifyNormalRun(ret2);
    }
    
    @Test 
    public void testDoubleThreadedRun() throws Exception{       
        final ReturnValue ret1 = workflowStatusChecker.init();
        Assert.assertTrue(ret1.getExitStatus() == ReturnValue.SUCCESS, "workflowStatusChecker could not init");
        mockupFakeRuns();
        
        when(options.has("threads-in-thread-pool")).thenReturn(true);
        when(options.valueOf("threads-in-thread-pool")).thenReturn(2);
        
        final ReturnValue ret2 = workflowStatusChecker.do_run();
        verifyNormalRun(ret2);
    }
    
    @Test 
    public void testManyThreadedRun() throws Exception{       
        final ReturnValue ret1 = workflowStatusChecker.init();
        Assert.assertTrue(ret1.getExitStatus() == ReturnValue.SUCCESS, "workflowStatusChecker could not init");
        mockupFakeRuns();
        
        when(options.has("threads-in-thread-pool")).thenReturn(true);
        when(options.valueOf("threads-in-thread-pool")).thenReturn(100);
        
        final ReturnValue ret2 = workflowStatusChecker.do_run();
        verifyNormalRun(ret2);
    }

    /**
     * For testing purposes, create some workflow runs and make our mocks aware of them
     * @throws Exception 
     */
    private void mockupFakeRuns() throws Exception {
        // mock up some fake workflow_runs so that their status can be checked
        List<WorkflowRun> wrList = new ArrayList<WorkflowRun>();
        for (int i = 0; i < 100; i++) {
            WorkflowRun wr = new WorkflowRun();
            wr.setOwnerUserName("user");
            wr.setWorkflowAccession(42);
            wr.setWorkflowRunId(42+i);
            wr.setCommand("dummyValue");
            wr.setTemplate("dummyValue");
            wr.setCurrentWorkingDir("dummyValue");
            wr.setDax("dummyValue");
            wr.setIniFile("dummyValue");
            wr.setWorkflowEngine("dummyValue");
            wr.setHost("localhost");
            wr.setStatusCmd("pegasus-status -l /home/seqware/pegasus-dax/seqware/pegasus/FastqQualityReportAndFilter_0.10.0/run00" + 42+i);
            wrList.add(wr);
        }
        PowerMockito.mockStatic(FileTools.class);
        when(FileTools.getLocalhost(options)).thenReturn(new LocalhostPair("localhost", new ReturnValue(ReturnValue.SUCCESS)));
        when(FileTools.isFileOwner(anyString())).thenReturn(true);
        final WorkflowTools workflowTools = mock(WorkflowTools.class);
        PowerMockito.whenNew(WorkflowTools.class).withAnyArguments().thenReturn(workflowTools);

        ReturnValue fakeReturn = new ReturnValue(ReturnValue.SUCCESS);
        fakeReturn.setAttribute("currStep", "1");
        fakeReturn.setAttribute("totalSteps", "1");
        
        when(workflowTools.watchWorkflow(anyString(), anyString(), anyInt())).thenReturn(fakeReturn);
        when(metadata.getWorkflowRunsByStatus(metadata.RUNNING)).thenReturn(wrList);
    }

    /**
     * Verify that the run returned normally and that the appropriate number of updates were make to the database
     * @param ret2 
     */
    private void verifyNormalRun(final ReturnValue ret2) {
        Assert.assertTrue(ret2.getExitStatus() == ReturnValue.SUCCESS, "workflowStatusChecker ran properly");
        verify(metadata).getWorkflowRunsByStatus(metadata.RUNNING);
        verify(metadata).getWorkflowRunsByStatus(metadata.PENDING);
        verify(metadata, times(100)).update_workflow_run(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString(), anyString());
    }
}
