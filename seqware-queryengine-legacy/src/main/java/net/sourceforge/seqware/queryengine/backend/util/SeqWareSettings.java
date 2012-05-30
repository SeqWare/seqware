/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.util;

import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;

/**
 * @author boconnor
 *
 */
public class SeqWareSettings {

  // generic
  private boolean readOnly;
  private boolean createMismatchDB;
  private boolean createConsequenceAnnotationDB;
  private boolean createDbSNPAnnotationDB;
  private boolean createCoverageDB;
  private String storeType;
  private String username;
  private String password;
  private String connectionString;
  private String server;
  private String database;
  
  // HBase-specific
  private String genomeId;
  private String referenceId;

  // BerkeleyDB-specific
  private String filePath;
  private long cacheSize;
  private int maxLockers = 100000;
  private int maxLocks = 100000;
  private int maxLockObjects = 100000;
  
  // PostgreSQL-specific
  static public int OID = 0;
  static public int BYTEA = 1;
  // if the particular object type doesn't support direclty writing to an object-specific table then 
  // it will use the OID approach instead
  static public int FIELDS = 2;
  private int postgresqlPersistenceStrategy = OID;
  private boolean returnIds = true;


  public boolean isReturnIds() {
    return returnIds;
  }
  public void setReturnIds(boolean returnIds) {
    this.returnIds = returnIds;
  }
  public int getPostgresqlPersistenceStrategy() {
    return postgresqlPersistenceStrategy;
  }
  public void setPostgresqlPersistenceStrategy(int postgresqlPersistenceStrategy) {
    this.postgresqlPersistenceStrategy = postgresqlPersistenceStrategy;
  }
  public int getMaxLockers() {
    return maxLockers;
  }
  public void setMaxLockers(int maxLockers) {
    this.maxLockers = maxLockers;
  }
  public int getMaxLocks() {
    return maxLocks;
  }
  public void setMaxLocks(int maxLocks) {
    this.maxLocks = maxLocks;
  }
  public int getMaxLockObjects() {
    return maxLockObjects;
  }
  public void setMaxLockObjects(int maxLockObjects) {
    this.maxLockObjects = maxLockObjects;
  }
  public boolean isCreateCoverageDB() {
    return createCoverageDB;
  }
  public void setCreateCoverageDB(boolean createCoverageDB) {
    this.createCoverageDB = createCoverageDB;
  }
  public boolean isCreateMismatchDB() {
    return createMismatchDB;
  }
  public void setCreateMismatchDB(boolean createMismatchDB) {
    this.createMismatchDB = createMismatchDB;
  }
  public boolean isCreateConsequenceAnnotationDB() {
    return createConsequenceAnnotationDB;
  }
  public void setCreateConsequenceAnnotationDB(
      boolean createConsequenceAnnotationDB) {
    this.createConsequenceAnnotationDB = createConsequenceAnnotationDB;
  }
  public boolean isCreateDbSNPAnnotationDB() {
    return createDbSNPAnnotationDB;
  }
  public void setCreateDbSNPAnnotationDB(boolean createDbSNPAnnotationDB) {
    this.createDbSNPAnnotationDB = createDbSNPAnnotationDB;
  }
  public long getCacheSize() {
    return cacheSize;
  }
  public void setCacheSize(long cacheSize) {
    this.cacheSize = cacheSize;
  }
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getConnectionString() {
    return connectionString;
  }
  public void setConnectionString(String connectionString) {
    this.connectionString = connectionString;
  }
  public String getFilePath() {
    return filePath;
  }
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
  public boolean isReadOnly() {
    return readOnly;
  }
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
  public String getStoreType() {
    return storeType;
  }
  public void setStoreType(String storeType) {
    this.storeType = storeType;
  }
  public String getGenomeId() {
    return genomeId;
  }
  public void setGenomeId(String genomeId) {
    this.genomeId = genomeId;
  }
  public String getReferenceId() {
    return referenceId;
  }
  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }
  public String getServer() {
    return server;
  }
  public void setServer(String server) {
    this.server = server;
  }
  public String getDatabase() {
    return database;
  }
  public void setDatabase(String database) {
    this.database = database;
  }
  

}
