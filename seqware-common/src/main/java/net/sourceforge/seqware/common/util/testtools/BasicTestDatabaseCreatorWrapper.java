package net.sourceforge.seqware.common.util.testtools;

import java.sql.SQLException;

/**
 * <p>
 * BasicTestDatabaseCreatorWrapper class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class BasicTestDatabaseCreatorWrapper {

    private static final BasicTestDatabaseCreator TEST_DB_CREATOR = new BasicTestDatabaseCreator();

    /**
     * <p>
     * createDatabase.
     * </p>
     * 
     * @throws java.sql.SQLException
     *             if any.
     */
    public static void createDatabase() throws SQLException {
        TEST_DB_CREATOR.createDatabase();
    }

    /**
     * <p>
     * dropDatabase.
     * </p>
     * 
     * @throws java.sql.SQLException
     *             if any.
     */
    public static void dropDatabase() throws SQLException {
        TEST_DB_CREATOR.dropDatabase();
    }

    /**
     * <p>
     * markDatabaseChanged.
     * </p>
     */
    public static void markDatabaseChanged() {
        BasicTestDatabaseCreator.markDatabaseChanged();
    }

    public static void resetDatabaseWithUsers() {
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
    }
}
