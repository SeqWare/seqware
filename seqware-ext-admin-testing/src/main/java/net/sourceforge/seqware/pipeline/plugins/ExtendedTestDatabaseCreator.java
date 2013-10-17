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
 * This class controls configuration of the database used for extended integration tests.
 * 
 * If these keys are used in your .seqware/settings file, you can redirect your testing DB 
 * (which should match the REST URL that you require as well, otherwise you will be 
 * resetting a database completely different from your web service)
 * @author dyuen
 */
public class ExtendedTestDatabaseCreator extends TestDatabaseCreator {

    public static final String EXTENDED_TEST_DB_HOST_KEY = "EXTENDED_TEST_DB_HOST";
    public static final String EXTENDED_TEST_DB_NAME_KEY = "EXTENDED_TEST_DB_NAME";
    public static final String EXTENDED_TEST_USERNAME_KEY = "EXTENDED_TEST_DB_USER";
    public static final String EXTENDED_TEST_PASSWORD_KEY = "EXTENDED_TEST_DB_PASSWORD";
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
        if (settings.containsKey(EXTENDED_TEST_DB_NAME_KEY)){
            return settings.get(EXTENDED_TEST_DB_NAME_KEY);
        }
        Log.debug("Could not retrieve extended test db, using default from unit tests");
        return super.getSEQWARE_DB();
    }

    /**
     * @return the EXTENDED_TEST_USERNAME_KEY
     */
    @Override
    protected String getSEQWARE_USER() {
        if (settings.containsKey(EXTENDED_TEST_USERNAME_KEY)){
            return settings.get(EXTENDED_TEST_USERNAME_KEY);
        }
        Log.debug("Could not retrieve extended test db username, using default from unit tests");
        return super.getSEQWARE_USER();
    }

    /**
     * @return the EXTENDED_TEST_PASSWORD_KEY
     */
    @Override
    protected String getSEQWARE_PASSWORD() {
        if (settings.containsKey(EXTENDED_TEST_PASSWORD_KEY)){
            return settings.get(EXTENDED_TEST_PASSWORD_KEY);
        }
        Log.debug("Could not retrieve extended test db password, using default from unit tests");
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
        Log.debug("Could not retrieve extended test db host, using default from unit tests");
        return super.getDEFAULT_DB_HOST();
    }
    
    /**
     * Unfortunately, postgres does not allow the straight dropdb and createdb
     * when tomcat is used (perhaps we leave open a connection)
     */
    public static void resetDatabaseWithUsers() {
        ExtendedTestDatabaseCreator creator = new ExtendedTestDatabaseCreator();
        creator.basicResetDatabaseWithUsers();
    }
}
