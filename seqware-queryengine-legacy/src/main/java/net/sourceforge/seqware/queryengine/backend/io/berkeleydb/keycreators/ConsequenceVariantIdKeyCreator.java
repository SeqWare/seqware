/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.keycreators;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.ContigPositionTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.StringIdTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.VariantTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.TagTB;
import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.model.ContigPosition;
import net.sourceforge.seqware.queryengine.backend.model.StringId;
import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.model.Tag;

import com.sleepycat.bind.tuple.ByteBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.SecondaryDatabase;
import com.sleepycat.db.SecondaryKeyCreator;
import com.sleepycat.db.SecondaryMultiKeyCreator;

/**
 * <p>ConsequenceVariantIdKeyCreator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ConsequenceVariantIdKeyCreator implements SecondaryKeyCreator {
    
    private TupleBinding binding;
    
    /**
     * <p>Constructor for ConsequenceVariantIdKeyCreator.</p>
     *
     * @param binding a {@link com.sleepycat.bind.tuple.TupleBinding} object.
     */
    public ConsequenceVariantIdKeyCreator(TupleBinding binding) {
      this.binding = binding;
    }
    
    /** {@inheritDoc} */
    public boolean createSecondaryKey(SecondaryDatabase secDb,
                                      DatabaseEntry key,
                                      DatabaseEntry value,
                                      DatabaseEntry result) {
      try {
        Consequence c = (Consequence) binding.entryToObject(value);
        /* tuple binding
        IntegerIdTB mitb = new IntegerIdTB();
        IntegerId mid = new IntegerId();
        mid.setId(new Long(c.getMismatchId()).toString());
        mitb.objectToEntry(mid, result);*/
        //StringBinding sb = new StringBinding();
        //ByteBinding sb = new ByteBinding();
        //sb.objectToEntry(c.getMismatchId(), result);
        //result = new DatabaseEntry(c.getMismatchId().getBytes("UTF-8"));
        
        StringIdTB sitb = new StringIdTB();
        StringId sid = new StringId();
        sid.setId(c.getMismatchId());
        sitb.objectToEntry(sid, result);
        
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
      return true;
    }
  }

