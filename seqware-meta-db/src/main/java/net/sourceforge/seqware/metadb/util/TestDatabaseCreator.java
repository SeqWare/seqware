package net.sourceforge.seqware.metadb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import java.io.InputStreamReader;

/**
 * <p>TestDatabaseCreator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class TestDatabaseCreator {

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
     * <p>createDatabase.</p>
     *
     * @throws java.sql.SQLException if any.
     */
    public static void createDatabase() throws SQLException {

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
            connectionToSeqware = createConnection(SEQWARE_DB, POSTGRE_USER, POSTGRE_PASSWORD);
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
     * <p>dropDatabase.</p>
     *
     * @throws java.sql.SQLException if any.
     */
    public static void dropDatabase() throws SQLException {
        Connection connectionToPostgres = null;
        try {
            connectionToPostgres = createConnection(POSTGRE_DB, POSTGRE_USER, POSTGRE_PASSWORD);
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
    public static void dropDatabaseWithUsers() throws SQLException {
        Connection connectionToPostgres = null;
        try {
            connectionToPostgres = createConnection(SEQWARE_DB, POSTGRE_USER, POSTGRE_PASSWORD);
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

    private static void unLoadDatabase(Connection connection) throws SQLException {
        connection.createStatement().execute("DROP DATABASE IF EXISTS "+SEQWARE_DB+";");
    }

    private static Connection createConnection(String databaseName, String userName, String password) throws Exception {
        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            throw new Exception("Where is your PostgreSQL JDBC Driver? Include in your library path!");

        }

        try {

            return DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/" + databaseName, userName, password);

        } catch (SQLException e) {

            throw new Exception("Connection Failed! Check output console "+e);

        }
    }

    private static void loadDatabase(Connection connection) throws SQLException {
        System.out.println("----------------Creating Database "+SEQWARE_DB+"--------------------");
        connection.createStatement().execute("DROP DATABASE IF EXISTS "+SEQWARE_DB+";");
        connection.createStatement().execute("CREATE DATABASE "+SEQWARE_DB+" WITH OWNER = "+SEQWARE_USER+";");
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
}

