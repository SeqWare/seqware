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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * List of integers.
 * </p>
 * 
 * Silly wrapper class, seriously?
 * http://howtodoinjava.com/2013/07/30/solved-javax-xml-bind-jaxbexception-class-java-util-arraylist-nor-any-
 * of-its-super-class-is-known-to-this-context/
 * 
 * 
 * @author dyuen
 * @version $Id: $Id
 */
@XmlRootElement
public class IntegerList {

    protected List<Integer> tList;

    /**
     * <p>
     * Constructor for FileList.
     * </p>
     */
    public IntegerList() {
        tList = new ArrayList<>();
    }

    /**
     * <p>
     * getList.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<Integer> getList() {
        return tList;
    }

    /**
     * <p>
     * setList.
     * </p>
     * 
     * @param list
     *            a {@link java.util.List} object.
     */
    public void setList(List<Integer> list) {
        this.tList = list;
    }

    /**
     * <p>
     * add.
     * </p>
     * 
     * @param ex
     *            a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public void add(Integer ex) {
        tList.add(ex);
    }
}
