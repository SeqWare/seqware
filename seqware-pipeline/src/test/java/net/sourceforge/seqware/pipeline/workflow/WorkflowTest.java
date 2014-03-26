/*
 * Copyright (C) 2014 SeqWare
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

package net.sourceforge.seqware.pipeline.workflow;

import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import org.junit.Test;

/**
 *
 * @author dyuen
 */
public class WorkflowTest {
    
    @Test(expected=RuntimeException.class)
    public void testDuplicateFileNames(){
        TestingWorkflow testingWorkflow = new TestingWorkflow();
        testingWorkflow.buildWorkflow();
    }


    public class TestingWorkflow extends AbstractWorkflowDataModel{

        @Override
        public void buildWorkflow() {
            this.createFile("bam_file_in");
            this.createFile("bam_file_in");
        }
        
    }
}
