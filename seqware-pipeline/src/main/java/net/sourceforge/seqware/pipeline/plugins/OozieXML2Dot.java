package net.sourceforge.seqware.pipeline.plugins;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.openide.util.lookup.ServiceProvider;


@ServiceProvider(service=PluginInterface.class)
public class OozieXML2Dot extends Plugin {

	private static Namespace NAMESPACE = Namespace.getNamespace("uri:oozie:workflow:0.2");
	private String input;
	private String output;
	
	private ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS); 
	
	public OozieXML2Dot() {
		super();
		parser
        .accepts(
            "input")
        .withRequiredArg().ofType(String.class).describedAs("input XML file");
		parser
        .accepts(
            "output")
        .withRequiredArg().ofType(String.class).describedAs("output dot file");

	}
	
	@Override
	public ReturnValue init() {
		this.input = (String)options.valueOf("input");
		this.output = (String)options.valueOf("output");
		if(!FileTools.fileExistsAndNotEmpty(new File(this.input)).equals(ReturnValue.SUCCESS))
			ret.setReturnValue(ReturnValue.INVALIDFILE);
		return ret;
	}

	@Override
	public ReturnValue do_test() {
		return ret;
	}

	@Override
	public ReturnValue do_run() {
		try {
			this.parseOozie(input, output);
		} catch (JDOMException e) {
			e.printStackTrace();
			ret.setReturnValue(ReturnValue.FAILURE);
		} catch (IOException e) {
			e.printStackTrace();
			ret.setReturnValue(ReturnValue.FAILURE);
		}
		return ret;
	}

	@Override
	public ReturnValue clean_up() {
		return ret;
	}
	
	
	private void parseOozie(String input, String output) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(input);
        Element root = doc.getRootElement();
        //start
        Element start = root.getChild("start", NAMESPACE);
        DotNode rootNode = new DotNode(start.getAttributeValue("to"));
        this.addSubNode(rootNode, root);
        
        //construct dot file
		
      	FileWriter fw = new FileWriter(output);
      	fw.write("digraph dag {\n");
      	//avoid duplicated
      	Set<String> allEdge = new HashSet<String>();
      	this.visitNode(rootNode, fw, allEdge);
      	fw.write("}\n");
      	fw.close();
	}
	
	private Element findElementByName(String name, Element root) {
		List<Element> actionelements = root.getChildren("action", NAMESPACE);
		for(Element element: actionelements) {
			if(element.getAttributeValue("name").equals(name))
				return element;
		}
		
		List<Element> forkElements = root.getChildren("fork", NAMESPACE);
		for(Element element: forkElements) {
			if(element.getAttributeValue("name").equals(name))
				return element;
		}	
		
		List<Element> joinElements = root.getChildren("join", NAMESPACE);
		for(Element element: joinElements) {
			if(element.getAttributeValue("name").equals(name))
				return element;
		}	
		if(name.equals("end"))
			return root.getChild("end", NAMESPACE);
		return null;
	}
	
	private void addSubNode(DotNode parent, Element root) {
		Element element = this.findElementByName(parent.getName(), root);
		if(element.getName().equals("action")) {
			Element child = element.getChild("ok", NAMESPACE);
			String okTo = child.getAttributeValue("to");
			DotNode childNode = new DotNode(okTo);
			parent.addChild(childNode);
			this.addSubNode(childNode, root);
		} else if(element.getName().equals("fork")) {
			List<Element> forks = element.getChildren();
			for(Element fork: forks) {
				DotNode childNode = new DotNode(fork.getAttributeValue("start"));
				parent.addChild(childNode);
				this.addSubNode(childNode, root);
			}
		} else if(element.getName().equals("join")) {
			DotNode childNode = new DotNode(element.getAttributeValue("to"));
			parent.addChild(childNode);
			this.addSubNode(childNode, root);
		}
	}
	
	private void visitNode(DotNode node, FileWriter fw, Collection<String> all) throws IOException {
		for(DotNode child: node.getChildren()) {
			String w = node.toString() + "  ->  " + child.toString();
			if(all.contains(w))
				continue;
			all.add(w);
			fw.write(w + "\n");
			this.visitNode(child, fw, all);
		}
	}

	class DotNode {
		private List<DotNode> children;
		private String name;
		
		public DotNode(String pid) {
			this.children = new ArrayList<DotNode>();
			this.name = pid;
		}
		
		public void addChild(DotNode node) {
			if(!this.children.contains(node))
				this.children.add(node);
		}
		
		@Override
		public String toString() {
			return this.name;
		}
		
		public List<DotNode> getChildren() {
			return this.children;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof DotNode == false)
				return false;
			if(obj == this)
				return true;
			DotNode rhs = (DotNode)obj;
			return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.name, rhs.name).isEquals();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(17,37).append(this.name).toHashCode();
		}
		
		public String getName() {
			return this.toString();
		}
	}
}