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
package net.sourceforge.seqware.common.security;

import java.util.HashSet;
import java.util.Set;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>PermissionsAware interface.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public abstract class PermissionsAware {
    
    public boolean givesPermission(Registration registration){
        return this.givesPermission(registration, new HashSet<Integer>());
    }
  
    public boolean givesPermission(Registration registration, Set<Integer> considered) {
        return this.givesPermissionInternal(registration, considered);
    } 
    /**
     * Short-circuit permission checking by providing a set of entities that have already been checked
     * @param registration
     * @param considered
     * @return 
     */
    public abstract boolean givesPermissionInternal(Registration registration, Set<Integer> considered);
        
}
