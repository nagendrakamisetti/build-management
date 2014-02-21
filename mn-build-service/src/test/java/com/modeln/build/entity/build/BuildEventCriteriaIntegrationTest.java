package com.modeln.build.entity.build;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.build.BuildEventCriteria;
import com.modeln.build.enums.build.EventLevel;

public class BuildEventCriteriaIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountBuildEventCriterias = em_bc.createQuery ("SELECT count(x) FROM BuildEventCriteria x");
		Number countBuildEventCriterias = (Number) queryCountBuildEventCriterias.getSingleResult ();
		assertTrue(!countBuildEventCriterias.equals(0));
		
		Query queryFindAllBuildEventCriteria = em_bc.createQuery("SELECT g FROM BuildEventCriteria g");
		List<BuildEventCriteria> buildEventCriterias = queryFindAllBuildEventCriteria.getResultList();
		assertTrue(countBuildEventCriterias.intValue() == buildEventCriterias.size());
		
		//Create Entity
		BuildEventCriteria buildEventCriteria = new BuildEventCriteria();
		buildEventCriteria.setAntTarget("antTarget");
		buildEventCriteria.setSeverity(EventLevel.DEBUG);
		buildEventCriteria.setText("text");
		buildEventCriteria.setBuildVersion("buildVersion");
		buildEventCriteria.setGroup("group");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(buildEventCriteria);
		tx_bc.commit();
		
		assertNotNull("BuildEvenCriteria ID should not be null", buildEventCriteria.getId());
		
		buildEventCriteria = em_bc.find(BuildEventCriteria.class, buildEventCriteria.getId());
		assertTrue(buildEventCriteria.getAntTarget().equals("antTarget"));
		assertTrue(buildEventCriteria.getSeverity().equals(EventLevel.DEBUG));
		assertTrue(buildEventCriteria.getText().equals("text"));
		assertTrue(buildEventCriteria.getBuildVersion().equals("buildVersion"));
		assertTrue(buildEventCriteria.getGroup().equals("group"));
		
		//Update Entity	
		buildEventCriteria.setText("updateText");
		tx_bc.begin();
		em_bc.persist(buildEventCriteria);	
		tx_bc.commit();
		
		buildEventCriteria = em_bc.find(BuildEventCriteria.class, buildEventCriteria.getId());
		assertTrue(buildEventCriteria.getAntTarget().equals("antTarget"));
		assertTrue(buildEventCriteria.getSeverity().equals(EventLevel.DEBUG));
		assertTrue(buildEventCriteria.getText().equals("updateText"));
		assertTrue(buildEventCriteria.getBuildVersion().equals("buildVersion"));
		assertTrue(buildEventCriteria.getGroup().equals("group"));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(buildEventCriteria);
		tx_bc.commit();
		
		Query queryFindAllBuildEventCriterias = em_bc.createQuery("SELECT u FROM BuildEventCriteria u");
		buildEventCriterias = queryFindAllBuildEventCriterias.getResultList();
		assertTrue(countBuildEventCriterias.intValue() == buildEventCriterias.size());		
	}
}

