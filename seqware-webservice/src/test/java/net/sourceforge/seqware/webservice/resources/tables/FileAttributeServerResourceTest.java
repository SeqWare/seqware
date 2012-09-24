package net.sourceforge.seqware.webservice.resources.tables;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import net.sourceforge.seqware.common.business.FileAttributeService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.seqware.webservice.dto.AttributeDto;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.restlet.Application;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;
import org.restlet.routing.Router;
import org.restlet.test.RestletTestCase;

@RunWith(MockitoJUnitRunner.class)
public class FileAttributeServerResourceTest extends RestletTestCase {

  @Mock
  FileAttributeService mockFileAttributeService;

  @Mock
  RegistrationService mockRegistrationService;

  private static class TestApplication extends Application {

    @Override
    public Restlet createInboundRoot() {
      Router router = new Router(getContext());
      router.attach("/file/{swa}/attribute", FileAttributeServerResource.class);
      router.attach("/file/{swa}/attribute/{id}", FileAttributeServerResource.class);

      return router;
    }

  }

  private Component c;

  private Client client;
  

  private String uri;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    Engine.getInstance().getRegisteredConverters().clear();
    Engine.getInstance().registerDefaultConverters();
    Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
    c = new Component();
    final Server server = c.getServers().add(Protocol.HTTP, 0);
    c.getDefaultHost().attach(new TestApplication());
    c.start();

    client = new Client(Protocol.HTTP);
    

    uri = "http://localhost:" + server.getEphemeralPort();
    
    FileAttributeServerResource.setFileAttributeService(mockFileAttributeService);
    FileAttributeServerResource.setRegistrationServiceForAuthentication(mockRegistrationService);

    FileAttributesServerResource.setFileAttributeService(mockFileAttributeService);
    FileAttributesServerResource.setRegistrationServiceForAuthentication(mockRegistrationService);
    when(mockRegistrationService.findByEmailAddress(anyString())).thenReturn(getAdminRegistrationDto());
  }

  @After
  public void tearDown() throws Exception {
    c.stop();
    c = null;
    client.stop();
    client = null;
    super.tearDown();
  }

  @Test
  public void test_get_attribute_exists() throws Exception {
    File file = new File();
    file.setSwAccession(1);
    FileAttribute fileAttribute = getFileAttribute();
    fileAttribute.setFile(file);
    when(mockFileAttributeService.get(1, 2)).thenReturn(fileAttribute);
    ClientResource clientResource = new ClientResource(uri + "/file/1/attribute/2");
    FileAttributeResource fileAttributeResource = clientResource.wrap(FileAttributeResource.class);
    AttributeDto attributeDto = fileAttributeResource.getAttribute();
    assertThat(clientResource.getStatus(), is(Status.SUCCESS_OK));
    assertThat(attributeDto.getName(), is("drink"));
  }
  
  private static FileAttribute getFileAttribute() {
    FileAttribute fileAttribute = new FileAttribute();
    fileAttribute.setTag("drink");
    fileAttribute.setValue("coffee");
    fileAttribute.setUnit("ml");
    fileAttribute.setFile(new File());
    return fileAttribute;
  }
  
  private static RegistrationDTO getAdminRegistrationDto() {
    RegistrationDTO registrationDto = new RegistrationDTO();
    registrationDto.setEmailAddress("admin@admin.com");
    registrationDto.setPassword("admin");
    return registrationDto;
  }

}
