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
package net.sourceforge.seqware.pipeline.tutorial;

import net.sourceforge.seqware.pipeline.plugins.ExtendedTestDatabaseCreator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This runs all the tests needed to test the User Tutorials. The test suite actually enforces order for us.
 * 
 * @author dyuen
 */
@Ignore("see https://github.com/SeqWare/seqware/issues/324")
@RunWith(Suite.class)
@Suite.SuiteClasses(value = { UserPhase1.class, UserPhase2.class, UserPhase3.class, UserPhase4.class, UserPhase5.class, UserPhase6.class })
public class OldUserTutorialSuiteET extends TutorialSuite {
    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }
}