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
package com.github.seqware.impl;

import com.github.seqware.model.Atom;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.util.SGID;
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

    private boolean inefficiencyWarning = false;
    public static final boolean TEST_REMOTELY = false;
    public static final int PAD = 15;
    private static final String TEST_TABLE_PREFIX = System.getProperty("user.name") + StorageInterface.separator + "hbaseTestTable";
    private static final String TEST_COLUMN = "allData";
    private static final String TEST_QUALIFIER = "qualifier";
    private static final boolean PERSIST = true;
    private Configuration config;
    private SerializationInterface serializer;
    private Map<String, HTable> tableMap = new HashMap<String, HTable>();

    public HBaseStorage(SerializationInterface i) {
        this.serializer = i;
        // The HBaseConfiguration reads in hbase-site.xml and hbase-default.xml,
        // as long as these can be found in the CLASSPATH.
        this.config = HBaseConfiguration.create();

        if (TEST_REMOTELY) {
            config.clear();
            config.set("hbase.zookeeper.quorum", "sqwdev.res");
            config.set("hbase.zookeeper.property.clientPort", "2181");
            config.set("hbase.master", "sqwdev.res:60000");
        }

        Logger.getLogger(HBaseStorage.class.getName()).log(Level.INFO, "Starting with {0} using {1}", new Object[]{HBaseStorage.class.getSimpleName(), serializer.getClass().getSimpleName()});
        for (String s : super.biMap.values()) {
            String tableName = TEST_TABLE_PREFIX + StorageInterface.separator + s;
            // Create a fresh table, i.e. delete an existing table if it exists:
            HTableDescriptor ht = new HTableDescriptor(tableName);
            ht.addFamily(new HColumnDescriptor(TEST_COLUMN));
            // make a persistent store exists already, otherwise try to retrieve existing items
            try {
                HBaseAdmin hba = new HBaseAdmin(config);
                if (!PERSIST && hba.isTableAvailable(tableName)) {
                    // clear tables if needed
                    hba.disableTable(tableName);
                    hba.deleteTable(tableName);
                }
                if (!hba.isTableAvailable(tableName)) {
                    hba.createTable(ht);
                }
                HTable table = new HTable(config, tableName);
                tableMap.put(s, table);
            } catch (IOException ex) {
                Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", ex);
            }
        }
    }

    @Override
    public void serializeAtomToTarget(Atom obj) {
        String prefix = ((AtomImpl) obj).getHBasePrefix();
        try {
            byte[] featureBytes = serializer.serialize(obj);
            // as a test, let's try readable rowKeys
            Put p = new Put(Bytes.toBytes(obj.getSGID().getChainID().toString()));
            // Serialize:
            p.add(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER), featureBytes);
            tableMap.get(prefix).put(p);
            // try to get back timestamp for now
            Get g = new Get(Bytes.toBytes(obj.getSGID().getChainID().toString()));
            Result result = tableMap.get(prefix).get(g);
            long timestamp = result.getColumnLatest(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER)).getTimestamp();
            obj.getSGID().setBackendTimestamp(new Date(timestamp));
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Atom deserializeTargetToAtom(SGID sgid) {
        return deserializeTargetToAtom(sgid, true);
    }

    private Atom deserializeTargetToAtom(SGID sgid, boolean useTimestamp) {
        if (!inefficiencyWarning){
            inefficiencyWarning = true;
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.WARNING, "Why you use deserializeTargetToAtom(SGID sgid) in HBase?");
        }
        for (Entry<String, HTable> entry : tableMap.entrySet()) {
            Class properClass = super.biMap.inverse().get(entry.getKey());
            Atom a = deserializeAtom(sgid, properClass, entry.getValue(), useTimestamp);
            if (a != null) {
                return a;
            }
        }
        return null;
    }

    private Atom deserializeAtom(SGID sgid, Class properClass, HTable table, boolean useTimestamp) {
        try {
            Get g = new Get(Bytes.toBytes(sgid.getChainID().toString()));
            Result result = table.get(g);
            // handle null case
            if (result.isEmpty()) {
                return null;
            }
            byte[] value;
            long getTimeStamp = Long.MIN_VALUE;
            if (useTimestamp) {
                value = result.getMap().get(Bytes.toBytes(TEST_COLUMN)).get(Bytes.toBytes(TEST_QUALIFIER)).get(sgid.getBackendTimestamp().getTime());
            } else {
                KeyValue columnLatest = result.getColumnLatest(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER));
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
            return deserializedAtom;
        } catch (IOException ex) {
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
        if (!inefficiencyWarning){
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

    @Override
    public <T extends Atom> T deserializeTargetToAtom(SGID sgid, Class<T> t) {
        return (T) deserializeTargetToAtom(sgid, t, true);
    }

    private <T extends Atom> T deserializeTargetToAtom(SGID sgid, Class<T> t, boolean useTimestamp) {
        String prefix = super.biMap.get(t);
        HTable table = tableMap.get(prefix);
        return (T) deserializeAtom(sgid, t, table, useTimestamp);
    }

    @Override
    public Atom deserializeTargetToLatestAtom(SGID sgid) {
        return deserializeTargetToAtom(sgid, false);
    }

    @Override
    public <T extends Atom> T deserializeTargetToLatestAtom(SGID sgid, Class<T> t) {
        return (T) deserializeTargetToAtom(sgid, t, false);
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
                byte[] bytes = sIter.next().getColumnLatest(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER)).getValue();
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
