package net.sourceforge.seqware.webservice.resources.tables;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.business.FileAttributeService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.err.NotFoundException;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.webservice.dto.AttributeDto;
import net.sourceforge.seqware.webservice.dto.Dtos;
import net.sourceforge.seqware.webservice.resources.BasicResource;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

/**
 * <p>FileAttributesServerResource class.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class FileAttributesServerResource extends BasicResource implements FileAttributesResource {

  private static FileAttributeService fileAttributeService;

  /**
   * <p>Constructor for FileAttributesServerResource.</p>
   */
  public FileAttributesServerResource() {
    super();
    FileAttributesServerResource.initialiseFileAttributeService();
  }

  private static void initialiseFileAttributeService() {
    if (FileAttributesServerResource.fileAttributeService == null) {
      FileAttributesServerResource.fileAttributeService = BeanFactory.getFileAttributeServiceBean();
    }
  }

  @VisibleForTesting
  static void setFileAttributeService(FileAttributeService fileAttributeService) {
    FileAttributesServerResource.fileAttributeService = fileAttributeService;
  }

  @VisibleForTesting
  static void setRegistrationServiceForAuthentication(RegistrationService registrationService) {
    BasicResource.setRegistrationService(registrationService);
  }

  /** {@inheritDoc} */
  @Override
  public List<AttributeDto> getFileAttributes() {
    authenticate();
    Integer fileSwa;
    try {
      fileSwa = parseClientInt("" + getRequestAttributes().get("swa"));
    } catch (NumberFormatException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
    }

    Set<FileAttribute> fileAttributes = null;
    try {
      fileAttributes = fileAttributeService.getFileAttributes(fileSwa);
    } catch (NotFoundException e) {
      // Could not find file specified by fileSwa.
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
    if (fileAttributes == null || fileAttributes.isEmpty()) {
      // List of attributes is empty.
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
    String baseUrl = getRequest().getRootRef().toString();
    List<AttributeDto> attributes = Lists.newArrayList();
    for (FileAttribute fileAttribute : fileAttributes) {
      AttributeDto attributeDto = Dtos.asDto(fileAttribute);
      attributeDto.setUrl(baseUrl + "/file/" + fileSwa + "/attribute/" + fileAttribute.getFileAttributeId().toString());
      attributeDto.setEntityUrl(baseUrl + "/files/" + fileSwa);
      attributes.add(attributeDto);
    }

    return attributes;
  }

}
