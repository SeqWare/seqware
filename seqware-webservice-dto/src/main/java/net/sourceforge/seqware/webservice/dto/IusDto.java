package net.sourceforge.seqware.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
/**
 * <p>IusDto class.</p>
 *
 * @author tdebat
 * @version $Id: $Id
 */
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

	/**
	 * <p>Getter for the field <code>url</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * <p>Setter for the field <code>url</code>.</p>
	 *
	 * @param url a {@link java.lang.String} object.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * <p>Getter for the field <code>swa</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getSwa() {
		return swa;
	}

	/**
	 * <p>Setter for the field <code>swa</code>.</p>
	 *
	 * @param swa a {@link java.lang.Integer} object.
	 */
	public void setSwa(Integer swa) {
		this.swa = swa;
	}

	/**
	 * <p>Getter for the field <code>barcode</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBarcode() {
		return barcode;
	}

	/**
	 * <p>Setter for the field <code>barcode</code>.</p>
	 *
	 * @param barcode a {@link java.lang.String} object.
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * <p>Getter for the field <code>skip</code>.</p>
	 *
	 * @return a {@link java.lang.Boolean} object.
	 */
	public Boolean getSkip() {
		return skip;
	}

	/**
	 * <p>Setter for the field <code>skip</code>.</p>
	 *
	 * @param skip a {@link java.lang.Boolean} object.
	 */
	public void setSkip(Boolean skip) {
		this.skip = skip;
	}

	/**
	 * <p>Getter for the field <code>lane_url</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getLane_url() {
		return lane_url;
	}

	/**
	 * <p>Setter for the field <code>lane_url</code>.</p>
	 *
	 * @param lane_url a {@link java.lang.String} object.
	 */
	public void setLane_url(String lane_url) {
		this.lane_url = lane_url;
	}

	/**
	 * <p>Getter for the field <code>sample_url</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSample_url() {
		return sample_url;
	}

	/**
	 * <p>Setter for the field <code>sample_url</code>.</p>
	 *
	 * @param sample_url a {@link java.lang.String} object.
	 */
	public void setSample_url(String sample_url) {
		this.sample_url = sample_url;
	}

	/**
	 * <p>Getter for the field <code>sequencer_run_url</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSequencer_run_url() {
		return sequencer_run_url;
	}

	/**
	 * <p>Setter for the field <code>sequencer_run_url</code>.</p>
	 *
	 * @param sequencer_run_url a {@link java.lang.String} object.
	 */
	public void setSequencer_run_url(String sequencer_run_url) {
		this.sequencer_run_url = sequencer_run_url;
	}

	/**
	 * <p>Getter for the field <code>createTimeStamp</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCreateTimeStamp() {
		return createTimeStamp;
	}

	/**
	 * <p>Setter for the field <code>createTimeStamp</code>.</p>
	 *
	 * @param createTimeStamp a {@link java.lang.String} object.
	 */
	public void setCreateTimeStamp(String createTimeStamp) {
		this.createTimeStamp = createTimeStamp;
	}

	/**
	 * <p>Getter for the field <code>updateTimeStamp</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUpdateTimeStamp() {
		return updateTimeStamp;
	}

	/**
	 * <p>Setter for the field <code>updateTimeStamp</code>.</p>
	 *
	 * @param updateTimeStamp a {@link java.lang.String} object.
	 */
	public void setUpdateTimeStamp(String updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}

	/**
	 * <p>Getter for the field <code>files_url</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFiles_url() {
		return files_url;
	}

	/**
	 * <p>Setter for the field <code>files_url</code>.</p>
	 *
	 * @param files_url a {@link java.lang.String} object.
	 */
	public void setFiles_url(String files_url) {
		this.files_url = files_url;
	}

}
