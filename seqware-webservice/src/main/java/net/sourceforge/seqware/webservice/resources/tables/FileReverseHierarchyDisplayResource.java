package net.sourceforge.seqware.webservice.resources.tables;

import net.sourceforge.seqware.common.business.ValidationReportService;
import net.sourceforge.seqware.common.factory.BeanFactory;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public class FileReverseHierarchyDisplayResource extends DatabaseResource {
  
  private String swa;

  public FileReverseHierarchyDisplayResource() {
    super("fileReverseHierarchyDisplayResource");
  }

  @Override
  public void doInit() {
    swa = (String) getRequestAttributes().get("swa");
  }

  @Get("text")
  public Representation validate() {
    ValidationReportService fileValidationService = BeanFactory.getFileValidationServiceBean();
    Integer swaInteger = Integer.valueOf(swa);
    String hierarchy = fileValidationService.fileReverseHierarchyDisplay(swaInteger);
    return new StringRepresentation(hierarchy);
  }

}
