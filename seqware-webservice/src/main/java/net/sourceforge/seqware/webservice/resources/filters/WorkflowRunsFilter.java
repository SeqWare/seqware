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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;
import java.util.logging.Level;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import org.restlet.data.Status;
import org.restlet.representation.ReadableRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;

/**
 * We presume there is either a runId or a testId, not both.
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunsFilter extends BasicResource {

    private String workflowId;

    /** {@inheritDoc} */
    @Override
    public void doInit() {
        workflowId = (String) getRequestAttributes().get("workflowId");
    }

    /** {@inheritDoc} */
    @Override
    protected Representation get() {
        String path = this.getRequest().getResourceRef().getPath();
        StringRepresentation rep;
        if (path.contains("runs")) {
            rep = new StringRepresentation("GET all runs from " + workflowId);
        } else {
            rep = new StringRepresentation("GET all tests from " + workflowId);
        }
        return rep;
    }

    /** {@inheritDoc} */
    @Override
    protected Representation post(Representation entity) {
        authenticate();
        String path = this.getRequest().getResourceRef().getPath();
        try {
            if (path.contains("tests")) {
                return postTest(entity);
            } else {
                Representation rep = new StringRepresentation("Have to post an INI file. POST run " + entity.getText());
                return rep;
            }
        } catch (IOException e) {
            e.printStackTrace();
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        return null;
    }

    /**
     * <p>postRun.</p>
     *
     * @param entity a {@link org.restlet.representation.Representation} object.
     * @return a {@link org.restlet.representation.Representation} object.
     * @throws java.io.IOException if any.
     * @throws java.lang.Exception if any.
     */
    @Post("txt")
    public Representation postRun(Representation entity) throws IOException, Exception {
        authenticate();
        this.getLogger().log(Level.INFO, "Posting an INI - Running workflow");

        String filename = saveFile(entity);

        WorkflowService ws = BeanFactory.getWorkflowServiceBean();

        Workflow workflow = ws.findByID(parseClientInt(workflowId));

        String workflowName = workflow.getName().replace(' ', '_');
        String workflowPath = workflow.getCwd();
        String workflowVersion = workflow.getVersion();

        runPluginRunner(new String[]{"--plugin", "net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher",
                    "--", "no-metadata", "--unzipped-bundle", workflowPath, "--workflow",
                    workflowName, "--workflow-version", workflowVersion, "--ini-files", filename}, filename);

        Representation rep = new StringRepresentation("POST run " + entity.getText());
        return rep;
    }

    private Representation postTest(Representation entity) throws FileNotFoundException, IOException {
        WorkflowService ws = BeanFactory.getWorkflowServiceBean();

        Workflow workflow = ws.findByID(parseClientInt(workflowId));

        String workflowPath = workflow.getCwd();

        String[] args = new String[]{"--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager",
            "--", "--unzipped-bundle", workflowPath, "--test"};

        runPluginRunner(args, "workflowtest" + workflowId);
        Representation rep = new StringRepresentation("POST test " + entity.getText());
        return rep;
    }

    private String saveFile(Representation entity) throws FileNotFoundException, IOException {
        ReadableRepresentation fr = (ReadableRepresentation) entity;
        String filename = "/tmp/" + (new Random()).nextInt(Integer.MAX_VALUE) + ".ini";
        FileOutputStream out = new FileOutputStream(filename);
        fr.write(out);
        out.close();
        return filename;
    }

    private String runPluginRunner(String[] args, String filename) throws FileNotFoundException, IOException {
        String errorFile = filename + "-errors.txt";

        FileOutputStream fos = new FileOutputStream(errorFile);
        PrintStream old = System.err;
        PrintStream ps = new PrintStream(fos);
        System.setErr(ps);
        new PluginRunner().run(args);
        System.setErr(old);
        fos.close();

        BufferedReader reader = new BufferedReader(new FileReader(errorFile));
        String line = reader.readLine();
        while (line != null) {
            if (line.contains("Exception")) {
                getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, line);
            }
            System.err.println(line);
            line = reader.readLine();
        }
        reader.close();
        return errorFile;
    }
}
