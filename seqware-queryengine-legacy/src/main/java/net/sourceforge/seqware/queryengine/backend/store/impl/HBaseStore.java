/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.store.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.ConsequenceTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.CoverageTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.FeatureTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.StringIdTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.TagTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.VariantTB;
import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.model.LocatableModel;
import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.HBaseModelIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.HBaseTagModelIterator;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;

/**
 * <p>HBaseStore class.</p>
 *
 * @author boconnor This is the backend implementation that uses HBase to store.
 *
 *         FIXME: if I'm going to use Genome<genomeId>TagIndexTable to store all
 *         sorts of tagged indexed items a given row may really point to family
 *         values of feature, consequence, variant, etc. There's no way of
 *         telling what family it should point to currently, should probably
 *         include this as a "family" field to make this clear.
 *
 *         FIXME: there will be issues that become apparent when trying to store
 *         multiple genomes in the same DB
 *
 *         FIXME: will need to alter all put methods to support passing in full
 *         ID with timestamp in order to do updates
 *
 *         FIXME: need to add methods to support variants, features, reference,
 *         etc
 *
 *         FIXME: going to have to deal with tags like nonsynachr:1243
 *         nonsynchr:1243 where does nonsyn then sort? I think before either of
 *         these which could lead to problems detecting the matches in the scan
 *
 *         FIXME: I will have to deal with tag table updates, currently I
 *         overwrite old data
 *
 *         FIXME: there may be queries below that return an unbounded iterator,
 *         see getModel below for how to fix this using a filter list
 *
 *         FIXME: turning autoflush on gives a 2x improvement in load speed
 *         however any locations where two variants occupy the same location are
 *         problematic because the timestamp won't be incremented properly. So
 *         the second variant will overwrite the first! This object could track
 *         this so that it could increment the timestamp manually if it loads
 *         the same position. This is complicated by the need for multithread
 *         support
 *
 *         TODO: To speed up the inserts in a non critical job (like an import
 *         job), you can use Put.writeToWAL(false) to bypass writing to the
 *         write ahead log.
 *
 *         Notes about ID: since this backend has no unique key generator I have
 *         to create unique IDs manually I've tried to do this using a
 *         consistent syntax. It's important that everything use this common
 *         syntax below otherwise info won't be retrievable.
 *
 **         java -cp $CLASSPATH:dist/seqware-qe-0.4.0.jar:lib/db.jar
 *         net.sourceforge.seqware.queryengine.prototypes.hadoop.HBaseWrite <chr
 *         [chr22]> <start_position [1234]> <count [1]> java -cp
 *         $CLASSPATH:dist/seqware-qe-0.4.0.jar:lib/db.jar
 *         net.sourceforge.seqware.queryengine.prototypes.hadoop.HBaseRead
 *         <id_from_above>
 * @version $Id: $Id
 */
public class HBaseStore extends Store {

	/*
	 * for testing run this as: java -cp $CLASSPATH:dist/seqware-qe-0.4.0.jar
	 * net.sourceforge.seqware.queryengine.prototypes.hadoop.HBaseWrite
	 */

	// timestamp
	// FIXME: pass this in or increment in the DB
	protected long commonTimestamp = new Date().getTime();

	// constants
	// FIXME: these should be settings somewhere
	/** Constant <code>PAD=15</code> */
	public static final int PAD = 15;
	/** Constant <code>MAXVERSIONS=1000</code> */
	public static final int MAXVERSIONS = 1000;
	/** Constant <code>AUTOFLUSH=false</code> */
	public static final boolean AUTOFLUSH = false;

	// settings object
	SeqWareSettings settings = null;

	// LEFT OFF WITH: need to try test connection to hbase
	// http://hadoop.apache.org/hbase/docs/r0.20.2/api/org/apache/hadoop/hbase/client/package-summary.html#package_description
	// this config is pulling information from any config files found in
	// classpath
	protected HBaseConfiguration config = new HBaseConfiguration();
	protected String genomeId = null;
	protected String referenceId = null;

	// table objects
	protected HTable genomeTable = null;
	protected HTable tagIndexTable = null;
	protected HTable currGenomeTagIndexTable = null;

	// tuple binders
	FeatureTB ftb = new FeatureTB();
	VariantTB mtb = new VariantTB();
	ConsequenceTB ctb = new ConsequenceTB();
	CoverageTB covtb = new CoverageTB();
	TagTB ttb = new TagTB();
	StringIdTB midtb = new StringIdTB();

	// SETUP AND UTILITY METHODS

	// FIXME: may want to have an option for autoflush
	/** {@inheritDoc} */
	public void setup(SeqWareSettings settings) throws FileNotFoundException,
			DatabaseException, Exception {

		this.genomeId = settings.getGenomeId();
		this.referenceId = settings.getReferenceId();

		// System.out.println("CREATE? "+settings.isCreateMismatchDB());

		// You need a configuration object to tell the client where to connect.
		// When you create a HBaseConfiguration, it reads in whatever you've set
		// into your hbase-site.xml and in hbase-default.xml, as long as these
		// can
		// be found on the CLASSPATH
		config = new HBaseConfiguration();
		// config.set(HConstants.REGION_SERVER_IMPL,
		// IndexedRegionServer.class.getName());
		// config.set(HConstants.REGION_SERVER_CLASS,
		// IndexedRegionInterface.class.getName());
		HBaseAdmin admin = new HBaseAdmin(config);

		// the genome tag index table
		if (!admin.tableExists("Genome" + genomeId + "TagIndexTable")
				|| settings.isCreateMismatchDB()) {
			// if (true) {
			HTableDescriptor table = new HTableDescriptor("Genome" + genomeId
					+ "TagIndexTable");
			// modified when moving to hbase-0.90.6, looks like colons are no
			// longer allowed in family names SEQWARE-607
			for (String name : new String[] { "rowId", "modelId", "key",
					"value" }) {
				table.addFamily(new HColumnDescriptor(Bytes.toBytes(name),
						HBaseStore.MAXVERSIONS,
						HColumnDescriptor.DEFAULT_COMPRESSION,
						HColumnDescriptor.DEFAULT_IN_MEMORY,
						HColumnDescriptor.DEFAULT_BLOCKCACHE,
						HConstants.FOREVER,
						HColumnDescriptor.DEFAULT_BLOOMFILTER));
			}
			admin.createTable(table);
			admin.flush("Genome" + genomeId + "TagIndexTable");
			// System.err.println("HERE3");
		}
		currGenomeTagIndexTable = new HTable(config, "Genome" + genomeId
				+ "TagIndexTable");
		// currGenomeTagIndexTable.setAutoFlush(false);
		currGenomeTagIndexTable.setAutoFlush(AUTOFLUSH);
		currGenomeTagIndexTable.setWriteBufferSize(1024 * 1024 * 12);
		// System.err.println("HERE4");

		// the base genome table
		if (!admin.tableExists(referenceId + "Table")
				|| settings.isCreateMismatchDB()) {
			HTableDescriptor table = new HTableDescriptor(referenceId + "Table");
			// modified when moving to hbase-0.90.6, looks like colons are no
			// longer allowed in family names SEQWARE-607
			for (String name : new String[] { "feature", "variant", "tag",
					"reference", "coverage", "consequence" }) {
				table.addFamily(new HColumnDescriptor(Bytes.toBytes(name),
						HBaseStore.MAXVERSIONS,
						HColumnDescriptor.DEFAULT_COMPRESSION,
						HColumnDescriptor.DEFAULT_IN_MEMORY,
						HColumnDescriptor.DEFAULT_BLOCKCACHE,
						HConstants.FOREVER,
						HColumnDescriptor.DEFAULT_BLOOMFILTER));
			}
			admin.createTable(table);
			admin.flush(referenceId + "Table");
		}
		genomeTable = new HTable(config, referenceId + "Table");
		// genomeTable.setAutoFlush(false);
		genomeTable.setAutoFlush(AUTOFLUSH);
		genomeTable.setWriteBufferSize(1024 * 1014 * 12);
		// System.err.println("HERE1");

		// the tag index table
		if (!admin.tableExists(referenceId + "TagIndexTable")
				|| settings.isCreateMismatchDB()) {
			HTableDescriptor table = new HTableDescriptor(referenceId
					+ "TagIndexTable");
			// modified when moving to hbase-0.90.6, looks like colons are no
			// longer allowed in family names SEQWARE-607
			for (String name : new String[] { "rowId", "modelId", "key",
					"value" }) {
				table.addFamily(new HColumnDescriptor(Bytes.toBytes(name),
						HBaseStore.MAXVERSIONS,
						HColumnDescriptor.DEFAULT_COMPRESSION,
						HColumnDescriptor.DEFAULT_IN_MEMORY,
						HColumnDescriptor.DEFAULT_BLOCKCACHE,
						HConstants.FOREVER,
						HColumnDescriptor.DEFAULT_BLOOMFILTER));
			}
			admin.createTable(table);
			admin.flush(referenceId + "TagIndexTable");
		}
		// tagIndexTable = new HTable(config, referenceId+"TagIndexTable");
		tagIndexTable = new HTable(config, "Genome" + genomeId
				+ "TagIndexTable");
		// tagIndexTable.setAutoFlush(false);
		tagIndexTable.setAutoFlush(AUTOFLUSH);
		tagIndexTable.setWriteBufferSize(1024 * 1024 * 12);

		// System.err.println("HERE2");

	}

	/**
	 * <p>close.</p>
	 *
	 * @throws java.io.IOException if any.
	 */
	public void close() throws IOException {
		genomeTable.flushCommits();
		genomeTable.close();
		tagIndexTable.flushCommits();
		tagIndexTable.close();
		currGenomeTagIndexTable.flushCommits();
		currGenomeTagIndexTable.close();
	}

	/**
	 * <p>Getter for the field <code>settings</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
	 */
	public SeqWareSettings getSettings() {
		return (settings);
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
		// ignore
		// throw new
		// DatabaseException("HBaseStore doesn't support transactions currently.");
	}

	/**
	 * <p>finishTransaction.</p>
	 *
	 * @throws com.sleepycat.db.DatabaseException if any.
	 */
	public void finishTransaction() throws DatabaseException {
		// ignore
		// throw new
		// DatabaseException("HBaseStore doesn't support transactions currently.");
	}

	/**
	 * <p>isActiveTransaction.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isActiveTransaction() {
		// HBaseStore doesn't support transactions currently
		return (false);
	}

	/**
	 * <p>abortTransaction.</p>
	 *
	 * @throws com.sleepycat.db.DatabaseException if any.
	 */
	public void abortTransaction() throws DatabaseException {
		// ignore
		// throw new
		// DatabaseException("HBaseStore doesn't support transactions currently.");
	}

	/**
	 * <p>padZeros.</p>
	 *
	 * @param input a int.
	 * @param totalPlaces a int.
	 * @return a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public static String padZeros(int input, int totalPlaces) throws Exception {
		String strInput = new Integer(input).toString();
		if (strInput.length() > totalPlaces) {
			throw new Exception("Integer " + input
					+ " is larger than total places of " + totalPlaces
					+ " so padding this string failed.");
		}
		int diff = totalPlaces - strInput.length();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < diff; i++) {
			buffer.append("0");
		}
		buffer.append(strInput);
		return (buffer.toString());
	}

	private synchronized long getTimestamp() {
		commonTimestamp++;
		return (commonTimestamp);
	}

	// FEATURE METHODS

	/**
	 * <p>getFeaturesUnordered.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
	 */
	public SeqWareIterator getFeaturesUnordered() {

		return (getFeatures());

	}

	/**
	 * <p>getFeatures.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
	 */
	public SeqWareIterator getFeatures() {

		return (getModels(genomeTable, null, ftb, "feature", "Genome"
				+ genomeId, true));

	}

	/**
	 * {@inheritDoc}
	 *
	 * This doesn't do bounds checking on the stop, the client will need to
	 * close the iterator when it reaches the requested end!
	 */
	public SeqWareIterator getFeatures(String contig, int start, int stop) {

		SeqWareIterator iterator = null;

		try {
			String id = contig + ":" + padZeros(start, HBaseStore.PAD);
			RowFilter filter = new RowFilter(CompareOp.GREATER_OR_EQUAL,
					new BinaryComparator(Bytes.toBytes(id)));
			iterator = getModels(genomeTable, filter, ftb, "feature", "Genome"
					+ genomeId, true);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			iterator = null;
		}
		return (iterator);

	}

	/**
	 * {@inheritDoc}
	 *
	 * The featureID should be of the form:
	 * "<ref_genome>.<ref_genome_location>.feature.<genome_ID>.v<version_aka_timestamp>"
	 * for example "hg18.chr12:00230.feature.genome1212.v1"
	 */
	public Feature getFeature(String featureId) throws Exception {

		String[] idArr = featureId.split("\\.");
		String versionStr = idArr[idArr.length - 1];
		long version = Long.parseLong(versionStr.substring(1));
		return ((Feature) getModel(idArr[1], ftb, genomeTable, "feature",
				"Genome" + genomeId, version));

	}

	/** {@inheritDoc} */
	public SeqWareIterator getFeaturesByTag(String tag) {

		return (getModelsByTag(tag, tagIndexTable, genomeTable, ftb, "feature",
				"Genome" + genomeId));

	}

	// TODO
	/**
	 * <p>getFeaturesTags.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
	 */
	public SeqWareIterator getFeaturesTags() {

		return (null);

	}

	/** {@inheritDoc} */
	public SeqWareIterator getFeatureTagsBySearch(String tagSearchStr) {
		return (null);
	}

	/**
	 * {@inheritDoc}
	 *
	 * This is really a pass through for putFeature(Feature feature) since the
	 * HBase backend doesn't support transactions.
	 */
	public String putFeature(Feature feature, SeqWareIterator it,
			boolean transactional) {

		return (putFeature(feature));

	}

	/**
	 * {@inheritDoc}
	 *
	 * This constructs an identifier such as:
	 * "hg18.chr12:00230.feature.genome1212", figures out the version to add
	 * e.g. "hg18.chr12:00230.feature.genome1212.v1", saves this as featureId,
	 * and save the whole thing to the DB.
	 */
	public String putFeature(Feature feature) {

		try {
			String idNoVersion = referenceId + "." + feature.getContig() + ":"
					+ padZeros(feature.getStartPosition(), HBaseStore.PAD)
					+ ".feature.Genome" + genomeId;
			return (putModel(idNoVersion, feature, ftb, genomeTable,
					tagIndexTable, currGenomeTagIndexTable, "feature", "Genome"
							+ genomeId));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return (null);

	}

	// VARIANT METHODS

	/**
	 * <p>getMismatchesUnordered.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
	 */
	public SeqWareIterator getMismatchesUnordered() {

		return (getMismatches());

	}

	/**
	 * <p>getMismatches.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
	 */
	public SeqWareIterator getMismatches() {

		return (getModels(genomeTable, null, mtb, "variant", "Genome"
				+ genomeId, true));

	}

	/**
	 * {@inheritDoc}
	 *
	 * This doesn't do bounds checking on the stop, the client will need to
	 * close the iterator when it reaches the requested end!
	 */
	public SeqWareIterator getMismatches(String contig, int start, int stop) {

		SeqWareIterator iterator = null;

		try {
			String startId = contig + ":" + padZeros(start, HBaseStore.PAD);
			String stopId = contig + ":" + padZeros(stop, HBaseStore.PAD);
			RowFilter startFilter = new RowFilter(CompareOp.GREATER_OR_EQUAL,
					new BinaryComparator(Bytes.toBytes(startId)));
			RowFilter stopFilter = new RowFilter(CompareOp.LESS_OR_EQUAL,
					new BinaryComparator(Bytes.toBytes(stopId)));
			FilterList filter = new FilterList(
					FilterList.Operator.MUST_PASS_ALL);
			filter.addFilter(startFilter);
			filter.addFilter(stopFilter);
			iterator = getModels(genomeTable, filter, mtb, "variant", "Genome"
					+ genomeId, true);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			iterator = null;
		}
		return (iterator);

	}

	/** {@inheritDoc} */
	public SeqWareIterator getMismatches(String contig) {

		return (getMismatches(contig, 0, Integer.MAX_VALUE));

	}

	/**
	 * {@inheritDoc}
	 *
	 * The mismatchID should be of the form:
	 * "<ref_genome>.<ref_genome_location>.variant.<genome_ID>.<consensus_base>.v<version_aka_timestamp>"
	 * for example "hg18.chr12:00230.variant.genome1212.G.v1"
	 */
	public Variant getMismatch(String mismatchId) throws Exception {

		String[] idArr = mismatchId.split("\\.");
		Pattern versionPat = Pattern.compile("^.*\\.v(\\d+)$");
		Matcher versionMat = versionPat.matcher(mismatchId);
		if (versionMat.find()) {
			String versionStr = idArr[idArr.length - 1];
			long version = Long.parseLong(versionStr.substring(1));
			return ((Variant) getModel(idArr[1] + ":" + idArr[2], mtb,
					genomeTable, "variant", "Genome" + genomeId, version));
		}
		// it may be that the id doesn't end in a version, in this case want to
		// use the most recent one
		// FIXME: this is really dangerous for bi and triallelic
		return ((Variant) getLatestModel(idArr[1] + ":" + idArr[2], mtb,
				genomeTable, "variant", "Genome" + genomeId));

	}

	/** {@inheritDoc} */
	public SeqWareIterator getMismatchesByTag(String tag) {

		return (getModelsByTag(tag, tagIndexTable, genomeTable, mtb, "variant",
				"Genome" + genomeId));

	}

	// TODO
	/**
	 * <p>getMismatchesTags.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
	 */
	public SeqWareIterator getMismatchesTags() {

		return (null);

	}

	/** {@inheritDoc} */
	public SeqWareIterator getMismatchTagsBySearch(String tagSearchStr) {
		return (null);
	}

	/**
	 * {@inheritDoc}
	 *
	 * This constructs an identifier such as:
	 * "hg18.chr12:00230.variant.genome1212.G", figures out the version to add
	 * e.g. "hg18.chr12:00230.variant.genome1212.G.v1", saves this as id, and
	 * save the whole thing to the DB.
	 */
	public String putMismatch(Variant variant) {

		try {
			// FIXME: figuring out if this is an update vs.
			String id = referenceId + "." + variant.getContig() + "."
					+ padZeros(variant.getStartPosition(), HBaseStore.PAD)
					+ ".variant.Genome" + genomeId + "."
					+ variant.getCalledBase();
			if (variant.getId() != null && !"".equals(variant.getId())) {
				id = variant.getId();
			}
			return (putModel(id, variant, mtb, genomeTable, tagIndexTable,
					currGenomeTagIndexTable, "variant", "Genome" + genomeId));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return (null);

	}

	/**
	 * {@inheritDoc}
	 *
	 * This is really a pass through for putMismatch(Variant variant) since the
	 * HBase backend doesn't support transactions.
	 */
	public String putMismatch(Variant variant, SeqWareIterator it,
			boolean transactional) {

		return (putMismatch(variant));

	}

	// COVERAGE METHODS

	/** {@inheritDoc} */
	public SeqWareIterator getCoverages(String contig, int start, int stop) {

		SeqWareIterator iterator = null;

		try {
			String startId = contig + ":" + padZeros(start, HBaseStore.PAD);
			String stopId = contig + ":" + padZeros(stop, HBaseStore.PAD);
			RowFilter startFilter = new RowFilter(CompareOp.GREATER_OR_EQUAL,
					new BinaryComparator(Bytes.toBytes(startId)));
			RowFilter stopFilter = new RowFilter(CompareOp.LESS_OR_EQUAL,
					new BinaryComparator(Bytes.toBytes(stopId)));
			// QualifierFilter qualFilter = new QualifierFilter(CompareOp.EQUAL,
			// new BinaryComparator(Bytes.toBytes("Genome"+genomeId)));
			FilterList filter = new FilterList(
					FilterList.Operator.MUST_PASS_ALL);
			filter.addFilter(startFilter);
			filter.addFilter(stopFilter);
			// filter.addFilter(qualFilter);
			Scan scan = new Scan();
			scan.addColumn(Bytes.toBytes("coverage"),
					Bytes.toBytes("Genome" + genomeId));
			iterator = getModels(genomeTable, scan, filter, covtb, "coverage",
					"Genome" + genomeId, false);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			iterator = null;
		}
		return (iterator);
	}

	/** {@inheritDoc} */
	public SeqWareIterator getCoverages(String contig) {

		return (getCoverages(contig, 0, Integer.MAX_VALUE));

	}

	/**
	 * {@inheritDoc}
	 *
	 * This constructs an identifier such as:
	 * "hg18.chr12:00230.coverage.genome1212", figures out the version to add
	 * e.g. "hg18.chr12:00230.coverage.genome1212.v1", saves this as id, and
	 * save the whole thing to the DB.
	 */
	public String putCoverage(Coverage coverage) {

		try {
			String idNoVersion = referenceId + "." + coverage.getContig() + "."
					+ padZeros(coverage.getStartPosition(), HBaseStore.PAD)
					+ ".coverage.Genome" + genomeId;
			return (putModel(idNoVersion, coverage, covtb, genomeTable,
					tagIndexTable, currGenomeTagIndexTable, "coverage",
					"Genome" + genomeId));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return (null);

	}

	/** {@inheritDoc} */
	public String putCoverage(Coverage coverage, boolean transactional) {

		return (putCoverage(coverage));

	}

	// CONSEQUENCE METHODS
	/**
	 * {@inheritDoc}
	 *
	 * TODO: I think I should store each consequence as
	 * "Genome"+genomeId+"Gene"+consequence.getGeneId()
	 */
	public String putConsequence(Consequence consequence, boolean transactional) {

		return (putConsequence(consequence));

	}

	/**
	 * {@inheritDoc}
	 *
	 * This constructs an identifier such as:
	 * "hg18.chr12:00230.consequence.genome1212.G.ak21219", figures out the
	 * version to add e.g.
	 * "hg18.chr12:00230.consequence.genome1212.G.ak21219.v1", saves this as
	 * consequenceId, and save the whole thing to the DB.
	 *
	 * FIXME: The consensusBaseCall needs to be added directly to the
	 * consequence object to avoid parsing it from the variant ID string which
	 * is error prone!
	 */
	public String putConsequence(Consequence consequence) {

		try {
			String geneName = consequence.getGeneId();
			geneName.replaceAll("\\.", "_");
			String[] variantIdArr = consequence.getMismatchId().split("\\.");
			String consensusBase = variantIdArr[variantIdArr.length - 2];
			String idNoVersion = referenceId + "." + consequence.getContig()
					+ "."
					+ padZeros(consequence.getStartPosition(), HBaseStore.PAD)
					+ ".consequence.Genome" + genomeId + "." + consensusBase
					+ "." + geneName;
			return (putModel(idNoVersion, consequence, ctb, genomeTable,
					tagIndexTable, currGenomeTagIndexTable, "consequence",
					"Genome" + genomeId));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return (null);

	}

	/**
	 * {@inheritDoc}
	 *
	 * The consequenceID should be of the form:
	 * "<ref_genome>.<ref_genome_location>.consequence.<genome_ID>.<variant_consensus_base>.<gene_model_name>.v<version_aka_timestamp>"
	 * for example "hg18.chr12:00230.consequence.genome1212.G.ak21219.v1"
	 */
	public Consequence getConsequence(String consequenceId) throws Exception {

		String[] idArr = consequenceId.split("\\.");
		String versionStr = idArr[idArr.length - 1];
		long version = Long.parseLong(versionStr.substring(1));

		return ((Consequence) getModel(idArr[1], ctb, genomeTable,
				"consequence", "Genome" + genomeId, version));

	}

	/** {@inheritDoc} */
	public SeqWareIterator getConsequencesByTag(String tag) {

		return (getModelsByTag(tag, tagIndexTable, genomeTable, ctb,
				"consequence", "Genome" + genomeId));

	}

	/** {@inheritDoc} */
	public SeqWareIterator getConsequenceTagsBySearch(String tagSearchStr) {
		return (null);
	}

	// TODO
	/**
	 * {@inheritDoc}
	 *
	 * Since both variants and consequences are stored under the same ID based
	 * on location this really doesn't make a lot of sense. This is only used by
	 * MismatchConsequenceReportProcessor and ConsequenceResource, can this be
	 * removed?
	 */
	public SeqWareIterator getConsequencesByMismatch(String mismatchId) {

		return (null);

	}

	// GENERIC METHODS
	// the generic methods that everything uses

	/**
	 * <p>getModels.</p>
	 *
	 * @param table a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param filter a {@link org.apache.hadoop.hbase.filter.Filter} object.
	 * @param binder a {@link com.sleepycat.bind.tuple.TupleBinding} object.
	 * @param family a {@link java.lang.String} object.
	 * @param label a {@link java.lang.String} object.
	 * @param returnAllVersions a boolean.
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
	 */
	protected SeqWareIterator getModels(HTable table, Filter filter,
			TupleBinding binder, String family, String label,
			boolean returnAllVersions) {

		HBaseModelIterator iterator = null;

		// System.out.println("Looking at table: "+Bytes.toString(table.getTableName()));
		// System.out.println("Family: "+family+" Label: "+label);

		try {

			// RowFilter filter = new RowFilter(CompareOp.GREATER_OR_EQUAL, new
			// BinaryComparator(Bytes.toBytes(tag)));
			// SingleColumnValueFilter filter = new
			// SingleColumnValueFilter(Bytes.toBytes("tagFamily"), null,
			// CompareOp.EQUAL, Bytes.toBytes("stop-codon-loss"));
			Scan scan = new Scan();
			scan.setMaxVersions();
			if (filter != null) {
				scan.setFilter(filter);
			}
			// System.out.println("Trying to get iterator for family: "+family+" and label: "+label+" table: "+Bytes.toString(table.getTableName()));
			iterator = new HBaseModelIterator(table.getScanner(scan), table,
					binder, family, label, returnAllVersions);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			iterator = null;
		}

		return (iterator);

	}

	/**
	 * <p>getModels.</p>
	 *
	 * @param table a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param scan a {@link org.apache.hadoop.hbase.client.Scan} object.
	 * @param filter a {@link org.apache.hadoop.hbase.filter.Filter} object.
	 * @param binder a {@link com.sleepycat.bind.tuple.TupleBinding} object.
	 * @param family a {@link java.lang.String} object.
	 * @param label a {@link java.lang.String} object.
	 * @param returnAllVersions a boolean.
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
	 */
	protected SeqWareIterator getModels(HTable table, Scan scan, Filter filter,
			TupleBinding binder, String family, String label,
			boolean returnAllVersions) {

		HBaseModelIterator iterator = null;

		try {

			// RowFilter filter = new RowFilter(CompareOp.GREATER_OR_EQUAL, new
			// BinaryComparator(Bytes.toBytes(tag)));
			// SingleColumnValueFilter filter = new
			// SingleColumnValueFilter(Bytes.toBytes("tagFamily"), null,
			// CompareOp.EQUAL, Bytes.toBytes("stop-codon-loss"));
			scan.setMaxVersions();
			if (filter != null) {
				scan.setFilter(filter);
			}
			// System.out.println("Trying to get iterator for family: "+family+" and label: "+label+" table: "+Bytes.toString(table.getTableName()));
			iterator = new HBaseModelIterator(table.getScanner(scan), table,
					binder, family, label, returnAllVersions);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			iterator = null;
		}

		return (iterator);

	}

	// FIXME: HBaseTagModelIterator will need to be updated to support storing
	// timestamp
	/**
	 * <p>getModelsByTag.</p>
	 *
	 * @param tag a {@link java.lang.String} object.
	 * @param tagTable a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param table a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param binder a {@link com.sleepycat.bind.tuple.TupleBinding} object.
	 * @param family a {@link java.lang.String} object.
	 * @param label a {@link java.lang.String} object.
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
	 */
	protected SeqWareIterator getModelsByTag(String tag, HTable tagTable,
			HTable table, TupleBinding binder, String family, String label) {

		HBaseTagModelIterator tagIterator = null;

		try {

			PrefixFilter filter = new PrefixFilter(Bytes.toBytes(tag));

			// SingleColumnValueFilter filter = new
			// SingleColumnValueFilter(Bytes.toBytes("tagFamily"), null,
			// CompareOp.EQUAL, Bytes.toBytes("stop-codon-loss"));
			Scan scan = new Scan();
			scan.setMaxVersions();
			scan.setFilter(filter);
			tagIterator = new HBaseTagModelIterator(tag,
					tagTable.getScanner(scan), table, binder, family, label);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			tagIterator = null;
		}
		return (tagIterator);

	}

	/**
	 * Other methods expect this to return a SeqWareIterator and not a HashMap.
	 *
	 * @param tagTable a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @return a {@link java.util.HashMap} object.
	 */
	protected HashMap<String, Integer> getModelTags(HTable tagTable) {

		HashMap<String, Integer> output = new HashMap<String, Integer>();
		try {
			Scan scan = new Scan();
			scan.setMaxVersions();
			ResultScanner scanner = tagTable.getScanner(scan);
			for (Result result : scanner) {
				String currTag = new String(result.getValue(
						Bytes.toBytes("key"), null));
				if (currTag != null) {
					Integer count = output.get(currTag);
					if (count == null) {
						count = new Integer(0);
					}
					count++;
					output.put(currTag, count);
				}
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return (output);
	}

	// FIXME: this will need to parameterize version!
	/**
	 * <p>getModel.</p>
	 *
	 * @param modelId a {@link java.lang.String} object.
	 * @param binder a {@link com.sleepycat.bind.tuple.TupleBinding} object.
	 * @param table a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param family a {@link java.lang.String} object.
	 * @param label a {@link java.lang.String} object.
	 * @param version a {@link java.lang.Long} object.
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.model.Model} object.
	 */
	protected Model getModel(String modelId, TupleBinding binder, HTable table,
			String family, String label, Long version) {

		Model model = null;

		try {
			// System.err.println("MODEL ID: "+modelId);
			Get g = new Get(Bytes.toBytes(modelId));
			g.setMaxVersions();
			g.addColumn(Bytes.toBytes(family), Bytes.toBytes(label));
			Result result = table.get(g);
			byte[] data = null;
			if (result != null
					&& !result.isEmpty()
					&& result.containsColumn(Bytes.toBytes(family),
							Bytes.toBytes(label))) {
				NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result
						.getMap();
				NavigableMap<Long, byte[]> entry = map.get(
						Bytes.toBytes(family)).get(Bytes.toBytes(label));
				// System.err.println("ENTRY SIZE: "+entry.size());
				for (Long currTimeStamp : entry.keySet()) {
					// System.err.println("Entry: "+currTimeStamp+" version: "+version);
					if (currTimeStamp.equals(version)) {
						// System.out.println("Entry is: "+entry.get(currTimeStamp));
						data = entry.get(currTimeStamp);
						break;
					}
				}
			}

			// Cell cell = result.getCellValue(Bytes.toBytes(family),
			// Bytes.toBytes(label));
			/*
			 * for(Map.Entry<Long, byte[]> entry : cell) {
			 * System.err.println("Entry: "+entry.getKey()); if
			 * (entry.getKey().equals(version)) { data = entry.getValue();
			 * break; } }
			 */
			if (data != null) {
				DatabaseEntry value = new DatabaseEntry(data);
				model = (Model) binder.entryToObject(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return (model);

	}

	/**
	 * This is actually a pretty dangerous method since just the most recent
	 * biallelic variant will be returned. It will seem to work for the vast
	 * majority of variants but fail for bi/triallelic locations
	 *
	 * @param modelId a {@link java.lang.String} object.
	 * @param binder a {@link com.sleepycat.bind.tuple.TupleBinding} object.
	 * @param table a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param family a {@link java.lang.String} object.
	 * @param label a {@link java.lang.String} object.
	 * @return a {@link net.sourceforge.seqware.queryengine.backend.model.Model} object.
	 */
	protected Model getLatestModel(String modelId, TupleBinding binder,
			HTable table, String family, String label) {

		Model model = null;

		try {
			// System.err.println("MODEL ID: "+modelId);
			Get g = new Get(Bytes.toBytes(modelId));
			// only the most recent
			g.setMaxVersions(1);
			g.addColumn(Bytes.toBytes(family), Bytes.toBytes(label));
			Result result = table.get(g);
			byte[] data = null;
			if (result != null
					&& !result.isEmpty()
					&& result.containsColumn(Bytes.toBytes(family),
							Bytes.toBytes(label))) {
				NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result
						.getMap();
				NavigableMap<Long, byte[]> entry = map.get(
						Bytes.toBytes(family)).get(Bytes.toBytes(label));
				// System.err.println("ENTRY SIZE: "+entry.size());
				for (Long currTimeStamp : entry.keySet()) {
					// only the first
					// System.out.println("Entry is: "+entry.get(currTimeStamp));
					data = entry.get(currTimeStamp);
					break;
				}
			}
			if (data != null) {
				DatabaseEntry value = new DatabaseEntry(data);
				model = (Model) binder.entryToObject(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return (model);

	}

	/**
	 * FIXME: what happens if something steps on this load below and creates a
	 * timestamp that exists already? may need to look at transaction support or
	 * making this syncronized (won't matter if other client on other machine
	 * overwrites this timestamp!)
	 *
	 * @param modelId a {@link java.lang.String} object.
	 * @param model a {@link net.sourceforge.seqware.queryengine.backend.model.LocatableModel} object.
	 * @param binder a {@link com.sleepycat.bind.tuple.TupleBinding} object.
	 * @param table a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param commonTagIndexTable a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param specificTagIndexTable a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param family a {@link java.lang.String} object.
	 * @param label a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	protected String putModel(String modelId, LocatableModel model,
			TupleBinding binder, HTable table, HTable commonTagIndexTable,
			HTable specificTagIndexTable, String family, String label) {

		String id = null;

		try {

			// using position as ID
			id = model.getContig() + ":"
					+ padZeros(model.getStartPosition(), HBaseStore.PAD);

			// need to figure out the timestamp to use so lookup what's there
			// already
			/*
			 * Get g = new Get(Bytes.toBytes(id)); Result result = table.get(g);
			 * // trying the keyvalue approach List<KeyValue> kvList =
			 * result.list(); //Cell cell =
			 * result.getCellValue(Bytes.toBytes(family), Bytes.toBytes(label));
			 * long numberOfTimestamps = 0; if (kvList != null) { for(KeyValue
			 * kv : kvList) { if (Bytes.toString(kv.getFamily()).equals(family)
			 * && Bytes.toString(kv.getQualifier()).equals(label)) {
			 * //System.out.println("The timestamp was: "+kv.getTimestamp()); if
			 * (kv.getTimestamp() > numberOfTimestamps) { numberOfTimestamps =
			 * kv.getTimestamp();
			 * //System.out.println("Found a previous value with ID: "
			 * +id+" family: "
			 * +family+" label: "+label+" using timestamp: "+numberOfTimestamps
			 * ); } } } }
			 */

			/*
			 * Using the database to find the next timestamp value was simply
			 * too slow and I couldn't figure out how to get a timestamp back
			 * upon insert (doesn't seem to work anyway when I use Autoflush
			 * false) so using my own counter. This should work so long as
			 * multiple HBaseStore instances aren't trying to load the same
			 * variants in the same order (which shouldn't happen!)
			 */
			String idWithVersion = null;
			long currTimestamp = 0L;
			Pattern versionPat = Pattern.compile("^.*\\.v(\\d+)$");
			Matcher versionMat = versionPat.matcher(modelId);
			if (versionMat.find()) {
				idWithVersion = modelId;
				currTimestamp = Long.parseLong(versionMat.group(1));
			} else {
				currTimestamp = getTimestamp();
				idWithVersion = modelId + ".v" + currTimestamp;
			}
			model.setId(idWithVersion);
			// System.err.println("Final ID: "+idWithVersion);

			// else { System.out.println("kv is null"); }
			/*
			 * if (cell != null) { numberOfTimestamps = cell.getNumValues();
			 * System.out.println("Found a previous value with ID: "+id+
			 * " using timestamp: "+numberOfTimestamps+1); }
			 */
			// numberOfTimestamps++;
			// System.out.println("New timestamp is: "+numberOfTimestamps);

			// create a complete ID that should be unique across the DB!
			// Date date = new Date();
			// String idWithVersion = idNoVersion+".v"+date;
			// System.out.println(idWithVersion);
			// model.setId(idWithVersion);
			// model.setId(idNoVersion);

			// String idWithVersion = idNoVersion+".v"+getTimestamp();
			// model.setId(idWithVersion);

			// create a byte array of the model object
			DatabaseEntry value = new DatabaseEntry();
			binder.objectToEntry(model, value);
			byte[] data = value.getData();

			// TODO: To speed up the inserts in a non critical job (like an
			// import job), you can use Put.writeToWAL(false) to bypass writing
			// to the write ahead log.
			Put p = new Put(Bytes.toBytes(id));
			p.add(Bytes.toBytes(family), Bytes.toBytes(label), currTimestamp,
					data);
			// p.add(Bytes.toBytes(family), Bytes.toBytes(label), new
			// Long(numberOfTimestamps), data);
			// try letting HBase figure out the timestamp!
			// p.add(Bytes.toBytes(family), Bytes.toBytes(label), data);

			// now deal with tags
			HashMap<String, String> tags = model.getTags();

			for (String key : tags.keySet()) {
				String tagId = key + idWithVersion;
				// String tagId = key+idNoVersion;
				// TODO: To speed up the inserts in a non critical job (like an
				// import job), you can use Put.writeToWAL(false) to bypass
				// writing to the write ahead log.
				Put tagP = new Put(Bytes.toBytes(tagId));
				tagP.add(Bytes.toBytes("rowId"), null, Bytes.toBytes(id));
				tagP.add(Bytes.toBytes("modelId"), null,
						Bytes.toBytes(idWithVersion));
				// tagP.add(Bytes.toBytes("modelId"), null,
				// Bytes.toBytes(idNoVersion));
				tagP.add(Bytes.toBytes("key"), null, Bytes.toBytes(key));
				if (tags.get(key) != null) {
					tagP.add(Bytes.toBytes("value"), null,
							Bytes.toBytes(tags.get(key)));
				}
				commonTagIndexTable.put(tagP);
				specificTagIndexTable.put(tagP);
			}

			// FIXME: want to add some sort of tag structure to genome table
			table.put(p);

			// this doesn't work
			// System.out.println("Can I get the timestamp back?: "+p.getTimeStamp());

			// FIXME: probably don't need these!
			// genomeTable.flushCommits();
			// tagIndexTable.flushCommits();
			// currGenomeTagIndexTable.flushCommits();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.toString());
		}

		// System.out.println("PUT: ID: "+id+" Model ID: "+model.getId());

		// always work with the correctly encoded, unique ID string!
		return (model.getId());
	}

}
