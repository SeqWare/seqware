package com.github.seqware.queryengine.impl.test;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.util.FSGID;
import java.util.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Let's test RowKey creation. Features that are added to a FeatureSet that are
 * attached to a proper Reference should have their SGIDs automatically upgraded
 * to FSGIDs that are position aware.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class RowKeyFeatureSetTest {

    private static FeatureSet fSet;

    /**
     * <p>setupTests.</p>
     */
    @BeforeClass
    public static void setupTests() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        String pragma = "##gvf-version 1.06 ##genome-build NCBI B36.3 ##sequence-region chr16 1 88827254";
        fSet = mManager.buildFeatureSet().setDescription(pragma).setReference(mManager.buildReference().setName("funky_ref").build()).build();
        Set<Feature> testFeatures = new HashSet<Feature>();
        TagSet stdTagSet = mManager.buildTagSet().setName("Std_tags").build();
        Tag idSpec = mManager.buildTag().setKey("ID").build();
        Tag variantSpec = mManager.buildTag().setKey("Variant_seq").build();
        Tag referenceSpec = mManager.buildTag().setKey("ReferenceSeq").build();
        stdTagSet.add(idSpec, variantSpec, referenceSpec);
        // have to make up scores, not present in example
        Feature f1 = mManager.buildFeature().setSeqid("chr16").setSource("samtools").setType("SNV").setStart(49291141).setStop(49291141).setStrand(Feature.Strand.POSITIVE).build();
        f1.associateTag(idSpec.toBuilder().setValue("ID_1").build());
        f1.associateTag(variantSpec.toBuilder().setValue("A,G").build());
        f1.associateTag(referenceSpec.toBuilder().setValue("G").build());
        Feature f2 = mManager.buildFeature().setSeqid("chr16").setSource("samtools").setType("SNV").setStart(49291360).setStop(49291360).setStrand(Feature.Strand.POSITIVE).build();
        f2.associateTag(idSpec.toBuilder().setValue("ID_2").build());
        f2.associateTag(variantSpec.toBuilder().setValue("G").build());
        f2.associateTag(referenceSpec.toBuilder().setValue("C").build());
        Feature f3 = mManager.buildFeature().setSeqid("chr16").setSource("samtools").setType("SNV").setStart(49302125).setStop(49302125).setStrand(Feature.Strand.POSITIVE).build();
        f3.associateTag(idSpec.toBuilder().setValue("ID_3").build());
        f3.associateTag(variantSpec.toBuilder().setValue("T,C").build());
        f3.associateTag(referenceSpec.toBuilder().setValue("C").build());

        testFeatures.add(f1);
        testFeatures.add(f2);
        testFeatures.add(f3);
        fSet.add(testFeatures);
        mManager.close();
    }

    /**
     * <p>testRowKeyGeneration.</p>
     */
    @Test
    public void testRowKeyGeneration() {
        String[] expKeys = {"funky_ref" + StorageInterface.SEPARATOR + "chr16:000000049291141", "funky_ref" + StorageInterface.SEPARATOR + "chr16:000000049291360", "funky_ref" + StorageInterface.SEPARATOR + "chr16:000000049302125"};
        List<String> actKeys = new ArrayList<String>();

        // test in memory 
        for (Feature f : fSet) {
            FSGID sgid = (FSGID) f.getSGID();
            actKeys.add(sgid.getRowKey());
        }
        Arrays.sort(expKeys);
        String[] actKeysArr = actKeys.toArray(new String[actKeys.size()]);
        Arrays.sort(actKeysArr);
        Assert.assertArrayEquals(expKeys, actKeysArr);

        // test in back-end
        actKeys.clear();
        FeatureSet targetSet = (FeatureSet) SWQEFactory.getQueryInterface().getAtomBySGID(FeatureSet.class, fSet.getSGID());
        for (Feature f : targetSet) {
            FSGID sgid = (FSGID) f.getSGID();
            actKeys.add(sgid.getRowKey());
        }
        actKeysArr = actKeys.toArray(new String[actKeys.size()]);
        Arrays.sort(actKeysArr);
        Assert.assertArrayEquals(expKeys, actKeysArr);
    }
}
