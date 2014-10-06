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

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.seqware.common.model.WorkflowRun;

/**
 * <p>
 * WorkflowRunList class.
 * </p>
 * 
 * @author mtaschuk
 * @version $Id: $Id
 */
public class WorkflowRunList {

    protected List<WorkflowRun> tList;

    /**
     * <p>
     * Constructor for WorkflowRunList.
     * </p>
     */
    public WorkflowRunList() {
        tList = new ArrayList<>();
    }

    /**
     * <p>
     * getList.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<WorkflowRun> getList() {
        return tList;
    }

    /**
     * <p>
     * setList.
     * </p>
     * 
     * @param list
     *            a {@link java.util.Collection} object.
     */
    public void setList(Collection<WorkflowRun> list) {
        this.tList = Lists.newArrayList(list);
    }

    /**
     * <p>
     * add.
     * </p>
     * 
     * @param ex
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    public void add(WorkflowRun ex) {
        tList.add(ex);
    }
}
