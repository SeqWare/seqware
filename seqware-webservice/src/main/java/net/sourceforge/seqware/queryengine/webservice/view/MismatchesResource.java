/**
 * 
 */
package net.sourceforge.seqware.queryengine.webservice.view;


import java.util.logging.Level;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import net.sourceforge.seqware.queryengine.webservice.util.EnvUtil;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;  
import org.restlet.resource.ServerResource;  

/**
 * <p>MismatchesResource class.</p>
 *
 * @author boconnor
 *
 * DEPRECATED: using the TemplateResource now instead
 * @version $Id: $Id
 */
public class MismatchesResource extends ServerResource {

  /**
   * <p>represent.</p>
   *
   * @return a {@link org.restlet.representation.Representation} object.
   */
  @Get
  public Representation represent() { 

    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException cnfe) {
      System.out.println("Couldn't find the driver!");
      System.out.println("Let's print a stack trace, and exit.");
      cnfe.printStackTrace();
      //System.exit(-1);
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
      //System.exit(1);
    }

    Statement s = null;
    try {
      s = c.createStatement();
    } catch (SQLException se) {
      System.out.println("We got an exception while creating a statement:" +
      "that probably means we're no longer connected.");
      se.printStackTrace();
      //System.exit(1);
    }
    ResultSet rs = null;
    try {
      
      rs = s.executeQuery("SELECT f.file_path, p.description, p.sw_accession, p.parameters, p.create_tstmp FROM processing as p, file as f, processing_files as pf " +
          "WHERE f.file_id = pf.file_id AND p.processing_id = pf.processing_id AND (f.meta_type = 'application/seqware-qe-vdb' OR f.meta_type = 'application/seqware-qe-db'" +
          "OR f.meta_type = 'application/seqware-qe-hbase-db' OR  f.meta_type = 'application/seqware-qe-postgresql-db') " +
          "AND (p.status = 'processed' OR p.status = 'success') ORDER BY p.sw_accession");
      getLogger().log(Level.SEVERE, "SELECT f.file_path, p.description, p.sw_accession, p.parameters, p.create_tstmp FROM processing as p, file as f, processing_files as pf " +
      "WHERE f.file_id = pf.file_id AND p.processing_id = pf.processing_id AND (f.meta_type = 'application/seqware-qe-vdb' OR f.meta_type = 'application/seqware-qe-db'" +
      "OR f.meta_type = 'application/seqware-qe-hbase-db' OR  f.meta_type = 'application/seqware-qe-postgresql-db') " +
      "AND (p.status = 'processed' OR p.status = 'success') ORDER BY p.sw_accession");
    } catch (SQLException se) {
      System.out.println("We got an exception while executing our query:" +
      "that probably means our SQL is invalid");
      se.printStackTrace();
      //System.exit(1);
    }

    int index = 0;
    
    StringBuffer output = new StringBuffer();
    
    // TODO: this should be a template

    output.append("<?xml version=\"1.0\"?>" +
        "<?xml-stylesheet href=\"/queryengine/static/xslt/queryengine.xsl\" type=\"text/xsl\" ?>" +
        "<queryengine>" +
        "<realtime>" +
    		"<variants>\n");
    try {
      while (rs.next()) {
        //output.append("Here's the result of row " + index++ + ":");
        String filePath = rs.getString(1);
        String desc = rs.getString(2);
        long swid = rs.getLong(3);
        String params = rs.getString(4);
        Timestamp createTstmp = rs.getTimestamp(5);
        //output.append("filePath: "+filePath+" swid: "+swid);
        output.append("  <mismatch>\n" +
        		"    <swid>"+swid+"</swid>\n" +
        	  "    <filepath>"+filePath+"</filepath>\n" +
        	  "    <description>"+desc+"</description>\n" +
        	  "    <parameters>"+params+"</parameters>\n" +
        	  "  </mismatch>\n");
      }
    } catch (SQLException se) {
      System.out.println("We got an exception while getting a result:this " +
      "shouldn't happen: we've done something really bad.");
      getLogger().log(Level.SEVERE, "We got an exception while getting a result "+se.getMessage());
      se.printStackTrace();
      //System.exit(1);
    }
    
    try {
      rs.close();
      s.close();
      c.close();
    } catch (SQLException se) {
      System.out.println("We got an exception while trying to close connections ");
      se.printStackTrace();
      //System.exit(1);
    }
    
    output.append("</variants></realtime></queryengine>");
    StringRepresentation repOutput = new StringRepresentation(output.toString());
    repOutput.setMediaType(MediaType.TEXT_XML);
    return(repOutput);

  }
}
