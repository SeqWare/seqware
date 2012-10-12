/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.comparators;

import java.util.Comparator;

import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.TagTB;
import net.sourceforge.seqware.queryengine.backend.model.Tag;

import com.sleepycat.db.DatabaseEntry;

/**
 * <p>TagComparator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class TagComparator implements Comparator {

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
    TagTB ttb = new TagTB();
    Tag tag1 = (Tag)ttb.entryToObject(dbe1);
    Tag tag2 = (Tag)ttb.entryToObject(dbe2);
    return(tag1.getTag().compareTo(tag2.getTag()));
  }

}
