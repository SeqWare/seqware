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
package net.sourceforge.seqware.webservice.resources.tables;

import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;

/**
 *
 * @author mtaschuk
 */
public class StudyIDResourceTest extends DatabaseResourceIDTest {

    public StudyIDResourceTest() {
        super("/studies/4758");
        jo = new JaxbObject<Study>();
        o = new Study();
    }
     @Override
    protected int testObject(Object o) {
        if (o instanceof Study) {
            Study e = (Study) o;
            if (e.getSwAccession() != Integer.parseInt(id)) {
                System.err.println("Actual ID: "+e.getSwAccession() + " and expected ID: " +Integer.parseInt(id));
                return ReturnValue.INVALIDFILE;
            }

        } else {
            System.err.println("Object is not an instance of Study");
            return ReturnValue.FILENOTREADABLE;
        }
        return ReturnValue.SUCCESS;
    }

	 @Override
	 public void testPut() {
		 
	 }
}
