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

import java.sql.Date;
import junit.framework.Assert;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

/**
 *
 * @author mtaschuk
 */
public class WorkflowRunResourceTest extends DatabaseResourceTest {

    public WorkflowRunResourceTest() {

        super("/workflowruns");
    }
    
    @Override
    public void testPost() {
        
        //This is driving me crazy. I'll fix it later.
        //See https://jira.oicr.on.ca/browse/SEQWARE-480
        
        
        Representation rep = null;
        try {
            Workflow workflow = new Workflow();
            workflow.setBaseIniFile("/home/seqware/provisioned-bundles/Workflow_Bundle_GATKRecalibrationAndVariantCalling_0.10.2_SeqWare_0.10.0/GATKRecalibrationAndVariantCalling/1.x.x/config/GATKRecalibrationAndVariantCallingHg19Tumour_1.3.16.ini");
            workflow.setCommand("java -jar /home/seqware/provisioned-bundles/Workflow_Bundle_GATKRecalibrationAndVariantCalling_0.10.2_SeqWare_0.10.0/GATKRecalibrationAndVariantCalling/1.x.x/lib/seqware-pipeline-0.10.0.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --bundle /home/seqware/provisioned-bundles/Workflow_Bundle_GATKRecalibrationAndVariantCalling_0.10.2_SeqWare_0.10.0 --workflow GATKRecalibrationAndVariantCalling --version 1.3.16-2");
//            workflow.setCreateTimestamp(Date.valueOf("2012-02-05 T19:19:17.978-05:00"));
            workflow.setCwd("/home/seqware/provisioned-bundles/Workflow_Bundle_GATKRecalibrationAndVariantCalling_0.10.2_SeqWare_0.10.0");
            workflow.setDescription("This workflow is designed to take a BAM file, break it down by chromosome, perform realignment, recalibration, duplicate flagging, and variant calling for small indels and SNVs. The result is a VCF file for SNVs and indels both filtered and un-filtered.  This workflow is designed to work with GATK version 1.3.16 which was released on 20111116. This workflow is identical to 1.3.16 but now has additional params to control memory usage. This has lower default memory requirements since it is designed for tumour sequencing. It assumes you have aligned to hg19 which DOES NOT use the rCRS standard tumour sequence. Use this workflow if you will annotate the resulting VCF file with hg19-based annotations.");
            workflow.setName("GATKRecalibrationAndVariantCallingHg19Tumour");
            workflow.setPrivate(false);
            workflow.setPublic(false);
            workflow.setSwAccession(5692);
            workflow.setTemplate("/home/seqware/provisioned-bundles/Workflow_Bundle_GATKRecalibrationAndVariantCalling_0.10.2_SeqWare_0.10.0/GATKRecalibrationAndVariantCalling/1.x.x/workflows/GATKRecalibrationAndVariantCalling_1.3.16.ftl");
//            workflow.setUpdateTimestamp(Date.valueOf("2012-02-05 T19:19:17.978-05:00"));
            workflow.setVersion("1.3.16-2");
            workflow.setWorkflowId(34);
            
            WorkflowRun wr = new WorkflowRun();
            wr.setWorkflow(workflow);
            wr.setName("test test "+ System.currentTimeMillis());
            
            Document doc = XmlTools.marshalToDocument(new JaxbObject<WorkflowRun>(), wr);
            rep = resource.post(XmlTools.getRepresentation(doc));
            rep.exhaust();
            rep.release();
        } catch (Exception e) {
           e.printStackTrace();
           Assert.fail(e.getMessage());
        }
    }   
    
}
