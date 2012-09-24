package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.dao.SampleDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;

public interface SampleService {

    public static final String NAME = "sampleService";

    public void setSampleDAO(SampleDAO sampleDAO);

    public Integer insert(Sample sample);

    public Integer insert(Registration registration, Sample sample);

    public void update(Sample sample);

    public void update(Registration registration, Sample sample);

    public void delete(Sample sample, String deleteRealFiles);

    public Sample findByName(String name);

    public Sample findByTitle(String title);

    public Sample findByID(Integer id);

    public Sample findBySWAccession(Integer swAccession);

    public List<Sample> listComplete();

    public List<Sample> listIncomplete();

    public boolean hasNameBeenUsed(String oldName, String newName);

    public List<Sample> listSample(Registration registration);

    public List<File> getFiles(Integer sampleId);

    public boolean isHasFile(Integer sampleId);

    public List<File> getFiles(Integer studyId, String metaType);

    public List<ReturnValue> findFiles(Integer swAccession);

    public boolean isHasFile(Integer studyId, String metaType);

    public SortedSet<Sample> setWithHasFile(Integer expId, SortedSet<Sample> list);

    public SortedSet<Sample> listWithHasFile(Integer expId, SortedSet<Sample> list, String metaType);

    public Sample updateDetached(Sample sample);
    
    public Sample updateDetached(Registration registration, Sample sample);

    public List<Sample> findByOwnerID(Integer registrationId);

    public List<Sample> findByCriteria(String criteria, boolean isCaseSens);

    public List<Sample> getRootSamples(Study study);

    public Sample getRootSample(Sample childSample);

    public List<Sample> list();
}

// ex:sw=4:ts=4:
