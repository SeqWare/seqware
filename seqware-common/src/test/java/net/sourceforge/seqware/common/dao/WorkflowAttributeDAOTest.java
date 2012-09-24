package net.sourceforge.seqware.common.dao;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowAttribute;

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
public class WorkflowAttributeDAOTest {

  @Autowired
  private WorkflowAttributeDAO workflowAttributeDao;

  @Test(expected = DataIntegrityViolationException.class)
  public void test_add_missing_required_fields_fails() {
    WorkflowAttribute workflowAttribute = new WorkflowAttribute();
    workflowAttribute.setUnit("ml");
    workflowAttributeDao.add(workflowAttribute);
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void test_add_missing_required_workflow_field_fails() {
    WorkflowAttribute workflowAttribute = new WorkflowAttribute();
    workflowAttribute.setTag("name");
    workflowAttribute.setValue("value");
    workflowAttributeDao.add(workflowAttribute);
  }

  @Test
  public void test_add_with_all_required_fields_succeeds() throws Exception {
    WorkflowAttribute workflowAttribute = new WorkflowAttribute();
    workflowAttribute.setTag("name");
    workflowAttribute.setValue("value");
    Workflow workflow = new Workflow();
    workflow.setCreateTimestamp(new Date());
    workflowAttribute.setWorkflow(workflow);
    Integer id = workflowAttributeDao.add(workflowAttribute);
    WorkflowAttribute actual = workflowAttributeDao.get(id);
    assertThat(actual.getTag(), is("name"));
    assertThat(actual.getValue(), is("value"));
    assertThat(actual.getUnit(), nullValue());
  }

}
