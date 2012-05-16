/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.comparators;

import java.util.Comparator;

import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.ContigPositionTB;
import net.sourceforge.seqware.queryengine.backend.model.ContigPosition;

import com.sleepycat.db.DatabaseEntry;

/**
 * @author boconnor
 *
 */
public class ContigPositionComparator implements Comparator {

  /* (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Object d1, Object d2) {
    byte[] b1 = (byte[])d1;
    byte[] b2 = (byte[])d2;
    DatabaseEntry dbe1 = new DatabaseEntry(b1);
    DatabaseEntry dbe2 = new DatabaseEntry(b2);
    ContigPositionTB agtb = new ContigPositionTB();
    ContigPosition agcp1 = (ContigPosition)agtb.entryToObject(dbe1);
    ContigPosition agcp2 = (ContigPosition)agtb.entryToObject(dbe2);
    // need to go from two byte arrays to either integer or long then
    // do comparison where -1 if d1<d2, 0 if d1==d2, and 1 if d1>d2
    int contigCompare = agcp1.getContig().compareTo(agcp2.getContig());
    if (contigCompare == 0) {
      if (agcp1.getStartPosition() < agcp2.getStartPosition()) { return -1; }
      else if (agcp1.getStartPosition() > agcp2.getStartPosition()) { return 1; }
      return 0;
    }
    else { return(contigCompare); }
  }

}
