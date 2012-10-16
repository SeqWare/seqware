package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.FileTypeDAO;
import net.sourceforge.seqware.common.model.FileType;

/**
 * <p>FileTypeService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileTypeService {
  /** Constant <code>NAME="FileTypeService"</code> */
  public static final String NAME = "FileTypeService";

  /**
   * <p>setFileTypeDAO.</p>
   *
   * @param fileTypeDAO a {@link net.sourceforge.seqware.common.dao.FileTypeDAO} object.
   */
  public void setFileTypeDAO(FileTypeDAO fileTypeDAO);

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
  FileType updateDetached(FileType fileType);
}
