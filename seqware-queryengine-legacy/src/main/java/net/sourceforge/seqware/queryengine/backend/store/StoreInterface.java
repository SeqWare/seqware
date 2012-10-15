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
 * <p>StoreInterface interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface StoreInterface {
    
    // UTILITY METHODS
    /**
     * <p>setup.</p>
     *
     * @param settings a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
     * @throws java.lang.Exception if any.
     */
    public void setup(SeqWareSettings settings) throws Exception;
    /**
     * <p>close.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void close() throws Exception;
    /**
     * <p>getSettings.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
     */
    public SeqWareSettings getSettings();
    /**
     * <p>setSettings.</p>
     *
     * @param settings a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
     */
    public void setSettings(SeqWareSettings settings);
    // FIXME: this is a BerkeleyDB exception type!
    /**
     * <p>startTransaction.</p>
     *
     * @throws com.sleepycat.db.DatabaseException if any.
     */
    public void startTransaction() throws DatabaseException;
    // FIXME: this is a BerkeleyDB exception type!
    /**
     * <p>finishTransaction.</p>
     *
     * @throws com.sleepycat.db.DatabaseException if any.
     */
    public void finishTransaction() throws DatabaseException;
    /**
     * <p>isActiveTransaction.</p>
     *
     * @return a boolean.
     */
    public boolean isActiveTransaction();
    // FIXME: this is a BerkeleyDB exception type!
    /**
     * <p>abortTransaction.</p>
     *
     * @throws com.sleepycat.db.DatabaseException if any.
     */
    public void abortTransaction() throws DatabaseException;
    
    // FEATURE METHODS
    /**
     * <p>getFeaturesUnordered.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getFeaturesUnordered();
    /**
     * <p>getFeatures.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getFeatures();
    /**
     * <p>getFeatures.</p>
     *
     * @param contig a {@link java.lang.String} object.
     * @param start a int.
     * @param stop a int.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getFeatures(String contig, int start, int stop);
    /**
     * <p>getFeature.</p>
     *
     * @param featureId a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.model.Feature} object.
     * @throws java.lang.Exception if any.
     */
    public Feature getFeature(String featureId) throws Exception;
    /**
     * <p>getFeaturesByTag.</p>
     *
     * @param tag a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getFeaturesByTag(String tag);
    /**
     * <p>getFeaturesTags.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getFeaturesTags();
    /**
     * <p>putFeature.</p>
     *
     * @param feature a {@link net.sourceforge.seqware.queryengine.backend.model.Feature} object.
     * @param it a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     * @param transactional a boolean.
     * @return a {@link java.lang.String} object.
     */
    public String putFeature(Feature feature, SeqWareIterator it, boolean transactional);
    /**
     * <p>putFeature.</p>
     *
     * @param feature a {@link net.sourceforge.seqware.queryengine.backend.model.Feature} object.
     * @return a {@link java.lang.String} object.
     */
    public String putFeature(Feature feature);
    
    // VARIANT METHODS
    /**
     * <p>getMismatchesUnordered.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getMismatchesUnordered();
    /**
     * <p>getMismatches.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getMismatches();
    /**
     * <p>getMismatches.</p>
     *
     * @param contig a {@link java.lang.String} object.
     * @param start a int.
     * @param stop a int.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getMismatches(String contig, int start, int stop);
    /**
     * <p>getMismatches.</p>
     *
     * @param contig a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getMismatches(String contig);
    /**
     * <p>getMismatch.</p>
     *
     * @param mismatchId a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.model.Variant} object.
     * @throws java.lang.Exception if any.
     */
    public Variant getMismatch(String mismatchId) throws Exception;
    /**
     * <p>getMismatchesByTag.</p>
     *
     * @param tag a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getMismatchesByTag(String tag);
    /**
     * <p>getMismatchesTags.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getMismatchesTags();
    /**
     * <p>putMismatch.</p>
     *
     * @param variant a {@link net.sourceforge.seqware.queryengine.backend.model.Variant} object.
     * @return a {@link java.lang.String} object.
     */
    public String putMismatch(Variant variant);
    /**
     * <p>putMismatch.</p>
     *
     * @param variant a {@link net.sourceforge.seqware.queryengine.backend.model.Variant} object.
     * @param it a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     * @param transactional a boolean.
     * @return a {@link java.lang.String} object.
     */
    public String putMismatch(Variant variant, SeqWareIterator it, boolean transactional);
    
    // COVERAGES METHODS
    /**
     * <p>getCoverages.</p>
     *
     * @param contig a {@link java.lang.String} object.
     * @param start a int.
     * @param stop a int.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getCoverages(String contig, int start, int stop);
    /**
     * <p>getCoverages.</p>
     *
     * @param contig a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getCoverages(String contig);
    /**
     * <p>putCoverage.</p>
     *
     * @param coverage a {@link net.sourceforge.seqware.queryengine.backend.model.Coverage} object.
     * @return a {@link java.lang.String} object.
     */
    public String putCoverage(Coverage coverage);
    /**
     * <p>putCoverage.</p>
     *
     * @param coverage a {@link net.sourceforge.seqware.queryengine.backend.model.Coverage} object.
     * @param transactional a boolean.
     * @return a {@link java.lang.String} object.
     */
    public String putCoverage(Coverage coverage, boolean transactional);
    
    // CONSEQUENCE METHODS
    /**
     * <p>putConsequence.</p>
     *
     * @param consequence a {@link net.sourceforge.seqware.queryengine.backend.model.Consequence} object.
     * @param transactional a boolean.
     * @return a {@link java.lang.String} object.
     */
    public String putConsequence(Consequence consequence, boolean transactional);
    /**
     * <p>putConsequence.</p>
     *
     * @param consequence a {@link net.sourceforge.seqware.queryengine.backend.model.Consequence} object.
     * @return a {@link java.lang.String} object.
     */
    public String putConsequence(Consequence consequence);
    /**
     * <p>getConsequence.</p>
     *
     * @param consequenceId a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.model.Consequence} object.
     * @throws java.lang.Exception if any.
     */
    public Consequence getConsequence(String consequenceId) throws Exception;
    /**
     * <p>getConsequencesByTag.</p>
     *
     * @param tag a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getConsequencesByTag(String tag);
    /**
     * <p>getConsequencesByMismatch.</p>
     *
     * @param mismatchId a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getConsequencesByMismatch(String mismatchId);
    
}
