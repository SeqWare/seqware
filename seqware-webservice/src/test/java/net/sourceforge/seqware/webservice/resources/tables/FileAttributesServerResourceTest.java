package net.sourceforge.seqware.webservice.resources.tables;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.business.FileAttributeService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.RegistrationDTO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.restlet.Application;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.routing.Router;
import org.restlet.test.RestletTestCase;

import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class FileAttributesServerResourceTest extends RestletTestCase {

  @Mock
  FileAttributeService mockFileAttributeService;

  @Mock
  RegistrationService mockRegistrationService;

  private static class TestApplication extends Application {

    @Override
    public Restlet createInboundRoot() {
      Router router = new Router(getContext());
      router.attach("/file/{swa}/attributes", FileAttributesServerResource.class);

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
    c = new Component();
    final Server server = c.getServers().add(Protocol.HTTP, 0);
    c.getDefaultHost().attach(new TestApplication());
    c.start();

    client = new Client(Protocol.HTTP);

    uri = "http://localhost:" + server.getEphemeralPort();

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
  public void test_get_files_attributes_list_is_empty() throws Exception {
    when(mockFileAttributeService.getFileAttributes(1)).thenReturn(Sets.<FileAttribute> newHashSet());

    client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, uri + "/file/1/attributes");
    request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin@admin.com", "admin"));
    Response response = client.handle(request);

    assertThat(response.getStatus(), is(Status.CLIENT_ERROR_NOT_FOUND));
    assertThat(response.getEntity().getText(), containsString("Not Found"));
    response.getEntity().release();
  }

  @Test
  public void test_get_files_attributes_list_contain_one_item_json() throws Exception {
    Set<FileAttribute> fileAttributes = Sets.newHashSet();
    fileAttributes.add(getFileAttribute(3));
    when(mockFileAttributeService.getFileAttributes(1)).thenReturn(fileAttributes);

    client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, uri + "/file/1/attributes");
    List<Preference<MediaType>> m = new ArrayList<Preference<MediaType>>();
    m.add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
    request.getClientInfo().setAcceptedMediaTypes(m);
    request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin@admin.com", "admin"));
    Response response = client.handle(request);

    assertThat(response.getStatus(), is(Status.SUCCESS_OK)); 
    response.getEntity().release();
  }
  
  @Test
  public void test_get_files_attributes_list_contain_one_item_xml() throws Exception {
    Set<FileAttribute> fileAttributes = Sets.newHashSet();
    fileAttributes.add(getFileAttribute(3));
    when(mockFileAttributeService.getFileAttributes(1)).thenReturn(fileAttributes);

    client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, uri + "/file/1/attributes");
    request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin@admin.com", "admin"));
    List<Preference<MediaType>> m = new ArrayList<Preference<MediaType>>();
    m.add(new Preference<MediaType>(MediaType.APPLICATION_XML));
    request.getClientInfo().setAcceptedMediaTypes(m);
    Response response = client.handle(request);

    assertThat(response.getStatus(), is(Status.SUCCESS_OK));   
    response.getEntity().release();
  }
  
  @Test
  public void test_get_files_attributes_list_contain_two_item() throws Exception {
    Set<FileAttribute> fileAttributes = Sets.newHashSet();
    fileAttributes.add(getFileAttribute(3));
    fileAttributes.add(getFileAttributeTwo(4));
    when(mockFileAttributeService.getFileAttributes(1)).thenReturn(fileAttributes);

    client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, uri + "/file/1/attributes");
    request.setEntity("", MediaType.APPLICATION_XML);
    request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin@admin.com", "admin"));
    List<Preference<MediaType>> m = new ArrayList<Preference<MediaType>>();
    m.add(new Preference<MediaType>(MediaType.APPLICATION_XML));
    request.getClientInfo().setAcceptedMediaTypes(m);
    
    Response response = client.handle(request);
    assertThat(response.getEntity().getMediaType().getName(), is("application/x-java-serialized-object"));
    
    response.getEntity().release();
  }

  @Test
  public void test_get_files_attributes_list_for_file_that_does_not_exist() throws Exception {
    when(mockFileAttributeService.getFileAttributes(99)).thenReturn(null);

    client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, uri + "/file/99/attributes");
    request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin@admin.com", "admin"));
    Response response = client.handle(request);

    assertThat(response.getStatus(), is(Status.CLIENT_ERROR_NOT_FOUND));
    assertThat(response.getEntity().getText(), containsString("Not Found"));
    response.getEntity().release();
  }

  @Test
  public void test_get_files_attributes_list_for_swa_larger_than_integer() throws Exception {
    client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, uri + "/file/999999999999999/attributes");
    request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin@admin.com", "admin"));
    Response response = client.handle(request);

    assertThat(response.getStatus(), is(Status.CLIENT_ERROR_BAD_REQUEST));
    assertThat(response.getEntity().getText(), containsString("Bad Request"));
    response.getEntity().release();
  }

  @Test
  public void test_get_files_attributes_list_for_malformed_swa() throws Exception {
    client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, uri + "/file/this_swa_is_malformed/attributes");
    request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin@admin.com", "admin"));
    Response response = client.handle(request);

    assertThat(response.getStatus(), is(Status.CLIENT_ERROR_BAD_REQUEST));
    assertThat(response.getEntity().getText(), containsString("Bad Request"));
    response.getEntity().release();
  }

  private static FileAttribute getFileAttribute(Integer id) {
    FileAttribute fileAttribute = new FileAttribute(id);
    fileAttribute.setTag("drink");
    fileAttribute.setValue("coffee");
    fileAttribute.setUnit("ml");
    fileAttribute.setFile(new File());
    return fileAttribute;
  }
  
  private static FileAttribute getFileAttributeTwo(Integer id) {
    FileAttribute fileAttribute = new FileAttribute(id);
    fileAttribute.setTag("height");
    fileAttribute.setValue("22");
    fileAttribute.setUnit("m");
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
