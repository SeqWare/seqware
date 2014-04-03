package net.sourceforge.seqware.pipeline.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openide.util.lookup.ServiceProvider;

import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;

@ServiceProvider(service=PluginInterface.class)
public class ProcessingDataStructure2Dot extends Plugin {

	private ReturnValue ret = new ReturnValue();
	
	public ProcessingDataStructure2Dot() {
		super();
		parser
        .accepts(
            "parent-accession",
            "The SWID of the processing")
        .withRequiredArg().ofType(String.class).describedAs("The SWID of the processing.");
		parser
        .accepts(
            "output-file",
            "Optional: file name")
        .withRequiredArg().ofType(String.class).describedAs("Output File Name");

	}
	
	@Override
	public ReturnValue init() {
		// TODO Auto-generated method stub
		return ret;
	}

	@Override
	public ReturnValue do_test() {
		// TODO Auto-generated method stub
		return ret;
	}

	@Override
	public ReturnValue do_run() {
		String swAccession = (String) options.valueOf("parent-accession");
		String outputFile = (String) options.valueOf("output-file");
		String output = metadata.getProcessingRelations(swAccession);
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	   @Override
	   public String get_description() {
	      return "This plugin will take in a sw_accession of a processing, and translate the hierarchy of the processing relationship into dot format";
	   }
	   
	@Override
	public ReturnValue clean_up() {
		// TODO Auto-generated method stub
		return ret;
	}
	
	class DotNode {
		private List<DotNode> children;
		private String processingId;
		
		public DotNode(String pid) {
			this.children = new ArrayList<DotNode>();
			this.processingId = pid;
		}
		
		public void addChild(DotNode node) {
			if(!this.children.contains(node))
				this.children.add(node);
		}
		
		@Override
		public String toString() {
			return this.processingId;
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
			return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.processingId, rhs.processingId).isEquals();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(17,37).append(this.processingId).toHashCode();
		}
	}
	
	private void buildDotTree(MetadataDB metadb, String parentAccession, File file) throws SQLException, IOException {
		
		String sql0 = "select processing_id from processing where sw_accession = " + parentAccession;
		String s = metadb.executeQuery(sql0, new ResultSetHandler<String>(){
      @Override
      public String handle(ResultSet rs) throws SQLException {
        if (rs.next()){
          return rs.getString("processing_id");
        } else {
          return null;
        }
      }
		});
		if(s == null)
			return;
		
		DotNode root = new DotNode(s);
		
		String sql = "select child_id from processing_relationship where parent_id = " + root.toString();    
		List<String> children = metadb.executeQuery(sql, new ResultSetHandler<List<String>>(){
      @Override
      public List<String> handle(ResultSet rs) throws SQLException {
        List<String> children = new ArrayList<String>();
        while(rs.next()) {
          children.add(rs.getString("child_id"));
        }
        return children;
      }
		});
		
		for(String c: children) {
			DotNode child = new DotNode(c);
			root.addChild(child);
			this.addSubNodes(metadb, child);
		}
		//construct dot file
		
		FileWriter fw = new FileWriter(file);
		fw.write("digraph dag {\n");
		//avoid duplicated
		Set<String> allEdge = new HashSet<String>();
		this.visitNode(root, fw, allEdge);
		fw.write("}\n");
		fw.close();
	}
	
	private void addSubNodes(MetadataDB db, DotNode parent) throws SQLException {
		String sql = "select child_id from processing_relationship where parent_id = " + parent.toString();
		List<String> children = db.executeQuery(sql, new ResultSetHandler<List<String>>(){
      @Override
      public List<String> handle(ResultSet rs) throws SQLException {
        List<String> children = new ArrayList<String>();
        while(rs.next()) {
          children.add(rs.getString("child_id"));
        }
        return children;
      }
		});
		for(String c: children) {
			DotNode child = new DotNode(c);
			parent.addChild(child);
			this.addSubNodes(db, child);
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
}