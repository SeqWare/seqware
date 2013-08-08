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
package net.sourceforge.seqware.pipeline.cli_tutorial;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.sourceforge.seqware.pipeline.plugins.PluginRunnerET;

/**
 * Build and install a bundle, used by both the User tutorial and the Developer tutorial
 * @author dyuen
 */
public class CLIUserPhase5 {
    

    protected File exportINI(PluginRunnerET pit, List<Integer> accessions) throws IOException {
        // launch our specific workflow and get store its workflow run accession
        File exportINIFile = pit.exportINIFile("Java workflow", accessions.get(0), true);
        return exportINIFile;
    }
}
