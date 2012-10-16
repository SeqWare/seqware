package net.sourceforge.seqware.webservice.dto;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.WorkflowAttribute;

import org.junit.Test;

/**
 * <p>DtosTest class.</p>
 *
 * @author tdebat
 * @version $Id: $Id
 * @since 0.13.3
 */
public class DtosTest {

	/**
	 * <p>test_FileAttribute_to_AttributeDto.</p>
	 *
	 * @throws java.lang.Exception if any.
	 */
	@Test
	public void test_FileAttribute_to_AttributeDto() throws Exception {
		FileAttribute fileAttribute = new FileAttribute();
		fileAttribute.setTag("name");
		fileAttribute.setValue("value");
		AttributeDto attributeDto = Dtos.asDto(fileAttribute);
		assertThat(attributeDto.getName(), is(fileAttribute.getTag()));
		assertThat(attributeDto.getValue(), is(fileAttribute.getValue()));
		assertThat(attributeDto.getUnit(), nullValue());
	}

	/**
	 * <p>test_WorkflowAttribute_to_AttributeDto.</p>
	 *
	 * @throws java.lang.Exception if any.
	 */
	@Test
	public void test_WorkflowAttribute_to_AttributeDto() throws Exception {
		WorkflowAttribute workflowAttribute = new WorkflowAttribute();
		workflowAttribute.setTag("name");
		workflowAttribute.setValue("value");
		AttributeDto attributeDto = Dtos.asDto(workflowAttribute);
		assertThat(attributeDto.getName(), is(workflowAttribute.getTag()));
		assertThat(attributeDto.getValue(), is(workflowAttribute.getValue()));
		assertThat(attributeDto.getUnit(), nullValue());
	}
	
	/**
	 * <p>test_AttributeDto_to_FileAttribute.</p>
	 *
	 * @throws java.lang.Exception if any.
	 */
	@Test
	public void test_AttributeDto_to_FileAttribute() throws Exception {
		AttributeDto attributeDto = new AttributeDto();
		attributeDto.setName("name");
		attributeDto.setValue("value");
		FileAttribute fileAttribute = Dtos.fromDto(attributeDto, FileAttribute.class);
		assertThat(fileAttribute.getTag(), is(attributeDto.getName()));
		assertThat(fileAttribute.getValue(), is(attributeDto.getValue()));
		assertThat(fileAttribute.getUnit(), nullValue());
		assertThat(fileAttribute instanceof FileAttribute, is(true));
	}
	
	/**
	 * <p>test_AttributeDto_to_WorkflowAttribute.</p>
	 *
	 * @throws java.lang.Exception if any.
	 */
	@Test
	public void test_AttributeDto_to_WorkflowAttribute() throws Exception {
		AttributeDto attributeDto = new AttributeDto();
		attributeDto.setName("name");
		attributeDto.setValue("value");
		WorkflowAttribute workflowAttribute = Dtos.fromDto(attributeDto, WorkflowAttribute.class);
		assertThat(workflowAttribute.getTag(), is(attributeDto.getName()));
		assertThat(workflowAttribute.getValue(), is(attributeDto.getValue()));
		assertThat(workflowAttribute.getUnit(), nullValue());
		assertThat(workflowAttribute instanceof WorkflowAttribute, is(true));
	}
}
