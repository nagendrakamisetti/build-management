package com.modeln.build.entity.deploy;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.deploy.DeployMetric;
import com.modeln.build.entity.deploy.id.DeployMetricId;
import com.modeln.build.enums.build.Activity;

public class DeployMetricTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		//Create Entity
		DeployMetric deployMetric = new DeployMetric();
		deployMetric.setActivity(Activity.APP_DBPOSTIMPORT);
		deployMetric.setBuildVersion("buildVersion");
		deployMetric.setStartDate(new Date());
		deployMetric.setEndDate(new Date());
		deployMetric.setHostname("hostname");
		deployMetric.setUsername("username");
		
		// Persists to the database
		tx.begin();
		em.persist(deployMetric);
		tx.commit();
		
		deployMetric = em.find(DeployMetric.class, new DeployMetricId("buildVersion", Activity.APP_DBPOSTIMPORT.getName().toLowerCase()));
		assertTrue(deployMetric.getStartDate() != null);
		assertTrue(deployMetric.getEndDate() != null);
		assertTrue(deployMetric.getHostname().equals("hostname"));
		assertTrue(deployMetric.getUsername().equals("username"));
	
		
		//Update Entity	
		deployMetric.setHostname("updateHostname");
		tx.begin();
		em.persist(deployMetric);	
		tx.commit();
		
		deployMetric = em.find(DeployMetric.class, new DeployMetricId("buildVersion", Activity.APP_DBPOSTIMPORT.getName().toLowerCase()));
		assertTrue(deployMetric.getStartDate() != null);
		assertTrue(deployMetric.getEndDate() != null);
		assertTrue(deployMetric.getHostname().equals("updateHostname"));
		assertTrue(deployMetric.getUsername().equals("username"));
		
		//Delete Entity
		tx.begin();
		em.remove(deployMetric);
		tx.commit();
		
		Query queryFindAllDeployMetrics = em.createQuery("SELECT u FROM DeployMetric u");
		List<DeployMetric> deployMetrics = queryFindAllDeployMetrics.getResultList();
		assertTrue(deployMetrics.isEmpty());
		
	}
	
}