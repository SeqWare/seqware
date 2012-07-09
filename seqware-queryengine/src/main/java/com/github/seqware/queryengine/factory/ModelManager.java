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
package com.github.seqware.queryengine.factory;

import com.github.seqware.queryengine.model.*;

/**
 * A very simple Manager that is somewhat like a very stripped down
 * EntityManager from JPA allowing us to create Models, persist them, and
 * retrieve them. See {@linktourl http://docs.oracle.com/javaee/5/api/javax/persistence/EntityManager.html}
 * If this gets too complex, maybe we should consider JPA as an option?
 *
 * Some differences to keep our implementation KISS, models are created through
 * this and are automatically managed. We do not have support for refresh,
 * remove, lock, etc. We also do not have explicit support for transactions.
 *
 * Models are created directly through this so that we can keep track of them
 * regardless of underlying implementation
 *
 * @author dyuen
 */
public interface ModelManager {

    /**
     * This manager defines Atoms to be in the following states
     */
    public enum State {

        /**
         * Unmanaged Atoms, this is implicit since the Model Manager should
         * have no records of unmanaged Atoms
         */
        UNMANAGED,
        /**
         * Totally new Atoms, these Atoms should be stored without
         * checking for old versions
         */
        NEW_CREATION,
        /**
         * New versions of Atoms, these Atoms should take into account a
         * predecessor when storing/updating
         */
        NEW_VERSION,
        /**
         * Managed Atoms that have not otherwise changed
         */
        MANAGED
    };

    /**
     * Erase the current set of managed models without saving them to the
     * back-end
     */
    public void clear();

    /**
     * Start managing a Atom that has been dirtied, for example, a Atom
     * returned from a query
     *
     * @param p
     */
    public void persist(Atom p);

    /**
     * Convenience method to flush() all entities and clear().
     * Currently managed objects will be left in an inconsistent state. 
     * However, this should be more efficient for bulk operations.
     */
    public void close();

    /**
     * Save all managed models to the back-end
     */
    public void flush();

    /**
     * Build a featureSet with a reference
     *
     * @return feature set
     */
    public FeatureSet.Builder buildFeatureSet();

    /**
     * Build a Reference with a given name
     *
     * @return reference
     */
    public Reference.Builder buildReference();

    /**
     * Build a new reference set
     *
     * @return reference set
     */
    public ReferenceSet.Builder buildReferenceSet();

    /**
     * Build a new analysis
     * @return  analysis
     */
    public Analysis.Builder buildAnalysis();
    
    /**
     * Build a set of tags
     *
     * @return tag set
     */
    public TagSpecSet.Builder buildTagSpecSet();

    /**
     * Build an analysis set
     *
     * @return an analysis set
     */
    public AnalysisSet.Builder buildAnalysisSet();

    /**
     * Build a user
     *
     * @return
     */
    public User.Builder buildUser();

    /**
     * Build a group
     *
     * @return
     */
    public Group.Builder buildGroup();

    /**
     * Build a TagSpecification
     * @return 
     */
    public TagSpec.Builder buildTagSpec();
    
//    /**
//     * Build a tag
//     *
//     * @return
//     */
//    public Tag.Builder buildTag();

    /**
     * Build a feature
     *
     * @return
     */
    public Feature.Builder buildFeature();

    /**
     * Called by Atoms when they are successfully created thus becoming
     * dirty, and needing to be written to the database.
     *
     * @param source new object to be created in the backend
     */
    public void objectCreated(Atom source);

    /**
     * Called by Atoms when they need to change state (for example, to
     * notify that they have become a dirty new version)
     *
     * @param source
     * @param state
     */
    public void atomStateChange(Atom source, State state);
}
