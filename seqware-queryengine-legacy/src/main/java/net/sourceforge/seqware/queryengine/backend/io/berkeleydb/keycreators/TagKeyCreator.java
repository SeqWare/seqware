/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.io.berkeleydb.keycreators;

import java.util.Iterator;
import java.util.Set;


import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.TagTB;
import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.model.Tag;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.SecondaryDatabase;
import com.sleepycat.db.SecondaryMultiKeyCreator;


/**
 * <p>TagKeyCreator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
// could alternatively extend SecondaryMultiKeyCreator for multiple read sequence groups
public class TagKeyCreator implements SecondaryMultiKeyCreator {
  
  private TupleBinding binding;
  
  /**
   * <p>Constructor for TagKeyCreator.</p>
   *
   * @param binding a {@link com.sleepycat.bind.tuple.TupleBinding} object.
   */
  public TagKeyCreator(TupleBinding binding) {
    this.binding = binding;
  }
  
  /** {@inheritDoc} */
  public void createSecondaryKeys(SecondaryDatabase secDb,
                                    DatabaseEntry key,
                                    DatabaseEntry value,
                                    Set results) {
    try {
      Model model = (Model) binding.entryToObject(value);
      // tuple binding
      TagTB ttb = new TagTB();
      Tag tag = new Tag();
      
      Iterator<String> tags = model.getTags().keySet().iterator();
      while(tags.hasNext()) {
        String tagStr = tags.next();
        tag.setTag(tagStr);
        tag.setValue(model.getTags().get(tag));
        DatabaseEntry result = new DatabaseEntry();
        ttb.objectToEntry(tag, result);
        results.add(result);
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
