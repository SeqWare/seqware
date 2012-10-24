/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import net.sourceforge.seqware.queryengine.backend.model.ContigPosition;
import net.sourceforge.seqware.queryengine.backend.model.Model;

/**
 * <p>ContigPositionTB class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ContigPositionTB extends TupleBinding {

  /** {@inheritDoc} */
  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    ContigPosition ag = (ContigPosition)object;
    to.writeByte(Model.CONTIGPOSITION);
    to.writeString(ag.getContig());
    to.writeInt(ag.getStartPosition());
    to.writeInt(ag.getStopPosition());
  }

  /** {@inheritDoc} */
  @Override
  public Object entryToObject(TupleInput ti) {
    // reset because this InputStream may have been sniffed before this tuple binder was called
    ti.reset();
    // the object type
    if (ti.readByte() != Model.CONTIGPOSITION) { return(null); }
    
    String contig = ti.readString();
    int startPosition = ti.readInt();
    int stopPosition = ti.readInt();
    
    ContigPosition ag = new ContigPosition();
    ag.setContig(contig);
    ag.setStartPosition(startPosition);
    ag.setStopPosition(stopPosition);
    
    return(ag);
  }

}
