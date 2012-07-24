package com.github.seqware.impl.test;

import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.model.test.FeatureStoreInterfaceTest;
import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.model.Feature;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.seqware.queryengine.model.QueryFuture;
import junit.framework.Assert;
import org.junit.Test;

import static com.github.seqware.queryengine.kernel.RPNStack.*;

/**
 * Tests for the {@link SimplePersistentBackEnd}. This also tests the complex 
 * attribute query.
 *
 * @author jbaran
 */
public class SimplePersistentBackEndTest extends FeatureStoreInterfaceTest {
    
    @Test
    public void storageAndRetrievalTest() {
        UUID testID = UUID.randomUUID();
        System.out.println("running subclass test in testID: " + testID.toString());
        
        // storage type needs to match the default set in the base class of the test otherwise bad things happen
        SimplePersistentBackEnd backend = new SimplePersistentBackEnd(Factory.getStorage());

        try {
            backend.store(aSet);
        }
        catch(Exception e) {
            Logger.getLogger(SimplePersistentBackEndTest.class.getName()).log(Level.SEVERE, "Exception",  e);
            Assert.assertTrue("Backend could not store the given FeatureSet.", false);
        }

        Atom atom = null;
        try {
            atom = backend.getAtomBySGID(FeatureSet.class, aSet.getSGID());
        }
        catch(Exception e) {
            Logger.getLogger(SimplePersistentBackEndTest.class.getName()).log(Level.SEVERE, "Exception",  e);
            Assert.assertTrue("Could not retrieve the previously stored FeatureSet.", false);
        }

        if (!(atom instanceof FeatureSet)){
            Assert.assertTrue("Returned result does not match the previously stored object by type.", false);
        }

        FeatureSet returnedSet = (FeatureSet)atom;

        Assert.assertTrue("Returned feature set does not contain the same amount of features as were stored.", aSet.getCount() == returnedSet.getCount());
        System.out.println("ending subclass test in testID: " + testID.toString());
    }

    @Test
    public void complexQueryTest() {
        SimplePersistentBackEnd backend = new SimplePersistentBackEnd(Factory.getStorage());

        try {
            backend.store(bSet);
        }
        catch(Exception e) {
            Logger.getLogger(SimplePersistentBackEndTest.class.getName()).log(Level.SEVERE, "Exception",  e);
            Assert.assertTrue("Backend could not store the given FeatureSet.", false);
        }

        QueryFuture queryFuture = backend.getFeaturesByAttributes(1, bSet, new RPNStack(
                new Constant("chr16"),
                "id",
                Operation.EQUAL
        ));
        FeatureSet resultSet = queryFuture.get();
        Assert.assertTrue("Setting a query constraints with 1 operation on 'id' failed.", resultSet.getCount() == 10);

        queryFuture = backend.getFeaturesByAttributes(1, bSet, new RPNStack(
                new Constant(Feature.Strand.NEGATIVE),
                "strand",
                Operation.EQUAL
        ));
        resultSet = queryFuture.get();
        Assert.assertTrue("Setting a query constraints with 1 operation on 'strand' failed.", resultSet.getCount() == 3);

        queryFuture = backend.getFeaturesByAttributes(1, bSet, new RPNStack(
                new Constant(Feature.Strand.NEGATIVE),
                "strand",
                Operation.EQUAL,
                new Constant("chr16"),
                "id",
                Operation.EQUAL,
                Operation.AND
        ));
        resultSet = queryFuture.get();
        Assert.assertTrue("Setting a query constraints with 3 operations failed.", resultSet.getCount() == 2);
    }
}
