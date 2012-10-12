package net.sourceforge.seqware.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
/**
 * <p>OwnerDto class.</p>
 *
 * @author tdebat
 * @version $Id: $Id
 */
@JsonAutoDetect
@JsonSerialize(include = Inclusion.NON_NULL)
public class OwnerDto {
	
	private String email;
	@JsonProperty("first_name")
	private String firstName;
	@JsonProperty("last_name")
	private String lastName;
	
	private String institution;

	/**
	 * <p>Getter for the field <code>email</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * <p>Setter for the field <code>email</code>.</p>
	 *
	 * @param email a {@link java.lang.String} object.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * <p>Getter for the field <code>firstName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * <p>Setter for the field <code>firstName</code>.</p>
	 *
	 * @param firstName a {@link java.lang.String} object.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * <p>Getter for the field <code>lastName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * <p>Setter for the field <code>lastName</code>.</p>
	 *
	 * @param lastName a {@link java.lang.String} object.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * <p>Getter for the field <code>institution</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getInstitution() {
		return institution;
	}

	/**
	 * <p>Setter for the field <code>institution</code>.</p>
	 *
	 * @param institution a {@link java.lang.String} object.
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}

}
