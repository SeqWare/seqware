package com.github.seqware.model;

import java.util.Arrays;

/**
 * Access control list that determines permissions and access rights to features
 * and feature sets.
 *
 * Philosophy here will be that the back-end will expose ACL, but the user (ex:
 * RESTful web service will enforce permissions rather than have us enforce it.
 * The idea being that if a user has direct access to this API, we cannot defend
 * successfully against a malicious user.
 *
 * Permissive philosophy is that owner and group are not specified and all read
 * and write is possible
 *
 * @author dyuen
 * @author jbaran
 */
public class ACL extends Particle<ACL> {

    private User owner = null;
    private Group group = null;
    private boolean[] rights = new boolean[6];

    /**
     * Creates a new access control list.
     */
    public ACL() {
        super();
        Arrays.fill(rights, true);
    }

    /**
     * Get the owner for the object
     *
     * @return User that owns the object
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Get the group for the object
     *
     * @return Group that the object belongs to
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Get whether 0) owner can read, 1) owner can write, 2) group can read, 3)
     * group can write, 4) other users can read 5) other users can write
     *
     * @return array access to RW rights for owner, group, and others
     */
    public boolean[] getAccess() {
        return rights;
    }

    /**
     * Set the group for the current particle
     *
     * @param group
     */
    public void setGroup(Group group) throws SecurityException {
        this.group = group;
    }

    /**
     * Set the owner for the current particle
     *
     * @param owner
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
