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

import com.github.seqware.queryengine.factory.BackEndInterface;
import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.factory.ModelManager.State;
import com.github.seqware.queryengine.model.Analysis.Builder;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.inMemory.*;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    private Map<String, AtomStatePair> dirtySet = new HashMap<String, AtomStatePair>();
    private BackEndInterface backend = Factory.getBackEnd();

    /**
     * Flush objects to the back-end giving a working list
     *
     * @param workingList
     */
    protected void flushObjects(List<Entry<String, AtomStatePair>> workingList) {
        // create separate working lists for types of classes 
        Map<Class, List<Atom>> sortedStore = new HashMap<Class, List<Atom>>();
        Map<Class, List<Atom>> sortedUpdate = new HashMap<Class, List<Atom>>();
        for (Entry<String, AtomStatePair> e : workingList) {
            AtomImpl atom = (AtomImpl) e.getValue().atom;
            Class cl = atom.getHBaseClass();
            if (e.getValue().getState() == State.NEW_VERSION){
                if (!sortedUpdate.containsKey(cl)) {
                    sortedUpdate.put(cl, new ArrayList<Atom>());
                }
                sortedUpdate.get(cl).add(e.getValue().getAtom());
            } else{
                if (!sortedStore.containsKey(cl)) {
                    sortedStore.put(cl, new ArrayList<Atom>());
                }
                sortedStore.get(cl).add(e.getValue().getAtom());
            }
        }
        // order in order to avoid problems when sets are flushed before their elements (leading to unpopulated 
        // timestamp values)
        Class[] classOrder = {Feature.class, Tag.class, User.class, Reference.class, Analysis.class, FeatureSet.class, Group.class, TagSpecSet.class, ReferenceSet.class, AnalysisSet.class};
        for(Class cl : classOrder){
            List<Atom> s1 = sortedStore.get(cl);
            if (s1 != null && !s1.isEmpty()){
                backend.store(s1.toArray(new Atom[s1.size()]));
            }
            List<Atom> s2 = sortedUpdate.get(cl);
            if (s2 != null && !s2.isEmpty()){
                backend.update(s2.toArray(new Atom[s2.size()]));
            }
        }
    }

    @Override
    public void persist(Atom p) {
        AtomImpl pImpl = (AtomImpl) p;
        if (this.dirtySet.containsKey(p.getSGID().toString())) {
            Logger.getLogger(SimpleModelManager.class.getName()).log(Level.INFO, "Attempted to persist a managed object, ignored it");
            return;
        }
        // we also have to make sure that the correct manager is associated with this Atom
        pImpl.setManager(this);
        this.dirtySet.put(p.getSGID().toString(), new AtomStatePair(p, State.MANAGED));
    }

    @Override
    public void clear() {
        dirtySet.clear();
    }

    @Override
    public void close() {
        // close connection with all objects
        for (AtomStatePair p : dirtySet.values()) {
            ((AtomImpl) p.atom).setManager(null);
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
        List<Entry<String, AtomStatePair>> workingList = grabObjectsToBeFlushed();
        flushObjects(workingList);
        if (maintainState) {
            manageFlushedObjects(workingList);
        }
    }

    protected void manageFlushedObjects(List<Entry<String, AtomStatePair>> workingList) {
        // stupid workaround, if someone really leans on the flush() command after doing very little, 
        // they can come back fast enough to start duplicating timestamp values, which leads to really bizarre behaviour
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimpleModelManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // reset dirty map and put back the objects from the working list
        for (Entry<String, AtomStatePair> e : workingList) {
            // looks redundant
            // dirtySet.remove(e.getKey());
            // e.getValue().setState(State.MANAGED);
            if (e.getValue().getAtom() instanceof MolSetInterface) {
                ((MolSetInterface) e.getValue().getAtom()).rebuild();
            }
            e.getValue().setState(State.MANAGED);
            dirtySet.put(e.getKey().toString(), e.getValue());
        }
    }

    protected List<Entry<String, AtomStatePair>> grabObjectsToBeFlushed() {
        // update dirty objects
        // TODO: to deal with the possible semantics of the back-end timestamp, we need to
        // remove objects from a map before they change and then put them back afterwards
        List<Entry<String, AtomStatePair>> workingList = new ArrayList<Entry<String, AtomStatePair>>();
        for (Entry<String, AtomStatePair> p : dirtySet.entrySet()) {
            if (p.getValue().getState() == State.NEW_CREATION || p.getValue().getState() == State.NEW_VERSION) {
                workingList.add(p);
            }
        }
        for (Entry<String, AtomStatePair> e : workingList) {
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
    public TagSpecSet.Builder buildTagSpecSet() {
        TagSpecSet.Builder tSet = null;
        if (backend instanceof SimplePersistentBackEnd) {
            tSet = InMemoryTagSpecSet.newBuilder().setManager(this);
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
    public Tag.Builder buildTagSpec() {
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
        atomStateChange(source, State.NEW_CREATION);
    }

    @Override
    public void atomStateChange(Atom source, State state) {
        // check for valid state transitions
        boolean validTransition = false;
        if (this.dirtySet.containsKey(source.getSGID().toString())) {
            State current = this.dirtySet.get(source.getSGID().toString()).getState();
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
            this.dirtySet.put(source.getSGID().toString(), new AtomStatePair(source, state));
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

        protected AtomStatePair(Atom atom, State state) {
            this.atom = atom;
            this.state = state;
        }
        private Atom atom;
        private State state;

        /**
         * @return the atom
         */
        protected Atom getAtom() {
            return atom;
        }

        /**
         * @param atom the atom to set
         */
        protected void setAtom(Atom p) {
            this.atom = p;
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
            return state.toString() + " " + atom.toString();
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
