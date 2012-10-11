package net.sourceforge.seqware.common.dao;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;

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
public class WorkflowRunAttributeDAOTest {

  @Autowired
  private WorkflowRunAttributeDAO workflowRunAttributeDao;

  @Test(expected = DataIntegrityViolationException.class)
  public void test_add_missing_required_fields_fails() {
    WorkflowRunAttribute workflowRunAttribute = new WorkflowRunAttribute();
    workflowRunAttribute.setUnit("ml");
    workflowRunAttributeDao.add(workflowRunAttribute);
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void test_add_missing_required_workflowRun_field_fails() {
    WorkflowRunAttribute workflowRunAttribute = new WorkflowRunAttribute();
    workflowRunAttribute.setTag("name");
    workflowRunAttribute.setValue("value");
    workflowRunAttributeDao.add(workflowRunAttribute);
  }

  @Test
  public void test_add_with_all_required_fields_succeeds() throws Exception {
    WorkflowRunAttribute workflowRunAttribute = new WorkflowRunAttribute();
    workflowRunAttribute.setTag("name");
    workflowRunAttribute.setValue("value");
    WorkflowRun workflowRun = new WorkflowRun();
    workflowRun.setCreateTimestamp(new Date());
    workflowRunAttribute.setWorkflowRun(workflowRun);
    Integer id = workflowRunAttributeDao.add(workflowRunAttribute);
    WorkflowRunAttribute actual = workflowRunAttributeDao.get(id);
    assertThat(actual.getTag(), is("name"));
    assertThat(actual.getValue(), is("value"));
    assertThat(actual.getUnit(), nullValue());
  }

}
