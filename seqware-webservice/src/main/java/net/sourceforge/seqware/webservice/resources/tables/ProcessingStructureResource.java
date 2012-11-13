package net.sourceforge.seqware.webservice.resources.tables;

import java.io.File;

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ProcessingStructureResource extends ServerResource {
	
	@Get
	public Representation represent() { 
		FileRepresentation rep = null;
	    // get the swid
	    String swid = (String)getRequestAttributes().get("sw-accesion");
	    // get the processingid
	    String  processingId = (String)getRequestAttributes().get("processingId");
	    String filePath = (String)getRequestAttributes().get("filePath");
	    
	    rep = new FileRepresentation(new File(filePath),
	              MediaType.TEXT_PLAIN, 0);
		return rep;
	}
}