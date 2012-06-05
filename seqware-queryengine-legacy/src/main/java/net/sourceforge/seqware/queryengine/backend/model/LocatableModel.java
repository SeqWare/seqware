package net.sourceforge.seqware.queryengine.backend.model;

import java.util.HashMap;

public class LocatableModel extends Model {
  
	// generic to all types of locatable model objects
	protected String contig = "";
	protected int startPosition = 0;
	protected int stopPosition = 0;


	// generated methods
	
	public String getContig() {
		return contig;
	}
	public void setContig(String contig) {
		this.contig = contig;
	}
	public int getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	public int getStopPosition() {
		return stopPosition;
	}
	public void setStopPosition(int stopPosition) {
		this.stopPosition = stopPosition;
	}

  
}