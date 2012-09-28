package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sourceforge.seqware.common.model.adapters.XmlizeIUSSortedSet;
import net.sourceforge.seqware.common.model.adapters.XmlizeLaneSortedSet;
import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

@XmlRootElement
public class Sample implements Serializable, Comparable<Sample>, PermissionsAware {

  private static final long serialVersionUID = 3681367228115990568L;
  private Integer sampleId;
  private Experiment experiment;
  private Registration owner;
  private String anonymizedName;
  private String individualName;
  private Integer swAccession;
  private String name;
  private String title;
  private String alias;
  private String description;
  private String type;
  private Organism organism;
  private String tags;
  private String adapters;
  private String regions;
  private Integer expectedNumRuns;
  private Integer expectedNumSpots;
  private Integer expectedNumReads;
  private Boolean skip;
  private Date createTimestamp;
  private Date updateTimestamp;
  private Boolean isSelected = false;
  private Boolean isHasFile = false;
  private Integer countFile;
  // addition form fields
  private String strExpectedNumRuns;
  private String strExpectedNumReads;
  private SortedSet<Lane> lanes = new TreeSet<Lane>();
  private SortedSet<IUS> ius = new TreeSet<IUS>();
  private Set<Processing> processings = new TreeSet<Processing>();
  private Set<Sample> parents = new TreeSet<Sample>();
  private Set<Sample> children = new TreeSet<Sample>();
  private Set<SampleAttribute> sampleAttributes = new TreeSet<SampleAttribute>();
  private Set<SampleLink> sampleLinks = new TreeSet<SampleLink>();
  // non-persisted field to store organism_id
  private Integer organismId;

  public Sample() {
    super();
    lanes = new TreeSet<Lane>();
    for (IUS i : ius) {
      if (i.getLane() != null) {
        lanes.add(i.getLane());
      }
    }
  }

  @Override
  public int compareTo(Sample that) {
    if (that == null) {
      return -1;
    }

    if (that.getSampleId() == this.getSampleId()) // when both names are null
    {
      return 0;
    }

    if (that.getSampleId() == null) {
      return -1; // when only the other name is null
    }
    return (that.getSampleId().compareTo(this.getSampleId()));
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("sampleId", getSampleId()).toString();
  }

  @Override
  public boolean equals(Object other) {
    if ((this == other)) {
      return true;
    }
    if (!(other instanceof Sample)) {
      return false;
    }
    Sample castOther = (Sample) other;
    return new EqualsBuilder().append(this.getSampleId(), castOther.getSampleId()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getSampleId()).toHashCode();
  }

  /*
   * public int compareTo(Sample that) { if(that == null) return -1;
   * 
   * if(that.getSwAccession() == this.getSwAccession()) // when both names are
   * null return 0;
   * 
   * if(that.getSwAccession() == null) return -1; // when only the other name is
   * null
   * 
   * return(that.getSwAccession().compareTo(this.getSwAccession())); }
   * 
   * public String toString() { return new ToStringBuilder(this)
   * .append("sampleId", getSampleId()) .toString(); }
   * 
   * public boolean equals(Object other) { if ( (this == other ) ) return true;
   * if ( !(other instanceof Sample) ) return false; Sample castOther = (Sample)
   * other; return new EqualsBuilder() .append(this.getSwAccession(),
   * castOther.getSwAccession()) .isEquals(); }
   * 
   * public int hashCode() { return new HashCodeBuilder()
   * .append(getSwAccession()) .toHashCode(); }
   */
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Integer getOrganismId() {
    return organismId;
  }

  public void setOrganismId(Integer organismId) {
    this.organismId = organismId;
  }

  public String getAdapters() {
    return adapters;
  }

  public void setAdapters(String adapters) {
    this.adapters = adapters;
  }

  public String getIndividualName() {
    return individualName;
  }

  public void setIndividualName(String individualName) {
    this.individualName = individualName;
  }

  public String getTitle() {
    return title;
  }

  public String getJsonEscapeTitle() {
    return JsonUtil.forJSON(title);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getExpectedNumRuns() {
    return expectedNumRuns;
  }

  public void setExpectedNumRuns(Integer expectedNumRuns) {
    if (expectedNumRuns != null) {
      this.strExpectedNumRuns = expectedNumRuns.toString();
    }
    this.expectedNumRuns = expectedNumRuns;
  }

  public Integer getExpectedNumSpots() {
    return expectedNumSpots;
  }

  public void setExpectedNumSpots(Integer expectedNumSpots) {
    this.expectedNumSpots = expectedNumSpots;
  }

  public Integer getExpectedNumReads() {
    return expectedNumReads;
  }

  public void setExpectedNumReads(Integer expectedNumReads) {
    if (expectedNumReads != null) {
      this.strExpectedNumReads = expectedNumReads.toString();
    }
    this.expectedNumReads = expectedNumReads;
  }

  @XmlJavaTypeAdapter(XmlizeLaneSortedSet.class)
  public SortedSet<Lane> getLanes() {
    // having this kind of logic in a get method is madness...
    // SortedSet<Lane> ln = new TreeSet<Lane>();
    // Set<IUS> tempIUS = getIUS();
    // for(IUS i: tempIUS){
    // if(i.getLane() != null){
    // ln.add(i.getLane());
    // }
    // }
    // return ln;
    return lanes;
  }

  public void setLanes(SortedSet<Lane> lanes) {
    this.lanes = lanes;
  }

  public void setLanesForView(SortedSet<Lane> lanes) {
    SortedSet<IUS> IUS = new TreeSet<IUS>();
    for (Lane lane : lanes) {
      IUS newIUS = new IUS();
      newIUS.setSample(this);
      newIUS.setLane(lane);
      newIUS.setIusId(lane.getLaneId());
      IUS.add(newIUS);
    }
    this.setIUS(IUS);
    // this.lanes = lanes;
  }

  public Integer getSampleId() {
    return sampleId;
  }

  public void setSampleId(Integer sampleId) {
    this.sampleId = sampleId;
  }

  public Experiment getExperiment() {
    return experiment;
  }

  public void setExperiment(Experiment experiment) {
    this.experiment = experiment;
  }

  public Registration getOwner() {
    return owner;
  }

  public void setOwner(Registration owner) {
    this.owner = owner;
  }

  public String getAnonymizedName() {
    return anonymizedName;
  }

  public void setAnonymizedName(String anonymizedName) {
    this.anonymizedName = anonymizedName;
  }

  public String getName() {
    return name;
  }

  public String getJsonEscapeName() {
    return JsonUtil.forJSON(name);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getJsonEscapeDescription() {
    return JsonUtil.forJSON(description);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public String getRegions() {
    return regions;
  }

  public void setRegions(String regions) {
    this.regions = regions;
  }

  public Boolean getSkip() {
    return skip;
  }

  public void setSkip(Boolean skip) {
    this.skip = skip;
  }

  public Date getCreateTimestamp() {
    return createTimestamp;
  }

  public void setCreateTimestamp(Date createTimestamp) {
    this.createTimestamp = createTimestamp;
  }

  public Date getUpdateTimestamp() {
    return updateTimestamp;
  }

  public void setUpdateTimestamp(Date updateTimestamp) {
    this.updateTimestamp = updateTimestamp;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public Organism getOrganism() {
    return organism;
  }

  public void setOrganism(Organism organism) {
    this.organism = organism;
  }

  public Boolean getIsSelected() {
    return isSelected;
  }

  public void setIsSelected(Boolean isSelected) {
    this.isSelected = isSelected;
  }

  public Boolean getIsHasFile() {
    return isHasFile;
  }

  public void setIsHasFile(Boolean isHasFile) {
    this.isHasFile = isHasFile;
  }

  public Integer getCountFile() {
    return countFile;
  }

  public void setCountFile(Integer countFile) {
    this.countFile = countFile;
  }

  public String getStrExpectedNumRuns() {
    return strExpectedNumRuns;
  }

  public void setStrExpectedNumRuns(String strExpectedNumRuns) {
    this.strExpectedNumRuns = strExpectedNumRuns;
  }

  public String getStrExpectedNumReads() {
    return strExpectedNumReads;
  }

  public void setStrExpectedNumReads(String strExpectedNumReads) {
    this.strExpectedNumReads = strExpectedNumReads;
  }

  // public SortedSet<IUS> getIus() {
  // return ius;
  // }
  @XmlJavaTypeAdapter(XmlizeIUSSortedSet.class)
  public SortedSet<IUS> getIUS() {
    return ius;
  }

  public void setIUS(SortedSet<IUS> ius) {
    this.ius = ius;
  }

  public Set<Processing> getProcessings() {
    return processings;
  }

  public void setProcessings(Set<Processing> processings) {
    this.processings = processings;
  }

  public Set<Sample> getParents() {
    return parents;
  }

  public void setParents(Set<Sample> parents) {
    this.parents = parents;
  }

  public Set<Sample> getChildren() {
    return children;
  }

  public void setChildren(Set<Sample> children) {
    this.children = children;
  }

  @XmlElementWrapper(name = "SampleAttributes")
  @XmlElement(name = "SampleAttribute")
  public Set<SampleAttribute> getSampleAttributes() {
    return sampleAttributes;
  }

  public void setSampleAttributes(Set<SampleAttribute> sampleAttributes) {
    this.sampleAttributes = sampleAttributes;
  }

  public Set<SampleLink> getSampleLinks() {
    return sampleLinks;
  }

  public void setSampleLinks(Set<SampleLink> sampleLinks) {
    this.sampleLinks = sampleLinks;
  }

  @Override
  public boolean givesPermission(Registration registration) {
    boolean hasPermission = true;
    Log.debug("registration: " + registration + " owner: " + owner);
    if (experiment != null) {
      hasPermission = experiment.givesPermission(registration);
    } else {// orphaned Sample
      if (registration.equals(this.owner) || registration.isLIMSAdmin()) {
        Logger.getLogger(Sample.class).warn("Modifying Orphan Sample: " + this.getName());
        hasPermission = true;
      } else {
        Logger.getLogger(Sample.class).warn("Not modifying Orphan Sample: " + this.getName());
        hasPermission = false;
      }
    }
    if (!hasPermission) {
      Logger.getLogger(Sample.class).info("Sample does not give permission to " + registration);
      throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify "
          + this.getName());
    }
    return hasPermission;
  }
}
