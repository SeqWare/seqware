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
package com.github.seqware.queryengine.model.interfaces;

import java.util.Date;

/**
 * Interface for object that can be set to expire at some point in the future.
 * There will be no guaranteed expiry times and the admin may actually wish to
 * turn this off. The way this might function is that an admin may simply decide
 * to not run a matching daily task to clean-up expired elements.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface TTLable {

    /**
     * A time to set that means that the associated object should never expire
     */
    public long FOREVER = -1;
    
    /**
     * Convenience method to set the expiry time
     *
     * @param expiryTime a rough time in the future to set expiry
     * @param cascade whether the expiry should cascade down to children in the
     * case of sets (ignored) when the target is not a set
     */
    public void setTTL(long expiryTime, boolean cascade);

    /**
     * Convenience method to set the expiry time for roughly hours ahead
     *
     * @param hours a rough time in the future to set expiry
     * @param cascade whether the expiry should cascade down to children in the
     * case of sets (ignored) when the target is not a set
     */
    public void setTTL(int hours, boolean cascade);
    
    /**
     * Return the expiry date
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getExpiryDate();
    
    /**
     * Return the expiry date in terms of hours in the future
     * (may fail if the time difference is too large)
     *
     * @return a int.
     */
    public int getTTL();
    
    /**
     * Return true iff this expires
     *
     * @return a boolean.
     */
    public boolean isExpires();
    
    /**
     * Return raw expiry time
     *
     * @return a long.
     */
    public long getExpiryTime();
    
    /**
     * Return whether cascade should occur on deletion
     *
     * @return a boolean.
     */
    public boolean getCascade();
}
