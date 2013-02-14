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
package net.sourceforge.seqware.webservice.resources.filters;

import java.util.List;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.lists.ExperimentList;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.AbstractResourceTest;
import net.sourceforge.seqware.webservice.resources.ClientResourceInstance;
import org.junit.*;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

/**
 *
 * @author mtaschuk
 */
public class ExperimentIDFilterTest extends AbstractResourceTest {

    String studyAcc;

    public ExperimentIDFilterTest() {
        super("");
        studyAcc = "120";
    }

    @Override
    public void testGet() {
    }

    @Test
    public void testGetFromStudy() throws Exception {
        List<Experiment> experiments = getExperiments("/studies/120/experiments");
        Assert.assertFalse(experiments.isEmpty());
    }

    @Test
    public void testGetFromStudyNotFound() throws Exception {
        try {
            getExperiments("/studies/1200/experiments");
            Assert.fail("This study should not exist, should not have experiments, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    @Test
    public void testGetFromStudyNoExperiments() throws Exception {
        try {
            getExperiments("/studies/6052/experiments");
            Assert.fail("This study should not have experiments, and should show a 404 Not Found");
        } catch (ResourceException ex) {
            if (!ex.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                throw ex;
            }
        }
    }

    private List<Experiment> getExperiments(String relativeURI) throws ResourceException, Exception {
        resource = ClientResourceInstance.getChild(relativeURI);
        Log.stdout(getRelativeURI() + " GET");
        ExperimentList parent = new ExperimentList();
        JaxbObject<ExperimentList> jaxb = new JaxbObject<ExperimentList>();
        try {
            Representation rep = resource.get();
            String text = rep.getText();
            parent = (ExperimentList) XmlTools.unMarshal(jaxb, parent, text);
            rep.exhaust();
            rep.release();
        } catch (ResourceException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return parent.getList();
    }

    @Override
    public void testPut() {
    }

    @Override
    public void testPost() {
    }

    @Override
    public void testDelete() {
    }
}
