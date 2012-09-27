package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.List;

public class Command {
	private List<String> arguments;
	
	public Command() {
		this.arguments = new ArrayList<String>();
	}

	public void addArgument(String argument) {
		this.arguments.add(argument);
	}
	
	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
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
}