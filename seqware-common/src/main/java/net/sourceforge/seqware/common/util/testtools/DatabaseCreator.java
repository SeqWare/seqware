package net.sourceforge.seqware.common.util.testtools;

import java.sql.SQLException;
import net.sourceforge.seqware.metadb.util.TestDatabaseCreator;

public class DatabaseCreator {

    public static void createDatabase() throws SQLException {
        TestDatabaseCreator.createDatabase();
    }

    public static void dropDatabase() throws SQLException {
        TestDatabaseCreator.dropDatabase();
    }

    public static void markDatabaseChanged() {
        TestDatabaseCreator.markDatabaseChanged();
    }
}
