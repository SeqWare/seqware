package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;

public interface FileAttributeDAO {

  public List<FileAttribute> getAll();

  public List<FileAttribute> get(File file);

  public FileAttribute get(Integer id);

  public Integer add(FileAttribute fileAttribute);

  public void update(FileAttribute fileAttribute);

  public void delete(FileAttribute fileAttribute);

}
