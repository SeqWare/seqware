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
package com.github.seqware.factory;

import com.github.seqware.model.*;
import javax.swing.event.ChangeListener;

/**
 * A very simple Manager that is somewhat like a very stripped down
 * EntityManager from JPA allowing us to create Models, persist them, and
 * retrieve them. See {@link http://docs.oracle.com/javaee/5/api/javax/persistence/EntityManager.html}
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
     * Erase the current set of managed models without saving them to the
     * back-end
     */
    public void clear();

    /**
     * Start managing a Particle that has been dirtied, for example, a particle 
     * returned from a query
     *
     * @param p
     */
    public void persist(Particle p);

    /**
     * Convenience method to flush() all entities and clear()
     */
    public void close();

    /**
     * Save all managed models to the back-end
     */
    public void flush();

    /**
     * Build a featureSet with a reference
     * @return feature set
     */
    public FeatureSet.Builder buildFeatureSet();

    /**
     * Build a Reference with a given name
     * @return reference
     */
    public Reference.Builder buildReference();

    /**
     * Build a new reference set
     * @return reference set
     */
    public ReferenceSet.Builder buildReferenceSet();

    /**
     * Build a set of tags
     * @return tag set
     */
    public TagSet.Builder buildTagSet();

    /**
     * Build an analysis set
     * @return an analysis set
     */
    public AnalysisSet.Builder buildAnalysisSet();
    
    /**
     * Build a user
     * @return 
     */
    public User.Builder buildUser();
    
    /**
     * Build a group
     * @return 
     */
    public Group.Builder buildGroup();
    
    /**
     * Build a tag
     * @return 
     */
    public Tag.Builder buildTag();
    
    /**
     * Build a feature
     * @return 
     */
    public Feature.Builder buildFeature();
    
    /**
     * Called by Particles when they are successfully created/updated 
     * thus becoming dirty, and needing to be written to the database. 
     *
     * @param source new object to be created in the backend
     * @param newObject whether this is truly a new object, or merely an update 
     * which means that this is just a new version of an existing particle
     */
    public void objectCreated(Particle source, boolean newObject);
}
