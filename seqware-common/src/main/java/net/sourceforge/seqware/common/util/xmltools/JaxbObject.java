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
package net.sourceforge.seqware.common.util.xmltools;

import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.model.lists.*;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

/**
 * Convenience class for converting objects into JAXB XML.
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class JaxbObject<T> {

    private static JAXBContext context = null;

    /**
     * JAXB has a memory leak when it comes to making new JAXBContext instances,
     * so one JAXBContext is created for the entire lifetime of the program and
     * initialized with all of the classes that we marshal and unmarshal at the
     * moment, as per the instructions at *
     * http://whileonefork.blogspot.com/2010/09/leaking-of-jaxb.html and *
     * http://jaxb.java.net/guide/Performance_and_thread_safety.html . In order
     * to marshall or unmarshall new objects, they must be added to the context
     * creation in this constructor.
     */
    public JaxbObject() {
        try {
            if (context == null) {
                context = JAXBContext.newInstance(Experiment.class,
                                                ExperimentAttribute.class, //ExperimentLibraryDesign.class, ExperimentLink.class,
                        //                        ExperimentSpotDesign.class, ExperimentSpotDesignReadSpec.class,
                        File.class,
                        FileType.class, IUS.class, IUSAttribute.class, IUSLink.class, Lane.class,
                        LaneAttribute.class, LaneLink.class, LibrarySelection.class, LibrarySource.class,
                        LibraryStrategy.class, Organism.class, Platform.class, Processing.class,
                        ProcessingAttribute.class,
                        //                        ProcessingExperiments.class, ProcessingIus.class,
                        //                        ProcessingLanes.class, ProcessingRelationship.class, ProcessingSamples.class,
                        //                        ProcessingSequencerRuns.class, ProcessingStudies.class,
                        Registration.class, Sample.class, SampleAttribute.class, SampleLink.class,
                        SequencerRun.class, SequencerRunWizardDTO.class,
                        //                        ShareExperiment.class, ShareFile.class, ShareLane.class,
                        //                        ShareProcessing.class, ShareSample.class, ShareStudy.class, ShareWorkflowRun.class,
                        Study.class, StudyAttribute.class, StudyLink.class, StudyType.class,
                        Workflow.class, WorkflowParam.class, WorkflowParamValue.class,
                        WorkflowRun.class, WorkflowRunParam.class,
                        ExperimentList.class, FileList.class, IUSList.class, LaneList.class, 
                        LibrarySelectionList.class, LibrarySourceList.class, LibraryStrategyList.class,
                        OrganismList.class, PlatformList.class, ProcessingList.class,
                        ReturnValueList.class, SampleList.class, SequencerRunList.class, StudyList.class,
                        StudyTypeList.class, WorkflowList.class, WorkflowRunList.class, WorkflowRunList2.class,
                        WorkflowParamList.class, WorkflowParamValueList.class, ArrayList.class);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Turn an object into XML using JAXB and provide the result in a
     * StreamResult.
     *
     * @param t The object to XMLize.
     * @return the XML. The StreamResult was created with a StringWriter, which
     * can be used to retrieve the XML.
     * @throws javax.xml.bind.JAXBException if any.
     */
    public Document marshalToDocument(T t) throws JAXBException {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.newDocument();

            T object = t;

            //get the XML
//            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller m = context.createMarshaller();
            m.marshal(new JAXBElement(new QName(object.getClass().getSimpleName()), object.getClass(), object), doc);

//            try {
//                XmlTools.getDocument(output);
//            } catch (Exception ex) {
//                Log.info("Exception while marshaling: " + ex.getMessage() + ". Trying again.");
//                output = marshal(t);
//            }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(JaxbObject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException jbe) {
            jbe.printStackTrace();
            throw jbe;
        }
        return doc;
    }

    /**
     * Turn an object into XML using JAXB and provide the result in a
     * StreamResult.
     *
     * @param t The object to XMLize.
     * @return the XML. The StreamResult was created with a StringWriter, which
     * can be used to retrieve the XML.
     * @throws javax.xml.bind.JAXBException if any.
     */
    public String marshal(T t) throws JAXBException {
        String output = null;
        try {
            StreamResult result = new StreamResult(new StringWriter());
            T object = t;

            //get the XML
//            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller m = context.createMarshaller();
            m.marshal(new JAXBElement(new QName(object.getClass().getSimpleName()), object.getClass(), object), result);

            //convert to String
            StringWriter writer = (StringWriter) result.getWriter();
            StringBuffer buffer = writer.getBuffer();
            output = buffer.toString();

//            try {
//                XmlTools.getDocument(output);
//            } catch (Exception ex) {
//                Log.info("Exception while marshaling: " + ex.getMessage() + ". Trying again.");
//                output = marshal(t);
//            }

        } catch (JAXBException jbe) {
            jbe.printStackTrace();
            throw jbe;
        }
        return output;
    }

    /**
     * Turn an XML stream into an object, if possible.
     *
     * @param expectedType a T object.
     * @param reader a {@link java.io.Reader} object.
     * @return a T object.
     * @throws javax.xml.bind.JAXBException if any.
     */
    public T unMarshal(T expectedType, Reader reader) throws JAXBException {
        T object = null;
        try {
//            JAXBContext context = JAXBContext.newInstance(expectedType.getClass());
            Unmarshaller m = context.createUnmarshaller();
            JAXBElement o = m.unmarshal(new StreamSource(reader), expectedType.getClass());
            object = (T) o.getValue();
        } catch (JAXBException jbe) {
            jbe.printStackTrace();
            throw jbe;
        }
        return object;
    }

    /**
     * Turn an XML stream into an object, if possible.
     *
     * @param expectedType a T object.
     * @param d a {@link org.w3c.dom.Document} object.
     * @return a T object.
     * @throws javax.xml.bind.JAXBException if any.
     */
    public T unMarshal(Document d, T expectedType) throws JAXBException {
        T object = null;
        try {
//            JAXBContext context = JAXBContext.newInstance(expectedType.getClass());
            Unmarshaller m = context.createUnmarshaller();
            JAXBElement o = m.unmarshal(d, expectedType.getClass());
            object = (T) o.getValue();
        } catch (JAXBException jbe) {
            jbe.printStackTrace();
            throw jbe;
        }
        return object;
    }
}
