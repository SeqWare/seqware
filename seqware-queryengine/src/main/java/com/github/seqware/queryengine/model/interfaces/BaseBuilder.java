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
package com.github.seqware.queryengine.model.interfaces;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import java.util.regex.Pattern;

/**
 * Builders are used to construct Atom.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class BaseBuilder<S, T extends BaseBuilder> {

    /**
     * Build a Atom
     *
     * @return constructed Atom
     */
    public abstract S build();

    /**
     * Set a manager for this Atom
     *
     * @param aThis a {@link com.github.seqware.queryengine.factory.CreateUpdateManager} object.
     * @return the base-builder itself
     */
    public abstract T setManager(CreateUpdateManager aThis);

    /**
     * Set a friendly rowKey for the atom to be built. Developer should check
     * whether the row key is already in use.
     *
     * @param rowKey a {@link java.lang.String} object.
     * @return a T object.
     */
    public abstract T setFriendlyRowKey(String rowKey);
    
    /**
     * Check a row key
     *
     * @param rowKey a {@link java.lang.String} object.
     * @return a boolean.
     */
    protected boolean checkFriendlyRowKey(String rowKey){
        if (rowKey == null || !Pattern.matches(Constants.refRegex, rowKey)) {
            throw new RuntimeException("Invalid rowkey (" + rowKey + ") names should be of the form " + Constants.refRegex);
            //return false;
        }
        return true;
    }
}
