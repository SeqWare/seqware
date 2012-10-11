package net.sourceforge.seqware.webservice.resources.tables;

import java.util.List;

import net.sourceforge.seqware.webservice.dto.AttributeDto;

import org.restlet.resource.Get;

public interface FileAttributesResource {
  
  @Get("json,xml")
  public List<AttributeDto> getFileAttributes();
  
}
