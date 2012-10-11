package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.naming.NamingException;

import net.sourceforge.seqware.common.BaseUnit;
import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.InSessionExecutions;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.testtools.DatabaseCreator;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Test;

public class ExperimentServiceImplTest extends BaseUnit {

  public ExperimentServiceImplTest() throws Exception {
    super();
  }

  @AfterClass
  public static void tearDown() throws NamingException {
  }

  @Test
  public void testFindByTitle() {

    ExperimentService experimentService = BeanFactory.getExperimentServiceBean();
    Experiment experiment = experimentService.findByTitle("MixExp1");
    assertNotNull(experiment);

    Experiment experimentCaseInsensitive = experimentService.findByTitle("mixexp1");
    assertNotNull(experimentCaseInsensitive);
    assertEquals(experiment.getTitle(), experimentCaseInsensitive.getTitle());
  }

//  @Test
//  public void testFindByOwnerId() {
//    Registration r = registrationService.findByEmailAddressAndPassword(email, password);
//    assertNotNull(r);
//    List<Experiment> experiments = BeanFactory.getExperimentServiceBean().findByOwnerID(r.getRegistrationId());
//    assertNotNull(experiments);
//    Log.info("Count: " + experiments.size());
//    // Must be 3 experiments for that registrationId
//    assertEquals("Expected 3, got "+experiments.size(), 3, experiments.size());
//  }

  @Test
  public void testFindBySWAccession() {
    Experiment experiment = BeanFactory.getExperimentServiceBean().findBySWAccession(834);
    assertNotNull(experiment);
    assertEquals("Sample_Exome_ABC015068", experiment.getTitle());
  }

  @Test
  public void testNoLazyInitializationException() {
    // Bind Session to the thread to prevent LazyInitializationException
    InSessionExecutions.bindSessionToThread();
    Experiment experiment = BeanFactory.getExperimentServiceBean().findByID(6);
    Log.info("Owner is " + experiment.getOwner().getFirstName());
    InSessionExecutions.unBindSessionFromTheThread();
  }

  @Test
  public void testUpdateDetached() {
    Session session = getSession();
    Experiment experiment = BeanFactory.getExperimentServiceBean().findByID(6);
    // detach object closing the session
    removeSession(session);

    // create new session
    session = getSession();
    Experiment newExperiment = BeanFactory.getExperimentServiceBean().findByID(6);
    assertFalse(experiment == newExperiment);

    // Update detached object
    experiment.setTitle("New Title");

    // Let's try to attach object
    Experiment attachedNewly = BeanFactory.getExperimentServiceBean().updateDetached(experiment);
    assertEquals("New Title", attachedNewly.getTitle());
    removeSession(session);

    session = getSession();
    Experiment updatedExperiment = BeanFactory.getExperimentServiceBean().findByID(6);
    assertEquals("New Title", updatedExperiment.getTitle());
    removeSession(session);

    DatabaseCreator.markDatabaseChanged();
  }

  @Test
  public void testFindByCriteria() {
    ExperimentService experimentService = BeanFactory.getExperimentServiceBean();
    List<Experiment> foundExperiments = experimentService.findByCriteria("Exp", true);
    assertEquals(1, foundExperiments.size());

    foundExperiments = experimentService.findByCriteria("834", true);
    assertEquals(1, foundExperiments.size());

    foundExperiments = experimentService.findByCriteria("(test", true);
    assertEquals(0, foundExperiments.size());

    // Test case insensitive search
    foundExperiments = experimentService.findByCriteria("test", false);
    assertEquals(2, foundExperiments.size());

    // Description
    foundExperiments = experimentService.findByCriteria("genome", false);
    assertEquals(1, foundExperiments.size());
  }

}
