package com.github.seqware.impl.test;

import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.impl.StorageInterface;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.util.FSGID;
import java.util.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Let's test RowKey creation (not working yet)
 *
 * @author dyuen
 */
public class RowKeyFeatureSetTest {
    
    private static FeatureSet fSet;

    @BeforeClass
    public static void setupTests() {
        ModelManager mManager = Factory.getModelManager();
        String pragma = "##gvf-version 1.06 ##genome-build NCBI B36.3 ##sequence-region chr16 1 88827254";
        fSet = mManager.buildFeatureSet().setDescription(pragma).setReference(mManager.buildReference().setName("funky_ref").build()).build();
        Set<Feature> testFeatures = new HashSet<Feature>();
        // have to make up scores, not present in example
        Feature f1 = mManager.buildFeature().setId("chr16").setSource("samtools").setType("SNV").setStart(49291141).setStop(49291141).setStrand(Feature.Strand.POSITIVE).build();
        f1.associateTag(mManager.buildTag().setKey("ID").setValue("ID_1").build());
        f1.associateTag(mManager.buildTag().setKey("Variant_seq").setValue("A,G").build());
        f1.associateTag(mManager.buildTag().setKey("Reference_seq").setValue("G").build());
        Feature f2 = mManager.buildFeature().setId("chr16").setSource("samtools").setType("SNV").setStart(49291360).setStop(49291360).setStrand(Feature.Strand.POSITIVE).build();
        f2.associateTag(mManager.buildTag().setKey("ID").setValue("ID_2").build());
        f2.associateTag(mManager.buildTag().setKey("Variant_seq").setValue("G").build());
        f2.associateTag(mManager.buildTag().setKey("Reference_seq").setValue("C").build());
        Feature f3 = mManager.buildFeature().setId("chr16").setSource("samtools").setType("SNV").setStart(49302125).setStop(49302125).setStrand(Feature.Strand.POSITIVE).build();
        f3.associateTag(mManager.buildTag().setKey("ID").setValue("ID_3").build());
        f3.associateTag(mManager.buildTag().setKey("Variant_seq").setValue("T,C").build());
        f3.associateTag(mManager.buildTag().setKey("Reference_seq").setValue("C").build());

        testFeatures.add(f1);
        testFeatures.add(f2);
        testFeatures.add(f3);
        fSet.add(testFeatures);
        mManager.close();
    }
    
    @Test
    public void testRowKeyGeneration() {
        String[] expKeys = {"funky_ref"+StorageInterface.separator+"chr16:000000049291141", "funky_ref"+StorageInterface.separator+"chr16:000000049291360" , "funky_ref"+StorageInterface.separator+"chr16:000000049302125"};
        List<String> actKeys = new ArrayList<String>();
     
        // test in memory 
        for(Feature f : fSet){
            FSGID sgid = (FSGID) f.getSGID();
            actKeys.add(sgid.getRowKey());
        }
        Arrays.sort(expKeys);
        String[] actKeysArr = actKeys.toArray(new String[actKeys.size()]);
        Arrays.sort(actKeysArr);
        Assert.assertArrayEquals(expKeys, actKeysArr);
        
        // test in back-end
        actKeys.clear();
        FeatureSet targetSet = (FeatureSet) Factory.getFeatureStoreInterface().getAtomBySGID(FeatureSet.class, fSet.getSGID());
        for(Feature f : targetSet){
            FSGID sgid = (FSGID) f.getSGID();
            actKeys.add(sgid.getRowKey());
        }
        actKeysArr = actKeys.toArray(new String[actKeys.size()]);
        Arrays.sort(actKeysArr);
        Assert.assertArrayEquals(expKeys, actKeysArr);
    }
}
