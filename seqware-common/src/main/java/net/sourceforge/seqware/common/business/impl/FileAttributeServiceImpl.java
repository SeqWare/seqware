package net.sourceforge.seqware.common.business.impl;

import java.util.Set;

import net.sourceforge.seqware.common.business.FileAttributeService;
import net.sourceforge.seqware.common.dao.FileAttributeDAO;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.err.NotFoundException;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * <p>FileAttributeServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Transactional
public class FileAttributeServiceImpl implements FileAttributeService {

  @Autowired
  private FileAttributeDAO fileAttributeDao;

  @Autowired
  private FileDAO fileDao;

  /** {@inheritDoc} */
  @Override
  public Set<FileAttribute> getFileAttributes(Integer fileSwa) {
    File file = fileDao.findBySWAccession(fileSwa);
    if (file == null) {
      throw new NotFoundException();
    }
    return file.getFileAttributes();
  }

  /** {@inheritDoc} */
  @Override
  public FileAttribute get(Integer fileSwa, Integer id) {
    File file = fileDao.findBySWAccession(fileSwa);
    if (file == null) {
      return null;
    }
    return fileAttributeDao.get(id);
  }

  /** {@inheritDoc} */
  @Override
  public Integer add(Integer fileSwa, FileAttribute fileAttribute) {
    File file = fileDao.findBySWAccession(fileSwa);
    if (file == null) {
      return null;
    }
    fileAttribute.setFile(file);
    return fileAttributeDao.add(fileAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void update(Integer fileSwa, FileAttribute fileAttribute) {
    File file = fileDao.findBySWAccession(fileSwa);
    if (file == null) {
      throw new NotFoundException();
    }
    FileAttribute currentFileAttribute = fileAttributeDao.get(fileAttribute.getFileAttributeId());
    if (currentFileAttribute == null) {
      throw new NotFoundException();
    }
    if (!currentFileAttribute.getFile().equals(file)) {
      throw new NotFoundException();
    }
    fileAttribute.setFile(file);
    fileAttributeDao.update(fileAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(Integer fileSwa, Integer fileAttributeId) {
    File file = fileDao.findBySWAccession(fileSwa);
    if (file == null) {
      throw new NotFoundException();
    }
    FileAttribute fileAttribute = fileAttributeDao.get(fileAttributeId);
    if (fileAttribute == null) {
      throw new NotFoundException();
    }
    if (!fileAttribute.getFile().equals(file)) {
      throw new NotFoundException();
    }
    fileAttributeDao.delete(fileAttribute);
  }

}
