package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Unit tests of {@link Taggable}.
 *
 * @author dyuen
 */
public class TaggableTest {

    
    @Test
    public void testTaggingOnEverything() {
        // test tagging every possible class that can be tagged
        FeatureSet aSet = Factory.buildFeatureSet(Factory.buildReference("testing dummy reference"));
        Set<Feature> testFeatures = new HashSet<Feature>();
        testFeatures.add(new Feature(aSet, 1000000, 1000100));
        testFeatures.add(new Feature(aSet, 1000200, 1000300));
        testFeatures.add(new Feature(aSet, 1000400, 1000500));
        aSet.add(testFeatures);

        
    }
    
    @Test
    public void testTagAddingAndRemoval(){
        // tags should be added and removed without changing version numbers 
    }
    
    @Test
    public void testTagQueries(){
        // test queries that filter based on all three possibilities for tags 
        // subject only, subject and predicate, or all three
    }
}
