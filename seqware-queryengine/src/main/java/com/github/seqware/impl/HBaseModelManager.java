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
package com.github.seqware.impl;

import com.github.seqware.model.Feature;
import com.github.seqware.util.FSGID;
import com.github.seqware.util.SGID;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Simple implementation of the ModelManager interface. We can make this more
 * efficient later.
 *
 * The current idea is that we try to minimize the interaction with the user by
 * using Hibernate/JPA-like semantics
 *
 * @author dyuen
 */
public class HBaseModelManager extends SimpleModelManager {

    /**
     * Normally, when doing a flush, we want to maintain the state of objects
     * that
     *
     * @param maintainState update flushed objects with the current state of
     * things
     */
    @Override
    protected void flush(boolean maintainState) {
        List<Entry<SGID, AtomStatePair>> workingList = grabObjectsToBeFlushed();
        // check for orphaned Features
        for (Entry<SGID, AtomStatePair> p : workingList) {
            if (p.getValue().getState() == State.NEW_CREATION && p.getValue().getP() instanceof Feature) {
                Feature f = (Feature) p.getValue().getP();
                // should be upgraded now, if not
                if (!(f.getSGID() instanceof FSGID)) {
                    // this should not happen
                    Logger.getLogger(FSGID.class.getName()).log(Level.WARNING, "Orphaned features, please add them to a FeatureSet, aborting flush()");
                    return;
                }
            }
        }

        // update dirty objects
        super.flushObjects(workingList);
        if (maintainState) {
            manageFlushedObjects(workingList);
        }
    }
}
