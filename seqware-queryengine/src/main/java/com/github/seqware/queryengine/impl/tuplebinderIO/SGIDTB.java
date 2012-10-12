/**
 * 
 */
package com.github.seqware.queryengine.impl.tuplebinderIO;

import com.github.seqware.queryengine.util.SGID;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * <p>SGIDTB class.</p>
 *
 * @author boconnor
 * @author dyuen
 * @version $Id: $Id
 */
public class SGIDTB extends TupleBinding {

  /** {@inheritDoc} */
  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    SGID f = (SGID)object;
    to.writeLong(f.getUuid().getMostSignificantBits());
    to.writeLong(f.getUuid().getLeastSignificantBits());
    to.writeString(f.getFriendlyRowKey());
  }

  /** {@inheritDoc} */
  @Override
  public Object entryToObject(TupleInput ti) {
      // reset because this InputStream may have been sniffed before this tuple binder was called
      ti.reset();
      SGID sgid = new SGID(ti.readLong(), ti.readLong(), ti.readLong(), ti.readString());
      return sgid;
  }

}
