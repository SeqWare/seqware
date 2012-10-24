package net.sourceforge.seqware.webservice.resources.tables;

import java.util.Map;
import java.util.Set;

import net.sourceforge.seqware.common.business.LibraryService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.webservice.dto.AttributeDto;
import net.sourceforge.seqware.webservice.dto.Dtos;
import net.sourceforge.seqware.webservice.dto.LibraryDto;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * <p>LibraryResource class.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class LibraryResource extends DatabaseResource {

  private Long swa;

  /**
   * <p>Constructor for LibraryResource.</p>
   */
  public LibraryResource() {
    super("library");
  }

  /** {@inheritDoc} */
  @Override
  protected void doInit() throws ResourceException {
    String swaString = (String) getRequestAttributes().get("swa");
    try {
      swa = Long.valueOf(swaString);
    } catch (NumberFormatException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Library {swa} is not a number.", e);
    }
  }

  /**
   * <p>someString.</p>
   *
   * @return a {@link net.sourceforge.seqware.webservice.dto.LibraryDto} object.
   */
  @Get
  public LibraryDto someString() {
    authenticate();

    LibraryService libraryService = BeanFactory.getLibraryServiceBean();
    Sample sample = libraryService.findBySWAccession(swa);
    if (sample == null) {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
    
    LibraryDto libraryDto = Dtos.asDto(sample);
    
    Reference sampleRef = getResourcesBase().addSegment("samples");
    Set<String> children = Sets.newHashSet();
    for(Sample child : sample.getChildren()) {
      children.add(sampleRef.toString() + "/" +Integer.toString(child.getSwAccession()));
    }
    libraryDto.setChildrenUrls(children);
    
    Set<String> parents = Sets.newHashSet();
    for(Sample parent : sample.getParents()) {
      parents.add(sampleRef.toString() + "/" +Integer.toString(parent.getSwAccession()));
    }
    libraryDto.setParentUrls(parents);
  
    Set<AttributeDto> attributes = Sets.newHashSet();
    for(Map.Entry<SampleAttribute, Sample> entry : getSampleAttributesToRoot(sample).entrySet()) {
      AttributeDto attributeDto = Dtos.sampleAttributeAsDto(entry.getKey());
      attributeDto.setEntityUrl(sampleRef.toString() + "/" + Integer.toString(entry.getValue().getSwAccession()));
      attributes.add(attributeDto);
    }
    libraryDto.setAttributes(attributes);

    return libraryDto;
  }

  private Map<SampleAttribute, Sample> getSampleAttributesToRoot(Sample sample) {
    Map<SampleAttribute, Sample> sampleAttributes = Maps.newHashMap();
    doGetSampleAttributes(sample, sampleAttributes);
    return sampleAttributes;
  }

  private void doGetSampleAttributes(Sample sample, Map<SampleAttribute, Sample> sampleAttributes) {
    for (SampleAttribute attribute : sample.getSampleAttributes()) {
      sampleAttributes.put(attribute, sample);
    }
    for (Sample parent : sample.getParents()) {
      doGetSampleAttributes(parent, sampleAttributes);
    }
  }

}
