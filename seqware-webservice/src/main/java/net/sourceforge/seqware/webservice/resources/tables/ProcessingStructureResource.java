package net.sourceforge.seqware.webservice.resources.tables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.business.ProcessingRelationshipService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingRelationship;
import net.sourceforge.seqware.queryengine.webservice.model.MetadataDB;
import net.sourceforge.seqware.queryengine.webservice.util.EnvUtil;

import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

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
        ProcessingService ps = BeanFactory.getProcessingServiceBean();
        ProcessingRelationshipService prs = BeanFactory.getProcessingRelationshipServiceBean();
        
        final StringBuffer sb = new StringBuffer();
        sb.append("digraph dag {\n");
        for(int i: accessionList) {
            DotNode root = this.buildDotTree(prs, ps, i);
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
    
        private DotNode buildDotTree(ProcessingRelationshipService prs, ProcessingService ps, int swAccession) {
            Processing p = ps.findBySWAccession(swAccession);
            if(p == null) {
                return null;
            }
            DotNode node0 = new DotNode(p.getProcessingId());
            node0.setAlgo(p.getAlgorithm());
            node0.setSWAccessionId(p.getSwAccession());
            //build sub node
            List<ProcessingRelationship> subrelations = prs.listByParentProcessingId(p.getProcessingId());
            for(ProcessingRelationship subprs: subrelations) {
                Processing subp = subprs.getProcessingByChildId();
                DotNode subNode = new DotNode(subp.getProcessingId());
                subNode.setAlgo(subp.getAlgorithm());
                subNode.setSWAccessionId(subp.getSwAccession());
                this.addSubNode(prs, subNode);
                node0.addChild(subNode);
            }
            return node0;
        }
        
        private void addSubNode(ProcessingRelationshipService prs, DotNode parent) {
            List<ProcessingRelationship> res = prs.listByParentProcessingId(parent.getProcessingId());
            for(ProcessingRelationship pr: res) {
                Processing p = pr.getProcessingByChildId();
                DotNode subNode = new DotNode(p.getProcessingId());
                subNode.setAlgo(p.getAlgorithm());
                subNode.setSWAccessionId(p.getSwAccession());
                parent.addChild(subNode);
                this.addSubNode(prs, subNode);
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

