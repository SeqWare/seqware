/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.store;

import com.sleepycat.db.DatabaseException;

import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.CursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.PostgresTagModelIterator;
import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.StoreInterface;

/**
 * <p>Abstract Store class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public abstract class Store implements StoreInterface {

  SeqWareSettings settings;

  /** {@inheritDoc} */
  public void setup(SeqWareSettings settings) throws Exception {
    setSettings(settings);
  }

  /**
   * <p>close.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public abstract void close() throws Exception;

  /**
   * <p>Getter for the field <code>settings</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
   */
  public SeqWareSettings getSettings() {
    return settings;
  }

  /** {@inheritDoc} */
  public void setSettings(SeqWareSettings settings) {
    this.settings = settings;
  }

  /**
   * <p>startTransaction.</p>
   *
   * @throws com.sleepycat.db.DatabaseException if any.
   */
  public void startTransaction() throws DatabaseException {
    // fill in
  }
  
  /**
   * <p>finishTransaction.</p>
   *
   * @throws com.sleepycat.db.DatabaseException if any.
   */
  public void finishTransaction() throws DatabaseException {
    // fill in
  }
  
  /**
   * <p>isActiveTransaction.</p>
   *
   * @return a boolean.
   */
  public boolean isActiveTransaction() {
    return(false);
  }
  
  /**
   * <p>abortTransaction.</p>
   *
   * @throws com.sleepycat.db.DatabaseException if any.
   */
  public void abortTransaction() throws DatabaseException {
    // fill in
  }
  
  /**
   * <p>getFeaturesUnordered.</p>
   *
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
   */
  public abstract SeqWareIterator getFeaturesUnordered();
  
  /**
   * <p>getFeatures.</p>
   *
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
   */
  public abstract SeqWareIterator getFeatures();
  
  /** {@inheritDoc} */
  public abstract SeqWareIterator getFeatures(String contig, int start, int stop);
  
  /** {@inheritDoc} */
  public abstract Feature getFeature(String featureId) throws Exception;
  
  /** {@inheritDoc} */
  public abstract SeqWareIterator getFeaturesByTag(String tag);
  
  /**
   * <p>getFeaturesTags.</p>
   *
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
   */
  public abstract SeqWareIterator getFeaturesTags();
  
  /**
   * <p>getFeatureTagsBySearch.</p>
   *
   * @param tagSearchStr a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
   */
  public abstract SeqWareIterator getFeatureTagsBySearch(String tagSearchStr);
  
  /** {@inheritDoc} */
  public abstract String putFeature(Feature feature, SeqWareIterator it, boolean transactional);
  
  /** {@inheritDoc} */
  public abstract String putFeature(Feature feature);
  
  /**
   * <p>getMismatchesUnordered.</p>
   *
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
   */
  public abstract SeqWareIterator getMismatchesUnordered();
  
  /**
   * <p>getMismatches.</p>
   *
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
   */
  public abstract SeqWareIterator getMismatches();
  
  /** {@inheritDoc} */
  public abstract SeqWareIterator getMismatches(String contig, int start, int stop);
  
  /** {@inheritDoc} */
  public abstract SeqWareIterator getMismatches(String contig);
  
  /** {@inheritDoc} */
  public abstract Variant getMismatch(String mismatchId) throws Exception;
  
  /** {@inheritDoc} */
  public abstract SeqWareIterator getMismatchesByTag(String tag);
  
  /**
   * <p>getMismatchesTags.</p>
   *
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
   */
  public abstract SeqWareIterator getMismatchesTags();
  
  /**
   * <p>getMismatchTagsBySearch.</p>
   *
   * @param tagSearchStr a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
   */
  public abstract SeqWareIterator getMismatchTagsBySearch(String tagSearchStr);
  
  /** {@inheritDoc} */
  public abstract String putMismatch(Variant variant);
  
  /** {@inheritDoc} */
  public abstract String putMismatch(Variant variant, SeqWareIterator it, boolean transactional);
  
  /** {@inheritDoc} */
  public abstract SeqWareIterator getCoverages(String contig, int start, int stop);
  
  /** {@inheritDoc} */
  public abstract SeqWareIterator getCoverages(String contig);
  
  /** {@inheritDoc} */
  public abstract String putCoverage(Coverage coverage);
  
  /** {@inheritDoc} */
  public abstract String putCoverage(Coverage coverage, boolean transactional);
  
  /** {@inheritDoc} */
  public abstract String putConsequence(Consequence consequence, boolean transactional);
  
  /** {@inheritDoc} */
  public abstract String putConsequence(Consequence consequence);
  
  /** {@inheritDoc} */
  public abstract Consequence getConsequence(String consequenceId) throws Exception;
  
  /** {@inheritDoc} */
  public abstract SeqWareIterator getConsequencesByTag(String tag);
  
  /**
   * <p>getConsequenceTagsBySearch.</p>
   *
   * @param tagSearchStr a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
   */
  public abstract SeqWareIterator getConsequenceTagsBySearch(String tagSearchStr);
  
  /** {@inheritDoc} */
  public abstract SeqWareIterator getConsequencesByMismatch(String mismatchId);
  
  
  
}
