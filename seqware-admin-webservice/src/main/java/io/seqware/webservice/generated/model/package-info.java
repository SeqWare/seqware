@XmlJavaTypeAdapters({ 
   @XmlJavaTypeAdapter(value = DateFormatterAdapter.class, type = Date.class)
})
package io.seqware.webservice.generated.model;

import io.seqware.webservice.adapter.DateFormatterAdapter;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

