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
package net.sourceforge.seqware.common.metadata;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBException;

import net.sourceforge.seqware.common.err.NotFoundException;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.ParentAccessionModel;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.ProcessingStatus;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SecondTierModel;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.SequencerRunStatus;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowParamValue;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import net.sourceforge.seqware.common.model.lists.ExperimentLibraryDesignList;
import net.sourceforge.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.model.lists.ExperimentList;
import net.sourceforge.seqware.common.model.lists.ExperimentSpotDesignList;
import net.sourceforge.seqware.common.model.lists.ExperimentSpotDesignReadSpecList;
import net.sourceforge.seqware.common.model.lists.IUSList;
import net.sourceforge.seqware.common.model.lists.LaneList;
import net.sourceforge.seqware.common.model.lists.LibrarySelectionList;
import net.sourceforge.seqware.common.model.lists.LibrarySourceList;
import net.sourceforge.seqware.common.model.lists.LibraryStrategyList;
import net.sourceforge.seqware.common.model.lists.OrganismList;
import net.sourceforge.seqware.common.model.lists.PlatformList;
import net.sourceforge.seqware.common.model.lists.ReturnValueList;
import net.sourceforge.seqware.common.model.lists.SampleList;
import net.sourceforge.seqware.common.model.lists.SequencerRunList;
import net.sourceforge.seqware.common.model.lists.StudyList;
import net.sourceforge.seqware.common.model.lists.StudyTypeList;
import net.sourceforge.seqware.common.model.lists.WorkflowList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.ext.ssl.SslContextFactory;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @version $Id: $Id
 */
public class MetadataWS implements Metadata {

  static{
    // deal with restlet's annoying logging implementation details
    System.getProperties().put("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");
  }
  
    private String version;
    private LowLevel ll;

    /**
     * <p>Constructor for MetadataWS.</p>
     */
    public MetadataWS(String database, String username, String password) {
      ll = new LowLevel(database, username, password);
    }

    /**
     * {@inheritDoc}
     *
     */
    
    
    @Override
    public ReturnValue addWorkflow(String name, String version, String description, String baseCommand, String configFile, String templateFile, String provisionDir, boolean storeProvisionDir, String archiveZip, boolean storeArchiveZip, String workflow_class, String workflow_type, String workflow_engine) {

        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        // figure out the correct command
        String command;
        if (baseCommand == null){ // SEQWARE-1692 a null baseCommand leads to a String "null" in the database
            command = null;
        } else{
            command = baseCommand + " ";
        }
        // FIXME: need to let the client determine this!
        // command = command.replaceAll("\\$\\{workflow_bundle_dir\\}",
        // provisionDir.getAbsolutePath());

        // FIXME: Remove bogus registration call
        // Registration owner = new Registration();
        // owner.setEmailAddress(user);
        // owner.setPassword(pass.toString());

        Workflow workflow = new Workflow();
        workflow.setName(name);
        workflow.setDescription(description);
        workflow.setVersion(version);
        workflow.setBaseIniFile(configFile);
        workflow.setCommand(command);
        if (storeProvisionDir) {
            workflow.setCwd(provisionDir);
        }
        workflow.setTemplate(templateFile);
        workflow.setCreateTimestamp(new Date());
        if (storeArchiveZip) {
            workflow.setPermanentBundleLocation(archiveZip);
        }
        // workflow.setUpdateTimestamp(new Date());
        // workflow.setOwner(owner);
        workflow.setWorkflowClass(workflow_class);
        workflow.setWorkflowType(workflow_type);
        workflow.setWorkflowEngine(workflow_engine);

        Log.info("Posting workflow");
        try {
            workflow = ll.addWorkflow(workflow);
            Log.stdout("WORKFLOW_ACCESSION: " + workflow.getSwAccession());
            ret.setAttribute("sw_accession", workflow.getSwAccession().toString());
            ret.setReturnValue(workflow.getWorkflowId());
        } catch (Exception e) {
            e.printStackTrace();
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }
        // open the ini file and parse each item
        // FIXME: this assumes there is one ini file which is generally fine for
        // bundled workflows but we could make this more flexible
        HashMap<String, Map<String, String>> hm = new HashMap<String, Map<String, String>>();
        // need to be careful, may contain un-expanded value
      if (configFile != null) { // SEQWARE-1692 : there is no config file for a "workflow" saved via metadata
          if (configFile.contains("${workflow_bundle_dir}")) {
              String newPath = configFile;
              newPath = newPath.replaceAll("\\$\\{workflow_bundle_dir\\}", provisionDir);
              MapTools.ini2RichMap(newPath, hm);
          } else {
              MapTools.ini2RichMap(configFile, hm);
          }
      }

        // foreach workflow param add an entry in the workflow_param table
        int count = 0;
        for (String key : hm.keySet()) {
            count++;
            Log.info("Adding WorkflowParam: " + key);
            ReturnValue rv = addWorkflowParam(hm, key, workflow);
            if (rv.getReturnValue() != ReturnValue.SUCCESS) {
                Log.error("Problem adding WorkflowParam");
                return rv;
            }
        }
        Log.info(count + " WorkflowParams should have been added");
        // add default params in workflow_param table

        // Log.info("Setting returned URI to " +
        // workflowResource.getLocationRef().getPath());
        // ret.setUrl(workflowResource.getLocationRef().getPath());

        return (ret);
    }

    /**
     * {@inheritDoc}
     *
     */
    
    
  @Override
    public ReturnValue addStudy(String title, String description, String centerName, String centerProjectName, Integer studyTypeId) {

        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        boolean studyTypeFound = isValidModelId(getStudyTypes(), studyTypeId);
        if (!studyTypeFound) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        }
        StudyType st = new StudyType();
        st.setStudyTypeId(studyTypeId);

        Study study = new Study();
        study.setTitle(title);
        study.setAlias(title);
        study.setDescription(description);
        study.setExistingType(st);
        study.setCenterName(centerName);
        study.setCenterProjectName(centerProjectName);
        study.setCreateTimestamp(new Date());

        Log.info("Posting workflow");
        try {
            study = ll.addStudy(study);
            ret.setAttribute("sw_accession", study.getSwAccession().toString());
        } catch (Exception e) {
            e.printStackTrace();
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     *
     * TODO: this needs to setup rows in experiment_library_design and
     * experiment_spot_design
     *
     * @param experimentLibraryDesignId the value of experimentLibraryDesignId
     * @param experimentSpotDesignId the value of experimentSpotDesignId
     */
    
    
  @Override
    public ReturnValue addExperiment(Integer studySwAccession, Integer platformId, String description, String title, Integer experimentLibraryDesignId, Integer experimentSpotDesignId) {

        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        try {
            boolean platformFound = isValidModelId(getPlatforms(), platformId);
            if (!platformFound){
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return ret;
            }
            Platform p = new Platform();
            p.setPlatformId(platformId);
      
            Study s = ll.findStudy("/" + studySwAccession.toString());

            Log.debug("Study: " + s);

            Experiment e = new Experiment();
            e.setStudy(s);
            e.setPlatform(p);
            e.setDescription(description);
            e.setTitle(title);
            e.setName(title);
            
            if (experimentLibraryDesignId != null){
                if (!isValidModelId(this.getExperimentLibraryDesigns(), experimentLibraryDesignId)){
                    ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                    return ret;
                }
                ExperimentLibraryDesign eld = new ExperimentLibraryDesign();
                eld.setExperimentLibraryDesignId(experimentLibraryDesignId);
                e.setExperimentLibraryDesign(eld);
            }
            if (experimentSpotDesignId != null){
                if (!isValidModelId(this.getExperimentSpotDesigns(), experimentSpotDesignId)){
                    ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                    return ret;
                }
                ExperimentSpotDesign esd = new ExperimentSpotDesign();
                esd.setExperimentSpotDesignId(experimentSpotDesignId);
                e.setExperimentSpotDesign(esd);
            }
            
            
            Log.info("Posting new experiment");

            e = ll.addExperiment(e);

            ret.setAttribute("sw_accession", e.getSwAccession().toString());

        } catch (Exception e) {
            e.printStackTrace();
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     *
     * TODO: this needs to setup rows in experiment_library_design and
     * experiment_spot_design
     */
    @Override
    public ReturnValue addSample(Integer experimentAccession, Integer parentSampleAccession,
            Integer organismId, String description, String title) {

        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        try {

            Sample s = new Sample();

            boolean organismFound = isValidModelId(getOrganisms(), organismId);
            if (!organismFound){
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return ret;
            }
            Organism o = new Organism();
            o.setOrganismId(organismId);

            if (experimentAccession!=0) {
                Experiment e = ll.findExperiment("/" + experimentAccession.toString());
                s.setExperiment(e);
            }
            Set<Sample> parents = new HashSet<Sample>();
            if (parentSampleAccession != 0) {
                Sample parentSample = ll.findSample("/"+parentSampleAccession);
                parents.add(parentSample);
            }
            /** SEQWARE-1576, let's try using an empty parent set to signal instead of a null */
            s.setParents(parents);
            s.setOrganism(o);
            s.setTitle(title);
            s.setName(title);
            s.setDescription(description);
            s.setCreateTimestamp(new Date());

            Log.info("Adding new sample");

            s = ll.addSample(s);

            ret.setAttribute("sw_accession", s.getSwAccession().toString());

        } catch(NotFoundException e){
            Log.fatal("NotFoundException" , e);
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        } catch (Exception e) {
            Log.fatal("Exception", e);
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        return ret;
    }

    /**
     * FIXME: there are problems with setting accession when I should set ID
     *
     * @param platformAccession
     * @param name
     * @param description
     * @param pairdEnd
     * @param skip
     * @param status the value of status
     * @return
     */
    
  @Override
    public ReturnValue addSequencerRun(Integer platformAccession, String name, String description, boolean pairdEnd, boolean skip, String filePath, SequencerRunStatus status) {
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        try {

            boolean platformFound = isValidModelId(this.getPlatforms(), platformAccession);
            if (!platformFound){
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return ret;
            }
            
            Platform p = new Platform();
            p.setPlatformId(platformAccession);

            SequencerRun sr = new SequencerRun();
            sr.setName(name);
            sr.setDescription(description);
            sr.setPairedEnd(pairdEnd);
            sr.setSkip(skip);
            sr.setPlatform(p);
            sr.setFilePath(filePath);
            sr.setStatus(status);

            Log.info("Posting new sequencer_run");

            sr = ll.addSequencerRun(sr);

            ret.setAttribute("sw_accession", sr.getSwAccession().toString());

        } catch(NotFoundException e){
            Log.fatal("NotFoundException, e");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        return (ret);
    }

    // FIXME: might actually need to turn off libraryStrategy et al.
    @Override
    public ReturnValue addLane(Integer sequencerRunAccession, Integer studyTypeId, Integer libraryStrategyId,
            Integer librarySelectionId, Integer librarySourceId, String name, String description,
            String cycleDescriptor, boolean skip, Integer laneNumber) {
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        try {
            
            if (!isValidModelId(getStudyTypes(), studyTypeId) || 
                    !isValidModelId(this.getLibraryStrategies(), libraryStrategyId) || 
                    !isValidModelId(this.getLibrarySelections(), librarySelectionId) || 
                    !isValidModelId(this.getLibrarySource(), librarySourceId)){
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return ret;
            }

            SequencerRun sr = ll.findSequencerRun("/" + sequencerRunAccession);
            StudyType st = new StudyType();
            st.setStudyTypeId(studyTypeId);
            LibraryStrategy ls = new LibraryStrategy();
            ls.setLibraryStrategyId(libraryStrategyId);
            LibrarySelection lsel = new LibrarySelection();
            lsel.setLibrarySelectionId(librarySelectionId);
            LibrarySource lsource = new LibrarySource();
            lsource.setLibrarySourceId(librarySourceId);

            Lane l = new Lane();
            l.setStudyType(st);
            l.setLibraryStrategy(ls);
            l.setLibrarySelection(lsel);
            l.setLibrarySource(lsource);
            l.setSequencerRun(sr);
            l.setName(name);
            l.setDescription(description);
            l.setCycleDescriptor(cycleDescriptor);
            l.setSkip(skip);
            l.setLaneIndex(laneNumber-1);

            Log.info("Posting new lane");

            l = ll.addLane(l);

            ret.setAttribute("sw_accession", l.getSwAccession().toString());

        } catch(NotFoundException e){
            Log.fatal("NotFoundException, e");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        return (ret);
    }

  @Override
    public ReturnValue addIUS(Integer laneAccession, Integer sampleAccession, String name, String description, String barcode, boolean skip) {
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        try {

            Lane l = ll.findLane("/" + laneAccession);
            Sample s = ll.findSample("/" + sampleAccession);

            IUS i = new IUS();
            i.setLane(l);
            i.setSample(s);
            i.setName(name);
            i.setDescription(description);
            i.setTag(barcode);
            i.setSkip(skip);

            Log.info("Posting new IUS");

            i = ll.addIUS(i);

            ret.setAttribute("sw_accession", i.getSwAccession().toString());

        } catch(NotFoundException e){
            Log.fatal("NotFoundException, e");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        return (ret);
    }

  @Override
    public List<Platform> getPlatforms() {
        try {
            return ll.findPlatforms();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return null;
    }

  @Override
    public List<Organism> getOrganisms() {
        try {
            return ll.findOrganisms();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return null;
    }

  @Override
    public List<StudyType> getStudyTypes() {
        try {
            return ll.findStudyTypes();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return null;
    }

  @Override
    public List<LibraryStrategy> getLibraryStrategies() {
        try {
            return ll.findLibraryStrategies();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return null;
    }

  @Override
    public List<LibrarySelection> getLibrarySelections() {
        try {
            return ll.findLibrarySelections();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return null;
    }

  @Override
    public List<LibrarySource> getLibrarySource() {
        try {
            return ll.findLibrarySources();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return null;
    }
  
    @Override
    public List<ParentAccessionModel> getViaParentAccessions(int[] potentialParentAccessions) {

        List<ParentAccessionModel> results = new ArrayList<ParentAccessionModel>();

        for (int parentAccession : potentialParentAccessions) {
            ParentAccessionModel resolveParentAccession = this.resolveParentAccession("/" + String.valueOf(parentAccession));
            results.add(resolveParentAccession);
        }
        return results;
    }
    
    /**
     * @param searchString resolve an accession "/SWA" or parent_id "?id=X"
     * @return null if the searchString cannot be resolved;
     */
    private ParentAccessionModel resolveParentAccession(String searchString) {

        Processing pr;
        Lane l;
        IUS i;
        SequencerRun sr;
        Study study;
        Experiment exp;
        Sample samp;

        if ((pr = ll.existsProcessing(searchString)) != null) {
            Log.debug("Adding parent processing " + pr.getSwAccession());
            return pr;
        } else if ((l = ll.existsLane(searchString)) != null) {
            Log.debug("Adding parent lane " + l.getSwAccession());
            return l;
        } else if ((i = ll.existsIUS(searchString)) != null) {
            Log.debug("Adding parent ius " + i.getSwAccession());
            return i;
        } else if ((sr = ll.existsSequencerRun(searchString)) != null) {
            Log.debug("Adding parent sequencer_run " + sr.getSwAccession());
            return sr;
        } else if ((study = ll.existsStudy(searchString)) != null) {
            Log.debug("Adding parent study " + study.getSwAccession());
            return study;
        } else if ((exp = ll.existsExperiment(searchString)) != null) {
            Log.debug("Adding parent experiment " + exp.getSwAccession());
            return exp;
        } else if ((samp = ll.existsSample(searchString)) != null) {
            Log.debug("Adding parent sample " + samp.getSwAccession());
            return samp;
        } else {
            return null;
        }
    }
  

    /**
     *
     * @param p
     * @param parentIds if sw accession, each ID must be in the form "/ID", if
     * db id then in the form ?id=ID
     * @param childIds
     * @throws ResourceException
     * @throws IOException
     * @throws JAXBException
     */
    private void addParentsAndChildren(Processing p, String[] parentIds, String[] childIds) throws ResourceException,
            IOException, JAXBException {

        // Associate the processing entry with the zero or more parents
        if (parentIds != null && parentIds.length != 0 && !(parentIds[0].trim().equals("/0"))) {
            for (String parentID : parentIds) {
                // TODO: I've moved the fetching so that it only occurs when needed,
                //       but should we really just associate to the first entity that
                //       happens to have the (not globally unique) ID?
                ParentAccessionModel resolveParentAccession = this.resolveParentAccession(parentID);
                if (resolveParentAccession != null){
                    if (resolveParentAccession instanceof Processing){
                         p.getParents().add((Processing)resolveParentAccession);
                    } else if (resolveParentAccession instanceof Lane){
                        p.getLanes().add((Lane)resolveParentAccession);
                    } else if (resolveParentAccession instanceof IUS){
                        p.getIUS().add((IUS)resolveParentAccession);
                    } else if (resolveParentAccession instanceof SequencerRun){
                        p.getSequencerRuns().add((SequencerRun)resolveParentAccession);
                    } else if (resolveParentAccession instanceof Study){
                        p.getStudies().add((Study)resolveParentAccession);
                    } else if (resolveParentAccession instanceof Experiment){
                        p.getExperiments().add((Experiment)resolveParentAccession);
                    } else if (resolveParentAccession instanceof Sample){
                        p.getSamples().add((Sample)resolveParentAccession);
                    } else{
                        throw new RuntimeException("Model unaccounted for, we cannot attach this");
                    }
                } else{
                    throw new IOException("This parent ID is invalid: " + parentID);
                }
            }
        }
        if (childIds != null && childIds.length != 0 && !(childIds[0].trim().equals("/0"))) {
            for (String childID : childIds) {
                Processing child = ll.findProcessing(childID);
                if (child != null) {
                    Log.debug("Adding child processing " + child.getSwAccession());
                    p.getChildren().add(child);
                } else {
                    throw new IOException("This child ID is invalid: " + childID);
                }
            }
        }

        ll.updateProcessing("/" + p.getSwAccession(), p);
    }

    private ReturnValue addWorkflowParam(HashMap<String, Map<String, String>> hm, String key, Workflow workflow) {

        Map<String, String> details = hm.get(key);
        boolean display = false;
        if ("T".equals(details.get("display"))) {
            display = true;
        }
        WorkflowParam wp = new WorkflowParam();
        wp.setWorkflow(workflow);
        wp.setType(format(details.get("type"), "text"));
        wp.setKey(format(details.get("key"), null));
        wp.setDisplay(display);
        wp.setDisplayName(format(details.get("display_name"), details.get("key")));
        wp.setFileMetaType(format(details.get("file_meta_type"), null));
        wp.setDefaultValue(format(details.get("default_value"), null));

        Log.info("Posting workflow param");
        try {
            ll.addWorkflowParam(wp);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ReturnValue(ReturnValue.FILENOTREADABLE);
        } catch (JAXBException ex) {
            ex.printStackTrace();
            return new ReturnValue(ReturnValue.FAILURE);
        } catch (ResourceException ex) {
            ex.printStackTrace();
            return new ReturnValue(ReturnValue.FAILURE);
        }
        // looks like we need to get back a workflow param object back from the database with a proper accession,
        // otherwise we will duplicate values. This is kinda clunky.
        SortedSet<WorkflowParam> workflowParams = this.getWorkflowParams(workflow.getSwAccession().toString());
        for(WorkflowParam param : workflowParams){
            if (param.getKey().equals(key)){
                wp = param;
                // and we need to set back a real workflow, yikes!
                wp.setWorkflow(workflow);
                break;
            }
        }


        Log.info("Done posting workflow param");
        // TODO: need to add support for pulldowns!
        // "pulldown", in which case we need to populate the pulldown table
        if ("pulldown".equals(details.get("type")) && details.get("pulldown_items") != null) {

            String[] pulldowns = details.get("pulldown_items").split(";");
            for (String pulldown : pulldowns) {
                String[] kv = pulldown.split("\\|");
                if (kv.length == 2) {
                    ReturnValue rv = addWorkflowParamValue(wp, kv);
                    if (rv.getReturnValue() != ReturnValue.SUCCESS) {
                        Log.error("Problem adding WorkflowParamValue");
                        return new ReturnValue(ReturnValue.FAILURE);
                    }
                }
            }
        }
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    private ReturnValue addWorkflowParamValue(WorkflowParam wp, String[] kv) {

        WorkflowParamValue wpv = new WorkflowParamValue();
        wpv.setWorkflowParam(wp);
        wpv.setDisplayName(format(kv[0], kv[0]));
        wpv.setValue(format(kv[1], kv[1]));

        try {
            ll.addWorkflowParamValue(wpv);
        } catch (IOException e) {
            e.printStackTrace();
            return new ReturnValue(ReturnValue.FAILURE);
        } catch (JAXBException e) {
            e.printStackTrace();
            return new ReturnValue(ReturnValue.FILENOTREADABLE);
        }
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue add_empty_processing_event(int[] parentIDs) {
        // FIXME: Add a new processing entry
        Processing processing = new Processing();
        processing.setStatus(ProcessingStatus.pending);
        processing.setCreateTimestamp(new Date());

        return addProcessingEventWithParentsAndChildren(processing, convertIDs(parentIDs, "?id="), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue add_empty_processing_event_by_parent_accession(int[] parentAccessions) {
        // FIXME: Add a new processing entry
        Processing processing = new Processing();
        processing.setStatus(ProcessingStatus.pending);
        processing.setCreateTimestamp(new Date());

        return addProcessingEventWithParentsAndChildren(processing, convertIDs(parentAccessions, "/"), null);
    }

    /**
     *
     * @param processing
     * @param parentIds may be db IDS or sw accession, previously converted to
     * be either "/ID" or "?id=SWA"
     * @param childIds may be db IDS or sw accession, previously converted to be
     * either "/ID" or "?id=SWA"
     * @return
     */
    private ReturnValue addProcessingEventWithParentsAndChildren(Processing processing, String[] parentIds,
            String[] childIds) {
        ReturnValue ret = new ReturnValue();
        try {
            Processing p = ll.addProcessing(processing);
            Log.debug("~~~~~~~~~~~~Processing id: " + p.getProcessingId() + " swa:" + p.getSwAccession());
            addParentsAndChildren(p, parentIds, childIds);

            ret.setReturnValue(p.getProcessingId());
            ret.setExitStatus(ReturnValue.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnValue(ReturnValue.FAILURE);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue add_task_group(int[] parentIDs, int[] childIDs, String algorithm, String description) {
        Processing processing = new Processing();
        processing.setCreateTimestamp(new Date());
        processing.setAlgorithm(algorithm);
        processing.setTaskGroup(true);
        processing.setDescription(description);

        return addProcessingEventWithParentsAndChildren(processing, convertIDs(parentIDs, "?id="),
                convertIDs(childIDs, "?id="));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int add_workflow_run(int workflowAccession) {
        WorkflowRun wr = new WorkflowRun();
        try {

            Workflow workflow = ll.findWorkflow("/" + workflowAccession);

            wr.setWorkflow(workflow);
            wr.setCreateTimestamp(new Date());
            wr.setUpdateTimestamp(new Date());

            wr = ll.addWorkflowRun(wr);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return (wr.getWorkflowRunId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add_workflow_run_ancestor(int workflowRunAccession, int processingId) {
        try {
            WorkflowRun wr = ll.findWorkflowRun("/" + workflowRunAccession);
            if (wr == null) {
                Log.error("add_workflow_run_ancestor: workflow_run not found with SWID " + workflowRunAccession);
            }
            Processing processing = ll.findProcessing("?id=" + processingId);

            processing.setWorkflowRunByAncestorWorkflowRunId(wr);

            ll.updateProcessing("/" + processingId, processing);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue associate_processing_event_with_parents_and_child(int processingID, int[] parentIDs, int[] childIDs) {
        ReturnValue ret = new ReturnValue();
        try {
            Processing p = ll.findProcessing("?id=" + processingID);
            addParentsAndChildren(p, convertIDs(parentIDs, "?id="), convertIDs(childIDs, "?id="));
        } catch (IOException ex) {
            ex.printStackTrace();
            ret.setExitStatus(ReturnValue.INVALIDFILE);
        } catch (JAXBException ex) {
            ex.printStackTrace();
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return ret;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue clean_up() {
        ll.clean_up();
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReturnValue> findFilesAssociatedWithASample(String sampleName) {
        return findFilesAssociatedWithASample(sampleName, true);
    }

    @Override
    public List<ReturnValue> findFilesAssociatedWithASample(String sampleName, boolean requireFiles) {
        ReturnValueList rv = new ReturnValueList();
        List<ReturnValue> values = new ArrayList<ReturnValue>();
        try {
            List<Sample> samples = ll.matchSampleName(sampleName);
            JaxbObject<ReturnValueList> jaxb = new JaxbObject<ReturnValueList>();
            String strRequireFiles = "";
            if (!requireFiles) {
                strRequireFiles = "?requireFiles=false";
            }
            for (Sample sample : samples) {
                rv = (ReturnValueList) ll.findObject("/samples", "/" + sample.getSwAccession() + "/files" + strRequireFiles, jaxb, rv);
                values.addAll(rv.getList());
            }
        } catch (Exception e) {
            Log.error("Problem finding files associated with sample: " + sampleName, e);
        }
        return values;
    }

    
    
    @Override
    public void studyReport(String studyTitle, Writer out) {
      Study study;
      try {
        study = ll.findStudy("?title=" + studyTitle);
      } catch(RuntimeException e){
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      ll.writeTo("/reports/studies/" + study.getSwAccession() + "/files.tsv", out);
    }


    
    @Override
    public void allStudiesReport(Writer out) {
      ll.writeTo("/reports/studies/files.tsv", out);
    }

    @Override
    public List<ReturnValue> findFilesAssociatedWithAStudy(String studyName, boolean requireFiles) {
        ReturnValueList rv = new ReturnValueList();
        try {
            Study study = ll.findStudy("?title=" + studyName);
            JaxbObject<ReturnValueList> jaxb = new JaxbObject<ReturnValueList>();
            String strRequireFiles = "";
            if (!requireFiles) {
                strRequireFiles = "?requireFiles=false";
            }
            rv = (ReturnValueList) ll.findObject("/studies", "/" + study.getSwAccession() + "/files" + strRequireFiles, jaxb,
                    new ReturnValueList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rv.getList();
    }
    
    

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReturnValue> findFilesAssociatedWithAStudy(String studyName) {
        return findFilesAssociatedWithAStudy(studyName, true);
    }

    @Override
    public List<ReturnValue> findFilesAssociatedWithASequencerRun(String runName, boolean requireFiles) {
        ReturnValueList rv = new ReturnValueList();
        try {
            SequencerRun run = ll.findSequencerRun("?name=" + runName);
            JaxbObject<ReturnValueList> jaxb = new JaxbObject<ReturnValueList>();
            String strRequireFiles = "";
            if (!requireFiles) {
                strRequireFiles = "?requireFiles=false";
            }
            rv = (ReturnValueList) ll.findObject("/sequencerruns", "/" + run.getSwAccession() + "/files" + strRequireFiles, jaxb,
                    new ReturnValueList());
        } catch (Exception e) {
            e.printStackTrace();
            // rv.setExitStatus(ReturnValue.FAILURE);
        }
        return rv.getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReturnValue> findFilesAssociatedWithASequencerRun(String runName) {
        return findFilesAssociatedWithASequencerRun(runName, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> get_workflow_info(int workflowAccession) {
        Map<String, String> map = new HashMap<String, String>();

        try {
            Workflow workflow = ll.findWorkflow("/" + workflowAccession);

            map.put("name", workflow.getName());
            map.put("description", workflow.getDescription());
            map.put("version", workflow.getVersion());
            map.put("base_ini_file", workflow.getBaseIniFile());
            map.put("cmd", workflow.getCommand());
            map.put("current_working_dir", workflow.getCwd());
            map.put("workflow_template", workflow.getTemplate());
            map.put("create_tstmp", workflow.getCreateTimestamp().toString());
            map.put("update_tstmp", workflow.getUpdateTimestamp().toString());
            map.put("workflow_accession", workflow.getSwAccession().toString());
            map.put("permanent_bundle_location", workflow.getPermanentBundleLocation());
            map.put("workflow_engine", workflow.getWorkflowEngine());
            map.put("workflow_type", workflow.getWorkflowType());
            map.put("workflow_class", workflow.getWorkflowClass());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private Object testNull(Object object, Class clazz, Object searchParameters) throws NotFoundException {
        if (object == null) {
            String text = "The " + clazz + " cannot be found with search parameters: " + searchParameters;
            Log.debug(text);
            throw new NotFoundException(text);
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int get_workflow_run_accession(int workflowRunId) {
        int accession = 0;
        try {
            WorkflowRun wr = ll.findWorkflowRun("?id=" + workflowRunId);
            accession = wr.getSwAccession();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accession;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Workflow getWorkflow(int workflowAccession) {
        Workflow wf = null;
        try {
            wf = ll.findWorkflow("/" + Integer.toString(workflowAccession));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (wf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowRun getWorkflowRun(int workflowRunAccession) {
        WorkflowRun wr = null;
        try {
            wr = ll.findWorkflowRun("/" + workflowRunAccession);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (wr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int get_workflow_run_id(int workflowRunAccession) {
        int id = 0;
        try {
            WorkflowRun wr = ll.findWorkflowRun("/" + workflowRunAccession);
            id = wr.getWorkflowRunId();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean linkWorkflowRunAndParent(int workflowRunId, int parentAccession) throws SQLException {
        JaxbObject<WorkflowRun> jow = new JaxbObject<WorkflowRun>();
        try {
            IUS ius = ll.existsIUS("/" + parentAccession);
            Lane lane = ll.existsLane("/" + parentAccession);
            // this one won't be able to get back lanes and ius
      WorkflowRun wr_withoutLanes = ll.findWorkflowRun("?id=" + workflowRunId /**+ "&show=lanes,ius"*/);
            // this will, but uses seqware accessions
            int accession = wr_withoutLanes.getSwAccession();
            WorkflowRun wr_withLanesAndIUS = ll.findWorkflowRun("/" + accession + "?show=lanes,ius");
            if (ius != null) {
                SortedSet<IUS> iuses = wr_withLanesAndIUS.getIus();
                if (iuses == null) {
                    iuses = new TreeSet<IUS>();
                }
                iuses.add(ius);
                wr_withLanesAndIUS.setIus(iuses);

                ll.updateWorkflowRun("/" + accession, wr_withLanesAndIUS);

            } else if (lane != null) {
                SortedSet<Lane> lanes = wr_withLanesAndIUS.getLanes();
                if (lanes == null) {
                    lanes = new TreeSet<Lane>();
                }
                lanes.add(lane);
                wr_withLanesAndIUS.setLanes(lanes);

                ll.updateWorkflowRun("/" + accession, wr_withLanesAndIUS);

            } else {
                Log.error("ERROR: SW Accession is neither a lane nor an IUS: " + parentAccession);
                return (false);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return (true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int mapProcessingIdToAccession(int processingId) {
        int accession = 0;
        try {
            Processing processing = ll.findProcessing("?id=" + processingId);
            accession = processing.getSwAccession();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accession;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue processing_event_to_task_group(int processingID, int[] parentIDs, int[] childIDs,
            String algorithm, String description) {

        try {
            Processing processing = ll.findProcessing("?id=" + processingID);

            processing.setTaskGroup(true);
            processing.setAlgorithm(algorithm);
            processing.setDescription(description);

            ll.updateProcessing("/" + processing.getSwAccession(), processing);

            addParentsAndChildren(processing, convertIDs(parentIDs, "?id="), convertIDs(childIDs, "?id="));

        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnValue(null, "Exception: " + e.getMessage(), ReturnValue.SQLQUERYFAILED);
        }

        /*
         * If no error, return success
         */
        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(processingID);
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue update_processing_event(int processingID, ReturnValue retval) {
        try {
            Processing processing = ll.findProcessing("?id=" + processingID + "&show=files");

            processing.setExitStatus(retval.getExitStatus());
            processing.setProcessExitStatus(retval.getProcessExitStatus());
            processing.setAlgorithm(retval.getAlgorithm());
            processing.setDescription(retval.getDescription());
            processing.setParameters(retval.getParameters());
            processing.setVersion(retval.getVersion());
            processing.setUrl(retval.getUrl());
            processing.setUrlLabel(retval.getUrlLabel());
            processing.setStdout(retval.getStdout());
            processing.setStderr(retval.getStderr());
            processing.setRunStartTimestamp(retval.getRunStartTstmp());
            processing.setRunStopTimestamp(retval.getRunStopTstmp());
            processing.setUpdateTimestamp(new Date());

            Set<File> modelFiles = new HashSet<File>();
            // Add and associate files for each item
            if (retval.getFiles() != null) {
                for (FileMetadata file : retval.getFiles()) {
                    // If the file path is empty, warn and skip
                    if (file.getFilePath().compareTo("") == 0) {
                        Log.error("WARNING: Skipping empty FilePath for ProcessingID entry: " + processingID);
                        continue;
                    }
                    // If the meta type is empty, warn and skip
                    if (file.getMetaType().compareTo("") == 0) {
                        Log.error("WARNING: Skipping empty MetaType for ProcessingID entry: " + processingID);
                        continue;
                    }
                    File modelFile = new File();
                    modelFile.setFilePath(file.getFilePath());
                    modelFile.setMetaType(file.getMetaType());
                    modelFile.setType(file.getType());
                    modelFile.setDescription(file.getDescription());
                    modelFile.setUrl(file.getUrl());
                    modelFile.setUrlLabel(file.getUrlLabel());
                    // md5sum??

                    modelFile = ll.addFile(modelFile);

                    modelFiles.add(modelFile);
                }
            }
            processing.getFiles().addAll(modelFiles);

            ll.updateProcessing("/" + processing.getSwAccession(), processing);

        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnValue(null, "Exception: " + e.getMessage(), ReturnValue.SQLQUERYFAILED);
        }

        /*
         * If no error, return success
         */
        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(processingID);
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue update_processing_status(int processingID, ProcessingStatus status) {
        try {
            Processing processing = ll.findProcessing("?id=" + processingID);

            processing.setStatus(status);

            ll.updateProcessing("/" + processing.getSwAccession(), processing);

        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnValue(null, "Exception: " + e.getMessage(), ReturnValue.SQLQUERYFAILED);
        }

        /*
         * If no error, return success
         */
        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(processingID);
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue update_processing_workflow_run(int processingID, int workflowRunAccession) {
        try {
            Processing processing = ll.findProcessing("?id=" + processingID + "&show=files");

            WorkflowRun run = ll.findWorkflowRun("/" + workflowRunAccession);

            processing.setWorkflowRun(run);

            ll.updateProcessing("/" + processing.getSwAccession(), processing);

        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnValue(null, "Exception: " + e.getMessage(), ReturnValue.SQLQUERYFAILED);
        }

        /*
         * If no error, return success
         */
        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(processingID);
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * @param workflowEngine the value of workflowEngine
     */
    @Override
    public ReturnValue update_workflow_run(int workflowRunId, String pegasusCmd, String workflowTemplate, WorkflowRunStatus status, String statusCmd, String workingDirectory, String dax, String ini, String host, String stdErr, String stdOut, String workflowEngine, Set<Integer> inputFiles) {
        int accession = 0;
        try {
            WorkflowRun wr = ll.findWorkflowRun("?id=" + workflowRunId);

            accession = wr.getSwAccession();
            wr.setCommand(pegasusCmd);
            wr.setTemplate(workflowTemplate);
            wr.setStatus(status);
            wr.setStatusCmd(statusCmd);
            wr.setCurrentWorkingDir(workingDirectory);
            wr.setDax(dax);
            wr.setIniFile(ini);
            wr.setHost(host);
            wr.setStdErr(stdErr);
            wr.setStdOut(stdOut);
            wr.setWorkflowEngine(workflowEngine);
            wr.setInputFileAccessions(inputFiles);

            ll.updateWorkflowRun("/" + accession, wr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(accession);
        return ret;
    }

    @Override
    public void updateWorkflowRun(WorkflowRun wr) {
      try {
        ll.updateWorkflowRun("/" + wr.getSwAccession(), wr);
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private String format(String variable, String defaultStr) {

        if ((variable == null || "".equals(variable)) && (defaultStr != null && !"".equals(defaultStr))) {
            String newDefaultStr = defaultStr.replaceAll("'", "");
            return newDefaultStr;
        } else if ((variable == null || "".equals(variable)) && (defaultStr == null || "".equals(defaultStr))) {
            return (null);
        } else {
            String newVariable = variable.replaceAll("'", "");
            return newVariable;
        }
    }

    private String[] convertIDs(int[] ids, String prefix) {
        String[] stringIds = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            stringIds[i] = prefix + new Integer(ids[i]).toString();
        }
        return stringIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue saveFileForIus(int workflowRunId, int iusAccession, FileMetadata file, int processingId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isDuplicateFile(String filepath) {
        File file = null;
        try {
            file = ll.findFile("?path=" + filepath);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        } catch (NotFoundException ex){
            /** do nothing, this is expected */
        }
        return file != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue updateWorkflow(int workflowId, String permanentBundleLocation) {
        ReturnValue rv = new ReturnValue();
        try {
            Workflow workflow = ll.findWorkflow("?id=" + workflowId);
            workflow.setPermanentBundleLocation(permanentBundleLocation);

            ll.updateWorkflow("/" + workflow.getSwAccession(), workflow);
            rv.setReturnValue(workflow.getSwAccession());

        } catch (IOException ex) {
            Log.error("IOException updating Workflow PK " + workflowId + " " + ex.getMessage());
        } catch (JAXBException ex) {
            Log.error("JAXBException updating Workflow PK " + workflowId + " " + ex.getMessage());
        }
        return rv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listInstalledWorkflows() {
        StringBuffer sb = new StringBuffer();
        try {
            for (Workflow w : ll.findWorkflows()) {
                sb.append(w.getName() + "\t");
                sb.append(w.getVersion() + "\t");
                sb.append(w.getCreateTimestamp() + "\t");
                sb.append(w.getSwAccession() + "\t");
                sb.append(w.getPermanentBundleLocation() + "\n");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return (sb.toString());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listInstalledWorkflowParams(String workflowAccession) {

        StringBuilder sb = new StringBuilder();
        try {
            Workflow workflow = ll.findWorkflowParams(workflowAccession);
            if (workflow.getWorkflowParams() == null){
                return sb.toString();
            }
            for (WorkflowParam wp : workflow.getWorkflowParams()) {
                sb.append("#key=").append(wp.getKey()).append(":type=").append(wp.getType()).append(":display=");
                if (wp.getDisplay()) {
                    sb.append("T");
                } else {
                    sb.append("F");
                }
                if (wp.getDisplayName() != null && wp.getDisplayName().length() > 0) {
                    sb.append(":display_name=").append(wp.getDisplayName());
                }
                if (wp.getFileMetaType() != null && wp.getFileMetaType().length() > 0) {
                    sb.append(":file_meta_type=").append(wp.getFileMetaType());
                }
                SortedSet<WorkflowParamValue> wpvalues = wp.getValues();
                if (wpvalues != null && wpvalues.size() > 0) {
                    sb.append(":pulldown_items=");
                    boolean first = true;
                    for (WorkflowParamValue wpv : wpvalues) {
                        if (first) {
                            first = false;
                            sb.append(wpv.getDisplayName()).append("|").append(wpv.getValue());
                        } else {
                            sb.append(";").append(wpv.getDisplayName()).append("|").append(wpv.getValue());
                        }
                    }
                }
                sb.append("\n");
        if (wp.getDefaultValue() == null){
                    sb.append(wp.getKey()).append("=" + "\n");
        } else{
                    sb.append(wp.getKey()).append("=").append(wp.getDefaultValue()).append("\n");
                }
            }

        } catch (IOException ex) {
            Log.error(ex);
        } catch (JAXBException ex) {
            Log.error(ex);
        }

        return (sb.toString());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<WorkflowParam> getWorkflowParams(String workflowAccession) {
        SortedSet<WorkflowParam> params = null;
        try {
            Workflow workflow = ll.findWorkflowParams(workflowAccession);
            params = workflow.getWorkflowParams();
            testNull(params, new TreeSet<WorkflowParam>().getClass(), workflowAccession);
        } catch (IOException ex) {
            Log.error(ex);
        } catch (JAXBException ex) {
            Log.error(ex);
        }
        return params;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWorkflowAccession(String name, String version) {

        int workflowAccession = -1;
        try {
            Workflow workflow = ll.findWorkflow("?name=" + name + "&version=" + version);
            workflowAccession = workflow.getSwAccession();
        } catch (IOException ex) {
            Log.error("IOException in getWorkflowAccession for " + name + version, ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException in getWorkflowAccession for " + name + version, ex);
        }
        return workflowAccession;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorkflowRun> getWorkflowRunsByStatus(WorkflowRunStatus status) {
        try {
            String searchString = "?status=" + status.name() + "";
            return ll.findWorkflowRuns(searchString);
        } catch (IOException ex) {
            Log.error("", ex);
        } catch (JAXBException ex) {
            Log.error("", ex);
        }
        return new ArrayList<WorkflowRun>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorkflowRun> getWorkflowRunsByHost(String host) {
        try {
            String searchString = "?host=" + host + "";
            return ll.findWorkflowRuns(searchString);
        } catch (IOException ex) {
            Log.error("", ex);
        } catch (JAXBException ex) {
            Log.error("", ex);
        }
        return new ArrayList<WorkflowRun>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowRun getWorkflowRunWithWorkflow(String workflowRunAccession) {
        try {
            WorkflowRun wr = ll.findWorkflowRun("/" + workflowRunAccession);

            Workflow w = ll.findWorkflowByWorkflowRun(workflowRunAccession);
            wr.setWorkflow(w);
            return (wr);
        } catch (IOException ex) {
            Log.error("", ex);
        } catch (JAXBException ex) {
            Log.error("", ex);
        }
        return (null);
    }

    /**
     * <p>getAllStudies.</p>
     *
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<Study> getAllStudies() {
        try {
            return ll.findStudies();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * <p>getAllSequencerRuns.</p>
     *
     * @return a {@link java.util.List} object.
     */
  @Override
    public List<SequencerRun> getAllSequencerRuns() {
        try {
            return ll.findSequencerRuns();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * FIXME: this is a hack, will need to add an object layer between this
     * metadata object and the response
     *
     * @author boconnor
     */
    @Override
    public String getSequencerRunReport() {
        return (ll.getString("/reports/sequencerruns"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateLane(int laneSWID, LaneAttribute laneAtt, Boolean skip) {
        try {
            Log.debug("Annotating Lane " + laneSWID + " with skip=" + skip + ", laneAtt = " + laneAtt);
            Lane lane = ll.findLane("/" + laneSWID /**+ "?show=attributes"*/);
            if (skip != null) {
                lane.setSkip(skip);
            }
            if (laneAtt != null) {
                Set<LaneAttribute> atts = lane.getLaneAttributes();
                if (atts == null) {
                    atts = new HashSet<LaneAttribute>();
                }

                atts.add(laneAtt);
                lane.setLaneAttributes(atts);
            }
            ll.updateLane("/" + laneSWID, lane);
        } catch (IOException ex) {
            Log.error("IOException while updating lane " + laneSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating lane " + laneSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating lane " + laneSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateIUS(int iusSWID, IUSAttribute iusAtt, Boolean skip) {
        try {
            Log.debug("Annotating IUS " + iusSWID + " with skip=" + skip + ", iusAtt = " + iusAtt);
            IUS lane = ll.findIUS("/" + iusSWID);
            if (skip != null) {
                lane.setSkip(skip);
            }
            if (iusAtt != null) {
                Set<IUSAttribute> atts = lane.getIusAttributes();
                if (atts == null) {
                    atts = new HashSet<IUSAttribute>();
                }

                atts.add(iusAtt);
                lane.setIusAttributes(atts);
            }
            ll.updateIUS("/" + iusSWID, lane);
        } catch (IOException ex) {
            Log.error("IOException while updating ius " + iusSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating ius " + iusSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating ius " + iusSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateSequencerRun(int sequencerRunSWID, SequencerRunAttribute sequencerRunAtt, Boolean skip) {
        try {
            Log.debug("Annotating SequencerRun " + sequencerRunSWID + " with skip=" + skip + ", sequencerRunAtt = "
                    + sequencerRunAtt);
            SequencerRun sequencerRun = ll.findSequencerRun("/" + sequencerRunSWID);
            if (skip != null) {
                sequencerRun.setSkip(skip);
            }
            if (sequencerRunAtt != null) {
                Set<SequencerRunAttribute> atts = sequencerRun.getSequencerRunAttributes();
                if (atts == null) {
                    atts = new HashSet<SequencerRunAttribute>();
                }

                atts.add(sequencerRunAtt);
                sequencerRun.setSequencerRunAttributes(atts);
            }
            ll.updateSequencerRun("/" + sequencerRunSWID, sequencerRun);
        } catch (IOException ex) {
            Log.error("IOException while updating sequencerRun " + sequencerRunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating sequencerRun " + sequencerRunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating sequencerRun " + sequencerRunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateExperiment(int experimentSWID, ExperimentAttribute att, Boolean skip) {
        try {
            Log.debug("Annotating Experiment " + experimentSWID + " with skip=" + skip + ", Att = " + att);
            Experiment obj = ll.findExperiment("/" + experimentSWID);
            if (skip != null) {
                // obj.setSkip(skip);
                Log.info("Experiment does not have a skip column!");
            }
            if (att != null) {
                Set<ExperimentAttribute> atts = obj.getExperimentAttributes();
                if (atts == null) {
                    atts = new HashSet<ExperimentAttribute>();
                }
                atts.add(att);
                obj.setExperimentAttributes(atts);
            }
            ll.updateExperiment("/" + experimentSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating experiment " + experimentSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating experiment " + experimentSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating experiment " + experimentSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateProcessing(int swid, ProcessingAttribute att, Boolean skip) {
        try {
            Log.debug("Annotating Processing " + swid + " with skip=" + skip + ", Att = " + att);
            Processing obj = ll.findProcessing("/" + swid);
            if (skip != null) {
                // obj.setSkip(skip);
                Log.info("Processing does not have a skip column!");
            }
            if (att != null) {
                Set<ProcessingAttribute> atts = obj.getProcessingAttributes();
                if (atts == null) {
                    atts = new HashSet<ProcessingAttribute>();
                }

                atts.add(att);
                obj.setProcessingAttributes(atts);
            }
            ll.updateProcessing("/" + swid, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating processing " + swid + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating processing " + swid + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating processing " + swid + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateSample(int swid, SampleAttribute att, Boolean skip) {
        try {
            Log.debug("Annotating Sample " + swid + " with skip=" + skip + ", Att = " + att);
            Sample obj = ll.findSample("/" + swid/**+"?show=attributes"*/);
            if (skip != null) {
                obj.setSkip(skip);
            }
            if (att != null) {
                Set<SampleAttribute> atts = obj.getSampleAttributes();
                if (atts == null) {
                    atts = new HashSet<SampleAttribute>();
                }

                atts.add(att);
                obj.setSampleAttributes(atts);
            }
            ll.updateSample("/" + swid, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating sample " + swid + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating sample " + swid + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating sample " + swid + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateStudy(int swid, StudyAttribute att, Boolean skip) {
        try {
            Log.debug("Annotating Study " + swid + " with skip=" + skip + ", Att = " + att);
            Study obj = ll.findStudy("/" + swid);
            if (skip != null) {
                // obj.setSkip(skip);
                Log.info("Processing does not have a skip column!");
            }
            if (att != null) {
                Set<StudyAttribute> atts = obj.getStudyAttributes();
                if (atts == null) {
                    atts = new HashSet<StudyAttribute>();
                }
                // att.setStudy(obj);
                atts.add(att);
                obj.setStudyAttributes(atts);
            }
            ll.updateStudy("/" + swid, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating study " + swid + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating study " + swid + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating study " + swid + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWorkflowRunReport(int workflowRunSWID) {
        String report = (String) ll.getString("/reports/workflowruns/" + workflowRunSWID);
        return report;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWorkflowRunReportStdErr(int workflowRunSWID) {
        return ((String) ll.getString("/reports/workflowruns/" + workflowRunSWID + "/stderr"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWorkflowRunReportStdOut(int workflowRunSWID) {
        return ((String) ll.getString("/reports/workflowruns/" + workflowRunSWID + "/stdout"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWorkflowRunReport(int workflowSWID, Date earliestDate, Date latestDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        StringBuilder dateQuery = new StringBuilder();
        if (earliestDate != null) {
            dateQuery.append("earliestDate=");
            dateQuery.append(dateFormat.format(earliestDate));
        }
        if (latestDate != null) {
            if (dateQuery.length() != 0) {
                dateQuery.append("&");
            }
            dateQuery.append("latestDate=");
            dateQuery.append(dateFormat.format(latestDate));
        }
        String report = (String) ll.getString("/reports/workflows/" + workflowSWID + "/runs?" + dateQuery.toString());
        return report;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWorkflowRunReport(Date earliestDate, Date latestDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        StringBuilder dateQuery = new StringBuilder();
        if (earliestDate != null) {
            dateQuery.append("earliestDate=");
            dateQuery.append(dateFormat.format(earliestDate));
        }
        if (latestDate != null) {
            if (dateQuery.length() != 0) {
                dateQuery.append("&");
            }
            dateQuery.append("latestDate=");
            dateQuery.append(dateFormat.format(latestDate));
        }
        String report = (String) ll.getString("/reports/workflowruns?" + dateQuery.toString());

        return report;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile(int swAccession) {
        try {
            return ll.findFile("/" + swAccession);
        } catch (IOException ex) {
            Log.error(ex);
        } catch (JAXBException ex) {
            Log.error(ex);
        }
        return null;
    }
    
    private void wrapAsRuntimeException(Exception e){
        if (!(e instanceof RuntimeException)){
            throw new RuntimeException(e);
        } else{
            throw (RuntimeException)e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateWorkflow(int workflowSWID, WorkflowAttribute att, Boolean skip) {
        try {
            Log.debug("Annotating WorkflowRun " + workflowSWID + " with skip=" + skip + ", Att = " + att);
            Workflow obj = ll.findWorkflow("/" + workflowSWID);
            if (skip != null) {
                // obj.setSkip(skip);
                Log.info("Processing does not have a skip column!");
            }
            if (att != null) {
                Set<WorkflowAttribute> atts = obj.getWorkflowAttributes();
                if (atts == null) {
                    atts = new HashSet<WorkflowAttribute>();
                }
                // att.setStudy(obj);
                atts.add(att);
                obj.setWorkflowAttributes(atts);
            }
            ll.updateWorkflow("/" + workflowSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating study " + workflowSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating study " + workflowSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating study " + workflowSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateWorkflowRun(int workflowrunSWID, WorkflowRunAttribute att, Boolean skip) {
        try {
            Log.debug("Annotating WorkflowRun " + workflowrunSWID + " with skip=" + skip + ", Att = " + att);
            WorkflowRun obj = ll.findWorkflowRun("/" + workflowrunSWID);
            if (skip != null) {
                // obj.setSkip(skip);
                Log.info("Processing does not have a skip column!");
            }
            if (att != null) {
                Set<WorkflowRunAttribute> atts = obj.getWorkflowRunAttributes();
                if (atts == null) {
                    atts = new HashSet<WorkflowRunAttribute>();
                }
                // att.setStudy(obj);
                atts.add(att);
                obj.setWorkflowRunAttributes(atts);
            }
            ll.updateWorkflowRun("/" + workflowrunSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating study " + workflowrunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating study " + workflowrunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating study " + workflowrunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateIUS(int iusSWID, Set<IUSAttribute> iusAtts) {
        try {
            Log.debug("Annotating IUS ");
            IUS ius = ll.findIUS("/" + iusSWID);
            ius.getIusAttributes().clear();
            for (IUSAttribute ia : iusAtts) {
                // ia.setIus(ius);
                ius.getIusAttributes().add(ia);
            }

            ll.updateIUS("/" + iusSWID, ius);
        } catch (IOException ex) {
            Log.error("IOException while updating ius " + iusSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating ius " + iusSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating ius " + iusSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateLane(int laneSWID, Set<LaneAttribute> laneAtts) {
        try {
            Log.debug("Annotating Lane " + laneSWID);
            Lane lane = ll.findLane("/" + laneSWID);
            lane.getLaneAttributes().clear();
            for (LaneAttribute la : laneAtts) {
                // la.setLane(lane);
                lane.getLaneAttributes().add(la);
            }
            ll.updateLane("/" + laneSWID, lane);
        } catch (IOException ex) {
            Log.error("IOException while updating lane " + laneSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating lane " + laneSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating lane " + laneSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateSequencerRun(int sequencerRunSWID, Set<SequencerRunAttribute> sequencerRunAtts) {
        try {
            Log.debug("Annotating SequencerRun " + sequencerRunSWID);
            SequencerRun sequencerRun = ll.findSequencerRun("/" + sequencerRunSWID);
            sequencerRun.getSequencerRunAttributes().clear();
            for (SequencerRunAttribute sa : sequencerRunAtts) {
                // sa.setSequencerRunWizardDTO((SequencerRunWizardDTO) sequencerRun);
                sequencerRun.getSequencerRunAttributes().add(sa);
            }
            ll.updateSequencerRun("/" + sequencerRunSWID, sequencerRun);
        } catch (IOException ex) {
            Log.error("IOException while updating sequencerRun " + sequencerRunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating sequencerRun " + sequencerRunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating sequencerRun " + sequencerRunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateExperiment(int experimentSWID, Set<ExperimentAttribute> atts) {
        try {
            Log.debug("Annotating Experiment " + experimentSWID);
            Experiment obj = ll.findExperiment("/" + experimentSWID);
            obj.getExperimentAttributes().clear();
            for (ExperimentAttribute ea : atts) {
                // ea.setExperiment(obj);
                obj.getExperimentAttributes().add(ea);
            }
            ll.updateExperiment("/" + experimentSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating experiment " + experimentSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating experiment " + experimentSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating experiment " + experimentSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateFile(int fileSWID, Set<FileAttribute> atts) {
        try {
            Log.debug("Annotating File " + fileSWID);
            File obj = ll.findFile("/" + fileSWID);
            obj.getFileAttributes().clear();
            for (FileAttribute ea : atts) {
                // ea.setFile(obj);
                obj.getFileAttributes().add(ea);
            }
            ll.updateFile("/" + fileSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating file " + fileSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating file " + fileSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating file " + fileSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateFile(int fileSWID, FileAttribute att, Boolean skip) {
        try {
            Log.debug("Annotating File " + fileSWID + " with skip=" + skip + ", Att = " + att);
            File obj = ll.findFile("/" + fileSWID);
            if (skip != null) {
                obj.setSkip(skip);
            }
            if (att != null) {
                Set<FileAttribute> atts = obj.getFileAttributes();
                if (atts == null) {
                    atts = new HashSet<FileAttribute>();
                }
                atts.add(att);
                obj.setFileAttributes(atts);
            }
            ll.updateFile("/" + fileSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating file " + fileSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating file " + fileSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating file " + fileSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateProcessing(int processingSWID, Set<ProcessingAttribute> atts) {
        try {
            Log.debug("Annotating Processing " + processingSWID);
            Processing obj = ll.findProcessing("/" + processingSWID);
            obj.getProcessingAttributes().clear();
            for (ProcessingAttribute pa : atts) {
                // pa.setProcessing(obj);
                obj.getProcessingAttributes().add(pa);
            }
            ll.updateProcessing("/" + processingSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating processing " + processingSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating processing " + processingSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating processing " + processingSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateSample(int sampleSWID, Set<SampleAttribute> atts) {
        try {
            Log.debug("Annotating Sample " + sampleSWID);
            Sample obj = ll.findSample("/" + sampleSWID);
            obj.getSampleAttributes().clear();
            for (SampleAttribute sa : atts) {
                // sa.setSample(obj);
                obj.getSampleAttributes().add(sa);
            }
            ll.updateSample("/" + sampleSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating sample " + sampleSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating sample " + sampleSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating sample " + sampleSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateStudy(int studySWID, Set<StudyAttribute> atts) {
        try {
            Log.debug("Annotating Study ");
            Study obj = ll.findStudy("/" + studySWID);
            obj.getStudyAttributes().clear();
            for (StudyAttribute sa : atts) {
                // sa.setStudy(obj);
                obj.getStudyAttributes().add(sa);
            }
            ll.updateStudy("/" + studySWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating study " + studySWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating study " + studySWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating study " + studySWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateWorkflow(int workflowSWID, Set<WorkflowAttribute> atts) {
        try {
            Log.debug("Annotating Workflow " + workflowSWID);
            Workflow obj = ll.findWorkflow("/" + workflowSWID);
            obj.getWorkflowAttributes().clear();
            for (WorkflowAttribute wa : atts) {
                // wa.setWorkflow(obj);
                obj.getWorkflowAttributes().add(wa);
            }
            ll.updateWorkflow("/" + workflowSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating workflow " + workflowSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating workflow " + workflowSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating workflow " + workflowSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void annotateWorkflowRun(int workflowrunSWID, Set<WorkflowRunAttribute> atts) {
        try {
            Log.debug("Annotating WorkflowRun ");
            WorkflowRun obj = ll.findWorkflowRun("/" + workflowrunSWID);
            obj.getWorkflowRunAttributes().clear();
            for (WorkflowRunAttribute wa : atts) {
                // wa.setWorkflowRun(obj);
                obj.getWorkflowRunAttributes().add(wa);
            }
            ll.updateWorkflowRun("/" + workflowrunSWID, obj);
        } catch (IOException ex) {
            Log.error("IOException while updating workflow run " + workflowrunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (JAXBException ex) {
            Log.error("JAXBException while updating workflow run " + workflowrunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        } catch (ResourceException ex) {
            Log.error("ResourceException while updating workflow run " + workflowrunSWID + " " + ex.getMessage());
            wrapAsRuntimeException(ex);
        }

    }

    @Override
    public List<Lane> getLanesFrom(int sequencerRunAccession) {
        try {
            JaxbObject<LaneList> jaxb = new JaxbObject<LaneList>();
            LaneList list = (LaneList) ll.existsObject("/sequencerruns/" + sequencerRunAccession + "/lanes", "", jaxb, new LaneList());
            if (list!=null)
		return list.getList();
        } catch (JAXBException ex) {
            Log.error("JAXBException while retrieving lanes from sequencer run", ex);
        }
        return null;
    }

    @Override
    public List<IUS> getIUSFrom(int laneOrSampleAccession) {
        StringBuilder sb = new StringBuilder();

        if (ll.existsLane("/" + laneOrSampleAccession) != null) {
            sb.append("/lanes/");
        } else if (ll.existsSample("/" + laneOrSampleAccession) != null) {
            sb.append("/samples/");
        } else {
            Log.error("There is no Lane or Sample with this sw_accession: " + laneOrSampleAccession);
            return null;
        }
        try {
            sb.append(laneOrSampleAccession).append("/ius");
            JaxbObject<IUSList> jaxb = new JaxbObject<IUSList>();
            IUSList list = (IUSList) ll.existsObject(sb.toString(), "", jaxb, new IUSList());
            if (list !=null)
		return list.getList();
        } catch (JAXBException ex) {
            Log.error("JAXBException while retrieving IUSes (barcodes) from lane or sample", ex);
        }
        return null;
    }

    @Override
    public List<Experiment> getExperimentsFrom(int studyAccession) {
        try {
            JaxbObject<ExperimentList> jaxb = new JaxbObject<ExperimentList>();
            ExperimentList list = (ExperimentList) ll.existsObject("/studies/" + studyAccession + "/experiments", "", jaxb, new ExperimentList());
            if (list!=null)
		return list.getList();
        } catch (JAXBException ex) {
            Log.error("JAXBException while retrieving experiments from study", ex);
        }
        return null;
    }

    @Override
    public List<Sample> getSamplesFrom(int experimentAccession) {
        try {
            JaxbObject<SampleList> jaxb = new JaxbObject<SampleList>();
            SampleList list = (SampleList) ll.existsObject("/experiments/" + experimentAccession + "/samples", "", jaxb, new SampleList());
            if (list != null)
		return list.getList();
        } catch (JAXBException ex) {
            Log.error("JAXBException while retrieving samples from experiment", ex);
        }
        return null;
    }

    @Override
    public List<Sample> getChildSamplesFrom(int parentSampleAccession) {
        try {
            JaxbObject<SampleList> jaxb = new JaxbObject<SampleList>();
            SampleList list = (SampleList) ll.existsObject("/samples/" + parentSampleAccession + "/children", "", jaxb, new SampleList());
            if (list!=null)
		return list.getList();
        } catch (JAXBException ex) {
            Log.error("JAXBException while retrieving child samples from parent sample", ex);
        }
        return null;
    }

    @Override
    public List<Sample> getParentSamplesFrom(int childSampleAccession) {
        try {
            JaxbObject<SampleList> jaxb = new JaxbObject<SampleList>();
            SampleList list = (SampleList) ll.existsObject("/samples/" + childSampleAccession + "/parents", "", jaxb, new SampleList());
            if (list !=null)
		return list.getList();
        } catch (JAXBException ex) {
            Log.error("JAXBException while retrieving parent samples from child sample", ex);
        }
        return null;
    }
    
    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithInputFiles(List<Integer> fileAccessions) {
        try {
            if (fileAccessions.size() > 0){
                return ll.findWorkflowByFiles(fileAccessions);
            } else{
                return new ArrayList<WorkflowRun>();
            }
        } catch (IOException ex) {
            Log.fatal("IOException", ex);
            throw new RuntimeException(ex);
        } catch (JAXBException ex) {
            Log.fatal("JAXBException", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithFiles(List<Integer> fileAccessions, String search_type) {
        try {
            return ll.findWorkflowByFiles(fileAccessions, search_type);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Lane getLane(int laneAccession) {
         try {
            return ll.findLane("/" + laneAccession);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }
    

    @Override
    public SequencerRun getSequencerRun(int sequencerRunAccession) {
        try {
            return ll.findSequencerRun("/" + sequencerRunAccession);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<ExperimentLibraryDesign> getExperimentLibraryDesigns() {
        try {
            return ll.findExperimentLibraryDesigns();
        } catch (IOException ex) {
             throw new RuntimeException(ex);
        } catch (JAXBException ex) {
             throw new RuntimeException(ex);
        }
    }

    @Override
    public List<ExperimentSpotDesignReadSpec> getExperimentSpotDesignReadSpecs() {
        try {
            return ll.findExperimentSpotDesignReadSpecs();
        } catch (IOException ex) {
             throw new RuntimeException(ex);
        } catch (JAXBException ex) {
             throw new RuntimeException(ex);
        }
    }

    @Override
    public List<ExperimentSpotDesign> getExperimentSpotDesigns() {
        try {
            return ll.findExperimentSpotDesigns();
        } catch (IOException ex) {
             throw new RuntimeException(ex);
        } catch (JAXBException ex) {
             throw new RuntimeException(ex);
        }
    }

    @Override
    public Experiment getExperiment(int swAccession) {
        try {
            return ll.findExperiment("/" + swAccession);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    /*
     * public void annotateFile(int fileSWID, FileAttribute att, Boolean skip) {
     * try { Log.debug("Annotating WorkflowRun " + fileSWID + " with skip=" +
     * skip + ", Att = " + att); File obj = ll.findFile("/" + fileSWID); if
     * (skip != null) { // obj.setSkip(skip); Log.info("Processing does not have
     * a skip column!"); } if (att != null) { Set<FileAttribute> atts =
     * obj.getFileAttributes(); if (atts == null) { atts = new
     * HashSet<FileAttribute>(); } // att.setStudy(obj); atts.add(att);
     * obj.setFileAttributes(atts); } ll.updateFile("/" + fileSWID, obj); }
     * catch (IOException ex) { Log.error("IOException while updating study " +
     * fileSWID + " " + ex.getMessage()); } catch (JAXBException ex) {
     * Log.error("JAXBException while updating study " + fileSWID + " " +
     * ex.getMessage()); } catch (ResourceException ex) {
     * Log.error("ResourceException while updating study " + fileSWID + " " +
     * ex.getMessage()); }
     *
     * }
     */
    protected class LowLevel {

        private ClientResource resource;

        public LowLevel(String database, String username, String password) {
            Client client = null;
            if (database.contains("https")) {
                try {
                    Log.info("using HTTPS connection");
                    client = new Client(new Context(), Protocol.HTTPS);
                    TrustManager tm = new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            Log.debug("checkClientTrusted");
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            Log.debug("getAcceptedIssuers");
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            Log.debug("checkServerTrusted");
                            // This will never throw an exception.
                            // This doesn't check anything at all: it's insecure.
                        }
                    };
                    // Engine.getInstance().getRegisteredClients()
                    // .add(0, new org.restlet.ext.httpclient.HttpClientHelper(null));

                    final SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{tm}, null);
                    Context context = client.getContext();
                    context.getAttributes().put("sslContextFactory", new SslContextFactory() {

                        @Override
                        public void init(Series<Parameter> parameters) {
                            Log.debug("sslcontextfactory init");
                        }

                        @Override
                        public SSLContext createSslContext() {
                            Log.debug("createsslcontext");
                            return sslContext;
                        }
                    });
                } catch (KeyManagementException ex) {
                    Log.fatal(ex);
                } catch (NoSuchAlgorithmException ex) {
                    Log.fatal(ex);
                }

            } else {
                Log.info("using HTTP connection");
                client = new Client(new Context(), Protocol.HTTP);
            }
            client.getContext().getParameters().add("useForwardedForHeader", "false");
            client.getContext().getParameters().add("maxConnectionsPerHost", "100");

            String[] pathElements = database.split("/");
            resource = new ClientResource(database);
            resource.setNext(client);
            resource.setFollowingRedirects(false);
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
            version = "";
            if (pathElements.length > 3) {
                for (int i = 3; i < pathElements.length; i++) {
                    version += "/" + pathElements[i];
                }
            }

            // version = (pathElements.length > 3 ? "/" +
            // pathElements[pathElements.length - 1] : "");
            Log.debug("Database string is " + database);
            Log.debug("root is " + resource);
            Log.debug("Version is " + version);
        }

        public Processing existsProcessing(String searchString) {
            Processing processing = null;
            try {
                processing = findProcessing(searchString);
            } catch (IOException ex) {
                Log.debug("Processing does not exist. Continuing.");
            } catch (JAXBException ex) {
                Log.error(ex);
            } catch (NotFoundException ex) {
                Log.debug("Processing does not exist. Continuing.");
            }
            return processing;
        }

        public Lane existsLane(String searchString) {
            Lane lane = null;
            try {
                lane = findLane(searchString);
            } catch (IOException ex) {
                Log.debug("Lane does not exist. Continuing.");
            } catch (JAXBException ex) {
                Log.error(ex);
            } catch (NotFoundException ex) {
                Log.debug("Lane does not exist. Continuing.");
            }
            return lane;
        }

        public IUS existsIUS(String searchString) {
            IUS ius = null;
            try {
                ius = findIUS(searchString);
            } catch (IOException ex) {
                Log.debug("IUS does not exist. Continuing.");
            } catch (JAXBException ex) {
                Log.error(ex);
            } catch (NotFoundException ex) {
                Log.debug("IUS does not exist. Continuing.");
            }
            return ius;
        }

        public SequencerRun existsSequencerRun(String searchString) {
            SequencerRun sr = null;
            try {
                sr = findSequencerRun(searchString);
            } catch (IOException ex) {
                Log.debug("SequencerRun does not exist. Continuing.");
            } catch (JAXBException ex) {
                Log.error(ex);
            } catch (NotFoundException ex) {
                Log.debug("SequencerRun does not exist. Continuing.");
            }
            return sr;
        }

        public Study existsStudy(String searchString) {
            Study study = null;
            try {
                study = findStudy(searchString);
            } catch (IOException ex) {
                Log.debug("Study does not exist. Continuing.");
            } catch (JAXBException ex) {
                Log.error(ex);
            } catch (NotFoundException ex) {
                Log.debug("Study does not exist. Continuing.");
            }
            return study;
        }

        public Experiment existsExperiment(String searchString) {
            Experiment experiment = null;
            try {
                experiment = findExperiment(searchString);
            } catch (IOException ex) {
                Log.debug("Experiment does not exist. Continuing.");
            } catch (JAXBException ex) {
                Log.error(ex);
            } catch (NotFoundException ex) {
                Log.debug("Experiment does not exist. Continuing.");
            }
            return experiment;
        }

        public Sample existsSample(String searchString) {
            Sample sample = null;
            try {
                sample = findSample(searchString);
            } catch (IOException ex) {
                Log.debug("Sample does not exist. Continuing.");
            } catch (NotFoundException ex) {
                Log.debug("Sample does not exist. Continuing.");
            } catch (JAXBException ex) {
                Log.error(ex);
            }
            return sample;
        }

        public Object existsObject(String uri, String searchString, JaxbObject jaxbObject, Object parent) throws JAXBException {
            Object object = parent;
            try {
                object = getObject(uri, searchString, jaxbObject, parent);
            } catch (SAXException ex) {
                Log.error("Error decoding message from server for query: " + uri + searchString, ex);
            } catch (IOException ex) {
                Log.debug("Resource at " + uri + searchString + " does not exist. Continuing.", ex);
            } catch (ResourceException ex) {
                Log.debug("Resource at " + uri + searchString + " does not exist. Continuing.", ex);
            }
            return object;
        }

        public void clean_up() {
            if (resource != null) {
                resource.release();
            }
        }

        private List<Study> findStudies() throws IOException, JAXBException {
            JaxbObject<StudyList> jaxb = new JaxbObject<StudyList>();
            StudyList list = (StudyList) findObject("/studies", "", jaxb, new StudyList());
            if (list !=null)
		return list.getList();
	    return null;
        }

        private List<Experiment> findExperiments() throws IOException, JAXBException {
            return findExperiments("/experiments");
        }

        private List<Experiment> findExperiments(String searchString) throws IOException, JAXBException {
            JaxbObject<ExperimentList> jaxb = new JaxbObject<ExperimentList>();
            ExperimentList list = (ExperimentList) findObject(searchString, "", jaxb, new ExperimentList());
            if (list !=null)
		return list.getList();
	    return null;
        }

        private List<SequencerRun> findSequencerRuns() throws IOException, JAXBException {
            JaxbObject<SequencerRunList> jaxb = new JaxbObject<SequencerRunList>();
            SequencerRunList list = (SequencerRunList) findObject("/sequencerruns", "", jaxb, new SequencerRunList());
            if (list !=null)
                return list.getList();
            return null;
	}

        private List<Workflow> findWorkflows() throws IOException, JAXBException {
            JaxbObject<WorkflowList> jaxb = new JaxbObject<WorkflowList>();
            WorkflowList list = (WorkflowList) findObject("/workflows", "", jaxb, new WorkflowList());
            if (list !=null)
                return list.getList();
            return null;
	}

        private Workflow findWorkflowParams(String workflowAccession) throws IOException, JAXBException {
            JaxbObject<Workflow> jaxb = new JaxbObject<Workflow>();
            Workflow list = (Workflow) findObject("/workflows", "/" + workflowAccession + "?show=params", jaxb,
                    new Workflow());
            return list;
        }

        private List<ExperimentLibraryDesign> findExperimentLibraryDesigns() throws IOException, JAXBException {
            JaxbObject<ExperimentLibraryDesignList> jaxb = new JaxbObject<ExperimentLibraryDesignList>();
            ExperimentLibraryDesignList list = (ExperimentLibraryDesignList) findObject("/experimentlibrarydesigns", "", jaxb, new ExperimentLibraryDesignList());
            if (list != null) {
                return list.getList();
            }
            return null;
        }

        private List<ExperimentSpotDesignReadSpec> findExperimentSpotDesignReadSpecs() throws IOException, JAXBException {
            JaxbObject<ExperimentSpotDesignReadSpecList> jaxb = new JaxbObject<ExperimentSpotDesignReadSpecList>();
            ExperimentSpotDesignReadSpecList list = (ExperimentSpotDesignReadSpecList) findObject("/experimentspotdesignreadspecs", "", jaxb, new ExperimentSpotDesignReadSpecList());
            if (list != null) {
                return list.getList();
            }
            return null;
        }

        private List<ExperimentSpotDesign> findExperimentSpotDesigns() throws IOException, JAXBException {
            JaxbObject<ExperimentSpotDesignList> jaxb = new JaxbObject<ExperimentSpotDesignList>();
            ExperimentSpotDesignList list = (ExperimentSpotDesignList) findObject("/experimentspotdesigns", "", jaxb, new ExperimentSpotDesignList());
            if (list != null) {
                return list.getList();
            }
            return null;
        }
        
        private List<Platform> findPlatforms() throws IOException, JAXBException {
            JaxbObject<PlatformList> jaxb = new JaxbObject<PlatformList>();
            PlatformList list = (PlatformList) findObject("/platforms", "", jaxb, new PlatformList());
	    if (list !=null)
                return list.getList();
            return null;            
        }

        private List<Organism> findOrganisms() throws IOException, JAXBException {
            JaxbObject<OrganismList> jaxb = new JaxbObject<OrganismList>();
            OrganismList list = (OrganismList) findObject("/organisms", "", jaxb, new OrganismList());
            if (list !=null)
                return list.getList();
            return null; 
        }

        private List<StudyType> findStudyTypes() throws IOException, JAXBException {
            JaxbObject<StudyTypeList> jaxb = new JaxbObject<StudyTypeList>();
            StudyTypeList list = (StudyTypeList) findObject("/studytypes", "", jaxb, new StudyTypeList());
            if (list !=null)
                return list.getList();
            return null;
	}

        private List<LibraryStrategy> findLibraryStrategies() throws IOException, JAXBException {
            JaxbObject<LibraryStrategyList> jaxb = new JaxbObject<LibraryStrategyList>();
            LibraryStrategyList list = (LibraryStrategyList) findObject("/librarystrategies", "", jaxb, new LibraryStrategyList());
            if (list !=null)
                return list.getList();
            return null;
	}

        private List<LibrarySelection> findLibrarySelections() throws IOException, JAXBException {
            JaxbObject<LibrarySelectionList> jaxb = new JaxbObject<LibrarySelectionList>();
            LibrarySelectionList list = (LibrarySelectionList) findObject("/libraryselections", "", jaxb, new LibrarySelectionList());
            if (list !=null)
                return list.getList();
            return null;
	}

        private List<LibrarySource> findLibrarySources() throws IOException, JAXBException {
            JaxbObject<LibrarySourceList> jaxb = new JaxbObject<LibrarySourceList>();
            LibrarySourceList list = (LibrarySourceList) findObject("/librarysources", "", jaxb, new LibrarySourceList());
            if (list !=null)
                return list.getList();
            return null;
	}

        private Processing findProcessing(String searchString) throws IOException, JAXBException {
            Processing parent = new Processing();
            JaxbObject<Processing> jaxbProcess = new JaxbObject<Processing>();
            return (Processing) findObject("/processes", searchString, jaxbProcess, parent);
        }

        private Lane findLane(String searchString) throws IOException, JAXBException {
            Lane lane = new Lane();
            JaxbObject<Lane> jaxb = new JaxbObject<Lane>();
            return (Lane) findObject("/lanes", searchString, jaxb, lane);
        }

        private IUS findIUS(String searchString) throws IOException, JAXBException {
            IUS ius = new IUS();
            JaxbObject<IUS> jaxb = new JaxbObject<IUS>();
            return (IUS) findObject("/ius", searchString, jaxb, ius);
        }

        private WorkflowRun findWorkflowRun(String searchString) throws IOException, JAXBException {
            WorkflowRun wr = new WorkflowRun();
            JaxbObject<WorkflowRun> jaxb = new JaxbObject<WorkflowRun>();
            return (WorkflowRun) findObject("/workflowruns", searchString, jaxb, wr);
        }

        private Workflow findWorkflowByWorkflowRun(String workflowRunAccession) throws IOException, JAXBException {
            Workflow w = new Workflow();
            JaxbObject<Workflow> jaxb = new JaxbObject<Workflow>();
            return (Workflow) findObject("/workflowruns", "/" + workflowRunAccession + "/workflow", jaxb, w);
        }
        
        private List<WorkflowRun> findWorkflowByFiles(List<Integer> files) throws IOException, JAXBException {
            WorkflowRunList2 w = new WorkflowRunList2();
            JaxbObject<WorkflowRunList2> jaxb = new JaxbObject<WorkflowRunList2>();
            String fileList = StringUtils.join(files.iterator(),',');
            WorkflowRunList2 wrl2 = (WorkflowRunList2) findObject("/reports/fileworkflowruns", "?files=" + fileList + "&DIRECT_SEARCH=true", jaxb, w);
            return wrl2.getList();
        }

        private List<WorkflowRun> findWorkflowByFiles(List<Integer> files, String search_type) throws IOException, JAXBException {
            WorkflowRunList2 w = new WorkflowRunList2();
            JaxbObject<WorkflowRunList2> jaxb = new JaxbObject<WorkflowRunList2>();
            String fileList = StringUtils.join(files.iterator(),',');
            WorkflowRunList2 wrl2 = (WorkflowRunList2) findObject("/reports/fileworkflowruns", "?files=" + fileList + "&search=" + search_type, jaxb, w);
            return wrl2.getList();
        }

        private List<WorkflowRun> findWorkflowRuns(String searchString) throws IOException, JAXBException {
            WorkflowRunList2 wrl = new WorkflowRunList2();
            JaxbObject<WorkflowRunList2> jaxb = new JaxbObject<WorkflowRunList2>();
            WorkflowRunList2 wrl2 = (WorkflowRunList2) findObject("/workflowruns", searchString, jaxb, wrl);
            return wrl2.getList();
        }

        private Workflow findWorkflow(String searchString) throws IOException, JAXBException {
            Workflow wr = new Workflow();
            JaxbObject<Workflow> jaxb = new JaxbObject<Workflow>();
            return (Workflow) findObject("/workflows", searchString, jaxb, wr);
        }

        private SequencerRun findSequencerRun(String searchString) throws IOException, JAXBException {
            SequencerRun study = new SequencerRun();
            JaxbObject<SequencerRun> jaxb = new JaxbObject<SequencerRun>();
            return (SequencerRun) findObject("/sequencerruns", searchString, jaxb, study);
        }

        private Study findStudy(String searchString) throws IOException, JAXBException {
            Study study = new Study();
            JaxbObject<Study> jaxb = new JaxbObject<Study>();
            return (Study) findObject("/studies", searchString, jaxb, study);
        }

        private Sample findSample(String searchString) throws IOException, JAXBException {
            Sample study = new Sample();
            JaxbObject<Sample> jaxb = new JaxbObject<Sample>();
            return (Sample) findObject("/samples", searchString, jaxb, study);
        }

        private Experiment findExperiment(String searchString) throws IOException, JAXBException {
            Experiment exp = new Experiment();
            JaxbObject<Experiment> jaxb = new JaxbObject<Experiment>();
            return (Experiment) findObject("/experiments", searchString, jaxb, exp);
        }

        private File findFile(String searchString) throws IOException, JAXBException {
            File study = new File();
            JaxbObject<File> jaxb = new JaxbObject<File>();
            return (File) findObject("/files", searchString, jaxb, study);
        }

        private Platform findPlatform(String searchString) throws IOException, JAXBException {
            Platform p = new Platform();
            JaxbObject<Platform> jaxb = new JaxbObject<Platform>();
            return (Platform) findObject("/platforms", searchString, jaxb, p);
        }

        private StudyType findStudyType(String searchString) throws IOException, JAXBException {
            StudyType st = new StudyType();
            JaxbObject<StudyType> jaxb = new JaxbObject<StudyType>();
            return (StudyType) findObject("/studytypes", searchString, jaxb, st);
        }

        private LibraryStrategy findLibraryStrategy(String searchString) throws IOException, JAXBException {
            LibraryStrategy ls = new LibraryStrategy();
            JaxbObject<LibraryStrategy> jaxb = new JaxbObject<LibraryStrategy>();
            return (LibraryStrategy) findObject("/librarystrategies", searchString, jaxb, ls);
        }

        private LibrarySelection findLibrarySelection(String searchString) throws IOException, JAXBException {
            LibrarySelection ls = new LibrarySelection();
            JaxbObject<LibrarySelection> jaxb = new JaxbObject<LibrarySelection>();
            return (LibrarySelection) findObject("/libraryselections", searchString, jaxb, ls);
        }

        private LibrarySource findLibrarySource(String searchString) throws IOException, JAXBException {
            LibrarySource ls = new LibrarySource();
            JaxbObject<LibrarySource> jaxb = new JaxbObject<LibrarySource>();
            return (LibrarySource) findObject("/librarysource", searchString, jaxb, ls);
        }
        
        private void writeTo(String url, Writer out){
          ClientResource cResource = resource.getChild(version + url);
          Representation result = cResource.get();
          try {
            Reader in = result.getReader();
            try {
              IOUtils.copy(in, out);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }

          } catch (IOException e) {
            throw new RuntimeException(e);
          } finally {
            try {
              result.exhaust();
            } catch (IOException e) {
            }
            result.release();
            resource.release();
          }
        }

        private String getString(String uri) {
            String text = null;
            Representation result = null;
            ClientResource cResource = resource.getChild(version + uri);

            try {
                result = cResource.get();
                text = result.getText();

            } catch (Exception ex) {
                Log.error("MetadataWS.getString " + ex.getMessage());
            } finally {
                if (result != null) {
                    try {
                        result.exhaust();
                    } catch (IOException ex) {
                        Log.error(ex);
                    }
                    result.release();
                }
                if (cResource.getResponseEntity() != null) {
                    cResource.getResponseEntity().release();
                }
                cResource.release();

            }
            return (text);
        }

        /**
         * Use percent sign to designate what should be matched.
         *
         * Eg. SAMPLE_1% will match SAMPLE_1_001 and SAMPLE_1_002 and SAMPLE_1
         *
         *
         * @param name
         * @return
         * @throws IOException
         * @throws JAXBException
         */
        private List<Sample> matchSampleName(String name) throws IOException, JAXBException {
            JaxbObject<SampleList> jaxb = new JaxbObject<SampleList>();
            SampleList list = (SampleList) findObject("/samples", "?matches=" + name, jaxb, new SampleList());
	    if (list !=null)
                return list.getList();
            return null;            
        }

        private Object findObject(String uri, String searchString, JaxbObject jaxb, Object parent)
                throws IOException, NotFoundException {

            Class clazz = parent.getClass();
            try {
                parent = getObject(uri, searchString, jaxb, parent);
            } catch (SAXException e) {
                Log.error("Error parsing response from server with query:" + uri + searchString, e);
            } catch (ResourceException e) {
                if (!e.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND)) {
                    Log.error("Server error with query:" + uri + searchString, e);
                    throw e;
                } else {
                    parent=null;
                }
            }
            // SEQWARE-1331, this was commented out for some reason, but that causes the checking of incorrect accessions to fail
            testNull(parent, clazz, searchString);
            return parent;
        }

        private Object getObject(String uri, String searchString, JaxbObject jaxb, Object parent)
                throws IOException, SAXException {
            Representation result = null;
            ClientResource cResource = resource.getChild(version + uri + searchString);
            try {
                Log.info("getObject: " + cResource);
                result = cResource.get();
                String text = result.getText();
                parent = XmlTools.unMarshal(jaxb, parent, text);
            } catch (SAXException ex) {
                Log.error("MetadataWS.findObject with search string " + searchString + " encountered error " + ex.getMessage());
                ex.printStackTrace();
                parent = null;
            } catch (ResourceException e) {
                // note: this happens ona  regular basis with calls that attempt to locate the same sw_accession in either lane or IUS, making this less vocal
                Log.info("MetadataWS.findObject with search string " + searchString + " encountered error " + e.getMessage());
                Log.info(" please check that the object you are looking for exists in the MetaDB");

                parent = null;
            } finally {
                if (result != null) {
                    result.exhaust();
                    result.release();
                }
                if (cResource.getResponseEntity() != null) {
                    cResource.getResponseEntity().release();
                }
                cResource.release();

            }
            return parent;
        }

        private void updateWorkflow(String searchString, Workflow parent) throws IOException, JAXBException,
                ResourceException {
            JaxbObject<Workflow> jaxbProcess = new JaxbObject<Workflow>();
            updateObject("/workflows", searchString, jaxbProcess, parent);
        }

        private void updateProcessing(String searchString, Processing parent) throws IOException, JAXBException,
                ResourceException {
            JaxbObject<Processing> jaxbProcess = new JaxbObject<Processing>();
            updateObject("/processes", searchString, jaxbProcess, parent);
        }

        private void updateWorkflowRun(String searchString, WorkflowRun parent) throws IOException, JAXBException,
                ResourceException {
            JaxbObject<WorkflowRun> jaxb = new JaxbObject<WorkflowRun>();
            updateObject("/workflowruns", searchString, jaxb, parent);
        }

        private void updateLane(String searchString, Lane parent) throws IOException, JAXBException, ResourceException {
            JaxbObject<Lane> jaxb = new JaxbObject<Lane>();
            updateObject("/lanes", searchString, jaxb, parent);
        }

        private void updateIUS(String searchString, IUS parent) throws IOException, JAXBException, ResourceException {
            JaxbObject<IUS> jaxb = new JaxbObject<IUS>();
            updateObject("/ius", searchString, jaxb, parent);
        }

        private void updateSequencerRun(String searchString, SequencerRun parent) throws IOException, JAXBException,
                ResourceException {
            JaxbObject<SequencerRun> jaxb = new JaxbObject<SequencerRun>();
            updateObject("/sequencerruns", searchString, jaxb, parent);
        }

        private void updateSample(String searchString, Sample parent) throws IOException, JAXBException, ResourceException {
            JaxbObject<Sample> jaxb = new JaxbObject<Sample>();
            updateObject("/samples", searchString, jaxb, parent);
        }

        private void updateStudy(String searchString, Study parent) throws IOException, JAXBException, ResourceException {
            JaxbObject<Study> jaxb = new JaxbObject<Study>();
            updateObject("/studies", searchString, jaxb, parent);
        }

        
        private void updateFile(String searchString, File parent) throws IOException, JAXBException, ResourceException { 
            JaxbObject<File> jaxb = new JaxbObject<File>(); 
            updateObject("/files", searchString, jaxb, parent); 
        }
        
        private void updateExperiment(String searchString, Experiment parent) throws IOException, JAXBException,
                ResourceException {
            JaxbObject<Experiment> jaxb = new JaxbObject<Experiment>();
            updateObject("/experiments", searchString, jaxb, parent);
        }

        private void updateObject(String uri, String searchString, JaxbObject jaxb, Object parent) throws IOException,
                JAXBException, ResourceException {
            Representation result = null;
            Log.debug("Updating object: " + parent.getClass().getCanonicalName() + " " + searchString);
            ClientResource cResource = resource.getChild(version + uri + searchString);
            Log.debug("updateObject: " + cResource);
            try {
                Document text = XmlTools.marshalToDocument(jaxb, parent);
                result = cResource.put(XmlTools.getRepresentation(text));
            } finally {
                if (result != null) {
                    result.exhaust();
                    result.release();
                }
                cResource.release();
            }
        }

        private Study addStudy(Study study) throws IOException, JAXBException, ResourceException {
            JaxbObject<Study> jaxb = new JaxbObject<Study>();
            return (Study) addObject("/studies", "", jaxb, study);
        }

        private Experiment addExperiment(Experiment o) throws IOException, JAXBException, ResourceException {
            JaxbObject<Experiment> jaxb = new JaxbObject<Experiment>();
            return (Experiment) addObject("/experiments", "", jaxb, o);
        }

        private Sample addSample(Sample o) throws IOException, JAXBException, ResourceException {
            JaxbObject<Sample> jaxb = new JaxbObject<Sample>();
            return (Sample) addObject("/samples", "", jaxb, o);
        }

        private Processing addProcessing(Processing processing) throws IOException, JAXBException, ResourceException {
            JaxbObject<Processing> jaxb = new JaxbObject<Processing>();
            return (Processing) addObject("/processes", "", jaxb, processing);
        }

        private File addFile(File workflowRun) throws IOException, JAXBException, ResourceException {
            JaxbObject<File> jaxb = new JaxbObject<File>();
            return (File) addObject("/files", "", jaxb, workflowRun);
        }

        private WorkflowRun addWorkflowRun(WorkflowRun workflowRun) throws IOException, JAXBException, ResourceException {
            JaxbObject<WorkflowRun> jaxb = new JaxbObject<WorkflowRun>();
            return (WorkflowRun) addObject("/workflowruns", "", jaxb, workflowRun);
        }

        private Workflow addWorkflow(Workflow workflow) throws IOException, JAXBException, ResourceException {
            JaxbObject<Workflow> jaxb = new JaxbObject<Workflow>();
            return (Workflow) addObject("/workflows", "", jaxb, workflow);
        }

        private WorkflowParam addWorkflowParam(WorkflowParam workflowParam) throws IOException, JAXBException,
                ResourceException {
            JaxbObject<WorkflowParam> jaxb = new JaxbObject<WorkflowParam>();
            return (WorkflowParam) addObject("/workflowparams", "", jaxb, workflowParam);
        }

        private WorkflowParamValue addWorkflowParamValue(WorkflowParamValue workflowParamVal) throws IOException,
                JAXBException, ResourceException {
            JaxbObject<WorkflowParamValue> jaxb = new JaxbObject<WorkflowParamValue>();
            return (WorkflowParamValue) addObject("/workflowparamvalues", "", jaxb, workflowParamVal);
        }

        private SequencerRun addSequencerRun(SequencerRun sequencerRun) throws IOException,
                JAXBException, ResourceException {
            JaxbObject<WorkflowRun> jaxb = new JaxbObject<WorkflowRun>();
            return (SequencerRun) addObject("/sequencerruns", "", jaxb, sequencerRun);
        }

        private Lane addLane(Lane lane) throws IOException,
                JAXBException, ResourceException {
            JaxbObject<Lane> jaxb = new JaxbObject<Lane>();
            return (Lane) addObject("/lanes", "", jaxb, lane);
        }

        private IUS addIUS(IUS ius) throws IOException,
                JAXBException, ResourceException {
            JaxbObject<IUS> jaxb = new JaxbObject<IUS>();
            return (IUS) addObject("/ius", "", jaxb, ius);
        }

        private Object addObject(String uri, String searchString, JaxbObject jaxb, Object parent) throws IOException,
                JAXBException, ResourceException {
            Representation result = null;
            ClientResource cResource = resource.getChild(version + uri + searchString);
            Log.debug("addObject: " + cResource);
            Document s = XmlTools.marshalToDocument(jaxb, parent);
            try {
                result = cResource.post(XmlTools.getRepresentation(s));
                if (result != null) {
                    String text = result.getText();
                    if (text == null) {
                        Log.warn("WARNING: POST process returned a null value. The object may not have been created");
                    } else {
                        try {
                            Log.debug("addObject:" + text);
                            parent = XmlTools.unMarshal(jaxb, parent, text);
                        } catch (SAXException ex) {
                            throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
                        }
                    }
                }
            } catch (ResourceException e) {
                Log.error("MetadataWS.addObject " + e.getMessage());
                e.printStackTrace();
            }
            if (result != null) {
                result.exhaust();
                result.release();
            }
            cResource.release();
            return parent;
        }
    }

    @Override
    public String getProcessingRelations(String swAccession) {
        String report = (String) ll.getString("/processingstructure?swAccession=" + swAccession);
        return report;
    }
    
    private boolean isValidModelId(final List<? extends SecondTierModel> models, final int modelId) {
        boolean modelFound = false;
        for (SecondTierModel organism : models) {
            if (organism.getModelId() == modelId) {
                modelFound = true;
            }
        }
        return modelFound;
    }
}
