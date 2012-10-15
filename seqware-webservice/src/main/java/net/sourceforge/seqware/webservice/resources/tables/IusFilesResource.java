package net.sourceforge.seqware.webservice.resources.tables;

import java.util.List;

import net.sourceforge.seqware.webservice.dto.FileDto;

import org.restlet.resource.Get;

/**
 * <p>IusFilesResource interface.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public interface IusFilesResource {

  /**
   * List files associated with Ius.
   *
   * @return A list of Files.
   */
  @Get("json,xml")
  public List<FileDto> getIusFiles();

}
