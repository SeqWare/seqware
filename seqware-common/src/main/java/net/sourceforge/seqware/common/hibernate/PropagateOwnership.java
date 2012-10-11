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
package net.sourceforge.seqware.common.hibernate;

import java.util.*;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

/**
 * Should only be used for one query and then discarded and re-instantiated. The
 * Session needs to be open for it to work (InSessionExecution). The list of
 * files is returned as-is, so I make no guarantees on constancy.
 *
 * @author mtaschuk
 */
public class PropagateOwnership {

   private StringBuilder out = new StringBuilder();
    
    public PropagateOwnership() {
    }

    public void filesFromStudy(Study study) {
        for (Experiment e : study.getExperiments()) {
            for (Sample parentSample : e.getSamples()) {
                filesFromSample(parentSample, e, study);
            }
        }
    }

    private void filesFromSample(Sample parentSample, Experiment e, Study study) {
        Stack<Sample> sampleStack = new Stack<Sample>();
        sampleStack.add(parentSample);
        Set<Sample> usefulSamples = new TreeSet<Sample>();
        while (!sampleStack.isEmpty()) {
            Sample sample = sampleStack.pop();
            sampleStack.addAll(sample.getChildren());
            if (sample.getIUS().size() > 0) {
                usefulSamples.add(sample);
            }
        }

        for (Sample sample : usefulSamples) {
            SortedSet<IUS> iuses = sample.getIUS();
            for (IUS ius : iuses) {
                SequencerRun sr = ius.getLane().getSequencerRun();
                Set<Processing> currentProcessings = new TreeSet<Processing>();

                parseProcessingsFromStack(ius, currentProcessings);
                for (Processing processing : currentProcessings) {
                    setOwner(processing, study);
                }
            }
        }
    }

    private void parseProcessingsFromStack(IUS ius, Set<Processing> currentProcessings) {
        Stack<Processing> processingStack = new Stack<Processing>();
        processingStack.addAll(ius.getProcessings());
        Stack<Processing> parents = new Stack<Processing>();

        for (Processing p : ius.getProcessings()) {
            parents.addAll(p.getParents());
        }

        while (!processingStack.isEmpty()) {
            Processing processing = processingStack.pop();
            if (!currentProcessings.contains(processing)) {
                processingStack.addAll(processing.getChildren());
                currentProcessings.add(processing);
            }
        }

        while (!parents.isEmpty()) {
            Processing processing = parents.pop();
            if (!currentProcessings.contains(processing)) {
                parents.addAll(processing.getParents());
                currentProcessings.add(processing);
            }
        }

    }

    private void setOwner(Processing processing, Study study) {

        Registration studyOwner = study.getOwner();

        WorkflowRun workflowRun = processing.getWorkflowRun();
        if (workflowRun == null) {
            workflowRun = processing.getWorkflowRunByAncestorWorkflowRunId();
        }


        Registration processingOwner = processing.getOwner();
        Registration workflowRunOwner = workflowRun.getOwner();

        //check processing owner and set if necessary
        if (processingOwner != null && (processingOwner.equals(studyOwner))) {
            Log.stderr("Owner already set for processing SWID " + processing.getSwAccession());
            Log.stderr("\tPreviously set: " + processingOwner.getRegistrationId());
            Log.stderr("\tUnable to set: " + studyOwner.getRegistrationId());
        } else {
            processing.setOwner(study.getOwner());
        }

        //check workflow run owner and set if necessary
        if (workflowRunOwner != null && (workflowRunOwner.equals(studyOwner))) {
            Log.stderr("Owner already set for workflowRun SWID " + workflowRun.getSwAccession());
            Log.stderr("\tPreviously set: " + workflowRunOwner.getRegistrationId());
            Log.stderr("\tUnable to set: " + studyOwner.getRegistrationId());
        } else {
            workflowRun.setOwner(study.getOwner());
        }

        //check file owners and set if necessary
        if (processing.getFiles() != null) {
            for (File file : processing.getFiles()) {
                Registration fileOwner = file.getOwner();
                if (fileOwner != null && (fileOwner.equals(studyOwner))) {
                    Log.stderr("Owner already set for file SWID " + file.getSwAccession());
                    Log.stderr("\tPreviously set: " + fileOwner.getRegistrationId());
                    Log.stderr("\tUnable to set: " + studyOwner.getRegistrationId());
                } else {

                    file.setOwner(study.getOwner());
                }
            }
        }
    }
}
