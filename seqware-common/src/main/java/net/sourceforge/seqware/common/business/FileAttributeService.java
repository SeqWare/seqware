package net.sourceforge.seqware.common.business;

import java.util.Set;

import net.sourceforge.seqware.common.model.FileAttribute;

public interface FileAttributeService {

  /**
   * Returns a complete set of attributes for a give File.
   * 
   * @param fileSwa
   *          File SeqWare Accession attributes are associated with.
   * @return All attributes for given File SeqWare Accession.
   */
  public Set<FileAttribute> getFileAttributes(Integer fileSwa);

  public FileAttribute get(Integer fileSwa, Integer id);

  public Integer add(Integer fileSwa, FileAttribute fileAttribute);

  public void update(Integer fileSwa, FileAttribute fileAttribute);

  public void delete(Integer fileSwa, Integer fileAttributeId);

}
