/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.controller;

import io.seqware.webservice.generated.controller.StudyTypeFacadeREST;

import javax.ejb.Stateless;
import javax.ws.rs.Path;

import com.wordnik.swagger.annotations.Api;

/**
 * 
 * @author dyuen
 */
@Stateless
@Path("io.seqware.webservice.model.studytype")
@Api(value="/io.seqware.webservice.model.studytype")
public class CustomStudyTypeFacadeREST extends StudyTypeFacadeREST {
}
