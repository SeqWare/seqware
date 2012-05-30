/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.store;

import com.sleepycat.db.DatabaseException;

import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.CursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.SecondaryCursorIterator;

/**
 * @author boconnor
 *
 */
public interface StoreInterface {
    
    // UTILITY METHODS
    public void setup(SeqWareSettings settings) throws Exception;
    public void close() throws Exception;
    public SeqWareSettings getSettings();
    public void setSettings(SeqWareSettings settings);
    // FIXME: this is a BerkeleyDB exception type!
    public void startTransaction() throws DatabaseException;
    // FIXME: this is a BerkeleyDB exception type!
    public void finishTransaction() throws DatabaseException;
    public boolean isActiveTransaction();
    // FIXME: this is a BerkeleyDB exception type!
    public void abortTransaction() throws DatabaseException;
    
    // FEATURE METHODS
    public SeqWareIterator getFeaturesUnordered();
    public SeqWareIterator getFeatures();
    public SeqWareIterator getFeatures(String contig, int start, int stop);
    public Feature getFeature(String featureId) throws Exception;
    public SeqWareIterator getFeaturesByTag(String tag);
    public SeqWareIterator getFeaturesTags();
    public String putFeature(Feature feature, SeqWareIterator it, boolean transactional);
    public String putFeature(Feature feature);
    
    // VARIANT METHODS
    public SeqWareIterator getMismatchesUnordered();
    public SeqWareIterator getMismatches();
    public SeqWareIterator getMismatches(String contig, int start, int stop);
    public SeqWareIterator getMismatches(String contig);
    public Variant getMismatch(String mismatchId) throws Exception;
    public SeqWareIterator getMismatchesByTag(String tag);
    public SeqWareIterator getMismatchesTags();
    public String putMismatch(Variant variant);
    public String putMismatch(Variant variant, SeqWareIterator it, boolean transactional);
    
    // COVERAGES METHODS
    public SeqWareIterator getCoverages(String contig, int start, int stop);
    public SeqWareIterator getCoverages(String contig);
    public String putCoverage(Coverage coverage);
    public String putCoverage(Coverage coverage, boolean transactional);
    
    // CONSEQUENCE METHODS
    public String putConsequence(Consequence consequence, boolean transactional);
    public String putConsequence(Consequence consequence);
    public Consequence getConsequence(String consequenceId) throws Exception;
    public SeqWareIterator getConsequencesByTag(String tag);
    public SeqWareIterator getConsequencesByMismatch(String mismatchId);
    
}
