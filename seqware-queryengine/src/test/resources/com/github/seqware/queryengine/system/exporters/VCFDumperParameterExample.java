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

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.QueryInterface;
import com.github.seqware.queryengine.system.test.QueryVCFDumperTest;

/**
 * An example of a parameter file. See more possible Queries in {@link QueryInterfaceTest}.
 * @author dyuen
 */
public class VCFDumperParameterExample implements QueryDumperInterface{

    @Override
    public int getNumQueries() {
        // we will run three queries
        return 3;
    }

    @Override
    public QueryFuture<FeatureSet> getQuery(FeatureSet set, int queryNum) {
        if (queryNum == 0){
            /// limits us to CHROM #21
            return SWQEFactory.getQueryInterface().getFeaturesByAttributes(0, set, new RPNStack(new RPNStack.FeatureAttribute("seqid"), new RPNStack.Constant("21"), RPNStack.Operation.EQUAL));
        } else if (queryNum == 1){
            // limits us to the range of 20000000 through 30000000
            return SWQEFactory.getQueryInterface().getFeaturesByRange(0, set, QueryInterface.Location.INCLUDES, "21", 20000000, 30000000);
        } else{
            // limits us to features with a particular tag
            return SWQEFactory.getQueryInterface().getFeaturesByAttributes(0, set, new RPNStack(new RPNStack.TagOccurrence(QueryVCFDumperTest.AD_HOC_TAG_SET, "non_synonymous_codon")));
        }
    }
    
}
