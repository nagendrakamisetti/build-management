package com.modeln.build.entity.test.act;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.test.act.ActBlacklist;
import com.modeln.build.entity.test.act.id.ActBlacklistId;

public class ActBlacklistIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountActBlacklists = em_bc.createQuery ("SELECT count(x) FROM ActBlacklist x");
		Number countActBlacklists = (Number) queryCountActBlacklists.getSingleResult ();
		assertTrue(!countActBlacklists.equals(0));
		
		Query queryCountActBlacklist = em_bc.createQuery("SELECT g FROM ActBlacklist g");
		List<ActBlacklist> actBlacklists = queryCountActBlacklist.getResultList();
		assertTrue(countActBlacklists.intValue() == actBlacklists.size());
		
		//Create Entity
		ActBlacklist actBlacklist = new ActBlacklist();
		actBlacklist.setFilename("filename");
		actBlacklist.setVersionControlRoot("versionControlRoot");
		actBlacklist.setHostname("hostname");
		actBlacklist.setMessage("message");
		actBlacklist.setTimeout(1);
		actBlacklist.setStartDate(new Date());
		actBlacklist.setEndDate(new Date());
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(actBlacklist);
		tx_bc.commit();
		
		assertNotNull("ActBlacklist ID should not be null", actBlacklist.getFilename());
		
		actBlacklist = em_bc.find(ActBlacklist.class, new ActBlacklistId(actBlacklist.getFilename(), actBlacklist.getVersionControlRoot()));
		assertTrue(actBlacklist.getHostname().equals("hostname"));
		assertTrue(actBlacklist.getMessage().equals("message"));
		assertTrue(actBlacklist.getTimeout() == 1);
		assertTrue(actBlacklist.getStartDate() != null);
		assertTrue(actBlacklist.getEndDate() != null);
		
		//Update Entity	
		actBlacklist.setHostname("updatedHostname");
		tx_bc.begin();
		em_bc.persist(actBlacklist);	
		tx_bc.commit();
		
		actBlacklist = em_bc.find(ActBlacklist.class, new ActBlacklistId(actBlacklist.getFilename(), actBlacklist.getVersionControlRoot()));
		assertTrue(actBlacklist.getHostname().equals("updatedHostname"));
		assertTrue(actBlacklist.getMessage().equals("message"));
		assertTrue(actBlacklist.getTimeout() == 1);
		assertTrue(actBlacklist.getStartDate() != null);
		assertTrue(actBlacklist.getEndDate() != null);
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(actBlacklist);
		tx_bc.commit();
		
		Query queryFindAllActBlacklists = em_bc.createQuery("SELECT u FROM ActBlacklist u");
		actBlacklists = queryFindAllActBlacklists.getResultList();
		assertTrue(actBlacklists.size() == countActBlacklists.intValue());
	}
}
