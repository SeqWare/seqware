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
package com.github.seqware.queryengine.impl;

import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.hbasemrlazy.MRLazyFeatureSet;

/**
 * A Simple implementation of the CreateUpdateManager interface. We can make this more
 * efficient later.
 *
 * The current idea is that we try to minimize the interaction with the user by
 * using Hibernate/JPA-like semantics
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class MRHBaseModelManager extends HBaseModelManager {
    
    /** {@inheritDoc} */
    @Override
    public FeatureSet.Builder buildFeatureSetInternal() {
        FeatureSet.Builder fSet = MRLazyFeatureSet.newBuilder();
        return fSet;
    }
}
