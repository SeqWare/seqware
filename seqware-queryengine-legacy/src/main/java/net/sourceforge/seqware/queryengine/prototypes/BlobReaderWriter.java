package net.sourceforge.seqware.queryengine.prototypes;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sleepycat.db.DatabaseEntry;

import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.VariantTB;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

public class BlobReaderWriter {
  public static void main(String [] args)
  {
    try {
      VariantTB vtb = new VariantTB();
      Variant v = new Variant();
      v.setContig("chr22");
      v.setStartPosition(1234);
      v.setStopPosition(34243);
      v.addTag("test", "works?");
      v.addTag("isDbSNP", "rs2121212");
      v.setZygosity(v.HOMOZYGOUS);
      DatabaseEntry value = new DatabaseEntry();
      vtb.objectToEntry(v, value);
      byte[] data = value.getData();
      
      try {
        Class.forName("org.postgresql.Driver");
      } catch (ClassNotFoundException cnfe) {
        System.out.println("Couldn't find the driver!");
        System.out.println("Let's print a stack trace, and exit.");
        cnfe.printStackTrace();
        System.exit(-1);
      }
  
      System.out.println("Registered the driver ok, so let's make a connection.");
  
      Connection conn = null;
  
      try {
        // The second and third arguments are the username and password,
        // respectively. They should be whatever is necessary to connect
        // to the database.
        String server = System.getProperty("dbserver");
        String db = System.getProperty("db");
        String user = System.getProperty("user");
        String pass = System.getProperty("pass");
        conn = DriverManager.getConnection("jdbc:postgresql://"+server+"/"+db,
            user, pass);
  
      } catch (SQLException se) {
        System.out.println("Couldn't connect: print out a stack trace and exit.");
        se.printStackTrace();
        System.exit(1);
      }
      
      PreparedStatement ps = conn.prepareStatement("select contig, start, stop from feature where contig = 'chr1'");
      ResultSet rs = ps.executeQuery();
      if (rs != null) {
        while(rs.next()) {
          String contig = rs.getString(1);
          int start = rs.getInt(2);
          int stop = rs.getInt(3);
          System.out.println(contig+"\t"+start+"\t"+stop);
        }
      }
      rs.close();
      ps.close();
      conn.close();
      
      /*
      // write to database
      PreparedStatement ps = conn.prepareStatement("INSERT INTO feature (type, contig, start, stop, bdata) VALUES (?, ?, ?, ?, ?)");
      ps.setString(1, "variant");
      ps.setString(2, v.getContig());
      ps.setInt(3, v.getStartPosition());
      ps.setInt(4, v.getStopPosition());
      ps.setBytes(5, data);
      ps.executeUpdate();
      ps.close();
      
      ps = conn.prepareStatement("Select contig, start, stop, bdata from feature");
      ResultSet rs = ps.executeQuery();
      if (rs != null) {
        while(rs.next()) {
          byte[] data2 = rs.getBytes(4);
          Variant v2 = (Variant)vtb.entryToObject(new DatabaseEntry(data2));
          System.out.println("Variant "+v2.getContig()+" "+v2.getStartPosition()+" "+v2.getTagValue("test"));
        }
      }
      
      
      // Now try using the PostgreSQL store
      SeqWareSettings settings = new SeqWareSettings();
      settings.setStoreType("postgresql-mismatch-store");
      settings.setDatabase(System.getProperty("db"));
      settings.setUsername(System.getProperty("user"));
      settings.setPassword(System.getProperty("pass"));
      settings.setServer(System.getProperty("dbserver"));
      Store store = new PostgreSQLStore();
      store.setSettings(settings);
      store.setup(settings);
      
      // now list variants
      System.out.println("List variants with store");
      SeqWareIterator it = store.getMismatches();
      while(it.hasNext()) {
        v = (Variant)it.next();
        System.out.println("Store test 1: "+v.getContig()+" "+v.getStartPosition()+" "+v.getTagValue("test"));
      }
      
      // now insert variant
      System.out.println("Insert variant with store");
      v = new Variant();
      v.setContig("chr2");
      v.setStartPosition(1234);
      v.setStopPosition(34243);
      v.addTag("bystore", "true");
      v.addTag("isDbSNP", "rs2121212");
      v.setZygosity(v.HOMOZYGOUS);
      store.putMismatch(v);
      
      // now list again
      System.out.println("List again by store");
      it = store.getMismatches();
      while(it.hasNext()) {
        v = (Variant)it.next();
        System.out.println("Store test 1: "+v.getContig()+" "+v.getStartPosition()+" "+v.getTagValue("test"));
      }
      
      // now list by tag
      System.out.println("List all variants with bystore tag using the store obj");
      it = store.getMismatchesByTag("bystore");
      while(it.hasNext()) {
        v = (Variant)it.next();
        System.out.println("Store test 1: "+v.getContig()+" "+v.getStartPosition()+" "+v.getTagValue("bystore"));
      }
      
      store.close();
      */
      
  
    } catch (Exception e) {
      e.printStackTrace();
      System.out.print(e.getMessage());
    }
  }
}
