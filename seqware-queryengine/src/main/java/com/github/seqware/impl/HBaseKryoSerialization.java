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
public class HBaseKryoSerialization implements StorageInterface {

    private static final String TEST_TABLE = "hbaseKryoTestTable";
    private static final String TEST_COLUMN = "allData";
    private static final String TEST_QUALIFIER = "qualifier";
    private static final boolean PERSIST = false;
    private Configuration config;
    private SerializationInterface serializer;
    private HTable table;

    public HBaseKryoSerialization(SerializationInterface i) {
        this.serializer = i;
        // The HBaseConfiguration reads in hbase-site.xml and hbase-default.xml,
        // as long as these can be found in the CLASSPATH.
        this.config = HBaseConfiguration.create();
        Logger.getLogger(HBaseKryoSerialization.class.getName()).log(Level.INFO, "Starting with HBaseKryoSerialization");
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
            Logger.getLogger(HBaseKryoSerialization.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", ex);
            System.exit(-1);
        }
    }

    @Override
    public void serializeAtomToTarget(Atom obj) {
        try {
            byte[] featureBytes = serializer.serialize(obj);
            Put p = new Put(Bytes.toBytes(obj.toString()));
            // Serialize:
            p.add(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER), featureBytes);
            table.put(p);
        } catch (IOException ex) {
            Logger.getLogger(HBaseKryoSerialization.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Atom deserializeTargetToAtom(SGID sgid) {
        try {
            Get g = new Get(Bytes.toBytes(sgid.toString()));
            Result result = table.get(g);
            byte[] columnLatest = result.getColumnLatest(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes(TEST_QUALIFIER)).getValue();
            // handle null case
            if (columnLatest == null){
                return null;
            }
            // I wonder if this handles subclassing properly ... turns out no
            AtomImpl deserializedAtom = (AtomImpl) serializer.deserialize(columnLatest, Atom.class);
            return deserializedAtom;
        } catch (IOException ex) {
            Logger.getLogger(HBaseKryoSerialization.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(HBaseKryoSerialization.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", ex);
            System.exit(-1);
        }
    }

    @Override
    public Iterable<SGID> getAllAtoms() {
        try {
            Scan s = new Scan();
            s.setFilter(new KeyOnlyFilter());
            ResultScanner scanner = table.getScanner(s);
            return new ScanIterable(scanner);
        } catch (IOException iOException) {
            Logger.getLogger(HBaseKryoSerialization.class.getName()).log(Level.SEVERE, "Big problem with HBase, abort!", iOException);
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
            byte[] bytes = sIter.next().getRow();
            SGID sAtom = serializer.deserialize(bytes, SGID.class);
            return sAtom;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
