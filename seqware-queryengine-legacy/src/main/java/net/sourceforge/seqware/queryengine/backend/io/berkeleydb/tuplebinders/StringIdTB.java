/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.model.StringId;

/**
 * <p>StringIdTB class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StringIdTB extends TupleBinding {

  /** {@inheritDoc} */
  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    StringId stringId = (StringId)object;
    to.writeByte(Model.STRINGID);
    to.writeString(stringId.getId());
  }

  /** {@inheritDoc} */
  @Override
  public Object entryToObject(TupleInput ti) {
    // reset because this InputStream may have been sniffed before this tuple binder was called
    ti.reset();
    // the object type
    if (ti.readByte() != Model.STRINGID) { return(null); }
    
    String id = ti.readString();
    
    StringId stringId = new StringId();
    stringId.setId(id);
    
    return(stringId);
  }

}
