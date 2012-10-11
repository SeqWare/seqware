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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.*;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 *
 * @author mtaschuk
 */
public class CheckForCycles {

    private StringBuilder results = new StringBuilder();

    public String checkStudy(Integer studySwa) {
            StudyService ss = BeanFactory.getStudyServiceBean();
            Study study = ss.findBySWAccession(studySwa);
            
            if (study==null)
            {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "The study SWID does not exist "+studySwa);
            }
            
            results.append("Study Name: ").append(study.getTitle()).append(" SWA: ").append(study.getSwAccession()).append("\n");
            Set<Sample> samples = new TreeSet<Sample>();
            results.append("Number of experiments: ").append(study.getExperiments().size()).append("\n");
            for (Experiment exp : study.getExperiments()) {
                results.append("Experiment: ").append(exp.getName()).append(" SWID: ").append(exp.getSwAccession()).append("\n");
                for (Sample s : exp.getSamples()) {
                    samples.addAll(checkSample(s));
                }
            }

            results.append("Number of unique samples: ").append(samples.size()).append("\n");
            for (Sample sample : samples) {
                results.append("Sample: ").append(sample.getName()).append(" SWID: ").append(sample.getSwAccession()).append("\n");
                for (IUS ius : sample.getIUS()) {
                    results.append("IUS: ").append(ius.getSwAccession()).append("\n");
                    for (Processing processing : ius.getLane().getAllProcessings()) {
                        results.append("Processing from lane: ").append(processing.getAlgorithm()).append(" SWID: ").append(processing.getSwAccession()).append("\n");
                        checkProcessing(processing);
                    }
                    for (Processing processing : ius.getProcessings()) {
                        results.append("Processing from IUS: ").append(processing.getAlgorithm()).append(" SWID: ").append(processing.getSwAccession()).append("\n");
                        checkProcessing(processing);
                    }
                }
            }
       
        return results.toString();

    }

    public Set<Sample> checkSample(Sample sample) {
        Set<Sample> samples = new TreeSet<Sample>();
        List<Sample> samplePath = new ArrayList<Sample>();
        samples.add(sample);
        samplePath.add(sample);
        recurseSample(sample, samplePath, samples);
        return samples;
    }

    private void recurseSample(Sample sample, List<Sample> samplePath, Set<Sample> samples) {
        for (Sample s1 : sample.getChildren()) {
            List<Sample> path = new ArrayList<Sample>(samplePath);
            boolean exists = path.contains(s1);
            path.add(s1);
            if (exists) {
                StringBuilder string = new StringBuilder();
                string.append("Sample cycle found! ");
                for (Sample s : samplePath) {
                    string.append(s.getSampleId()).append(":").append(s.getName()).append("->");
                }
                results.append(string.toString()).append("\n");
            } else {
                samples.add(s1);
                recurseSample(s1, path, samples);
            }
        }
    }

    public void checkProcessing(Processing processing) {
        List<Processing> processingPath = new ArrayList<Processing>();
        processingPath.add(processing);
        recurseProcessing(processing, processingPath);

    }

    private void recurseProcessing(Processing processing, List<Processing> processingPath) {
        for (Processing s1 : processing.getChildren()) {
            List<Processing> path = new ArrayList<Processing>(processingPath);
            boolean exists = path.contains(s1);
            path.add(s1);
            if (exists) {
                StringBuilder string = new StringBuilder();
                string.append("Processing cycle found! ");
                for (Processing s : processingPath) {
                    string.append(s.getProcessingId()).append(":").append(s.getAlgorithm()).append("->");
                }
                results.append(string.toString()).append("\n");
            } else {
                recurseProcessing(s1, path);
            }
        }
    }
}
