package net.sourceforge.seqware.webservice.resources.tables;

import net.sourceforge.seqware.webservice.dto.AttributeDto;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

/**
 * <p>FileAttributeResource interface.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public interface FileAttributeResource {
  
  /**
   * <p>addAttribute.</p>
   *
   * @param attributeDto a {@link net.sourceforge.seqware.webservice.dto.AttributeDto} object.
   */
  @Post("json,xml")
  public void addAttribute(AttributeDto attributeDto);
  
  /**
   * <p>getAttribute.</p>
   *
   * @return a {@link net.sourceforge.seqware.webservice.dto.AttributeDto} object.
   */
  @Get("json,xml")
  public AttributeDto getAttribute();
  
  /**
   * <p>updateAttribute.</p>
   *
   * @param attributeDto a {@link net.sourceforge.seqware.webservice.dto.AttributeDto} object.
   */
  @Put("json,xml")
  public void updateAttribute(AttributeDto attributeDto);
  
  /**
   * <p>deleteAttribute.</p>
   */
  @Delete("json,xml")
  public void deleteAttribute();
}
