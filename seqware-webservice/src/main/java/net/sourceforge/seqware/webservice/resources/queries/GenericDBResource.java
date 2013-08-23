/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.webservice.resources.queries;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.apache.commons.dbutils.ResultSetHandler;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.ResourceInfo;
import org.restlet.ext.wadl.WadlDescribable;

/**
 * <p>GenericDBResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class GenericDBResource extends BasicRestlet implements WadlDescribable {

    /**
     * <p>Constructor for GenericDBResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public GenericDBResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        String query = request.getEntityAsText();

        if (query == null || query.trim().isEmpty()) {
            response.setEntity("<?xml version=\"1.0\"?>"
                    + "<version major=\"0\" minor=\"11\" patch=\"0\"/>", MediaType.TEXT_XML);
            return;
        }

        if (request.getMethod() == Method.GET) {
            try {
              String table = DBAccess.get().executeQuery(query, new ResultSetHandler<String>(){
                @Override
                public String handle(ResultSet rs) throws SQLException {
                  return printResultSet(rs);
                }
              });
                response.setEntity(table, MediaType.TEXT_PLAIN);
            } catch (SQLException ex) {
                ex.printStackTrace();
                response.setStatus(Status.SERVER_ERROR_INTERNAL, ex);

            } finally {
                DBAccess.close();
            }
        } else if (request.getMethod() == Method.PUT) {
            try {
                int rows = DBAccess.get().executeUpdate(query);
                String reply = "Statement: " + query + "\n\t" + rows + " rows were modified";
                response.setEntity(reply, MediaType.TEXT_PLAIN);
            } catch (SQLException ex) {
                ex.printStackTrace();
                response.setStatus(Status.SERVER_ERROR_INTERNAL, ex);
            } finally {
                DBAccess.close();
            }
        }
    }

    private String printResultSet(ResultSet rs) throws SQLException {
        StringBuilder s = new StringBuilder();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        //header
        for (int i = 1; i <= numColumns; i++) {
            s.append(rsmd.getColumnName(i));
            s.append("\t");
        }
        //values
        while (rs.next()) {
            for (int i = 1; i <= numColumns; i++) {
                s.append(rs.getString(i));
                s.append("\t");
            }
            s.append("\n");
        }

        return s.toString();
    }

    /** {@inheritDoc} */
    @Override
    public ResourceInfo getResourceInfo(ApplicationInfo ai) {
        ResourceInfo ri = new ResourceInfo();

        return ri;
    }
}
