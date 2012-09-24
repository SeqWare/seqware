package net.sourceforge.seqware.pipeline.bundle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sourceforge.seqware.common.util.Log;

import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BundleInfo {

  private ArrayList<WorkflowInfo> workflows = new ArrayList<WorkflowInfo>();

  public void parseFromFile(File metadata) {

    try {

      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(metadata);
      doc.getDocumentElement().normalize();

      NodeList nList = doc.getElementsByTagName("workflow");

      for (int temp = 0; temp < nList.getLength(); temp++) {

        Node nNode = nList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

          Element eElement = (Element) nNode;

          String testCommand = eElement.getElementsByTagName("test").item(0).getAttributes().getNamedItem("command")
              .getNodeValue();

          String templatePath = eElement.getElementsByTagName("workflow_template").item(0).getAttributes()
              .getNamedItem("path").getNodeValue();

          String classesPath = null;
          if (null != eElement.getElementsByTagName("classes").item(0)) {
            classesPath = eElement.getElementsByTagName("classes").item(0).getAttributes().getNamedItem("path")
                .getNodeValue();
          }

          String configPath = eElement.getElementsByTagName("config").item(0).getAttributes().getNamedItem("path")
              .getNodeValue();

          String command = eElement.getElementsByTagName("workflow_command").item(0).getAttributes()
              .getNamedItem("command").getNodeValue();

          String computeReq = eElement.getElementsByTagName("requirements").item(0).getAttributes()
              .getNamedItem("compute").getNodeValue();
          String memReq = eElement.getElementsByTagName("requirements").item(0).getAttributes().getNamedItem("memory")
              .getNodeValue();
          String networkReq = eElement.getElementsByTagName("requirements").item(0).getAttributes()
              .getNamedItem("network").getNodeValue();

          WorkflowInfo wi = new WorkflowInfo();
          wi.setName(eElement.getAttribute("name"));
          wi.setVersion(eElement.getAttribute("version"));
          wi.setDescription(getTagValue("description", eElement));
          wi.setTestCmd(testCommand);
          wi.setTemplatePath(templatePath);
          wi.setConfigPath(configPath);
          wi.setComputeReq(computeReq);
          wi.setMemReq(memReq);
          wi.setNetworkReq(networkReq);
          wi.setCommand(command);
          wi.setClassesDir(classesPath);
          wi.setBaseDir(eElement.getAttribute("basedir"));
          wi.setWorkflowSqwVersion(eElement.getAttribute("seqware_version"));
          workflows.add(wi);

        }
      }

    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public ArrayList<WorkflowInfo> getWorkflowInfo() {
    return workflows;
  }

  public void setWorkflows(ArrayList<WorkflowInfo> workflows) {
    this.workflows = workflows;
  }

  private static String getTagValue(String sTag, Element eElement) {
    NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
    Node nValue = (Node) nlList.item(0);
    return nValue.getNodeValue();
  }

}
