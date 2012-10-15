/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.comparators;

import java.util.Comparator;

import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.StringIdTB;
import net.sourceforge.seqware.queryengine.backend.model.StringId;

import com.sleepycat.db.DatabaseEntry;

/**
 * <p>IntegerIdComparator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class IntegerIdComparator implements Comparator {

  /* (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  /** {@inheritDoc} */
  @Override
  public int compare(Object d1, Object d2) {
    byte[] b1 = (byte[])d1;
    byte[] b2 = (byte[])d2;
    DatabaseEntry dbe1 = new DatabaseEntry(b1);
    DatabaseEntry dbe2 = new DatabaseEntry(b2);
    StringIdTB mitb = new StringIdTB();
    StringId m1 = (StringId)mitb.entryToObject(dbe1);
    StringId m2 = (StringId)mitb.entryToObject(dbe2);
    return(m1.getId().compareTo(m2.getId()));
  }

}
