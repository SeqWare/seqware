package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.io.File;
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
  private String type;
  private String sourcePath;
  private String outputPath;
  private boolean input = true;
  private boolean forceCopy;
  private List<String> parentAccessions = new ArrayList<String>();

  public SqwFile() {
  }

  /**
   * Constructor for input files; output path is defaulted to #{@link SqwFile#provisionPath(String)}.
   */
  public SqwFile(String type, String inputPath) {
    setType(type);
    setIsInput(true);
    setSourcePath(inputPath);
    setForceCopy(false);
  }

  /**
   * Constructor for output files; forceCopy is defaulted to true.
   */
  public SqwFile(String type, String sourcePath, String outputPath) {
    setType(type);
    setIsInput(false);
    setSourcePath(sourcePath);
    setOutputPath(outputPath);
    setForceCopy(true);
  }

  public static String provisionPath(String sourcePath) {
    String unique = Long.toString(System.nanoTime());
    String dir = "provisionfiles/" + unique + "/" + FilenameUtils.getName(sourcePath);
    return dir;
  }

  private void checkOutputPath() {
    if (input && outputPath == null && sourcePath != null) {
      outputPath = provisionPath(sourcePath);
    }
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public void setSourcePath(String location) {
    this.sourcePath = location;
    checkOutputPath();
  }

  public String getOutputPath() {
    return outputPath;
  }

  public void setOutputPath(String location) {
    this.outputPath = location;
    checkOutputPath();
  }

  /**
   * is an output file?
   * 
   * @return
   */
  public boolean isOutput() {
    return !input;
  }

  /**
   * is an input file?
   * 
   * @return
   */
  public boolean isInput() {
    return input;
  }

  /**
   * isInput = @param isInput isOutput = !@param isInput
   * 
   * @param isInupt
   */
  public void setIsInput(boolean isInupt) {
    this.input = isInupt;
    checkOutputPath();
  }

  /**
   * isInput = !@param isOutput isOutput = @param isOutput
   * 
   * @param isInupt
   */
  public void setIsOutput(boolean isOutput) {
    this.input = !isOutput;
    checkOutputPath();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SqwFile == false)
      return false;
    if (obj == this)
      return true;
    SqwFile rhs = (SqwFile) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj))
                              .append(type, rhs.type)
                              .append(sourcePath, rhs.sourcePath)
                              .append(input, rhs.input)
                              .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(type).append(sourcePath).append(input).toHashCode();
  }

  public boolean isForceCopy() {
    return forceCopy;
  }

  /**
   * when forceCopy is true, it will pass "--forcecopy" argument to
   * provisionFileJob
   * 
   * @param forceCopy
   */
  public void setForceCopy(boolean forceCopy) {
    this.forceCopy = forceCopy;
  }

  /**
   * 
   * @return the file path after provisioned.
   */
  @Deprecated
  public String getProvisionedPath() {
    return getOutputPath();
  }

  /**
   * return the unqiue dir associate with the file if the file type is input,
   * the provisioned file will be output to provisionfiles/uniquedir + filename
   * 
   * @return
   */
  @Deprecated
  public String getUniqueDir() {
    throw new UnsupportedOperationException();
  }

  /**
   * set the parent accessions for provision file job
   * 
   * @param parentAccessions
   */
  public void setParentAccessions(Collection<String> parentAccessions) {
    this.parentAccessions.addAll(parentAccessions);
  }

  public Collection<String> getParentAccessions() {
    return this.parentAccessions;
  }
}