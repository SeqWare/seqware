/*
 * Copyright (C) 2012 SeqWare
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

import java.util.Arrays;
import net.sourceforge.seqware.common.metadata.MetadataWSTest;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 *
 * @author mtaschuk
 */
public class PluginTest {

    protected static net.sourceforge.seqware.common.metadata.Metadata metadata;
    protected Plugin instance;

    public PluginTest() {
    }

    protected void checkReturnValue(int expected, ReturnValue rv) {
        Assert.assertEquals("Plugin did not exit successfully.", expected, rv.getExitStatus());
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////
    protected void launchPlugin(String... params) {
        instance.setParams(Arrays.asList(params));
        instance.setConfig(ConfigTools.getSettings());
        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
        checkReturnValue(ReturnValue.SUCCESS, instance.init());
        checkReturnValue(ReturnValue.SUCCESS, instance.do_run());
    }

    @Before
    public void setUp() {
        metadata = MetadataWSTest.newTestMetadataInstance();
    }

    @After
    public void tearDown() {
        metadata.clean_up();
    }
}
