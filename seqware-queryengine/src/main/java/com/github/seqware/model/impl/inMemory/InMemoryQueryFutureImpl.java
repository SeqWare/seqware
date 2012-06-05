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
package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.Analysis;
import com.github.seqware.model.AnalysisPluginInterface;
import com.github.seqware.model.FeatureSet;

/**
 *
 * @author dyuen
 */
public class InMemoryQueryFutureImpl extends Analysis {
    
    private transient AnalysisPluginInterface plugin;

    public InMemoryQueryFutureImpl() {
        super();
    }

    @Override
    public FeatureSet get() {
        getPlugin().map();
        getPlugin().reduce();
        return getPlugin().getFinalResult();
    }

    @Override
    public boolean isDone() {
        return true;
    }

    /**
     * Create a new AnalysisSet builder
     *
     * @return
     */
    public static Analysis.Builder newBuilder() {
        return new InMemoryQueryFutureImpl.Builder();
    }

    @Override
    public Analysis.Builder toBuilder() {
        InMemoryQueryFutureImpl.Builder b = new InMemoryQueryFutureImpl.Builder();
        b.analysis = (InMemoryQueryFutureImpl) this.copy(false);
        b.setParameters(this.getParameters());
        return b;
    }

    @Override
    public AnalysisPluginInterface getPlugin() {
        return plugin;
    }

    @Override
    protected void setPlugin(AnalysisPluginInterface plugin) {
        this.plugin = plugin;
    }

    public static class Builder extends Analysis.Builder {

        public Builder() {
            analysis = new InMemoryQueryFutureImpl();
        }

        @Override
        public Analysis build() {
            if (analysis.getParameters() == null || analysis.getPlugin() == null) {
                throw new RuntimeException("Invalid build of Analysis");
            }
            return analysis;
        }
    }
}
