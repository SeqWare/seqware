package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;

/**
 * <p>FileAttributeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileAttributeDAO {

  /**
   * <p>getAll.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<FileAttribute> getAll();

  /**
   * <p>get.</p>
   *
   * @param file a {@link net.sourceforge.seqware.common.model.File} object.
   * @return a {@link java.util.List} object.
   */
  public List<FileAttribute> get(File file);

  /**
   * <p>get.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.FileAttribute} object.
   */
  public FileAttribute get(Integer id);

  /**
   * <p>add.</p>
   *
   * @param fileAttribute a {@link net.sourceforge.seqware.common.model.FileAttribute} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer add(FileAttribute fileAttribute);

  /**
   * <p>update.</p>
   *
   * @param fileAttribute a {@link net.sourceforge.seqware.common.model.FileAttribute} object.
   */
  public void update(FileAttribute fileAttribute);

  /**
   * <p>delete.</p>
   *
   * @param fileAttribute a {@link net.sourceforge.seqware.common.model.FileAttribute} object.
   */
  public void delete(FileAttribute fileAttribute);

}
