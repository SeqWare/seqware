package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.*;

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
/**
 * <p>Sample class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Sample implements Serializable, Comparable<Sample>, PermissionsAware {

  // Attributes relied upon by seqware code
  
  /**
   * The presence of this attribute is used to identify that the sample is a library
   */
  public static final String GEO_REACTION_ID_ATTR_TAG = "geo_reaction_id";
  
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
    private Set<Sample> parents = new TreeSet<Sample>(new Comparator<Sample>() {

        @Override
        public int compare(Sample t, Sample t1) {
            if (t == null && t1 == null) {
                return 0;
            } else if (t == null) {
                return -1;
            } else if (t1 == null) {
                return 1;
            } else {
                return t.compareTo(t1);
            }
        }
        
        
    });
    private Set<Sample> children = new TreeSet<Sample>();
    private Set<SampleAttribute> sampleAttributes = new TreeSet<SampleAttribute>();
    private Set<SampleLink> sampleLinks = new TreeSet<SampleLink>();
    // non-persisted field to store organism_id
    private Integer organismId;

    /**
     * <p>Constructor for Sample.</p>
     */
    public Sample() {
        super();
        lanes = new TreeSet<Lane>();
        for (IUS i : ius) {
            if (i.getLane() != null) {
                lanes.add(i.getLane());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("sampleId", getSampleId()).toString();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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
     * if(that.getSwAccession() == null) return -1; // when only the other name
     * is null
     *
     * return(that.getSwAccession().compareTo(this.getSwAccession())); }
     *
     * public String toString() { return new ToStringBuilder(this)
     * .append("sampleId", getSampleId()) .toString(); }
     *
     * public boolean equals(Object other) { if ( (this == other ) ) return
     * true; if ( !(other instanceof Sample) ) return false; Sample castOther =
     * (Sample) other; return new EqualsBuilder() .append(this.getSwAccession(),
     * castOther.getSwAccession()) .isEquals(); }
     *
     * public int hashCode() { return new HashCodeBuilder()
     * .append(getSwAccession()) .toHashCode(); }
     */
    /**
     * <p>Getter for the field
     * <code>alias</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * <p>Setter for the field
     * <code>alias</code>.</p>
     *
     * @param alias a {@link java.lang.String} object.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * <p>Getter for the field
     * <code>organismId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getOrganismId() {
        return organismId;
    }

    /**
     * <p>Setter for the field
     * <code>organismId</code>.</p>
     *
     * @param organismId a {@link java.lang.Integer} object.
     */
    public void setOrganismId(Integer organismId) {
        this.organismId = organismId;
    }

    /**
     * <p>Getter for the field
     * <code>adapters</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAdapters() {
        return adapters;
    }

    /**
     * <p>Setter for the field
     * <code>adapters</code>.</p>
     *
     * @param adapters a {@link java.lang.String} object.
     */
    public void setAdapters(String adapters) {
        this.adapters = adapters;
    }

    /**
     * <p>Getter for the field
     * <code>individualName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getIndividualName() {
        return individualName;
    }

    /**
     * <p>Setter for the field
     * <code>individualName</code>.</p>
     *
     * @param individualName a {@link java.lang.String} object.
     */
    public void setIndividualName(String individualName) {
        this.individualName = individualName;
    }

    /**
     * <p>Getter for the field
     * <code>title</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTitle() {
        return title;
    }

    /**
     * <p>getJsonEscapeTitle.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeTitle() {
        return JsonUtil.forJSON(title);
    }

    /**
     * <p>Setter for the field
     * <code>title</code>.</p>
     *
     * @param title a {@link java.lang.String} object.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Getter for the field
     * <code>expectedNumRuns</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getExpectedNumRuns() {
        return expectedNumRuns;
    }

    /**
     * <p>Setter for the field
     * <code>expectedNumRuns</code>.</p>
     *
     * @param expectedNumRuns a {@link java.lang.Integer} object.
     */
    public void setExpectedNumRuns(Integer expectedNumRuns) {
        if (expectedNumRuns != null) {
            this.strExpectedNumRuns = expectedNumRuns.toString();
        }
        this.expectedNumRuns = expectedNumRuns;
    }

    /**
     * <p>Getter for the field
     * <code>expectedNumSpots</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getExpectedNumSpots() {
        return expectedNumSpots;
    }

    /**
     * <p>Setter for the field
     * <code>expectedNumSpots</code>.</p>
     *
     * @param expectedNumSpots a {@link java.lang.Integer} object.
     */
    public void setExpectedNumSpots(Integer expectedNumSpots) {
        this.expectedNumSpots = expectedNumSpots;
    }

    /**
     * <p>Getter for the field
     * <code>expectedNumReads</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getExpectedNumReads() {
        return expectedNumReads;
    }

    /**
     * <p>Setter for the field
     * <code>expectedNumReads</code>.</p>
     *
     * @param expectedNumReads a {@link java.lang.Integer} object.
     */
    public void setExpectedNumReads(Integer expectedNumReads) {
        if (expectedNumReads != null) {
            this.strExpectedNumReads = expectedNumReads.toString();
        }
        this.expectedNumReads = expectedNumReads;
    }

    /**
     * <p>Getter for the field
     * <code>lanes</code>.</p>
     *
     * @return a {@link java.util.SortedSet} object.
     */
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

    /**
     * <p>Setter for the field
     * <code>lanes</code>.</p>
     *
     * @param lanes a {@link java.util.SortedSet} object.
     */
    public void setLanes(SortedSet<Lane> lanes) {
        this.lanes = lanes;
    }

    /**
     * <p>setLanesForView.</p>
     *
     * @param lanes a {@link java.util.SortedSet} object.
     */
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

    /**
     * <p>Getter for the field
     * <code>sampleId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getSampleId() {
        return sampleId;
    }

    /**
     * <p>Setter for the field
     * <code>sampleId</code>.</p>
     *
     * @param sampleId a {@link java.lang.Integer} object.
     */
    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    /**
     * <p>Getter for the field
     * <code>experiment</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    public Experiment getExperiment() {
        return experiment;
    }

    /**
     * <p>Setter for the field
     * <code>experiment</code>.</p>
     *
     * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment}
     * object.
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    /**
     * <p>Getter for the field
     * <code>owner</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Registration}
     * object.
     */
    public Registration getOwner() {
        return owner;
    }

    /**
     * <p>Setter for the field
     * <code>owner</code>.</p>
     *
     * @param owner a {@link net.sourceforge.seqware.common.model.Registration}
     * object.
     */
    public void setOwner(Registration owner) {
        this.owner = owner;
    }

    /**
     * <p>Getter for the field
     * <code>anonymizedName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAnonymizedName() {
        return anonymizedName;
    }

    /**
     * <p>Setter for the field
     * <code>anonymizedName</code>.</p>
     *
     * @param anonymizedName a {@link java.lang.String} object.
     */
    public void setAnonymizedName(String anonymizedName) {
        this.anonymizedName = anonymizedName;
    }

    /**
     * <p>Getter for the field
     * <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>getJsonEscapeName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeName() {
        return JsonUtil.forJSON(name);
    }

    /**
     * <p>Setter for the field
     * <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>getJsonEscapeDescription.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeDescription() {
        return JsonUtil.forJSON(description);
    }

    /**
     * <p>Getter for the field
     * <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field
     * <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field
     * <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field
     * <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>Getter for the field
     * <code>tags</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTags() {
        return tags;
    }

    /**
     * <p>Setter for the field
     * <code>tags</code>.</p>
     *
     * @param tags a {@link java.lang.String} object.
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * <p>Getter for the field
     * <code>regions</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRegions() {
        return regions;
    }

    /**
     * <p>Setter for the field
     * <code>regions</code>.</p>
     *
     * @param regions a {@link java.lang.String} object.
     */
    public void setRegions(String regions) {
        this.regions = regions;
    }

    /**
     * <p>Getter for the field
     * <code>skip</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getSkip() {
        return skip;
    }

    /**
     * <p>Setter for the field
     * <code>skip</code>.</p>
     *
     * @param skip a {@link java.lang.Boolean} object.
     */
    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    /**
     * <p>Getter for the field
     * <code>createTimestamp</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     * <p>Setter for the field
     * <code>createTimestamp</code>.</p>
     *
     * @param createTimestamp a {@link java.util.Date} object.
     */
    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    /**
     * <p>Getter for the field
     * <code>updateTimestamp</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    /**
     * <p>Setter for the field
     * <code>updateTimestamp</code>.</p>
     *
     * @param updateTimestamp a {@link java.util.Date} object.
     */
    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    /**
     * <p>Getter for the field
     * <code>serialVersionUID</code>.</p>
     *
     * @return a long.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * <p>Getter for the field
     * <code>swAccession</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getSwAccession() {
        return swAccession;
    }

    /**
     * <p>Setter for the field
     * <code>swAccession</code>.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     */
    public void setSwAccession(Integer swAccession) {
        this.swAccession = swAccession;
    }

    /**
     * <p>Getter for the field
     * <code>organism</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Organism} object.
     */
    public Organism getOrganism() {
        return organism;
    }

    /**
     * <p>Setter for the field
     * <code>organism</code>.</p>
     *
     * @param organism a {@link net.sourceforge.seqware.common.model.Organism}
     * object.
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * <p>Getter for the field
     * <code>isSelected</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsSelected() {
        return isSelected;
    }

    /**
     * <p>Setter for the field
     * <code>isSelected</code>.</p>
     *
     * @param isSelected a {@link java.lang.Boolean} object.
     */
    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * <p>Getter for the field
     * <code>isHasFile</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsHasFile() {
        return isHasFile;
    }

    /**
     * <p>Setter for the field
     * <code>isHasFile</code>.</p>
     *
     * @param isHasFile a {@link java.lang.Boolean} object.
     */
    public void setIsHasFile(Boolean isHasFile) {
        this.isHasFile = isHasFile;
    }

    /**
     * <p>Getter for the field
     * <code>countFile</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getCountFile() {
        return countFile;
    }

    /**
     * <p>Setter for the field
     * <code>countFile</code>.</p>
     *
     * @param countFile a {@link java.lang.Integer} object.
     */
    public void setCountFile(Integer countFile) {
        this.countFile = countFile;
    }

    /**
     * <p>Getter for the field
     * <code>strExpectedNumRuns</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStrExpectedNumRuns() {
        return strExpectedNumRuns;
    }

    /**
     * <p>Setter for the field
     * <code>strExpectedNumRuns</code>.</p>
     *
     * @param strExpectedNumRuns a {@link java.lang.String} object.
     */
    public void setStrExpectedNumRuns(String strExpectedNumRuns) {
        this.strExpectedNumRuns = strExpectedNumRuns;
    }

    /**
     * <p>Getter for the field
     * <code>strExpectedNumReads</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStrExpectedNumReads() {
        return strExpectedNumReads;
    }

    /**
     * <p>Setter for the field
     * <code>strExpectedNumReads</code>.</p>
     *
     * @param strExpectedNumReads a {@link java.lang.String} object.
     */
    public void setStrExpectedNumReads(String strExpectedNumReads) {
        this.strExpectedNumReads = strExpectedNumReads;
    }

    // public SortedSet<IUS> getIus() {
    // return ius;
    // }
    /**
     * <p>getIUS.</p>
     *
     * @return a {@link java.util.SortedSet} object.
     */
    @XmlJavaTypeAdapter(XmlizeIUSSortedSet.class)
    public SortedSet<IUS> getIUS() {
        return ius;
    }

    /**
     * <p>setIUS.</p>
     *
     * @param ius a {@link java.util.SortedSet} object.
     */
    public void setIUS(SortedSet<IUS> ius) {
        this.ius = ius;
    }

    /**
     * <p>Getter for the field
     * <code>processings</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Processing> getProcessings() {
        return processings;
    }

    /**
     * <p>Setter for the field
     * <code>processings</code>.</p>
     *
     * @param processings a {@link java.util.Set} object.
     */
    public void setProcessings(Set<Processing> processings) {
        this.processings = processings;
    }

    /**
     * <p>Getter for the field
     * <code>parents</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Sample> getParents() {
        return parents;
    }

    /**
     * <p>Setter for the field
     * <code>parents</code>.</p>
     *
     * @param parents a {@link java.util.Set} object.
     */
    public void setParents(Set<Sample> parents) {
        this.parents = parents;
    }

    /**
     * <p>Getter for the field
     * <code>children</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Sample> getChildren() {
        return children;
    }

    /**
     * <p>Setter for the field
     * <code>children</code>.</p>
     *
     * @param children a {@link java.util.Set} object.
     */
    public void setChildren(Set<Sample> children) {
        this.children = children;
    }

    /**
     * <p>Getter for the field
     * <code>sampleAttributes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    @XmlElementWrapper(name = "SampleAttributes")
    @XmlElement(name = "SampleAttribute")
    public Set<SampleAttribute> getSampleAttributes() {
        return sampleAttributes;
    }

    /**
     * <p>Setter for the field
     * <code>sampleAttributes</code>.</p>
     *
     * @param sampleAttributes a {@link java.util.Set} object.
     */
    public void setSampleAttributes(Set<SampleAttribute> sampleAttributes) {
        this.sampleAttributes = sampleAttributes;
    }

    /**
     * <p>Getter for the field
     * <code>sampleLinks</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<SampleLink> getSampleLinks() {
        return sampleLinks;
    }

    /**
     * <p>Setter for the field
     * <code>sampleLinks</code>.</p>
     *
     * @param sampleLinks a {@link java.util.Set} object.
     */
    public void setSampleLinks(Set<SampleLink> sampleLinks) {
        this.sampleLinks = sampleLinks;
    }


    /**
     * {@inheritDoc}
     */
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
