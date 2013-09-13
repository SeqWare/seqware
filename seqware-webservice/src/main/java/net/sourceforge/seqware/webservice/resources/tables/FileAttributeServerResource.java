package net.sourceforge.seqware.webservice.resources.tables;

import net.sourceforge.seqware.common.business.FileAttributeService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.err.NotFoundException;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.webservice.dto.AttributeDto;
import net.sourceforge.seqware.webservice.dto.Dtos;
import net.sourceforge.seqware.webservice.resources.BasicResource;

import org.hibernate.exception.ConstraintViolationException;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.google.common.annotations.VisibleForTesting;

/**
 * <p>FileAttributeServerResource class.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class FileAttributeServerResource extends BasicResource implements FileAttributeResource {

  private static FileAttributeService fileAttributeService;

  /**
   * <p>Constructor for FileAttributeServerResource.</p>
   */
  public FileAttributeServerResource() {
    super();
    FileAttributeServerResource.initialiseFileAttributeService();
  }

  private static void initialiseFileAttributeService() {
    if (FileAttributeServerResource.fileAttributeService == null) {
      FileAttributeServerResource.fileAttributeService = BeanFactory.getFileAttributeServiceBean();
    }
  }
  
  @VisibleForTesting
  static void setFileAttributeService(FileAttributeService fileAttributeService) {
    FileAttributeServerResource.fileAttributeService = fileAttributeService;
  }

  @VisibleForTesting
  static void setRegistrationServiceForAuthentication(RegistrationService registrationService) {
    BasicResource.setRegistrationService(registrationService);
  }

  /** {@inheritDoc} */
  @Override
  public AttributeDto getAttribute() {
    Integer fileSwa;
    Integer id;
    try {
      fileSwa = parseClientInt("" + getRequestAttributes().get("swa"));
      id = parseClientInt("" + getRequestAttributes().get("id"));
    } catch (NumberFormatException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
    }
    FileAttribute fileAttribute = fileAttributeService.get(fileSwa, id);
    if (fileAttribute == null) {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
    if (!fileAttribute.getFile().getSwAccession().equals(fileSwa)) {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
    AttributeDto attributeDto = Dtos.asDto(fileAttribute);
    attributeDto.setUrl(getRequest().getOriginalRef().toString());
    String baseUrl = getRequest().getRootRef().toString();
    attributeDto.setEntityUrl(baseUrl + "/files/" + fileSwa);
    return attributeDto;
  }

  /** {@inheritDoc} */
  @Override
  public void addAttribute(AttributeDto attributeDto) {
    Integer fileSwa;
    try {
      fileSwa = parseClientInt("" + getRequestAttributes().get("swa"));
    } catch (NumberFormatException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
    }
    FileAttribute fileAttribute = getFileAttribute(attributeDto);
    
    Integer id;
    try {
      id = fileAttributeService.add(fileSwa, fileAttribute);
    } catch (ConstraintViolationException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
    }
    if (id == null) {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "null id");
    }
    setLocationRef(getRequest().getOriginalRef().addSegment(id.toString()));
    setStatus(Status.SUCCESS_CREATED);
  }

  /** {@inheritDoc} */
  @Override
  public void updateAttribute(AttributeDto attributeDto) {
    Integer fileSwa;
    Integer id;
    try {
      fileSwa = parseClientInt("" + getRequestAttributes().get("swa"));
      id = parseClientInt("" + getRequestAttributes().get("id"));
    } catch (NumberFormatException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
    }
    FileAttribute fileAttribute = getFileAttribute(attributeDto);
    fileAttribute.setFileAttributeId(id);
    try {
      fileAttributeService.update(fileSwa, fileAttribute);
    } catch (NotFoundException e) {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void deleteAttribute() {
    Integer fileSwa;
    Integer id;
    try {
      fileSwa = parseClientInt("" + getRequestAttributes().get("swa"));
      id = parseClientInt("" + getRequestAttributes().get("id"));
    } catch (NumberFormatException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
    }
    try {
      fileAttributeService.delete(fileSwa, id);
    } catch (NotFoundException e) {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
  }

  private FileAttribute getFileAttribute(AttributeDto attributeDto) {
    try {
      return Dtos.fromDto(attributeDto, FileAttribute.class);
    } catch (InstantiationException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    } catch (IllegalAccessException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
}
