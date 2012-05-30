/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders;

import java.util.HashMap;
import java.util.Iterator;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Model;

/**
 * @author boconnor
 *
 * FIXME: this may need full tag support at some point
 * 
 */
public class ConsequenceTB extends TupleBinding {

  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    Consequence c = (Consequence)object;
    to.writeByte(Model.CONSEQUENCE);
	  to.writeString(c.getId());
	  to.writeString(c.getMismatchId());
	  to.writeString(c.getGeneId());
	  to.writeString(c.getGeneChr());
	  to.writeInt(c.getCodingStart());
	  to.writeInt(c.getCodingStop());
	  to.writeByte(c.getStrand());
	  to.writeByte(c.getMismatchType());
	  to.writeString(c.getMismatchChr());
	  to.writeInt(c.getMismatchStart());
	  to.writeInt(c.getMismatchStop());
	  to.writeInt(c.getMismatchCodonPosition());
	  to.writeString(c.getMismatchCodonChange());
	  to.writeString(c.getMismatchAminoAcidChange());
	  to.writeFloat(c.getMismatchAAChangeBlosumScore());
	  to.writeString(c.getGenomicSequence());
	  to.writeString(c.getMutatedGenomicSequence());
	  to.writeString(c.getTranslatedSequence());
	  to.writeString(c.getMutatedTranslatedSequence());
	  
    // now deal with tags
    HashMap <String,String>tags = c.getTags();
    Iterator tagIt = tags.keySet().iterator();
    to.writeInt(tags.size());
    while(tagIt.hasNext()) {
      String tag = (String) tagIt.next();
      String value = tags.get(tag);
      to.writeString(tag);
      to.writeString(value);
    }
   
  }

  @Override
  public Object entryToObject(TupleInput ti) {
      Consequence c = new Consequence();
      // reset because this InputStream may have been sniffed before this tuple binder was called
      ti.reset();
      // the object type
      if (ti.readByte() != Model.CONSEQUENCE) { return(null); }
      c.setId(ti.readString());
      c.setMismatchId(ti.readString());
      c.setGeneId(ti.readString());
      c.setGeneChr(ti.readString());
      c.setCodingStart(ti.readInt());
      c.setCodingStop(ti.readInt());
      c.setStrand(ti.readByte());
      c.setMismatchType(ti.readByte());
      c.setMismatchChr(ti.readString());
      c.setMismatchStart(ti.readInt());
      c.setMismatchStop(ti.readInt());
      c.setMismatchCodonPosition(ti.readInt());
      c.setMismatchCodonChange(ti.readString());
      c.setMismatchAminoAcidChange(ti.readString());
      c.setMismatchAAChangeBlosumScore(ti.readFloat());
      c.setGenomicSequence(ti.readString());
      c.setMutatedGenomicSequence(ti.readString());
      c.setTranslatedSequence(ti.readString());
      c.setMutatedTranslatedSequence(ti.readString());
      
      // now deal with tags
      int tagCount = ti.readInt();
      for(int i=0; i<tagCount; i++) {
          String tag = ti.readString();
          String value = ti.readString();
          c.getTags().put(tag, value);
      }
      
      return(c);
  }

}
