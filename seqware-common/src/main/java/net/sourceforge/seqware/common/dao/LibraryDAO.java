package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Sample;

/**
 * <p>LibraryDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LibraryDAO {

  /**
   * Returns library matching given SeqWare Accesssion.
   *
   * @param swAccession
   *          SeqWare Accession.
   * @return Library.
   */
  public Sample findBySWAccession(Long swAccession);

  /**
   * Returns list of libraries that contain the attribute
   * attributeName=attributeValue. The attribute can occur anywhere in the
   * hierarchy, from the library intself all the way up to the root.
   *
   * @param attributeName
   *          Attribute Name.
   * @param attributeValue
   *          Attribute Value.
   * @return List of Libraries.
   */
  public List<Sample> getLibraries(String attributeName, String attributeValue);

  /**
   * Returns a complete list of libraries.
   *
   * @return All libraries.
   */
  public List<Sample> getLibraries();
}
