package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.List;

public class Command {
	private List<String> arguments;
	//default FIXME should put in a property file
	private String mem = "500M";
	private boolean gcrSkipIfMissing;
	private boolean gcrSkipIfOutputExists;
	private String gcrOutputFile;
	
	public Command() {
		this.arguments = new ArrayList<String>();
	}

	public Command addArgument(String argument) {
		this.arguments.add(argument);
		return this;
	}
	
	public List<String> getArguments() {
		return arguments;
	}

	public Command setArguments(List<String> arguments) {
		this.arguments = arguments;
		return this;
	}
	
	@Override
	/**
	 * concat the arguments to a string with "\n" at the end of each line
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.arguments.isEmpty() == false) {
			sb.append(this.arguments.get(0));
			for(int i=1; i<this.arguments.size(); i++) {
				sb.append("\n"+this.arguments.get(i));
			}
		}
		return sb.toString();
	}

	public String getMaxMemory() {
		return mem;
	}

	public Command setMaxMemory(String mem) {
	  if (mem.matches("\\d+(g|G|m|M|k|K)?")){
	    this.mem = mem;
	    return this;
	  }
	  throw new IllegalArgumentException("Invalid max-memory value: "+mem);
	}

	public boolean isGcrSkipIfMissing() {
		return gcrSkipIfMissing;
	}

	public void setGcrSkipIfMissing(boolean gcrSkipIfMissing) {
		this.gcrSkipIfMissing = gcrSkipIfMissing;
	}

	public boolean isGcrSkipIfOutputExists() {
		return gcrSkipIfOutputExists;
	}

	public void setGcrSkipIfOutputExists(boolean gcrSkipIfOutputExists) {
		this.gcrSkipIfOutputExists = gcrSkipIfOutputExists;
	}

	public String getGcrOutputFile() {
		return gcrOutputFile;
	}

	public void setGcrOutputFile(String gcrOutputFile) {
		this.gcrOutputFile = gcrOutputFile;
	}
}