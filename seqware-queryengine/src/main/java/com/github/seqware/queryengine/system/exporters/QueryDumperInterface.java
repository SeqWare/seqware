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
package com.github.seqware.queryengine.system.exporters;

import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;

/**
 * This interface should be extended by a class that a developer wishes to use as
 * input to the QueryVCFDumper. This will run query after query on the input featureset
 * basically filtering the input featureset by each of the queries
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface QueryDumperInterface {
    /**
     * Return the number of queries that we wish to run consecutively
     *
     * @return a int.
     */
    public int getNumQueries();
    
    /**
     * Return each of the getNumQueries
     *
     * @param set a {@link com.github.seqware.queryengine.model.FeatureSet} object.
     * @param queryNum a int.
     * @return a {@link com.github.seqware.queryengine.model.QueryFuture} object.
     */
    public QueryFuture<FeatureSet> getQuery(FeatureSet set, int queryNum);
}
