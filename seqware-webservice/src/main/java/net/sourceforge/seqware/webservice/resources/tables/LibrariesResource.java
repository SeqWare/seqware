package net.sourceforge.seqware.webservice.resources.tables;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.business.LibraryService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.webservice.dto.Dtos;
import net.sourceforge.seqware.webservice.dto.LibraryDto;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LibrariesResource extends DatabaseResource {

  private Map<String, String> attributes;
  
  public LibrariesResource() {
    super("libraries");
  }
  
  @Override
  public void doInit() {
      Form form = getRequest().getResourceRef().getQueryAsForm();
      queryValues = form.getValuesMap();
      if (queryValues == null) {
          queryValues = new HashMap<String, String>();
      }
      attributes = getGroupingAttributesFromQuery(queryValues);
  }
  
  private Map<String, String> getGroupingAttributesFromQuery(Map<String, String> queryValueMap) {
    List<String> attributes = Lists.newArrayList();
    if(queryValueMap.containsKey("attributes")) {
      String[] fieldArray = queryValueMap.get("attributes").split(",");
      attributes.addAll(Arrays.asList(fieldArray));
    }
    return splitGroupingAttributes(attributes);    
  }
  
  private Map<String, String> splitGroupingAttributes(List<String> attributes) {
    checkNotNull(attributes);
    Map<String, String> result = Maps.newHashMap();
    for( String attribute : attributes) {
      String[] fieldArr = attribute.split("=");
      // Fail parts on equal to two.
      if(fieldArr.length == 2) { 
        result.put(fieldArr[0], fieldArr[1]);
      } else {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Malformed attributes list.");
      }
    }
    return result;
  }

  @Get
  public List<LibraryDto> getLibraries() {
    authenticate();
    LibraryService libraryService = BeanFactory.getLibraryServiceBean();
    List<Sample> libraries = Lists.newArrayList();
    if(attributes.size() == 0) {
      libraries = libraryService.getLibraries();
    } else {
      libraries = libraryService.getLibraries(attributes);
    }
    if (libraries.isEmpty()) {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
    
    List<LibraryDto> libraryDtos = new ArrayList<LibraryDto>();
    for (Sample sample : libraries) {
      LibraryDto libraryDto = Dtos.asDto(sample);
      libraryDto.setUrl(getResourcesBase() + "/library/" + sample.getSwAccession());
      libraryDtos.add(libraryDto);
    }

    return libraryDtos;
  }



}
