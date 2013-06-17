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

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import org.junit.Test;

/**
 *
 * @author dyuen
 */
public class UserPhase2 {
    
    public static final String EXPERIMENT = "experiment";
    
    @Test
    public void createExperimentAndLinkToStudy() throws IOException{
        String output = ITUtility.runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table experiment "
                + "--create --field title::New Test Experiment --field description::This is a test description --field study_accession::"+AccessionMap.accessionMap.get(UserPhase1.STUDY) +" --field platform_id::26", 
                ReturnValue.SUCCESS, null);
        String sw_accession  = UserTutorialSuiteIT.getAndCheckSwid(output);
        AccessionMap.accessionMap.put(EXPERIMENT, sw_accession);
    }
}
