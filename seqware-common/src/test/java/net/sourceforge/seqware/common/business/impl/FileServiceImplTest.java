package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.sourceforge.seqware.common.BaseUnit;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.File;

import org.junit.Test;

/**
 * <p>FileServiceImplTest class.</p>
 *
 * @author Oleg Lopatin
 * @version $Id: $Id
 * @since 0.13.3
 */
public class FileServiceImplTest extends BaseUnit {

  /**
   * <p>Constructor for FileServiceImplTest.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public FileServiceImplTest() throws Exception {
    super();
  }

  /**
   * <p>testFindByCriteria.</p>
   */
  @Test
  public void testFindByCriteria() {
    FileService fileService = BeanFactory.getFileServiceBean();

    // No results by path matching
    List<File> foundFiles = fileService.findByCriteria("https", false);
    assertEquals(0, foundFiles.size());

    foundFiles = fileService.findByCriteria("ABC015068", true);
    assertEquals(11, foundFiles.size());

    // Case insensitive search
    foundFiles = fileService.findByCriteria("abc015068", true);
    assertEquals(0, foundFiles.size());

    foundFiles = fileService.findByCriteria("abc015068", false);
    assertEquals(11, foundFiles.size());

    // Test SW Accession
    foundFiles = fileService.findByCriteria("1963", false);
    assertEquals(1, foundFiles.size());
  }
}
