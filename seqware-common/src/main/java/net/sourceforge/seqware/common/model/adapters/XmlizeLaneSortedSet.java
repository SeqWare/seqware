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
package net.sourceforge.seqware.common.model.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.lists.LaneList;


/**
 *
 * @author mtaschuk
 */
public class XmlizeLaneSortedSet extends XmlAdapter<LaneList, SortedSet<Lane>>{

    @Override
    public SortedSet<Lane> unmarshal(LaneList vt) throws Exception {
        SortedSet<Lane> laneSet = new TreeSet<Lane>();
        for (Lane l : vt.getList())
        {
            laneSet.add(l);
        }
        return laneSet;        
    }

    @Override
    public LaneList marshal(SortedSet<Lane> bt) throws Exception {
        if (bt != null)
        {
        List<Lane> list = new ArrayList<Lane>(bt);        
        LaneList laneList = new LaneList();
        laneList.setList(list);
        return laneList;
        }
        else return null;
    }
}
