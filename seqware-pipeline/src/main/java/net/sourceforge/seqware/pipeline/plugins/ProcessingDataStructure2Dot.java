package net.sourceforge.seqware.pipeline.plugins;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openide.util.lookup.ServiceProvider;

import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;

@ServiceProvider(service=PluginInterface.class)
public class ProcessingDataStructure2Dot extends Plugin {

	private ReturnValue ret = new ReturnValue();
	
	public ProcessingDataStructure2Dot() {
		super();
		parser
        .accepts(
            "sw-db-server",
            "Optional: database server.")
        .withRequiredArg().ofType(String.class).describedAs("Database Server.");
		parser
        .accepts(
            "sw-db",
            "Optional: database name")
        .withRequiredArg().ofType(String.class).describedAs("Database Name.");
		parser
        .accepts(
            "sw-db-user",
            "Optional: database user")
        .withRequiredArg().ofType(String.class).describedAs("Database User.");
		parser
        .accepts(
            "sw-db-pass",
            "Optional: password")
        .withRequiredArg().ofType(String.class).describedAs("Password.");
		parser
        .accepts(
            "parent-accession",
            "Optional: Specifies a path to prepend to every file returned by the module. Useful for dealing when staging files back.")
        .withRequiredArg().ofType(String.class).describedAs("Path to prepend to each file location.");
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

		//has to use metadatadb here since there is no webservice api available
	    MetadataDB metadb = new MetadataDB();
	    String db_server = (String)options.valueOf("sw-db-server");
	    String db = (String)options.valueOf("sw-db");
	    String db_user = (String)options.valueOf("sw-db-user");
	    String db_pass = (String)options.valueOf("sw-db-pass");
	    String parentAccession = (String)options.valueOf("parent-accession");
	    String output = (String)options.valueOf("output-file");
	    
	    String connection = "jdbc:postgresql://" + db_server + "/" + db;
	    ReturnValue ret = metadb.init(connection, db_user, db_pass);
	    if (ret.getExitStatus() != ReturnValue.SUCCESS) {
	      Log.stderr("ERROR connecting to metadata DB "+connection+" "+config.get("SW_DB_USER")+" "+config.get("SW_DB_PASS"));
	      Log.stderr(ret.getStderr());
	      System.exit(ret.getExitStatus());
	    }
	    
	    try {
			this.buildDotTree(metadb, parentAccession, new File(output));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		ResultSet rs0 = metadb.executeQuery(sql0);
		if(!rs0.next())
			return;
		
		DotNode root = new DotNode(rs0.getString("processing_id"));
		rs0.close();
		
		String sql = "select child_id from processing_relationship where parent_id = " + root.toString();    
		ResultSet rs = metadb.executeQuery(sql);
		//to avoid recursively open resultset, store them in array first, then close rs
		List<String> children = new ArrayList<String>();
		while(rs.next()) {
			children.add(rs.getString("child_id"));
		}
		rs.close();	
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
		ResultSet rs = db.executeQuery(sql);
		//to avoid recursively open resultset, store them in array first, then close rs
		List<String> children = new ArrayList<String>();
		while(rs.next()) {
			children.add(rs.getString("child_id"));
		}
		rs.close();	
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