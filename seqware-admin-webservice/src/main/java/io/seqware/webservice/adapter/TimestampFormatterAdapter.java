package io.seqware.webservice.adapter;

import java.util.Date;
import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Allows for conversion for the Timestamp values backing the @Version columns 
 * used for optimistic locking
 * @author dyuen
 * 
 */
public class TimestampFormatterAdapter extends XmlAdapter<Date, Timestamp> {

  @Override
  public Date marshal(Timestamp v) {
          return new Date(v.getTime());
      }
  @Override
      public Timestamp unmarshal(Date v) {
          return new Timestamp(v.getTime());
      }
}
