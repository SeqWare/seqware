package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * a file object which has all information for provision
 *
 */
public class SqwFile {
        private boolean attached = false;
	private String type;
	private String location;
        private String outputLocation;
	private boolean input = true;
	private boolean forceCopy;
	private String uniqueDir;
	private List<String> parentAccessions;
	
	public SqwFile() {
		//need to create a random directory for later reference
		this.uniqueDir = Long.toString(System.nanoTime());
		this.parentAccessions = new ArrayList<String>();
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSourcePath() {
		return location;
	}
	public void setSourcePath(String location) {
		this.location = location;
	}
        public String getOutputPath() {
          return outputLocation;
        }

        public void setOutputPath(String location) {
          this.outputLocation = location;
        }
	
	/**
	 * is an output file?
	 * @return
	 */
	public boolean isOutput() {
		return !input;
	}
	/**
	 * is an input file?
	 * @return
	 */
	public boolean isInput() {
		return input;
	}
	
	/**
	 * isInput = @param isInput
	 * isOutput = !@param isInput
	 * @param isInupt
	 */
	public void setIsInput(boolean isInupt) {
		this.input = isInupt;
	}
	
	/**
	 * isInput = !@param isOutput
	 * isOutput = @param isOutput
	 * @param isInupt
	 */
	public void setIsOutput(boolean isOutput) {
		this.input = !isOutput;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SqwFile == false)
			return false;
		if(obj == this)
			return true;
		SqwFile rhs = (SqwFile)obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(type, rhs.type).
				append(location, rhs.location).append(input, rhs.input).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(type).append(location).append(input).toHashCode();
	}
	public boolean isForceCopy() {
		return forceCopy;
	}
	/**
	 * when forceCopy is true, it will pass "--forcecopy" argument to provisionFileJob
	 * @param forceCopy
	 */
	public void setForceCopy(boolean forceCopy) {
		this.forceCopy = forceCopy;
	}
	/**
	 * 
	 * @return the file path after provisioned.
	 */
	public String getProvisionedPath() {
		return "provisionfiles/" + this.uniqueDir + "/" + FilenameUtils.getName(this.getSourcePath());
	}

	
	/**
	 * return the unqiue dir associate with the file
	 * if the file type is input, the provisioned file will be output to provisionfiles/uniquedir + filename
	 * @return
	 */
	public String getUniqueDir() {
		return this.uniqueDir;
	}
	
	/**
	 * set the parent accessions for provision file job
	 * @param parentAccessions
	 */
	public void setParentAccessions(Collection<String> parentAccessions) {
		this.parentAccessions.addAll(parentAccessions);
	}
	
	public Collection<String> getParentAccessions() {
		return this.parentAccessions;
	}

    /**
     * @return the attached
     */
    public boolean isAttached() {
        return attached;
    }

    /**
     * @param attached the attached to set
     */
    public void setAttached(boolean attached) {
        this.attached = attached;
    }
}