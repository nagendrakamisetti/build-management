package com.modeln.build.entity.release;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.release.ReleaseSummary;
import com.modeln.build.enums.release.Status;
import com.modeln.build.enums.release.State;

public class ReleaseSummaryTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		//Create Entity
		ReleaseSummary releaseSummary = new ReleaseSummary();
		releaseSummary.setBuildVersion("buildVersion");
		releaseSummary.setName("name");
		releaseSummary.setOrder(1);
		releaseSummary.setStatus(Status.ACTIVE);
		releaseSummary.setText("text");
		releaseSummary.setType(State.INCREMENTAL);
		
		// Persists to the database
		tx.begin();
		em.persist(releaseSummary);
		tx.commit();
		
		assertNotNull("ReleaseSummary ID should not be null", releaseSummary.getId());
		
		releaseSummary = em.find(ReleaseSummary.class, releaseSummary.getId());
		assertTrue(releaseSummary.getBuildVersion().equals("buildVersion"));
		assertTrue(releaseSummary.getName().equals("name"));
		assertTrue(releaseSummary.getOrder() == 1);
		assertTrue(releaseSummary.getStatus().equals(Status.ACTIVE));
		assertTrue(releaseSummary.getText().equals("text"));
		assertTrue(releaseSummary.getType().equals(State.INCREMENTAL));
		
		//Update Entity	
		releaseSummary.setName("updatedName");
		tx.begin();
		em.persist(releaseSummary);	
		tx.commit();
		
		releaseSummary = em.find(ReleaseSummary.class, releaseSummary.getId());
		assertTrue(releaseSummary.getBuildVersion().equals("buildVersion"));
		assertTrue(releaseSummary.getName().equals("updatedName"));
		assertTrue(releaseSummary.getOrder() == 1);
		assertTrue(releaseSummary.getStatus().equals(Status.ACTIVE));
		assertTrue(releaseSummary.getText().equals("text"));
		assertTrue(releaseSummary.getType().equals(State.INCREMENTAL));
		
		//Delete Entity
		tx.begin();
		em.remove(releaseSummary);
		tx.commit();
		
		Query queryFindAllReleaseSummarys = em.createQuery("SELECT u FROM ReleaseSummary u");
		List<ReleaseSummary> releaseSummarys = queryFindAllReleaseSummarys.getResultList();
		assertTrue(releaseSummarys.isEmpty());
		
	}
}
