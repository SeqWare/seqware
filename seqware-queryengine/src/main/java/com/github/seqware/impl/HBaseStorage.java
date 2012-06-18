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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;


/**
 *
 * @author dyuen
 */
public class HBaseStorage implements StorageInterface {
    
    public static final int PAD = 15;

    private static final String TEST_TABLE = "hbaseTestTable";
    private static final String TEST_COLUMN = "allData";
    private static final String TEST_QUALIFIER = "qualifier";
    private static final boolean PERSIST = false;
    private Configuration config;
    private SerializationInterface serializer;
    private HTable table;

    public HBaseStorage(SerializationInterface i) {
        this.serializer = i;
        // The HBaseConfiguration reads in hbase-site.xml and hbase-default.xml,
        // as long as these can be found in the CLASSPATH.
        this.config = HBaseConfiguration.create();
        Logger.getLogger(TmpFileStorage.class.getName()).log(Level.INFO, "Starting with {0} using {1}", new Object[]{HBaseStorage.class.getSimpleName(), serializer.getClass().getSimpleName()});
        // Create a fresh table, i.e. delete an existing table if it exists:
        HTableDescriptor ht = new HTableDescriptor(TEST_TABLE);
        ht.addFamily(new HColumnDescriptor(TEST_COLUMN));

        // make a persistent store exists already, otherwise try to retrieve existing items
        try {
            HBaseAdmin hba = new HBaseAdmin(config);
            if (!PERSIST && hba.isTableAvailable(TEST_TABLE)) {
                // clear tables if needed
                hba.disableTable(TEST_TABLE);
                hba.deleteTable(TEST_TABLE);
            }
            if (!hba.isTableAvailable(TEST_TABLE)) {
                hba.createTable(ht);
            }
            this.table = new HTable(config, TEST_TABLE);
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", ex);
        }
    }

    @Override
    public void serializeAtomToTarget(Atom obj) {
        try {
            byte[] featureBytes = serializer.serialize(obj);
            // as a test, let's try readable rowKeys
            Put p = new Put(Bytes.toBytes(obj.getSGID().toString()));
            // Serialize:
            p.add(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER), featureBytes);
            table.put(p);
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Atom deserializeTargetToAtom(SGID sgid) {
        try {
            Get g = new Get(Bytes.toBytes(sgid.toString()));
            Result result = table.get(g);
            // handle null case
            if (result.isEmpty()){
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
        HTableDescriptor ht = new HTableDescriptor(TEST_TABLE);
        ht.addFamily(new HColumnDescriptor(TEST_COLUMN));

        // make a persistent store exists already, otherwise try to retrieve existing items
        try {
            HBaseAdmin hba = new HBaseAdmin(config);
            hba.disableTable(TEST_TABLE);
            hba.deleteTable(TEST_TABLE);
            hba.createTable(ht);
            this.table = new HTable(config, TEST_TABLE);
        } catch (IOException ex) {
            Logger.getLogger(HBaseStorage.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", ex);
            System.exit(-1);
        }
    }

    @Override
    public Iterable<SGID> getAllAtoms() {
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
