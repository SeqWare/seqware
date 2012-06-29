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

import com.github.seqware.factory.BackEndInterface;
import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Analysis.Builder;
import com.github.seqware.model.*;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.model.impl.inMemory.*;
import com.github.seqware.model.interfaces.AbstractMolSet;
import com.github.seqware.util.SGID;
import java.util.Map.Entry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Simple implementation of the ModelManager interface. We can make this more
 * efficient later.
 *
 * The current idea is that we try to minimize the interaction with the user by
 * using Hibernate/JPA-like semantics.
 *
 * @author dyuen
 */
public class SimpleModelManager implements ModelManager {

    private Map<SGID, AtomStatePair> dirtySet = new HashMap<SGID, AtomStatePair>();
    private BackEndInterface backend = Factory.getBackEnd();

    protected void flushObjects(List<Entry<SGID, AtomStatePair>> workingList) {
        // this is ugly, but if do end up using phased flushing, we should have one map per class type
        Set<Class> classesToFlush = new HashSet<Class>();
        classesToFlush.add(Tag.class);
        flushObjectsOfClass(workingList, classesToFlush);
        classesToFlush.clear();
        classesToFlush.add(Feature.class);
        classesToFlush.add(User.class);
        classesToFlush.add(Reference.class);
        classesToFlush.add(Analysis.class);
        flushObjectsOfClass(workingList, classesToFlush);
        classesToFlush.clear();
        classesToFlush.add(FeatureSet.class);
        classesToFlush.add(Group.class);
        classesToFlush.add(TagSet.class);
        classesToFlush.add(ReferenceSet.class);
        classesToFlush.add(AnalysisSet.class);
        flushObjectsOfClass(workingList, classesToFlush);
    }

    protected void flushObjectsOfClass(List<Entry<SGID, AtomStatePair>> workingList, Set<Class> classes) {
        // we'll need to flush members of sets first before flushing sets
        for (Entry<SGID, AtomStatePair> p : workingList) {
            AtomImpl p1 = (AtomImpl) p.getValue().getP();
            if (classes.contains(p1.getHBaseClass())) {
                if (p.getValue().getState() == State.NEW_VERSION) {
                    // if they have preceding versions do an update, otherwise store
                    // only molecules should have preceding states
                    backend.update(p.getValue().getP());
                } else {
                    backend.store(p.getValue().getP());
                }
            }
        }
    }

    protected BackEndInterface getBackend() {
        return backend;
    }

    protected Map<SGID, AtomStatePair> getDirtySet() {
        return dirtySet;
    }

    @Override
    public void persist(Atom p) {
        if (this.dirtySet.containsKey(p.getSGID())) {
            Logger.getLogger(SimpleModelManager.class.getName()).log(Level.INFO, "Attempted to persist a managed object, ignored it");
            return;
        }
        // we also have to make sure that the correct manager is associated with this Atom
        ((AtomImpl) p).setManager(this);
        this.dirtySet.put(p.getSGID(), new AtomStatePair(p, State.MANAGED));
    }

    @Override
    public void clear() {
        dirtySet.clear();
    }

    @Override
    public void close() {
        // close connection with all objects
        for (AtomStatePair p : dirtySet.values()) {
            ((AtomImpl) p.p).setManager(null);
        }
        this.flush(false);
        this.clear();
    }

    @Override
    public void flush() {
        this.flush(true);
    }

    /**
     * Normally, when doing a flush, we want to maintain the state of objects
     * that
     *
     * @param maintainState update flushed objects with the current state of
     * things
     */
    protected void flush(boolean maintainState) {
        List<Entry<SGID, AtomStatePair>> workingList = grabObjectsToBeFlushed();
        flushObjects(workingList);
        if (maintainState) {
            manageFlushedObjects(workingList);
        }
    }

    protected void manageFlushedObjects(List<Entry<SGID, AtomStatePair>> workingList) {
        // reset dirty map and put back the objects from the working list
        for (Entry<SGID, AtomStatePair> e : workingList) {
// looks redundant
//            dirtySet.remove(e.getKey());
//            e.getValue().setState(State.MANAGED);
            if (e.getValue().getP() instanceof AbstractMolSet) {
                ((AbstractMolSet) e.getValue().getP()).rebuild();
            }
            e.getValue().setState(State.MANAGED);
            dirtySet.put(e.getKey(), e.getValue());
        }
    }

    protected List<Entry<SGID, AtomStatePair>> grabObjectsToBeFlushed() {
        // update dirty objects
        // TODO: to deal with the possible semantics of the back-end timestamp, we need to
        // remove objects from a map before they change and then put them back afterwards
        List<Entry<SGID, AtomStatePair>> workingList = new ArrayList<Entry<SGID, AtomStatePair>>();
        for (Entry<SGID, AtomStatePair> p : dirtySet.entrySet()) {
            if (p.getValue().getState() == State.NEW_CREATION || p.getValue().getState() == State.NEW_VERSION) {
                workingList.add(p);
            }
        }
        for (Entry<SGID, AtomStatePair> e : workingList) {
            dirtySet.remove(e.getKey());
        }
        return workingList;
    }

    @Override
    public FeatureSet.Builder buildFeatureSet() {
        FeatureSet.Builder fSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            fSet = InMemoryFeatureSet.newBuilder().setManager(this);
        }
        return fSet;
    }

    @Override
    public Reference.Builder buildReference() {
        Reference.Builder ref = null;
        if (backend instanceof SimplePersistentBackEnd) {
            ref = InMemoryReference.newBuilder().setManager(this);
        }
        assert (ref != null);
        return ref;
    }

    @Override
    public ReferenceSet.Builder buildReferenceSet() {
        ReferenceSet.Builder rSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            rSet = InMemoryReferenceSet.newBuilder().setManager(this);
        }
        assert (rSet != null);
        return rSet;
    }

    @Override
    public TagSet.Builder buildTagSet() {
        TagSet.Builder tSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            tSet = InMemoryTagSet.newBuilder().setManager(this);
        }
        assert (tSet != null);
        return tSet;
    }

    @Override
    public AnalysisSet.Builder buildAnalysisSet() {
        AnalysisSet.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            return InMemoryAnalysisSet.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public User.Builder buildUser() {
        User.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            return User.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public Group.Builder buildGroup() {
        Group.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            return InMemoryGroup.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public Tag.Builder buildTag() {
        Tag.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            return Tag.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public Feature.Builder buildFeature() {
        Feature.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            return Feature.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public void objectCreated(Atom source) {
        AtomStateChange(source, State.NEW_CREATION);
    }

    @Override
    public void AtomStateChange(Atom source, State state) {
        // check for valid state transitions
        boolean validTransition = false;
        if (this.dirtySet.containsKey(source.getSGID())) {
            State current = this.dirtySet.get(source.getSGID()).getState();
            if (current == State.MANAGED && state == State.NEW_VERSION) {
                validTransition = true;
            } else if (current == State.MANAGED && state == State.NEW_CREATION) {
                validTransition = true;
            } else if (current == State.UNMANAGED && state == State.MANAGED) {
                validTransition = true;
            } else if (current == State.NEW_CREATION && state == State.MANAGED) {
                validTransition = true;
            } else if (current == State.NEW_VERSION && state == State.MANAGED) {
                validTransition = true;
            }
        } else {
            // assume all other transitions are valid for now
            validTransition = true;
        }
        if (validTransition) {
            this.dirtySet.put(source.getSGID(), new AtomStatePair(source, state));
        }
    }

    @Override
    public Builder buildAnalysis() {
        Analysis.Builder aSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            return InMemoryQueryFutureImpl.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    protected class AtomStatePair {

        protected AtomStatePair(Atom p, State state) {
            this.p = p;
            this.state = state;
        }
        private Atom p;
        private State state;

        /**
         * @return the p
         */
        protected Atom getP() {
            return p;
        }

        /**
         * @param p the p to set
         */
        protected void setP(Atom p) {
            this.p = p;
        }

        /**
         * @return the state
         */
        protected State getState() {
            return state;
        }

        /**
         * @param state the state to set
         */
        protected void setState(State state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return state.toString() + " " + p.toString();
        }
    }

    /**
     * Used for testing only, override the backend choice when testing
     * implementations
     *
     * @param backend
     */
    public void overrideBackEnd(BackEndInterface backend) {
        this.backend = backend;
    }
}
