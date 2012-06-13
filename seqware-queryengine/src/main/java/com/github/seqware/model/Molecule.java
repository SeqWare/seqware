package com.github.seqware.model;

import com.github.seqware.model.interfaces.ACLable;

/**
 * Implements core functionality that is shared by classes that are controlled
 * by permissions and {@link Versionable} (as well as {@link Taggable})
 *
 * @author dyuen
 */
public interface Molecule<T extends Molecule> extends Atom<T>, ACLable {

}
