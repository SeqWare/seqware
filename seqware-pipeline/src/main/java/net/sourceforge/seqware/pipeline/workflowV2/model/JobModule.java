package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>JobModule class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class JobModule {
  private List<Argument> arguments;
  private Module moduleType;

  /**
   * <p>Constructor for JobModule.</p>
   *
   * @param module a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Module} object.
   */
  public JobModule(Module module) {
	  this.moduleType = module;
    this.arguments = new ArrayList<Argument>();
  }

  /**
   * <p>getName.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getName() {
    return this.moduleType.getName();
  }

  /**
   * <p>addArgumentPair.</p>
   *
   * @param key a {@link java.lang.String} object.
   * @param value a {@link java.lang.String} object.
   */
  public void addArgumentPair(String key, String value) {
    Argument arg = new Argument(key, value);
    if (!this.arguments.contains(arg)) {
      this.arguments.add(arg);
    }
  }

  /**
   * <p>Getter for the field <code>arguments</code>.</p>
   *
   * @return a {@link java.util.Collection} object.
   */
  public Collection<Argument> getArguments() {
    return this.arguments;
  }

  /**
   * <p>addArgument.</p>
   *
   * @param args a {@link java.lang.String} object.
   */
  public void addArgument(String args) {

  }

  /**
   * <p>Getter for the field <code>moduleType</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Module} object.
   */
  public Module getModuleType() {
	  return this.moduleType;
  }

}
