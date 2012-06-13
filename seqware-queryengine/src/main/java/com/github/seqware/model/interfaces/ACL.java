package com.github.seqware.model.interfaces;

import com.github.seqware.model.Group;
import com.github.seqware.model.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.SerializationUtils;

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
public class ACL implements Serializable {

    private User owner = null;
    private Group group = null;
    private List<Boolean> rights = new ArrayList<Boolean>();

    /**
     * Creates a new access control list.
     */
    private ACL() {
        super();
        for(int i = 1; i <= 6; i ++){ rights.add(true);}
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
    public List<Boolean> getAccess() {
        return Collections.unmodifiableList(rights);
    }

    /**
     * Create a new ACL builder, this is needed to add ne
     * @return a new builder for new ACL instances
     */
    public static Builder newBuilder() {
        return new Builder();
    }
    
    /**
     * Create an ACL builder started with a copy of this
     * @return 
     */
    public Builder toBuilder(){
        Builder b = new Builder();
        b.acl = (ACL) SerializationUtils.clone(this);
        return b;
    }

    public static class Builder {

        private ACL acl = new ACL();

        /**
         * Set the group for the current Atom
         *
         * @param group
         */
        public Builder setGroup(Group group) {
            acl.group = group;
            return this;
        }

        /**
         * Set the owner for the current Atom
         *
         * @param owner
         */
        public Builder setOwner(User owner) {
            acl.owner = owner;
            return this;
        }

        public Builder setRights(boolean[] rights) {
            assert (rights.length == 6); 
            acl.rights.clear();
            for(boolean b : rights){
                acl.rights.add(b);
            }
            return this;
        }

        public ACL build() {
            if (acl.rights.size() != 6) {
                throw new RuntimeException("Invalid build of ACL"); 
            }
            return acl;
        }
    }
}
