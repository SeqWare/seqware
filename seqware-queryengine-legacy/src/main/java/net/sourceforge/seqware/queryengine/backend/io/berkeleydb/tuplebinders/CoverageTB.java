/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders;

import java.util.Iterator;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Model;

/**
 * @author boconnor
 * 
 */
public class CoverageTB extends TupleBinding {

  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    Coverage c = (Coverage)object;
    to.writeByte(Model.COVERAGE);
    Iterator <Integer> it = c.getCoverage().keySet().iterator();
    int count = c.getCoverage().keySet().size();
    to.writeString(c.getId());
    to.writeString(c.getContig());
    to.writeInt(c.getStartPosition());
    to.writeInt(c.getStopPosition());
    // TODO: may want to add tags too
    // write out the number of positions with size
    to.writeInt(count);
    to.writeInt(c.getSum());
    while(it.hasNext()) {
    	Integer position = it.next();
    	Integer coverage = c.getCoverage(position);
    	to.writeInt(position);
    	to.writeInt(coverage);
    	//System.out.println("Writing coverage out to disk "+c.getContig()+" "+position+" "+coverage);
    }

  }

  @Override
  public Object entryToObject(TupleInput ti) {
      Coverage c = new Coverage();
      // reset because this InputStream may have been sniffed before this tuple binder was called
      ti.reset();
      // the object type
      if (ti.readByte() != Model.COVERAGE) { return(null); }
      c.setId(ti.readString());
      c.setContig(ti.readString());
      c.setStartPosition(ti.readInt());
      c.setStopPosition(ti.readInt());
      c.setCount(ti.readInt());
      c.setSum(ti.readInt());
      for(int i=0; i<c.getCount(); i++) {
    	  int position = ti.readInt();
    	  int coverage = ti.readInt();
    	  c.putCoverage(new Integer(position), new Integer(coverage));
    	  //System.out.println("Reading coverage from disk "+c.getContig()+" "+position+" "+coverage);
      }
      
      return(c);
  }

}
