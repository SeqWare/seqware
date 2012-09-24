package net.sourceforge.seqware.common.dao;

import java.io.IOException;
import java.util.List;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;

import org.springframework.web.multipart.MultipartFile;

public interface FileDAO {

    public void insert(File file);

    public void insert(Registration registration, File file);

    public void update(File file);

    public void update(Registration registration, File file);

    public void delete(File file);

    public void deleteAll(List<File> files);

    public void deleteAllWithFolderStore(List<File> list);

    public File findByID(Integer id);

    public File findByPath(String path);

    public java.io.File saveFile(MultipartFile uploadFile, String folderStore, Registration owner) throws IOException;

    public File findBySWAccession(Integer swAccession);

    public File updateDetached(File file);
    
    public File updateDetached(Registration registration, File file);

    public List<File> findByOwnerId(Integer registrationId);

    public List<File> findByCriteria(String criteria, boolean isCaseSens);

    public List<File> list();
}