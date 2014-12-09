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

import io.seqware.pipeline.plugins.WorkflowLauncher;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.tools.RunLock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author dyuen
 */
@PrepareForTest({ FileTools.class, WorkflowLauncher.class, RunLock.class })
@RunWith(PowerMockRunner.class)
public class WorkflowLauncherTest {

    @Mock
    private OptionSet options;
    @Mock
    private net.sourceforge.seqware.common.metadata.Metadata metadata;
    @InjectMocks
    private WorkflowLauncher workflowLauncher;

    @Before
    public void initMocks() throws Exception {
        reset(options, metadata);
        workflowLauncher = new WorkflowLauncher(); // this is kind of hacky
        // apparantly testNG retains the state of mocks and statuschecker from test to test, so we need to rebuild everything
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(RunLock.class);
    }

    @Test
    public void testEmptyLifeCycle() {
        final ReturnValue ret1 = workflowLauncher.init();
        Assert.assertTrue("workflowStatusChecker could not init", ret1.getExitStatus() == ReturnValue.SUCCESS);
        final ReturnValue ret2 = workflowLauncher.do_test();
        Assert.assertTrue("workflowStatusChecker ran properly", ret2.getExitStatus() == ReturnValue.SUCCESS);
        final ReturnValue ret3 = workflowLauncher.clean_up();
        Assert.assertTrue("workflowStatusChecker ran properly", ret3.getExitStatus() == ReturnValue.SUCCESS);
        verifyNoMoreInteractions(metadata);
    }
}
