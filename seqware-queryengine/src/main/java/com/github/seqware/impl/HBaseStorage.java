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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author dyuen
 */
public class HBaseStorage extends StorageInterface {

    public static final int PAD = 15;
    private static final String TEST_TABLE_PREFIX = "hbaseTestTable";
    private static final String TEST_COLUMN = "allData";
    private static final String TEST_QUALIFIER = "qualifier";
    private static final boolean PERSIST = false;
    private Configuration config;
    private SerializationInterface serializer;
    private Map<String, HTable> tableMap = new HashMap<String, HTable>();

    public HBaseStorage(SerializationInterface i) {
        this.serializer = i;
        // The HBaseConfiguration reads in hbase-site.xml and hbase-default.xml,
        // as long as these can be found in the CLASSPATH.
        this.config = HBaseConfiguration.create();
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
        String prefix = ((AtomImpl)obj).getHBasePrefix();
        try {
            byte[] featureBytes = serializer.serialize(obj);
            // as a test, let's try readable rowKeys
            Put p = new Put(Bytes.toBytes(obj.getSGID().toString()));
            // Serialize:
            p.add(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER), featureBytes);
            tableMap.get(prefix).put(p);
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Atom deserializeTargetToAtom(SGID sgid) {
        // inefficient, try to favour methods where the class is known
        for (HTable table : tableMap.values()) {
            Atom a = deserializeAtom(sgid, table);
            if (a != null){
                return a;
            }
        }
        return null;
    }

    private Atom deserializeAtom(SGID sgid, HTable table) {
        try {
            Get g = new Get(Bytes.toBytes(sgid.toString()));
            Result result = table.get(g);
            // handle null case
            if (result.isEmpty()) {
                return null;
            }
            byte[] columnLatest = result.getColumnLatest(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER)).getValue();
            // I wonder if this handles subclassing properly ... turns out no
            AtomImpl deserializedAtom = (AtomImpl) serializer.deserialize(columnLatest, Atom.class);
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
        //FIXME: super-duper inefficient, make really sure these calls do not make it into production
        List<SGID> list = new ArrayList<SGID>();
        for(String prefix : tableMap.keySet()){
            for(SGID id : this.getAllAtomsForTable(prefix)){
                list.add(id);
            }
        }
        return list;
    }
    
    public Iterable<SGID> getAllAtomsForTable(String prefix) {
        HTable table = tableMap.get(prefix);
        try {
            Scan s = new Scan();
            // we need the actual values if we do not store SGID in row key for debugging
            //s.setFilter(new KeyOnlyFilter());
            ResultScanner scanner = table.getScanner(s);
            return new ScanIterable(scanner);
        } catch (IOException iOException) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", iOException);
            return null;
        }
    }

    @Override
    public <T extends Atom> T deserializeTargetToAtom(SGID sgid, Class<T> t) {
        String prefix = super.biMap.get(t);
        HTable table = tableMap.get(prefix);
        return (T) deserializeAtom(sgid, table);
    }

    public class ScanIterable implements Iterable<SGID> {

        private final ResultScanner scanner;

        public ScanIterable(ResultScanner scanner) {
            this.scanner = scanner;
        }

        @Override
        public Iterator<SGID> iterator() {
            return new ScanIterator(scanner);
        }
    }

    public class ScanIterator implements Iterator<SGID> {

        private final Iterator<Result> sIter;

        protected ScanIterator(ResultScanner scanner) {
            this.sIter = scanner.iterator();
        }

        @Override
        public boolean hasNext() {
            return sIter.hasNext();
        }

        @Override
        public SGID next() {
            byte[] bytes = sIter.next().getColumnLatest(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER)).getValue();
            Atom sAtom = serializer.deserialize(bytes, AtomImpl.class);
            return sAtom.getSGID();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
