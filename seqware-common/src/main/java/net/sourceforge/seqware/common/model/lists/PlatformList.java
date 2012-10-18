/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.common.model.lists;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.model.Platform;

/**
 * <p>PlatformList class.</p>
 * 
 */
public class PlatformList{

    protected List<Platform> tList;

    /**
     * <p>Constructor for PlatformList.</p>
     */
    public PlatformList() {
        tList = new ArrayList<Platform>();
    }

    /**
     * <p>getList.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Platform> getList() {
        return tList;
    }

    /**
     * <p>setList.</p>
     *
     * @param list a {@link java.util.List} object.
     */
    public void setList(List<Platform> list) {
        this.tList = list;
    }

    /**
     * <p>add.</p>
     *
     * @param ex a {@link net.sourceforge.seqware.common.model.Platform} object.
     */
    public void add(Platform ex) {
        tList.add(ex);
    }
}
