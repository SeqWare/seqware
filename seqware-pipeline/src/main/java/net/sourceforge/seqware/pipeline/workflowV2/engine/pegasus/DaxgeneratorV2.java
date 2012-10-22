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
package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Adag;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author yongliang
 */
public class DaxgeneratorV2 {
    
	/**
	 * generate a dax file from the object model
	 * @param wfdm
	 * @param output
	 * @return
	 */
    public ReturnValue generateDax(AbstractWorkflowDataModel wfdm, String output) {
    	ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
    	ret = this.createDax(wfdm, output);
    	return ret;
    }
    
    private ReturnValue createDax(AbstractWorkflowDataModel wfdm, String output) {
    	ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
    	File dax = new File(output);
    	// write to dax
    	Document doc = new Document();
    	try {
    	    OutputStream out = new FileOutputStream(dax);
    	    XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
    	    Adag adag = new Adag(wfdm);
    	    doc.setRootElement(adag.serializeXML());
    	    serializer.output(doc, out);
    	    // serializer.output(doc, System.out);
    	    out.flush();
    	    out.close();
    	} catch (IOException e) {
    	    Log.error(e);
    	    ret.setExitStatus(ReturnValue.FAILURE);
    	}
    	return ret;
    }

}
