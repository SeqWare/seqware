package com.github.seqware.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A feature set is a set (a.k.a. collection, bag) of features.
 *
 * @author jbaran
 */
public class FeatureSet extends Atom implements Versionable, ACLable {

    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;

    /**
     * The set of features this instance represents.
     */
    private Set<Feature> features = new HashSet<Feature>();

    /**
     * Creates an instance of an anonymous feature set.
     */
    public FeatureSet() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }

    public ACL getPermissions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setPermissions(ACL acl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setVersion(String version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Versionable getPrecedingVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPrecedingVersion(Versionable predecessor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
