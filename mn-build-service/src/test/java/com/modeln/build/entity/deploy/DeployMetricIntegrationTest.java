package com.modeln.build.entity.deploy;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.deploy.DeployMetric;
import com.modeln.build.entity.deploy.id.DeployMetricId;
import com.modeln.build.enums.build.Activity;

public class DeployMetricIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountDeployMetrics = em_bc.createQuery ("SELECT count(x) FROM DeployMetric x");
		Number countDeployMetrics = (Number) queryCountDeployMetrics.getSingleResult ();
		assertTrue(!countDeployMetrics.equals(0));
		
		Query queryCountDeployMetric = em_bc.createQuery("SELECT g FROM DeployMetric g");
		queryCountDeployMetric.setMaxResults(10);
		List<DeployMetric> deployMetrics = queryCountDeployMetric.getResultList();
		assertTrue(deployMetrics.size() != 0);
		
		//Create Entity
		DeployMetric deployMetric = new DeployMetric();
		deployMetric.setActivity(Activity.APP_DBPOSTIMPORT);
		deployMetric.setBuildVersion("buildVersion");
		deployMetric.setStartDate(new Date());
		deployMetric.setEndDate(new Date());
		deployMetric.setHostname("hostname");
		deployMetric.setUsername("username");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(deployMetric);
		tx_bc.commit();
		
		deployMetric = em_bc.find(DeployMetric.class, new DeployMetricId("buildVersion", Activity.APP_DBPOSTIMPORT.getName().toLowerCase()));
		assertTrue(deployMetric.getStartDate() != null);
		assertTrue(deployMetric.getEndDate() != null);
		assertTrue(deployMetric.getHostname().equals("hostname"));
		assertTrue(deployMetric.getUsername().equals("username"));
		
		//Update Entity	
		deployMetric.setHostname("updateHostname");
		tx_bc.begin();
		em_bc.persist(deployMetric);	
		tx_bc.commit();
		
		deployMetric = em_bc.find(DeployMetric.class, new DeployMetricId("buildVersion", Activity.APP_DBPOSTIMPORT.getName().toLowerCase()));
		assertTrue(deployMetric.getStartDate() != null);
		assertTrue(deployMetric.getEndDate() != null);
		assertTrue(deployMetric.getHostname().equals("updateHostname"));
		assertTrue(deployMetric.getUsername().equals("username"));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(deployMetric);
		tx_bc.commit();
		
		Query queryCountDeployMetricAfter = em_bc.createQuery ("SELECT count(x) FROM DeployMetric x");
		Number countDeployMetricAfter = (Number) queryCountDeployMetricAfter.getSingleResult ();
		assertTrue(countDeployMetrics.intValue() == countDeployMetricAfter.intValue());
	}
}