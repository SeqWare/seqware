/*
 * Copyright (C) 2012 SeqWare
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
package com.github.seqware.queryengine.impl.test.util;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

/**
 * Allows us to dynamically build test suites
 * Taken from <a href="http://stackoverflow.com/questions/1070202/junit-suiteclasses-with-a-static-list-of-classes">here</a>
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class DynamicSuite extends Suite {

    /**
     * <p>Constructor for DynamicSuite.</p>
     *
     * @param setupClass a {@link java.lang.Class} object.
     * @throws org.junit.runners.model.InitializationError if any.
     */
    public DynamicSuite(Class<?> setupClass) throws InitializationError {
        super(setupClass, DynamicSuiteBuilder.implSuite());
    }
}
