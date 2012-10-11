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
package net.sourceforge.seqware.common.hibernate.reports;

import java.util.Collection;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowRun;

/**
 * Contains the workflow's Processing events, the parent Processings, the input 
 * files, the output files, the identity and library samples, as well as the 
 * total running time.
 * @author mtaschuk
 */
public class WorkflowRunReportRow {
    private WorkflowRun workflowRun;
    private Collection<File> inputFiles;
    private Collection<File> outputFiles;
    private Collection<Processing> workflowRunProcessings;
    private Collection<Processing> parentProcessings;
    private Collection<Sample> identitySamples;
    private Collection<Sample> librarySamples;
    private String timeTaken;

    public Collection<Sample> getIdentitySamples() {
        return identitySamples;
    }

    public void setIdentitySamples(Collection<Sample> identitySamples) {
        this.identitySamples = identitySamples;
    }

    public Collection<File> getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(Collection<File> inputFiles) {
        this.inputFiles = inputFiles;
    }

    public Collection<Sample> getLibrarySamples() {
        return librarySamples;
    }

    public void setLibrarySamples(Collection<Sample> librarySamples) {
        this.librarySamples = librarySamples;
    }

    public Collection<File> getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(Collection<File> outputFiles) {
        this.outputFiles = outputFiles;
    }

    public Collection<Processing> getParentProcessings() {
        return parentProcessings;
    }

    public void setParentProcessings(Collection<Processing> parentProcessings) {
        this.parentProcessings = parentProcessings;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public WorkflowRun getWorkflowRun() {
        return workflowRun;
    }

    public void setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
    }

    public Collection<Processing> getWorkflowRunProcessings() {
        return workflowRunProcessings;
    }

    public void setWorkflowRunProcessings(Collection<Processing> workflowRunProcessings) {
        this.workflowRunProcessings = workflowRunProcessings;
    }
    
    
    
    
}
