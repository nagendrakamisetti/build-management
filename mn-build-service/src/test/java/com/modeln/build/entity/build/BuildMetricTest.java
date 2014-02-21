package com.modeln.build.entity.build;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.build.BuildMetric;
import com.modeln.build.entity.build.id.BuildMetricId;
import com.modeln.build.enums.build.Activity;

public class BuildMetricTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		//Create Entity
		BuildMetric buildMetric = new BuildMetric();
		buildMetric.setActivity(Activity.BUILD);
		buildMetric.setBuildVersion("buildVersion");
		buildMetric.setStartDate(new Date());
		buildMetric.setEndDate(new Date());
		buildMetric.setHostname("hostname");
		buildMetric.setUsername("username");
		
		// Persists to the database
		tx.begin();
		em.persist(buildMetric);
		tx.commit();
		
		buildMetric = em.find(BuildMetric.class, new BuildMetricId("buildVersion", Activity.BUILD.getName().toLowerCase()));
		assertTrue(buildMetric.getStartDate() != null);
		assertTrue(buildMetric.getEndDate() != null);
		assertTrue(buildMetric.getHostname().equals("hostname"));
		assertTrue(buildMetric.getUsername().equals("username"));
		
		//Update Entity	
		buildMetric.setHostname("updateHostname");
		tx.begin();
		em.persist(buildMetric);	
		tx.commit();
		
		buildMetric = em.find(BuildMetric.class, new BuildMetricId("buildVersion", Activity.BUILD.getName().toLowerCase()));
		assertTrue(buildMetric.getStartDate() != null);
		assertTrue(buildMetric.getEndDate() != null);
		assertTrue(buildMetric.getHostname().equals("updateHostname"));
		assertTrue(buildMetric.getUsername().equals("username"));
		
		//Delete Entity
		tx.begin();
		em.remove(buildMetric);
		tx.commit();
		
		Query queryFindAllBuildMetrics = em.createQuery("SELECT u FROM BuildMetric u");
		List<BuildMetric> buildMetrics = queryFindAllBuildMetrics.getResultList();
		assertTrue(buildMetrics.isEmpty());
		
	}

}
