package com.modeln.build.entity.release;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.release.ReleaseSummary;
import com.modeln.build.enums.release.Status;
import com.modeln.build.enums.release.State;

public class ReleaseSummaryIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountReleaseSummarys = em_bc.createQuery ("SELECT count(x) FROM ReleaseSummary x");
		Number countReleaseSummarys = (Number) queryCountReleaseSummarys.getSingleResult ();
		assertTrue(!countReleaseSummarys.equals(0));
		
		Query queryCountReleaseSummary = em_bc.createQuery("SELECT g FROM ReleaseSummary g");
		List<ReleaseSummary> releaseSummarys = queryCountReleaseSummary.getResultList();
		assertTrue(countReleaseSummarys.intValue() == releaseSummarys.size());
		
		//Create Entity
		ReleaseSummary releaseSummary = new ReleaseSummary();
		releaseSummary.setBuildVersion("buildVersion");
		releaseSummary.setName("name");
		releaseSummary.setOrder(1);
		releaseSummary.setStatus(Status.ACTIVE);
		releaseSummary.setText("text");
		releaseSummary.setType(State.INCREMENTAL);
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(releaseSummary);
		tx_bc.commit();
		
		assertNotNull("ReleaseSummary ID should not be null", releaseSummary.getId());
		
		releaseSummary = em_bc.find(ReleaseSummary.class, releaseSummary.getId());
		assertTrue(releaseSummary.getBuildVersion().equals("buildVersion"));
		assertTrue(releaseSummary.getName().equals("name"));
		assertTrue(releaseSummary.getOrder() == 1);
		assertTrue(releaseSummary.getStatus().equals(Status.ACTIVE));
		assertTrue(releaseSummary.getText().equals("text"));
		assertTrue(releaseSummary.getType().equals(State.INCREMENTAL));
		
		//Update Entity	
		releaseSummary.setName("updatedName");
		tx_bc.begin();
		em_bc.persist(releaseSummary);	
		tx_bc.commit();
		
		releaseSummary = em_bc.find(ReleaseSummary.class, releaseSummary.getId());
		assertTrue(releaseSummary.getBuildVersion().equals("buildVersion"));
		assertTrue(releaseSummary.getName().equals("updatedName"));
		assertTrue(releaseSummary.getOrder() == 1);
		assertTrue(releaseSummary.getStatus().equals(Status.ACTIVE));
		assertTrue(releaseSummary.getText().equals("text"));
		assertTrue(releaseSummary.getType().equals(State.INCREMENTAL));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(releaseSummary);
		tx_bc.commit();
		
		Query queryFindAllReleaseSummarys = em_bc.createQuery("SELECT u FROM ReleaseSummary u");
		releaseSummarys = queryFindAllReleaseSummarys.getResultList();
		assertTrue(releaseSummarys.size() == countReleaseSummarys.intValue());
	}
}
