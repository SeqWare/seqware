package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.dao.LaneDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.UploadSequence;
import net.sourceforge.seqware.common.module.ReturnValue;

public interface LaneService {

    public static final String NAME = "laneService";

    public void setLaneDAO(LaneDAO laneDAO);

    public void insert(Lane lane);

    public void insert(Registration registration, Lane lane);

    public void update(Lane lane);

    public void update(Registration registration, Lane lane);

    public void delete(Lane lane, String deleteRealFiles);

    public Lane findByName(String name);

    public Lane findByID(Integer laneID);

    public Lane findBySWAccession(Integer swAccession);

    public boolean hasNameBeenUsed(String oldName, String newName);

    public Integer insertLane(Registration registration, Sample sample, UploadSequence uploadSequence, FileType fileType)
            throws Exception;

    public List<File> listFile(Registration reqistration, String typeNode, List<File> list, String[] ids,
            String[] statuses);

    public List<ReturnValue> findFiles(Integer swAccession);

    public List<File> getFiles(Integer laneId);

    public boolean isHasFile(Integer laneId);

    public List<File> getFiles(Integer studyId, String metaType);

    public boolean isHasFile(Integer studyId, String metaType);

    public SortedSet<Lane> setWithHasFile(SortedSet<Lane> list);

    public SortedSet<Lane> listWithHasFile(SortedSet<Lane> list, String metaType);

    public List<Lane> list(List<Integer> laneIds);

    public Lane updateDetached(Lane lane);
    
    public Lane updateDetached(Registration registration, Lane lane);

    public List<Lane> findByOwnerID(Integer registrationId);

    public List<Lane> findByCriteria(String criteria, boolean isCaseSens);

    public List<Lane> list();
}

// ex:sw=4:ts=4:
