package io.seqware.webservice.adapter;

import java.util.Date;
import javax.servlet.Servlet;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * A JAXB Adapter to convert {@link Date} to ISO8601 date time strings when
 * generating XML and JSON. This adapter also converts ISO8601 date time strings
 * into {@link Date} classes when converting from XML or JSON to a Java Object.
 * 
 * ISO8601 date time strings has the format of the following example:
 * {@code 2012-01-09T21:39:17-05:00}.
 * 
 * @author tdebat
 * 
 */
public class DateFormatterAdapter extends XmlAdapter<String, Date> {

   private static DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

   @Override
   public Date unmarshal(final String v) throws Exception {
      return dateTimeFormatter.parseDateTime(v).toDate();
   }

   @Override
   public String marshal(final Date v) throws Exception {
      return dateTimeFormatter.print(v.getTime());
   }
}
