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

import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SGIDIterable;
import com.github.seqware.queryengine.util.SeqWareIterable;

/**
 * Implement HBase optimizations for the back-end
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class HBasePersistentBackEnd extends SimplePersistentBackEnd {

    /**
     * <p>Constructor for HBasePersistentBackEnd.</p>
     *
     * @param i a {@link com.github.seqware.queryengine.impl.StorageInterface} object.
     */
    public HBasePersistentBackEnd(StorageInterface i) {
        super(i);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<User> getUsers() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(User.class, User.prefix);
        }
        return super.getUsers();
    }

    /**
     * Wraps the Scan class for low memory use
     * @return
     */
    private <T extends Atom> SeqWareIterable handleTableScan(Class<T> t, String prefix) {
        Iterable<SGID> allAtomsForTable = ((HBaseStorage) storage).getAllAtomsForTable(prefix);
        return new SGIDIterable(allAtomsForTable, t);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<Group> getGroups() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(Group.class, Group.prefix);
        }
        return super.getGroups();
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<ReferenceSet> getReferenceSets() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(ReferenceSet.class, ReferenceSet.prefix);
        }
        return super.getReferenceSets();
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<FeatureSet> getFeatureSets() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(FeatureSet.class, FeatureSet.prefix);
        }
        return super.getFeatureSets();
    }
    
    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<Reference> getReferences(){
        if (storage instanceof HBaseStorage) {
            return handleTableScan(Reference.class, Reference.prefix);
        }
        return super.getReferences();
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<TagSet> getTagSets() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(TagSet.class, TagSet.prefix);
        }
        return super.getTagSets();
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<Tag> getTags() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(Tag.class, Tag.prefix);
        }
        return super.getTags();
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<Plugin> getPlugins() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(Plugin.class, Plugin.prefix);
        }
        return super.getPlugins();
    }
}
