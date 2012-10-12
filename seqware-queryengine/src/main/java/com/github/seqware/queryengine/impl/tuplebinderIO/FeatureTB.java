/**
 * 
 */
package com.github.seqware.queryengine.impl.tuplebinderIO;

import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.Feature.Builder;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * <p>FeatureTB class.</p>
 *
 * @author boconnor
 * @author dyuen
 * @version $Id: $Id
 */
public class FeatureTB extends TupleBinding {

  /** {@inheritDoc} */
  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    Feature f = (Feature)object;
    to.writeLong(f.getSGID().getUuid().getMostSignificantBits());
    to.writeLong(f.getSGID().getUuid().getLeastSignificantBits());
    to.writeLong(f.getTimestamp().getTime());
    to.writeString(f.getSGID().getFriendlyRowKey());
    //to.writeLong(f.getPrecedingSGID().getUuid().getMostSignificantBits());
    //to.writeLong(f.getPrecedingSGID().getUuid().getLeastSignificantBits());
    to.writeString(f.getPragma());
    to.writeString(f.getSource());
    to.writeString(f.getType());
    to.writeDouble(f.getScore());
    to.writeString(f.getPhase());
    to.writeString(f.getSeqid());
    to.writeLong(f.getStart());
    to.writeLong(f.getStop());
    to.writeInt(f.getStrand().ordinal());
    // now deal with tags
    SeqWareIterable<Tag> tags = f.getTags();
    to.writeLong(tags.getCount());
    for(Tag t : tags) {
      String tag = (String) t.getKey();
      //String value = t.getValue();
      to.writeString(tag);
      //to.writeString(value);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Object entryToObject(TupleInput ti) {
      Builder newBuilder = Feature.newBuilder();
      // reset because this InputStream may have been sniffed before this tuple binder was called
      ti.reset();
      SGID sgid = new SGID(ti.readLong(), ti.readLong(), ti.readLong(), ti.readString());
      SGID oldSgid = null; //new SGID(ti.readLong(), ti.readLong());
      newBuilder.setPragma(ti.readString()).setSource(ti.readString()).setType(ti.readString()).setScore(ti.readDouble());
      newBuilder.setPhase(ti.readString()).setSeqid(ti.readString()).setStart(ti.readLong()).setStop(ti.readLong());
      newBuilder.setStrand(Feature.Strand.values()[ti.readInt()]);
      Feature f =  newBuilder.build();
      for(int i = 0; i < ti.readLong(); i++){
          f.associateTag(Tag.newBuilder().setKey(ti.readString()).build());
      }
      f.impersonate(sgid, oldSgid);
      return f;
  }

}
