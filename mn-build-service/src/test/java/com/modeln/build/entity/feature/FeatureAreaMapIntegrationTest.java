package com.modeln.build.entity.feature;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.feature.FeatureArea;
import com.modeln.build.entity.feature.FeatureAreaMap;

public class FeatureAreaMapIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountFeatureAreas = em_bc.createQuery ("SELECT count(x) FROM FeatureArea x");
		Number countFeatureAreas = (Number) queryCountFeatureAreas.getSingleResult ();
		assertTrue(!countFeatureAreas.equals(0));
		
		Query queryCountFeatureArea = em_bc.createQuery("SELECT g FROM FeatureArea g");
		List<FeatureArea> featureAreas = queryCountFeatureArea.getResultList();
		assertTrue(countFeatureAreas.intValue() == featureAreas.size());
		
		Query queryCountFeatureAreaMaps = em_bc.createQuery ("SELECT count(x) FROM FeatureAreaMap x");
		Number countFeatureAreaMaps = (Number) queryCountFeatureAreaMaps.getSingleResult ();
		assertTrue(!countFeatureAreaMaps.equals(0));
		
		Query queryCountFeatureAreaMap = em_bc.createQuery("SELECT g FROM FeatureAreaMap g");
		List<FeatureAreaMap> featureAreaMaps = queryCountFeatureAreaMap.getResultList();
		assertTrue(countFeatureAreaMaps.intValue() == featureAreaMaps.size());
		
		//Create Dependent Entity
		FeatureArea featureArea = new FeatureArea();
		featureArea.setDesc("desc");
		featureArea.setEmail("email@company.com");
		featureArea.setName("name");
		
		//Create Entity
		FeatureAreaMap featureAreaMap = new FeatureAreaMap();
		featureAreaMap.setArea(featureArea);
		featureAreaMap.setFeature("feature");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(featureArea);
		em_bc.persist(featureAreaMap);
		tx_bc.commit();
		
		assertNotNull("FeatureAreaMap ID should not be null", featureAreaMap.getFeature());
		
		featureAreaMap = em_bc.find(FeatureAreaMap.class, featureAreaMap.getFeature());
		assertTrue(featureAreaMap.getArea().getId() == featureArea.getId());
		
		// Cannot Update Entity here	
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(featureAreaMap);
		em_bc.remove(featureArea);
		tx_bc.commit();
		
		Query queryFindAllFeatureAreas = em_bc.createQuery("SELECT u FROM FeatureArea u");
		featureAreas = queryFindAllFeatureAreas.getResultList();
		assertTrue(featureAreas.size() == countFeatureAreas.intValue());
		
		Query queryFindAllFeatureAreaMaps = em_bc.createQuery("SELECT u FROM FeatureAreaMap u");
		featureAreaMaps = queryFindAllFeatureAreaMaps.getResultList();
		assertTrue(featureAreaMaps.size() == countFeatureAreaMaps.intValue());
	}
}
