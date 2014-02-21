package com.modeln.build.entity.build;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.build.BuildEvent;
import com.modeln.build.entity.build.BuildEventCriteria;
import com.modeln.build.enums.build.EventLevel;

public class BuildEventIntegrationTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		//Create Depend Entities
		BuildEventCriteria buildEventCriteria = new BuildEventCriteria();
		buildEventCriteria.setAntTarget("antTarget");
		buildEventCriteria.setSeverity(EventLevel.DEBUG);
		buildEventCriteria.setText("text");
		buildEventCriteria.setBuildVersion("buildVersion");
		buildEventCriteria.setGroup("group");

		//Create Entity
		BuildEvent buildEvent = new BuildEvent();
		buildEvent.setBuildVersion("buildVersion");
		buildEvent.setDate(new Date());
		buildEvent.setLevel(EventLevel.DEBUG);
		buildEvent.setMessage("message");
		buildEvent.setStack("stack");
		buildEvent.setCriteria(buildEventCriteria);
		
		// Persists to the database
		tx.begin();
		em.persist(buildEventCriteria);
		em.persist(buildEvent);
		tx.commit();
		
		assertNotNull("BuildEventCriteria ID should not be null", buildEventCriteria.getId());
		assertNotNull("BuildEvent ID should not be null", buildEvent.getId());
		
		buildEvent = em.find(BuildEvent.class, buildEvent.getId());
		assertTrue(buildEvent.getBuildVersion().equals("buildVersion"));
		assertTrue(buildEvent.getDate() != null);
		assertTrue(buildEvent.getLevel().equals(EventLevel.DEBUG));
		assertTrue(buildEvent.getMessage().equals("message"));
		assertTrue(buildEvent.getStack().equals("stack"));
		assertTrue(buildEvent.getCriteria().getId() == buildEventCriteria.getId());
	
		
		//Update Entity	
		buildEvent.setMessage("updateMessage");
		tx.begin();
		em.persist(buildEvent);	
		tx.commit();
		
		buildEvent = em.find(BuildEvent.class, buildEvent.getId());
		assertTrue(buildEvent.getBuildVersion().equals("buildVersion"));
		assertTrue(buildEvent.getDate() != null);
		assertTrue(buildEvent.getLevel().equals(EventLevel.DEBUG));
		assertTrue(buildEvent.getMessage().equals("updateMessage"));
		assertTrue(buildEvent.getStack().equals("stack"));
		assertTrue(buildEvent.getCriteria().getId() == buildEventCriteria.getId());
		
		//Delete Entity
		tx.begin();
		em.remove(buildEvent);
		em.remove(buildEventCriteria);
		tx.commit();
		
		Query queryFindAllBuildEventCriterias = em.createQuery("SELECT u FROM BuildEventCriteria u");
		List<BuildEventCriteria> buildEventCriterias = queryFindAllBuildEventCriterias.getResultList();
		assertTrue(buildEventCriterias.isEmpty());
		
		Query queryFindAllBuildEvents = em.createQuery("SELECT u FROM BuildEvent u");
		List<BuildEvent> buildEvents = queryFindAllBuildEvents.getResultList();
		assertTrue(buildEvents.isEmpty());
		
	}
	
//	@SuppressWarnings("unchecked")
//	@Test
//	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
//		Query queryCountBuildEventCriterias = em_bc.createQuery ("SELECT count(x) FROM BuildEventCriteria x");
//		Number countBuildEventCriterias = (Number) queryCountBuildEventCriterias.getSingleResult ();
//		assertTrue(!countBuildEventCriterias.equals(0));
//		
//		Query queryCountBuildEventCriteria = em_bc.createQuery("SELECT g FROM BuildEventCriteria g");
//		List<BuildEventCriteria> buildEventCriterias = queryCountBuildEventCriteria.getResultList();
//		assertTrue(countBuildEventCriterias.intValue() == buildEventCriterias.size());
//		
//		//Count number of Entities
//		Query queryCountBuildEvents = em_bc.createQuery ("SELECT count(x) FROM BuildEvent x");
//		Number countBuildEvents = (Number) queryCountBuildEvents.getSingleResult ();
//		assertTrue(!countBuildEvents.equals(0));
//		
//		//Retrieve Entities
//		Query queryFindAllBuildEvent = em_bc.createQuery("SELECT g FROM BuildEvent g");
//		List<BuildEvent> buildEvents = queryFindAllBuildEvent.getResultList();
//		assertTrue(countBuildEvents.intValue() == buildEvents.size());
//		
//		//Create Depend Entities
//		BuildEventCriteria buildEventCriteria = new BuildEventCriteria();
//		buildEventCriteria.setAntTarget("antTarget");
//		buildEventCriteria.setSeverity(EventLevel.DEBUG);
//		buildEventCriteria.setText("text");
//		buildEventCriteria.setBuildVersion("buildVersion");
//		buildEventCriteria.setGroup("group");
//
//		//Create Entity
//		BuildEvent buildEvent = new BuildEvent();
//		buildEvent.setBuildVersion("buildVersion");
//		buildEvent.setDate(new Date());
//		buildEvent.setLevel(EventLevel.DEBUG);
//		buildEvent.setMessage("message");
//		buildEvent.setStack("stack");
//		buildEvent.setCriteria(buildEventCriteria);
//		
//		// Persists to the database
//		tx_bc.begin();
//		em_bc.persist(buildEventCriteria);
//		em_bc.persist(buildEvent);
//		tx_bc.commit();
//		
//		assertNotNull("BuildEventCriteria ID should not be null", buildEventCriteria.getId());
//		assertNotNull("BuildEvent ID should not be null", buildEvent.getId());
//		
//		buildEvent = em_bc.find(BuildEvent.class, buildEvent.getId());
//		assertTrue(buildEvent.getBuildVersion().equals("buildVersion"));
//		assertTrue(buildEvent.getDate() != null);
//		assertTrue(buildEvent.getLevel().equals(EventLevel.DEBUG));
//		assertTrue(buildEvent.getMessage().equals("message"));
//		assertTrue(buildEvent.getStack().equals("stack"));
//		assertTrue(buildEvent.getCriteria().getId() == buildEventCriteria.getId());
//	
//		
//		//Update Entity	
//		buildEvent.setMessage("updateMessage");
//		tx_bc.begin();
//		em_bc.persist(buildEvent);	
//		tx_bc.commit();
//		
//		buildEvent = em_bc.find(BuildEvent.class, buildEvent.getId());
//		assertTrue(buildEvent.getBuildVersion().equals("buildVersion"));
//		assertTrue(buildEvent.getDate() != null);
//		assertTrue(buildEvent.getLevel().equals(EventLevel.DEBUG));
//		assertTrue(buildEvent.getMessage().equals("updateMessage"));
//		assertTrue(buildEvent.getStack().equals("stack"));
//		assertTrue(buildEvent.getCriteria().getId() == buildEventCriteria.getId());
//		
//		//Delete Entity
//		tx_bc.begin();
//		em_bc.remove(buildEvent);
//		em_bc.remove(buildEventCriteria);
//		tx_bc.commit();
//		
//		Query queryFindAllBuildEventCriterias = em_bc.createQuery("SELECT u FROM BuildEventCriteria u");
//		buildEventCriterias = queryFindAllBuildEventCriterias.getResultList();
//		assertTrue(buildEventCriterias.size() == countBuildEventCriterias.intValue());
//		
//		Query queryFindAllBuildEvents = em_bc.createQuery("SELECT u FROM BuildEvent u");
//		buildEvents = queryFindAllBuildEvents.getResultList();
//		assertTrue(buildEvents.size() == countBuildEvents.intValue());	
//	}
}