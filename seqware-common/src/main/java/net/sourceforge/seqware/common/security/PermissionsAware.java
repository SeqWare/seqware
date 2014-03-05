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

import java.util.LinkedHashSet;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>PermissionsAware interface.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public interface PermissionsAware {
    
    public boolean givesPermission(Registration registration, LinkedHashSet<Integer> path);
    
    /**
     * <p>givesPermission.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a boolean.
     */
    public boolean givesPermission(Registration registration);
        
}
