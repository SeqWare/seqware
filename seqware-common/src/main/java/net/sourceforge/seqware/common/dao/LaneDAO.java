package net.sourceforge.seqware.common.dao;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.module.ReturnValue;

public interface LaneDAO {

    public void insert(Lane lane);

    public void insert(Registration registration, Lane lane);

    public void update(Lane lane);

    public void update(Registration registration, Lane lane);

    public void delete(Lane lane);

    public void delete(SortedSet<Lane> lanes);

    public Lane findByName(String name);

    public Lane findByID(Integer laneId);

    public List<File> getFiles(Integer laneId);

    public boolean isHasFile(Integer laneId);

    public List<File> getFiles(Integer studyId, String metaType);

    public boolean isHasFile(Integer studyId, String metaType);

    public List<Lane> list(List<Integer> laneIds);

    public Lane findBySWAccession(Integer swAccession);

    public Lane updateDetached(Lane lane);
    
    public Lane updateDetached(Registration registration, Lane lane);

    public List<Lane> findByOwnerID(Integer registrationId);

    public List<Lane> findByCriteria(String criteria, boolean isCaseSens);

    public List<ReturnValue> findFiles(Integer swAccession);

    public List<Lane> list();
}