/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.util;

import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;

/**
 * <p>SeqWareSettings class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
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
  /** Constant <code>OID=0</code> */
  static public int OID = 0;
  /** Constant <code>BYTEA=1</code> */
  static public int BYTEA = 1;
  // if the particular object type doesn't support direclty writing to an object-specific table then 
  // it will use the OID approach instead
  /** Constant <code>FIELDS=2</code> */
  static public int FIELDS = 2;
  private int postgresqlPersistenceStrategy = OID;
  private boolean returnIds = true;


  /**
   * <p>isReturnIds.</p>
   *
   * @return a boolean.
   */
  public boolean isReturnIds() {
    return returnIds;
  }
  /**
   * <p>Setter for the field <code>returnIds</code>.</p>
   *
   * @param returnIds a boolean.
   */
  public void setReturnIds(boolean returnIds) {
    this.returnIds = returnIds;
  }
  /**
   * <p>Getter for the field <code>postgresqlPersistenceStrategy</code>.</p>
   *
   * @return a int.
   */
  public int getPostgresqlPersistenceStrategy() {
    return postgresqlPersistenceStrategy;
  }
  /**
   * <p>Setter for the field <code>postgresqlPersistenceStrategy</code>.</p>
   *
   * @param postgresqlPersistenceStrategy a int.
   */
  public void setPostgresqlPersistenceStrategy(int postgresqlPersistenceStrategy) {
    this.postgresqlPersistenceStrategy = postgresqlPersistenceStrategy;
  }
  /**
   * <p>Getter for the field <code>maxLockers</code>.</p>
   *
   * @return a int.
   */
  public int getMaxLockers() {
    return maxLockers;
  }
  /**
   * <p>Setter for the field <code>maxLockers</code>.</p>
   *
   * @param maxLockers a int.
   */
  public void setMaxLockers(int maxLockers) {
    this.maxLockers = maxLockers;
  }
  /**
   * <p>Getter for the field <code>maxLocks</code>.</p>
   *
   * @return a int.
   */
  public int getMaxLocks() {
    return maxLocks;
  }
  /**
   * <p>Setter for the field <code>maxLocks</code>.</p>
   *
   * @param maxLocks a int.
   */
  public void setMaxLocks(int maxLocks) {
    this.maxLocks = maxLocks;
  }
  /**
   * <p>Getter for the field <code>maxLockObjects</code>.</p>
   *
   * @return a int.
   */
  public int getMaxLockObjects() {
    return maxLockObjects;
  }
  /**
   * <p>Setter for the field <code>maxLockObjects</code>.</p>
   *
   * @param maxLockObjects a int.
   */
  public void setMaxLockObjects(int maxLockObjects) {
    this.maxLockObjects = maxLockObjects;
  }
  /**
   * <p>isCreateCoverageDB.</p>
   *
   * @return a boolean.
   */
  public boolean isCreateCoverageDB() {
    return createCoverageDB;
  }
  /**
   * <p>Setter for the field <code>createCoverageDB</code>.</p>
   *
   * @param createCoverageDB a boolean.
   */
  public void setCreateCoverageDB(boolean createCoverageDB) {
    this.createCoverageDB = createCoverageDB;
  }
  /**
   * <p>isCreateMismatchDB.</p>
   *
   * @return a boolean.
   */
  public boolean isCreateMismatchDB() {
    return createMismatchDB;
  }
  /**
   * <p>Setter for the field <code>createMismatchDB</code>.</p>
   *
   * @param createMismatchDB a boolean.
   */
  public void setCreateMismatchDB(boolean createMismatchDB) {
    this.createMismatchDB = createMismatchDB;
  }
  /**
   * <p>isCreateConsequenceAnnotationDB.</p>
   *
   * @return a boolean.
   */
  public boolean isCreateConsequenceAnnotationDB() {
    return createConsequenceAnnotationDB;
  }
  /**
   * <p>Setter for the field <code>createConsequenceAnnotationDB</code>.</p>
   *
   * @param createConsequenceAnnotationDB a boolean.
   */
  public void setCreateConsequenceAnnotationDB(
      boolean createConsequenceAnnotationDB) {
    this.createConsequenceAnnotationDB = createConsequenceAnnotationDB;
  }
  /**
   * <p>isCreateDbSNPAnnotationDB.</p>
   *
   * @return a boolean.
   */
  public boolean isCreateDbSNPAnnotationDB() {
    return createDbSNPAnnotationDB;
  }
  /**
   * <p>Setter for the field <code>createDbSNPAnnotationDB</code>.</p>
   *
   * @param createDbSNPAnnotationDB a boolean.
   */
  public void setCreateDbSNPAnnotationDB(boolean createDbSNPAnnotationDB) {
    this.createDbSNPAnnotationDB = createDbSNPAnnotationDB;
  }
  /**
   * <p>Getter for the field <code>cacheSize</code>.</p>
   *
   * @return a long.
   */
  public long getCacheSize() {
    return cacheSize;
  }
  /**
   * <p>Setter for the field <code>cacheSize</code>.</p>
   *
   * @param cacheSize a long.
   */
  public void setCacheSize(long cacheSize) {
    this.cacheSize = cacheSize;
  }
  /**
   * <p>Getter for the field <code>username</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getUsername() {
    return username;
  }
  /**
   * <p>Setter for the field <code>username</code>.</p>
   *
   * @param username a {@link java.lang.String} object.
   */
  public void setUsername(String username) {
    this.username = username;
  }
  /**
   * <p>Getter for the field <code>password</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getPassword() {
    return password;
  }
  /**
   * <p>Setter for the field <code>password</code>.</p>
   *
   * @param password a {@link java.lang.String} object.
   */
  public void setPassword(String password) {
    this.password = password;
  }
  /**
   * <p>Getter for the field <code>connectionString</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getConnectionString() {
    return connectionString;
  }
  /**
   * <p>Setter for the field <code>connectionString</code>.</p>
   *
   * @param connectionString a {@link java.lang.String} object.
   */
  public void setConnectionString(String connectionString) {
    this.connectionString = connectionString;
  }
  /**
   * <p>Getter for the field <code>filePath</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getFilePath() {
    return filePath;
  }
  /**
   * <p>Setter for the field <code>filePath</code>.</p>
   *
   * @param filePath a {@link java.lang.String} object.
   */
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
  /**
   * <p>isReadOnly.</p>
   *
   * @return a boolean.
   */
  public boolean isReadOnly() {
    return readOnly;
  }
  /**
   * <p>Setter for the field <code>readOnly</code>.</p>
   *
   * @param readOnly a boolean.
   */
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
  /**
   * <p>Getter for the field <code>storeType</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getStoreType() {
    return storeType;
  }
  /**
   * <p>Setter for the field <code>storeType</code>.</p>
   *
   * @param storeType a {@link java.lang.String} object.
   */
  public void setStoreType(String storeType) {
    this.storeType = storeType;
  }
  /**
   * <p>Getter for the field <code>genomeId</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getGenomeId() {
    return genomeId;
  }
  /**
   * <p>Setter for the field <code>genomeId</code>.</p>
   *
   * @param genomeId a {@link java.lang.String} object.
   */
  public void setGenomeId(String genomeId) {
    this.genomeId = genomeId;
  }
  /**
   * <p>Getter for the field <code>referenceId</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getReferenceId() {
    return referenceId;
  }
  /**
   * <p>Setter for the field <code>referenceId</code>.</p>
   *
   * @param referenceId a {@link java.lang.String} object.
   */
  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }
  /**
   * <p>Getter for the field <code>server</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getServer() {
    return server;
  }
  /**
   * <p>Setter for the field <code>server</code>.</p>
   *
   * @param server a {@link java.lang.String} object.
   */
  public void setServer(String server) {
    this.server = server;
  }
  /**
   * <p>Getter for the field <code>database</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getDatabase() {
    return database;
  }
  /**
   * <p>Setter for the field <code>database</code>.</p>
   *
   * @param database a {@link java.lang.String} object.
   */
  public void setDatabase(String database) {
    this.database = database;
  }
  

}
