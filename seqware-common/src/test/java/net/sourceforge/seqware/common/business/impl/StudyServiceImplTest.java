package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.BaseUnit;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.InSessionExecutions;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.Log;
import org.hibernate.Session;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * <p>
 * StudyServiceImplTest class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class StudyServiceImplTest extends BaseUnit {

    private static StudyService ss;

    /**
     * <p>
     * Constructor for StudyServiceImplTest.
     * </p>
     * 
     * @throws java.lang.Exception
     *             if any.
     */
    public StudyServiceImplTest() throws Exception {
        super();
        ss = BeanFactory.getStudyServiceBean();
    }

    // @AfterClass
    // public static void tearDownAfterClass() throws Exception {
    // DatabaseCreator.markDatabaseChanged();
    // }

    /**
     * <p>
     * testFindByTitle.
     * </p>
     */
    @Test
    public void testFindByTitle() {

        List<Study> studySensitive = ss.findByTitle("AbcCo_Exome_Sequencing");
        // Should return one item.
        assertNotNull(studySensitive);

        List<Study> studyInsensitive = ss.findByTitle("abcco_Exome_Sequencing");
        // Should return one item.
        assertNotNull(studyInsensitive);

        // Look for not existing Title
        List<Study> studyNotExist = ss.findByTitle("Not Exist");
        // Should return one item.
        assertNull(studyNotExist);
    }

    // @Test
    // public void testFindByOwnerId() {
    // Registration r = registrationService.findByEmailAddressAndPassword(email, password);
    // List<Study> studies = ss.findByOwnerID(r.getRegistrationId());
    // Log.info("Count: " + studies.size());
    // // Must be 2 studies for that registrationId
    // assertEquals("Expected 2, got "+studies.size(), 2, studies.size());
    // }

    /**
     * <p>
     * testFindBySWAccession.
     * </p>
     */
    @Test
    public void testFindBySWAccession() {
        Study study = ss.findBySWAccession(120);
        assertNotNull(study);
        assertEquals("AbcCo_Exome_Sequencing", study.getTitle());
    }

    /**
     * <p>
     * testUpdateDetached.
     * </p>
     */
    @Test
    public void testUpdateDetached() {
        Session session = getSession();
        Study study = ss.findByID(12);
        // detach object closing the session
        removeSession(session);

        // create new session
        session = getSession();
        Study newStudy = ss.findByID(12);
        assertFalse(study == newStudy);

        // Update detached object
        study.setTitle("New Title");

        // Let's try to attach object
        Study attachedNewly = ss.updateDetached(study);
        assertEquals("New Title", attachedNewly.getTitle());
        removeSession(session);

        session = getSession();
        Study updatedStudy = ss.findByID(12);
        assertEquals("New Title", updatedStudy.getTitle());
        removeSession(session);
    }

    /**
     * <p>
     * testNoLazyInitializationException.
     * </p>
     */
    @Test
    public void testNoLazyInitializationException() {
        // Bind Session to the thread to prevent LazyInitializationException
        InSessionExecutions.bindSessionToThread();
        Study study = ss.findByID(10);
        Set<Processing> processings = study.getProcessings();
        Log.info("Procissings count: " + processings.size());
        Log.info("Owner is " + study.getOwner().getFirstName());
        InSessionExecutions.unBindSessionFromTheThread();
    }

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        StudyService studyService = BeanFactory.getStudyServiceBean();
        // List<Study> foundStudies = studyService.findByCriteria("HuRef", false);
        // assertEquals(1, foundStudies.size());

        List<Study> foundStudies = studyService.findByCriteria("Human", false);
        assertEquals(1, foundStudies.size());

        // Case sens
        foundStudies = studyService.findByCriteria("human", true);
        assertEquals(0, foundStudies.size());

        foundStudies = studyService.findByCriteria("human", false);
        assertEquals(1, foundStudies.size());

        // SWID
        foundStudies = studyService.findByCriteria("120", false);
        assertEquals(1, foundStudies.size());

        // Title
        foundStudies = studyService.findByCriteria("data", false);
        assertEquals(1, foundStudies.size());
    }

}
