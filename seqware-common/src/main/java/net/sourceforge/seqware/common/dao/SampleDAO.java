package net.sourceforge.seqware.common.dao;

import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;

public interface SampleDAO {

    public Integer insert(Sample sample);

    public Integer insert(Registration registration, Sample sample);

    public void update(Sample sample);

    public void update(Registration registration, Sample sample);

    public void delete(Sample sample);

    public List<Sample> listComplete();

    public List<Sample> listIncomplete();

    public Sample findByName(String name);

    public Sample findByTitle(String title);

    public Sample findByID(Integer sampleId);

    public List<Sample> listSample(Registration registration);

    public List<File> getFiles(Integer sampleId);

    public List<ReturnValue> findFiles(Integer swAccession);

    public boolean isHasFile(Integer sampleId);

    public List<File> getFiles(Integer sampleId, String metaType);

    public boolean isHasFile(Integer sampleId, String metaType);

    public Map<Integer, Integer> getCountFiles(Integer expId);

    public Map<Integer, Integer> getCountFiles(Integer expId, String metaType);

    public Sample findBySWAccession(Integer swAccession);

    public Sample updateDetached(Sample sample);

    public Sample updateDetached(Registration registration, Sample sample);

    public List<Sample> findByOwnerID(Integer registrationId);

    public List<Sample> findByCriteria(String criteria, boolean isCaseSens);

    public List<Sample> getRootSamples(Study study);

    public Sample getRootSample(Sample sample);

    public List<Sample> list();
}
