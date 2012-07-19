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
import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.util.SGID;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author dyuen
 */
public class HBaseStorage extends StorageInterface {

    private static final String TEST_COLUMN = "allData";
    private static final byte[] TEST_COLUMN_INBYTES = Bytes.toBytes("allData");
    private static final byte[] TEST_QUALIFIER_INBYTES = Bytes.toBytes("qualifier");
    private boolean inefficiencyWarning = false;
    public static final int PAD = 15;
    private static final String TEST_TABLE_PREFIX = System.getProperty("user.name") + StorageInterface.separator + "hbaseTestTable";
    private static final boolean PERSIST = Constants.PERSIST;
    private Configuration config;
    private SerializationInterface serializer;
    private Map<String, HTable> tableMap = new HashMap<String, HTable>();
    public final static boolean DEBUG = true;
    private Map<String, Integer> minMap = null;
    private Map<String, Integer> maxMap = null;

    public HBaseStorage(SerializationInterface i) {
        if (DEBUG) {
            minMap = new HashMap<String, Integer>();
            maxMap = new HashMap<String, Integer>();
        }

        this.serializer = i;
        // The HBaseConfiguration reads in hbase-site.xml and hbase-default.xml,
        // as long as these can be found in the CLASSPATH.
        this.config = HBaseConfiguration.create();
        configureHBaseConfig(config);
        Logger.getLogger(HBaseStorage.class.getName()).log(Level.INFO, "Starting with {0} using {1}", new Object[]{HBaseStorage.class.getSimpleName(), serializer.getClass().getSimpleName()});
        try {
            HBaseAdmin hba = new HBaseAdmin(config);
            // kick out existing tables if thats what we want
            if (!PERSIST) {
                Logger.getLogger(HBaseStorage.class.getName()).log(Level.INFO, "Clearing existing tables");
                HTableDescriptor[] listTables = hba.listTables(TEST_TABLE_PREFIX + ".*");
                hba.disableTables(TEST_TABLE_PREFIX + ".*");
                hba.deleteTables(TEST_TABLE_PREFIX + ".*");
            }

            for (String s : super.biMap.values()) {
                String tableName = TEST_TABLE_PREFIX + StorageInterface.separator + s;
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
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", ex);
        }
    }

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
                p.add(TEST_COLUMN_INBYTES, TEST_QUALIFIER_INBYTES, featureBytes);
                putList.add(p);
            }
            if (DEBUG) {
                if (!this.minMap.containsKey(prefix)) {
                    this.minMap.put(prefix, Integer.MAX_VALUE);
                }
                if (!this.maxMap.containsKey(prefix)) {
                    this.maxMap.put(prefix, Integer.MIN_VALUE);
                }
                this.minMap.put(prefix, Math.min(minSize, this.minMap.get(prefix)));
                this.maxMap.put(prefix, Math.max(maxSize, this.maxMap.get(prefix)));
            }

            // establish put
            Object[] putBatch = table.batch(putList);
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.WARNING, "Why you use deserializeTargetToAtom(SGID sgid) in HBase?");
        }
        for (Entry<String, HTable> entry : tableMap.entrySet()) {
            Class properClass = super.biMap.inverse().get(entry.getKey());
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
                if (useTimestamp) {
                    value = result.getMap().get(TEST_COLUMN_INBYTES).get(TEST_QUALIFIER_INBYTES).get(sgid.getBackendTimestamp().getTime());
                } else {
                    KeyValue columnLatest = result.getColumnLatest(TEST_COLUMN_INBYTES, TEST_QUALIFIER_INBYTES);
                    value = columnLatest.getValue();
                    getTimeStamp = columnLatest.getTimestamp();
                }
                // I wonder if this handles subclassing properly ... turns out no
                AtomImpl deserializedAtom = (AtomImpl) serializer.deserialize(value, properClass);
                // populate the timestamp field on the way out
                if (useTimestamp) {
                    deserializedAtom.getSGID().setBackendTimestamp(sgid.getBackendTimestamp());
                } else {
                    deserializedAtom.getSGID().setBackendTimestamp(new Date(getTimeStamp));
                }
                atomList.add(deserializedAtom);
            }
            return atomList;
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (InterruptedException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public final void clearStorage() {
        for (String s : super.biMap.values()) {
            String tableName = TEST_TABLE_PREFIX + StorageInterface.separator + s;
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
                Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", ex);
                System.exit(-1);
            }
        }
    }

    @Override
    public Iterable<SGID> getAllAtoms() {
        if (!inefficiencyWarning) {
            inefficiencyWarning = true;
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.WARNING, "Why you use getAllAtoms() in HBase?");
        }
        List<SGID> list = new ArrayList<SGID>();
        for (String prefix : tableMap.keySet()) {
            for (SGID id : this.getAllAtomsForTable(prefix)) {
                list.add(id);
            }
        }
        return list;
    }

    public Iterable<SGID> getAllAtomsForTable(String prefix) {
        HTable table = tableMap.get(prefix);
        Class cl = super.biMap.inverse().get(prefix);
        try {
            Scan s = new Scan();
            // we need the actual values if we do not store SGID in row key for debugging
            //s.setFilter(new KeyOnlyFilter());
            ResultScanner scanner = table.getScanner(s);
            return new ScanIterable(scanner, cl);
        } catch (IOException iOException) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", iOException);
            return null;
        }
    }

    private <T extends Atom> T deserializeTargetToAtom(SGID sgid, Class<T> t, boolean useTimestamp) {
        String prefix = super.biMap.get(t);
        HTable table = tableMap.get(prefix);
        List<Atom> deserializeAtom = deserializeAtom(t, table, useTimestamp, sgid);
        if (deserializeAtom != null && deserializeAtom.size() > 0) {
            return (T) deserializeAtom.get(0);
        }
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
        String prefix = super.biMap.get(t);
        HTable table = tableMap.get(prefix);
        return (List<T>) deserializeAtom(t, table, true, sgid);
    }

    @Override
    public void closeStorage() {
        for (HTable table : tableMap.values()) {
            try {
                table.close();
            } catch (IOException ex) {
                Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, "exception on HTable closing", ex);
            }
        }
        Logger.getLogger(HBaseStorage.class.getName()).log(Level.INFO, "closing HBaseStorage tables");
        tableMap.clear();
        if (DEBUG) {
            for (Entry<String, Integer> e : maxMap.entrySet()) {
                int maxValue = e.getValue();
                int minValue = minMap.get(e.getKey());
                Logger.getLogger(HBaseStorage.class.getName()).log(Level.INFO, "Serialized sizes for {0} are max: {1} and min: {2}", new Object[]{e.getKey(), maxValue, minValue});
            }
        }
        maxMap.clear();
        minMap.clear();
        // ensure that the factory references are also closed, just in case
        Factory.closeStorage();
    }

    public class ScanIterable implements Iterable {

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
    }

    public class ScanIterator implements Iterator<SGID> {

        private final Iterator<Result> sIter;
        private final Class cl;
        private SGID payload = null;

        protected ScanIterator(ResultScanner scanner, Class cl) {
            this.sIter = scanner.iterator();
            this.cl = cl;
        }

        @Override
        public boolean hasNext() {
            // we actually need to check for nulls due to different serialization formats
            while (payload == null && sIter.hasNext()) {
                // check
                byte[] bytes = sIter.next().getColumnLatest(TEST_COLUMN_INBYTES, TEST_QUALIFIER_INBYTES).getValue();
                Object obj = serializer.deserialize(bytes, cl);
                if (obj != null) {
                    payload = (SGID) ((AtomImpl) obj).getSGID();
                    return true;
                }
            }
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
}
