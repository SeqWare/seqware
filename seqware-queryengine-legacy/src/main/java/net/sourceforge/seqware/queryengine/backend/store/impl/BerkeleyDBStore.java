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


/**
 * @author boconnor
 * TODO: the putConsequence and putCoverage methods should detect deadlocks and retry
 * TODO: I may be able to reduce redundancy in the coverage and consequence methods below.
 */
public class BerkeleyDBStore extends Store {

  // environment
  private Environment myDbEnvironment = null;
  // generic features
  private Database featureDb = null;
  private SecondaryDatabase featureContigPositionsDb = null;
  private SecondaryDatabase featureAnnotationTagDb = null;
  // variants
  private Database mismatchDb = null;
  private SecondaryDatabase mismatchContigPositionsDb = null;
  private SecondaryDatabase mismatchAnnotationTagDb = null;
  // consequence reports
  // FIXME should have a consequenceAnnotationPositionsDb to quickly search for consequences by position 
  private Database consequenceAnnotationDb = null;
  private SecondaryDatabase consequenceAnnotationTagDb = null;
  private SecondaryDatabase consequenceAnnotationToMismatchDb = null;
  // coverage
  private Database coverageDb = null;
  private SecondaryDatabase coverageMetadataDb = null; // FIXME: never used, remove or populate this
  private SecondaryDatabase coverageContigPositionDb = null;

  // transaction
  Transaction txn = null;
  private static final int MAX_RETRY = 20;

  private String environmentPath = null;
  // use to store generic, locatable features for dumping to GFF, BED, etc
  private String featureDbName = "feature";
  private String featureContigPositionsDbName = "featureContigPositions";  
  private String featureAnnotationTagDbName = "featureAnnotationTag";
  // used to store primary SNV and/or indel information
  private String mismatchDbName = "mismatch";
  private String mismatchContigPositionsDbName = "mismatchContigPositions";
  // used for mismatch tag annotation
  private String mismatchAnnotationTagDbName = "mismatchAnnotationTag";
  // used for the mutation consequence analysis
  private String consequenceAnnotationDbName = "consequenceAnnotation";
  private String consequenceAnnotationTagDbName = "consequenceAnnotationTag";
  private String consequenceAnnotationToMismatchDbName = "consequenceAnnotationToMismatch";
  // used for storing coverage information
  private String coverageDbName = "coverage";
  private String coverageMetadataDbName = "coverageMetadata";
  private String coverageContigPositionDbName = "coverageContigPosition";


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

  public void setup(SeqWareSettings settings) throws FileNotFoundException, DatabaseException, Exception {

    super.setup(settings);
    String databaseHome = settings.getFilePath();
    long cacheSize = settings.getCacheSize();

    // create the environment
    environmentPath = databaseHome;
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setTransactional(true);
    envConfig.setCacheSize(cacheSize);
    // breaks up the cache into 4 separate chunks
    // FIXME: this should calculated based on total size / 4GB
    envConfig.setCacheCount(4);
    envConfig.setInitializeCache(true);
    envConfig.setInitializeLocking(true);
    envConfig.setMaxLockers(settings.getMaxLockers());
    envConfig.setMaxLocks(settings.getMaxLocks());
    envConfig.setMaxLockObjects(settings.getMaxLockObjects());
    envConfig.setInitializeLogging(true);
    envConfig.setAllowCreate(true);
    envConfig.setErrorStream(System.err);
    envConfig.setTxnMaxActive(40);
    // recovery does 1) uses logs to find missing data and 2) cleans out region files and unreleased locks
    // http://www.oracle.com/technology/documentation/berkeley-db/db/gsg_txn/JAVA/recovery.html
    envConfig.setRunRecovery(true);
    // this should allow automated recovery
    // http://www.oracle.com/technology/documentation/berkeley-db/db/gsg_txn/JAVA/architectrecovery.html
    envConfig.setRegister(true);
    // to deal with deadlocks
    // http://www.oracle.com/technology/documentation/berkeley-db/db/gsg_txn/JAVA/lockingsubsystem.html
    envConfig.setLockDetectMode(LockDetectMode.MINWRITE);
    // Configure a maximum transaction timeout of 60 seconds.
    // envConfig.setTxnTimeout(60000000);
    // Configure 40 maximum transactions.
    envConfig.setTxnWriteNoSync(true); // for performance reasons http://www.oracle.com/technology/documentation/berkeley-db/db/gsg_txn/JAVA/usingtxns.html
    envConfig.setThreaded(true); // make it threadsafe
    System.out.println("CREATING ENVIRONMENT");
    myDbEnvironment = new Environment(new File(environmentPath), envConfig);
    System.out.println("ENVIRONMENT CREATED");

    /* 
     * TODO:
     * * add transactional support
     * * add checkpoint Environment.checkpoint() http://www.oracle.com/technology/documentation/berkeley-db/db/gsg_txn/JAVA/filemanagement.html
     * * checklist: http://www.oracle.com/technology/documentation/berkeley-db/db/gsg_txn/JAVA/wrapup.html
     * 
     */
    
    // the environment is created, now create DBs

    // GENERIC FEATURES
    // the main mismatchDb
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setType(DatabaseType.BTREE);
    // the number of keys stored per page, want high "fill factor" so this is empirically determined
    dbConfig.setBtreeMinKey(6);
    // page size, this is the default, should determine page size on lustre and set to match, up to 64K
    //dbConfig.setPageSize(4096);
    System.out.println("OPENING FEATUREDB");
    // FIXME: should this get it's own setting?
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateMismatchDB());
    //dbConfig.setExclusiveCreate(settings.isCreateMismatchDB());
    dbConfig.setAllowCreate(settings.isCreateMismatchDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    dbConfig.setReadOnly(settings.isReadOnly());
    dbConfig.setTransactional(true);
    dbConfig.setReadUncommitted(true);
    // may want to make this a hashtable for performance reasons
    dbConfig.setSortedDuplicates(false);
    // min number of key/value pairs
    //dbConfig.setPageSize(512);
    featureDb = myDbEnvironment.openDatabase(txn, featureDbName+".db", featureDbName, dbConfig);
    System.out.println("FEATUREDB OPENED");

    // secondary db to support lookup based on position
    System.out.println("OPENING SECONDARY FEATURECONTIGPOSITOINSDB");
    SecondaryConfig contigPositionsDbConfig = new SecondaryConfig();
    contigPositionsDbConfig.setBtreeMinKey(4);
    contigPositionsDbConfig.setTransactional(true);
    contigPositionsDbConfig.setReadUncommitted(true);
    contigPositionsDbConfig.setAllowCreate(settings.isCreateMismatchDB());
    //contigPositionsDbConfig.setExclusiveCreate(settings.isCreateMismatchDB());
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateMismatchDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    contigPositionsDbConfig.setReadOnly(settings.isReadOnly());
    FeatureTB ftb = new FeatureTB();
    ContigPositionKeyCreator cpkc = new ContigPositionKeyCreator(ftb);
    ContigPositionComparator compare = new ContigPositionComparator();
    contigPositionsDbConfig.setType(DatabaseType.BTREE);
    // this will automatically compare the uid to sort them,
    contigPositionsDbConfig.setSortedDuplicates(true);
    contigPositionsDbConfig.setBtreeComparator(compare);
    contigPositionsDbConfig.setAllowPopulate(!settings.isReadOnly());
    contigPositionsDbConfig.setKeyCreator(cpkc);
    featureContigPositionsDb = myDbEnvironment.openSecondaryDatabase(txn, 
        featureContigPositionsDbName+".db", 
        featureContigPositionsDbName,
        featureDb, contigPositionsDbConfig);
    System.out.println("SECONDARY FEATURECONTIGPOSITOINSDB OPENED");

    // tags secondary db
    System.out.println("TRYING TO OPEN FEATURE TAG DB");
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateMismatchDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    SecondaryConfig tagDbConfig = new SecondaryConfig();
    tagDbConfig.setTransactional(true);
    tagDbConfig.setReadUncommitted(true);
    TagComparator tagCompare = new TagComparator();
    tagDbConfig.setAllowCreate(settings.isCreateMismatchDB());
    //tagDbConfig.setExclusiveCreate(settings.isCreateMismatchDB());
    tagDbConfig.setReadOnly(settings.isReadOnly());
    tagDbConfig.setType(DatabaseType.BTREE);
    tagDbConfig.setBtreeComparator(tagCompare);
    tagDbConfig.setSortedDuplicates(true);
    tagDbConfig.setAllowPopulate(!settings.isReadOnly());
    TagKeyOnlyCreator tkc = new TagKeyOnlyCreator(ftb);
    tagDbConfig.setBtreeMinKey(4);
    tagDbConfig.setMultiKeyCreator(tkc);
    featureAnnotationTagDb = myDbEnvironment.openSecondaryDatabase(txn, 
        featureAnnotationTagDbName+".db", 
        featureAnnotationTagDbName,
        featureDb, tagDbConfig);
    System.out.println("OPENED TAG DB");
    
    // VARIANTS
    // the main mismatchDb
    DatabaseConfig dbConfig3 = new DatabaseConfig();
    dbConfig3.setType(DatabaseType.BTREE);
    // the number of keys stored per page, want high "fill factor" so this is emperically determined
    dbConfig3.setBtreeMinKey(6);
    // page size, this is the default, should determine page size on lustre and set to match, up to 64K
    //dbConfig.setPageSize(4096);
    System.out.println("OPENING MISMATCHDB");
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateMismatchDB());
    //dbConfig.setExclusiveCreate(settings.isCreateMismatchDB());
    dbConfig3.setAllowCreate(settings.isCreateMismatchDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    dbConfig3.setReadOnly(settings.isReadOnly());
    dbConfig3.setTransactional(true);
    dbConfig3.setReadUncommitted(true);
    // may want to make this a hashtable for performance reasons
    dbConfig3.setSortedDuplicates(false);
    // min number of key/value pairs
    //dbConfig.setPageSize(512);
    mismatchDb = myDbEnvironment.openDatabase(txn, mismatchDbName+".db", mismatchDbName, dbConfig);
    System.out.println("MISMATCH DB OPENED");

    // secondary db to support lookup based on position
    System.out.println("OPENING SECONDARY MUTATION DB OPENED");
    SecondaryConfig contigPositionsDbConfig3 = new SecondaryConfig();
    contigPositionsDbConfig3.setBtreeMinKey(4);
    contigPositionsDbConfig3.setTransactional(true);
    contigPositionsDbConfig3.setReadUncommitted(true);
    contigPositionsDbConfig3.setAllowCreate(settings.isCreateMismatchDB());
    //contigPositionsDbConfig.setExclusiveCreate(settings.isCreateMismatchDB());
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateMismatchDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    contigPositionsDbConfig3.setReadOnly(settings.isReadOnly());
    VariantTB mtb = new VariantTB();
    ContigPositionKeyCreator cpkc3 = new ContigPositionKeyCreator(mtb);
    ContigPositionComparator compare3 = new ContigPositionComparator();
    contigPositionsDbConfig3.setType(DatabaseType.BTREE);
    // this will automatically compare the uid to sort them,
    contigPositionsDbConfig3.setSortedDuplicates(true);
    contigPositionsDbConfig3.setBtreeComparator(compare3);
    contigPositionsDbConfig3.setAllowPopulate(!settings.isReadOnly());
    contigPositionsDbConfig3.setKeyCreator(cpkc3);
    mismatchContigPositionsDb = myDbEnvironment.openSecondaryDatabase(txn, 
        mismatchContigPositionsDbName+".db", 
        mismatchContigPositionsDbName,
        mismatchDb, contigPositionsDbConfig3);
    System.out.println("SECONDARY MUTATION DB OPENED");

    // tags secondary db
    System.out.println("TRYING TO OPEN MUTATION TAG DB");
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateMismatchDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    SecondaryConfig tagDbConfig3 = new SecondaryConfig();
    tagDbConfig3.setTransactional(true);
    tagDbConfig3.setReadUncommitted(true);
    TagComparator tagCompare3 = new TagComparator();
    tagDbConfig3.setAllowCreate(settings.isCreateMismatchDB());
    //tagDbConfig.setExclusiveCreate(settings.isCreateMismatchDB());
    tagDbConfig3.setReadOnly(settings.isReadOnly());
    tagDbConfig3.setType(DatabaseType.BTREE);
    tagDbConfig3.setBtreeComparator(tagCompare3);
    tagDbConfig3.setSortedDuplicates(true);
    tagDbConfig3.setAllowPopulate(!settings.isReadOnly());
    TagKeyOnlyCreator tkc3 = new TagKeyOnlyCreator(mtb);
    tagDbConfig3.setBtreeMinKey(4);
    tagDbConfig3.setMultiKeyCreator(tkc3);
    mismatchAnnotationTagDb = myDbEnvironment.openSecondaryDatabase(txn, 
        mismatchAnnotationTagDbName+".db", 
        mismatchAnnotationTagDbName,
        mismatchDb, tagDbConfig3);
    System.out.println("OPENED TAG DB");

    // MUTATION CONSEQUENCE
    // primary database for mutation consequence info
    // FIXME: add lookup of consequences directly by contig, start and stop coords too
    System.out.println("TRYING TO OPEN CONSEQUENCE DB");
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateConsequenceAnnotationDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    DatabaseConfig dbConfig2 = new DatabaseConfig();
    dbConfig2.setTransactional(true);
    dbConfig2.setReadUncommitted(true);
    //dbConfig2.setExclusiveCreate(settings.isCreateConsequenceAnnotationDB());
    dbConfig2.setAllowCreate(settings.isCreateConsequenceAnnotationDB());
    dbConfig2.setReadOnly(settings.isReadOnly());
    ConsequenceTB ctb = new ConsequenceTB();
    dbConfig2.setType(DatabaseType.BTREE);
    dbConfig2.setDuplicateComparator(null);
    dbConfig2.setSortedDuplicates(false);
    dbConfig2.setBtreeMinKey(4); // based on ff of 53% with setting of 2
    consequenceAnnotationDb = myDbEnvironment.openDatabase(txn, consequenceAnnotationDbName+".db", consequenceAnnotationDbName, dbConfig2);
    System.out.println("OPENED CONSEQUENCE DB");

    // secondary db to support lookup based on tags
    tkc = new TagKeyOnlyCreator(ctb);
    tagDbConfig.setMultiKeyCreator(tkc);
    System.out.println("TRYING TO OPEN CONSEQUENCE TAG DB");
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateConsequenceAnnotationDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    tagDbConfig.setAllowCreate(settings.isCreateConsequenceAnnotationDB());
    //tagDbConfig.setExclusiveCreate(settings.isCreateConsequenceAnnotationDB());
    tagDbConfig.setReadOnly(settings.isReadOnly());
    tagDbConfig.setBtreeMinKey(8);
    consequenceAnnotationTagDb = myDbEnvironment.openSecondaryDatabase(txn, 
        consequenceAnnotationTagDbName+".db", 
        consequenceAnnotationTagDbName,
        consequenceAnnotationDb, tagDbConfig);
    System.out.println("OPENED CONSEQUENCE TAG DB");

    // secondary db to support lookup of consequence by mutationId
    SecondaryConfig toMismatchDbConfig = new SecondaryConfig();
    System.out.println("TRYING TO OPEN CONSEQUENCE MISMATCHID DB");
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateConsequenceAnnotationDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    toMismatchDbConfig.setTransactional(true);
    toMismatchDbConfig.setReadUncommitted(true);
    toMismatchDbConfig.setAllowCreate(settings.isCreateConsequenceAnnotationDB());
    //toMismatchDbConfig.setExclusiveCreate(settings.isCreateConsequenceAnnotationDB());
    toMismatchDbConfig.setReadOnly(settings.isReadOnly());
    ConsequenceVariantIdKeyCreator cmikc = new ConsequenceVariantIdKeyCreator(ctb);
    toMismatchDbConfig.setType(DatabaseType.BTREE);
    toMismatchDbConfig.setSortedDuplicates(true);
    toMismatchDbConfig.setAllowPopulate(!settings.isReadOnly());
    toMismatchDbConfig.setKeyCreator(cmikc);
    toMismatchDbConfig.setBtreeMinKey(4);
    consequenceAnnotationToMismatchDb = myDbEnvironment.openSecondaryDatabase(txn, 
        consequenceAnnotationToMismatchDbName+".db", 
        consequenceAnnotationToMismatchDbName,
        consequenceAnnotationDb, toMismatchDbConfig);
    System.out.println("OPENED CONSEQUENCE MISMATCHID DB");

    // COVERAGE DATABASE
    System.out.println("TRYING TO OPEN COVERAGE DB");
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateCoverageDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    dbConfig = new DatabaseConfig();
    dbConfig.setTransactional(true);
    dbConfig.setReadUncommitted(true);
    //dbConfig.setExclusiveCreate(settings.isCreateCoverageDB());
    dbConfig.setAllowCreate(settings.isCreateCoverageDB());
    dbConfig.setReadOnly(settings.isReadOnly());
    dbConfig.setType(DatabaseType.BTREE);
    dbConfig.setDuplicateComparator(null);
    dbConfig.setSortedDuplicates(false);
    dbConfig.setBtreeMinKey(4);
    coverageDb = myDbEnvironment.openDatabase(txn, coverageDbName+".db", coverageDbName, dbConfig);
    System.out.println("OPENED COVERAGE DB");

    // secondary db to support lookup based on position
    System.out.println("TRYING TO OPEN COVERAGE POSITION DB");
    System.out.println(" EXCLUSIVE/ALLOW CREATE: "+settings.isCreateCoverageDB());
    System.out.println(" READONLY: "+settings.isReadOnly());
    contigPositionsDbConfig.setAllowCreate(settings.isCreateCoverageDB());
    //contigPositionsDbConfig.setExclusiveCreate(settings.isCreateCoverageDB());
    contigPositionsDbConfig.setReadOnly(settings.isReadOnly());
    cpkc = new ContigPositionKeyCreator(covtb);
    contigPositionsDbConfig.setSortedDuplicates(true);
    contigPositionsDbConfig.setBtreeComparator(compare);
    contigPositionsDbConfig.setKeyCreator(cpkc);
    contigPositionsDbConfig.setBtreeMinKey(4);
    coverageContigPositionDb = myDbEnvironment.openSecondaryDatabase(txn, 
        coverageContigPositionDbName+".db", 
        coverageContigPositionDbName,
        coverageDb, contigPositionsDbConfig);
    System.out.println("OPENED COVERAGE POSITION DB");


    // TODO: need to add a metadata table so the app knows what the bin size is, API version number, tags used, etc.
  }

  public void startTransaction() throws DatabaseException {
    if (txn == null) {
      TransactionConfig tc = new TransactionConfig();
      tc.setReadUncommitted(true);
      tc.setWriteNoSync(true);
      txn = getEnvironment().beginTransaction(null, tc);
    }
  }

  public void finishTransaction() throws DatabaseException {
    if (txn != null) {
      txn.commit();
      txn = null;
    }
  }

  public boolean isActiveTransaction() {
    return(txn != null);
  }

  public void abortTransaction() throws DatabaseException {
    if (txn != null) { txn.abort(); txn = null; }
  }
  
  
  // Generic Methods
  
  protected CursorIterator getModelsUnordered(Database database, TupleBinding binding) {
    CursorIterator ci = null;
    try {
      CursorConfig curConfig = new CursorConfig();
      curConfig.setReadUncommitted(true);
      Cursor modelCursor = database.openCursor(txn, curConfig);
      ci = new CursorIterator(modelCursor, binding);
    } catch (Exception e) { e.printStackTrace(); return(null); }
    return(ci);
  }
  
  protected CursorIterator getModels(SecondaryDatabase database, TupleBinding binding) {
    CursorIterator ci = null;
    try {
      CursorConfig curConfig = new CursorConfig();
      curConfig.setReadUncommitted(true);
      Cursor modelCursor = database.openSecondaryCursor(txn, curConfig);
      ci = new CursorIterator(modelCursor, binding);
    } catch (Exception e) { e.printStackTrace(); return(null); }
    return(ci);
  }
  
  protected LocatableSecondaryCursorIterator getLocatableModels(SecondaryDatabase database, TupleBinding binding, String contig, int start, int stop) {
    LocatableSecondaryCursorIterator cci = null;
    try {
      ContigPosition mcp = new ContigPosition();
      ContigPositionTB mcptb = new ContigPositionTB();
      mcp.setContig(contig);
      mcp.setStartPosition(start);
      mcp.setStopPosition(stop);
      DatabaseEntry searchKey = new DatabaseEntry();
      mcptb.objectToEntry(mcp, searchKey);
      CursorConfig curConfig = new CursorConfig();
      curConfig.setReadUncommitted(true);
      SecondaryCursor searchCursor = database.openSecondaryCursor(txn, curConfig);
      cci = new LocatableSecondaryCursorIterator(searchCursor, searchKey, mcp, binding);
    } catch (Exception e) { e.printStackTrace(); }
    return(cci);
  }
  
  protected SecondaryCursorIterator getModelsByTag(SecondaryDatabase database, TupleBinding binding, String tag) {
    SecondaryCursorIterator i = null;
    try {
      DatabaseEntry searchKey = new DatabaseEntry();
      Tag tModel = new Tag();
      tModel.setTag(tag);
      ttb.objectToEntry(tModel, searchKey);
      CursorConfig curConfig = new CursorConfig();
      curConfig.setReadUncommitted(true);
      SecondaryCursor searchCursor = database.openSecondaryCursor(txn, curConfig);
      i = new SecondaryCursorIterator(searchCursor, searchKey, binding);
    } catch (Exception e) { e.printStackTrace(); }
    return(i);
  }
  
  /**
   * Get access to an iterator containing all the mismatch tags.
   * @return
   */
  protected SecondaryCursorIterator getModelsTags(SecondaryDatabase database, TupleBinding binding) {
    SecondaryCursorIterator i = null;
    try {
      CursorConfig curConfig = new CursorConfig();
      curConfig.setReadUncommitted(true);
      SecondaryCursor searchCursor = database.openSecondaryCursor(txn, curConfig);
      i = new SecondaryCursorIterator(searchCursor, binding, false);
    } catch (Exception e) { e.printStackTrace(); }
    return(i);
  }
  
  protected synchronized Long putModel(Database database, TupleBinding binding, Model model, Long currentId, SeqWareIterator it, boolean transactional) {
    
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry value = new DatabaseEntry();
    int retry_count = 0;
    boolean retry = true;
    while(retry) {

      try {

        if (transactional) { this.startTransaction(); }

        // find the curr ID
        if (model.getId() == null || "".equals(model.getId()) || "0".equals(model.getId())) {
          //System.out.println("HERE! modelID: "+model.getId()+" currentId: "+currentId);
          if (currentId == null) {
            BtreeStats stats = (BtreeStats)database.getStats(txn, new StatsConfig());
            int keyNum = stats.getNumKeys();
            currentId = new Long(keyNum);
          }
          currentId++;
          // record the ID within the object so it can be referenced by secondary index
          //System.out.println("After lookup, the currentId is: "+currentId);
          model.setId(currentId.toString());
          //System.out.println("model Set ID: "+model.getId());
        }
        else {
          //System.out.println("Model ID is not null: "+model.getId());
        }
        
        key = new DatabaseEntry(model.getId().getBytes("UTF-8"));
        binding.objectToEntry(model, value);

        if (it != null) {
          it.getCursor().putCurrent(value);
        } else {
          database.put(txn, key, value);
        }

        // now commit the transaction
        if (transactional) { this.finishTransaction(); }

        // if everything went OK then shouldn't retry
        retry = false;

      } catch (DeadlockException de) {
        System.out.println("################# " + model.getId() +
        " : caught deadlock");
        // retry if necessary
        if (retry_count < MAX_RETRY) {
          System.err.println(model.getId() +
          " : Retrying operation.");
          retry = true;
          retry_count++;
        } else {
          System.err.println(model.getId() +
          " : out of retries. Giving up.");
          retry = false;
        }
      } catch (DatabaseException e) {
        // abort and don't retry
        retry = false;
        System.err.println(model.getId() +
            " : caught exception: " + e.toString());
        System.err.println(model.getId() +
            " : errno: " + e.getErrno());
        e.printStackTrace();  
      } catch (Exception e) {
        retry = false;
        System.err.println("caught exception: "+e.getMessage());
        e.printStackTrace();
      } finally {
        if (transactional && isActiveTransaction()) {
          try {
            abortTransaction();
          } catch (Exception e) {
            System.err.println("Error aborting txn: " +
                e.toString());
            e.printStackTrace();
          }
        }
      }
    }
    //System.out.println("Returning ID: "+model.getId());
    return(Long.parseLong(model.getId()));
  }
  
  
  // FEATURE METHODS
  
  public CursorIterator getFeaturesUnordered() {
    return(getModelsUnordered(getFeatureDb(), ftb));
  }
  
  public CursorIterator getFeatures() {
    return(getModels(getFeatureContigPositionsDb(), ftb));
  }
  
  public LocatableSecondaryCursorIterator getFeatures(String contig, int start, int stop) {
    return(getLocatableModels(getFeatureContigPositionsDb(), ftb, contig, start, stop));
  }
  
  public Feature getFeature(String featureId) throws Exception {
    DatabaseEntry value = new DatabaseEntry();
    OperationStatus os = getFeatureDb().get(txn, new DatabaseEntry(featureId.getBytes("UTF-8")), value, LockMode.READ_UNCOMMITTED);
    if (os != os.SUCCESS) { throw new Exception("Can't get feature for ID: "+featureId); }
    return((Feature)ftb.entryToObject(value));
  }
  
  public SecondaryCursorIterator getFeaturesByTag(String tag) {
    return(getModelsByTag(getFeatureAnnotationTagDb(), ftb, tag));
  }
  
  public SecondaryCursorIterator getFeaturesTags() {
    return(getModelsTags(getFeatureAnnotationTagDb(), ttb));
  }
  
  public SeqWareIterator getFeatureTagsBySearch(String tagSearchStr) {
    return(null);
  }
  
  public synchronized String putFeature(Feature feature, SeqWareIterator it, boolean transactional) {
    currFeatureId = putModel(getFeatureDb(), ftb, feature, currFeatureId, it, transactional);
    return(currFeatureId.toString());
  }
 
  public synchronized String putFeature(Feature feature) {
    currFeatureId = putModel(getFeatureDb(), ftb, feature, currFeatureId, null, true);
    return(currFeatureId.toString());
  }
  
  // VARIANT METHODS

  public CursorIterator getMismatchesUnordered() {
    return(getModelsUnordered(getMismatchDb(), mtb));
  }

  public CursorIterator getMismatches() {
    return(getModels(getMismatchContigPositionsDb(), mtb));
  }

  public LocatableSecondaryCursorIterator getMismatches(String contig, int start, int stop) {
    return(getLocatableModels(getMismatchContigPositionsDb(), mtb, contig, start, stop));
  }

  public LocatableSecondaryCursorIterator getMismatches(String contig) {
    return(getMismatches(contig, 1, Integer.MAX_VALUE)); // basically setting this to the largest value possible
  }

  public Variant getMismatch(String mismatchId) throws Exception {
    DatabaseEntry value = new DatabaseEntry();
    OperationStatus os = getMismatchDb().get(txn, new DatabaseEntry(mismatchId.getBytes("UTF-8")), value, LockMode.READ_UNCOMMITTED);
    if (os != os.SUCCESS) { throw new Exception("Can't get mismatch for ID: "+mismatchId); }
    return((Variant)mtb.entryToObject(value));
  }

  public SecondaryCursorIterator getMismatchesByTag(String tag) {
    return(getModelsByTag(getMismatchAnnotationTagDb(), mtb, tag));
  }

  /**
   * Get access to an iterator containing all the mismatch tags.
   * @return
   */
  public SecondaryCursorIterator getMismatchesTags() {
    return(getModelsTags(getMismatchAnnotationTagDb(), ttb));
  }

  public SeqWareIterator getMismatchTagsBySearch(String tagSearchStr) {
    return(null);
  }
  
  public synchronized String putMismatch(Variant variant) {
    return(putMismatch(variant, null, true));
  }

  public synchronized String putMismatch(Variant variant, SeqWareIterator it, boolean transactional) {
    currId = putModel(getMismatchDb(), mtb, variant, currId, it, transactional);
    return(currId.toString());
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
    try {
      ContigPosition mcp = new ContigPosition();
      ContigPositionTB mcptb = new ContigPositionTB();
      mcp.setContig(contig);
      mcp.setStartPosition(start);
      mcp.setStopPosition(stop);
      DatabaseEntry searchKey = new DatabaseEntry();
      mcptb.objectToEntry(mcp, searchKey);
      CursorConfig curConfig = new CursorConfig();
      curConfig.setReadUncommitted(true);
      SecondaryCursor searchCursor = getCoverageContigPositionDb().openSecondaryCursor(txn, curConfig);
      cci = new LocatableSecondaryCursorIterator(searchCursor, searchKey, mcp, covtb);
    } catch (Exception e) { e.printStackTrace(); }
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
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry value = new DatabaseEntry();
    CoverageTB ctb = new CoverageTB(); 
    int retry_count = 0;
    boolean retry = true;
    while(retry) {

      try {

        // start transaction
        if (transactional) { this.startTransaction(); }

        if (coverage.getId() == null || "".equals(coverage.getId()) || "0".equals(coverage.getId())) {
          if (currCoverageId == null) {
            BtreeStats stats = (BtreeStats)getCoverageDb().getStats(txn, new StatsConfig());
            int keyNum = stats.getNumKeys();
            currCoverageId = new Long(keyNum);
          }
          currCoverageId++;
          // record the ID within the object so it can be referenced by secondary index
          coverage.setId(currCoverageId.toString());
        }
        key = new DatabaseEntry(coverage.getId().getBytes("UTF-8"));
        ctb.objectToEntry(coverage, value);
        this.getCoverageDb().put(txn, key, value);

        // now commit the transaction
        if (transactional) { this.finishTransaction(); }

        // if everything went OK then shouldn't retry
        retry = false;

      } catch (DeadlockException de) {
        de.printStackTrace();
        System.out.println("################# " + coverage.getId() +
        " : caught deadlock");
        // retry if necessary
        if (retry_count < MAX_RETRY) {
          System.err.println(coverage.getId() +
          " : Retrying operation.");
          retry = true;
          retry_count++;
        } else {
          System.err.println(coverage.getId() +
          " : out of retries. Giving up.");
          retry = false;
        }
      } catch (DatabaseException e) {
        e.printStackTrace();
        // abort and don't retry
        retry = false;
        System.err.println(coverage.getId() +
            " : caught exception: " + e.toString());
        System.err.println(coverage.getId() +
            " : errno: " + e.getErrno());
        e.printStackTrace();  
      } catch (Exception e) {
        e.printStackTrace();
        retry = false;
        System.err.println("caught exception: "+e.getMessage());
        e.printStackTrace();
      } finally {
        if (transactional && isActiveTransaction()) {
          try {
            abortTransaction();
          } catch (Exception e) {
            System.err.println("Error aborting txn: " +
                e.toString());
            e.printStackTrace();
          }
        }
      }
    }
    return(currCoverageId.toString());
  }

  // CONSEQUENCE METHODS
  
  public synchronized String putConsequence(Consequence consequence) {
    return(putConsequence(consequence, true));
  }
  
  //FIXME: this should be redone to use putModel
  public synchronized String putConsequence(Consequence consequence, boolean transactional) {
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry value = new DatabaseEntry();
    ConsequenceTB ctb = new ConsequenceTB(); 
    int retry_count = 0;
    boolean retry = true;
    while(retry) {

      try {

        // start a transaction
        if (transactional) { this.startTransaction(); }

        // find curr ID
        if (consequence.getId() == null || "".equals(consequence.getId()) || "0".equals(consequence.getId())) {
          if (currConsequenceId == null) {
            BtreeStats stats = (BtreeStats)getConsequenceAnnotationDb().getStats(txn, new StatsConfig());
            int keyNum = stats.getNumKeys();
            currConsequenceId = new Long(keyNum);
          }
          currConsequenceId++;
          // record the ID within the object so it can be referenced by secondary index
          consequence.setId(currConsequenceId.toString());
        }

        key = new DatabaseEntry(consequence.getId().getBytes("UTF-8"));
        ctb.objectToEntry(consequence, value);
        this.getConsequenceAnnotationDb().put(txn, key, value);

        // now commit the transaction
        if (transactional) { this.finishTransaction(); }

        // if everything went OK then shouldn't retry
        retry = false;

      } catch (DeadlockException de) {
        de.printStackTrace();
        System.out.println("################# " + consequence.getId() +
        " : caught deadlock");
        // retry if necessary
        if (retry_count < MAX_RETRY) {
          System.err.println(consequence.getId() +
          " : Retrying operation.");
          retry = true;
          retry_count++;
        } else {
          System.err.println(consequence.getId() +
          " : out of retries. Giving up.");
          retry = false;
        }
      } catch (DatabaseException e) {
        e.printStackTrace();
        // abort and don't retry
        retry = false;
        System.err.println(consequence.getId() +
            " : caught exception: " + e.toString());
        System.err.println(consequence.getId() +
            " : errno: " + e.getErrno());
        e.printStackTrace();  
      } catch (Exception e) {
        e.printStackTrace();
        retry = false;
        System.err.println("caught exception: "+e.getMessage());
        e.printStackTrace();
      } finally {
        if (transactional && isActiveTransaction()) {
          try {
            abortTransaction();
          } catch (Exception e) {
            System.err.println("Error aborting txn: " +
                e.toString());
            e.printStackTrace();
          }
        }
      }
    }
    return(currConsequenceId.toString());
  }

  public Consequence getConsequence(String consequenceId) throws Exception {
    DatabaseEntry value = new DatabaseEntry();
    OperationStatus os = this.getConsequenceAnnotationDb().get(txn, new DatabaseEntry(consequenceId.getBytes("UTF-8")), value, LockMode.READ_UNCOMMITTED);
    if (os != os.SUCCESS) { throw new Exception("Can't get consequence record for ID: "+consequenceId); }
    return((Consequence)ctb.entryToObject(value));
  }

  public SecondaryCursorIterator getConsequencesByTag(String tag) {
    SecondaryCursorIterator i = null;
    try {
      DatabaseEntry searchKey = new DatabaseEntry();
      Tag tModel = new Tag();
      tModel.setTag(tag);
      ttb.objectToEntry(tModel, searchKey);
      CursorConfig curConfig = new CursorConfig();
      curConfig.setReadUncommitted(true);
      SecondaryCursor searchCursor = getConsequenceAnnotationTagDb().openSecondaryCursor(txn, curConfig);
      i = new SecondaryCursorIterator(searchCursor, searchKey, ctb);
    } catch (Exception e) { e.printStackTrace(); }
    return(i);
  }

  public SeqWareIterator getConsequenceTagsBySearch(String tagSearchStr) {
    return(null);
  }  
  
  public SecondaryCursorIterator getConsequencesByMismatch(String mismatchId) {
    SecondaryCursorIterator i = null;
    try {
      DatabaseEntry searchKey = new DatabaseEntry();
      StringId mid = new StringId();
      mid.setId(mismatchId);
      midtb.objectToEntry(mid, searchKey);
      CursorConfig curConfig = new CursorConfig();
      curConfig.setReadUncommitted(true);
      SecondaryCursor searchCursor = getConsequenceAnnotationToMismatchDb().openSecondaryCursor(txn, curConfig);
      i = new SecondaryCursorIterator(searchCursor, searchKey, ctb);
    } catch (Exception e) { e.printStackTrace(); }
    return(i);
  }

  // UTIL METHODS
  
  public void checkpoint() throws DatabaseException {
    if (myDbEnvironment != null) { myDbEnvironment.checkpoint(new CheckpointConfig()); }
  }
  
  public void close() throws DatabaseException {
    // checkpoint before closing
    CheckpointConfig cpc = new CheckpointConfig();
    cpc.setKBytes(1024);
    myDbEnvironment.checkpoint(cpc);
    // close the dbs and environment
    if (featureAnnotationTagDb != null) { featureAnnotationTagDb.close(); }
    if (featureContigPositionsDb != null) { featureContigPositionsDb.close(); }
    if (featureDb != null) { featureDb.close(); }
    if (mismatchAnnotationTagDb != null) { mismatchAnnotationTagDb.close(); }
    if (mismatchContigPositionsDb != null) { mismatchContigPositionsDb.close(); }
    if (mismatchDb != null) { mismatchDb.close(); }
    if (consequenceAnnotationTagDb != null) { consequenceAnnotationTagDb.close(); }
    if (consequenceAnnotationToMismatchDb != null) { consequenceAnnotationToMismatchDb.close(); }
    if (consequenceAnnotationDb != null) { consequenceAnnotationDb.close(); }
    if (coverageDb != null) { coverageDb.close(); }
    if (coverageMetadataDb != null) { coverageMetadataDb.close(); }
    if (coverageContigPositionDb != null) { coverageContigPositionDb.close(); }
    if (myDbEnvironment != null) { myDbEnvironment.close(); }
  }

  // private methods
  private Database getFeatureDb() {
    return(featureDb);
  }

  private SecondaryDatabase getFeatureContigPositionsDb() {
    return(featureContigPositionsDb);
  }

  private SecondaryDatabase getFeatureAnnotationTagDb() {
    return(featureAnnotationTagDb);
  }
  
  private Database getMismatchDb() {
    return(mismatchDb);
  }

  private SecondaryDatabase getMismatchContigPositionsDb() {
    return(mismatchContigPositionsDb);
  }

  private SecondaryDatabase getMismatchAnnotationTagDb() {
    return(mismatchAnnotationTagDb);
  }

  private Database getConsequenceAnnotationDb() {
    return(consequenceAnnotationDb);
  }

  private SecondaryDatabase getConsequenceAnnotationTagDb() {
    return(consequenceAnnotationTagDb);
  }

  private SecondaryDatabase getConsequenceAnnotationToMismatchDb() {
    return(consequenceAnnotationToMismatchDb);
  }

  private Database getCoverageDb() {
    return(coverageDb);
  }

  private SecondaryDatabase getCoverageContigPositionDb() {
    return(coverageContigPositionDb);
  }

  private Environment getEnvironment() {
    return (myDbEnvironment);
  }

  private Long getCurrId() {
    return currId;
  }

  private void setCurrId(Long currId) {
    this.currId = currId;
  }

  private Long getCurrFeatureId() {
    return currFeatureId;
  }

  private void setCurrFeatureId(Long currFeatureId) {
    this.currFeatureId = currFeatureId;
  }

  private Long getCurrConsequenceId() {
    return currConsequenceId;
  }

  private void setCurrConsequenceId(Long currConsequenceId) {
    this.currConsequenceId = currConsequenceId;
  }

  private Long getCurrCoverageId() {
    return currCoverageId;
  }

  private void setCurrCoverageId(Long currCoverageId) {
    this.currCoverageId = currCoverageId;
  }
  
  

}
