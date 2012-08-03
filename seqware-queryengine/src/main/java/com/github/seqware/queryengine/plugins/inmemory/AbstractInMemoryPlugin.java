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
package com.github.seqware.queryengine.plugins.inmemory;

import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;

/**
 * Base class for all in-memory plug-ins
 *
 * @author dyuen
 */
public abstract class AbstractInMemoryPlugin implements AnalysisPluginInterface {

    protected FeatureSet inputSet;

    @Override
    public AnalysisPluginInterface.ReturnValue test() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue verifyParameters() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue verifyInput() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue filterInit() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue filter() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue verifyOutput() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue cleanup() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    public abstract void performInMemoryRun();
}
