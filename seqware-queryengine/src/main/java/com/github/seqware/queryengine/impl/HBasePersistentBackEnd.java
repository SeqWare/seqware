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

import com.github.seqware.queryengine.model.AnalysisSet;
import com.github.seqware.queryengine.model.ReferenceSet;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.TagSpecSet;
import com.github.seqware.queryengine.model.Group;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.util.InMemoryIterable;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implement HBase optimizations for the back-end
 *
 * @author dyuen
 */
public class HBasePersistentBackEnd extends SimplePersistentBackEnd {

    private StorageInterface storage;

    public HBasePersistentBackEnd(StorageInterface i) {
        super(i);
        this.storage = i;
    }

    @Override
    public SeqWareIterable<User> getUsers() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(User.class, User.prefix);
        }
        return super.getUsers();
    }

    /**
     * TODO: change this to wrap the scan class for speed
     *
     * @return
     */
    private <T extends Atom> SeqWareIterable handleTableScan(Class<T> t, String prefix) {
        List<T> list = new ArrayList<T>();
        Iterable<SGID> allAtomsForTable = ((HBaseStorage) storage).getAllAtomsForTable(prefix);
        for (SGID u : allAtomsForTable) { //listOfEverything) {
            T p = storage.deserializeTargetToLatestAtom(u, t);
            list.add(p);
        }
        return new InMemoryIterable(list);
    }

    @Override
    public SeqWareIterable<Group> getGroups() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(Group.class, Group.prefix);
        }
        return super.getGroups();
    }

    @Override
    public SeqWareIterable<ReferenceSet> getReferenceSets() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(ReferenceSet.class, ReferenceSet.prefix);
        }
        return super.getReferenceSets();
    }

    @Override
    public SeqWareIterable<FeatureSet> getFeatureSets() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(FeatureSet.class, FeatureSet.prefix);
        }
        return super.getFeatureSets();
    }

    @Override
    public SeqWareIterable<TagSpecSet> getTagSpecSets() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(TagSpecSet.class, TagSpecSet.prefix);
        }
        return super.getTagSpecSets();
    }

    @Override
    public SeqWareIterable<Tag> getTags() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(Tag.class, Tag.prefix);
        }
        return super.getTags();
    }

    @Override
    public SeqWareIterable<AnalysisSet> getAnalysisSets() {
        if (storage instanceof HBaseStorage) {
            return handleTableScan(AnalysisSet.class, AnalysisSet.prefix);
        }
        return super.getAnalysisSets();
    }
}
