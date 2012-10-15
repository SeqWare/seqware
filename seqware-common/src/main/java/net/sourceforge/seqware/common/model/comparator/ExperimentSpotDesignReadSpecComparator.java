package net.sourceforge.seqware.common.model.comparator;

import java.util.Comparator;

import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;

/**
 * <p>ExperimentSpotDesignReadSpecComparator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentSpotDesignReadSpecComparator implements Comparator<ExperimentSpotDesignReadSpec> {

  /** {@inheritDoc} */
  @Override
  public int compare(ExperimentSpotDesignReadSpec o1, ExperimentSpotDesignReadSpec o2) {
    if (o1 != null && o2 != null && o1.getReadIndex() != null && o2.getReadIndex() != null) {
      return o1.getReadIndex().compareTo(o2.getReadIndex());
    }
    if (o1 != null && o2 != null) {
      return o1.compareTo(o2);
    }

    return 0;
  }

}
