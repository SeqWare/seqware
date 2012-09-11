/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine.impl;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import com.github.seqware.queryengine.system.importers.FeatureImporter;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.SGID;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

/**
 *
 * @author dyuen
 */
public class HBaseStorage extends StorageInterface {

    private static final String TEST_COLUMN = "d";
    public static final byte[] TEST_FAMILY_INBYTES = Bytes.toBytes(TEST_COLUMN); // Try to keep the ColumnFamily names as small as possible, preferably one character (e.g. "d" for data/default). 
    public static final byte[] TEST_QUALIFIER_INBYTES = Bytes.toBytes("qualifier");
    private boolean inefficiencyWarning = false;
    public static final int PAD = 15;
    public static final String TEST_TABLE_PREFIX = Constants.NAMESPACE + StorageInterface.SEPARATOR + "hbaseTestTable_v2";
    private static final boolean PERSIST = Constants.PERSIST;
    private Configuration config;
    private SerializationInterface serializer;
    private Map<String, HTable> tableMap = new HashMap<String, HTable>();
    public final static boolean DEBUG = true;
    private Map<String, Integer> minMap = null;
    private Map<String, Integer> maxMap = null;
    private Map<String, Long> countMap = null;

    public HBaseStorage(SerializationInterface i) {
        if (DEBUG) {
            minMap = new HashMap<String, Integer>();
            maxMap = new HashMap<String, Integer>();
            countMap = new HashMap<String, Long>();
        }

        this.serializer = i;
        // The HBaseConfiguration reads in hbase-site.xml and hbase-default.xml,
        // as long as these can be found in the CLASSPATH.
        this.config = HBaseConfiguration.create();
        configureHBaseConfig(config);
        try {
            Logger.getLogger(HBaseStorage.class.getName()).info("Starting with " + HBaseStorage.class.getSimpleName() + " using " + serializer.getClass().getSimpleName() + " on " + java.net.InetAddress.getLocalHost().getHostName());
            HBaseAdmin hba = new HBaseAdmin(config);
            // kick out existing tables if thats what we want
            if (!PERSIST) {
                Logger.getLogger(HBaseStorage.class.getName()).info("Clearing existing tables");
//                HTableDescriptor[] listTables = hba.listTables(TEST_TABLE_PREFIX + ".*");
                hba.disableTables(TEST_TABLE_PREFIX + ".*");
                hba.deleteTables(TEST_TABLE_PREFIX + ".*");

            }
            for (String s : super.directBIMap.values()) {
                createTable(s, hba);
            }
            // attach all tables with variable names as well
            HTableDescriptor[] listTables = hba.listTables(TEST_TABLE_PREFIX + ".");
            for (HTableDescriptor des : listTables) {
                String nameAsString = des.getNameAsString();
                if (!tableMap.containsKey(nameAsString)) {
                    tableMap.put(nameAsString, new HTable(config, nameAsString));
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).fatal("Big problem with HBase, abort!", ex);
        }
    }

    /**
     * Create a table with the given String name
     *
     * @param s
     * @param hba
     * @throws IOException
     */
    private void createTable(String s, HBaseAdmin hba) throws IOException {
        String tableName = TEST_TABLE_PREFIX + StorageInterface.SEPARATOR + s;
        // Create a fresh table, i.e. delete an existing table if it exists:
        HTableDescriptor ht = new HTableDescriptor(tableName);
        HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(TEST_COLUMN);
        hColumnDescriptor.setMaxVersions(Integer.MAX_VALUE);
        ht.addFamily(hColumnDescriptor);
        // make a persistent store exists already, otherwise try to retrieve existing items
        if (!hba.isTableAvailable(tableName)) {
            hba.createTable(ht);
        }
        HTable table = new HTable(config, tableName);
        tableMap.put(s, table);
    }

    /**
     * Configure a HBaseConfiguration with a given set of properties
     *
     * @param config
     */
    public static void configureHBaseConfig(Configuration config) {
        if (Constants.HBASE_REMOTE_TESTING) {
            config.clear();
            for (Entry<String, String> e : Constants.HBASE_PROPERTIES.entrySet()) {
                config.set(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public <T extends Atom> void serializeAtomsToTarget(T... objArr) {
        if (objArr.length == 0) {
            return;
        }
        try {
            int maxSize = Integer.MIN_VALUE;
            int minSize = Integer.MAX_VALUE;

            String prefix = ((AtomImpl) objArr[0]).getHBasePrefix();
            HTable table = tableMap.get(prefix);
            if (table == null) {
                // create table if it does not already exist
                HBaseAdmin hba = new HBaseAdmin(config);
                this.createTable(prefix, hba);
                table = tableMap.get(prefix);
                assert (table != null);
            }
            List<Row> putList = new ArrayList<Row>();
            // List<Row> getList = new ArrayList<Row>();
            // queue up HBase calls for the batch interface
            for (T obj : objArr) {
                // create timestamps, since this API will run on the server, we don't need to do anything complicated
//                obj.getSGID().setBackendTimestamp(new Date());
                assert (prefix.equals(((AtomImpl) objArr[0]).getHBasePrefix()));
                byte[] featureBytes = serializer.serialize(obj);

                if (DEBUG) {
                    maxSize = Math.max(maxSize, featureBytes.length);
                    minSize = Math.min(minSize, featureBytes.length);
                }

                // as a test, let's try readable rowKeys
                Put p = new Put(Bytes.toBytes(obj.getSGID().getRowKey().toString()), obj.getSGID().getBackendTimestamp().getTime());
                // Serialize:
                if (obj instanceof FeatureList) {
                    FSGID fsgid = (FSGID) obj.getSGID();
                    p.add(TEST_FAMILY_INBYTES, Bytes.toBytes(fsgid.getFeatureSetID().getUuid().toString()), featureBytes);
                    Logger.getLogger(HBaseStorage.class.getName()).trace("Put on (FeatureList of size " + ((FeatureList) obj).getFeatures().size() + ") " + obj.toString() + " at " + obj.getSGID().toString());
                } else {
                    p.add(TEST_FAMILY_INBYTES, TEST_QUALIFIER_INBYTES, featureBytes);
                    Logger.getLogger(HBaseStorage.class.getName()).trace("Put on " + obj.toString() + " at " + obj.getSGID().toString());
                }
                putList.add(p);
            }
            if (DEBUG) {
                if (!this.minMap.containsKey(prefix)) {
                    this.minMap.put(prefix, Integer.MAX_VALUE);
                }
                if (!this.maxMap.containsKey(prefix)) {
                    this.maxMap.put(prefix, Integer.MIN_VALUE);
                }
                if (!this.countMap.containsKey(prefix)) {
                    this.countMap.put(prefix, 0L);
                }
                this.minMap.put(prefix, Math.min(minSize, this.minMap.get(prefix)));
                this.maxMap.put(prefix, Math.max(maxSize, this.maxMap.get(prefix)));
                this.countMap.put(prefix, this.countMap.get(prefix) + objArr.length);
            }

            // establish put
            Object[] putBatch = table.batch(putList);
            Logger.getLogger(HBaseStorage.class.getName()).trace("putBatch results: " + putBatch.length);
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).fatal("IOException during table.batch()", ex);
            // try to parse out more information to track down this issue with large data loads
            if (ex instanceof RetriesExhaustedWithDetailsException) {
                RetriesExhaustedWithDetailsException e = (RetriesExhaustedWithDetailsException) ex;
                Logger.getLogger(FeatureImporter.class.getName()).fatal("Extra information on RetriesExhaustedWithDetailsException");
                Logger.getLogger(FeatureImporter.class.getName()).fatal("Are we dealing with cluster issues? " + e.mayHaveClusterIssues());
                Logger.getLogger(FeatureImporter.class.getName()).fatal("Issues over " + e.getNumExceptions() + " exceptions");
            }
            throw new RuntimeException("Unrecoverable error in HBaseStorage");
        } catch (InterruptedException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).fatal("InterruptedException during table.batch()", ex);
            throw new RuntimeException("Unrecoverable error in HBaseStorage");
        }
    }

    @Override
    public void serializeAtomToTarget(Atom obj) {
        serializeAtomsToTarget(obj);
    }

    @Override
    public Atom deserializeTargetToAtom(SGID sgid) {
        return deserializeTargetToAtom(sgid, true);
    }

    private Atom deserializeTargetToAtom(SGID sgid, boolean useTimestamp) {
        if (!inefficiencyWarning) {
            inefficiencyWarning = true;
            Logger.getLogger(HBaseStorage.class.getName()).warn("Why you use deserializeTargetToAtom(SGID sgid) in HBase?");
        }
        if (sgid instanceof FSGID) {
            FSGID fsgid = (FSGID) sgid;
            if (!tableMap.containsKey(fsgid.getTablename())) {
                establishTableConnection(fsgid.getTablename());
            }
            deserializeAtom(Feature.class, tableMap.get(fsgid.getTablename()), useTimestamp, sgid);
        }

        for (Entry<String, HTable> entry : tableMap.entrySet()) {
            Class properClass = super.directBIMap.inverse().get(entry.getKey());
            properClass = handleNullClass(properClass, entry.getKey());
            List<Atom> list = deserializeAtom(properClass, entry.getValue(), useTimestamp, sgid);
            if (list == null || list.isEmpty()) {
                continue;
            }
            Atom a = list.get(0);
            if (a != null) {
                return a;
            }
        }
        return null;
    }

    private List<Atom> deserializeAtom(Class properClass, HTable table, boolean useTimestamp, SGID... sgidArr) {
        List<Row> getList = new ArrayList<Row>();
        for (SGID sID : sgidArr) {
            Get g = new Get(Bytes.toBytes(sID.getRowKey().toString()));
            g.setMaxVersions();
            getList.add(g);
        }

        try {
            Object[] batch = table.batch(getList);
            assert (batch.length == sgidArr.length);
            List<Atom> atomList = new ArrayList<Atom>();
            for (int i = 0; i < sgidArr.length; i++) {
                SGID sgid = sgidArr[i];
                Result result = (Result) batch[i];
                // handle null case
                if (result.isEmpty()) {
                    return null;
                }
                byte[] value;
                long getTimeStamp = Long.MIN_VALUE;
                byte[] qualifier;
                if (properClass == Feature.class) {
                    assert (sgid instanceof FSGID);
                    qualifier = Bytes.toBytes(((FSGID) sgid).getFeatureSetID().getUuid().toString());
                } else {
                    qualifier = TEST_QUALIFIER_INBYTES;
                }
                if (useTimestamp) {
                    value = result.getMap().get(TEST_FAMILY_INBYTES).get(qualifier).get(sgid.getBackendTimestamp().getTime());
                } else {
                    KeyValue columnLatest = result.getColumnLatest(TEST_FAMILY_INBYTES, qualifier);
                    value = columnLatest.getValue();
                    getTimeStamp = columnLatest.getTimestamp();
                }
                // I wonder if this handles subclassing properly ... turns out no
                AtomImpl deserializedAtom = (AtomImpl) serializer.deserialize(value, properClass == Feature.class ? FeatureList.class : properClass);
                atomList.add(deserializedAtom);
            }
            return atomList;
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).fatal(null, ex);
            return null;
        } catch (InterruptedException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).fatal(null, ex);
            return null;
        }
    }

    @Override
    public final void clearStorage() {
        for (String s : tableMap.keySet()) {
            String tableName = TEST_TABLE_PREFIX + StorageInterface.SEPARATOR + s;
            // Create a fresh table, i.e. delete an existing table if it exists:
            HTableDescriptor ht = new HTableDescriptor(tableName);
            ht.addFamily(new HColumnDescriptor(TEST_COLUMN));
            // make a persistent store exists already, otherwise try to retrieve existing items
            try {
                HBaseAdmin hba = new HBaseAdmin(config);
                hba.disableTable(tableName);
                hba.deleteTable(tableName);
                hba.createTable(ht);
                tableMap.put(s, new HTable(config, tableName));
            } catch (IOException ex) {
                Logger.getLogger(HBaseStorage.class.getName()).fatal("Big problem with HBase, abort!", ex);
                System.exit(-1);
            }
        }
    }

    @Override
    public Iterable<SGID> getAllAtoms() {
        if (!inefficiencyWarning) {
            inefficiencyWarning = true;
            Logger.getLogger(HBaseStorage.class.getName()).warn("getAllAtoms() in HBase is extremely expensive, you probably want to avoid this");
        }
        List<SGID> list = new ArrayList<SGID>();
        Set<String> keys = new HashSet<String>(tableMap.keySet());
        for (String prefix : keys) {
            for (SGID id : this.getAllAtomsForTable(prefix)) {
                list.add(id);
            }
        }
        return list;
    }

    public Iterable<SGID> getAllAtomsForTable(String prefix) {
        HTable table = tableMap.get(prefix);
        Class cl = super.directBIMap.inverse().get(prefix);
        cl = handleNullClass(cl, prefix);
        try {
            Scan s = new Scan();
            s.setMaxVersions();
            // we need the actual values if we do not store SGID in row key for debugging
            //s.setFilter(new KeyOnlyFilter());
            ResultScanner scanner = table.getScanner(s);
            return new ScanIterable(scanner, cl);
        } catch (IOException iOException) {
            Logger.getLogger(HBaseStorage.class.getName()).fatal("Big problem with HBase, abort!", iOException);
            return null;
        }
    }

    @Override
    public Iterable<FeatureList> getAllFeatureListsForFeatureSet(FeatureSet fSet) {
        assert (fSet instanceof LazyFeatureSet);
        LazyFeatureSet lfSet = (LazyFeatureSet) fSet;
        String prefix = lfSet.getTablename();
        HTable table = tableMap.get(prefix);
        if (table == null) {
            establishTableConnection(lfSet.getTablename());
            table = tableMap.get(prefix);
        }

        try {
            // I think this should return in sorted order already
            Scan s = new Scan();
            s.setMaxVersions();
            s.setCaching(500);
            // we need the actual values if we do not store SGID in row key for debugging
            //s.setFilter(new KeyOnlyFilter());
            ResultScanner scanner = table.getScanner(s);
            return new FeatureScanIterable(scanner, fSet.getSGID());
        } catch (IOException iOException) {
            Logger.getLogger(HBaseStorage.class.getName()).fatal("Big problem with HBase, abort!", iOException);
            return null;
        }
    }

    /**
     * Establish a connection to a table that is not yet present in our map
     *
     * @param tableName
     */
    private void establishTableConnection(String tableName) {
        try {
            // attach table
            HBaseAdmin hba = new HBaseAdmin(this.config);
            this.createTable(tableName, hba);
        } catch (Exception ex) {
            Logger.getLogger(HBaseStorage.class.getName()).fatal("Big problem with HBase, abort!", ex);
        }
    }

    private Class handleNullClass(Class cl, String prefix) {
        if (cl == null) {
            if (prefix.startsWith(FeatureList.prefix)) {
                cl = Feature.class;
            } else {
                assert (false);
            }
        }
        return cl;
    }

    private <T extends Atom> T deserializeTargetToAtom(SGID sgid, Class<T> t, boolean useTimestamp) {
        String prefix = super.directBIMap.get(t);
        if (prefix == null) {
            // for now, we separate only the Feature table by Reference
            assert (sgid instanceof FSGID);
            prefix = ((FSGID) sgid).getTablename();
        }
        HTable table = tableMap.get(prefix);
        if (table == null) {
            establishTableConnection(prefix);
            table = tableMap.get(prefix);
        }
        List<Atom> deserializeAtom = deserializeAtom(t, table, useTimestamp, sgid);
        if (deserializeAtom != null && deserializeAtom.size() > 0) {
            return (T) deserializeAtom.get(0);
        }
        Logger.getLogger(HBaseStorage.class.getName()).warn("Unable to locate " + sgid.getRowKey() + " as a " + t.getName());
        return null;
    }

    @Override
    public Atom deserializeTargetToLatestAtom(SGID sgid) {
        return deserializeTargetToAtom(sgid, false);
    }

    @Override
    public <T extends Atom> T deserializeTargetToLatestAtom(SGID sgid, Class<T> t) {
        return (T) deserializeTargetToAtom(sgid, t, false);
    }

    @Override
    public <T extends Atom> T deserializeTargetToAtom(Class<T> t, SGID sgid) {
        return (T) deserializeTargetToAtom(sgid, t, true);
    }

    @Override
    public <T extends Atom> List<T> deserializeTargetToAtoms(Class<T> t, SGID... sgid) {
        if (t == Feature.class) {
            // Features may be from a multiple number of tables
            Map<String, List<SGID>> map = new HashMap<String, List<SGID>>();
            for (SGID sid : sgid) {
                FSGID fID = (FSGID) sid;
                // sort by tables
                String tablename = fID.getTablename();
                if (!map.containsKey(tablename)) {
                    // create reference to HTable
                    map.put(tablename, new ArrayList<SGID>());
                }
                map.get(tablename).add(sid);
            }
            // go through sorted tables
            List<T> results = new ArrayList<T>();
            for (Entry<String, List<SGID>> e : map.entrySet()) {
                String tableName = e.getKey();
                if (!tableMap.containsKey(tableName)) {
                    establishTableConnection(tableName);
                }
                HTable table = tableMap.get(tableName);
                results.addAll((List<T>) deserializeAtom(t, table, true, e.getValue().toArray(new SGID[e.getValue().size()])));
            }
            return results;
        } else {
            String prefix = super.directBIMap.get(t);
            HTable table = tableMap.get(prefix);
            return (List<T>) deserializeAtom(t, table, true, sgid);
        }
    }

    @Override
    public void closeStorage() {
        for (HTable table : tableMap.values()) {
            try {
                table.close();
            } catch (IOException ex) {
                Logger.getLogger(HBaseStorage.class.getName()).fatal("exception on HTable closing", ex);
            }
        }
        Logger.getLogger(HBaseStorage.class.getName()).info("closing HBaseStorage tables");
        tableMap.clear();
        if (DEBUG) {
            for (Entry<String, Integer> e : maxMap.entrySet()) {
                int maxValue = e.getValue();
                int minValue = minMap.get(e.getKey());
                long count = countMap.get(e.getKey());
                Logger.getLogger(HBaseStorage.class.getName()).info(count + " " + e.getKey() + " serialized with a max: " + maxValue + " and min: " + minValue);
            }
            maxMap.clear();
            minMap.clear();
            countMap.clear();
        }
        // ensure that the factory references are also closed, just in case
        SWQEFactory.closeStorage();
    }

    /**
     * A scanner specifically for Features. Has to be FeatureList aware
     */
    public class FeatureScanIterable implements Iterable<FeatureList> {

        private final ResultScanner scanner;
        private final SGID featureSetID;

        public FeatureScanIterable(ResultScanner scanner, SGID sgid) {
            this.scanner = scanner;
            this.featureSetID = sgid;
        }

        @Override
        public Iterator<FeatureList> iterator() {
            return new FeatureScanIterator(scanner, featureSetID);
        }

        @Override
        protected void finalize() throws Throwable {
//            // make sure that we close the scanner if the using aborts the iteration and garbage collects this 
//            super.finalize();
//            scanner.close();
            // don't think this is working properly? getting UnknownScannerExceptions
        }
    }

    /**
     * Presents an interface for iterating through FeatureLists
     */
    public class FeatureScanIterator implements Iterator<FeatureList> {

        private final Iterator<Result> sIter;
        private FeatureList payload = null;
        private List<FeatureList> cachedPayloads = new ArrayList<FeatureList>();
        private final ResultScanner scanner;
        private final SGID featureSetID;

        protected FeatureScanIterator(ResultScanner scanner, SGID featureSetID) {
            this.scanner = scanner;
            this.sIter = scanner.iterator();
            this.featureSetID = featureSetID;
        }

        @Override
        public boolean hasNext() {
            // make this time-insensitive
            //            long timestamp = featureSetID.getBackendTimestamp().getTime();
            // we actually need to check for nulls due to different serialization formats
            while (sIter.hasNext() || cachedPayloads.size() > 0) {
                while (cachedPayloads.isEmpty() && sIter.hasNext()) {
                    Result result = sIter.next();
                    List<FeatureList> grabFeatureListsGivenRow = grabFeatureListsGivenRow(result, this.featureSetID, serializer);
                    cachedPayloads.addAll(grabFeatureListsGivenRow);
                }
                if (cachedPayloads.isEmpty() && payload == null) {
                    return false;
                }
                if (payload == null) {
                    payload = cachedPayloads.remove(cachedPayloads.size() - 1);
                }
                return true;
            }
            // if there are no more, close the scanner
            assert (cachedPayloads.isEmpty() && !sIter.hasNext());
            scanner.close();
            return payload != null;
        }

        @Override
        public FeatureList next() {
            boolean hasNext = this.hasNext();
            assert (hasNext);
            FeatureList load = payload;
            payload = null;
            return load;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * A generic scanner
     */
    public class ScanIterable implements Iterable<SGID> {

        private final ResultScanner scanner;
        private final Class cl;

        public ScanIterable(ResultScanner scanner, Class cl) {
            this.scanner = scanner;
            this.cl = cl;
        }

        @Override
        public Iterator<SGID> iterator() {
            return new ScanIterator(scanner, cl);
        }

        @Override
        protected void finalize() throws Throwable {
            // make sure that we close the scanner if the using aborts the iteration and garbage collects this 
            super.finalize();
            scanner.close();
        }
    }

    /**
     * Presents an interface for iterating through SGIDs
     */
    public class ScanIterator implements Iterator<SGID> {

        private final Iterator<Result> sIter;
        private final Class cl;
        private SGID payload = null;
        private List<SGID> cachedPayloads = new ArrayList<SGID>();
        private final ResultScanner scanner;

        protected ScanIterator(ResultScanner scanner, Class cl) {
            this.scanner = scanner;
            this.sIter = scanner.iterator();
            this.cl = cl;
        }

        @Override
        public boolean hasNext() {
            // we actually need to check for nulls due to different serialization formats
            while (payload == null && sIter.hasNext()) {
                // check
                if (cl != Feature.class) {
                    byte[] bytes = sIter.next().getColumnLatest(TEST_FAMILY_INBYTES, TEST_QUALIFIER_INBYTES).getValue();
                    Object obj = serializer.deserialize(bytes, cl);
                    if (obj != null) {
                        payload = (SGID) ((AtomImpl) obj).getSGID();
                        return true;
                    }
                } else {
                    while (cachedPayloads.isEmpty() && sIter.hasNext()) {
                        NavigableMap<byte[], byte[]> get = sIter.next().getNoVersionMap().get(TEST_FAMILY_INBYTES);
                        // go through the possible qualifiers and break them down
                        for (byte[] value : get.values()) {
                            FeatureList list = serializer.deserialize(value, FeatureList.class);
                            if (list == null) {
                                // TODO: investigate these
                                continue;
                            }
                            cachedPayloads.add(list.getSGID());
                        }
                    }
                    if (cachedPayloads.isEmpty()) {
                        return false;
                    }
                    payload = cachedPayloads.remove(cachedPayloads.size() - 1);
                    return true;
                }
            }
            // if there are no more, close the scanner
            scanner.close();
            return false;
        }

        @Override
        public SGID next() {
            SGID load = payload;
            payload = null;
            return load;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static List<FeatureList> grabFeatureListsGivenRow(Result result, SGID featureSetID, SerializationInterface serializer) {
        byte[] qualifier = Bytes.toBytes(featureSetID.getUuid().toString());
        List<FeatureList> cachedPayloads = new ArrayList<FeatureList>();
        // map is time -> data
        NavigableMap<Long, byte[]> map = result.getMap().get(TEST_FAMILY_INBYTES).get(qualifier);
        if (map == null) {
            // column not present in this row
            return cachedPayloads;
        }
        // go through the possible qualifiers and break them down
        for (Entry<Long, byte[]> e : map.entrySet()) {
            long time = e.getKey();
            if (time >= featureSetID.getBackendTimestamp().getTime()) {
                continue;
            }
            FeatureList list = serializer.deserialize(e.getValue(), FeatureList.class);
            if (list == null) {
                //TODO: investigate this
                continue;
            }
            cachedPayloads.add(list);
        }
        return cachedPayloads;
    }
}
