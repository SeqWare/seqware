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
package com.github.seqware.model;

/**
 * This interface sketches out the result that might be returned from a
 * asynchronous query.
 *
 * @author dyuen
 */
public interface QueryFuture {

    /**
     * Blocking call to retrieve results of a query
     *
     * @return FeatureSet with desired results, null in case of failure?
     */
    public FeatureSet get();

    /**
     * Returns true iff the query is ready with its results.
     *
     * @return whether the results are ready without blocking
     */
    public boolean isDone();
}
