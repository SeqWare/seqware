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
 * @author boconnor
 *
 */
public abstract class Store implements StoreInterface {

  SeqWareSettings settings;

  public void setup(SeqWareSettings settings) throws Exception {
    setSettings(settings);
  }

  public abstract void close() throws Exception;

  public SeqWareSettings getSettings() {
    return settings;
  }

  public void setSettings(SeqWareSettings settings) {
    this.settings = settings;
  }

  public void startTransaction() throws DatabaseException {
    // fill in
  }
  
  public void finishTransaction() throws DatabaseException {
    // fill in
  }
  
  public boolean isActiveTransaction() {
    return(false);
  }
  
  public void abortTransaction() throws DatabaseException {
    // fill in
  }
  
  public abstract SeqWareIterator getFeaturesUnordered();
  
  public abstract SeqWareIterator getFeatures();
  
  public abstract SeqWareIterator getFeatures(String contig, int start, int stop);
  
  public abstract Feature getFeature(String featureId) throws Exception;
  
  public abstract SeqWareIterator getFeaturesByTag(String tag);
  
  public abstract SeqWareIterator getFeaturesTags();
  
  public abstract SeqWareIterator getFeatureTagsBySearch(String tagSearchStr);
  
  public abstract String putFeature(Feature feature, SeqWareIterator it, boolean transactional);
  
  public abstract String putFeature(Feature feature);
  
  public abstract SeqWareIterator getMismatchesUnordered();
  
  public abstract SeqWareIterator getMismatches();
  
  public abstract SeqWareIterator getMismatches(String contig, int start, int stop);
  
  public abstract SeqWareIterator getMismatches(String contig);
  
  public abstract Variant getMismatch(String mismatchId) throws Exception;
  
  public abstract SeqWareIterator getMismatchesByTag(String tag);
  
  public abstract SeqWareIterator getMismatchesTags();
  
  public abstract SeqWareIterator getMismatchTagsBySearch(String tagSearchStr);
  
  public abstract String putMismatch(Variant variant);
  
  public abstract String putMismatch(Variant variant, SeqWareIterator it, boolean transactional);
  
  public abstract SeqWareIterator getCoverages(String contig, int start, int stop);
  
  public abstract SeqWareIterator getCoverages(String contig);
  
  public abstract String putCoverage(Coverage coverage);
  
  public abstract String putCoverage(Coverage coverage, boolean transactional);
  
  public abstract String putConsequence(Consequence consequence, boolean transactional);
  
  public abstract String putConsequence(Consequence consequence);
  
  public abstract Consequence getConsequence(String consequenceId) throws Exception;
  
  public abstract SeqWareIterator getConsequencesByTag(String tag);
  
  public abstract SeqWareIterator getConsequenceTagsBySearch(String tagSearchStr);
  
  public abstract SeqWareIterator getConsequencesByMismatch(String mismatchId);
  
  
  
}
