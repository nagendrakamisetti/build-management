package com.modeln.build.entity.build;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.build.BuildMetric;
import com.modeln.build.entity.build.id.BuildMetricId;
import com.modeln.build.enums.build.Activity;

public class BuildMetricIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountBuildMetrics = em_bc.createQuery ("SELECT count(x) FROM BuildMetric x");
		Number countBuildMetrics = (Number) queryCountBuildMetrics.getSingleResult ();
		assertTrue(!countBuildMetrics.equals(0));
		
		Query queryCountBuildMetric = em_bc.createQuery("SELECT g FROM BuildMetric g");
		queryCountBuildMetric.setMaxResults(10);
		List<BuildMetric> buildMetrics = queryCountBuildMetric.getResultList();
		assertTrue(buildMetrics.size() != 0);
		
		//Create Entity
		BuildMetric buildMetric = new BuildMetric();
		buildMetric.setActivity(Activity.BUILD);
		buildMetric.setBuildVersion("buildVersion");
		buildMetric.setStartDate(new Date());
		buildMetric.setEndDate(new Date());
		buildMetric.setHostname("hostname");
		buildMetric.setUsername("username");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(buildMetric);
		tx_bc.commit();
		
		buildMetric = em_bc.find(BuildMetric.class, new BuildMetricId("buildVersion", Activity.BUILD.getName().toLowerCase()));
		assertTrue(buildMetric.getStartDate() != null);
		assertTrue(buildMetric.getEndDate() != null);
		assertTrue(buildMetric.getHostname().equals("hostname"));
		assertTrue(buildMetric.getUsername().equals("username"));
	
		
		//Update Entity	
		buildMetric.setHostname("updateHostname");
		tx_bc.begin();
		em_bc.persist(buildMetric);	
		tx_bc.commit();
		
		buildMetric = em_bc.find(BuildMetric.class, new BuildMetricId("buildVersion", Activity.BUILD.getName().toLowerCase()));
		assertTrue(buildMetric.getStartDate() != null);
		assertTrue(buildMetric.getEndDate() != null);
		assertTrue(buildMetric.getHostname().equals("updateHostname"));
		assertTrue(buildMetric.getUsername().equals("username"));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(buildMetric);
		tx_bc.commit();
		
		Query queryCountBuildMetricAfter = em_bc.createQuery ("SELECT count(x) FROM BuildMetric x");
		Number countBuildMetricAfter = (Number) queryCountBuildMetricAfter.getSingleResult ();
		assertTrue(countBuildMetrics.intValue() == countBuildMetricAfter.intValue());
	}
}
