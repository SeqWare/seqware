package net.sourceforge.seqware.webservice.resources.tables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.business.ProcessingRelationshipService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingRelationship;

import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;


public class ProcessingStructureResource extends BasicRestlet {
	
    public ProcessingStructureResource(Context context) {
        super(context);
    }
	
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        init(request);
        
        Form form = request.getResourceRef().getQueryAsForm();
        String swAccession = form.getFirstValue("swAccession");
        //handle multi accession
        if(swAccession == null) {
            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "null swAccession");
            return;
        }
        String[] accessions = swAccession.trim().split(",");
        List<Integer> accessionList = new ArrayList<Integer>();
        for(String a: accessions) {
            try {
                Integer i = Integer.parseInt(a);
                accessionList.add(i);
            } catch(NumberFormatException e) {
                response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid swAccession");
                return;               
            }
        }
        
        final StringBuffer sb = new StringBuffer();
        sb.append("digraph dag {\n");
        for(int i: accessionList) {
            DotNode root = null;
			try {
				root = this.buildDotTree(i);
			} catch (SQLException e) {
				e.printStackTrace();
			}
            if(root == null) {
                response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid swAccession");
                return;                   
            }
            Set<String> cache = new HashSet<String>();
            this.visitNode(root, sb, cache);
        }
        
        sb.append("}\n");

        
        OutputRepresentation output = new OutputRepresentation(MediaType.TEXT_ALL) {

            @Override
            public void write(OutputStream out) throws IOException {
                 Writer writer = new BufferedWriter(new OutputStreamWriter(out));
                 writer.write(sb.toString());
                 writer.flush();
                 writer.close();
            }
        }; 
        response.setEntity(output);
                  
    }
    
    
	private DotNode buildDotTree( int parentAccession) throws SQLException {
		
		String sql0 = "select processing_id, algorithm, sw_accession from processing where sw_accession = " + parentAccession;
		ResultSet rs0 = DBAccess.get().executeQuery(sql0);
		if(!rs0.next())
			return null;
		
		DotNode root = new DotNode(rs0.getInt("processing_id"));
		root.setAlgo(rs0.getString("algorithm"));
		root.setSWAccessionId(rs0.getInt("sw_accession"));
		rs0.close();
		
		String sql = "select a.child_id, b.algorithm, b.sw_accession from processing_relationship a, processing b " +
				"where a.child_id = b.processing_id and a.parent_id = " + root.getProcessingId();    
		ResultSet rs = DBAccess.get().executeQuery(sql);
		//to avoid recursively open resultset, store them in array first, then close rs
		List<DotNode> children = new ArrayList<DotNode>();
		while(rs.next()) {
			DotNode c = new DotNode(rs.getInt("child_id"));
			c.setAlgo(rs.getString("algorithm"));
			c.setSWAccessionId(rs.getInt("sw_accession"));
			children.add(c);
		}
		rs.close();	
		for(DotNode c: children) {
			root.addChild(c);
			this.addSubNodes(c);
		}
		return root;
	}
	
	private void addSubNodes(DotNode parent) throws SQLException {
		String sql = "select a.child_id, b.algorithm, b.sw_accession from processing_relationship a, processing b " +
				"where a.child_id = b.processing_id and a.parent_id = " + parent.getProcessingId();  
		ResultSet rs = DBAccess.get().executeQuery(sql);
		//to avoid recursively open resultset, store them in array first, then close rs
		List<DotNode> children = new ArrayList<DotNode>();
		while(rs.next()) {
			DotNode c = new DotNode(rs.getInt("child_id"));
			c.setAlgo(rs.getString("algorithm"));
			c.setSWAccessionId(rs.getInt("sw_accession"));
			children.add(c);
		}
		rs.close();	
		for(DotNode c: children) {
			parent.addChild(c);
			this.addSubNodes(c);
		}		
	}
	

    
    	private void visitNode(DotNode node, StringBuffer fw, Collection<String> all)  {
		for(DotNode child: node.getChildren()) {
			String w = node.toString() + "  ->  " + child.toString();
			if(all.contains(w))
                            continue;
			all.add(w);
			fw.append(w + "\n");
			this.visitNode(child, fw, all);
		}
	}
    

    	class DotNode {
		private List<DotNode> children;
		private int processingId;
                private String algo;
                private int swAccessionId;
		
		public DotNode(int pid) {
			this.children = new ArrayList<DotNode>();
			this.processingId = pid;
		}
		
		public void addChild(DotNode node) {
			if(!this.children.contains(node))
				this.children.add(node);
		}
		
		@Override
		public String toString() {
			return this.algo + "-" + this.swAccessionId;
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
                
                public int getProcessingId() {
                    return this.processingId;
                }
                
                public String getAlgo() {
                    return this.algo;
                }
                
                public void setAlgo(String algo) {
                    this.algo = algo;
                }
                
                public int getSWAccessionId() {
                    return this.swAccessionId;
                }
                
                public void setSWAccessionId(int id) {
                    this.swAccessionId = id;
                }
	}
    
    
}

