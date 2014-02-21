package com.modeln.build.entity.test.act;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.test.act.ActBlacklist;
import com.modeln.build.entity.test.act.id.ActBlacklistId;

public class ActBlacklistTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
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
		tx.begin();
		em.persist(actBlacklist);
		tx.commit();
		
		assertNotNull("ActBlacklist ID should not be null", actBlacklist.getFilename());
		
		actBlacklist = em.find(ActBlacklist.class, new ActBlacklistId(actBlacklist.getFilename(), actBlacklist.getVersionControlRoot()));
		assertTrue(actBlacklist.getHostname().equals("hostname"));
		assertTrue(actBlacklist.getMessage().equals("message"));
		assertTrue(actBlacklist.getTimeout() == 1);
		assertTrue(actBlacklist.getStartDate() != null);
		assertTrue(actBlacklist.getEndDate() != null);
		
		//Update Entity	
		actBlacklist.setHostname("updatedHostname");
		tx.begin();
		em.persist(actBlacklist);	
		tx.commit();
		
		actBlacklist = em.find(ActBlacklist.class, new ActBlacklistId(actBlacklist.getFilename(), actBlacklist.getVersionControlRoot()));
		assertTrue(actBlacklist.getHostname().equals("updatedHostname"));
		assertTrue(actBlacklist.getMessage().equals("message"));
		assertTrue(actBlacklist.getTimeout() == 1);
		assertTrue(actBlacklist.getStartDate() != null);
		assertTrue(actBlacklist.getEndDate() != null);
		
		//Delete Entity
		tx.begin();
		em.remove(actBlacklist);
		tx.commit();
		
		Query queryFindAllActBlacklists = em.createQuery("SELECT u FROM ActBlacklist u");
		List<ActBlacklist> actBlacklists = queryFindAllActBlacklists.getResultList();
		assertTrue(actBlacklists.isEmpty());
		
	}
	
}
