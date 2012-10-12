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
package com.github.seqware.queryengine.system.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Small hack, allows us to profile test suites for performance.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class ProfilingStub extends Configured implements Tool {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception{
        // Let ToolRunner handle generic command-line options 
        int res = ToolRunner.run(new Configuration(), new ProfilingStub(), args);
        System.exit(res);
    }

    /** {@inheritDoc} */
    @Override
    public int run(String[] args) throws Exception {
        JUnitCore runner = new JUnitCore();
        Result run = runner.run(SystemTestSuite.class);
        //runner.run(InMemoryFileStoragePBSerializationSuite.class);
        System.out.println(run.toString());
        System.out.println("Fail count: " + run.getFailureCount());
        System.out.println("Success: " + run.wasSuccessful());
        return run.getFailureCount();
    }
}
