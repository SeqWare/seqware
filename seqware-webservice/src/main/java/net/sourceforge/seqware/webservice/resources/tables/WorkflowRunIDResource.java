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
import java.sql.SQLException;
import java.util.*;
import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.apache.commons.lang.ArrayUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>WorkflowRunIDResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunIDResource extends DatabaseIDResource {

    /**
     * <p>Constructor for WorkflowRunIDResource.</p>
     */
    public WorkflowRunIDResource() {
        super("workflowRunId");

    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        authenticate();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject<WorkflowRun> jaxbTool = new JaxbObject<WorkflowRun>();
        WorkflowRunService ss = BeanFactory.getWorkflowRunServiceBean();

        WorkflowRun workflowRun = getWorkflowRun(ss);
        // specify that we want the input file set to be copied along, if this works, we should clean up the manual copying below
        CollectionPropertyName<WorkflowRun>[] createCollectionPropertyNames = CollectionPropertyName.createCollectionPropertyNames(WorkflowRun.class, new String[]{"inputFileAccessions"});
        WorkflowRun dto = copier.hibernate2dto(WorkflowRun.class, workflowRun, ArrayUtils.EMPTY_CLASS_ARRAY, createCollectionPropertyNames);
        //Log.debug("getXML() Workflow run contains " + workflowRun.getInputFileAccessions().size()  + " input files");
        //dto.setInputFileAccessions(workflowRun.getInputFileAccessions());

        if (fields.contains("lanes")) {

            SortedSet<Lane> lanes = workflowRun.getLanes();
            if (lanes != null) {
                SortedSet<Lane> copiedLanes = new TreeSet<Lane>();
                for (Lane lane : lanes) {
                    copiedLanes.add(copier.hibernate2dto(Lane.class, lane));
                }
                dto.setLanes(copiedLanes);
            } else {
                Log.info("Could not be found: lanes");
            }
        }

        if (fields.contains("ius")) {
            SortedSet<IUS> iuses = workflowRun.getIus();
            if (iuses != null) {
                SortedSet<IUS> copiedIUS = new TreeSet<IUS>();
                for (IUS i : iuses) {
                    copiedIUS.add(copier.hibernate2dto(IUS.class, i));
                }
                dto.setIus(copiedIUS);
            } else {
                Log.info("Could not be found: ius");
            }
        }

        if (fields.contains("processes")) {
            SortedSet<Processing> procs = workflowRun.getProcessings();
            if (procs != null) {
                SortedSet<Processing> copiedPs = new TreeSet<Processing>();
                for (Processing p : procs) {
                    copiedPs.add(copier.hibernate2dto(Processing.class, p));
                }
                dto.setProcessings(copiedPs);
            } else {
                Log.info("Could not be found: processings");
            }
            procs = workflowRun.getOffspringProcessings();
            if (procs != null) {
                SortedSet<Processing> copiedPs = new TreeSet<Processing>();
                for (Processing p : procs) {
                    copiedPs.add(copier.hibernate2dto(Processing.class, p));
                }
                dto.setOffspringProcessings(copiedPs);
            } else {
                Log.info("Could not be found: offspring processings");
            }
        }
		if (fields.contains("attributes")) {
			Set<WorkflowRunAttribute> wras = workflowRun.getWorkflowRunAttributes();
			if(wras!=null && !wras.isEmpty()) {
				Set<WorkflowRunAttribute> newwras = new TreeSet<WorkflowRunAttribute>();
				for(WorkflowRunAttribute wra: wras) {
					newwras.add(copier.hibernate2dto(WorkflowRunAttribute.class, wra));
				}
				dto.setWorkflowRunAttributes(newwras);
			}
		}

        Document line = XmlTools.marshalToDocument(jaxbTool, dto);
        getResponse().setEntity(XmlTools.getRepresentation(line));


    }

    /**
     * <p>updateWorkflowRun.</p>
     *
     * @param newWR a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     * @throws org.restlet.resource.ResourceException if any.
     * @throws java.sql.SQLException if any.
     */
    public WorkflowRun updateWorkflowRun(WorkflowRun newWR) throws ResourceException, SQLException {
        authenticate();
        WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
        WorkflowRun wr = (WorkflowRun) testIfNull(wrs.findBySWAccession(newWR.getSwAccession()));
        wr.givesPermission(registration);
        //ius_workflow_runs
        if (newWR.getIus() != null) {
            SortedSet<IUS> iuses = newWR.getIus();
            if (iuses != null) {
                SortedSet<IUS> set = new TreeSet<IUS>();
                for (IUS ius : iuses) {
                    IUSService is = BeanFactory.getIUSServiceBean();
                    IUS newI = is.findBySWAccession(ius.getSwAccession());
                    newI.givesPermission(registration);
                    set.add(newI);
                }
                wr.setIus(set);
            } else {
                Log.info("Could not be found: iuses");
            }
        }
        //lane_workflow_runs
        if (newWR.getLanes() != null) {
            SortedSet<Lane> lanes = newWR.getLanes();
            if (lanes != null) {
                SortedSet<Lane> set = new TreeSet<Lane>();
                for (Lane lane : lanes) {
                    LaneService ls = BeanFactory.getLaneServiceBean();
                    Lane newL = ls.findBySWAccession(lane.getSwAccession());
                    newL.givesPermission(registration);
                    set.add(newL);
                }
                wr.setLanes(set);
            } else {
                Log.info("Could not be found: lanes");
            }
        }


        wr.setCommand(newWR.getCommand());
        wr.setCurrentWorkingDir(newWR.getCurrentWorkingDir());
        wr.setDax(newWR.getDax());
        wr.setHost(newWR.getHost());
        wr.setIniFile(newWR.getIniFile());
        wr.setName(newWR.getName());
        wr.setStatus(newWR.getStatus());
        wr.setStatusCmd(newWR.getStatusCmd());
        wr.setTemplate(newWR.getTemplate());
        wr.setSeqwareRevision(newWR.getSeqwareRevision());
        wr.setUserName(newWR.getUserName());
        wr.setUpdateTimestamp(new Date());
        wr.setStdErr(newWR.getStdErr());
        wr.setStdOut(newWR.getStdOut());
        wr.setWorkflowEngine(newWR.getWorkflowEngine());
        if (newWR.getInputFileAccessions() != null){
            Log.debug("Saving " + wr.getInputFileAccessions().size() + " input files");
            wr.getInputFileAccessions().clear();
            wr.getInputFileAccessions().addAll(newWR.getInputFileAccessions());
        }
        
        if (newWR.getWorkflow() != null) {
            WorkflowService ws = BeanFactory.getWorkflowServiceBean();
            Workflow w = ws.findByID(newWR.getWorkflow().getWorkflowId());
            if (w != null) {
                wr.setWorkflow(w);
            } else {
                Log.info("Could not be found: workflow "+newWR.getWorkflow());
            }
        }
        if (newWR.getOwner() != null) {
            Registration reg = BeanFactory.getRegistrationServiceBean().findByEmailAddress(newWR.getOwner().getEmailAddress());
            if (reg != null) {
                wr.setOwner(reg);
            } else {
                Log.info("Could not be found: "+newWR.getOwner());
            }
        } else if (wr.getOwner() == null) {
            wr.setOwner(registration);
        }
        
        if (newWR.getWorkflowRunAttributes() != null) {
            this.mergeAttributes(wr.getWorkflowRunAttributes(), newWR.getWorkflowRunAttributes(), wr);
        }
        wrs.update(registration, wr);

        //direct DB calls
        if (newWR.getIus() != null) {
            addNewIUSes(newWR, wr);
        }
        if (newWR.getLanes() != null) {
            addNewLanes(newWR, wr);
        }

        return wr;

    }

    private WorkflowRun getWorkflowRun(WorkflowRunService ss) throws NumberFormatException {
        WorkflowRun workflowRun = (WorkflowRun) testIfNull(ss.findBySWAccession(getId()));
        return workflowRun;
    }

    /** {@inheritDoc} */
    @Override
    public Representation put(Representation entity) {
        Representation toreturn = null;
        if (entity.getMediaType().equals(MediaType.APPLICATION_XML)) {
            WorkflowRun newWR = null;

            JaxbObject<WorkflowRun> jo = new JaxbObject<WorkflowRun>();
            try {
                String text = entity.getText();
                Log.debug(text);
                newWR = (WorkflowRun) XmlTools.unMarshal(jo, new WorkflowRun(), text);
            } catch (SAXException ex) {
                ex.printStackTrace();
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex);
            }catch (IOException e) {
                e.printStackTrace();
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
            }
            try {


                WorkflowRun wr = updateWorkflowRun(newWR);

                Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
                Document line = XmlTools.marshalToDocument(jo, copier.hibernate2dto(wr));
                toreturn = XmlTools.getRepresentation(line);
                getResponse().setEntity(toreturn);
                getResponse().setLocationRef(getRequest().getRootRef() + "/workflowruns/" + newWR.getSwAccession());
                getResponse().setStatus(Status.SUCCESS_CREATED);

            } catch (SecurityException e) {
                getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex);
            } catch (Exception e) {
                e.printStackTrace();
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
            } finally {
                try {
                    entity.exhaust();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex);
                }
                entity.release();
                DBAccess.close();
            }
        } else {
            throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
        }
        return toreturn;
    }

    private void addNewIUSes(WorkflowRun wr, WorkflowRun currentWR) throws SQLException, ResourceException {

        Set<Integer> newIUSswa = new HashSet<Integer>();
        for (IUS ius : wr.getIus()) {
            newIUSswa.add(ius.getSwAccession());
        }

        Set<IUS> iuses = currentWR.getIus();

        for (IUS ius : iuses) {
            int swa = ius.getSwAccession();
            newIUSswa.remove(swa);
        }

        IUSService is = BeanFactory.getIUSServiceBean();

        for (int swa : newIUSswa) {
            IUS ius = is.findBySWAccession(swa);
            if (ius !=null && ius.givesPermission(registration)) {
                currentWR.getIus().add(ius);
            } else if (ius == null) {
                Log.info("Could not be found: ius "+swa);
            }
        }
    }

    private void addNewLanes(WorkflowRun wr, WorkflowRun currentWR) throws SQLException, ResourceException {
        Set<Integer> newLaneSWA = new HashSet<Integer>();
        for (Lane lane : wr.getLanes()) {
            newLaneSWA.add(lane.getSwAccession());
        }
        Set<Lane> lanes = currentWR.getLanes();

        for (Lane l : lanes) {
            int swa = l.getSwAccession();
            newLaneSWA.remove(swa);
        }
        LaneService ls = BeanFactory.getLaneServiceBean();
        for (int swa : newLaneSWA) {
            Lane l = ls.findBySWAccession(swa);
            if (l !=null && l.givesPermission(registration)) {
                currentWR.getLanes().add(l);
            } else if (lanes == null) {
                Log.info("Could not be found: ius "+swa);
            }
        }
    }
}
