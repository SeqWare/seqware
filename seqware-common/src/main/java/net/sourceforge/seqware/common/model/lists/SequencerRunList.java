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
import net.sourceforge.seqware.common.model.SequencerRun;

/**
 *
 * @author mtaschuk
 */
public class SequencerRunList{

    protected List<SequencerRun> tList;

    public SequencerRunList() {
        tList = new ArrayList<SequencerRun>();
    }

    public List<SequencerRun> getList() {
        return tList;
    }

    public void setList(List<SequencerRun> list) {
        this.tList = list;
    }

    public void add(SequencerRun ex) {
        tList.add(ex);
    }
}