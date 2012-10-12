package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>FileService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileService {

  /** Constant <code>NAME="fileService"</code> */
  public static final String NAME = "fileService";

  /**
   * <p>setFileDAO.</p>
   *
   * @param fileDAO a {@link net.sourceforge.seqware.common.dao.FileDAO} object.
   */
  public void setFileDAO(FileDAO fileDAO);

  /**
   * <p>insert.</p>
   *
   * @param file a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public void insert(File file);

  /**
   * <p>insert.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param file a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public void insert(Registration registration, File file);

  /**
   * <p>update.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param file a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public void update(Registration registration, File file);

  /**
   * <p>update.</p>
   *
   * @param file a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public void update(File file);

  /**
   * <p>delete.</p>
   *
   * @param file a {@link net.sourceforge.seqware.common.model.File} object.
   * @param deleteRealFiles a {@link java.lang.String} object.
   */
  public void delete(File file, String deleteRealFiles);

  /**
   * <p>deleteAll.</p>
   *
   * @param file a {@link java.util.List} object.
   * @param deleteRealFiles a {@link java.lang.String} object.
   */
  public void deleteAll(List<File> file, String deleteRealFiles);

  /**
   * <p>findByID.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public File findByID(Integer id);

  /**
   * <p>findByPath.</p>
   *
   * @param path a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public File findByPath(String path);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public File findBySWAccession(Integer swAccession);

  /**
   * <p>findByOwnerId.</p>
   *
   * @param registrationId a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<File> findByOwnerId(Integer registrationId);

  /**
   * <p>getFiles.</p>
   *
   * @param fileId a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<File> getFiles(Integer fileId);

  /**
   * <p>getFiles.</p>
   *
   * @param fileId a {@link java.lang.Integer} object.
   * @param metaType a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<File> getFiles(Integer fileId, String metaType);

  /**
   * <p>setWithHasFile.</p>
   *
   * @param list a {@link java.util.Set} object.
   * @param metaType a {@link java.lang.String} object.
   * @return a {@link java.util.Set} object.
   */
  public Set<File> setWithHasFile(Set<File> list, String metaType);

  /**
   * <p>isExists.</p>
   *
   * @param fileName a {@link java.lang.String} object.
   * @param folderStore a {@link java.lang.String} object.
   * @return a boolean.
   */
  public boolean isExists(String fileName, String folderStore);

  /**
   * <p>updateDetached.</p>
   *
   * @param file a {@link net.sourceforge.seqware.common.model.File} object.
   * @return a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public File updateDetached(File file);

  /**
   * <p>updateDetached.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param file a {@link net.sourceforge.seqware.common.model.File} object.
   * @return a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public File updateDetached(Registration registration, File file);

  /**
   * <p>findByCriteria.</p>
   *
   * @param criteria a {@link java.lang.String} object.
   * @param isCaseSens a boolean.
   * @return a {@link java.util.List} object.
   */
  public List<File> findByCriteria(String criteria, boolean isCaseSens);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<File> list();
}
