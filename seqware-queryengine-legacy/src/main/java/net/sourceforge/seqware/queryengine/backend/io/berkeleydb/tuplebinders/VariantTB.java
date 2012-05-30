/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders;

import java.util.HashMap;
import java.util.Iterator;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.model.Variant;

/**
 * @author boconnor
 *
 */
public class VariantTB extends TupleBinding {

  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    Variant m = (Variant)object;
    to.writeByte(Model.VARIENT);
    // tells you what type of records this is: 0=snv, 1=indel
    to.writeString(m.getId());
    to.writeByte(m.getType());
    to.writeString(m.getContig());
    to.writeInt(m.getStartPosition());
    to.writeInt(m.getStopPosition());
    to.writeString(m.getReferenceBase());
    to.writeString(m.getConsensusBase());
    to.writeFloat(m.getReferenceCallQuality());
    to.writeFloat(m.getConsensusCallQuality());
    to.writeFloat(m.getMaximumMappingQuality());
    to.writeInt(m.getReadCount());
    to.writeString(m.getReadBases());
    to.writeString(m.getBaseQualities());
    to.writeString(m.getCalledBase());
    to.writeInt(m.getCalledBaseCount());
    to.writeInt(m.getCalledBaseCountForward());
    to.writeInt(m.getCalledBaseCountReverse());
    to.writeByte(m.getZygosity());
    // SNV-specific
    to.writeFloat(m.getReferenceMaxSeqQuality());
    to.writeFloat(m.getReferenceAveSeqQuality());
    to.writeFloat(m.getConsensusMaxSeqQuality());
    to.writeFloat(m.getConsensusAveSeqQuality());
    // indel-specific    
    to.writeString(m.getCallOne());
    to.writeString(m.getCallTwo());
    to.writeInt(m.getReadsSupportingCallOne());
    to.writeInt(m.getReadsSupportingCallTwo());
    to.writeInt(m.getReadsSupportingCallThree());
    // SV-specific
    to.writeByte(m.getSvType());
    to.writeByte(m.getRelativeLocation());
    // translocation-specific
    to.writeByte(m.getTranslocationType());
    to.writeString(m.getTranslocationDestinationContig());
    to.writeInt(m.getTranslocationDestinationStartPosition());
    to.writeInt(m.getTranslocationDestinationStopPosition());
    // now deal with tags
    HashMap <String,String>tags = m.getTags();
    Iterator tagIt = tags.keySet().iterator();
    int tagCount = tags.size();
    to.writeInt(tagCount);
    while(tagIt.hasNext()) {
      String tag = (String) tagIt.next();
      String value = tags.get(tag);
      to.writeString(tag);
      to.writeString(value);
    }
  }

  @Override
  public Object entryToObject(TupleInput ti) {
      Variant m = new Variant();
      // reset because this InputStream may have been sniffed before this tuple binder was called
      ti.reset();
      // the object type
      if (ti.readByte() != Model.VARIENT) { return(null); }
      m.setId(ti.readString());
      m.setType(ti.readByte());
      m.setContig(ti.readString());
      m.setStartPosition(ti.readInt());
      m.setStopPosition(ti.readInt());
      m.setReferenceBase(ti.readString());
      m.setConsensusBase(ti.readString());
      m.setReferenceCallQuality(ti.readFloat());
      m.setConsensusCallQuality(ti.readFloat());
      m.setMaximumMappingQuality(ti.readFloat());
      m.setReadCount(ti.readInt());
      m.setReadBases(ti.readString());
      m.setBaseQualities(ti.readString());
      m.setCalledBase(ti.readString());
      m.setCalledBaseCount(ti.readInt());
      m.setCalledBaseCountForward(ti.readInt());
      m.setCalledBaseCountReverse(ti.readInt());
      m.setZygosity(ti.readByte());
      // SNV-specific
      m.setReferenceMaxSeqQuality(ti.readFloat());
      m.setReferenceAveSeqQuality(ti.readFloat());
      m.setConsensusMaxSeqQuality(ti.readFloat());
      m.setConsensusAveSeqQuality(ti.readFloat());
      // Indel-specific
      m.setCallOne(ti.readString());
      m.setCallTwo(ti.readString());
      m.setReadsSupportingCallOne(ti.readInt());
      m.setReadsSupportingCallTwo(ti.readInt());
      m.setReadsSupportingCallThree(ti.readInt());
      // SV-specific
      m.setSvType(ti.readByte());
      m.setRelativeLocation(ti.readByte());
      // translocation-specific
      m.setTranslocationType(ti.readByte());
      m.setTranslocationDestinationContig(ti.readString());
      m.setTranslocationDestinationStartPosition(ti.readInt());
      m.setTranslocationDestinationStopPosition(ti.readInt());
      // now deal with tags
      int tagCount = ti.readInt();
      for(int i=0; i<tagCount; i++) {
          String tag = ti.readString();
          String value = ti.readString();
          m.addTag(tag, value);
      }
      return(m);
  }

}
