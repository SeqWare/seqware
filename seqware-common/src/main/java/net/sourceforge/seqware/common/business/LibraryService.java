package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.model.Sample;

/**
 * <p>LibraryService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LibraryService {

  /**
   * Returns a complete list of libraries. A library is a particular kind of
   * Sample that has been run or is eligible to run on a sequencer.
   *
   * @return All libraries.
   */
  public List<Sample> getLibraries();

  /**
   * Returns a list of libraries that match all attributes provided in the give
   * attributes Map. Attributes may occur anywhere in the hierarchy, from the
   * library itself all the way to the root.
   *
   * @param attributes
   *          A Map of attributes.
   * @return A list of libraries.
   */
  public List<Sample> getLibraries(Map<String, String> attributes);

  /**
   * Returns library matching given SeqWare Accession.
   *
   * @param swAccession
   *          SeqWare Accession.
   * @return Library
   */
  public Sample findBySWAccession(Long swAccession);

}
