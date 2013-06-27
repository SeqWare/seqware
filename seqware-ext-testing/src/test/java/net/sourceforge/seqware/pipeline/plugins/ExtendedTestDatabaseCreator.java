/*
 * Copyright (C) 2013 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.util.Map;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.metadb.util.TestDatabaseCreator;

/**
 *
 * @author dyuen
 */
public class ExtendedTestDatabaseCreator extends TestDatabaseCreator {

    public static final String EXTENDED_TEST_DB_HOST_KEY = "EXTENDED_TEST_DB_HOST";
    public static final String EXTENDED_TEST_DB_KEY = "EXTENDED_TEST_DB_NAME";
    public static final String SEQWARE_USER_KEY = "EXTENDED_TEST_DB_USER";
    public static final String SEQWARE_PASSWORD_KEY = "EXTENDED_TEST_DB_PASSWORD";
    private static Map<String, String> settings = null;

    public ExtendedTestDatabaseCreator() {
        try {
            settings = ConfigTools.getSettings();
        } catch (Exception e) {
            Log.fatal("Could not read .seqware/settings, this will likely crash extended integration tests", e);
        }
    }

    /**
     * @return the SEQWARE_DB
     */
    @Override
    protected String getSEQWARE_DB() {
        if (settings.containsKey(EXTENDED_TEST_DB_KEY)){
            return settings.get(EXTENDED_TEST_DB_KEY);
        }
        Log.info("Could not retrieve extended test db, using default from unit tests");
        return super.getSEQWARE_DB();
    }

    /**
     * @return the SEQWARE_USER_KEY
     */
    @Override
    protected String getSEQWARE_USER() {
        if (settings.containsKey(SEQWARE_USER_KEY)){
            return settings.get(SEQWARE_USER_KEY);
        }
        Log.info("Could not retrieve extended test db username, using default from unit tests");
        return super.getSEQWARE_USER();
    }

    /**
     * @return the SEQWARE_PASSWORD_KEY
     */
    @Override
    protected String getSEQWARE_PASSWORD() {
        if (settings.containsKey(SEQWARE_PASSWORD_KEY)){
            return settings.get(SEQWARE_PASSWORD_KEY);
        }
        Log.info("Could not retrieve extended test db password, using default from unit tests");
        return super.getSEQWARE_PASSWORD();
    }
    
    /**
     * @return the DEFAULT_DB_HOST
     */
    @Override
    protected String getDEFAULT_DB_HOST() {
         if (settings.containsKey(EXTENDED_TEST_DB_HOST_KEY)){
            return settings.get(EXTENDED_TEST_DB_HOST_KEY);
        }
        Log.info("Could not retrieve extended test db host, using default from unit tests");
        return super.getDEFAULT_DB_HOST();
    }
    
    /**
     * Unfortunately, postgres does not allow the straight dropdb and createdb
     * when tomcat is used (perhaps we leave open a connection)
     */
    public static void resetDatabaseWithUsers() {
        try {
            ExtendedTestDatabaseCreator creator = new ExtendedTestDatabaseCreator();
            creator.dropDatabaseWithUsers();
            creator.markDatabaseChanged();
            creator.createDatabase();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
