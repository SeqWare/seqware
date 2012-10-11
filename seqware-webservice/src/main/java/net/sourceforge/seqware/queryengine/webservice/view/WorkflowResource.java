/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import net.sourceforge.seqware.queryengine.webservice.model.MetadataDB;
import net.sourceforge.seqware.queryengine.webservice.util.EnvUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;


import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.servlet.ServletContext;
import org.restlet.Request;

/**
 * @author boconnor
 * FIXME: this needs to be made generic so all workflow-specific cruft comes from the workflow_param and other tables!
 */
public class WorkflowResource extends ServerResource {

    //Get("text/html")
    @Get
    public Representation represent() {

        StringWriter sw = null;

        // logging
        this.getLogger().log(Level.INFO, "WorkflowResource Called");

        // TODO: move into a helper
        // get the ROOT URL for various uses
        //String rootURL = EnvUtil.getProperty("rooturl");
        String host = this.getRequest().getResourceRef().getHostIdentifier();
        String path = this.getRequest().getResourceRef().getPath();
        String[] pathArr = path.split("/");
        String rootURL = host + "/" + pathArr[1];


        // get the swid
        String swid = (String) getRequestAttributes().get("workflowId");

        // now build a model  
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

        // contains all the get params
        Form form = this.getRequest().getResourceRef().getQueryAsForm();

        // now pull back the workflowId so we can render
        MetadataDB metadataDB = new MetadataDB();
        data = metadataDB.getWorkflowParamMetadata(Integer.parseInt(swid));

        try {
            Configuration cfg = new Configuration();
            ServletContext context = (ServletContext) getContext().getServerDispatcher().getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
            // Specify the data source where the template files come from.
            // Here I set a file directory for it:
            cfg.setDirectoryForTemplateLoading(
                    new File(context.getRealPath("/WEB-INF/templates")));
            //new File("templates"));
            // Specify how templates will see the data-model. This is an advanced topic...
            // but just use this:
            cfg.setObjectWrapper(new DefaultObjectWrapper());

            // get template
            Template temp = cfg.getTemplate("workflow_params.ftl");

            // store the data
            HashMap root = new HashMap();
            root.put("data", data);
            root.put("rooturl", rootURL);
            root.put("workflowId", swid);

            sw = new StringWriter();

            temp.process(root, sw);

        } catch (Exception e) {
            StringRepresentation repOutput = new StringRepresentation(e.getMessage());
            repOutput.setMediaType(MediaType.TEXT_PLAIN);
            return (repOutput);
        }

        StringRepresentation repOutput = new StringRepresentation(sw.toString());
        repOutput.setMediaType(MediaType.TEXT_XML);
        return (repOutput);

    }

    @Post
    public Representation accept(Representation entity) throws Exception {
        Representation rep = null;
        if (entity != null) {
            if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {

                // get the ROOT URL for various uses
                //String rootURL = EnvUtil.getProperty("rooturl");

                // 1/ Create a factory for disk-based file items
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1000240);

                // 2/ Create a new file upload handler based on the Restlet
                // FileUpload extension that will parse Restlet requests and
                // generates FileItems.
                RestletFileUpload upload = new RestletFileUpload(factory);
                //ServletFileUpload upload = new ServletFileUpload(factory);

                List<FileItem> items;


                // 3/ Request is parsed by the handler which generates a
                // list of FileItems
                items = upload.parseRequest(getRequest());


                // get the swid
                String swid = (String) getRequestAttributes().get("workflowId");

                // get the ROOT URL for various uses
                String dataDir = EnvUtil.getProperty("datadir");

                // The Apache FileUpload project parses HTTP requests which
                // conform to RFC 1867, "Form-based File Upload in HTML". That
                // is, if an HTTP request is submitted using the POST method,
                // and with a content type of "multipart/form-data", then
                // FileUpload can parse that request, and get all uploaded files
                // as FileItem.



                // the other variables that have been posted (not files)
                Map<String, String> props = new HashMap<String, String>();

                // a hash that contains the field name mapped to the actual output file path
                HashMap<String, String> files = new HashMap<String, String>();

                // random number
                Random randomGenerator = new Random(new Date().getTime());

                // setup a directory to house this data
                String tempDir = dataDir + "/" + Math.abs(randomGenerator.nextInt());
                File tempDirObj = new File(tempDir);
                tempDirObj.mkdirs();
                tempDirObj.setWritable(true, false);
                tempDirObj.setExecutable(true, false);

                System.err.println("The file iterator: " + items.size());


                // iterate over all the files that have been uploaded
                for (final Iterator<FileItem> it = items.iterator(); it.hasNext();) {
                    FileItem fi = it.next();
                    String name = fi.getName();
                    System.err.println("The file name: " + name);
                    if (name == null) {
                        props.put(fi.getFieldName(), new String(fi.get(), "UTF-8"));
                    } else {
                        String fileName = tempDir + "/" + Math.abs(randomGenerator.nextInt()) + "." + fi.getName();
                        File file = new File(fileName);
                        files.put(fi.getFieldName(), fileName);
                        fi.write(file);
                    }
                }

                // at this point all the files have been written to the disk, the next step is to setup an ini file
                // with all the other fields so
                String iniFilePath = tempDir + "/" + Math.abs(randomGenerator.nextInt()) + ".workflow.ini";
                File iniFile = new File(iniFilePath);
                BufferedWriter out = new BufferedWriter(new FileWriter(iniFile));
                StringBuffer iniContents = new StringBuffer();
                for (String key : props.keySet()) {
                    out.write(key + "=" + props.get(key) + "\n");
                    iniContents.append(key + "=" + props.get(key) + "\n");
                }
                for (String key : files.keySet()) {
                    out.write(key + "=" + files.get(key) + "\n");
                    iniContents.append(key + "=" + files.get(key) + "\n");
                }
                // FIXME: now this is a hack specific to this workflow
                out.write("tmpdir=" + tempDir + "\n");
                iniContents.append("tmpdir=" + tempDir + "\n");
                out.write("outdir=" + tempDir + "\n");
                iniContents.append("outdir=" + tempDir + "\n");

                // now call the workflow tool 
                // FIXME: the workflow (and perhaps command) should be in the database so this command can be constructed
                // FIXME: hardcoded below
                MetadataDB metadataDB = new MetadataDB();
                int workflowAccession = metadataDB.getWorkflowAccession(Integer.parseInt(swid));
                String server = EnvUtil.getProperty("dbserver");
                String db = EnvUtil.getProperty("db");
                String user = EnvUtil.getProperty("user");
                String pass = EnvUtil.getProperty("pass");
                String command = "";
                // Can't run directly since the tomcat6 user isn't authorized to run workflows
                //RunTools.runCommand( new String[] { "bash", "-c", command } );
                // FIXME: hardcoded here
                int wrAccession = metadataDB.scheduleWorkflowRun(workflowAccession, "VariantAnnotationWorkflow", "/home/seqware/svnroot/seqware-complete/trunk/seqware-pipeline", iniContents.toString(), command, "/home/seqware/svnroot/seqware-complete/trunk/seqware-pipeline/workflows/HelloWorld.ftl", "hold");
                //command = "SEQWARE_DB_HOST="+server+" /home/seqware/svnroot/seqware-complete/trunk/seqware-pipeline/bin/pegasus-run.pl --seqware_meta_db_url=jdbc:postgresql://"+server+"/"+db+" --seqware_meta_db_username="+user+" --seqware_meta_db_password="+pass+" --workflow-accession="+workflowAccession+" --workflow-run-accession="+wrAccession+" /home/seqware/svnroot/seqware-complete/trunk/seqware-pipeline/config/site-specific/ec2/VariantAnnotation/VariantAnnotation_0.8.0.ini "+iniFilePath+" /home/seqware/svnroot/seqware-complete/trunk/seqware-pipeline/workflows/VariantAnnotation/VariantAnnotation_0.8.0.ftl";

                // loop to create workflow_run_params
                for (String key : props.keySet()) {
                    metadataDB.addWorkflowRunParam(wrAccession, key, props.get(key), "text");
                }
                for (String key : files.keySet()) {
                    metadataDB.addWorkflowRunParam(wrAccession, key, files.get(key), "file");
                }

                command = "perl bin/pegasus-run.pl ";

                // FIXME TESTING
                //command = "SEQWARE_DB_HOST="+server+" /home/seqware/svnroot/seqware-complete/trunk/seqware-pipeline/bin/pegasus-run.pl --seqware_meta_db_url=jdbc:postgresql://"+server+"/"+db+" --seqware_meta_db_username="+user+" --seqware_meta_db_password="+pass+" --workflow-run-accession="+wrAccession+" --input_file=/tmp/input --output_file=/tmp/output /home/seqware/svnroot/seqware-complete/trunk/seqware-pipeline/workflows/HelloWorld.ftl";
                out.write("workflow-accession=" + workflowAccession + "\n");
                iniContents.append("workflow-accession=" + workflowAccession + "\n");
                out.write("workflow_run_accession=" + wrAccession + "\n");
                iniContents.append("workflow_run_accession=" + wrAccession + "\n");
                out.close();
                metadataDB.updateWorkflowRun(wrAccession, "VariantAnnotationWorkflow", "/home/seqware/svnroot/seqware-complete/trunk/seqware-pipeline", iniContents.toString(), command, "/home/seqware/svnroot/seqware-complete/trunk/seqware-pipeline/workflows/VariantAnnotation/VariantAnnotation_0.8.0.ftl", "submitted");

                Request req = getRequest();
                String host = req.getResourceRef().getHostIdentifier();
                String path = req.getResourceRef().getPath();
                String[] pathArr = path.split("/");
                String rootURL = host + "/" + pathArr[1];

                if (wrAccession > 0) {
                    String result = "<?xml version=\"1.0\"?>"
                            + "<queryengine>"
                            + "  <asynchronous>"
                            + /* "    <workflows>"+
                            "        <workflow uri=\""+rootURL+"/queryengine/asynchronous/workflow/1\">"+ */ "          <workflow_run>"
                            + "              <status uri=\"" + rootURL + "/queryengine/asynchronous/workflow_run/status/" + wrAccession + "\""
                            + "              swid=\"" + wrAccession + "\" />"
                            + "          </workflow_run>"
                            + /* "        </workflow>"+
                            "    </workflows>"+ */ "  </asynchronous>"
                            + "</queryengine>";
                    rep = new StringRepresentation(result, MediaType.TEXT_XML);
                } else {
                    // Some problem occurs, sent back a simple line of text.
                    rep = new StringRepresentation("NOK", MediaType.TEXT_PLAIN);
                }
            }
        } else {
            // POST request with no entity.
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }

        return rep;
    }
}
