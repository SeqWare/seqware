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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowTools;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author dyuen
 */
@PrepareForTest({WorkflowTools.class, FileTools.class, SymLinkFileReporter.class})
@RunWith(PowerMockRunner.class)
public class SymLinkFileReporterTest {

    @Mock
    private OptionSet options;
    @Mock
    private net.sourceforge.seqware.common.metadata.Metadata metadata;
    @InjectMocks
    private SymLinkFileReporter symLinkFileReporter;

    @Before
    public void initMocks() throws Exception {
        reset(options, metadata);
        symLinkFileReporter = new SymLinkFileReporter(); // this is kind of hacky
        // apparantly testNG retains the state of mocks and statuschecker from test to test, so we need to rebuild everything
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testEmptyLifeCycle() {
        final ReturnValue ret1 = symLinkFileReporter.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret1.getExitStatus() == ReturnValue.SUCCESS);
        final ReturnValue ret2 = symLinkFileReporter.do_test();
        Assert.assertTrue("workflowStatusChecker ran properly", ret2.getExitStatus() == ReturnValue.SUCCESS);
        final ReturnValue ret3 = symLinkFileReporter.clean_up();
        Assert.assertTrue("workflowStatusChecker ran properly", ret3.getExitStatus() == ReturnValue.SUCCESS);
        verifyNoMoreInteractions(metadata);
    }

    @Test
    public void testInvalidSyntax() {
        ReturnValue ret = symLinkFileReporter.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        ret = symLinkFileReporter.do_run();
        Assert.assertTrue("workflowStatusChecker did not return invalid syntax", ret.getExitStatus() == ReturnValue.INVALIDPARAMETERS);
        verifyNoMoreInteractions(metadata);
    }

    @Test
    public void testEmptyStudy() {
        ReturnValue ret = symLinkFileReporter.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        mockEmptyX("study");
        when(options.has("output-filename")).thenReturn(false);
        ret = symLinkFileReporter.do_run();
        Assert.assertTrue("workflowStatusChecker did not return invalid syntax", ret.getExitStatus() == ReturnValue.SUCCESS);
    }

    @Test
    public void testOutputFilename() {
        ReturnValue ret = symLinkFileReporter.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        mockEmptyX("study");
        when(options.has("output-filename")).thenReturn(true);
        String randomString = UUID.randomUUID().toString();
        when(options.valueOf("output-filename")).thenReturn(randomString);
        when(options.has("stdout")).thenReturn(false);
        ret = symLinkFileReporter.do_run();
        Assert.assertTrue("workflowStatusChecker did not return invalid syntax", ret.getExitStatus() == ReturnValue.SUCCESS);
        File file = new File(randomString + ".csv");
        Assert.assertTrue("output file was not created", file.exists());
    }

    @Test
    public void testStdOut() {
        ReturnValue ret = symLinkFileReporter.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        mockEmptyX("study");
        when(options.has("stdout")).thenReturn(true);
        ret = symLinkFileReporter.do_run();
        Assert.assertTrue("workflowStatusChecker did not return invalid syntax", ret.getExitStatus() == ReturnValue.SUCCESS);
    }

    @Test
    public void testEmptySample() {
        ReturnValue ret = symLinkFileReporter.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        mockEmptyX("sample");
        when(options.has("output-filename")).thenReturn(false);
        ret = symLinkFileReporter.do_run();
        Assert.assertTrue("workflowStatusChecker did not return invalid syntax", ret.getExitStatus() == ReturnValue.SUCCESS);
    }

    @Test
    public void testEmptySequencerRun() {
        ReturnValue ret = symLinkFileReporter.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        mockEmptyX("sequencer-run");
        when(options.has("output-filename")).thenReturn(false);
        ret = symLinkFileReporter.do_run();
        Assert.assertTrue("workflowStatusChecker did not return invalid syntax", ret.getExitStatus() == ReturnValue.SUCCESS);
    }

    @Test
    public void testDumpAll() {
        ReturnValue ret = symLinkFileReporter.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        mockEmptyX("dump-all");
        when(options.has("output-filename")).thenReturn(false);
        ret = symLinkFileReporter.do_run();
        Assert.assertTrue("workflowStatusChecker did not return invalid syntax", ret.getExitStatus() == ReturnValue.SUCCESS);
    }

    private void mockEmptyX(String x) {
        when(options.has(x)).thenReturn(true);
        when(options.valueOf(x)).thenReturn("Test" + x);
        if (x.equals("study")) {
            when(metadata.findFilesAssociatedWithAStudy("Test" + x)).thenReturn(new ArrayList<ReturnValue>());
        } else if (x.equals("sample")) {
            when(metadata.findFilesAssociatedWithASample("Test" + x)).thenReturn(new ArrayList<ReturnValue>());
        } else if (x.equals("sequencer-run")) {
            when(metadata.findFilesAssociatedWithASequencerRun("Test" + x)).thenReturn(new ArrayList<ReturnValue>());
        } else {
            Assert.assertTrue(x.equals("dump-all"));
            when(metadata.getAllStudies()).thenReturn(new ArrayList<Study>());
        }
        when(options.has("human")).thenReturn(false);
        when(options.has("duplicates")).thenReturn(true);
        when(options.has("show-failed-and-running")).thenReturn(true);
        when(options.has(SymLinkFileReporter.SHOW_STATUS)).thenReturn(true);
    }
    
    @Test
    public void testSimpleSample() {
        ReturnValue ret = symLinkFileReporter.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        mockSimpleStudyWithFiles();
        when(options.has("output-filename")).thenReturn(true);
        String randomString = UUID.randomUUID().toString();
        when(options.valueOf("output-filename")).thenReturn(randomString);
        when(options.has("stdout")).thenReturn(false);
        ret = symLinkFileReporter.do_run();
        Assert.assertTrue("workflowStatusChecker did not return invalid syntax", ret.getExitStatus() == ReturnValue.SUCCESS);
        File file = new File(randomString + ".csv");
        Assert.assertTrue("output file was not created", file.exists());
    }

    private void mockSimpleStudyWithFiles() {
        when(options.has("study")).thenReturn(true);
        when(options.valueOf("study")).thenReturn("TestStudy");
        List<ReturnValue> list = new ArrayList<ReturnValue>();
        for(int i = 0; i < 10; i++){
            ReturnValue ret = new ReturnValue();
            list.add(ret);
        }
        when(metadata.findFilesAssociatedWithAStudy("TestStudy")).thenReturn(list);
        when(options.has("human")).thenReturn(false);
        when(options.has("duplicates")).thenReturn(true);
        when(options.has("show-failed-and-running")).thenReturn(true);
        when(options.has(SymLinkFileReporter.SHOW_STATUS)).thenReturn(true);
    }
}
