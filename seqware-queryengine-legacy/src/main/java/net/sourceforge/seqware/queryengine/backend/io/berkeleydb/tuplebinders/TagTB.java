/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.model.Tag;

/**
 * @author boconnor
 *
 */
public class TagTB extends TupleBinding {

  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    Tag tag = (Tag)object;
    to.writeByte(Model.TAG);
    to.writeString(tag.getTag());
    to.writeString(tag.getValue());
  }

  @Override
  public Object entryToObject(TupleInput ti) {
    Tag t = new Tag();
    // reset because this InputStream may have been sniffed before this tuple binder was called
    ti.reset();
    // the object type
    if (ti.readByte() != Model.TAG) { return(null); }
    String tag = ti.readString();
    String value = ti.readString();
    t.setTag(tag);
    t.setValue(value);

    return(t);
  }

}
