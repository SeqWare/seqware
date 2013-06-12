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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author dyuen
 */
@PrepareForTest({WorkflowTools.class, FileTools.class, WorkflowStatusChecker.class})
@RunWith(PowerMockRunner.class)
public class SymLinkFileReporterTest{
   
    
    @Mock 
    private Map<String, String> config;
    
    @Mock
    private OptionSet options;
    
    @Mock
    private net.sourceforge.seqware.common.metadata.Metadata metadata;
    
    @InjectMocks
    private SymLinkFileReporter symLinkFileReporterChecker;
    
    @Before
    public void initMocks() throws Exception{
        reset(config, options, metadata);
        symLinkFileReporterChecker = new SymLinkFileReporter(); // this is kind of hacky
        // apparantly testNG retains the state of mocks and statuschecker from test to test, so we need to rebuild everything
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testShouldInjectMocks(){
        Assert.assertNotNull(metadata);
        Assert.assertNotNull(symLinkFileReporterChecker);
        Assert.assertNotNull(symLinkFileReporterChecker.getMetadata());
    }
    
    @Test 
    public void testEmptyLifeCycle(){
        final ReturnValue ret1 = symLinkFileReporterChecker.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret1.getExitStatus() == ReturnValue.SUCCESS);
        final ReturnValue ret2 = symLinkFileReporterChecker.do_test();
        Assert.assertTrue("workflowStatusChecker ran properly", ret2.getExitStatus() == ReturnValue.SUCCESS);
        final ReturnValue ret3 = symLinkFileReporterChecker.clean_up();
        Assert.assertTrue("workflowStatusChecker ran properly", ret3.getExitStatus() == ReturnValue.SUCCESS);       
        verifyNoMoreInteractions(metadata);
    }
}
