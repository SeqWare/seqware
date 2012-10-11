package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JobModule {
  private List<Argument> arguments;
  private Module moduleType;

  public JobModule(Module module) {
	  this.moduleType = module;
    this.arguments = new ArrayList<Argument>();
  }

  public String getName() {
    return this.moduleType.getName();
  }

  public void addArgumentPair(String key, String value) {
    Argument arg = new Argument(key, value);
    if (!this.arguments.contains(arg)) {
      this.arguments.add(arg);
    }
  }

  public Collection<Argument> getArguments() {
    return this.arguments;
  }

  public void addArgument(String args) {

  }

  public Module getModuleType() {
	  return this.moduleType;
  }

}
