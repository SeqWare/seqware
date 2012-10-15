package net.sourceforge.seqware.common.util.testtools;

import java.sql.SQLException;
import net.sourceforge.seqware.metadb.util.TestDatabaseCreator;

/**
 * <p>DatabaseCreator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class DatabaseCreator {

    /**
     * <p>createDatabase.</p>
     *
     * @throws java.sql.SQLException if any.
     */
    public static void createDatabase() throws SQLException {
        TestDatabaseCreator.createDatabase();
    }

    /**
     * <p>dropDatabase.</p>
     *
     * @throws java.sql.SQLException if any.
     */
    public static void dropDatabase() throws SQLException {
        TestDatabaseCreator.dropDatabase();
    }

    /**
     * <p>markDatabaseChanged.</p>
     */
    public static void markDatabaseChanged() {
        TestDatabaseCreator.markDatabaseChanged();
    }
}
