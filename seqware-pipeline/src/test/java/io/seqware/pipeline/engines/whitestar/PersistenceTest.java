/*
 * Copyright (C) 2015 SeqWare
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
package io.seqware.pipeline.engines.whitestar;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.SortedSet;
import net.sourceforge.seqware.common.model.WorkflowRun;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author dyuen
 */
public class PersistenceTest {

    private final Path path;

    public PersistenceTest() {
        this.path = FileSystems.getDefault().getPath("src/test/resources/io/seqware/pipeline/engines");
    }

    /**
     * Test of readCompletedJobs method, of class Persistence.
     */
    @Test
    public void testReadCompletedJobs() {
        Persistence persistence = new Persistence(path.toFile());
        SortedSet<String> result = persistence.readCompletedJobs();
        assertEquals(result.size(), 7);
    }

    /**
     * Test of readWorkflowRun method, of class Persistence.
     */
    @Test
    public void testReadWorkflowRun() {
        Persistence persistence = new Persistence(path.toFile());
        WorkflowRun run = persistence.readWorkflowRun();
        assertTrue("workflow is not attached", run.getWorkflow() != null);
        assertTrue("workflow run sw_accession incorrect", run.getWorkflowRunId() == 10);
        assertTrue("workflow sw_accession incorrect", run.getWorkflow().getSwAccession() == 1);
    }

}
