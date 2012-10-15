package net.sourceforge.seqware.webservice.resources.tables;

import net.sourceforge.seqware.common.business.ValidationReportService;
import net.sourceforge.seqware.common.factory.BeanFactory;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

/**
 * <p>FileReverseHierarchyDisplayResource class.</p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class FileReverseHierarchyDisplayResource extends DatabaseResource {
  
  private String swa;

  /**
   * <p>Constructor for FileReverseHierarchyDisplayResource.</p>
   */
  public FileReverseHierarchyDisplayResource() {
    super("fileReverseHierarchyDisplayResource");
  }

  /** {@inheritDoc} */
  @Override
  public void doInit() {
    swa = (String) getRequestAttributes().get("swa");
  }

  /**
   * <p>validate.</p>
   *
   * @return a {@link org.restlet.representation.Representation} object.
   */
  @Get("text")
  public Representation validate() {
    ValidationReportService fileValidationService = BeanFactory.getFileValidationServiceBean();
    Integer swaInteger = Integer.valueOf(swa);
    String hierarchy = fileValidationService.fileReverseHierarchyDisplay(swaInteger);
    return new StringRepresentation(hierarchy);
  }

}
