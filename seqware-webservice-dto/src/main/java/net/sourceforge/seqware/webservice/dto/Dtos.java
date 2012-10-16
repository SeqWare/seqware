package net.sourceforge.seqware.webservice.dto;

import net.sourceforge.seqware.common.model.Attribute;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SampleSearch;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Methods to convert between domain objects and dtos.
 *
 * @author tdebat
 * @version $Id: $Id
 */
public final class Dtos {

	/**
	 * <p>asDto.</p>
	 *
	 * @param from a {@link net.sourceforge.seqware.common.model.SampleSearch} object.
	 * @return a {@link net.sourceforge.seqware.webservice.dto.SampleSearchDto} object.
	 */
	public static SampleSearchDto asDto(SampleSearch from) {
		SampleSearchDto dto = new SampleSearchDto();
		return dto;
	}

	// Todo remove this one after changing units method name.
	/**
	 * <p>sampleAttributeAsDto.</p>
	 *
	 * @param from a {@link net.sourceforge.seqware.common.model.SampleAttribute} object.
	 * @return a {@link net.sourceforge.seqware.webservice.dto.AttributeDto} object.
	 */
	public static AttributeDto sampleAttributeAsDto(SampleAttribute from) {
		AttributeDto dto = new AttributeDto();
		dto.setName(from.getTag());
		dto.setValue(from.getValue());
		dto.setUnit(from.getUnits());
		return dto;
	}

	/**
	 * <p>asDto.</p>
	 *
	 * @param from a {@link net.sourceforge.seqware.common.model.Attribute} object.
	 * @return a {@link net.sourceforge.seqware.webservice.dto.AttributeDto} object.
	 */
	public static AttributeDto asDto(Attribute from) {
		AttributeDto dto = new AttributeDto();
		dto.setName(from.getTag());
		dto.setValue(from.getValue());
		dto.setUnit(from.getUnit());
		return dto;
	}

	/**
	 * <p>fromDto.</p>
	 *
	 * @param attributeDto a {@link net.sourceforge.seqware.webservice.dto.AttributeDto} object.
	 * @param clazz a {@link java.lang.Class} object.
	 * @param <T> a T object.
	 * @return a T object.
	 * @throws java.lang.InstantiationException if any.
	 * @throws java.lang.IllegalAccessException if any.
	 */
	public static <T extends Attribute> T fromDto(AttributeDto attributeDto, Class<T> clazz)
			throws InstantiationException, IllegalAccessException {
		T attribute = clazz.newInstance();
		attribute.setTag(attributeDto.getName());
		attribute.setValue(attributeDto.getValue());
		if (attributeDto.getUnit() != null) {
			attribute.setUnit(attributeDto.getUnit());
		}
		return attribute;
	}

	/**
	 * <p>asDto.</p>
	 *
	 * @param from a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @return a {@link net.sourceforge.seqware.webservice.dto.OwnerDto} object.
	 */
	public static OwnerDto asDto(Registration from) {
		OwnerDto dto = new OwnerDto();
		dto.setEmail(from.getEmailAddress());
		dto.setFirstName(from.getFirstName());
		dto.setLastName(from.getLastName());
		dto.setInstitution(from.getInstitution());
		return dto;
	}

	/**
	 * <p>atDto.</p>
	 *
	 * @param from a {@link net.sourceforge.seqware.common.model.Organism} object.
	 * @return a {@link net.sourceforge.seqware.webservice.dto.OrganismDto} object.
	 */
	public static OrganismDto atDto(Organism from) {
		OrganismDto dto = new OrganismDto();
		dto.setName(from.getName());
		dto.setCode(from.getCode());
		dto.setNcbiTaxonomyId(from.getNcbiTaxId());
		return dto;
	}

	/**
	 * <p>asDto.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @return a {@link net.sourceforge.seqware.webservice.dto.LibraryDto} object.
	 */
	public static LibraryDto asDto(Sample sample) {
		DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();
		LibraryDto dto = new LibraryDto();
		dto.setName(sample.getName());
		dto.setDescription(sample.getDescription());
		dto.setCreateTimeStamp(dateTimeFormatter.print(sample.getCreateTimestamp().getTime()));
		dto.setUpdateTimeStamp(dateTimeFormatter.print(sample.getUpdateTimestamp().getTime()));
		dto.setOwner(Dtos.asDto(sample.getOwner()));
		dto.setOrganism(Dtos.atDto(sample.getOrganism()));

		return dto;
	}

	/**
	 * <p>asDto.</p>
	 *
	 * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
	 * @return a {@link net.sourceforge.seqware.webservice.dto.IusDto} object.
	 */
	public static IusDto asDto(IUS ius) {
		DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();
		IusDto dto = new IusDto();
		dto.setSwa(ius.getSwAccession());
		dto.setSkip(ius.getSkip());
		dto.setCreateTimeStamp(dateTimeFormatter.print(ius.getCreateTimestamp().getTime()));
		dto.setUpdateTimeStamp(dateTimeFormatter.print(ius.getUpdateTimestamp().getTime()));
		if (ius.getTag() != null) {
			dto.setBarcode(ius.getTag());
		}
		return dto;
	}

	/**
	 * <p>asDto.</p>
	 *
	 * @param file a {@link net.sourceforge.seqware.common.model.File} object.
	 * @return a {@link net.sourceforge.seqware.webservice.dto.FileDto} object.
	 */
	public static FileDto asDto(File file) {
		FileDto dto = new FileDto();
		dto.setFilePath(file.getFilePath());
		if (file.getMetaType() != null) {
			dto.setMetaType(file.getMetaType());
		}
		if (file.getDescription() != null) {
			dto.setDescription(file.getDescription());
		}
		if (file.getMd5sum() != null) {
			dto.setMd5sum(file.getMd5sum());
		}
		if (file.getSize() != null) {
			dto.setSize(file.getSize());
		}
		if (file.getType() != null) {
			dto.setType(file.getType());
		}
		return dto;
	}
}
