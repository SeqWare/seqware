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
package net.sourceforge.seqware.webservice.resources.tables;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.queryengine.webservice.controller.SeqWareWebServiceApplication;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>WorkflowIDResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowIDResource extends DatabaseIDResource {

    /**
     * <p>Constructor for WorkflowIDResource.</p>
     */
    public WorkflowIDResource() {
        super("workflowId");
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareWebServiceApplication getApplication() {
        return (SeqWareWebServiceApplication) super.getApplication();
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        authenticate();
        WorkflowService ss = BeanFactory.getWorkflowServiceBean();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<Workflow> jaxbTool = new JaxbObject<Workflow>();
        Workflow workflow = (Workflow) testIfNull(ss.findBySWAccession(getId()));
        Workflow dto = copier.hibernate2dto(Workflow.class, workflow);

        if (fields.contains("params")) {
            SortedSet<WorkflowParam> wps = workflow.getWorkflowParams();
            if (wps != null) {
                SortedSet<WorkflowParam> copiedParams = new TreeSet<WorkflowParam>();
                for (WorkflowParam param : workflow.getWorkflowParams()) {
                    copiedParams.add(copier.hibernate2dto(WorkflowParam.class, param));
                }
                dto.setWorkflowParams(copiedParams);
            } else {
                Log.info("Could not be found: workflow params");
            }
        }
		if (fields.contains("attributes")) {
			Set<WorkflowAttribute> was = workflow.getWorkflowAttributes();
			if(was!=null && !was.isEmpty()) {
				Set<WorkflowAttribute> newwas = new TreeSet<WorkflowAttribute>();
				for(WorkflowAttribute wa: was) {
					newwas.add(copier.hibernate2dto(WorkflowAttribute.class,wa));
				}
				dto.setWorkflowAttributes(newwas);
			}
		}

        Document line = XmlTools.marshalToDocument(jaxbTool, dto);
        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

    /** {@inheritDoc} */
    @Override
    public Representation put(Representation entity) {
        authenticate();
        Representation representation = null;
        Workflow newWorkflow = null;
        JaxbObject<Workflow> jo = new JaxbObject<Workflow>();
        try {
            String text = entity.getText();
            newWorkflow = (Workflow) XmlTools.unMarshal(jo, new Workflow(), text);
        } catch (SAXException ex) {
            ex.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
        try {
            WorkflowService fs = BeanFactory.getWorkflowServiceBean();
            Workflow workflow = (Workflow) testIfNull(fs.findByID(newWorkflow.getWorkflowId()));
            workflow.givesPermission(registration);
            //simple types
            String name = newWorkflow.getName();
            String desc = newWorkflow.getDescription();
            String baseIniFile = newWorkflow.getBaseIniFile();
            String command = newWorkflow.getCommand();
            String cwd = newWorkflow.getCwd();
            String host = newWorkflow.getHost();
            String inputAlgorithm = newWorkflow.getInputAlgorithm();
            String permanentBundleLocation = newWorkflow.getPermanentBundleLocation();
            String seqwareVersion = newWorkflow.getSeqwareVersion();
            String template = newWorkflow.getTemplate();
            String username = newWorkflow.getUsername();
            String version = newWorkflow.getVersion();
            //foreign keys
            Registration owner = newWorkflow.getOwner();

            workflow.setName(name);
            workflow.setDescription(desc);
            workflow.setBaseIniFile(baseIniFile);
            workflow.setCommand(command);
            workflow.setCwd(cwd);
            workflow.setHost(host);
            workflow.setInputAlgorithm(inputAlgorithm);
            workflow.setPermanentBundleLocation(permanentBundleLocation);
            workflow.setSeqwareVersion(seqwareVersion);
            workflow.setTemplate(template);
            workflow.setUsername(username);
            workflow.setVersion(version);
            workflow.setUpdateTimestamp(new Date());
            workflow.setWorkflowClass(newWorkflow.getWorkflowClass());
            workflow.setWorkflowType(newWorkflow.getWorkflowType());
            workflow.setWorkflowEngine(newWorkflow.getWorkflowEngine());

            if (owner != null) {
                RegistrationService rs = BeanFactory.getRegistrationServiceBean();
                Registration newReg = rs.findByEmailAddress(owner.getEmailAddress());
                if (newReg != null) {
                    workflow.setOwner(newReg);
                } else {
                    Log.info("Could not be found: " + owner);
                }
            } else if (workflow.getOwner() == null) {
                workflow.setOwner(registration);
            }

            if(newWorkflow.getWorkflowAttributes()!=null) {
              //SEQWARE-1577 - AttributeAnnotator cascades deletes when annotating
              this.mergeAttributes(workflow.getWorkflowAttributes(), newWorkflow.getWorkflowAttributes(), workflow);
            }
            fs.update(registration, workflow);
            Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
            Workflow detachedWorkflow = copier.hibernate2dto(Workflow.class, workflow);

            Document line = XmlTools.marshalToDocument(jo, detachedWorkflow);
            representation = XmlTools.getRepresentation(line);
            getResponse().setEntity(representation);
            getResponse().setLocationRef(getRequest().getRootRef() + "/workflows/" + detachedWorkflow.getSwAccession());
            getResponse().setStatus(Status.SUCCESS_CREATED);
        } catch (SecurityException e) {
            getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        }

        return representation;
    }
}
