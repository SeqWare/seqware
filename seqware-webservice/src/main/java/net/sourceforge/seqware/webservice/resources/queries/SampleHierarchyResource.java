package net.sourceforge.seqware.webservice.resources.queries;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;

import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.queryengine.webservice.model.SampleHierarchies;
import net.sourceforge.seqware.queryengine.webservice.model.SampleHierarchy;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.json.JSONException;
import org.json.JSONObject;

public class SampleHierarchyResource extends BasicRestlet {

	public SampleHierarchyResource(Context context) {
		super(context);
	}
	
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        if (request.getMethod().compareTo(Method.GET) == 0) {      
        	List<SampleHierarchy> shs = new ArrayList<SampleHierarchy>();
        	try {
				ResultSet rs = DBAccess.get().executeQuery("select sample_id, parent_id from sample_hierarchy" );
				while(rs.next()) {
					SampleHierarchy sh = new SampleHierarchy();
					sh.setSampleId(rs.getInt("sample_id"));
					sh.setParentId(rs.getInt("parent_id"));
					shs.add(sh);
				}
				rs.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
                SampleHierarchies ret = new SampleHierarchies();
                ret.setSampleHierarchies(shs);
        	Representation rep = new JsonRepresentation(ret);
        	response.setEntity(rep);
        	
        } else if (request.getMethod().compareTo(Method.PUT) == 0) {
            Representation entity = request.getEntity();
            try {
                JsonRepresentation represent = new JsonRepresentation(entity);      
                JSONObject obj = represent.getJsonObject();
                System.out.println(obj);
            } catch (IOException ex) {
                Logger.getLogger(SampleHierarchyResource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                    Logger.getLogger(SampleHierarchyResource.class.getName()).log(Level.SEVERE, null, ex);
                }

        }
    }
}