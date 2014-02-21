package com.modeln.build.entity.build;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.build.BuildEventCriteria;
import com.modeln.build.enums.build.EventLevel;

public class BuildEventCriteriaTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		
		//Create Entity
		BuildEventCriteria buildEventCriteria = new BuildEventCriteria();
		buildEventCriteria.setAntTarget("antTarget");
		buildEventCriteria.setSeverity(EventLevel.DEBUG);
		buildEventCriteria.setText("text");
		buildEventCriteria.setBuildVersion("buildVersion");
		buildEventCriteria.setGroup("group");
		
		// Persists to the database
		tx.begin();
		em.persist(buildEventCriteria);
		tx.commit();
		
		assertNotNull("BuildEvenCriteria ID should not be null", buildEventCriteria.getId());
		
		buildEventCriteria = em.find(BuildEventCriteria.class, buildEventCriteria.getId());
		assertTrue(buildEventCriteria.getAntTarget().equals("antTarget"));
		assertTrue(buildEventCriteria.getSeverity().equals(EventLevel.DEBUG));
		assertTrue(buildEventCriteria.getText().equals("text"));
		assertTrue(buildEventCriteria.getBuildVersion().equals("buildVersion"));
		assertTrue(buildEventCriteria.getGroup().equals("group"));
		
		//Update Entity	
		buildEventCriteria.setText("updateText");
		tx.begin();
		em.persist(buildEventCriteria);	
		tx.commit();
		
		buildEventCriteria = em.find(BuildEventCriteria.class, buildEventCriteria.getId());
		assertTrue(buildEventCriteria.getAntTarget().equals("antTarget"));
		assertTrue(buildEventCriteria.getSeverity().equals(EventLevel.DEBUG));
		assertTrue(buildEventCriteria.getText().equals("updateText"));
		assertTrue(buildEventCriteria.getBuildVersion().equals("buildVersion"));
		assertTrue(buildEventCriteria.getGroup().equals("group"));
		
		//Delete Entity
		tx.begin();
		em.remove(buildEventCriteria);
		tx.commit();
		
		Query queryFindAllBuildEventCriterias = em.createQuery("SELECT u FROM BuildEventCriteria u");
		List<BuildEventCriteria> buildEventCriterias = queryFindAllBuildEventCriterias.getResultList();
		assertTrue(buildEventCriterias.isEmpty());
		
	}
}

