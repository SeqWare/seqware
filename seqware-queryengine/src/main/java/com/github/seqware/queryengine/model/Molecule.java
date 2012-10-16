package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.model.interfaces.ACLable;
import com.github.seqware.queryengine.model.interfaces.TTLable;

/**
 * Defines core functionality that is shared by classes that are controlled
 * by permissions and are atoms
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface Molecule<T extends Molecule> extends Atom<T>, ACLable, TTLable {

}
