package net.sourceforge.seqware.webservice.resources.tables;

import java.util.List;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.webservice.dto.Dtos;
import net.sourceforge.seqware.webservice.dto.IusDto;
import net.sourceforge.seqware.webservice.resources.BasicResource;

import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;


/**
 * <p>IusSearchServerResource class.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class IusSearchServerResource extends BasicResource implements IusSearchResource {

  private static IUSService iusService;
  
  private Form form;

  /**
   * <p>Constructor for IusSearchServerResource.</p>
   */
  public IusSearchServerResource() {
    super();
    IusSearchServerResource.initialiseIusService();
  }

  private static void initialiseIusService() {
    if (IusSearchServerResource.iusService == null) {
      IusSearchServerResource.iusService = BeanFactory.getIUSServiceBean();
    }
  }
  
  @VisibleForTesting
  static void setIusService(IUSService iusService) {
    IusSearchServerResource.iusService = iusService;
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
  public IusDto findIus() {
    authenticate();
    String sequencerRunName = "" + getFirstQueryValue("sequencerRunName");
    Integer lane;
    try {
      lane = parseClientInt("" + getFirstQueryValue("lane"));
    } catch (NumberFormatException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
    }
    String sampleName = getFirstQueryValue("sampleName");
    List<IUS> iuses = Lists.newArrayList();
    try {
      iuses = iusService.find(sequencerRunName, lane, sampleName);
    } catch(IllegalStateException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
    }
    if(iuses.size() == 0) {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
    if(iuses.size() > 1) {
      throw new ResourceException(Status.REDIRECTION_MULTIPLE_CHOICES, "More than one iuse returned. Specify barcode to narrow choice.");
    }
    IUS ius = iuses.get(0);
    IusDto iusDto = Dtos.asDto(ius);
    String baseUrl = getRequest().getRootRef().toString();
    iusDto.setUrl(baseUrl + "/ius/" + iusDto.getSwa());
    iusDto.setLane_url(baseUrl + "/lanes/" + ius.getLane().getSwAccession());
    iusDto.setSample_url(baseUrl + "/samples/" + ius.getSample().getSwAccession());
    iusDto.setSequencer_run_url(baseUrl + "/sequencerruns/" + ius.getLane().getSequencerRun().getSwAccession());
    iusDto.setFiles_url(baseUrl + "/ius/" + ius.getIusId() + "/files");
    return iusDto;
  }

}
