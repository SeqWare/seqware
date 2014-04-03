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
package net.sourceforge.seqware.webservice.resources.queries;

import junit.framework.Assert;
import net.sourceforge.seqware.common.model.lists.ReturnValueList;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.webservice.resources.tables.DatabaseResourceIDTest;
import org.junit.Ignore;

/**
 *
 * @author mtaschuk
 */
public class SequencerRunIdFilesResourceTest extends DatabaseResourceIDTest {

    public SequencerRunIdFilesResourceTest() {
        super("/sequencerruns/4715/files");
        jo = new JaxbObject<ReturnValueList>();
        o = new ReturnValueList();
    }

    @Override
    protected int testObject(Object o) {
        if (o instanceof ReturnValueList) {
            ReturnValueList e = (ReturnValueList) o;
            if (e.getList().isEmpty()) {
                Assert.fail("ReturnValueList is empty!!");
            }
            for (Object ob : e.getList()) {
                if (ob instanceof ReturnValue) {
                    ReturnValue rt = (ReturnValue) ob;
                    System.out.println("Client: " + rt.getAlgorithm());
                    for (FileMetadata f : rt.getFiles()) {
                        System.out.println("Client side: " + f.getFilePath());
                    }
                } else {
                    System.err.println("Object is not an instance of ReturnValue: " + ob.toString());
                }
            }
        } else {
            System.err.println("Object is not an instance of ReturnValueList");
            return ReturnValue.FILENOTREADABLE;
        }
        return ReturnValue.SUCCESS;
    }

    @Ignore
    @Override
    public void testDelete() {
//        super.testDelete();
    }

    @Ignore
    @Override
    public void testPost() {
//        super.testPost();
    }

    @Ignore
    @Override
    public void testPut() {
//        super.testPut();
    }
}
