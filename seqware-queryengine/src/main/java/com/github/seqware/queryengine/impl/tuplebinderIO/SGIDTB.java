/**
 * 
 */
package com.github.seqware.queryengine.impl.tuplebinderIO;

import com.github.seqware.queryengine.util.SGID;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * @author boconnor
 * @author dyuen
 *
 */
public class SGIDTB extends TupleBinding {

  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    SGID f = (SGID)object;
    to.writeLong(f.getUuid().getMostSignificantBits());
    to.writeLong(f.getUuid().getLeastSignificantBits());
  }

  @Override
  public Object entryToObject(TupleInput ti) {
      // reset because this InputStream may have been sniffed before this tuple binder was called
      ti.reset();
      SGID sgid = new SGID(ti.readLong(), ti.readLong(), ti.readLong());
      return sgid;
  }

}
