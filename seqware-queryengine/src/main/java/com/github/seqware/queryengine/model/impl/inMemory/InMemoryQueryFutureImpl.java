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
package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.AnalysisRun;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;

/**
 * <p>InMemoryQueryFutureImpl class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class InMemoryQueryFutureImpl<T> extends AnalysisRun {
    
    private static final long serialVersionUID = 1L;
    
    private transient AnalysisPluginInterface<T> plugin;

    /**
     * <p>Constructor for InMemoryQueryFutureImpl.</p>
     */
    public InMemoryQueryFutureImpl() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public T get() {
        return getPlugin().getFinalResult();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDone() {
        return getPlugin().isComplete();
    }

    /**
     * Create a new AnalysisRun builder
     *
     * @return a {@link com.github.seqware.queryengine.model.AnalysisRun.Builder} object.
     */
    public static AnalysisRun.Builder newBuilder() {
        return new InMemoryQueryFutureImpl.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public AnalysisRun.Builder toBuilder() {
        InMemoryQueryFutureImpl.Builder b = new InMemoryQueryFutureImpl.Builder();
        b.analysis = (InMemoryQueryFutureImpl) this.copy(true);
        b.setParameters(this.getParameters());
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public AnalysisPluginInterface<T> getPlugin() {
        return plugin;
    }

    /** {@inheritDoc} */
    @Override
    protected void setPlugin(AnalysisPluginInterface plugin) {
        this.plugin = plugin;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return AnalysisRun.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        return AnalysisRun.prefix;
    }

    public static class Builder extends AnalysisRun.Builder {

        public Builder() {
            analysis = new InMemoryQueryFutureImpl();
        }

        @Override
        public AnalysisRun build() {
            if (analysis.getParameters() == null /**|| analysis.getPlugin() == null**/) {
                throw new RuntimeException("Invalid build of Analysis");
            }
            return analysis;
        }
    }
}
