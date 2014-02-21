package com.modeln.build.entity.deploy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.deploy.DeployEvent;
import com.modeln.build.entity.deploy.DeployEventCriteria;
import com.modeln.build.enums.build.EventLevel;

public class DeployEventTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		//Create Depend Entities
		DeployEventCriteria deployEventCriteria = new DeployEventCriteria();
//		deployEventCriteria.setAntTarget("antTarget");
		deployEventCriteria.setSeverity(EventLevel.DEBUG);
		deployEventCriteria.setText("text");
//		deployEventCriteria.setBuildVersion("buildVersion");
//		deployEventCriteria.setGroup("group");

		//Create Entity
		DeployEvent deployEvent = new DeployEvent();
//		deployEvent.setBuildVersion("buildVersion");
		deployEvent.setDate(new Date());
		deployEvent.setLevel(EventLevel.DEBUG);
		deployEvent.setMessage("message");
//		deployEvent.setStack("stack");
		deployEvent.setCriteria(deployEventCriteria);
		
		// Persists to the database
		tx.begin();
		em.persist(deployEventCriteria);
		em.persist(deployEvent);
		tx.commit();
		
		assertNotNull("DeployEventCriteria ID should not be null", deployEventCriteria.getId());
		assertNotNull("DeployEvent ID should not be null", deployEvent.getId());
		
		deployEvent = em.find(DeployEvent.class, deployEvent.getId());
//		assertTrue(deployEvent.getBuildVersion().equals("buildVersion"));
		assertTrue(deployEvent.getDate() != null);
		assertTrue(deployEvent.getLevel().equals(EventLevel.DEBUG));
		assertTrue(deployEvent.getMessage().equals("message"));
//		assertTrue(deployEvent.getStack().equals("stack"));
		assertTrue(deployEvent.getCriteria().getId() == deployEventCriteria.getId());
	
		
		//Update Entity	
		deployEvent.setMessage("updateMessage");
		tx.begin();
		em.persist(deployEvent);	
		tx.commit();
		
		deployEvent = em.find(DeployEvent.class, deployEvent.getId());
//		assertTrue(deployEvent.getBuildVersion().equals("buildVersion"));
		assertTrue(deployEvent.getDate() != null);
		assertTrue(deployEvent.getLevel().equals(EventLevel.DEBUG));
		assertTrue(deployEvent.getMessage().equals("updateMessage"));
//		assertTrue(deployEvent.getStack().equals("stack"));
		assertTrue(deployEvent.getCriteria().getId() == deployEventCriteria.getId());
		
		//Delete Entity
		tx.begin();
		em.remove(deployEvent);
		em.remove(deployEventCriteria);
		tx.commit();
		
		Query queryFindAllDeployEventCriterias = em.createQuery("SELECT u FROM DeployEventCriteria u");
		List<DeployEventCriteria> deployEventCriterias = queryFindAllDeployEventCriterias.getResultList();
		assertTrue(deployEventCriterias.isEmpty());
		
		Query queryFindAllDeployEvents = em.createQuery("SELECT u FROM DeployEvent u");
		List<DeployEvent> deployEvents = queryFindAllDeployEvents.getResultList();
		assertTrue(deployEvents.isEmpty());
		
	}

}