package com.modeln.build.entity.feature;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.feature.FeatureArea;

public class FeatureAreaTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		//Create Entity
		FeatureArea featureArea = new FeatureArea();
		featureArea.setDesc("desc");
		featureArea.setEmail("email@company.com");
		featureArea.setName("name");
		
		// Persists to the database
		tx.begin();
		em.persist(featureArea);
		tx.commit();
		
		assertNotNull("FeatureArea ID should not be null", featureArea.getId());
		
		featureArea = em.find(FeatureArea.class, featureArea.getId());
		assertTrue(featureArea.getDesc().equals("desc"));
		assertTrue(featureArea.getEmail().equals("email@company.com"));
		assertTrue(featureArea.getName().equals("name"));
		
		//Update Entity	
		featureArea.setName("updatedName");
		tx.begin();
		em.persist(featureArea);	
		tx.commit();
		
		featureArea = em.find(FeatureArea.class, featureArea.getId());
		assertTrue(featureArea.getDesc().equals("desc"));
		assertTrue(featureArea.getEmail().equals("email@company.com"));
		assertTrue(featureArea.getName().equals("updatedName"));
		
		//Delete Entity
		tx.begin();
		em.remove(featureArea);
		tx.commit();
		
		Query queryFindAllFeatureAreas = em.createQuery("SELECT u FROM FeatureArea u");
		List<FeatureArea> featureAreas = queryFindAllFeatureAreas.getResultList();
		assertTrue(featureAreas.isEmpty());
		
	}

}
