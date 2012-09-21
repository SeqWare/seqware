package net.sourceforge.seqware.webservice.resources.tables;

import net.sourceforge.seqware.webservice.dto.AttributeDto;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

public interface FileAttributeResource {
  
  @Post("json,xml")
  public void addAttribute(AttributeDto attributeDto);
  
  @Get("json,xml")
  public AttributeDto getAttribute();
  
  @Put("json,xml")
  public void updateAttribute(AttributeDto attributeDto);
  
  @Delete("json,xml")
  public void deleteAttribute();
}
