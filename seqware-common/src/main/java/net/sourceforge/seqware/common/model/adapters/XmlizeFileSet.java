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
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.lists.FileList;

/**
 * <p>XmlizeFileSet class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class XmlizeFileSet extends XmlAdapter<FileList, Set<File>> {

    /** {@inheritDoc} */
    @Override
    public Set<File> unmarshal(FileList vt) throws Exception {
        Set<File> fileSet = new TreeSet<File>();
        for (File l : vt.getList()) {
            fileSet.add(l);
        }
        return fileSet;
    }

    /** {@inheritDoc} */
    @Override
    public FileList marshal(Set<File> bt) throws Exception {
        if (bt != null) {
            List<File> list = new ArrayList<File>(bt);
            FileList fileList = new FileList();
            fileList.setList(list);
            return fileList;
        } else {
            return null;
        }
    }
}
