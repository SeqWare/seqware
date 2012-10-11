package net.sourceforge.seqware.webservice.resources.tables;

import java.util.List;

import net.sourceforge.seqware.webservice.dto.FileDto;

import org.restlet.resource.Get;

public interface IusFilesResource {

  /**
   * List files associated with Ius.
   * 
   * @return A list of Files.
   */
  @Get("json,xml")
  public List<FileDto> getIusFiles();

}
