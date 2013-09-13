package net.sourceforge.seqware.metadb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import java.io.InputStreamReader;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * This class handles basic database creation.
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class TestDatabaseCreator {

    private final static String DEFAULT_DB_HOST = "127.0.0.1";
    private final static String POSTGRE_DB = "postgres";
    private final static String SEQWARE_DB = "test_seqware_meta_db";
    //We should not have a postgres user with an easily guessable password. It 
    //is a security risk. The seqware user needs CREATEDB for this to work but
    //it is more secure. Since we are using local database for testing it is 
    //not really security breach here.
    private final static String POSTGRE_USER = "seqware";
    private final static String POSTGRE_PASSWORD = "seqware";
    private final static String SEQWARE_USER = "seqware";
    private final static String SEQWARE_PASSWORD = "seqware";
    private static boolean database_changed;
    private static boolean first_time_created = true;
    private static Logger logger = Logger.getLogger(TestDatabaseCreator.class);

    /**
     * @return the DEFAULT_DB_HOST
     */
    protected String getDEFAULT_DB_HOST() {
        return DEFAULT_DB_HOST;
    }
    
    /**
     * <p>createDatabase.</p>
     *
     * @throws java.sql.SQLException if any.
     */
    public void createDatabase() throws SQLException {

        if (!first_time_created && !database_changed) {
            logger.info("TestDatabaseCreator.createDatabase: database not changed or not first time so not creating DB");
            return;
        }

        first_time_created = false;
        Connection connectionToPostgres = null;
        Connection connectionToSeqware = null;
        try {
            // connectionToPostgres = createConnection(POSTGRE_DB, POSTGRE_USER, POSTGRE_PASSWORD);
            // loadDatabase(connectionToPostgres);
            connectionToSeqware = createConnection(getSEQWARE_DB(), getPOSTGRE_USER(), getPOSTGRE_PASSWORD());
            loadDBStructure(connectionToSeqware);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("TestDatabaseCreator.createDatabase " + e.getMessage());
        } finally {
            if (connectionToPostgres != null) {
                connectionToPostgres.close();
            }
            if (connectionToSeqware != null) {
                connectionToSeqware.close();
            }
        }
        database_changed = false;
    }
    
    /**
     * Convenient method to run a query against the test database, avoids unclosed connections.
     * @param <T>
     * @param h
     * @param query
     * @param params
     * @return
     */
    public <T extends Object> T runQuery(ResultSetHandler<T> h, String query, Object... params) {
        QueryRunner run = new QueryRunner();
        T result = null;
        Connection connectionToSeqware = null;
        try {
            connectionToSeqware = createConnection(getSEQWARE_DB(), getPOSTGRE_USER(), getPOSTGRE_PASSWORD());
            result = run.query(connectionToSeqware, query, h, params);
        } catch(Exception e){
            throw new RuntimeException(e);
        } finally {
            // Use this helper method so we don't have to check for null
            DbUtils.closeQuietly(connectionToSeqware);
        }

        return result;
    }

    /**
     * <p>dropDatabase.</p>
     *
     * @throws java.sql.SQLException if any.
     */
    public void dropDatabase() throws SQLException {
        Connection connectionToPostgres = null;
        try {
            connectionToPostgres = createConnection(getPOSTGRE_DB(), getPOSTGRE_USER(), getPOSTGRE_PASSWORD());
            unLoadDatabase(connectionToPostgres);
        } catch (Exception e) {
//            e.printStackTrace();
            logger.info("TestDatabaseCreator.dropDatabase" + e.getMessage());
        } finally {
            if (connectionToPostgres != null) {
                connectionToPostgres.close();
            }
        }
    }
    
    /**
     * Drop a database schema even when users are connected to it
     *
     * @throws java.sql.SQLException if any.
     */
    public void dropDatabaseWithUsers() throws SQLException {
        Connection connectionToPostgres = null;
        try {
            connectionToPostgres = createConnection(getSEQWARE_DB(), getPOSTGRE_USER(), getPOSTGRE_PASSWORD());
            connectionToPostgres.createStatement().execute("drop schema if exists public cascade;");
            connectionToPostgres.createStatement().execute("create schema public;");
        } catch (Exception e) {
//            e.printStackTrace();
            logger.info("TestDatabaseCreator.dropDatabaseWithUsers" + e.getMessage());
        } finally {
            if (connectionToPostgres != null) {
                connectionToPostgres.close();
            }
        }
    }

    /**
     * <p>markDatabaseChanged.</p>
     */
    public static void markDatabaseChanged() {
        database_changed = true;
    }

    private void unLoadDatabase(Connection connection) throws SQLException {
        connection.createStatement().execute("DROP DATABASE IF EXISTS "+getSEQWARE_DB()+";");
    }

    private Connection createConnection(String databaseName, String userName, String password) throws Exception {
        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            throw new Exception("Where is your PostgreSQL JDBC Driver? Include in your library path!");

        }

        try {

            return DriverManager.getConnection("jdbc:postgresql://"+getDEFAULT_DB_HOST()+":5432/" + databaseName, userName, password);

        } catch (SQLException e) {

            throw new Exception("Connection Failed! Check output console "+e);

        }
    }

    private void loadDatabase(Connection connection) throws SQLException {
        System.out.println("----------------Creating Database "+getSEQWARE_DB()+"--------------------");
        connection.createStatement().execute("DROP DATABASE IF EXISTS "+getSEQWARE_DB()+";");
        connection.createStatement().execute("CREATE DATABASE "+getSEQWARE_DB()+" WITH OWNER = "+getSEQWARE_USER()+";");
    }

    private static void loadDBStructure(Connection connection) throws SQLException {
        System.out.println("----------------Loading dump into PostgreSQL--------------------");
        try {
            System.out.println("Loading schema");
            connection.createStatement().execute(getClassPathFileToString("seqware_meta_db.sql"));
            System.out.println("Loading basic data");
            connection.createStatement().execute(getClassPathFileToString("seqware_meta_db_data.sql"));
            System.out.println("Loading testing data");
            connection.createStatement().execute(getClassPathFileToString("seqware_meta_db_testdata.sql"));
	

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("----------------Dump Loaded--------------------");
    }

    private static String getClassPathFileToString(String path) throws IOException {
        InputStream in = TestDatabaseCreator.class.getResourceAsStream(path);
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * @return the POSTGRE_DB
     */
    protected static String getPOSTGRE_DB() {
        return POSTGRE_DB;
    }

    /**
     * @return the SEQWARE_DB
     */
    protected String getSEQWARE_DB() {
        return SEQWARE_DB;
    }

    /**
     * @return the POSTGRE_USER
     */
    protected String getPOSTGRE_USER() {
        return POSTGRE_USER;
    }

    /**
     * @return the POSTGRE_PASSWORD
     */
    protected String getPOSTGRE_PASSWORD() {
        return POSTGRE_PASSWORD;
    }

    /**
     * @return the SEQWARE_USER
     */
    protected String getSEQWARE_USER() {
        return SEQWARE_USER;
    }

    /**
     * @return the SEQWARE_PASSWORD
     */
    protected String getSEQWARE_PASSWORD() {
        return SEQWARE_PASSWORD;
    }
    
      /**
     * Unfortunately, postgres does not allow the straight dropdb and createdb
     * when tomcat is used (perhaps we leave open a connection)
     */
    protected void basicResetDatabaseWithUsers() {
        try {
            this.dropDatabaseWithUsers();
            this.markDatabaseChanged();
            this.createDatabase();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}

