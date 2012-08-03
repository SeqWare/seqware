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

import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.ScanPlugin;
import java.util.Iterator;

/**
 * Base class for all in-memory plug-ins that do Scan
 *
 * @author dyuen
 */
public abstract class AbstractScanInMemoryPlugin extends AbstractInMemoryPlugin implements ScanPlugin<Feature, FeatureSet> {

    @Override
    public void performInMemoryRun() {
        for (Iterator<Feature> it = this.inputSet.iterator(); it.hasNext();) {
            Feature f = it.next();
            scan(f, null);
        }
    }
}
