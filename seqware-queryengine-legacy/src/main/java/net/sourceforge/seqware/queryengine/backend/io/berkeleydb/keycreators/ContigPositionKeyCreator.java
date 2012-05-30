/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.keycreators;


import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.ContigPositionTB;
import net.sourceforge.seqware.queryengine.backend.model.LocatableModel;
import net.sourceforge.seqware.queryengine.backend.model.ContigPosition;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.SecondaryDatabase;
import com.sleepycat.db.SecondaryKeyCreator;

/**
 * @author boconnor
 *
 */
// could alternatively extend SecondaryMultiKeyCreator for multiple read sequence groups
public class ContigPositionKeyCreator implements SecondaryKeyCreator {
  
  private TupleBinding binding;
  
  public ContigPositionKeyCreator(TupleBinding binding) {
    this.binding = binding;
  }
  
  public boolean createSecondaryKey(SecondaryDatabase secDb,
                                    DatabaseEntry key,
                                    DatabaseEntry value,
                                    DatabaseEntry result) {
    try {
      LocatableModel lm = (LocatableModel) binding.entryToObject(value);
      // tuple binding
      ContigPositionTB cptb = new ContigPositionTB();
      ContigPosition cp = new ContigPosition();
      cp.setContig(lm.getContig());
      cp.setStartPosition(lm.getStartPosition());
      cp.setStopPosition(lm.getStopPosition());
      
      //System.out.println("The contig position "+cp.getContig()+" "+cp.getStartPosition()+" "+cp.getStopPosition());
      
      cptb.objectToEntry(cp, result);
      
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    return true;
  }
}
