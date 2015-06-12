package io.seqware.webservice.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
/**
 * This class can be used to convert an Integer to a String, for use as an XML ID Ref value.
 * @author sshorser
 *
 */
public class IntegerAdapter extends XmlAdapter<String, Integer> {

	@Override
	public Integer unmarshal(String v) throws Exception {
		return Integer.parseInt(v);
	}

	@Override
	public String marshal(Integer v) throws Exception {
		return String.valueOf(v);
	}
}
