package net.sourceforge.seqware.common.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.sql.DataSource;
import net.sourceforge.seqware.common.factory.DBAccess;

import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import org.apache.commons.dbutils.DbUtils;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

// FIXME: Have to record processing event (event), what the workflow it was, etc. 
// FIXME: Need to add workflow table, and then have each processing event associated with a workflowID for this particular run of the workflow  
/**
 * <p>MetadataDB class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class MetadataDB extends Metadata {

  private Connection db; // A connection to the database
  private DatabaseMetaData dbmd; // This is basically info the driver delivers
  // about the DB it just connected to. I use
  // it to get the DB version to confirm the
  // connection in this example.
  private Statement sql;
  private Logger logger;

  /**
   * <p>Constructor for MetadataDB.</p>
   */
  public MetadataDB() {
    super();
    logger = Logger.getLogger(MetadataDB.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue addStudy(String title, String description, String accession, StudyType studyType,
          String centerName, String centerProjectName, Integer studyTypeId) {
    return (new ReturnValue(ReturnValue.NOTIMPLEMENTED));
  }

  /**
   * {@inheritDoc}
   */
  public ReturnValue addExperiment(Integer studySwAccession, Integer platformId, String description, String title) {
    return (new ReturnValue(ReturnValue.NOTIMPLEMENTED));
  }

  /**
   * {@inheritDoc}
   */
    @Override
    public ReturnValue addSample(Integer experimentAccession, Integer parentSampleAccession, Integer organismId, String description, String title) {
        return (new ReturnValue(ReturnValue.NOTIMPLEMENTED));
    }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ReturnValue> findFilesAssociatedWithAStudy(String studyName) {
    // String getStudyInfoFromStudyName =
    // "SELECT study_id, title, sw_accession FROM study WHERE title = '?'";
    // StringBuffer buffer = new StringBuffer();
    // buffer.append("SELECT s.name, e.title, e.sw_accession ");
    // buffer.append("FROM sample AS s, experiment AS e ");
    // buffer.append("WHERE e.experiment_id = s.experiment_id ");
    // buffer.append("AND e.study_id = ?");
    // String getSampleAndExperimentFromStudyId = buffer.toString();
    //
    // ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    // String query = null;
    // try {
    // /*
    // * Get study information
    // */
    // query = getStudyInfoFromStudyName.replace("?", studyName);
    // ResultSet rs = executeQuery(query);
    // String studyId = null;
    // if (rs.next()) {
    // studyId = new Integer(rs.getInt("study_id")).toString();
    // finished.setAttribute("study_title", rs.getString("title"));
    // finished.setAttribute("study_swaccession", new
    // Integer(rs.getInt("sw_accession")).toString());
    // } else {
    // finished.setExitStatus(ReturnValue.INVALIDPARAMETERS);
    // finished.setStderr("No study with name " + studyName);
    // return finished;
    // }
    //
    // ArrayList<FileMetadata> samples = new ArrayList<FileMetadata>();
    // query = getSampleAndExperimentFromStudyId.replace("?", studyId);
    // rs = executeQuery(query);
    // while (rs.next()) {
    // String sampleName = rs.getString("name");
    // String experiment = rs.getString("title") + "-" +
    // rs.getInt("sw_accession");
    // FileMetadata sample = new FileMetadata();
    // sample.setFilePath(experiment);
    // sample.setDescription(sampleName);
    // samples.add(sample);
    // }
    // if (samples.isEmpty()) {
    // finished.setExitStatus(ReturnValue.FILEEMPTY);
    // finished.setStderr("No samples for study " + studyName);
    // return finished;
    // }
    // finished.setFiles(samples);
    //
    // } catch (SQLException e) {
    // logger.error("SQL Command failed: " + query);
    // finished.setStderr("Could not execute one of the SQL commands: " + query
    // + "\nException: " + e.getMessage());
    // finished.setExitStatus(ReturnValue.SQLQUERYFAILED);
    // }
    // finished.setExitStatus(ReturnValue.SUCCESS);
    // return finished;
    throw new NotImplementedException("Please use the SymLinker through the Web service.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ReturnValue> findFilesAssociatedWithASample(String sampleName) {
    // StringBuffer buffer = new StringBuffer();
    // buffer.append("SELECT sr.name as sequencer_run_name, sr.sw_accession as sr_swaccess, ");
    // buffer.append("l.name as lane_name, i.ius_id, s.name as sample_name, s.sw_accession as sample_swaccess, ");
    // buffer.append("s.sample_id ");
    // buffer.append("FROM ius AS i, lane AS l, sequencer_run as sr, sample as s ");
    // buffer.append("WHERE i.lane_id = l.lane_id ");
    // buffer.append("AND l.sequencer_run_id = sr.sequencer_run_id ");
    // buffer.append("AND i.sample_id = s.sample_id ");
    // buffer.append("AND s.name = '?'");
    // String getSampleInfoFromSampleName = buffer.toString();
    //
    // buffer = new StringBuffer();
    // buffer.append("SELECT pi.processing_id ");
    // buffer.append("FROM ius AS i, lane AS l, processing_ius as pi ");
    // buffer.append("WHERE i.lane_id = l.lane_id AND pi.ius_id = i.ius_id ");
    // buffer.append("AND i.sample_id = ?");
    // String sampleToProcessingWithSampleId = buffer.toString();
    //
    // buffer = new StringBuffer();
    // buffer.append("SELECT pl.processing_id ");
    // buffer.append("FROM ius AS i, lane AS l, processing_lanes as pl ");
    // buffer.append("WHERE i.lane_id = l.lane_id AND pl.lane_id = l.lane_id ");
    // buffer.append("AND i.sample_id = ?");
    // String sampleToProcessingWithSampleIdViaLane = buffer.toString();
    //
    // buffer = new StringBuffer();
    // buffer.append("SELECT f.file_id, f.file_path, f.meta_type, f.sw_accession ");
    // buffer.append("FROM processing_relationship AS pr, file as f, processing_files as pf ");
    // buffer.append("WHERE pr.child_id = pf.processing_id ");
    // buffer.append("AND pf.file_id = f.file_id ");
    // buffer.append("AND pr.parent_id = ?;");
    // String findFilesWithParentId = buffer.toString();
    //
    // buffer = new StringBuffer();
    // buffer.append("SELECT pr.parent_id, pr.child_id ");
    // buffer.append("FROM processing_relationship AS pr ");
    // buffer.append("WHERE pr.parent_id = ?");
    // String parentChildRecurseWithParentId = buffer.toString();
    //
    // buffer = new StringBuffer();
    // buffer.append("SELECT w.name as workflow_name, wr.name as workflow_run_name, wr.sw_accession ");
    // buffer.append("FROM processing AS p, workflow_run AS wr, workflow as w ");
    // buffer.append("WHERE (p.ancestor_workflow_run_id = wr.workflow_run_id OR p.workflow_run_id = wr.workflow_run_id) ");
    // buffer.append("AND wr.workflow_id = w.workflow_id ");
    // buffer.append("AND p.processing_id = ?");
    // String getWorkflowInformationFromProcessingId = buffer.toString();
    //
    // String query = null;
    // ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
    //
    // try {
    // /*
    // * Get the sample information from the name
    // */
    // query = getSampleInfoFromSampleName.replace("?", sampleName);
    // ResultSet rs = executeQuery(query);
    // String sampleId = null;
    // if (rs.next()) {
    // sampleId = new Integer(rs.getInt("sample_id")).toString();
    //
    // finished.setAttribute("sequencer_run_name",
    // rs.getString("sequencer_run_name"));
    // finished.setAttribute("sequencer_run_swaccession", new
    // Integer(rs.getInt("sr_swaccess")).toString());
    // finished.setAttribute("lane_name", rs.getString("lane_name"));
    // finished.setAttribute("ius_id", new
    // Integer(rs.getInt("ius_id")).toString());
    // finished.setAttribute("sample_name", rs.getString("sample_name"));
    // finished.setAttribute("sample_swaccession", new
    // Integer(rs.getInt("sample_swaccess")).toString());
    //
    // } else {
    // finished.setStderr("No such sample name " + sampleName);
    // finished.setExitStatus(ReturnValue.INVALIDPARAMETERS);
    // return finished;
    // }
    //
    // /*
    // * Get the first processing ids
    // */
    // // processing events attached to IUS
    // query = sampleToProcessingWithSampleId.replace("?", sampleId);
    // rs = executeQuery(query);
    // Queue<String> processingEvents = new LinkedList<String>();
    // while (rs.next()) {
    // processingEvents.add(new Integer(rs.getInt("processing_id")).toString());
    // }
    // // now try looking for processing events attached to lane directly
    // query = sampleToProcessingWithSampleIdViaLane.replace("?", sampleId);
    // rs = executeQuery(query);
    // while (rs.next()) {
    // processingEvents.add(new Integer(rs.getInt("processing_id")).toString());
    // }
    // // error out if no processing events
    // if (processingEvents.isEmpty()) {
    // finished.setStderr("No processing events for sample name " + sampleName);
    // finished.setExitStatus(ReturnValue.FAILURE);
    // return finished;
    // }
    //
    // ArrayList<FileMetadata> files = new ArrayList<FileMetadata>();
    // while (!processingEvents.isEmpty()) {
    // /*
    // * try to find the files
    // */
    // String currId = processingEvents.poll();
    // query = findFilesWithParentId.replace("?", currId);
    // rs = executeQuery(query);
    // while (rs.next()) {
    // FileMetadata fm = new FileMetadata();
    // fm.setFilePath(rs.getString("file_path"));
    // fm.setMetaType(rs.getString("meta_type"));
    // fm.setDescription(rs.getString("sw_accession"));
    // files.add(fm);
    // }
    //
    // /*
    // * Find child processes
    // */
    // query = parentChildRecurseWithParentId.replace("?", currId);
    // rs = executeQuery(query);
    // while (rs.next()) {
    // String childId = new Integer(rs.getInt("child_id")).toString();
    // processingEvents.add(childId);
    // }
    //
    // /*
    // * See if this processing has a workflow ID
    // */
    //
    // query = getWorkflowInformationFromProcessingId.replace("?", currId);
    // rs = executeQuery(query);
    // if (rs.next()) {
    // finished.setAttribute("workflow_name", rs.getString("workflow_name"));
    // finished.setAttribute("workflow_run_name",
    // rs.getString("workflow_run_name"));
    // finished.setAttribute("workflow_swaccession",
    // rs.getString("sw_accession"));
    // }
    //
    // }
    // if (files.isEmpty()) {
    // finished.setStderr("No files produced for " + sampleName);
    // finished.setExitStatus(ReturnValue.FILEEMPTY);
    // }
    //
    // finished.setFiles(files);
    //
    // } catch (SQLException e) {
    // logger.error("SQL Command failed: " + query);
    // finished.setStderr("Could not execute one of the SQL commands: " + query
    // + "\nException: " + e.getMessage());
    // finished.setExitStatus(ReturnValue.SQLQUERYFAILED);
    // }
    // finished.setExitStatus(ReturnValue.SUCCESS);
    // return finished;
    throw new NotImplementedException("Please use the Symlinker through the Web service.");
  }

  // FIXME: Need to tune these statements in case of null values. Need to figure
  // what we exactly need
  // FIXME: to require in a ReturnValue and gracefully exit on missing required
  // value.
  /**
   * Find out the primary key for the last inserted record FIXME: This is
   * hardcoded for Postgres, need to make DB agnostic
   *
   * @param SequenceID a {@link java.lang.String} object.
   * @throws java.sql.SQLException if any.
   * @param sqlQuery a {@link java.lang.String} object.
   * @return a int.
   */
  public int InsertAndReturnNewPrimaryKey(String sqlQuery, String SequenceID) throws SQLException {
    executeUpdate(sqlQuery);
    executeQuery("select currval('" + SequenceID + "')");
    this.getSql().getResultSet().next();
    return this.getSql().getResultSet().getInt(1);
  }

  // FIXME: This should all be a transaction. For now, we end up with cruft in
  // the DB if something failed.
  /*
   * FIXME: instead of taking in parentID's here, need to take in tubles to
   * discuss the relationship. Different types of relationships: match1 ->
   * variant1 is process match -> variant is algorithm match -> match1, match2,
   * etc is subprocess
   */
  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue add_empty_processing_event(int[] parentIDs) {
    int processingID;
    // Create a SQL statement
    StringBuffer sql = new StringBuffer();
    try {
      // FIXME: Add a new processing entry
      sql.append("INSERT INTO processing (status, create_tstmp) VALUES( '" + Metadata.PENDING + "', now())");
      processingID = InsertAndReturnNewPrimaryKey(sql.toString(), "processing_processing_id_seq");

      // Associate the processing entry with the zero or more parents
      if (parentIDs != null) {
        for (int parentID : parentIDs) {
          sql = new StringBuffer();

          sql.append("INSERT INTO processing_relationship (");
          sql.append("parent_id,");
          sql.append("child_id,");
          sql.append("relationship");
          sql.append(") VALUES (");
          sql.append(parentID + ",");
          sql.append(processingID + ",");
          sql.append("'parent-child'");
          sql.append(")");

          executeUpdate(sql.toString());
        }
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString());
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql.toString() + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    /*
     * If no error, return success
     */
    ReturnValue ret = new ReturnValue();
    ret.setReturnValue(processingID);
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue add_empty_processing_event_by_parent_accession(int[] parentAccessions) {
    int processingID;
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    // Create a SQL statement
    StringBuffer sql = new StringBuffer();
    try {
      // FIXME: Add a new processing entry
      sql.append("INSERT INTO processing (status, create_tstmp) VALUES( '" + Metadata.PENDING + "', now())");
      processingID = InsertAndReturnNewPrimaryKey(sql.toString(), "processing_processing_id_seq");

      // Associate the processing entry with the zero or more parents
      if (parentAccessions != null) {
        for (int accession : parentAccessions) {

          // LEFT OFF HERE: need to check ius, lane, sequencer_run, processing
          // to see which table to populate
          if (!linkAccessionAndParent(accession, processingID)) {
            ret.setExitStatus(ReturnValue.SQLQUERYFAILED);
            ret.setStderr(ret.getStderr() + "\nCouldn't link this processing event: " + processingID + " to accession "
                    + accession);
            return (ret);
          }

        }
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString());
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql.toString() + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    } finally{
        DBAccess.close();
    }

    /*
     * If no error, return success
     */
    ret.setReturnValue(processingID);
    return ret;
  }

  /**
   * {@inheritDoc}
   *
   * This maps processing_id to sw_accession for that event.
   */
  public int mapProcessingIdToAccession(int processingId) {
    int result = 0;
    String sql = "select sw_accession from processing where processing_id = " + processingId;
    ResultSet rs;
    try {
      rs = executeQuery(sql);
      if (rs.next()) {
        result = rs.getInt("sw_accession");
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString());
      return (-1);
    }
    return (result);
  }

  /**
   * {@inheritDoc}
   *
   * TODO: needs to support more relationship types, but will need to add to the
   * SQL schema to support this
   */
  public boolean linkWorkflowRunAndParent(int workflowRunId, int parentAccession) throws SQLException {
    StringBuffer sql = new StringBuffer();
    if (findAccessionInTable("ius", "ius_id", parentAccession) != 0) {

      int parentId = findAccessionInTable("ius", "ius_id", parentAccession);
      sql.append("INSERT INTO ius_workflow_runs (");
      sql.append("ius_id, ");
      sql.append("workflow_run_id ");
      sql.append(") VALUES (");
      sql.append(parentId + ",");
      sql.append(workflowRunId);
      sql.append(")");

      executeUpdate(sql.toString());

    } else if (findAccessionInTable("lane", "lane_id", parentAccession) != 0) {

      int parentId = findAccessionInTable("lane", "lane_id", parentAccession);
      sql.append("INSERT INTO lane_workflow_runs (");
      sql.append("lane_id, ");
      sql.append("workflow_run_id ");
      sql.append(") VALUES (");
      sql.append(parentId + ",");
      sql.append(workflowRunId);
      sql.append(")");

      executeUpdate(sql.toString());

    } else {
      return (false);
    }
    return (true);
  }

  /**
   * <p>linkAccessionAndParent.</p>
   *
   * @param accession a int.
   * @param processingID a int.
   * @return a boolean.
   * @throws java.sql.SQLException if any.
   */
  public boolean linkAccessionAndParent(int accession, int processingID) throws SQLException {
    StringBuffer sql = new StringBuffer();
    Log.debug("Link Accession and Parent accession:" + accession + " processingId:" + processingID);

    if (accession == 0) {
      Log.warn("This processing event has no parents! " + processingID);
      return true;
    }

    if (findAccessionInTable("ius", "ius_id", accession) != 0) {

      int parentId = findAccessionInTable("ius", "ius_id", accession);
      sql.append("INSERT INTO processing_ius (");
      sql.append("ius_id, ");
      sql.append("processing_id ");
      sql.append(") VALUES (");
      sql.append(parentId + ",");
      sql.append(processingID);
      sql.append(")");

      executeUpdate(sql.toString());

    } else if (findAccessionInTable("lane", "lane_id", accession) != 0) {

      int parentId = findAccessionInTable("lane", "lane_id", accession);
      sql.append("INSERT INTO processing_lanes (");
      sql.append("lane_id, ");
      sql.append("processing_id ");
      sql.append(") VALUES (");
      sql.append(parentId + ",");
      sql.append(processingID);
      sql.append(")");

      executeUpdate(sql.toString());

    } else if (findAccessionInTable("sequencer_run", "sequencer_run_id", accession) != 0) {

      int parentId = findAccessionInTable("sequencer_run", "sequencer_run_id", accession);
      sql.append("INSERT INTO processing_sequencer_runs (");
      sql.append("sequencer_run_id, ");
      sql.append("processing_id ");
      sql.append(") VALUES (");
      sql.append(parentId + ",");
      sql.append(processingID);
      sql.append(")");

      executeUpdate(sql.toString());

    } else if (findAccessionInTable("processing", "processing_id", accession) != 0) {

      int parentId = findAccessionInTable("processing", "processing_id", accession);
      sql.append("INSERT INTO processing_relationship (");
      sql.append("parent_id, ");
      sql.append("child_id, ");
      sql.append("relationship");
      sql.append(") VALUES (");
      sql.append(parentId + ",");
      sql.append(processingID + ",");
      sql.append("'parent-child'");
      sql.append(")");

      executeUpdate(sql.toString());

    } else if (findAccessionInTable("study", "study_id", accession) != 0) {

      int parentId = findAccessionInTable("study", "study_id", accession);
      sql.append("INSERT INTO processing_studies (");
      sql.append("study_id, ");
      sql.append("processing_id ");
      sql.append(") VALUES (");
      sql.append(parentId + ",");
      sql.append(processingID);
      sql.append(")");

      executeUpdate(sql.toString());

    } else if (findAccessionInTable("experiment", "experiment_id", accession) != 0) {

      int parentId = findAccessionInTable("experiment", "experiment_id", accession);
      sql.append("INSERT INTO processing_experiments (");
      sql.append("experiment_id, ");
      sql.append("processing_id ");
      sql.append(") VALUES (");
      sql.append(parentId + ",");
      sql.append(processingID);
      sql.append(")");

      executeUpdate(sql.toString());

    } else if (findAccessionInTable("sample", "sample_id", accession) != 0) {

      int parentId = findAccessionInTable("sample", "sample_id", accession);
      sql.append("INSERT INTO processing_samples (");
      sql.append("sample_id, ");
      sql.append("processing_id ");
      sql.append(") VALUES (");
      sql.append(parentId + ",");
      sql.append(processingID);
      sql.append(")");

      executeUpdate(sql.toString());

    } else {
      return (false);
    }
    return (true);
  }

  private int findAccessionInTable(String table, String idCol, int accession) throws SQLException {

    int result = 0;
    String sql = "select " + idCol + " from " + table + " where sw_accession = " + accession;
    ResultSet rs = null;
    try{
    rs = executeQuery(sql);
    if (rs.next()) {
      result = rs.getInt(idCol);
    } 
      } finally {
          DbUtils.closeQuietly(rs);
          // DBAccess.close(); causes problems since this is called privately
      }
    return (result);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue processing_event_to_task_group(int processingID, int parentIDs[], int[] childIDs,
          String algorithm, String description) {
    // Create a SQL statement
    StringBuffer sql = new StringBuffer();
    try {
      sql.append("UPDATE processing SET task_group = true WHERE processing_id = " + processingID);
      executeUpdate(sql.toString());

      if (algorithm != null) {
        sql = new StringBuffer();
        sql.append("UPDATE processing SET algorithm = '" + algorithm + "' WHERE processing_id = " + processingID);
        executeUpdate(sql.toString());
      }

      if (description != null) {
        sql = new StringBuffer();
        sql.append("UPDATE processing SET description = '" + description + "' WHERE processing_id = " + processingID);
        executeUpdate(sql.toString());
      }

      // Associate the processing entry with the zero or more parents
      this.associate_processing_event_with_parents_and_child(processingID, parentIDs, childIDs);
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString());
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql.toString() + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    /*
     * If no error, return success
     */
    ReturnValue ret = new ReturnValue();
    ret.setReturnValue(processingID);
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue add_task_group(int parentIDs[], int[] childIDs, String algorithm, String description) {
    int processingID;
    // Create a SQL statement
    StringBuffer sql = new StringBuffer();
    try {
      sql.append("INSERT INTO processing (create_tstmp, task_group, algorithm) VALUES( now(), true, '" + algorithm
              + "' )");
      processingID = InsertAndReturnNewPrimaryKey(sql.toString(), "processing_processing_id_seq");

      if (description != null) {
        sql = new StringBuffer();
        sql.append("UPDATE processing SET description = '" + description + "' WHERE processing_id = " + processingID);
        executeUpdate(sql.toString());
      }

      // Associate the processing entry with the zero or more parents
      this.associate_processing_event_with_parents_and_child(processingID, parentIDs, childIDs);
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString());
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql.toString() + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    /*
     * If no error, return success
     */
    ReturnValue ret = new ReturnValue();
    ret.setReturnValue(processingID);
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  /*
   * FIXME: this should check if association is already made, to make duplicates
   * impossible
   */
  public ReturnValue associate_processing_event_with_parents_and_child(int processingID, int[] parentIDs, int[] childIDs) {
    // Create a SQL statement
    StringBuffer sql = new StringBuffer();
    try {
      // Associate the processing entry with the zero or more parents
      if (parentIDs != null) {
        for (int parentID : parentIDs) {
          sql = new StringBuffer();

          sql.append("INSERT INTO processing_relationship (");
          sql.append("parent_id,");
          sql.append("child_id,");
          sql.append("relationship");
          sql.append(") VALUES (");
          sql.append(parentID + ",");
          sql.append(processingID + ",");
          sql.append("'parent-child'");
          sql.append(")");

          executeUpdate(sql.toString());
        }
      }

      // Associate the processing entry with the zero or more children
      if (childIDs != null) {
        for (int childID : childIDs) {
          sql = new StringBuffer();

          sql.append("INSERT INTO processing_relationship (");
          sql.append("parent_id,");
          sql.append("child_id,");
          sql.append("relationship");
          sql.append(") VALUES (");
          sql.append(processingID + ",");
          sql.append(childID + ",");
          sql.append("'parent-child'");
          sql.append(")");

          executeUpdate(sql.toString());
        }
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql.toString() + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    /*
     * If no error, return success
     */
    ReturnValue ret = new ReturnValue();
    ret.setReturnValue(processingID);
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue update_processing_status(int processingID, String status) {

    if (status == null) {
      return new ReturnValue(null, "String argument cannot be null", ReturnValue.INVALIDARGUMENT);
    }

    // Create a SQL statement
    StringBuffer sql = new StringBuffer();
    try {
      sql.append("UPDATE processing SET status = ");
      sql.append("'" + status + "'");
      sql.append(", update_tstmp='" + new Timestamp(System.currentTimeMillis()) + "' ");
      sql.append(" WHERE processing_id = " + processingID);

      executeUpdate(sql.toString());
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql.toString() + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    // On sucess, return empty ReturnValue
    return new ReturnValue();
  }

  /**
   * {@inheritDoc}
   */
  public int add_workflow_run(int workflowAccession) {
    int workflowId = 0;
    int id = 0;
    try {
      workflowId = findAccessionInTable("workflow", "workflow_id", workflowAccession);

      StringBuffer sql = new StringBuffer();
      sql.append("insert into workflow_run (workflow_id, create_tstmp, update_tstmp)");
      sql.append(" values (" + workflowId + ", now(), now())");
      id = InsertAndReturnNewPrimaryKey(sql.toString(), "workflow_run_workflow_run_id_seq");

    } catch (Exception e) {
      e.printStackTrace();
      return (0);
    }
    return (id);
  }

  /**
   * {@inheritDoc}
   */
  public int get_workflow_run_accession(int workflowRunId) {
    int result = 0;

    String sql = "select sw_accession from workflow_run where workflow_run_id = " + workflowRunId;
    ResultSet rs;
    try {
      rs = executeQuery(sql);
      if (rs.next()) {
        result = rs.getInt("sw_accession");
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
      return (-1);
    }
    return (result);
  }

  /**
   * {@inheritDoc}
   */
  public int get_workflow_run_id(int workflowRunAccession) {
    int result = 0;

    String sql = "select workflow_run_id from workflow_run where sw_accession = " + workflowRunAccession;
    ResultSet rs;
    try {
      rs = executeQuery(sql);
      if (rs.next()) {
        result = rs.getInt("workflow_run_id");
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
      return (-1);
    }
    return (result);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WorkflowRun getWorkflowRun(int workflowRunAccession) {

    WorkflowRun wr = null;

    String sql = "select workflow_run_id, name, ini_file, cmd, workflow_template, dax, "
            + "status, status_cmd, seqware_revision, host, stderr, stdout, "
            + "current_working_dir, username, create_tstmp from workflow_run where sw_accession = " + workflowRunAccession;
    ResultSet rs;
    try {
      rs = executeQuery(sql);
      if (rs.next()) {
        wr = new WorkflowRun();
        wr.setWorkflowRunId(rs.getInt("workflow_run_id"));
        wr.setName(rs.getString("name"));
        wr.setIniFile(rs.getString("ini_file"));
        wr.setCommand(rs.getString("cmd"));
        wr.setTemplate(rs.getString("workflow_template"));
        wr.setDax(rs.getString("dax"));
        wr.setStatus(rs.getString("status"));
        wr.setStatusCmd(rs.getString("status_cmd"));
        wr.setHost(rs.getString("host"));
        wr.setCurrentWorkingDir(rs.getString("current_working_dir"));
        wr.setStdErr(rs.getString("stderr"));
        wr.setStdOut(rs.getString("stdout"));
        wr.setCreateTimestamp(rs.getDate("create_tstmp"));
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
    }

    return (wr);

  }

  /**
   * {@inheritDoc}
   */
  public void add_workflow_run_ancestor(int workflowRunAccession, int processingId) {
    int workflowRunId = 0;
    try {
      workflowRunId = findAccessionInTable("workflow_run", "workflow_run_id", workflowRunAccession);

      StringBuffer sql = new StringBuffer();
      sql.append("update processing set ancestor_workflow_run_id = ").append(workflowRunId).append(", update_tstmp='")
              .append(new Timestamp(System.currentTimeMillis())).append("' where processing_id = ").append(processingId);
      executeUpdate(sql.toString());

    } catch (Exception e) {
      logger.error("SQL Command failed: " + e.getMessage() + ":" + e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue update_processing_workflow_run(int processingID, int workflowRunAccession) {

    // Create a SQL statement
    int workflowRunId = 0;
    StringBuffer sql = new StringBuffer();
    try {
      workflowRunId = findAccessionInTable("workflow_run", "workflow_run_id", workflowRunAccession);

      sql.append("UPDATE processing SET workflow_run_id = ");
      sql.append(workflowRunId);
      sql.append(", update_tstmp='" + new Timestamp(System.currentTimeMillis()) + "' ");
      sql.append(" WHERE processing_id = " + processingID);

      executeUpdate(sql.toString());
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql.toString() + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    // On sucess, return empty ReturnValue
    return new ReturnValue();
  }

  /**
   * {@inheritDoc}
   */
  public ReturnValue update_workflow_run(int workflowRunId, String pegasusCmd, String workflowTemplate, String status,
          String statusCmd, String workingDirectory, String dax, String ini, String host, int currStep, int totalSteps,
          String stdErr, String stdOut, String workflowengine) {

    // metadata.update_workflow_run(workflowRunId, pegasusCmd, template,
    // "pending", statusCmd, wi.getWorkflowDir(), daxReader.toString(),
    // mapBuffer.toString());

    ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

    // Create a SQL statement
    // TODO: need to add the currStep, stderr, etc
    try {
      //
      String sql = "UPDATE workflow_run SET status = " + formatSQL(status, "pending") + ", cmd = "
              + formatSQL(pegasusCmd, null) + ", workflow_template = " + formatSQL(workflowTemplate, null) + ", dax = "
              + formatSQL(dax, null) + ", status_cmd = " + formatSQL(statusCmd, null) + ", current_working_dir = "
              + formatSQL(workingDirectory, null) + ", ini_file = " + formatSQL(ini, null) + ", host = "
              + formatSQL(host, null) + ", stderr = " + formatSQL(stdErr, null) + ", stdout = " + formatSQL(stdOut, null)
              + ", workflow_engine = " + formatSQL(workflowengine, null) 
              + ", update_tstmp='" + new Timestamp(System.currentTimeMillis())
              + "' where workflow_run_id = "+ workflowRunId;
            
      executeUpdate(sql);
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql + "\n" + e.getMessage());
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    return (ret);

  }

  /**
   * <p>linkProcessingAndFile.</p>
   *
   * @param processingID a int.
   * @param fileID a int.
   * @throws java.sql.SQLException if any.
   */
  public void linkProcessingAndFile(int processingID, int fileID) throws SQLException {
    StringBuffer sql;
    sql = new StringBuffer();
    sql.append("INSERT INTO processing_files (");
    sql.append("processing_id,");
    sql.append("file_id");
    sql.append(") VALUES (");
    sql.append(processingID + ",");
    sql.append(fileID);
    sql.append(")");
    executeUpdate(sql.toString());
  }

  private String sqlQuote(String value) {
    return value == null ? "null" : "'" + value + "'";
  }

  private String sqlQuote(Long value) {
    return value == null ? "null" : value.toString();
  }

  private int insertFileRecord(FileMetadata file) throws SQLException {

    String sql = "INSERT INTO FILE (file_path, meta_type, type, description, url, url_label, md5sum, size) "
            + " VALUES (%s, %s, %s, %s, %s, %s, %s, %s)";

    sql = String.format(sql, sqlQuote(file.getFilePath()), sqlQuote(file.getMetaType()), sqlQuote(file.getType()),
            sqlQuote(file.getDescription()), sqlQuote(file.getUrl()), sqlQuote(file.getUrlLabel()), sqlQuote(file.getMd5sum()),
            sqlQuote(file.getSize()));

    try {
      /*
       * Create a processing_files entry
       */
      return InsertAndReturnNewPrimaryKey(sql, "file_file_id_seq");
    } catch (SQLException e) {
      logger.error("Error executing sql: " + sql + ":" + e.getMessage());
      throw e;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue update_processing_event(int processingID, ReturnValue retval) {
    // Create a SQL statement
    StringBuffer sql = new StringBuffer();
    ArrayList params = new ArrayList();
    try {
      // FIXME: Update a processing entry from ReturnValue
      sql.append("UPDATE processing SET ");
      // FIXME: sql.append("sequencer_run_id,");
      // FIXME: sql.append("workflow_id,");

      // exit_status integer
      if (retval.getExitStatus() != ReturnValue.NULL) {
        sql.append("exit_status = ?, ");
        params.add(retval.getExitStatus());
      }

      // process_exit_status integer
      if (retval.getProcessExitStatus() != ReturnValue.NULL) {
        sql.append("process_exit_status = ?, ");
        params.add(retval.getProcessExitStatus());
      }

      // algorithm = text
      if (retval.getAlgorithm() != null) {
        sql.append("algorithm = ?, ");
        params.add(retval.getAlgorithm());
      }

      // parameters text
      if (retval.getParameters() != null) {
        sql.append("parameters = ?, ");
        params.add(retval.getParameters());
      }

      // parameters text
      if (retval.getDescription() != null) {
        sql.append("description = ?, ");
        params.add(retval.getDescription());
      }

      // version text
      if (retval.getVersion() != null) {
        sql.append("version = ?, ");
        params.add(retval.getVersion());
      }

      // url text
      if (retval.getUrl() != null) {
        sql.append("url = ?, ");
        params.add(retval.getUrl());
      }

      // urlLabel text
      if (retval.getUrlLabel() != null) {
        sql.append("url_label = ?, ");
        params.add(retval.getUrlLabel());
      }

      // stdout text
      if (retval.getStdout() != null) {
        sql.append("stdout = ?, ");
        params.add(retval.getStdout());
      }

      // stderr text
      if (retval.getStderr() != null) {
        sql.append("stderr = ?, ");
        params.add(retval.getStderr());
      }

      // run start timestamp
      if (retval.getRunStartTstmp() != null) {
        sql.append("run_start_tstmp = ?, ");
        params.add(new Timestamp(retval.getRunStartTstmp().getTime()));
      }

      // run stop timestamp
      if (retval.getRunStopTstmp() != null) {
        sql.append("run_stop_tstmp = ?, ");
        params.add(new Timestamp(retval.getRunStopTstmp().getTime()));
      }

      // create_tstmp timestamp and update_tstmp
      sql.append("update_tstmp = now()");
      sql.append(" WHERE processing_id = " + processingID);

      // Execute above
      PreparedStatement ps = null;
      try{
      ps = this.getDb().prepareStatement(sql.toString());
      for (int i = 0; i < params.size(); i++) {
        ps.setObject(i + 1, params.get(i));
      }
      ps.executeUpdate();
      } finally{
          DbUtils.closeQuietly(ps);
      }

      // Add and associate files for each item
      if (retval.getFiles() != null) {
        for (FileMetadata file : retval.getFiles()) {
          // If the file path is empty, warn and skip
          if (file.getFilePath().compareTo("") == 0) {
            logger.warn("WARNING: Skipping empty FilePath for ProcessingID entry: " + processingID);
            continue;
          }

          // If the meta type is empty, warn and skip
          if (file.getMetaType().compareTo("") == 0) {
            logger.warn("WARNING: Skipping empty MetaType for ProcessingID entry: " + processingID);
            continue;
          }

          // Add a new file entry
          int fileID = insertFileRecord(file);

          linkProcessingAndFile(processingID, fileID);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql.toString() + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    /*
     * If no error, return success
     */
    return new ReturnValue();
  }

  /**
   * {@inheritDoc}
   *
   * Connect to a database for future use
   */
  @Override
  public ReturnValue init(String database, String username, String password) {
    // FIXME: Do we need to do this or not? If so, how do we do it abstractly to
    // support different meta-db backends?
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    try {
      this.setDb(DriverManager.getConnection(database, username, password));
    } catch (SQLException e) {
      e.printStackTrace();
      return new ReturnValue(null, "Could not connect to SQL database: " + e.getMessage(),
              ReturnValue.DBCOULDNOTINITIALIZE);
    }
    return init();

  }

  /**
   * Connect to a database for future use
   *
   * @param ds a {@link javax.sql.DataSource} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue init(DataSource ds) {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
      Log.fatal("Class not found: " + e1.getMessage(), e1);
    }
    
    // try to output some information on connections
    if (ds instanceof BasicDataSource){
        BasicDataSource bds = (BasicDataSource)ds;
        Log.info("Tomcat Basic data source init with: Log-abandoned =" +  bds.getLogAbandoned() + 
                " Max-active: " + bds.getMaxActive() +  
                " Max-wait: " + bds.getMaxWait() + 
                " Remove-abandoned: " + bds.getRemoveAbandoned() + 
                " Remove-abandoned-timeout: " + bds.getRemoveAbandonedTimeout() + 
                " Num-active: " + bds.getNumActive() + 
                " Num-idle: " + bds.getNumIdle());
        bds.getMaxWait();
    }

    try {
      this.setDb(ds.getConnection());
    } catch (SQLException e) {
      e.printStackTrace();
      Log.fatal("init()  could not connect to SQL database", e);
      return new ReturnValue(null, "Could not connect to SQL database: " + e.getMessage(),
              ReturnValue.DBCOULDNOTINITIALIZE);
    }
    return init();
  }

  private ReturnValue init() {
    try {
      DatabaseMetaData foo2 = this.getDb().getMetaData();
      this.setDbmd(foo2);

      this.setDbmd(this.getDb().getMetaData());
    } catch (SQLException e) {
      return new ReturnValue(null,
              "Could not retreive Connection Metadata from Database. Assuming connection was not successful: "
              + e.getMessage(), ReturnValue.DBCOULDNOTINITIALIZE);
    }

    Log.debug("init() and create statement");
    // Create a SQL statement and preparedStatement
    try {
      this.setSql(this.getDb().createStatement());
    } catch (SQLException e) {
      Log.debug("init() could not create a Statement", e);
      return new ReturnValue(null, "Could not create a SQL statement" + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    Log.debug("init() of  " + this.toString());
    // If no error so far, return Meta information
    try {
      return new ReturnValue(null, "Connection to " + dbmd.getDatabaseProductName() + " "
              + dbmd.getDatabaseProductVersion() + " successful.\n", ReturnValue.SUCCESS);
    } catch (SQLException e) {
      return new ReturnValue(null,
              "Could not parse Connection Metadata from Database. Assuming connection was not successful: "
              + e.getMessage(), ReturnValue.DBCOULDNOTINITIALIZE);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue clean_up() {
    Log.debug("clean_up() of MetadataDB occured " + Integer.toHexString(this.hashCode()));
    ReturnValue ret = new ReturnValue();

    try {
      if (this.getSql() != null) {
          Log.info("clean_up() of statement " + Integer.toHexString(this.getSql().hashCode()));
          this.getSql().close();
          // we need to explicitly set the statement to null to avoid the risk of closing it twice
          this.setSql(null);
      }
      if (this.getDb() != null) {
          Log.info("clean_up() of connection " + Integer.toHexString(this.getDb().hashCode()));
        this.getDb().close();
        // we need to explicitly set the connection to null to avoid the risk of closing it tiwce
        // see https://tomcat.apache.org/tomcat-6.0-doc/jndi-datasource-examples-howto.html
        this.setDb(null);
      }
    } catch (SQLException e) {
      Log.fatal("clean_up() of occured, with exception " + e);
      ret.setStderr("Failed to close database connection: " + e.getMessage());
      ret.setExitStatus(ReturnValue.DBCOULDNOTDISCONNECT);
    }

    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ArrayList<String> fix_file_paths(String prefix, ArrayList<String> files) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * <p>Getter for the field
   * <code>db</code>.</p>
   *
   * @return a {@link java.sql.Connection} object.
   */
  public Connection getDb() {
    return db;
  }

  /**
   * <p>Setter for the field
   * <code>db</code>.</p>
   *
   * @param db a {@link java.sql.Connection} object.
   */
  public void setDb(Connection db) {
    if (db != null) {
        Log.debug("MetadataDB set connection + " + Integer.toHexString(db.hashCode()));
    }
    this.db = db;
  }

  /**
   * <p>Getter for the field
   * <code>dbmd</code>.</p>
   *
   * @return a {@link java.sql.DatabaseMetaData} object.
   */
  public DatabaseMetaData getDbmd() {
    return dbmd;
  }

  /**
   * <p>Setter for the field
   * <code>dbmd</code>.</p>
   *
   * @param dbmd a {@link java.sql.DatabaseMetaData} object.
   */
  public void setDbmd(DatabaseMetaData dbmd) {
    this.dbmd = dbmd;
  }

  /**
   * <p>Getter for the field
   * <code>sql</code>.</p>
   *
   * @return a {@link java.sql.Statement} object.
   */
  public Statement getSql() {
    return sql;
  }

  /**
   * <p>Setter for the field
   * <code>sql</code>.</p>
   *
   * @param sql a {@link java.sql.Statement} object.
   */
  public void setSql(Statement sql) {
    if (sql != null) {
        Log.debug("MetadataDB set statement + " + Integer.toHexString(sql.hashCode()));
    }
    this.sql = sql;
  }

    /**
     * {@inheritDoc}
     *
     */
    
    
  public ReturnValue addWorkflow(String name, String version, String description, String baseCommand, String configFile, String templateFile, String provisionDir, boolean storeProvisionDir, String archiveZip, boolean storeArchiveZip, String workflowClass, String workflowType, String workflowEngine) {

    ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

    // figure out the correct command
    String command = baseCommand + " ";
    command = command.replaceAll("\\$\\{workflow_bundle_dir\\}", provisionDir);

    // make the base workflow row
    int workflowId = 0;

    // Create a SQL statement
    StringBuffer sql = new StringBuffer();
    try {

      sql.append("INSERT INTO workflow (name, description, version, base_ini_file, cmd, current_working_dir, permanent_bundle_location, workflow_template, create_tstmp, update_tstmp, workflow_engine, workflow_class, workflow_type) "
              + "VALUES( '"
              + name
              + "', '"
              + description
              + "', '"
              + version
              + "', '"
              + configFile
              + "', '"
              + command
              + "', '" 
              + provisionDir 
              + "', '" 
              + archiveZip 
              + "', '" 
              + templateFile 
              + "', '" 
              + "now()"
              + "', '" 
              + "now()"
              + "', '" 
              + workflowEngine
              + "', '" 
              + workflowClass
              + "', '" 
              + workflowType
              + ")");

      // get back last ID value
      workflowId = InsertAndReturnNewPrimaryKey(sql.toString(), "workflow_workflow_id_seq");
      ret.setReturnValue(workflowId);
      // open the ini file and parse each item
      // FIXME: this assumes there is one ini file which is generally fine for
      // bundled workflows but we could make this more flexible
      HashMap<String, Map<String, String>> hm = new HashMap<String, Map<String, String>>();
      MapTools.ini2RichMap(configFile, hm);

      // foreach workflow param add an entry in the workflow_param table
      for (String key : hm.keySet()) {
        Map<String, String> details = hm.get(key);
        String display = "false";
        if ("T".equals(details.get("display"))) {
          display = "true";
        }
        String insert = "insert into workflow_param (workflow_id, type, key, display, display_name, file_meta_type, default_value) values ( "
                + workflowId
                + ", "
                + formatSQL(details.get("type"), "text")
                + ", "
                + formatSQL(details.get("key"), null)
                + ", "
                + display
                + ", "
                + formatSQL(details.get("display_name"), details.get("key"))
                + ", "
                + formatSQL(details.get("file_meta_type"), null)
                + ", "
                + formatSQL(details.get("default_value"), null)
                + ")";
        int workflowParamId = InsertAndReturnNewPrimaryKey(insert, "workflow_param_workflow_param_id_seq");

        // at this point everything should be setup unless it's of type
        // "pulldown", in which case we need to populate the pulldown table
        if ("pulldown".equals(details.get("type")) && details.get("pulldown_items") != null) {

          String[] pulldowns = details.get("pulldown_items").split(";");
          for (String pulldown : pulldowns) {
            String[] kv = pulldown.split("\\|");
            if (kv.length == 2) {
              String pulldownInsert = "insert into workflow_param_value (workflow_param_id, display_name, value) values ( "
                      + workflowParamId + ", " + formatSQL(kv[0], kv[0]) + ", " + formatSQL(kv[1], kv[1]) + ")";
              int workflowParamValueId = InsertAndReturnNewPrimaryKey(pulldownInsert,
                      "workflow_param_value_workflow_param_value_id_seq");
            }
          }
        }

      }

      // TODO: need to add support for pulldowns!

    } catch (SQLException e) {
      logger.error("SQL Command failed: " + e.getMessage() + ":" + e.getMessage());
      return new ReturnValue(null, "Could not execute one of the SQL commands: " + sql.toString() + "\nException: "
              + e.getMessage(), ReturnValue.SQLQUERYFAILED);
    }

    // add default params in workflow_param table

    return (ret);
  }

  private String formatSQL(String variable, String defaultStr) {

    if ((variable == null || "".equals(variable)) && (defaultStr != null && !"".equals(defaultStr))) {
      String newDefaultStr = defaultStr.replaceAll("'", "");
      return ("'" + newDefaultStr + "'");
    } else if ((variable == null || "".equals(variable)) && (defaultStr == null || "".equals(defaultStr))) {
      return ("NULL");
    } else {
      String newVariable = variable.replaceAll("'", "");
      return ("'" + newVariable + "'");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> get_workflow_info(int workflowAccession) {

    HashMap<String, String> map = new HashMap<String, String>();
    String sql = "SELECT name, description, version, base_ini_file, cmd, current_working_dir, workflow_template, create_tstmp, update_tstmp, permanent_bundle_location, workflow_engine, workflow_type, workflow_class FROM workflow where sw_accession = "
            + workflowAccession;
    ResultSet rs;

    try {
      rs = executeQuery(sql);
      if (rs.next()) {
        map.put("name", rs.getString("name"));
        map.put("description", rs.getString("description"));
        map.put("version", rs.getString("version"));
        map.put("base_ini_file", rs.getString("base_ini_file"));
        map.put("cmd", rs.getString("cmd"));
        map.put("current_working_dir", rs.getString("current_working_dir"));
        map.put("workflow_template", rs.getString("workflow_template"));
        map.put("create_tstmp", rs.getString("create_tstmp"));
        map.put("update_tstmp", rs.getString("update_tstmp"));
        map.put("workflow_accession", new Integer(workflowAccession).toString());
        map.put("permanent_bundle_location", rs.getString("permanent_bundle_location"));
        map.put("workflow_engine", rs.getString("workflow_engine"));
        map.put("workflow_type", rs.getString("workflow_type"));
        map.put("workflow_class", rs.getString("workflow_class"));
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
      return (null);
    }

    return (map);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue saveFileForIus(int workflowRunId, int iusAccession, FileMetadata file, int processingId) {
    ReturnValue returnVal = new ReturnValue(ReturnValue.SUCCESS);
    try {
      int fileId = insertFileRecord(file);
      if (!linkWorkflowRunAndParent(workflowRunId, iusAccession)) {
        update_processing_status(processingId, Metadata.FAILED);
        returnVal = new ReturnValue(ReturnValue.INVALIDPARAMETERS);
        return returnVal;
      }
      update_processing_workflow_run(processingId, get_workflow_run_accession(workflowRunId));
      linkProcessingAndFile(processingId, fileId);
      update_processing_status(processingId, Metadata.SUCCESS);

    } catch (SQLException e) {
      e.printStackTrace();
      returnVal = new ReturnValue(ReturnValue.SQLQUERYFAILED);
      if (processingId != Integer.MIN_VALUE) {
        update_processing_status(processingId, Metadata.FAILED);
      }
    }

    return returnVal;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean isDuplicateFile(String filepath) {

    String sql = "SELECT sw_accession FROM file WHERE file_path = '" + filepath + "';";
    ResultSet rs;

    try {
      rs = executeQuery(sql);
      if (rs.next()) {
        return true;
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
      e.printStackTrace();
      return null;
    }

    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue updateWorkflow(int workflowId, String permanentBundleLocation) {
    String sql = "UPDATE workflow SET " + "permanent_bundle_location = '" + permanentBundleLocation + "',"
            + " update_tstmp='" + new Timestamp(System.currentTimeMillis()) + "' " + "WHERE workflow_id = " + workflowId;
    ReturnValue ret = new ReturnValue();

    try {
      executeUpdate(sql.toString());
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
      e.printStackTrace();
      ret.setExitStatus(ReturnValue.SQLQUERYFAILED);
    }
    return ret;
  }

  /**
   * <p>listInstalledWorkflows.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String listInstalledWorkflows() {
    String sql = "SELECT name, version, sw_accession, create_tstmp, permanent_bundle_location FROM workflow";
    StringBuffer sb = new StringBuffer();
    try {
      ResultSet rs = executeQuery(sql);
      while (rs.next()) {
        sb.append(rs.getString("name") + "\t");
        sb.append(rs.getString("version") + "\t");
        sb.append(rs.getString("create_tstmp") + "\t");
        sb.append(rs.getString("sw_accession") + "\t");
        sb.append(rs.getString("permanent_bundle_location") + "\n");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return (sb.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  // FIXME: need to implement this for this backend type
  public String listInstalledWorkflowParams(String workflowAccession) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public int getWorkflowAccession(String name, String version) {
    int result = 0;

    String sql = "select sw_accession from workflow where name = '" + name + "' AND version = '" + version + "'";
    ResultSet rs;
    try {
      rs = executeQuery(sql);
      if (rs.next()) {
        result = rs.getInt("sw_accession");
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
      return (-1);
    }
    return (result);
  }

  /**
   * <p>executeQuery.</p>
   *
   * @param s a {@link java.lang.String} object.
   * @return a {@link java.sql.ResultSet} object.
   * @throws java.sql.SQLException if any.
   */
  public ResultSet executeQuery(String s) throws SQLException {
    Statement sql1 = getSql();
    Log.debug("MetadataDB executeQuery: \"" + s + "\" on connection + " + Integer.toHexString(this.getDb().hashCode()));
    Log.debug("MetadataDB executeQuery: query is " + (s == null ? "null": "not null"));
    Log.debug("MetadataDB executeQuery: statement "+Integer.toHexString(sql1.hashCode())+" is " + (sql1 == null ? "null": "not null"));
    return sql1.executeQuery(s);
  }

  /**
   * <p>executeUpdate.</p>
   *
   * @param s a {@link java.lang.String} object.
   * @return a int.
   * @throws java.sql.SQLException if any.
   */
  public int executeUpdate(String s) throws SQLException {
    logger.debug("MetadataDB executeUpdate:" + s);
    return getSql().executeUpdate(s);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ReturnValue> findFilesAssociatedWithASequencerRun(String sequencerRunName) {
    throw new NotImplementedException("Please use the Symlinker through the Web service.");
  }

  /**
   * {@inheritDoc}
   */
  public List<WorkflowRun> getWorkflowRunsByStatus(String status) {

    ArrayList<WorkflowRun> results = new ArrayList<WorkflowRun>();

    String sql = "select workflow_run_id, name, ini_file, cmd, workflow_template, dax, status, status_cmd, seqware_revision, host, current_working_dir, username, create_tstmp from workflow_run where status = '"
            + status + "'";
    ResultSet rs;
    try {
      rs = executeQuery(sql);
      while (rs.next()) {
        WorkflowRun wr = new WorkflowRun();
        wr.setWorkflowRunId(rs.getInt("workflow_run_id"));
        wr.setName(rs.getString("name"));
        wr.setIniFile(rs.getString("ini_file"));
        wr.setCommand(rs.getString("cmd"));
        wr.setTemplate(rs.getString("workflow_template"));
        wr.setDax(rs.getString("dax"));
        wr.setStatus(rs.getString("status"));
        wr.setStatusCmd(rs.getString("status_cmd"));
        wr.setHost(rs.getString("host"));
        wr.setCurrentWorkingDir(rs.getString("current_working_dir"));
        wr.setCreateTimestamp(rs.getDate("create_tstmp"));
        // FIXME: need to update with workflow engine etc
        results.add(wr);
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
    }

    return (results);
  }

  /**
   * {@inheritDoc}
   */
  public List<WorkflowRun> getWorkflowRunsByHost(String host) {
    ArrayList<WorkflowRun> results = new ArrayList<WorkflowRun>();

    String sql = "select workflow_run_id, name, ini_file, cmd, workflow_template, dax, status, status_cmd, seqware_revision, host, current_working_dir, username, create_tstmp from workflow_run where host = '"
            + host + "'";
    ResultSet rs;
    try {
      rs = executeQuery(sql);
      while (rs.next()) {
        WorkflowRun wr = new WorkflowRun();
        wr.setWorkflowRunId(rs.getInt("workflow_run_id"));
        wr.setName(rs.getString("name"));
        wr.setIniFile(rs.getString("ini_file"));
        wr.setCommand(rs.getString("cmd"));
        wr.setTemplate(rs.getString("workflow_template"));
        wr.setDax(rs.getString("dax"));
        wr.setStatus(rs.getString("status"));
        wr.setStatusCmd(rs.getString("status_cmd"));
        wr.setHost(rs.getString("host"));
        wr.setCurrentWorkingDir(rs.getString("current_working_dir"));
        wr.setCreateTimestamp(rs.getDate("create_tstmp"));
        results.add(wr);
      }
    } catch (SQLException e) {
      logger.error("SQL Command failed: " + sql.toString() + ":" + e.getMessage());
    }

    return (results);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WorkflowRun getWorkflowRunWithWorkflow(String workflowRunAccession) {
    throw new NotImplementedException("This is currently not implemented for a direct DB connection!");
  }

  /**
   * <p>getAllStudies.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Study> getAllStudies() {
    throw new NotImplementedException("All studies must be retrieved through webservice");
  }

  /**
   * <p>getSequencerRunReport.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getSequencerRunReport() {
    return (null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateIUS(int iusSWID, IUSAttribute iusAtt, Boolean skip) {
    throw new NotImplementedException("Updating IUSes must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateLane(int laneSWID, LaneAttribute laneAtt, Boolean skip) {
    throw new NotImplementedException("Updating Lanes must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateSequencerRun(int sequencerRunSWID, SequencerRunAttribute sequencerRunAtt, Boolean skip) {
    throw new NotImplementedException("Updating Sequencer Runs must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateExperiment(int experimentSWID, ExperimentAttribute att, Boolean skip) {
    throw new NotImplementedException("Updating Experiment annotation must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateProcessing(int processingSWID, ProcessingAttribute att, Boolean skip) {
    throw new NotImplementedException("Updating processing attribute must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateSample(int sampleSWID, SampleAttribute att, Boolean skip) {
    throw new NotImplementedException("Updating sample attribute must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateStudy(int studySWID, StudyAttribute att, Boolean skip) {
    throw new NotImplementedException("Updating Study attribute must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getWorkflowRunReport(int workflowRunSWID) {
    throw new NotImplementedException("Retrieving workflow run reports must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getWorkflowRunReport(int workflowSWID, Date earliestDate, Date latestDate) {
    throw new NotImplementedException("Retrieving workflow run reports must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getWorkflowRunReport(Date earliestDate, Date latestDate) {
    throw new NotImplementedException("Retrieving workflow run reports must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public net.sourceforge.seqware.common.model.File getFile(int swAccession) {
    throw new NotImplementedException("Retrieving files must be performed through the webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<WorkflowParam> getWorkflowParams(String swAccession) {
    throw new NotImplementedException("Retrieving workflow params must be performed through the webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateWorkflow(int workflowSWID, WorkflowAttribute att, Boolean skip) {
    throw new NotImplementedException("Updating processing attribute must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateWorkflowRun(int workflowrunSWID, WorkflowRunAttribute att, Boolean skip) {
    throw new NotImplementedException("Updating processing attribute must be performed through webservice");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateIUS(int laneSWID, Set<IUSAttribute> iusAtts) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateLane(int laneSWID, Set<LaneAttribute> laneAtts) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateSequencerRun(int sequencerRunSWID, Set<SequencerRunAttribute> sequencerRunAtts) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateExperiment(int experimentSWID, Set<ExperimentAttribute> atts) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateProcessing(int processingSWID, Set<ProcessingAttribute> atts) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateSample(int sampleSWID, Set<SampleAttribute> atts) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateStudy(int studySWID, Set<StudyAttribute> atts) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateWorkflow(int workflowSWID, Set<WorkflowAttribute> atts) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void annotateWorkflowRun(int workflowSWID, Set<WorkflowRunAttribute> atts) {
    // TODO Auto-generated method stub
  }

  public ReturnValue addSequencerRun(Integer platformAccession, String name, String description, boolean pairdEnd, boolean skip) {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  public ReturnValue addLane(Integer sequencerRunAccession, Integer studyTypeId, Integer libraryStrategyId, Integer librarySelectionId, Integer librarySourceId, String name, String description, String cycleDescriptor, boolean skip, Integer laneNumber) {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  public ReturnValue addIUS(Integer laneAccession, Integer sampleAccession, String name, String description, String barcode, boolean skip) {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  public List<Platform> getPlatforms() {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  public List<Organism> getOrganisms() {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  public List<StudyType> getStudyTypes() {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  public List<LibraryStrategy> getLibraryStrategies() {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  public List<LibrarySelection> getLibrarySelections() {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  public List<LibrarySource> getLibrarySource() {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  @Override
  public String getProcessingRelations(String swAccession) {

     return null;
  }

  @Override
  public String getWorkflowRunReportStdErr(int workflowRunSWID) {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

  @Override
  public String getWorkflowRunReportStdOut(int workflowRunSWID) {
    throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
  }

    @Override
    public Workflow getWorkflow(int workflowAccession) {
        throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<ReturnValue> findFilesAssociatedWithASample(String sampleName, boolean requireFiles) {
         throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<ReturnValue> findFilesAssociatedWithAStudy(String studyName, boolean requireFiles) {
         throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<ReturnValue> findFilesAssociatedWithASequencerRun(String sequencerRunName, boolean requireFiles) {
         throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<SequencerRun> getAllSequencerRuns() {
        throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<Lane> getLanesFrom(int sequencerRunAccession) {
        throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<IUS> getIUSFrom(int laneOrSampleAccession) {
        throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<Experiment> getExperimentsFrom(int studyAccession) {
        throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<Sample> getSamplesFrom(int experimentAccession) {
        throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<Sample> getChildSamplesFrom(int parentSampleAccession) {
        throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<Sample> getParentSamplesFrom(int childSampleAccession) {
        throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithFiles(List<Integer> fileAccessions, String search_type) {
        throw new NotImplementedException("This method is not supported through the direct MetaDB connection!");
    }
}
