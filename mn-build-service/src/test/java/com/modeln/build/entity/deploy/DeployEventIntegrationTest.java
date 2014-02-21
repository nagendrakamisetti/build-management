package com.modeln.build.entity.deploy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.deploy.DeployEvent;
import com.modeln.build.entity.deploy.DeployEventCriteria;
import com.modeln.build.enums.build.EventLevel;

public class DeployEventIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountDeployEventCriterias = em_bc.createQuery ("SELECT count(x) FROM DeployEventCriteria x");
		Number countDeployEventCriterias = (Number) queryCountDeployEventCriterias.getSingleResult ();
		assertTrue(!countDeployEventCriterias.equals(0));
		
		Query queryCountDeployEventCriteria = em_bc.createQuery("SELECT g FROM DeployEventCriteria g");
		List<DeployEventCriteria> deployEventCriterias = queryCountDeployEventCriteria.getResultList();
		assertTrue(countDeployEventCriterias.intValue() == deployEventCriterias.size());
		
		//Count number of Entities
		Query queryCountDeployEvents = em_bc.createQuery ("SELECT count(x) FROM DeployEvent x");
		Number countDeployEvents = (Number) queryCountDeployEvents.getSingleResult ();
		assertTrue(!countDeployEvents.equals(0));
		
		//Retrieve Entities
		Query queryFindAllDeployEvent = em_bc.createQuery("SELECT g FROM DeployEvent g");
		List<DeployEvent> deployEvents = queryFindAllDeployEvent.getResultList();
		assertTrue(countDeployEvents.intValue() == deployEvents.size());
		
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
		tx_bc.begin();
		em_bc.persist(deployEventCriteria);
		em_bc.persist(deployEvent);
		tx_bc.commit();
		
		assertNotNull("DeployEventCriteria ID should not be null", deployEventCriteria.getId());
		assertNotNull("DeployEvent ID should not be null", deployEvent.getId());
		
		deployEvent = em_bc.find(DeployEvent.class, deployEvent.getId());
//		assertTrue(deployEvent.getBuildVersion().equals("buildVersion"));
		assertTrue(deployEvent.getDate() != null);
		assertTrue(deployEvent.getLevel().equals(EventLevel.DEBUG));
		assertTrue(deployEvent.getMessage().equals("message"));
//		assertTrue(deployEvent.getStack().equals("stack"));
		assertTrue(deployEvent.getCriteria().getId() == deployEventCriteria.getId());
	
		
		//Update Entity	
		deployEvent.setMessage("updateMessage");
		tx_bc.begin();
		em_bc.persist(deployEvent);	
		tx_bc.commit();
		
		deployEvent = em_bc.find(DeployEvent.class, deployEvent.getId());
//		assertTrue(deployEvent.getBuildVersion().equals("buildVersion"));
		assertTrue(deployEvent.getDate() != null);
		assertTrue(deployEvent.getLevel().equals(EventLevel.DEBUG));
		assertTrue(deployEvent.getMessage().equals("updateMessage"));
//		assertTrue(deployEvent.getStack().equals("stack"));
		assertTrue(deployEvent.getCriteria().getId() == deployEventCriteria.getId());
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(deployEvent);
		em_bc.remove(deployEventCriteria);
		tx_bc.commit();
		
		Query queryFindAllDeployEventCriterias = em_bc.createQuery("SELECT u FROM DeployEventCriteria u");
		deployEventCriterias = queryFindAllDeployEventCriterias.getResultList();
		assertTrue(deployEventCriterias.size() == countDeployEventCriterias.intValue());
		
		Query queryFindAllDeployEvents = em_bc.createQuery("SELECT u FROM DeployEvent u");
		deployEvents = queryFindAllDeployEvents.getResultList();
		assertTrue(deployEvents.size() == countDeployEvents.intValue());	
	}
}