package net.sourceforge.seqware.queryengine.webservice.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import net.sourceforge.seqware.queryengine.webservice.util.EnvUtil;

/**
 * <p>MetadataDB class.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class MetadataDB {

  /* private static final SessionFactory sessionFactory = buildSessionFactory();
  
  private static SessionFactory buildSessionFactory() {
    try {
        // Create the SessionFactory from hibernate.cfg.xml
      return new Configuration().configure("hibernate.properties").buildSessionFactory();
    }
    catch (Throwable ex) {
        // Make sure you log the exception, as it might be swallowed
        System.err.println("Initial SessionFactory creation failed." + ex);
        throw new ExceptionInInitializerError(ex);
    }
  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }*/
  
  
  private Connection setupConnection() {

    // load JDBC driver
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException cnfe) {
      System.out.println("Couldn't find the driver!");
      System.out.println("Let's print a stack trace, and exit.");
      cnfe.printStackTrace();
      return(null);
    }

    System.out.println("Registered the driver ok, so let's make a connection.");

    Connection c = null;

    try {
      // The second and third arguments are the username and password,
      // respectively. They should be whatever is necessary to connect
      // to the database.
      String server = EnvUtil.getProperty("dbserver");
      String db = EnvUtil.getProperty("db");
      String user = EnvUtil.getProperty("user");
      String pass = EnvUtil.getProperty("pass");
      c = DriverManager.getConnection("jdbc:postgresql://"+server+"/"+db,
          user, pass);

    } catch (SQLException se) {
      System.out.println("Couldn't connect: print out a stack trace and exit.");
      se.printStackTrace();
      return(null);
    }
    return (c);
  }

  /**
   * <p>getMetadata.</p>
   *
   * @return a {@link java.util.ArrayList} object.
   */
  public ArrayList getMetadata() {
    return(genericGetMetadata("SELECT f.file_path, p.description, p.sw_accession, p.parameters, p.create_tstmp, f.meta_type FROM processing as p, file as f, processing_files as pf " +
        "WHERE f.file_id = pf.file_id AND p.processing_id = pf.processing_id AND (f.meta_type = 'application/seqware-qe-vdb' OR f.meta_type = 'application/seqware-qe-db' OR" +
        " f.meta_type = 'application/seqware-qe-hbase-db' OR  f.meta_type = 'application/seqware-qe-postgresql-db' OR f.meta_type = 'application/seqware-qe-berkeleydb-db') AND " +
        "(p.status = 'processed' OR p.status = 'success') ORDER BY p.sw_accession"));
  }
  
  /**
   * <p>getTagMetadata.</p>
   *
   * @return a {@link java.util.ArrayList} object.
   */
  public ArrayList getTagMetadata() {
    return(genericGetMetadata("SELECT f.file_path, p.description, p.sw_accession, p.parameters, p.create_tstmp, f.meta_type FROM processing as p, file as f, processing_files as pf" +
    		" WHERE f.file_id = pf.file_id AND p.processing_id = pf.processing_id AND f.meta_type = 'application/seqware-qe-tags' AND (p.status = 'processed' OR p.status = 'success') " +
    		"ORDER BY p.sw_accession"));
  }

  
  /**
   * <p>getWorkflowMetadata.</p>
   *
   * @return a {@link java.util.ArrayList} object.
   */
  public ArrayList<Map<String, String>> getWorkflowMetadata() {
    return(moreGenericGetMetadata("SELECT name, sw_accession, description, version, seqware_version FROM workflow"));
  }
  
  /**
   * <p>getWorkflowParamMetadata.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link java.util.ArrayList} object.
   */
  public ArrayList<Map<String, String>> getWorkflowParamMetadata(Integer id) {
    return(moreGenericGetMetadata("SELECT type, key, display, file_meta_type, default_value FROM workflow_param where workflow_id = "+id));
  }
  
 /**
  * <p>getFilePath.</p>
  *
  * @param accession a int.
  * @return a {@link java.lang.String} object.
  */
 public String getFilePath(int accession) {
        
    Connection c = setupConnection();

    String filePath = null;
    
    Statement s = null;
    try {
      s = c.createStatement();
      ResultSet rs = null;
      rs = s.executeQuery("select file_path from file where sw_accession = "+accession);

      if (rs.next()) {
        filePath = rs.getString(1);
      }
      
      rs.close();
      s.close();
      c.close();
      
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
          "that probably means our SQL is invalid:\n"+se.getMessage()+"\n");
          se.printStackTrace();
          return(null);
    }
    return(filePath);
 }
  
  /**
   * <p>getWorkflowRunInfo.</p>
   *
   * @param workflowRunAccession a int.
   * @return a {@link java.util.HashMap} object.
   */
  public HashMap<String,Object> getWorkflowRunInfo(int workflowRunAccession) {
    
    HashMap<String,Object> d = new HashMap<String,Object>();
    
    Connection c = setupConnection();
    int workflowRunId = 0;

    Statement s = null;
    try {
      s = c.createStatement();
      ResultSet rs = null;
      rs = s.executeQuery("select workflow_run_id, status, create_tstmp, update_tstmp, host from workflow_run where sw_accession = "+workflowRunAccession);

      if (rs.next()) {
        workflowRunId = rs.getInt(1);
        String status = rs.getString(2);
        Timestamp start = rs.getTimestamp(3);
        Timestamp stop = rs.getTimestamp(4);
        String host = rs.getString(5);
        d.put("status", status);
        d.put("create_tsmp", start.toString());
        d.put("update_tstmp", stop.toString());
        d.put("host", host);
      }
      rs.close();
      
      ArrayList procEvents = new ArrayList();
      // find all processing events
      rs = s.executeQuery("select sw_accession, processing_id, algorithm, status from processing where ancestor_workflow_run_id = "+workflowRunId+" or workflow_run_id = "+workflowRunId);
      while(rs.next()) {
        
        HashMap<String, Object> proc = new HashMap<String, Object>();

        int accession = rs.getInt(1);
        int procId = rs.getInt(2);
        String algo = rs.getString(3);
        String status = rs.getString(4);
        
        proc.put("accession", accession);
        proc.put("algo", algo);
        proc.put("status", status);
        
        HashMap<Integer, String> files = new HashMap<Integer, String>();
        Statement s2 = c.createStatement();
        ResultSet rs2 = s2.executeQuery("select sw_accession, file_path from file where file_id in (select file_id from processing_files where processing_id = "+procId+")");
        while(rs2.next()) {
          int fileAccession = rs2.getInt(1);
          String path = rs2.getString(2);
          files.put(fileAccession, path);
        }
        rs2.close();
        s2.close();
        proc.put("files", files);
        
        procEvents.add(proc);
      }
      d.put("procs", procEvents);
      rs.close();
      s.close();
      c.close();
      
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
          "that probably means our SQL is invalid:\n"+se.getMessage()+"\n");
          se.printStackTrace();
          return(null);
     }
    
    return(d);
  }

  
  private ArrayList<Map<String, String>> moreGenericGetMetadata(String query) {

    ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
    
    String rootURL = EnvUtil.getProperty("rooturl");

    Connection c = setupConnection();

    Statement s = null;
    try {
      s = c.createStatement();
    } catch (SQLException se) {
      System.out.println("We got an exception while creating a statement:" +
      "that probably means we're no longer connected.");
      se.printStackTrace();
      return(null);
    }

    ResultSet rs = null;
    try {
      rs = s.executeQuery(query);
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
      "that probably means our SQL is invalid:\n"+query+"\n");
      se.printStackTrace();
      return(null);
    }

    try {
      
      ResultSetMetaData rsMetaData = rs.getMetaData();

      int numberOfColumns = rsMetaData.getColumnCount();
      Map<Integer, String> columnNames = new HashMap<Integer, String>();
      for (int i = 1; i <= numberOfColumns; i++) {
        // get the column's name.
        System.out.println(rsMetaData.getColumnName(i));
        columnNames.put(i, rsMetaData.getColumnName(i));
      }
      while (rs.next()) {
        Map<String, String> result = new HashMap<String, String>();
        for (int i=1; i<=numberOfColumns; i++) {
          String key = columnNames.get(i);
          String value = rs.getString(i);
          if (key == null) { key = ""; }
          if (value == null) { value = ""; }
          result.put(key, value);
        }
        data.add(result);
      }
    } catch (SQLException se) {
      System.out.println("We got an exception while getting a result:this " +
      "shouldn't happen: we've done something really bad.");
      se.printStackTrace();
      return(null);
    }

    // cleanup connection
    try {
      rs.close();
      s.close();
      c.close();
    } catch (SQLException se) {
      System.out.println("We got an exception while trying to close connections ");
      se.printStackTrace();
      return(null);
    }

    return(data);
  }
  
  private ArrayList genericGetMetadata(String query) {

    ArrayList data = new ArrayList();
    
    String rootURL = EnvUtil.getProperty("rooturl");

    Connection c = setupConnection();

    Statement s = null;
    try {
      s = c.createStatement();
    } catch (SQLException se) {
      System.out.println("We got an exception while creating a statement:" +
      "that probably means we're no longer connected.");
      se.printStackTrace();
      return(null);
    }

    ResultSet rs = null;
    try {
      rs = s.executeQuery(query);
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
      "that probably means our SQL is invalid:\n"+query+"\n");
      se.printStackTrace();
      return(null);
    }

    try {
      while (rs.next()) {
        String filePathStr = rs.getString(1);
        File filePath = new File(filePathStr);
        // if it doesn't exist don't show it!
        if (filePath.exists() || "NA".equals(filePathStr)) {
          Map item = new HashMap();
          item.put("filePath", filePathStr);
          item.put("desc", rs.getString(2));
          long swid = rs.getLong(3);
          item.put("uri", rootURL);
          item.put("swid", rs.getString(3));
          String params = rs.getString(4);
          item.put("params", params);
          if (params != null && !"NA".equals(params) && params.contains("=")) {
            String[] paramsArray = params.split(",");
            for (String param : paramsArray) {
              String[] kv = param.split("=");
              item.put(kv[0], kv[1]);
            }
          }
          Timestamp createTstmp = rs.getTimestamp(5);
          item.put("metatype", rs.getString(6));
          item.put("createTstmp", createTstmp.toString());
          
          data.add(item);
        }
      }
    } catch (SQLException se) {
      System.out.println("We got an exception while getting a result:this " +
      "shouldn't happen: we've done something really bad.");
      se.printStackTrace();
      return(null);
    }

    // cleanup connection
    try {
      rs.close();
      s.close();
      c.close();
    } catch (SQLException se) {
      System.out.println("We got an exception while trying to close connections ");
      se.printStackTrace();
      return(null);
    }

    return(data);
  }

  /**
   * <p>getMetadata.</p>
   *
   * @param swid a long.
   * @return a {@link java.util.Map} object.
   */
  public Map getMetadata(long swid) {
    return getMetadata(swid, "SELECT f.file_path, p.description, p.sw_accession, p.parameters, p.create_tstmp, f.meta_type FROM processing as p, file as f, processing_files as pf " +
        "WHERE f.file_id = pf.file_id AND p.processing_id = pf.processing_id AND p.sw_accession = " +swid+" AND "+
        "(f.meta_type = 'application/seqware-qe-vdb' OR f.meta_type = 'application/seqware-qe-db' OR f.meta_type = 'application/seqware-qe-hbase-db' OR f.meta_type = 'application/seqware-qe-postgresql-db'" +
        " OR f.meta_type = 'application/seqware-qe-berkeleydb-db'" +
        ") AND (p.status = 'processed' OR p.status = 'success') ORDER BY p.sw_accession");
  }
  
  /**
   * <p>getTagMetadata.</p>
   *
   * @param swid a long.
   * @return a {@link java.util.Map} object.
   */
  public Map getTagMetadata(long swid) {
    return getMetadata(swid, "SELECT f.file_path, p.description, p.sw_accession, p.parameters, p.create_tstmp, f.meta_type FROM processing as p, file as f, processing_files as pf " +
        "WHERE f.file_id = pf.file_id AND p.processing_id = pf.processing_id AND p.sw_accession = " +swid+" AND (f.meta_type = 'application/seqware-qe-tags' OR f.meta_type = 'application/seqware-qe-postgresql-db'" +
        " OR f.meta_type = 'application/seqware-qe-hbase-db') "+
        "AND (p.status = 'processed' OR p.status = 'success') ORDER BY p.sw_accession");
  }
  
  
  private Map getMetadata(long swid, String queryStr) {

    // the data to return
    Map data = new HashMap();
    
    String rootURL = EnvUtil.getProperty("rooturl");

    // load data from the database
    Connection c = setupConnection();

    Statement s = null;
    try {
      s = c.createStatement();
    } catch (SQLException se) {
      System.out.println("We got an exception while creating a statement:" +
      "that probably means we're no longer connected.");
      se.printStackTrace();
      return(null);
    }

    ResultSet rs = null;
    String query = queryStr;
    
    try {
      rs = s.executeQuery(query);
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
      "that probably means our SQL is invalid: "+query);
      se.printStackTrace();
      return(null);
    }

    // read the file path and 
    try {
      if (rs.next()) {
        data.put("filePath", rs.getString(1));
        data.put("desc", rs.getString(2));
        data.put("swid", rs.getLong(3));
        data.put("uri", rootURL);
        String params = rs.getString(4);
        if (params != null && !"NA".equals(params) && params.contains("=")) {
          String[] paramsArray = params.split(",");
          for (String param : paramsArray) {
            String[] kv = param.split("=");
            data.put(kv[0], kv[1]);
          }
        }
        data.put("params", params);
        data.put("createTstmp", rs.getString(5));
        data.put("metatype", rs.getString(6));
      }
    }  catch (SQLException se) {
      System.out.println("We got an exception while getting a result:this " +
      "shouldn't happen: we've done something really bad.");
      se.printStackTrace();
      return(null);
    }

    try {
      rs.close();
      s.close();
      c.close();
    } catch (SQLException se) {
      System.out.println("We got an exception while trying to close connections ");
      se.printStackTrace();
    }

    return(data);
  }
  
  /**
   * <p>addWorkflowRunParam.</p>
   *
   * @param workflowRunAccession a int.
   * @param key a {@link java.lang.String} object.
   * @param value a {@link java.lang.String} object.
   * @param type a {@link java.lang.String} object.
   */
  public void addWorkflowRunParam(int workflowRunAccession, String key, String value, String type) {
    
    Connection c = setupConnection();
    Statement s = null;
    ResultSet rs = null;
    int workflowRunId = 0;
    
    try {
      s = c.createStatement();
      rs = s.executeQuery("select workflow_run_id from workflow_run where sw_accession = "+workflowRunAccession);
      if (rs.next()) {
        workflowRunId = rs.getInt(1);
        
        System.err.println("The workflowrunid "+workflowRunId);
        
        s.execute("insert into workflow_run_param (workflow_run_id, key, value, type) values ("+workflowRunId+", '"+key+"', '"+value+"','"+type+"' )");
        
      }
      rs.close();
      s.close();
      rs.close();
      
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
      "that probably means our SQL is invalid: ");
      se.printStackTrace();
    }
   
  }
  
  /**
   * <p>scheduleWorkflowRun.</p>
   *
   * @param workflowAccession a int.
   * @param name a {@link java.lang.String} object.
   * @param cwd a {@link java.lang.String} object.
   * @param iniContents a {@link java.lang.String} object.
   * @param command a {@link java.lang.String} object.
   * @param workflowTemplate a {@link java.lang.String} object.
   * @param status a {@link java.lang.String} object.
   * @return a int.
   */
  public int scheduleWorkflowRun(int workflowAccession, String name, String cwd, 
          String iniContents, String command, String workflowTemplate, String status) {
    
    int workflowRunAccession = 0;
    int workflowId = 0;
    Connection c = setupConnection();
    Statement s = null;
    
    try {
      s = c.createStatement();
    } catch (SQLException se) {
      System.out.println("We got an exception while creating a statement:" +
      "that probably means we're no longer connected.");
      se.printStackTrace();
      return(0);
    }

    ResultSet rs = null;
    
    try {
      rs = s.executeQuery("select workflow_id from workflow where sw_accession = "+workflowAccession);
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
      "that probably means our SQL is invalid: ");
      se.printStackTrace();
      return(0);
    }
    
    try {
      if (rs.next()) {
        workflowId = rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return(0);
    }
    
    if (workflowId > 0) {
      try {
        // workflowAccession, name, cwd, iniContents.toString(), command, workflowTemplate
        s.execute("insert into workflow_run (workflow_id, create_tstmp, name, current_working_dir, ini_file, cmd, workflow_template, status) values" +
        		" ("+workflowId+", now(), '"+name+"', " +
        		"'"+cwd+"', '"+iniContents+"', '"+command+"', '"+workflowTemplate+"', '"+status+"')");
        rs = s.executeQuery("select currval('sw_accession_seq')");
        if (rs.next()) {
          workflowRunAccession = rs.getInt(1);
        }
      } catch (SQLException se) {
        System.out.println("Problem with SQL: "+se.getMessage());
        se.printStackTrace();
        return(0);
      }
    }
    
    
    try {
      rs.close();
      s.close();
      c.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    
    return workflowRunAccession;
    
  }
  
  /**
   * <p>updateWorkflowRun.</p>
   *
   * @param workflowRunAccession a int.
   * @param name a {@link java.lang.String} object.
   * @param cwd a {@link java.lang.String} object.
   * @param iniContents a {@link java.lang.String} object.
   * @param command a {@link java.lang.String} object.
   * @param workflowTemplate a {@link java.lang.String} object.
   * @param status a {@link java.lang.String} object.
   */
  public void updateWorkflowRun(int workflowRunAccession, String name, String cwd, String iniContents, String command, String workflowTemplate, String status) {
    Connection c = setupConnection();
    Statement s = null;
    
    try {
      s = c.createStatement();
    } catch (SQLException se) {
      System.out.println("We got an exception while creating a statement:" +
      "that probably means we're no longer connected.");
      se.printStackTrace();
    }
    
    try {
      s.execute(
          "update workflow_run " +
          " set name = '"+name+"', current_working_dir = " +
          "'"+cwd+"', ini_file = '"+iniContents+"', cmd = '"+command+"', workflow_template = '"+workflowTemplate+"', " +
          		"status = '"+status+"' where sw_accession = "+workflowRunAccession
      );
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
      "that probably means our SQL is invalid: ");
      se.printStackTrace();
    }
    
    try {
      s.close();
      c.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  /**
   * <p>getWorkflowAccession.</p>
   *
   * @param workflowId a int.
   * @return a int.
   */
  public int getWorkflowAccession(int workflowId) {

    // workflow accession
    int workflowAccession = 0;
    
    // load data from the database
    Connection c = setupConnection();

    Statement s = null;
    try {
      s = c.createStatement();
    } catch (SQLException se) {
      System.out.println("We got an exception while creating a statement:" +
      "that probably means we're no longer connected.");
      se.printStackTrace();
      return(0);
    }

    ResultSet rs = null;
    
    try {
      rs = s.executeQuery("select sw_accession from workflow where workflow_id = "+workflowId);
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
      "that probably means our SQL is invalid: ");
      se.printStackTrace();
      return(0);
    }
    
    try {
      if (rs.next()) {
        workflowAccession = rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return(0);
    }
    
    try {
      rs.close();
      s.close();
      c.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    return workflowAccession;
  }

}
