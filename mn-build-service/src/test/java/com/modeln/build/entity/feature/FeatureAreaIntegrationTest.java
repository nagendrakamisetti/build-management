package com.modeln.build.entity.feature;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.feature.FeatureArea;

public class FeatureAreaIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountFeatureAreas = em_bc.createQuery ("SELECT count(x) FROM FeatureArea x");
		Number countFeatureAreas = (Number) queryCountFeatureAreas.getSingleResult ();
		assertTrue(!countFeatureAreas.equals(0));
		
		Query queryCountFeatureArea = em_bc.createQuery("SELECT g FROM FeatureArea g");
		List<FeatureArea> featureAreas = queryCountFeatureArea.getResultList();
		assertTrue(countFeatureAreas.intValue() == featureAreas.size());
		
		//Create Entity
		FeatureArea featureArea = new FeatureArea();
		featureArea.setDesc("desc");
		featureArea.setEmail("email@company.com");
		featureArea.setName("name");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(featureArea);
		tx_bc.commit();
		
		assertNotNull("FeatureArea ID should not be null", featureArea.getId());
		
		featureArea = em_bc.find(FeatureArea.class, featureArea.getId());
		assertTrue(featureArea.getDesc().equals("desc"));
		assertTrue(featureArea.getEmail().equals("email@company.com"));
		assertTrue(featureArea.getName().equals("name"));
		
		//Update Entity	
		featureArea.setName("updatedName");
		tx_bc.begin();
		em_bc.persist(featureArea);	
		tx_bc.commit();
		
		featureArea = em_bc.find(FeatureArea.class, featureArea.getId());
		assertTrue(featureArea.getDesc().equals("desc"));
		assertTrue(featureArea.getEmail().equals("email@company.com"));
		assertTrue(featureArea.getName().equals("updatedName"));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(featureArea);
		tx_bc.commit();
		
		Query queryFindAllFeatureAreas = em_bc.createQuery("SELECT u FROM FeatureArea u");
		featureAreas = queryFindAllFeatureAreas.getResultList();
		assertTrue(featureAreas.size() == countFeatureAreas.intValue());
	}
}
