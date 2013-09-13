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

import static net.sourceforge.seqware.webservice.resources.BasicResource.parseClientInt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.webservice.resources.BasicResource;
import net.sourceforge.seqware.webservice.resources.BasicRestlet;

import org.apache.commons.dbutils.ResultSetHandler;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;

/**
 * <p>WorkflowRuntimeResource class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRuntimeResource
        extends BasicRestlet {

    // processing IDs
    HashMap<Integer, Boolean> seen = new HashMap<Integer, Boolean>();
    // main data structure
    HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> d = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>>();
    // algos
    HashMap<String, Boolean> algos = new HashMap<String, Boolean>();
    // workflow run IDs
    HashMap<Integer, Boolean> wrIds = new HashMap<Integer, Boolean>();

    /**
     * <p>Constructor for WorkflowRuntimeResource.</p>
     *
     * @param context a {@link org.restlet.Context} object.
     */
    public WorkflowRuntimeResource(Context context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handle(Request request, Response response) {
        authenticate(request.getChallengeResponse().getIdentifier());
        if (request.getMethod().compareTo(Method.GET) == 0) {

            String workflowAccession = null;
            String format = "text";
            if (request != null && request.getAttributes() != null) {
                String localWorkflowAccession = (String) request.getAttributes().get("workflowId");
                if (localWorkflowAccession != null) {
                    workflowAccession = localWorkflowAccession;
                }
                String localFormat = (String) request.getAttributes().get("workflowId");
                if (localFormat != null) {
                    format = localFormat;
                }
            }

            StringBuilder m = new StringBuilder();

            try {

                /*
                 * StringBuilder sb = new StringBuilder(); sb.append("SELECT
                 * name, version FROM workflow ");
                 *
                 * ResultSet rs = DBAccess.get().executeQuery(sb.toString());
                 * while(rs.next()) { String name = rs.getString("name"); String
                 * version = rs.getString("version");
                 * m.append(name+"\t"+version+"\n");
        }
                 */

                // now iterate over each workflow (or just the one specified) and find average runtime per step
                String query = "select p.processing_id, p.algorithm, p.status, p.create_tstmp, w.name, wr.workflow_run_id "
                        + "from workflow_run as wr, workflow as w, processing as p "
                        + "where w.workflow_id = wr.workflow_id and p.workflow_run_id = wr.workflow_run_id and p.status = 'success' "
                        + "and wr.status = 'completed' ";

                if (workflowAccession != null) {
                    query = query + " and w.sw_accession = " + workflowAccession;
                }

                query = query + " order by p.create_tstmp";

                Map<Integer, Map<String, String>> currentProcIds = DBAccess.get().executeQuery(query, new ResultSetHandler<Map<Integer, Map<String, String>>>(){
                  @Override
                  public Map<Integer, Map<String, String>> handle(ResultSet rs) throws SQLException {
                    Map<Integer, Map<String, String>> currentProcIds = new HashMap<Integer, Map<String, String>>();
                    while (rs.next()) {
                      Integer processingId = rs.getInt(1);
                      String algorithm = rs.getString(2);
                      String status = rs.getString(3);
                      Timestamp createTstmp = rs.getTimestamp(4);
                      String workflowName = rs.getString(5);
                      Integer workflowRunId = rs.getInt(6);
                      HashMap<String, String> currentProcHash = new HashMap<String, String>();
                      currentProcHash.put("procId", processingId.toString());
                      currentProcHash.put("workflowRunId", workflowRunId.toString());
                      currentProcHash.put("workflowName", workflowName);
                      currentProcIds.put(processingId, currentProcHash);
                    }
                    return currentProcIds;
                  }
                });
                

                for (Integer currentProcId : currentProcIds.keySet()) {
                    String procId = currentProcIds.get(currentProcId).get("procId");
                    String workflowRunId = currentProcIds.get(currentProcId).get("workflowRunId");
                    String workflowName = currentProcIds.get(currentProcId).get("workflowName");
                    recursiveFindProcessings(currentProcId, parseClientInt(workflowRunId), workflowName);
                }

                // at this point the whole hash should be populated
                // workflow name -> algorithm -> workflow run id -> runtime -> int
                //                                               -> counts  -> int
                for (String workflow : d.keySet()) {
                    m.append(workflow).append("\t");
                    String lastAlgo = "";
                    for (String algo : d.get(workflow).keySet()) {
                        m.append(algo).append("\t");
                        lastAlgo = algo;
                    }
                    m.append("Total\n");
                    for (String wrId : d.get(workflow).get(lastAlgo).keySet()) {
                        m.append(wrId).append("\t");
                        int totalWRuntime = 0;
                        for (String algo : d.get(workflow).keySet()) {
                            if (d.get(workflow) != null && d.get(workflow).get(algo) != null && d.get(workflow).get(algo).get(wrId) != null && d.get(workflow).get(algo).get(wrId).get("runtime") != null) {
                                Integer runtime = d.get(workflow).get(algo).get(wrId).get("runtime");
                                totalWRuntime += runtime;
                                m.append(runtime).append("\t");
                            }
                        }
                        m.append(totalWRuntime).append("\n");
                    }
                }


            } catch (Exception e) {
                System.err.print(e.getMessage());
                e.printStackTrace();
            } finally {
                DBAccess.close();
            }

            response.setEntity(m.toString(), MediaType.TEXT_PLAIN);
        } else {
            response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
        }
    }

    private void recursiveFindProcessings(Integer processingId, Integer workflowRunId, String workflowName) {
        if (seen.get(processingId) != null && seen.get(processingId)) {
            return;
        }
        seen.put(processingId, true);

        try {
            Object[] tuple = DBAccess.get().executeQuery("select p.processing_id, p.algorithm, p.status, p.create_tstmp, EXTRACT(EPOCH from p.run_stop_tstmp - p.run_start_tstmp) as length from processing as p where p.processing_id = " + processingId + " and p.status = 'success' order by p.create_tstmp", new ResultSetHandler<Object[]>(){
              @Override
              public Object[] handle(ResultSet rs) throws SQLException {
                if (rs.next()) {
                  return new Object[]{rs.getString(2), rs.getString(3), rs.getTimestamp(4), rs.getInt(5)};
                } else {
                  return null;                  
                }
              }
            });

            Map<Integer, Boolean> childProcessingHash = new HashMap<Integer, Boolean>();
            if (tuple != null) {

                String algorithm = (String)tuple[0];
                String status = (String)tuple[1];
                Timestamp create = (Timestamp)tuple[2];
                Integer runtime = (Integer)tuple[3];

                algos.put(algorithm, true);
                wrIds.put(workflowRunId, true);

                //HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> d = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>>();

                // workflow name -> algorithm -> workflow run id -> runtime -> int
                //                                               -> counts  -> int
                HashMap<String, HashMap<String, HashMap<String, Integer>>> algorithmsHash = d.get(workflowName);
                if (algorithmsHash == null) {
                    algorithmsHash = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
                    d.put(workflowName, algorithmsHash);
                }

                HashMap<String, HashMap<String, Integer>> workflowRunHash = algorithmsHash.get(algorithm);
                if (workflowRunHash == null) {
                    workflowRunHash = new HashMap<String, HashMap<String, Integer>>();
                    algorithmsHash.put(algorithm, workflowRunHash);
                }

                HashMap<String, Integer> runtimeHash = workflowRunHash.get(workflowRunId.toString());
                if (runtimeHash == null) {
                    runtimeHash = new HashMap<String, Integer>();
                    workflowRunHash.put(workflowRunId.toString(), runtimeHash);
                }

                Integer runtimes = runtimeHash.get("runtime");
                if (runtimes == null) {
                    runtimes = runtime;
                } else {
                    runtimes += runtime;
                }
                runtimeHash.put("runtime", runtimes);

                Integer counts = runtimeHash.get("counts");
                if (counts == null) {
                    counts = 1;
                } else {
                    counts++;
                }
                runtimeHash.put("counts", counts);

                childProcessingHash = DBAccess.get().executeQuery("select p.processing_id, p.algorithm, p.status, p.create_tstmp from processing as p, processing_relationship as pr where pr.parent_id = " + processingId + " and pr.child_id = p.processing_id and p.ancestor_workflow_run_id = " + workflowRunId, new ResultSetHandler<Map<Integer, Boolean>>(){
                  @Override
                  public Map<Integer, Boolean> handle(ResultSet rs) throws SQLException {
                    Map<Integer, Boolean> childProcessingHash = new HashMap<Integer, Boolean>();
                    while (rs.next()) {
                      childProcessingHash.put(rs.getInt(1), true);
                    }
                    return childProcessingHash;
                  }
                });


            }

            // now recursively call
            for (Integer childProcId : childProcessingHash.keySet()) {
                recursiveFindProcessings(childProcId, workflowRunId, workflowName);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally{
            DBAccess.close();
        }
    }
}
