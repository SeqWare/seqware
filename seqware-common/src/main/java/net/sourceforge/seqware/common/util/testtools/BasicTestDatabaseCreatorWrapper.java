package net.sourceforge.seqware.common.util.testtools;

import java.sql.SQLException;

/**
 * <p>BasicTestDatabaseCreatorWrapper class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class BasicTestDatabaseCreatorWrapper {
    
    private static BasicTestDatabaseCreator testDBCreator = new BasicTestDatabaseCreator();

    /**
     * <p>createDatabase.</p>
     *
     * @throws java.sql.SQLException if any.
     */
    public static void createDatabase() throws SQLException {
        testDBCreator.createDatabase();
    }

    /**
     * <p>dropDatabase.</p>
     *
     * @throws java.sql.SQLException if any.
     */
    public static void dropDatabase() throws SQLException {
        testDBCreator.dropDatabase();
    }

    /**
     * <p>markDatabaseChanged.</p>
     */
    public static void markDatabaseChanged() {
        testDBCreator.markDatabaseChanged();
    }
    
    public static void resetDatabaseWithUsers(){
        testDBCreator.resetDatabaseWithUsers();
    }
}
