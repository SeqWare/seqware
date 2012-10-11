package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;

public interface FileService {

  public static final String NAME = "fileService";

  public void setFileDAO(FileDAO fileDAO);

  public void insert(File file);

  public void insert(Registration registration, File file);

  public void update(Registration registration, File file);

  public void update(File file);

  public void delete(File file, String deleteRealFiles);

  public void deleteAll(List<File> file, String deleteRealFiles);

  public File findByID(Integer id);

  public File findByPath(String path);

  public File findBySWAccession(Integer swAccession);

  public List<File> findByOwnerId(Integer registrationId);

  public List<File> getFiles(Integer fileId);

  public List<File> getFiles(Integer fileId, String metaType);

  public Set<File> setWithHasFile(Set<File> list, String metaType);

  public boolean isExists(String fileName, String folderStore);

  public File updateDetached(File file);

  public File updateDetached(Registration registration, File file);

  public List<File> findByCriteria(String criteria, boolean isCaseSens);

  public List<File> list();
}
