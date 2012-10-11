package net.sourceforge.seqware.common.dao;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:dao-test-context.xml" })
@Transactional
public class FileAttributeDAOTest {

  @Autowired
  private FileAttributeDAO fileAttributeDao;

  @Test(expected = DataIntegrityViolationException.class)
  public void test_add_missing_required_fields_fails() {
    FileAttribute fileAttribute = new FileAttribute();
    fileAttribute.setUnit("ml");
    fileAttributeDao.add(fileAttribute);
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void test_add_missing_required_file_field_fails() {
    FileAttribute fileAttribute = new FileAttribute();
    fileAttribute.setTag("name");
    fileAttribute.setValue("value");
    fileAttributeDao.add(fileAttribute);
  }

  @Test
  public void test_add_with_all_required_fields_succeeds() throws Exception {
    FileAttribute fileAttribute = new FileAttribute();
    fileAttribute.setTag("name");
    fileAttribute.setValue("value");
    fileAttribute.setFile(new File());
    Integer id = fileAttributeDao.add(fileAttribute);
    FileAttribute actual = fileAttributeDao.get(id);
    assertThat(actual.getTag(), is("name"));
    assertThat(actual.getValue(), is("value"));
    assertThat(actual.getUnit(), nullValue());
  }

}
