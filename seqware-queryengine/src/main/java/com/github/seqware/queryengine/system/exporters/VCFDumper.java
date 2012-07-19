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
package com.github.seqware.queryengine.system.exporters;

import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.system.importers.workers.GFF3VariantImportWorker;
import com.github.seqware.queryengine.system.importers.workers.ImportConstants;
import com.github.seqware.queryengine.system.importers.workers.ImportWorker;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This will dump VCF files given a reference that was originally imported from
 * a VCF file.
 *
 * @author dyuen
 */
public class VCFDumper {

    private String[] args;

    public static void main(String[] args) {
        VCFDumper dumper = new VCFDumper(new String[]{"hg_101"});
        dumper.export();
    }

    public void export() {

        if (args.length < 1 || args.length > 1) {
            System.err.println(args.length + " arguments found");
            System.out.println("VCFDumper <referenceID>");
            System.exit(-1);
        }

        String referenceID = args[0];

        // objects to access the mutation datastore
        ModelManager modelManager = Factory.getModelManager();
        SeqWareIterable<Reference> references = Factory.getFeatureStoreInterface().getReferences();
        Reference ref = null;
        for (Reference reference : references) {
            if (reference.getName().equals(referenceID)) {
                ref = reference;
                System.err.println("reference found");
                break;
            }
        }
        // if this referenceID already exists
        if (ref == null) {
            System.out.println("referenceID not found");
            System.exit(-2);
        }
        modelManager.close();

        BufferedWriter outputStream = null;
        try {
            outputStream = new BufferedWriter(new OutputStreamWriter(System.out));
            outputStream.append("#CHROM	POS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n");
            SeqWareIterable<FeatureSet> featureSets = Factory.getFeatureStoreInterface().getFeatureSets();
            for(FeatureSet fSet : featureSets){
                if(fSet.getReferenceID().equals(ref.getSGID())){
                    for(Feature feature : fSet){
                        outputStream.append(feature.getId() + "\t" + (feature.getStart()+1) + "\t");
                        if (feature.getTagByKey(ImportConstants.VCF_SECOND_ID) == null){
                            outputStream.append(".\t");
                        } else{
                            outputStream.append(feature.getTagByKey(ImportConstants.VCF_SECOND_ID).getValue().toString() + "\t");
                        }
                        outputStream.append(feature.getTagByKey(ImportConstants.VCF_REFERENCE_BASE).getValue().toString() + "\t");
                        outputStream.append(feature.getTagByKey(ImportConstants.VCF_CALLED_BASE).getValue().toString() + "\t");
                        outputStream.append(feature.getScore() + "\t");
                        outputStream.append(feature.getTagByKey(ImportConstants.VCF_FILTER).getValue().toString() + "\t");
                        outputStream.append(feature.getTagByKey(ImportConstants.VCF_INFO).getValue().toString() + "\t");
                        outputStream.newLine();
                    }
                }
            }
        } // TODO: clearly this should be expanded to include closing database etc 
        catch (Exception e) {
            Logger.getLogger(VCFDumper.class.getName()).log(Level.SEVERE, "Exception thrown exporting to file: \n", e);
            System.exit(-1);
        } finally{
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(VCFDumper.class.getName()).log(Level.SEVERE, "Exception thrown flushing to file: \n", ex);
            }
        }
    }

    public VCFDumper(String[] args) {
        this.args = args;
    }
}
