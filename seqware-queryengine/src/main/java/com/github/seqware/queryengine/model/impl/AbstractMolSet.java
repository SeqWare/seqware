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
package com.github.seqware.queryengine.model.impl;

import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 * Implements TTL behaviour for sets
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class AbstractMolSet<S extends MolSetInterface> extends MoleculeImpl<S> {

    /**
     * Record whether deletes should cascade
     */
    private boolean cascade = false;
    
    /**
     * <p>Constructor for AbstractMolSet.</p>
     */
    public AbstractMolSet() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public boolean getCascade() {
        return cascade;
    }

    /** {@inheritDoc} */
    @Override
    public void setTTL(long time, boolean cascade) {
        super.setTTL(time, cascade);
        this.cascade = cascade;
    }

    /** {@inheritDoc} */
    @Override
    public void setTTL(int hours, boolean cascade) {
        super.setTTL(hours, cascade);
        this.cascade = cascade;
    }
    
}
