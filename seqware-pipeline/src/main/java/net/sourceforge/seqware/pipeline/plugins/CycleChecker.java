/*
 * Copyright (C) 2011 SeqWare
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

import java.util.*;
import net.sourceforge.seqware.common.hibernate.CheckForCycles;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.util.Log;
import org.openide.util.lookup.ServiceProvider;

/**
 * <p>CycleChecker class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class CycleChecker extends Plugin {

    ReturnValue ret = new ReturnValue();

    /**
     * <p>Constructor for CycleChecker.</p>
     */
    public CycleChecker() {
        super();
        parser.acceptsAll(Arrays.asList("study-accession"), "The SeqWare accession of the study you want to check").withRequiredArg();
        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue init() {
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_test() {
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_run() {
        
            if (options.has("study-accession")) {
                Integer study = Integer.parseInt((String) options.valueOf("study"));
                CheckForCycles cfc = new CheckForCycles();
                println(cfc.checkStudy(study));
            } else {
                println("Combination of parameters not recognized!");
                println(this.get_syntax());
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            }
        
        return ret;
    }


    /** {@inheritDoc} */
    @Override
    public ReturnValue clean_up() {
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public String get_description() {
        return "Checks for cycles in the sample hierarchy and processing hierarchy of a particular study and prints some information about the study";
    }
    
    
}
