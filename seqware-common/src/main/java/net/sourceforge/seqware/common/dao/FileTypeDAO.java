package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.FileType;

/**
 * <p>FileTypeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileTypeDAO {
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<FileType> list();

  /**
   * <p>findByID.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.FileType} object.
   */
  public FileType findByID(Integer id);

  /**
   * <p>updateDetached.</p>
   *
   * @param fileType a {@link net.sourceforge.seqware.common.model.FileType} object.
   * @return a {@link net.sourceforge.seqware.common.model.FileType} object.
   */
  public FileType updateDetached(FileType fileType);
}
