package net.sourceforge.seqware.queryengine.backend.util.iterators;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseEntry;

/**
 * <p>HBaseTagModelIterator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class HBaseTagModelIterator implements SeqWareIterator {

	ResultScanner scanner = null;
	HTable table = null;
	TupleBinding binder = null;
	String family = null;
	String label = null;
	String tag = null;
	Iterator<Result> scannerIt = null;
	Model currModel = null;

	/**
	 * <p>Constructor for HBaseTagModelIterator.</p>
	 *
	 * @param tag a {@link java.lang.String} object.
	 * @param scanner a {@link org.apache.hadoop.hbase.client.ResultScanner} object.
	 * @param table a {@link org.apache.hadoop.hbase.client.HTable} object.
	 * @param binder a {@link com.sleepycat.bind.tuple.TupleBinding} object.
	 * @param family a {@link java.lang.String} object.
	 * @param label a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public HBaseTagModelIterator(String tag, ResultScanner scanner,
			HTable table, TupleBinding binder, String family, String label)
			throws Exception {

		this.tag = tag;
		this.scanner = scanner;
		this.table = table;
		this.binder = binder;
		this.family = family;
		this.label = label;

		scannerIt = scanner.iterator();
	}

	// so the iterator class provides: hasNext(), next(), remove()
	/**
	 * <p>close.</p>
	 *
	 * @throws java.lang.Exception if any.
	 */
	public void close() throws Exception {
		scanner.close();
	}

	/**
	 * Not supported for HBase backend!
	 *
	 * @return a int.
	 * @throws java.lang.Exception if any.
	 */
	public int getCount() throws Exception {
		return (0);
	}

	/**
	 * Not applicable for HBase backend. FIXME: is there a way to get rid of
	 * this?
	 *
	 * @return a {@link com.sleepycat.db.Cursor} object.
	 * @throws java.lang.Exception if any.
	 */
	public Cursor getCursor() throws Exception {
		return (null);
	}

	/**
	 * Not applicable for HBase backend. FIXME: this is a BerkeleyDB specific
	 * method
	 *
	 * @return a {@link java.lang.Object} object.
	 * @throws java.io.UnsupportedEncodingException if any.
	 */
	public Object nextSecondaryKey() throws UnsupportedEncodingException {
		return (null);
	}

	/**
	 * <p>hasNext.</p>
	 *
	 * @return a boolean.
	 */
	@SuppressWarnings("deprecation")
	public boolean hasNext() {

		currModel = null;
		boolean hasNext = false;
		try {
			while (scannerIt.hasNext()) {
				Result result = scannerIt.next();
				String id = new String(result.getRow());
				// System.out.println("TAG_ID: "+id);
				String currTag = new String(result.getValue(
						Bytes.toBytes("key"), null));
				if (id.startsWith(tag) && currTag.equals(tag)) {
					String modelId = new String(result.getValue(
							Bytes.toBytes("modelId"), null));
					String rowId = new String(result.getValue(
							Bytes.toBytes("rowId"), null));
					Get g = new Get(Bytes.toBytes(rowId));
					// System.out.println("MODELID: "+modelId+" ROWID: "+rowId);
					String[] modelIdArr = modelId.split("\\.");
					String versionStr = modelIdArr[modelIdArr.length - 1];
					Long version = Long.parseLong(versionStr.substring(1));
					g.setTimeStamp(version);
					// System.out.println("TABLE: "+Bytes.toString(table.getTableName()));
					Result r = table.get(g);
					// cell was deprecated after 0.20.4, so had to re-write this
					// (needs testing)
					// Cell cell = r.getCellValue(Bytes.toBytes(family),
					// Bytes.toBytes(label));
					// byte[] data = null;
					// if (cell != null && cell.getNumValues() > 1) {
					// for(Map.Entry<Long, byte[]> entry : cell) {
					// if (entry.getKey().equals(version)) {
					// data = entry.getValue();
					// break;
					// }
					// }
					// }
					// else if (cell != null) {
					// data = cell.getValue();
					// }
					/** start of re-write */
					List<KeyValue> values = r.getColumn(Bytes.toBytes(family),
							Bytes.toBytes(label));
					byte[] data = null;
					if (values != null && values.size() > 1) {
						for (KeyValue entry : values) {
							if (entry.getKey().equals(version)) {
								data = entry.getValue();
								break;
							}
						}
					} else if (values != null) {
						data = values.get(0).getValue();
					}

					/** end of rw */

					if (data != null) {
						DatabaseEntry value = new DatabaseEntry(data);
						currModel = (Model) binder.entryToObject(value);
						// System.out.println("CURR_MODEL: "+currModel.getId());
						if (currModel != null) {
							return (true);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return (hasNext);

	}

	/**
	 * <p>next.</p>
	 *
	 * @return a {@link java.lang.Object} object.
	 */
	public Object next() {
		return (currModel);
	}

	/**
	 * Removes are unimplemented.
	 */
	public void remove() {
		// FIXME: this doesn't do anything!
	}

}
