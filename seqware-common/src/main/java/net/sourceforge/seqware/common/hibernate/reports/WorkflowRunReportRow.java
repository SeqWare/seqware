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
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunReportRow {
    private WorkflowRun workflowRun;
    private Collection<File> allInputFiles;
    private Collection<File> immediateInputFiles;
    private Collection<File> outputFiles;
    private Collection<Processing> workflowRunProcessings;
    private Collection<Processing> parentProcessings;
    private Collection<Sample> identitySamples;
    private Collection<Sample> librarySamples;
    private String timeTaken;

    /**
     * <p>Getter for the field <code>identitySamples</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Sample> getIdentitySamples() {
        return identitySamples;
    }

    /**
     * <p>Setter for the field <code>identitySamples</code>.</p>
     *
     * @param identitySamples a {@link java.util.Collection} object.
     */
    public void setIdentitySamples(Collection<Sample> identitySamples) {
        this.identitySamples = identitySamples;
    }

    /**
     * <p>Getter for the field <code>allInputFiles</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<File> getAllInputFiles() {
        return allInputFiles;
    }

    /**
     * <p>Setter for the field <code>allInputFiles</code>.</p>
     *
     * @param allInputFiles a {@link java.util.Collection} object.
     */
    public void setAllInputFiles(Collection<File> allInputFiles) {
        this.allInputFiles = allInputFiles;
    }

    /**
     * <p>Getter for the field <code>immediateInputFiles</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<File> getImmediateInputFiles() {
        return immediateInputFiles;
    }

    /**
     * <p>Setter for the field <code>immediateInputFiles</code>.</p>
     *
     * @param immediateInputFiles a {@link java.util.Collection} object.
     */
    public void setImmediateInputFiles(Collection<File> immediateInputFiles) {
        this.immediateInputFiles = immediateInputFiles;
    }

    /**
     * <p>Getter for the field <code>librarySamples</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Sample> getLibrarySamples() {
        return librarySamples;
    }

    /**
     * <p>Setter for the field <code>librarySamples</code>.</p>
     *
     * @param librarySamples a {@link java.util.Collection} object.
     */
    public void setLibrarySamples(Collection<Sample> librarySamples) {
        this.librarySamples = librarySamples;
    }

    /**
     * <p>Getter for the field <code>outputFiles</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<File> getOutputFiles() {
        return outputFiles;
    }

    /**
     * <p>Setter for the field <code>outputFiles</code>.</p>
     *
     * @param outputFiles a {@link java.util.Collection} object.
     */
    public void setOutputFiles(Collection<File> outputFiles) {
        this.outputFiles = outputFiles;
    }

    /**
     * <p>Getter for the field <code>parentProcessings</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Processing> getParentProcessings() {
        return parentProcessings;
    }

    /**
     * <p>Setter for the field <code>parentProcessings</code>.</p>
     *
     * @param parentProcessings a {@link java.util.Collection} object.
     */
    public void setParentProcessings(Collection<Processing> parentProcessings) {
        this.parentProcessings = parentProcessings;
    }

    /**
     * <p>Getter for the field <code>timeTaken</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTimeTaken() {
        return timeTaken;
    }

    /**
     * <p>Setter for the field <code>timeTaken</code>.</p>
     *
     * @param timeTaken a {@link java.lang.String} object.
     */
    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    /**
     * <p>Getter for the field <code>workflowRun</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    public WorkflowRun getWorkflowRun() {
        return workflowRun;
    }

    /**
     * <p>Setter for the field <code>workflowRun</code>.</p>
     *
     * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    public void setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
    }

    /**
     * <p>Getter for the field <code>workflowRunProcessings</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Processing> getWorkflowRunProcessings() {
        return workflowRunProcessings;
    }

    /**
     * <p>Setter for the field <code>workflowRunProcessings</code>.</p>
     *
     * @param workflowRunProcessings a {@link java.util.Collection} object.
     */
    public void setWorkflowRunProcessings(Collection<Processing> workflowRunProcessings) {
        this.workflowRunProcessings = workflowRunProcessings;
    }
    
    
    
    
}
