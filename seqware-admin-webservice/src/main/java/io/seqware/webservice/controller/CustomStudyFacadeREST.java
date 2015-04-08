/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.StudyFacadeREST;
import io.seqware.webservice.generated.model.Study;
import io.seqware.webservice.generated.model.StudyAttribute;

import javax.ejb.Stateless;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * 
 * @author dyuen
 */
@Stateless
@Api(value="/io.seqware.webservice.model.study")
@Path("io.seqware.webservice.model.study")
public class CustomStudyFacadeREST extends StudyFacadeREST {
	
        @ApiOperation(value="Creates a study")
	@Path("/createStudy")
	@POST
	@Consumes({ javax.ws.rs.core.MediaType.APPLICATION_JSON, javax.ws.rs.core.MediaType.APPLICATION_XML })
	@Produces({ javax.ws.rs.core.MediaType.APPLICATION_XML })
	public Study createStudy(Study s)
	{
		//Study s = (Study) args.get("study");
		//List<StudyAttribute> attributes = (List<StudyAttribute>) args.get("attributes");
		try
		{
			super.create(s);
			System.out.println("Study ID: "+s.getStudyId());

			for (StudyAttribute attrib : s.getStudyAttributeCollection())
			{
				attrib.setStudyId(s);
				this.getEntityManager().persist(attrib);
				System.out.println("Attribute ID: "+attrib.getStudyAttributeId());
			}
		}
		catch (ConstraintViolationException e)
		{
			System.out.println("ContraintViolations detected: "+e.getMessage());
			for(ConstraintViolation<?> e1 : e.getConstraintViolations())
			{
				System.out.println(e1.getInvalidValue());
				System.out.println(e1.getMessage());
				System.out.println(e1);
				System.out.println(e1.getConstraintDescriptor());
				System.out.println(e1.getPropertyPath());
			}
		}
		return s;
	}
}
