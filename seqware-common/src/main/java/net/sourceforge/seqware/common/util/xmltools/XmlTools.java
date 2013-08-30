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

/**
 * <p>XmlTools class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
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
    
    public static boolean prettyPrint(Document doc, OutputStream out) {

        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            serializer.transform(new DOMSource(doc), new StreamResult(out));
        } catch (TransformerException e) {
            // this is fatal, just dump the stack and throw a runtime exception
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        return true;
    }
    
    /**
     * <p>prettyPrint.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean prettyPrint(String filename){
      Logger.getLogger(XmlTools.class).info("start");
      // Parse input file
      DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
      Document inputDOM = null;
      try {
          DocumentBuilder parser = dFactory.newDocumentBuilder();
          inputDOM = parser.parse(new File(filename));
          return prettyPrint(inputDOM, new FileOutputStream(filename+ "prettyprint"));
      } catch (RuntimeException e){
          throw e;
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
      
    }


    public static String prettyPrintString(Document doc){
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      prettyPrint(doc, out);
      return new String(out.toByteArray());
    }
    
    public static String prettyPrintString(String xml){
      try {
        Document doc = XmlTools.getDocument(xml);
        return prettyPrintString(doc);
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    /**
     * <p>getRepresentation.</p>
     *
     * @param document a {@link org.w3c.dom.Document} object.
     * @return a {@link org.restlet.ext.xml.DomRepresentation} object.
     */
    public static DomRepresentation getRepresentation(Document document) {
        return new DomRepresentation(MediaType.APPLICATION_XML, document);
    }

    /**
     * <p>marshalToDocument.</p>
     *
     * @param jaxbTool a {@link net.sourceforge.seqware.common.util.xmltools.JaxbObject} object.
     * @param o a {@link java.lang.Object} object.
     * @return a {@link org.w3c.dom.Document} object.
     */
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

    /**
     * <p>unMarshal.</p>
     *
     * @param jaxbTool a {@link net.sourceforge.seqware.common.util.xmltools.JaxbObject} object.
     * @param expectedType a {@link java.lang.Object} object.
     * @param string a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
     * @throws org.xml.sax.SAXException if any.
     */
    public static Object unMarshal(JaxbObject jaxbTool, Object expectedType, String string) throws SAXException {
        Object o = null;
        try {
            // SEQWARE-1549
            if (string == null) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
            }
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
