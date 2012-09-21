package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.FileType;

public interface FileTypeDAO {
  public List<FileType> list();

  public FileType findByID(Integer id);

  public FileType updateDetached(FileType fileType);
}
