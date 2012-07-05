package com.github.seqware.model;

import com.github.seqware.model.interfaces.ACLable;
import com.github.seqware.model.interfaces.TTLable;

/**
 * Defines core functionality that is shared by classes that are controlled
 * by permissions and are atoms
 *
 * @author dyuen
 */
public interface Molecule<T extends Molecule> extends Atom<T>, ACLable, TTLable {

}
