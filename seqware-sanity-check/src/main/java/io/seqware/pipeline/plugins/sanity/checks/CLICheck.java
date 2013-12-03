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
package io.seqware.pipeline.plugins.sanity.checks;

import io.seqware.pipeline.plugins.sanity.ProvidedBundleCLIUserPhase5;
import io.seqware.pipeline.plugins.sanity.QueryRunner;
import io.seqware.pipeline.plugins.sanity.SanityCheckPluginInterface;
import java.sql.SQLException;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase1;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase2;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase3;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase4;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase6;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.openide.util.lookup.ServiceProvider;

/**
 * Runs through our CLI tutorial
 * @author dyuen
 */
@ServiceProvider(service = SanityCheckPluginInterface.class)
public class CLICheck implements SanityCheckPluginInterface { 

    @Override
    public boolean check(QueryRunner qRunner, Metadata metadataWS) throws SQLException {
        JUnitCore core = new JUnitCore();
        Result run = core.run(CLIUserPhase1.class, CLIUserPhase2.class, CLIUserPhase3.class , CLIUserPhase4.class , ProvidedBundleCLIUserPhase5.class, CLIUserPhase6.class);
        System.out.println("Test run count: " + run.getRunCount());
        System.out.println("Test fail count: " + run.getFailureCount());
        return run.wasSuccessful();
    }
    
    @Override
    public String getDescription(){
        return "Could not run through the CLI \"Getting Started\" tutorials";
    }
    
    @Override
    public int getPriority(){
        return 110;
    }
}
