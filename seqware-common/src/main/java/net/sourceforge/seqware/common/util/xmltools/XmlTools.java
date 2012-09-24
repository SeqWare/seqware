package net.sourceforge.seqware.common.util.xmltools;

import java.io.*;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlTools {

    private static Document getDocument(String string) throws ParserConfigurationException, IOException, SAXException {
        Document document;
        if (string == null) {
            Logger.getLogger(XmlTools.class).debug("String is :" + string);
            return null;
        }
        //UTF-8 Encoded strings occasionally have silly byte-order marks
        //Solution from http://mark.koli.ch/2009/02/resolving-orgxmlsaxsaxparseexception-content-is-not-allowed-in-prolog.html
        string = string.trim().replaceFirst("^([\\W]+)<", "<");

        document = DocumentBuilderFactory.newInstance().
                newDocumentBuilder().parse(new ByteArrayInputStream(string.getBytes()));
        return document;
    }

//    public static boolean validateXMLFile(String filename) {
//        
//        return true;
//    }
    public static boolean prettyPrint(String filename) {
        Logger.getLogger(XmlTools.class).info("start");
        // Parse input file
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        Document inputDOM = null;
        try {
            DocumentBuilder parser = dFactory.newDocumentBuilder();
            inputDOM = parser.parse(new File(filename));
        } catch (SAXException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            filename = filename + "prettyprint";
            serializer.transform(new DOMSource(inputDOM), new StreamResult(new FileOutputStream(filename)));
        } catch (TransformerException e) {
            // this is fatal, just dump the stack and throw a runtime exception
            e.printStackTrace();

            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static DomRepresentation getRepresentation(Document document) {
        return new DomRepresentation(MediaType.APPLICATION_XML, document);
    }

    public static Document marshalToDocument(JaxbObject jaxbTool, Object o) {
        Document doc = null;
        try {
            doc = jaxbTool.marshalToDocument(o);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return doc;
    }

//    public static String marshal(JaxbObject jaxbTool, Object o) {
//        StringBuilder builder = new StringBuilder();
//        try {
//            String line = jaxbTool.marshal(o);
//            builder.append(line);
//        } catch (JAXBException e) {
//            builder.append(o.toString());
//            builder.append(" could not be converted to XML");
//            e.printStackTrace();
//        }
//        String xml = builder.toString();
//        builder = null;
//        return xml;
//    }

    public static Object unMarshal(JaxbObject jaxbTool, Object expectedType, String string) throws SAXException {
        Object o = null;
        try {
            Document document = XmlTools.getDocument(string);
            o = jaxbTool.unMarshal(document, expectedType);


        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
        }
        return o;
    }
}
