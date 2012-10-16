package net.sourceforge.seqware.webservice.resources.tables;

import java.util.List;

import net.sourceforge.seqware.webservice.dto.AttributeDto;

import org.restlet.resource.Get;

/**
 * <p>FileAttributesResource interface.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public interface FileAttributesResource {
  
  /**
   * <p>getFileAttributes.</p>
   *
   * @return a {@link java.util.List} object.
   */
  @Get("json,xml")
  public List<AttributeDto> getFileAttributes();
  
}
