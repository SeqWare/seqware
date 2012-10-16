/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders;

import java.util.HashMap;
import java.util.Iterator;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.model.Model;

/**
 * <p>FeatureTB class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FeatureTB extends TupleBinding {

  /** {@inheritDoc} */
  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    Feature f = (Feature)object;
    to.writeByte(Model.FEATURE);
    to.writeString(f.getId());
    to.writeString(f.getContig());
    to.writeInt(f.getStartPosition());
    to.writeInt(f.getStopPosition());
    to.writeString(f.getName());
    to.writeInt(f.getScore());
    to.writeChar(f.getStrand());
    to.writeInt(f.getThickStart());
    to.writeInt(f.getThickEnd());
    to.writeString(f.getItemRgb());
    to.writeInt(f.getBlockCount());
    int[] blockSizes = f.getBlockSizes();
    int[] blockStarts = f.getBlockStarts();
    if (blockSizes.length != f.getBlockCount() || blockStarts.length != f.getBlockCount()) {
      System.err.println("block count "+f.getBlockCount()+" doesn't match blockStarts ("+f.getBlockStarts().length+") " +
      		"or blockSizes ("+f.getBlockSizes().length+")!");
    }
    for (int i=0; i<f.getBlockCount(); i++) {
      to.writeInt(blockSizes[i]);
    }
    for (int i=0; i<f.getBlockCount(); i++) {
      to.writeInt(blockStarts[i]);
    }
    
    // GFF-specific
    to.writeString(f.getSource());
    to.writeString(f.getFeature());
    if (f.getFrame() < 0 || f.getFrame() > 2) {
      System.err.println("frame should be 0, 1, or 2 not "+f.getFrame()+"!");
    }
    to.writeByte(f.getFrame());
    to.writeString(f.getGroup());
    
    // GTF-specific
    to.writeString(f.getGeneId());
    to.writeString(f.getTranscriptId());

    // now deal with tags
    HashMap <String,String>tags = f.getTags();
    Iterator tagIt = tags.keySet().iterator();
    to.writeInt(tags.size());
    while(tagIt.hasNext()) {
      String tag = (String) tagIt.next();
      String value = tags.get(tag);
      to.writeString(tag);
      to.writeString(value);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Object entryToObject(TupleInput ti) {
      Feature f = new Feature();
      // reset because this InputStream may have been sniffed before this tuple binder was called
      ti.reset();
      // the object type
      if (ti.readByte() != Model.FEATURE) { return(null); }
      f.setId(ti.readString());
      f.setContig(ti.readString());
      f.setStartPosition(ti.readInt());
      f.setStopPosition(ti.readInt());
      f.setName(ti.readString());
      f.setScore(ti.readInt());
      f.setStrand(ti.readChar());
      f.setThickStart(ti.readInt());
      f.setThickEnd(ti.readInt());
      f.setItemRgb(ti.readString());
      f.setBlockCount(ti.readInt());
      int[] blockSizes = new int[f.getBlockCount()];
      int[] blockStarts = new int[f.getBlockCount()];
      for (int i=0; i<f.getBlockCount(); i++) {
        blockSizes[i] = ti.readInt();
      }
      for (int i=0; i<f.getBlockCount(); i++) {
        blockStarts[i] = ti.readInt();
      }
      f.setBlockSizes(blockSizes);
      f.setBlockStarts(blockStarts);
      f.setSource(ti.readString());
      f.setFeature(ti.readString());
      f.setFrame(ti.readByte());
      f.setGroup(ti.readString());
      f.setGeneId(ti.readString());
      f.setTranscriptId(ti.readString());
      
      int tagCount = ti.readInt();
      for (int i=0; i<tagCount; i++) {
        String tag = ti.readString();
        String value = ti.readString();
        f.getTags().put(tag, value);
      }
      return(f);
  }

}
