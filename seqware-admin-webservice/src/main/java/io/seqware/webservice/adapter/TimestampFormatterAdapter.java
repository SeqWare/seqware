package io.seqware.webservice.adapter;

import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Allows for conversion for the Timestamp values backing the @Version columns used for optimistic locking
 * 
 * @author dyuen
 * 
 */
public class TimestampFormatterAdapter extends XmlAdapter<String, Timestamp> {

    @Override
    public String marshal(Timestamp v) throws Exception {
        return v.toString();
    }

    @Override
    public Timestamp unmarshal(String v) throws Exception {
        return Timestamp.valueOf(v);
    }
}
