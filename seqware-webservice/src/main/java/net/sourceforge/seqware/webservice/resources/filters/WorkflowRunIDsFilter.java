/*
 * Copyright (C) 2011 SeqWare
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


import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

/**
 * We presume there is either a runId or a testId, not both.
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunIDsFilter extends ServerResource {

    private String workflowId;
    private String testId;
    private String runId;
    private boolean isTest, isRun;

    /** {@inheritDoc} */
    @Override
    public void doInit() {
        workflowId = (String) getRequestAttributes().get("workflowId");
        testId = (String) getRequestAttributes().get("testId");
        runId = (String) getRequestAttributes().get("runId");
        isTest = (testId != null);
        isRun = (runId != null);
        System.out.println("isTest " + isTest + " isRun " + isRun);
        System.out.println("Test...: Setting up tests");


    }

    /** {@inheritDoc} */
    @Override
    protected Representation delete() {
        if (isTest) {
            StringRepresentation rep = new StringRepresentation("DELETE isTest " + testId);
            return rep;
        } else {//isRun
            StringRepresentation rep = new StringRepresentation("DELETE isRun " + runId);
            return rep;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected Representation get() {
        if (isTest) {
            StringRepresentation rep = new StringRepresentation("GET isTest " + testId);
            return rep;
        } else {//isRun
            StringRepresentation rep = new StringRepresentation("GET isRun " + runId);
            return rep;
        }
    }
}
