package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.FileTypeDAO;
import net.sourceforge.seqware.common.model.FileType;

public interface FileTypeService {
  public static final String NAME = "FileTypeService";

  public void setFileTypeDAO(FileTypeDAO fileTypeDAO);

  public List<FileType> list();

  public FileType findByID(Integer id);

  FileType updateDetached(FileType fileType);
}
