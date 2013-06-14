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
package net.sourceforge.seqware.pipeline.modules.utilities;

import net.sourceforge.seqware.metadb.util.TestDatabaseCreator;
import org.junit.Assert;

/**
 *
 * @author dyuen
 */
public class ExternalTestingUtility {
    /**
     * Unfortunately, postgres does not allow the straight dropdb and createdb 
     * when tomcat is used (perhaps we leave open a connection)
     */
    public static void resetDatabaseWithUsers(){
         try {
            TestDatabaseCreator.dropDatabaseWithUsers();
            TestDatabaseCreator.markDatabaseChanged();
            TestDatabaseCreator.createDatabase();
        } catch (Exception e) {
            Assert.assertTrue("Unable to reset database", false);
        }
    }
}
