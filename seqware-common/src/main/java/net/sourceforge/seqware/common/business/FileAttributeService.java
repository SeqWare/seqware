package net.sourceforge.seqware.common.business;

import java.util.Set;

import net.sourceforge.seqware.common.model.FileAttribute;

/**
 * <p>FileAttributeService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileAttributeService {

  /**
   * Returns a complete set of attributes for a give File.
   *
   * @param fileSwa
   *          File SeqWare Accession attributes are associated with.
   * @return All attributes for given File SeqWare Accession.
   */
  public Set<FileAttribute> getFileAttributes(Integer fileSwa);

  /**
   * <p>get.</p>
   *
   * @param fileSwa a {@link java.lang.Integer} object.
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.FileAttribute} object.
   */
  public FileAttribute get(Integer fileSwa, Integer id);

  /**
   * <p>add.</p>
   *
   * @param fileSwa a {@link java.lang.Integer} object.
   * @param fileAttribute a {@link net.sourceforge.seqware.common.model.FileAttribute} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer add(Integer fileSwa, FileAttribute fileAttribute);

  /**
   * <p>update.</p>
   *
   * @param fileSwa a {@link java.lang.Integer} object.
   * @param fileAttribute a {@link net.sourceforge.seqware.common.model.FileAttribute} object.
   */
  public void update(Integer fileSwa, FileAttribute fileAttribute);

  /**
   * <p>delete.</p>
   *
   * @param fileSwa a {@link java.lang.Integer} object.
   * @param fileAttributeId a {@link java.lang.Integer} object.
   */
  public void delete(Integer fileSwa, Integer fileAttributeId);

}
