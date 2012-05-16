/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.store.impl;

import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.comparators.ContigPositionComparator;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.comparators.TagComparator;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.keycreators.ContigPositionKeyCreator;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.keycreators.ConsequenceVariantIdKeyCreator;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.keycreators.TagKeyOnlyCreator;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.ConsequenceTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.CoverageTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.ContigPositionTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.FeatureTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.StringIdTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.VariantTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.TagTB;
import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.model.LocatableModel;
import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.model.ContigPosition;
import net.sourceforge.seqware.queryengine.backend.model.StringId;
import net.sourceforge.seqware.queryengine.backend.model.Tag;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.CursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.PostgresModelIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.PostgresTagModelIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.PostgresVariantModelIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.SecondaryCursorIterator;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.db.BtreeStats;
import com.sleepycat.db.CheckpointConfig;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.CursorConfig;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.DeadlockException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.Database;
import com.sleepycat.db.LockDetectMode;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
import com.sleepycat.db.SecondaryConfig;
import com.sleepycat.db.SecondaryCursor;
import com.sleepycat.db.SecondaryDatabase;
import com.sleepycat.db.StatsConfig;
import com.sleepycat.db.Transaction;
import com.sleepycat.db.TransactionConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;


/**
 * @author boconnor
 * 
 * This is a simple first pass at a backend powered by a traditional relational DB. Note that only
 * generic features and variants are currently implemented.
 * 
 * ToDo:
 * * look into doing an update rather than delete followed by insert of a new record
 * 
 */
public class PostgreSQLStore extends Store {

  private static final int MAX_RETRY = 20;

  // tuble binder
  FeatureTB ftb = new FeatureTB();
  VariantTB mtb = new VariantTB();
  ConsequenceTB ctb = new ConsequenceTB();
  CoverageTB covtb = new CoverageTB();
  TagTB ttb = new TagTB();
  StringIdTB midtb = new StringIdTB();

  // the current unique IDs 
  Long currId = null;
  Long currFeatureId = null;
  Long currConsequenceId = null;
  Long currCoverageId = null;
  
  // database connection objects
  Connection conn = null;
  PreparedStatement ps1 = null;
  PreparedStatement ps1b = null;
  PreparedStatement ps2 = null;
  PreparedStatement ps3 = null;
  PreparedStatement ps4 = null;
  PreparedStatement ps5 = null;
  PreparedStatement ps6 = null;
  PreparedStatement ps7 = null;
  PreparedStatement psVariant = null;
  PreparedStatement psVariantUpdate = null;
  PreparedStatement psVariantSearch = null;
  PreparedStatement psVariantSearchByLocation = null;
  PreparedStatement psVariantSearchById = null;
  PreparedStatement psVariantSearchByTag = null;
  PreparedStatement psVariantId = null;
  PreparedStatement psVariantTag = null;
  
  // static variables
  static public int OID = 0;
  static public int BYTEA = 1;
  // if the particular object type doesn't support direclty writing to an object-specific table then 
  // it will use the OID approach instead
  static public int FIELDS = 2;
  
  // preferred method for persistence
  private int persistenceMethod = PostgreSQLStore.OID;
  
  LargeObjectManager lobj = null;

  
  // cache for tag IDs
  HashMap<String, Integer> tags = new HashMap<String, Integer>();
  
  // UTIL METHODS

  public void setup(SeqWareSettings settings) throws FileNotFoundException, DatabaseException, Exception {

    super.setup(settings);
    
    // see what the default persistence strategy is
    if (settings.getPostgresqlPersistenceStrategy() > -1) { persistenceMethod = settings.getPostgresqlPersistenceStrategy(); }
    
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException cnfe) {
      System.out.println("Couldn't find the driver!");
      System.out.println("Let's print a stack trace, and exit.");
      cnfe.printStackTrace();
      System.exit(-1);
    }
    
    System.out.println("Registered the driver ok, so let's make a connection.");
    
    try {
      // The second and third arguments are the username and password,
      // respectively. They should be whatever is necessary to connect
      // to the database.
      String server = settings.getServer();
      String db = settings.getDatabase();
      String user = settings.getUsername();
      String pass = settings.getPassword();
      conn = DriverManager.getConnection("jdbc:postgresql://"+server+"/"+db,
          user, pass);
      
      // need to do this for OID support
      if (persistenceMethod == PostgreSQLStore.OID) {
        conn.setAutoCommit(false);
        lobj = ((org.postgresql.PGConnection)conn).getLargeObjectAPI();
      }
      
      ps1 = conn.prepareStatement("INSERT INTO feature (contig, start, stop, type, bdata) VALUES (?, ?, ?, ?, ?)");
      ps1b = conn.prepareStatement("INSERT INTO feature (contig, start, stop, type, oiddata) VALUES (?, ?, ?, ?, ?)");
      ps2 = conn.prepareStatement("select tag_id from tag where key = ? and value = ?");
      ps3 = conn.prepareStatement("insert into tag (key, value) values (?, ?)");
      ps4 = conn.prepareStatement("select currval('tag_tag_id_seq')");
      ps5 = conn.prepareStatement("insert into feature_tag (feature_id, tag_id) values (currval('feature_feature_id_seq'), ?)");
      ps6 = conn.prepareStatement("delete from feature where feature_id = ?");
      ps7 = conn.prepareStatement("delete from feature_tag where feature_id = ?");
      
      // variant specific
      psVariant = conn.prepareStatement("insert into variant (type, contig, start, stop, " +
      "fuzzyStartPositionMax, fuzzyStopPositionMin, referenceBase, consensusBase, calledBase , " +
      "referenceCallQuality, consensusCallQuality, maximumMappingQuality, readCount, readBases, " +
      "baseQualities, calledBaseCount, calledBaseCountForward, calledBaseCountReverse, zygosity, " +
      "referenceMaxSeqQuality, referenceAveSeqQuality, consensusMaxSeqQuality, consensusAveSeqQuality, " +
      "callOne, callTwo, readsSupportingCallOne, readsSupportingCallTwo, readsSupportingCallThree, " +
      "svType, relativeLocation, translocationType, translocationDestinationContig, " +
      "translocationDestinationStartPosition, translocationDestinationStopPosition, keyvalues) " +
      "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

      psVariantUpdate = conn.prepareStatement("update variant set type = ?, contig = ?, start = ?, stop = ?, " +
          "fuzzyStartPositionMax = ?, fuzzyStopPositionMin = ?, referenceBase = ?, consensusBase = ?, calledBase = ? , " +
          "referenceCallQuality = ?, consensusCallQuality = ?, maximumMappingQuality = ?, readCount = ?, readBases = ?, " +
          "baseQualities = ?, calledBaseCount = ?, calledBaseCountForward = ?, calledBaseCountReverse = ?, zygosity = ?, " +
          "referenceMaxSeqQuality = ?, referenceAveSeqQuality = ?, consensusMaxSeqQuality = ?, consensusAveSeqQuality = ?, " +
          "callOne = ?, callTwo = ?, readsSupportingCallOne = ?, readsSupportingCallTwo = ?, readsSupportingCallThree = ?, " +
          "svType = ?, relativeLocation = ?, translocationType = ?, translocationDestinationContig = ?, " +
          "translocationDestinationStartPosition = ?, translocationDestinationStopPosition = ?, keyvalues = ? where variant_id = ?");
      
      psVariantSearchById = conn.prepareStatement("select * from variant where variant_id = ?");

      psVariantSearchByTag = conn.prepareStatement("select v.* from variant as v, variant_tag as vt, tag as t where t.key = ? and t.tag_id = vt.tag_id and vt.variant_id = v.variant_id");

      psVariantSearch = conn.prepareStatement("select * from variant");
      
      psVariantSearchByLocation = conn.prepareStatement("select * from variant where contig = ? and start >= ? and stop <= ?");

      psVariantId = conn.prepareStatement("select currval('variant_variant_id_seq')");

      psVariantTag = conn.prepareStatement("select t.key, t.value from tag as t, variant_tag as vt where vt.variant_id = ? and vt.tag_id = t.tag_id");

    } catch (SQLException se) {
      System.out.println("Couldn't connect: print out a stack trace and exit.");
      se.printStackTrace();
      System.exit(1);
    }
    
  }

  public void close() throws DatabaseException {
    try {
      ps1.close();
      ps1b.close();
      ps2.close();
      ps3.close();
      ps4.close();
      ps5.close();
      ps6.close();
      ps7.close();
      psVariant.close();
      psVariantUpdate.close();
      psVariantSearchById.close();
      psVariantSearchByTag.close();
      psVariantSearch.close();
      psVariantSearchByLocation.close();
      psVariantId.close();
      psVariantTag.close();
      conn.close();
    } catch (SQLException se) {
      System.out.println("Couldn't connect: print out a stack trace and exit.");
      se.printStackTrace();
      System.exit(1);
    }
  }

  
  // Generic Methods
  
  protected PostgresModelIterator getModelsUnordered(String table, String type, TupleBinding binding) {
    
    try {
      PreparedStatement ps = null;
      if (persistenceMethod == PostgreSQLStore.BYTEA) { ps = conn.prepareStatement("select bdata, "+table+"_id from "+table+" where type = '"+type+"'"); }
      else if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.FIELDS) { ps = conn.prepareStatement("select oiddata, "+table+"_id from "+table+" where type = '"+type+"'"); }
      
      ResultSet rs = ps.executeQuery();
      
      return(new PostgresModelIterator(rs, binding, persistenceMethod, lobj));
      
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return(null);
  }
   
  protected PostgresModelIterator getModels(String table, String type, TupleBinding binding) {
      return(getModelsUnordered(table, type, binding));
  }
  
  protected PostgresModelIterator getLocatableModels(String table, String type, TupleBinding binding, String contig, int start, int stop) {
    try {
      PreparedStatement ps = null;
      if (persistenceMethod == PostgreSQLStore.BYTEA) { ps = conn.prepareStatement("select bdata, "+table+"_id from "+table+" where type = '"+type+"' and contig = '"+contig+"' and start >= "+start+" and stop <= "+stop); }
      if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.FIELDS) { ps = conn.prepareStatement("select oiddata, "+table+"_id from "+table+" where type = '"+type+"' and contig = '"+contig+"' and start >= "+start+" and stop <= "+stop); }
      ResultSet rs = ps.executeQuery();
      
      return(new PostgresModelIterator(rs, binding, persistenceMethod, lobj));
      
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return(null);
  }

  protected PostgresModelIterator getModelsByTag(String table, String linkTable, String type, String tag, TupleBinding binding) {
    try {
      PreparedStatement ps = null;
      if (persistenceMethod == PostgreSQLStore.BYTEA) { ps = conn.prepareStatement("select bdata, "+table+"_id from "+table+", "+linkTable+", tag where type = '"+type+"' and "+table+"."+table+"_id = "+linkTable+"."+table+"_id and tag.tag_id = "+linkTable+".tag_id and tag.key = '"+tag+"'"); }
      if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.FIELDS) { ps = conn.prepareStatement("select "+table+".oiddata, "+table+"."+table+"_id from "+table+", "+linkTable+", tag where type = '"+type+"' and "+table+"."+table+"_id = "+linkTable+"."+table+"_id and tag.tag_id = "+linkTable+".tag_id and tag.key = '"+tag+"'"); }

      ResultSet rs = ps.executeQuery();
      
      return(new PostgresModelIterator(rs, binding, persistenceMethod, lobj));
      
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return(null);
  }
  
  protected PostgresTagModelIterator getModelsTags(String table, String linkTable) {
    // FIXME: need to implement, is this tuple binder for tags?
    try {
      PreparedStatement ps = conn.prepareStatement("select key, count(key) from tag, "+linkTable+" where "+linkTable+".tag_id = tag.tag_id group by key"); 
      ResultSet rs = ps.executeQuery();
      
      return(new PostgresTagModelIterator(rs, this));
      
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }    
    return(null);
    
  }
  
  protected PostgresTagModelIterator getModelsTags(String table, String linkTable, String tagSearchStr) {
    // FIXME: need to implement, is this tuple binder for tags?
    try {
      String lowerSearch = tagSearchStr.toLowerCase();
      PreparedStatement ps = conn.prepareStatement("select key, count(key) from tag, "+linkTable+" where "+linkTable+".tag_id = tag.tag_id and lower(key) like lower('"+lowerSearch+"%') group by key"); 
      ResultSet rs = ps.executeQuery();
      
      return(new PostgresTagModelIterator(rs, this));
      
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }    
    return(null);
    
  }
  
  protected Model getModel(String table, String type, String modelId, TupleBinding binding) throws Exception {

    Model result = null;
    try {
      PreparedStatement ps = null;
      if (persistenceMethod == PostgreSQLStore.BYTEA) { ps = conn.prepareStatement("select bdata, "+table+"_id from "+table+" where type = '"+type+"' and "+table+"_id = "+modelId); }
      if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.FIELDS) { ps = conn.prepareStatement("select oiddata, "+table+"_id from "+table+" where type = '"+type+"' and "+table+"_id = "+modelId); }
      
      ResultSet rs = ps.executeQuery();
      if (rs != null) {
        if (rs.next()) {
          byte[] data = null;
          if (persistenceMethod == PostgreSQLStore.BYTEA) {
            data = rs.getBytes(1);
          } else {
            int oid = rs.getInt(1);
            LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
            data = new byte[obj.size()];
            obj.read(data, 0, obj.size());
            obj.close();
          }
          result = (Model) binding.entryToObject(new DatabaseEntry(data));
        }
        rs.close();
      }
      ps.close();
      
      
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return(result);
  }
  
  // FIXME: tag put is broken here 
  protected synchronized Long putModel(String table, String linkTable, String type, TupleBinding binding, LocatableModel model) {
    
    /*
     * 1) insert into target table and get back ID
     * 2a) foreach tag, insert in tag table
     * 2b) get back tag ID
     * 2c) insert into linker table the feature and tag IDs
     */
    
    try {
     
      // FIXME: it would probably be more efficient to do an update than a delete followed by insert
      // delete the old entry if this is an update
      if (model.getId() != null && !"".equals(model.getId())) {
        ps7.setInt(1, Integer.parseInt(model.getId()));
        ps7.execute();
        ps6.setInt(1, Integer.parseInt(model.getId()));
        ps6.execute();
      }
      
      // insert model
      //PreparedStatement ps = conn.prepareStatement("INSERT INTO "+table+" (contig, start, stop, type, bdata) VALUES (?, ?, ?, ?, ?)");
      if (persistenceMethod == PostgreSQLStore.BYTEA) {
        ps1.setString(1, model.getContig());
        ps1.setInt(2, model.getStartPosition());
        ps1.setInt(3, model.getStopPosition());
        ps1.setString(4, type);
        DatabaseEntry objData = new DatabaseEntry();
        binding.objectToEntry(model, objData);
        ps1.setBytes(5, objData.getData());
        ps1.executeUpdate();
      } else if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.FIELDS) {
        ps1b.setString(1, model.getContig());
        ps1b.setInt(2, model.getStartPosition());
        ps1b.setInt(3, model.getStopPosition());
        ps1b.setString(4, type);
        DatabaseEntry objData = new DatabaseEntry();
        binding.objectToEntry(model, objData);
        int oid = lobj.create(LargeObjectManager.READ | LargeObjectManager.WRITE);
        LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);
        obj.write(objData.getData());
        obj.close();
        ps1b.setInt(5, oid);
        ps1b.executeUpdate();
      }
      
      ResultSet rs = null;
      // now get the ID back
      PreparedStatement ps = conn.prepareStatement("select currval('"+table+"_"+table+"_id_seq')");
      rs = ps.executeQuery();
      int modelId = -1;
      if (rs != null) {
        if(rs.next()) {
          modelId = rs.getInt(1);
        }
      }
      rs.close(); ps.close();
      
      // insert tags
      for (String key : model.getTags().keySet()) {
        
        int tagId = -1;
        if (tags.get(key) != null) {
          tagId = tags.get(key);
          //System.out.println("Found previous tag "+tagId+" key; "+key);
        } else {
          //ps = conn.prepareStatement("select tag_id from tag where key = ?");
          ps2.setString(1, key);
          rs = ps2.executeQuery();
          if (rs != null) {
            while(rs.next()) {
              tagId = rs.getInt(1);
            }
          }
          rs.close(); //ps.close();
        }
        if (tagId < 0) {
          //ps3 = conn.prepareStatement("insert into tag (key, value) values (?, ?)");
          ps3.setString(1, key);
          ps3.setString(2, model.getTags().get(key));
          ps3.executeUpdate();
          //ps4 = conn.prepareStatement("select currval('tag_tag_id_seq')");
          rs = ps4.executeQuery();
          if (rs != null) {
            if(rs.next()) {
              tagId = rs.getInt(1);
              //System.out.println("Saving tag "+tagId+" key; "+key);
              tags.put(key, tagId);
            }
          }
          rs.close(); //ps.close();
        }
        
        // now we know the tagId and modelId, time to insert in linker table
        //ps5 = conn.prepareStatement("insert into "+table+"_tag ("+table+"_id, tag_id) values (currval('"+table+"_"+table+"_id_seq'), ?)");
        ps5.setInt(1, tagId);
        ps5.executeUpdate();
        //ps.close();
      }


      // if we're here then commit
      conn.commit();

      // FIXME: does this work?
      // now return the modelId as -1 since we don't get it back from the DB
      return(new Long(modelId));
      
    } catch (SQLException e) {
      try { conn.rollback(); } catch (SQLException e2) { e2.printStackTrace(); }
      e.printStackTrace();
      System.err.println(e.getMessage());
    } catch (Exception e) {
      try { conn.rollback(); } catch (SQLException e2) { e2.printStackTrace(); }
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return(null);
    
  }
  
  public void addTags(Model model, String table) {
    
    try {
      Statement s = conn.createStatement();
      // clear out all the old tag associations if this model object already exists in the db
      // FIXME: this isn't cleaning up tags table if a tag is orphaned here!!
      if (model.getId() != null && !"".equals(model.getId())) { 
        s.execute("delete from "+table+"_tag where "+table+"_id = "+model.getId()); 
      }
      
      for (String key : model.getTags().keySet()) {
        
        // first, just populate the keyvalues field in the object
        
        
        int tagId = -1;
        if (tags.get(key+":"+model.getTags().get(key)) != null) {
          tagId = tags.get(key+":"+model.getTags().get(key));
          //System.out.println("Found previous tag "+tagId+" key; "+key);
        } else {
          //ps = conn.prepareStatement("select tag_id from tag where key = ?");
          ps2.setString(1, key);
          ps2.setString(2, model.getTags().get(key));
          ResultSet rs = ps2.executeQuery();
          if (rs != null) {
            if(rs.next()) {
              tagId = rs.getInt(1);
              if (tagId > 0) { tags.put(key+":"+model.getTags().get(key), tagId); }
            }
          }
          rs.close(); //ps.close();
        }
        if (tagId < 0) {
          ps3.setString(1, key);
          ps3.setString(2, model.getTags().get(key));
          ps3.executeUpdate();
          ResultSet rs = ps4.executeQuery();
          if (rs != null) {
            if(rs.next()) {
              tagId = rs.getInt(1);
              //System.out.println("Saving tag "+tagId+" key; "+key);
              tags.put(key+":"+model.getTags().get(key), tagId);
            }
          }
          rs.close(); //ps.close();
        }
        
        // now we know the tagId and modelId, time to insert in linker table
        if (model.getId() != null && !"".equals(model.getId())) { s.execute("insert into "+table+"_tag ("+table+"_id, tag_id) values ("+model.getId()+", "+tagId+")"); }
        else { s.execute("insert into "+table+"_tag ("+table+"_id, tag_id) values (currval('"+table+"_"+table+"_id_seq'), "+tagId+")"); }
      }
      s.close();

    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
  }
  
  // FIXME: make generic, tied to variant table for now
  public void readTags(Model model, String table) {
    
    try {
      // clear out all the old tag associations if this model object already exists in the db
      // FIXME: this isn't cleaning up tags table if a tag is orphaned here!!
      if (model.getId() != null && !"".equals(model.getId())) { 
        
        // FIXME: will need to rethink this to make this generic!
        psVariantTag.setInt(1, Integer.parseInt(model.getId()));
        ResultSet rs = psVariantTag.executeQuery();
        if (rs != null) {
          while(rs.next()) {
            model.getTags().put(rs.getString(1), rs.getString(2));
          }
          rs.close();
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
  }
  
  // FEATURE METHODS
  
  public PostgresModelIterator getFeaturesUnordered() {
    return(getModelsUnordered("feature", "feature", ftb));
  }
  
  public PostgresModelIterator getFeatures() {
    return(getModels("feature", "feature", ftb));
  }
  
  public PostgresModelIterator getFeatures(String contig, int start, int stop) {
    return(getLocatableModels("feature", "feature", ftb, contig, start, stop));
  }
  
  public Feature getFeature(String featureId) throws Exception {
    return((Feature)getModel("feature", "feature", featureId, ftb));
  }
  
  public PostgresModelIterator getFeaturesByTag(String tag) {
    return(getModelsByTag("feature", "feature_tag", "feature", tag, ftb));
  }
  
  public PostgresTagModelIterator getFeaturesTags() {
    return(getModelsTags("feature", "feature_tag"));
  }
  
  public SeqWareIterator getFeatureTagsBySearch(String tagSearchStr) {
    return(null);
  }
  
  public synchronized String putFeature(Feature feature, SeqWareIterator it, boolean transactional) {
    currFeatureId = putModel("feature", "feature_tag", "feature", ftb, feature);
    return(currFeatureId.toString());
  }
 
  public synchronized String putFeature(Feature feature) {
    currFeatureId = putModel("feature", "feature_tag", "feature", ftb, feature);
    return(currFeatureId.toString());
  }
  
  // VARIANT METHODS

 public SeqWareIterator getMismatchesUnordered() {
    if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.BYTEA) {
      return(getModelsUnordered("feature", "variant", mtb));
    } else if (persistenceMethod == PostgreSQLStore.FIELDS) {
      try {
        ResultSet rs = psVariantSearch.executeQuery();
        return(new PostgresVariantModelIterator(rs, this));
      } catch (SQLException e) {
        e.printStackTrace();
        System.err.println(e.getMessage());
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getMessage());
      }
    }
    return(null);
  }

  public SeqWareIterator getMismatches() {
    return(getMismatchesUnordered());
  }

  public SeqWareIterator getMismatches(String contig, int start, int stop) {
    if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.BYTEA) {
      return(getLocatableModels("feature", "variant", mtb, contig, start, stop));
    } else if (persistenceMethod == PostgreSQLStore.FIELDS) {
      try {
        psVariantSearchByLocation.setString(1, contig);
        psVariantSearchByLocation.setInt(2, start);
        psVariantSearchByLocation.setInt(3, stop);
        ResultSet rs = psVariantSearchByLocation.executeQuery();
        return(new PostgresVariantModelIterator(rs, this));
      } catch (SQLException e) {
        e.printStackTrace();
        System.err.println(e.getMessage());
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getMessage());
      }
    }
    return(null);
  }

  public SeqWareIterator getMismatches(String contig) {
    // basically setting this to the largest value possible
    return(getMismatches(contig, 1, Integer.MAX_VALUE));
  }

  public Variant getMismatch(String mismatchId) throws Exception {
    Variant v = null;
    if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.BYTEA) {
      return((Variant)getModel("feature", "variant", mismatchId, mtb));
    } else if (persistenceMethod == PostgreSQLStore.FIELDS) {
      psVariantSearchById.setInt(1, new Integer(mismatchId));
      ResultSet rs = psVariantSearchById.executeQuery();
      if (rs != null) {
        if(rs.next()) {
          v = new Variant();
          v.setId((new Integer(rs.getInt(1))).toString());
          v.setType(rs.getByte(2));
          v.setContig(rs.getString(3));
          v.setStartPosition(rs.getInt(4));
          v.setStopPosition(rs.getInt(5));
          v.setFuzzyStartPositionMax(rs.getInt(6));
          v.setFuzzyStopPositionMin(rs.getInt(7));
          v.setReferenceBase(rs.getString(8));
          v.setConsensusBase(rs.getString(9));
          v.setCalledBase(rs.getString(10));
          v.setReferenceCallQuality(rs.getFloat(11));
          v.setConsensusCallQuality(rs.getFloat(12));
          v.setMaximumMappingQuality(rs.getFloat(13));
          v.setReadCount(rs.getInt(14));
          v.setReadBases(rs.getString(15));
          v.setBaseQualities(rs.getString(16));
          v.setCalledBaseCount(rs.getInt(17));
          v.setCalledBaseCountForward(rs.getInt(18));
          v.setCalledBaseCountReverse(rs.getInt(19));
          v.setZygosity(rs.getByte(20));
          v.setReferenceMaxSeqQuality(rs.getFloat(21));
          v.setReferenceAveSeqQuality(rs.getFloat(22));
          v.setConsensusMaxSeqQuality(rs.getFloat(23));
          v.setConsensusAveSeqQuality(rs.getFloat(24));
          v.setCallOne(rs.getString(25));
          v.setCallTwo(rs.getString(26));
          v.setReadsSupportingCallOne(rs.getInt(27));
          v.setReadsSupportingCallTwo(rs.getInt(28));
          v.setReadsSupportingCallThree(rs.getInt(29));
          v.setSvType(rs.getByte(30));
          v.setRelativeLocation(rs.getByte(31));
          v.setTranslocationType(rs.getByte(32));
          v.setTranslocationDestinationContig(rs.getString(33));
          v.setTranslocationDestinationStartPosition(rs.getInt(34));
          v.setTranslocationDestinationStopPosition(rs.getInt(35));
          v.setKeyvalues(rs.getString(36));
          
          // populate the tags
          // disable join on second table and use local cache instead for performance reasons
          //readTags(v, "variant");
          String[] keyvalues = v.getKeyvalues().split(":");
          for (String keyval : keyvalues) {
            String[] keyvalue = keyval.split("=");
            if (keyvalue.length == 2) {
              v.addTag(keyvalue[0], keyvalue[1]);
            } else {
              v.addTag(keyvalue[0], null);              
            }
          }
        }
      }
    }
    return(v);
  }

  public SeqWareIterator getMismatchesByTag(String tag) {
    
    if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.BYTEA) {
      return(getModelsByTag("feature", "feature_tag", "variant", tag, mtb));
    } else if (persistenceMethod == PostgreSQLStore.FIELDS) {
      try {
        System.err.println("Search on tag: "+tag);
        psVariantSearchByTag.setString(1, tag);
        ResultSet rs = psVariantSearchByTag.executeQuery();
        return(new PostgresVariantModelIterator(rs, this));
      } catch (SQLException e) {
        e.printStackTrace();
        System.err.println(e.getMessage());
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getMessage());
      }
    }
    return(null);
  }

  /**
   * Get access to an iterator containing all the mismatch tags.
   * @return
   */
  public PostgresTagModelIterator getMismatchesTags() {
    return(getModelsTags("variant", "variant_tag"));
  }
  
  /**
   * Get access to an iterator containing all the mismatch tags.
   * @return
   */
  public PostgresTagModelIterator getMismatchTagsBySearch(String tagSearchStr) {
    return(getModelsTags("variant", "variant_tag", tagSearchStr));
  }

  public synchronized String putMismatch(Variant variant) {
    
    if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.BYTEA) {
      currId = putModel("feature", "feature_tag", "variant", mtb, variant);
      return(currId.toString());
    } else if (persistenceMethod == PostgreSQLStore.FIELDS) {
      // if this is true then we save directly to the variant table 
      
      /* variant_id integer not null, type int, contig text not null, start integer not null, stop integer not null, 
      fuzzyStartPositionMax integer, fuzzyStopPositionMin integer, referenceBase text, consensusBase text, calledBase text,
      referenceCallQuality float, consensusCallQuality float, maximumMappingQuality float, readCount integer, readBases text,
      baseQualities text,
       calledBaseCount integer, calledBaseCountForward integer, calledBaseCountReverse integer, zygosity integer,
      referenceMaxSeqQuality float, referenceAveSeqQuality float, consensusMaxSeqQuality float, consensusAveSeqQuality float,
      callOne text, callTwo text, readsSupportingCallOne integer, readsSupportingCallTwo integer, readsSupportingCallThree integer,
      svType int, relativeLocation int, translocationType integer, translocationDestinationContig text, 
      translocationDestinationStartPosition integer, translocationDestinationStopPosition integer */
      
      try {
        
        // save keyvalues as text field so it's faster to retrieve
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        for (String key : variant.getTags().keySet()) {
          if (isFirst) { isFirst = false; }
          else { sb.append(":"); } 
          String value = variant.getTagValue(key);
          if (value != null && !"".equals(value)) {
            sb.append(key+"="+value);
          } else {
            sb.append(key);
          }
        }
        variant.setKeyvalues(sb.toString());
      
        if (variant.getId() != null && !"".equals(variant.getId())) {
          // then it's an update
          psVariantUpdate.setInt(1, variant.getType());
          psVariantUpdate.setString(2, variant.getContig());
          psVariantUpdate.setInt(3, variant.getStartPosition());
          psVariantUpdate.setInt(4, variant.getStopPosition());
          psVariantUpdate.setInt(5, variant.getFuzzyStartPositionMax());
          psVariantUpdate.setInt(6, variant.getFuzzyStopPositionMin());
          psVariantUpdate.setString(7, variant.getReferenceBase());
          psVariantUpdate.setString(8, variant.getConsensusBase());
          psVariantUpdate.setString(9, variant.getCalledBase());
          psVariantUpdate.setFloat(10, variant.getReferenceCallQuality());
          psVariantUpdate.setFloat(11, variant.getConsensusCallQuality());
          psVariantUpdate.setFloat(12, variant.getMaximumMappingQuality());
          psVariantUpdate.setInt(13, variant.getReadCount());
          psVariantUpdate.setString(14, variant.getReadBases());
          psVariantUpdate.setString(15, variant.getBaseQualities());
          psVariantUpdate.setInt(16, variant.getCalledBaseCount());
          psVariantUpdate.setInt(17, variant.getCalledBaseCountForward());
          psVariantUpdate.setInt(18, variant.getCalledBaseCountReverse());
          psVariantUpdate.setInt(19, variant.getZygosity());
          psVariantUpdate.setFloat(20, variant.getReferenceMaxSeqQuality());
          psVariantUpdate.setFloat(21, variant.getReferenceAveSeqQuality());
          psVariantUpdate.setFloat(22, variant.getConsensusMaxSeqQuality());
          psVariantUpdate.setFloat(23, variant.getConsensusAveSeqQuality());
          psVariantUpdate.setString(24, variant.getCallOne());
          psVariantUpdate.setString(25, variant.getCallTwo());
          psVariantUpdate.setInt(26, variant.getReadsSupportingCallOne());
          psVariantUpdate.setInt(27, variant.getReadsSupportingCallTwo());
          psVariantUpdate.setInt(28, variant.getReadsSupportingCallThree());
          psVariantUpdate.setInt(29, variant.getSvType());
          psVariantUpdate.setInt(30, variant.getRelativeLocation());
          psVariantUpdate.setInt(31, variant.getTranslocationType());
          psVariantUpdate.setString(32, variant.getTranslocationDestinationContig());
          psVariantUpdate.setInt(33, variant.getTranslocationDestinationStartPosition());
          psVariantUpdate.setInt(34, variant.getTranslocationDestinationStopPosition());
          psVariantUpdate.setString(35, variant.getKeyvalues());
          psVariantUpdate.setInt(36, Integer.parseInt(variant.getId()));
          psVariantUpdate.executeUpdate();          
        } else {
          // otherwise create a new variant
          psVariant.setInt(1, variant.getType());
          psVariant.setString(2, variant.getContig());
          psVariant.setInt(3, variant.getStartPosition());
          psVariant.setInt(4, variant.getStopPosition());
          psVariant.setInt(5, variant.getFuzzyStartPositionMax());
          psVariant.setInt(6, variant.getFuzzyStopPositionMin());
          psVariant.setString(7, variant.getReferenceBase());
          psVariant.setString(8, variant.getConsensusBase());
          psVariant.setString(9, variant.getCalledBase());
          psVariant.setFloat(10, variant.getReferenceCallQuality());
          psVariant.setFloat(11, variant.getConsensusCallQuality());
          psVariant.setFloat(12, variant.getMaximumMappingQuality());
          psVariant.setInt(13, variant.getReadCount());
          psVariant.setString(14, variant.getReadBases());
          psVariant.setString(15, variant.getBaseQualities());
          psVariant.setInt(16, variant.getCalledBaseCount());
          psVariant.setInt(17, variant.getCalledBaseCountForward());
          psVariant.setInt(18, variant.getCalledBaseCountReverse());
          psVariant.setInt(19, variant.getZygosity());
          psVariant.setFloat(20, variant.getReferenceMaxSeqQuality());
          psVariant.setFloat(21, variant.getReferenceAveSeqQuality());
          psVariant.setFloat(22, variant.getConsensusMaxSeqQuality());
          psVariant.setFloat(23, variant.getConsensusAveSeqQuality());
          psVariant.setString(24, variant.getCallOne());
          psVariant.setString(25, variant.getCallTwo());
          psVariant.setInt(26, variant.getReadsSupportingCallOne());
          psVariant.setInt(27, variant.getReadsSupportingCallTwo());
          psVariant.setInt(28, variant.getReadsSupportingCallThree());
          psVariant.setInt(29, variant.getSvType());
          psVariant.setInt(30, variant.getRelativeLocation());
          psVariant.setInt(31, variant.getTranslocationType());
          psVariant.setString(32, variant.getTranslocationDestinationContig());
          psVariant.setInt(33, variant.getTranslocationDestinationStartPosition());
          psVariant.setInt(34, variant.getTranslocationDestinationStopPosition());
          psVariant.setString(35, variant.getKeyvalues());
          psVariant.executeUpdate();
          
          // now if settings.setReturnIds(true) then pull back the id that was just created
          if (this.getSettings().isReturnIds()) {
            ResultSet rs = null;
            // now get the ID back
            rs = psVariantId.executeQuery();
            if (rs != null) {
              if(rs.next()) {
                variant.setId(new Integer(rs.getInt(1)).toString());
              }
            }
            rs.close();
          }
        }
        
        // now deal with tags
        addTags(variant, "variant");
      
      } catch (SQLException e) {
        try { conn.rollback(); } catch (SQLException e2) { e2.printStackTrace(); }
        e.printStackTrace();
        System.err.println(e.getMessage());
      } catch (Exception e) {
        try { conn.rollback(); } catch (SQLException e2) { e2.printStackTrace(); }
        e.printStackTrace();
        System.err.println(e.getMessage());
      }
      
    }
    return(variant.getId());
  }

  public synchronized String putMismatch(Variant variant, SeqWareIterator it, boolean transactional) {
    return(putMismatch(variant));
  }

  // COVERAGE METHODS
  
  /**
   * Calling function will get back the coverage blocks that are contained within the range of interest
   * so make sure it pads +/- the bin size in the requested range otherwise may miss data!
   * @param contig
   * @param start
   * @param stop
   * @return
   */
  public LocatableSecondaryCursorIterator getCoverages(String contig, int start, int stop) {
    LocatableSecondaryCursorIterator cci = null;
    return(cci);
  }
  
  public LocatableSecondaryCursorIterator getCoverages(String contig) {
    return(getCoverages(contig, 1, Integer.MAX_VALUE)); // basically setting this to the largest value possible
  }

  public synchronized String putCoverage(Coverage coverage) {
    return(putCoverage(coverage, true));
  }

  // FIXME: this should be redone to use putModel
  public synchronized String putCoverage(Coverage coverage, boolean transactional) {
    return(null);
  }

  // CONSEQUENCE METHODS
  
  public synchronized String putConsequence(Consequence consequence) {
    return(putConsequence(consequence, true));
  }
  
  public synchronized String putConsequence(Consequence consequence, boolean transactional) {
    return(null);
  }

  public Consequence getConsequence(String consequenceId) throws Exception {
    return(null);
  }

  public SecondaryCursorIterator getConsequencesByTag(String tag) {
    SecondaryCursorIterator i = null;
    return(i);
  }

  public SeqWareIterator getConsequenceTagsBySearch(String tagSearchStr) {
    return(null);
  }  
  
  public SecondaryCursorIterator getConsequencesByMismatch(String mismatchId) {
    SecondaryCursorIterator i = null;
    return(i);
  }

  
}
