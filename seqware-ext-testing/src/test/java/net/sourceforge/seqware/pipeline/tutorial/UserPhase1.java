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
package net.sourceforge.seqware.pipeline.tutorial;

import java.io.IOException;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import org.junit.Test;

/**
 * Do all tests that can be concurrently done in the user tutorial
 *
 * @author dyuen
 */
public class UserPhase1 {

    public static final String STUDY = "study";
    
    @Test
    public void testMetadataListTables() throws IOException {
        String output = runListTables();
        Assert.assertTrue("output should include table names", output.contains("TableName") && output.contains("study") && output.contains("experiment"));
        Assert.assertTrue("output should include table names", output.contains("sample") && output.contains("sequencer_run") && output.contains("ius") && output.contains("lane"));
    }

    @Test
    public void testMetadataListFields() throws IOException {
        String output = runListFields();
        Assert.assertTrue("output should include column names", output.contains("Field\tType\tPossible_Values"));
        Assert.assertTrue("output should include field names", output.contains("title\tString"));
        Assert.assertTrue("output should include field names", output.contains("study_type\tInteger"));
    }

    @Test
    public void testStudyCreation() throws IOException {
        String output = runStudyCreation();
        String sw_accession = OldUserTutorialSuiteET.getAndCheckSwid(output);
        AccessionMap.accessionMap.put(STUDY, sw_accession);
    }

    protected String runListTables() throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --list-tables", ReturnValue.SUCCESS, null);
        return output;
    }

    protected String runListFields() throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --list-fields", ReturnValue.SUCCESS, null);
        return output;
    }

    protected String runStudyCreation() throws IOException {
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --create --field title::New Test Study --field description::This is a test description --field accession::InternalID123 --field center_name::SeqWare --field center_project_name::SeqWare Test Project --field study_type::4", ReturnValue.SUCCESS, null);
        return output;
    }
}
