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
package net.sourceforge.seqware.pipeline.runner;

import io.seqware.Reports;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ExtendedPluginTest;
import net.sourceforge.seqware.pipeline.plugins.ModuleRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Runs tests for the ModuleRunner class.
 *
 * @author dyuen
 */
public class ModuleRunnerTest extends ExtendedPluginTest {

    @BeforeClass
    public static void beforeClass() {
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
        Reports.triggerProvenanceReport();
    }

    @Before
    @Override
    public void setUp() {
        instance = new ModuleRunner();
        super.setUp();
    }

    public ModuleRunnerTest() {
    }

    @Test
    public void testNormalRun() throws IOException {
        System.out.println("testNormalRun");
        Path parentAccession = Files.createTempFile("accession", "test");
        FileUtils.write(parentAccession.toFile(), "836");
        launchPlugin("--metadata --metadata-parent-accession-file " + parentAccession.toAbsolutePath()
                + " --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner -- --gcr-algorithm bash_cp --gcr-command echo");
    }

    @Test
    public void testBlankParentAccessionFileFail() throws IOException {
        System.out.println("testBlankParentAccessionFile");
        Path parentAccession = Files.createTempFile("accession", "test");
        launchPlugin("--metadata --metadata-parent-accession-file " + parentAccession.toAbsolutePath()
                + " --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner -- --gcr-algorithm bash_cp --gcr-command echo");
    }

    @Test
    public void testInvalidAccessionFileFail() throws IOException {
        System.out.println("testInvalidAccessionFile");
        Path parentAccession = Files.createTempFile("accession", "test");
        FileUtils.write(parentAccession.toFile(), "-1");
        launchPlugin("--metadata --metadata-parent-accession-file " + parentAccession.toAbsolutePath()
                + " --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner -- --gcr-algorithm bash_cp --gcr-command echo");
    }

}
