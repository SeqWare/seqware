package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;

public interface ProcessingDAO {

    /**
     * Inserts a new Processing and returns its sw_accession number.
     *
     * @param processing Processing to be inserted.
     * @return The SeqWare Accession number for the newly inserted Processing.
     */
    public Integer insert(Processing processing);
    public Integer insert(Registration registration,Processing processing);
    public void update(Processing processing);
    
    public void update(Registration registration, Processing processing);

    public void delete(Processing processing);

    public Processing findByFilePath(String filePath);

    public Processing findByID(Integer processingId);

    public List<File> getFiles(Integer processingId);

    public boolean isHasFile(Integer processingId);

    public List<File> getFiles(Integer processingId, String metaType);

    public boolean isHasFile(Integer processingId, String metaType);

    public Processing findBySWAccession(Integer swAccession);

    public Processing updateDetached(Processing processing);
    
    public Processing updateDetached(Registration registration, Processing processing);

    public List<Processing> findByOwnerID(Integer registrationId);

    public List<Processing> findByCriteria(String criteria, boolean isCaseSens);
    
    public List<Processing> list();
}
