package net.sourceforge.seqware.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize(include = Inclusion.NON_NULL)
public class IusDto {

	private String url;
	private String files_url;
	private Integer swa;
	private String barcode;
	private Boolean skip;
	private String lane_url;
	private String sample_url;
	private String sequencer_run_url;
	@JsonProperty("create_time_stamp")
	private String createTimeStamp;
	@JsonProperty("update_time_stamp")
	private String updateTimeStamp;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSwa() {
		return swa;
	}

	public void setSwa(Integer swa) {
		this.swa = swa;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Boolean getSkip() {
		return skip;
	}

	public void setSkip(Boolean skip) {
		this.skip = skip;
	}

	public String getLane_url() {
		return lane_url;
	}

	public void setLane_url(String lane_url) {
		this.lane_url = lane_url;
	}

	public String getSample_url() {
		return sample_url;
	}

	public void setSample_url(String sample_url) {
		this.sample_url = sample_url;
	}

	public String getSequencer_run_url() {
		return sequencer_run_url;
	}

	public void setSequencer_run_url(String sequencer_run_url) {
		this.sequencer_run_url = sequencer_run_url;
	}

	public String getCreateTimeStamp() {
		return createTimeStamp;
	}

	public void setCreateTimeStamp(String createTimeStamp) {
		this.createTimeStamp = createTimeStamp;
	}

	public String getUpdateTimeStamp() {
		return updateTimeStamp;
	}

	public void setUpdateTimeStamp(String updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}

	public String getFiles_url() {
		return files_url;
	}

	public void setFiles_url(String files_url) {
		this.files_url = files_url;
	}

}
