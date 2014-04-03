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
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.lists.IUSList;


/**
 * <p>XmlizeIUSSortedSet class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class XmlizeIUSSortedSet extends XmlAdapter<IUSList, SortedSet<IUS>>{

    /** {@inheritDoc} */
    @Override
    public SortedSet<IUS> unmarshal(IUSList vt) throws Exception {
        SortedSet<IUS> iusSet = new TreeSet<IUS>();
        for (IUS i : vt.getList())
        {
            iusSet.add(i);
        }
        return iusSet;        
    }

    /** {@inheritDoc} */
    @Override
    public IUSList marshal(SortedSet<IUS> bt) throws Exception {
        if (bt != null)
        {
        List<IUS> list = new ArrayList<IUS>(bt);
        IUSList iusList = new IUSList();
        iusList.setList(list);
        return iusList;
        }
        else return null;
    }
}
