package net.sourceforge.seqware.webservice.resources.tables;

import net.sourceforge.seqware.webservice.dto.IusDto;

import org.restlet.resource.Get;

public interface IusSearchResource {

  /**
   * Locate IUS by sequencer run name, lane number and optionally sample name.
   * @return A single IUS dto.
   */
  @Get("json,xml")
  public IusDto findIus();
  
}
