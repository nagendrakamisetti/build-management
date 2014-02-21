package com.modeln.build.entity.feature;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.feature.FeatureArea;
import com.modeln.build.entity.feature.FeatureAreaMap;

public class FeatureAreaMapTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
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
		tx.begin();
		em.persist(featureArea);
		em.persist(featureAreaMap);
		tx.commit();
		
		assertNotNull("FeatureAreaMap ID should not be null", featureAreaMap.getFeature());
		
		featureAreaMap = em.find(FeatureAreaMap.class, featureAreaMap.getFeature());
		assertTrue(featureAreaMap.getArea().getId() == featureArea.getId());
		
		// Cannot Update Entity here	
		
		//Delete Entity
		tx.begin();
		em.remove(featureAreaMap);
		em.remove(featureArea);
		tx.commit();
		
		Query queryFindAllFeatureAreas = em.createQuery("SELECT u FROM FeatureArea u");
		List<FeatureArea> featureAreas = queryFindAllFeatureAreas.getResultList();
		assertTrue(featureAreas.isEmpty());
		
		Query queryFindAllFeatureAreaMaps = em.createQuery("SELECT u FROM FeatureAreaMap u");
		List<FeatureAreaMap> featureAreaMaps = queryFindAllFeatureAreaMaps.getResultList();
		assertTrue(featureAreaMaps.isEmpty());
		
	}

}
