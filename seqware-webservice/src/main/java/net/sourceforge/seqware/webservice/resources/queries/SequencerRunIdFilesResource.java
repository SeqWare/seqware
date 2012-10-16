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
import java.sql.SQLException;
import java.util.ArrayList;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import java.util.List;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.lists.ReturnValueList;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.w3c.dom.Document;

/**
 * <p>SequencerRunIdFilesResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class SequencerRunIdFilesResource extends BasicRestlet {

    /**
     * <p>Constructor for SequencerRunIdFilesResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public SequencerRunIdFilesResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        try {
            String id = request.getAttributes().get("sequencerRunId").toString();

            List<ReturnValue> returnValues = hello(Integer.parseInt(id));

            ReturnValueList list = new ReturnValueList();
            list.setList(returnValues);

            JaxbObject<ReturnValueList> jaxbTool = new JaxbObject<ReturnValueList>();

            Document line = XmlTools.marshalToDocument(jaxbTool, returnValues);

            response.setEntity(XmlTools.getRepresentation(line));
        } catch (SQLException ex) {
            ex.printStackTrace();
            response.setStatus(Status.SERVER_ERROR_INTERNAL);
        }
    }

    /**
     * <p>hello.</p>
     *
     * @param srSWA a int.
     * @return a {@link java.util.List} object.
     * @throws java.sql.SQLException if any.
     */
    public List<ReturnValue> hello(int srSWA) throws SQLException {
        List<ReturnValue> returnValues = new ArrayList<ReturnValue>();
        try {
            ResultSet rs = DBAccess.get().executeQuery("SELECT l.lane_id FROM lane l, sequencer_run sr "
                    + "WHERE l.sequencer_run_id=sr.sequencer_run_id AND "
                    + "sr.sw_accession = " + srSWA);

            while (rs.next()) {
                LaneService ss = BeanFactory.getLaneServiceBean();

                FindAllTheFiles fatf = new FindAllTheFiles();
                Lane lane = (Lane) testIfNull(ss.findByID(rs.getInt("lane_id")));
                returnValues = fatf.filesFromLane(lane, null, null);
            }
        } finally {
            DBAccess.close();
        }
        return returnValues;
    }
}
