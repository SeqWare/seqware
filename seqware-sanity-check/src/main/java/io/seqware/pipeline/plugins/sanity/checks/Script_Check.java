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

import io.seqware.pipeline.plugins.sanity.QueryRunner;
import io.seqware.pipeline.plugins.sanity.SanityCheckPluginInterface;
import java.io.File;
import java.sql.SQLException;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase1;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase2;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase3;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase4;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase5;
import net.sourceforge.seqware.pipeline.cli_tutorial.CLIUserPhase6;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.openide.util.lookup.ServiceProvider;

/**
 * Runs through our CLI tutorial
 * @author dyuen
 */
@ServiceProvider(service = SanityCheckPluginInterface.class)
public class Script_Check implements SanityCheckPluginInterface { 

    @Override
    public boolean check(QueryRunner qRunner, Metadata metadataWS) throws SQLException {
        File retrieveCompiledSeqwareScript = ITUtility.retrieveCompiledSeqwareScript();
        return retrieveCompiledSeqwareScript.exists();
    }
    
    @Override
    public String getDescription(){
        return "Could not locate seqware script";
    }
    
    @Override
    public int getPriority(){
        return 8;
    }
}
