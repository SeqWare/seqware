package net.sourceforge.seqware.webservice.resources.tables;

import java.util.List;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.webservice.dto.Dtos;
import net.sourceforge.seqware.webservice.dto.FileDto;
import net.sourceforge.seqware.webservice.resources.BasicResource;

import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;


/**
 * <p>IusFilesServerResource class.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class IusFilesServerResource extends BasicResource implements IusFilesResource {

  private static IUSService iusService;
  
  private Form form;

  /**
   * <p>Constructor for IusFilesServerResource.</p>
   */
  public IusFilesServerResource() {
    super();
    IusFilesServerResource.initialiseIusService();
  }

  private static void initialiseIusService() {
    if (IusFilesServerResource.iusService == null) {
      IusFilesServerResource.iusService = BeanFactory.getIUSServiceBean();
    }
  }
  
  @VisibleForTesting
  static void setIusService(IUSService iusService) {
    IusFilesServerResource.iusService = iusService;
  }

  @VisibleForTesting
  static void setRegistrationServiceForAuthentication(RegistrationService registrationService) {
    BasicResource.setRegistrationService(registrationService);
  }
  
  
  
  /** {@inheritDoc} */
  @Override
  protected void doInit() throws ResourceException {
    super.doInit();
    form = getRequest().getResourceRef().getQueryAsForm();
  }
  
  private String getFirstQueryValue(String name) {
    for (Parameter parameter : form) {
      if(name.equals( parameter.getName())) {
        return parameter.getValue();
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public List<FileDto> getIusFiles() {
    authenticate();
    
    Integer iusId;
    try {
      iusId = parseClientInt("" + getRequestAttributes().get("id"));
    } catch (NumberFormatException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
    }
    
    String mimeType = getFirstQueryValue("mimeType");
    List<File> files = Lists.newArrayList();
    if(mimeType == null) {
      files = iusService.getFiles(iusId);
    } else {
      files = iusService.getFiles(iusId, mimeType);
    }
    
    if(files.size() == 0) {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
    List<FileDto> fileDtos = Lists.newArrayList();
    String baseUrl = getRequest().getRootRef().toString();
    for(File file : files) {
      FileDto fileDto = Dtos.asDto(file);
      fileDto.setUrl(baseUrl + "/files/" + file.getSwAccession() );
      fileDtos.add(fileDto);
    }
    return fileDtos;
  }

}
