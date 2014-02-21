package com.modeln.build.entity.deploy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.deploy.DeployEventCriteria;
import com.modeln.build.enums.build.EventLevel;

public class DeployEventCriteriaTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		
		//Create Entity
		DeployEventCriteria deployEventCriteria = new DeployEventCriteria();
//		deployEventCriteria.setAntTarget("antTarget");
		deployEventCriteria.setSeverity(EventLevel.DEBUG);
		deployEventCriteria.setText("text");
//		deployEventCriteria.setBuildVersion("buildVersion");
//		deployEventCriteria.setGroup("group");
		
		// Persists to the database
		tx.begin();
		em.persist(deployEventCriteria);
		tx.commit();
		
		assertNotNull("DeployEventCriteria ID should not be null", deployEventCriteria.getId());
		
		deployEventCriteria = em.find(DeployEventCriteria.class, deployEventCriteria.getId());
//		assertTrue(deployEventCriteria.getAntTarget().equals("antTarget"));
		assertTrue(deployEventCriteria.getSeverity().equals(EventLevel.DEBUG));
		assertTrue(deployEventCriteria.getText().equals("text"));
//		assertTrue(deployEventCriteria.getBuildVersion().equals("buildVersion"));
//		assertTrue(deployEventCriteria.getGroup().equals("group"));
		
		//Update Entity	
		deployEventCriteria.setText("updateText");
		tx.begin();
		em.persist(deployEventCriteria);	
		tx.commit();
		
		deployEventCriteria = em.find(DeployEventCriteria.class, deployEventCriteria.getId());
//		assertTrue(deployEventCriteria.getAntTarget().equals("antTarget"));
		assertTrue(deployEventCriteria.getSeverity().equals(EventLevel.DEBUG));
		assertTrue(deployEventCriteria.getText().equals("updateText"));
//		assertTrue(deployEventCriteria.getBuildVersion().equals("buildVersion"));
//		assertTrue(deployEventCriteria.getGroup().equals("group"));
		
		//Delete Entity
		tx.begin();
		em.remove(deployEventCriteria);
		tx.commit();
		
		Query queryFindAllDeployEventCriterias = em.createQuery("SELECT u FROM DeployEventCriteria u");
		List<DeployEventCriteria> deployEventCriterias = queryFindAllDeployEventCriterias.getResultList();
		assertTrue(deployEventCriterias.isEmpty());
		
	}
}