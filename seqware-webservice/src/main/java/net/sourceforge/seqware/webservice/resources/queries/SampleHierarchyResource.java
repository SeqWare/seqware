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

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
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
        	List<SampleHierarchy> shs;
        	try {
			shs = DBAccess.get().executeQuery("select sample_id, parent_id from sample_hierarchy", new ResultSetHandler<List<SampleHierarchy>>(){
        @Override
        public List<SampleHierarchy> handle(ResultSet rs) throws SQLException {
          List<SampleHierarchy> shs = new ArrayList<SampleHierarchy>();
          while(rs.next()) {
            SampleHierarchy sh = new SampleHierarchy();
            sh.setSampleId(rs.getInt("sample_id"));
                                    if(null == rs.getString("parent_id")) {
                                         sh.setParentId(-1);
                                    } else {                                          
                                         sh.setParentId(rs.getInt("parent_id"));
                                    }
            shs.add(sh);
          }     
          return shs;
        }
			});
		} catch (SQLException e) {
                     throw new RuntimeException(e);
		} finally {
                        DBAccess.close();
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
                org.json.JSONArray array = (org.json.JSONArray)obj.get("sampleHierarchies");
                String insertSql = this.jsonArrayToSql(array);
                //delete all rows in db, then insert them again
                String deleteAll = "delete from sample_hierarchy";
                DBAccess.get().executeUpdate(deleteAll);
                DBAccess.get().executeUpdate(insertSql);
            } catch (IOException ex) {
                Logger.getLogger(SampleHierarchyResource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                    Logger.getLogger(SampleHierarchyResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SQLException ex) {
                    Logger.getLogger(SampleHierarchyResource.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                DBAccess.close();
            }

        }
    }
    
    private String jsonArrayToSql(org.json.JSONArray array) throws JSONException {
        StringBuilder sb = new StringBuilder("insert into sample_hierarchy (\"sample_id\", \"parent_id\") values ");
        for(int i = 0; i<array.length(); i++) {
            if(i>0) {
                sb.append(",");
            }
            JSONObject obj = (JSONObject) array.get(i);
            sb.append("(").append(obj.getInt("sampleId")).append(",");
            if(-1!=obj.getInt("parentId")) {
                sb.append(obj.getInt("parentId"));
            } else {
                sb.append("null");
            }
            sb.append(")");
        }
        return sb.toString();
    }
}