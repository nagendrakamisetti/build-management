package com.modeln.build.entity.deploy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.deploy.DeployEventCriteria;
import com.modeln.build.enums.build.EventLevel;

public class DeployEventCriteriaIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountDeployEventCriterias = em_bc.createQuery ("SELECT count(x) FROM DeployEventCriteria x");
		Number countDeployEventCriterias = (Number) queryCountDeployEventCriterias.getSingleResult ();
		assertTrue(!countDeployEventCriterias.equals(0));
		
		Query queryFindAllDeployEventCriteria = em_bc.createQuery("SELECT g FROM DeployEventCriteria g");
		List<DeployEventCriteria> deployEventCriterias = queryFindAllDeployEventCriteria.getResultList();
		assertTrue(countDeployEventCriterias.intValue() == deployEventCriterias.size());
		
		//Create Entity
		DeployEventCriteria deployEventCriteria = new DeployEventCriteria();
//		deployEventCriteria.setAntTarget("antTarget");
		deployEventCriteria.setSeverity(EventLevel.DEBUG);
		deployEventCriteria.setText("text");
//		deployEventCriteria.setBuildVersion("buildVersion");
//		deployEventCriteria.setGroup("group");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(deployEventCriteria);
		tx_bc.commit();
		
		assertNotNull("DeployEvenCriteria ID should not be null", deployEventCriteria.getId());
		
		deployEventCriteria = em_bc.find(DeployEventCriteria.class, deployEventCriteria.getId());
//		assertTrue(deployEventCriteria.getAntTarget().equals("antTarget"));
		assertTrue(deployEventCriteria.getSeverity().equals(EventLevel.DEBUG));
		assertTrue(deployEventCriteria.getText().equals("text"));
//		assertTrue(deployEventCriteria.getBuildVersion().equals("buildVersion"));
//		assertTrue(deployEventCriteria.getGroup().equals("group"));
		
		//Update Entity	
		deployEventCriteria.setText("updateText");
		tx_bc.begin();
		em_bc.persist(deployEventCriteria);	
		tx_bc.commit();
		
		deployEventCriteria = em_bc.find(DeployEventCriteria.class, deployEventCriteria.getId());
//		assertTrue(deployEventCriteria.getAntTarget().equals("antTarget"));
		assertTrue(deployEventCriteria.getSeverity().equals(EventLevel.DEBUG));
		assertTrue(deployEventCriteria.getText().equals("updateText"));
//		assertTrue(deployEventCriteria.getBuildVersion().equals("buildVersion"));
//		assertTrue(deployEventCriteria.getGroup().equals("group"));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(deployEventCriteria);
		tx_bc.commit();
		
		Query queryFindAllDeployEventCriterias = em_bc.createQuery("SELECT u FROM DeployEventCriteria u");
		deployEventCriterias = queryFindAllDeployEventCriterias.getResultList();
		assertTrue(countDeployEventCriterias.intValue() == deployEventCriterias.size());		
	}
}