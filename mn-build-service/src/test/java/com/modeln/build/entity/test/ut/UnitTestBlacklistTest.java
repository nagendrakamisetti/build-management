package com.modeln.build.entity.test.ut;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.test.ut.UnitTestBlacklist;
import com.modeln.build.entity.test.ut.id.UnitTestBlacklistId;

public class UnitTestBlacklistTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		//Create Entity
		UnitTestBlacklist unitTestBlacklist = new UnitTestBlacklist();
		unitTestBlacklist.setTestClass("testClass");
		unitTestBlacklist.setTestMethod("testMethod");
		unitTestBlacklist.setVersionControlRoot("versionControlRoot");
		unitTestBlacklist.setHostname("hostname");
		unitTestBlacklist.setMessage("message");
		unitTestBlacklist.setTimeout(1);
		unitTestBlacklist.setStartDate(new Date());
		unitTestBlacklist.setEndDate(new Date());
		
		// Persists to the database
		tx.begin();
		em.persist(unitTestBlacklist);
		tx.commit();
		
		assertNotNull("UnitTestBlacklist ID should not be null", unitTestBlacklist.getTestClass());
		
		unitTestBlacklist = em.find(UnitTestBlacklist.class, new UnitTestBlacklistId(unitTestBlacklist.getTestClass(), unitTestBlacklist.getTestMethod(), unitTestBlacklist.getVersionControlRoot()));
		assertTrue(unitTestBlacklist.getTestClass().equals("testClass"));
		assertTrue(unitTestBlacklist.getTestMethod().equals("testMethod"));
		assertTrue(unitTestBlacklist.getVersionControlRoot().equals("versionControlRoot"));
		assertTrue(unitTestBlacklist.getHostname().equals("hostname"));
		assertTrue(unitTestBlacklist.getMessage().equals("message"));
		assertTrue(unitTestBlacklist.getTimeout() == 1);
		assertTrue(unitTestBlacklist.getStartDate() != null);
		assertTrue(unitTestBlacklist.getEndDate() != null);
		
		//Update Entity	
		unitTestBlacklist.setHostname("updatedHostname");
		tx.begin();
		em.persist(unitTestBlacklist);	
		tx.commit();
		
		unitTestBlacklist = em.find(UnitTestBlacklist.class, new UnitTestBlacklistId(unitTestBlacklist.getTestClass(), unitTestBlacklist.getTestMethod(), unitTestBlacklist.getVersionControlRoot()));
		assertTrue(unitTestBlacklist.getTestClass().equals("testClass"));
		assertTrue(unitTestBlacklist.getTestMethod().equals("testMethod"));
		assertTrue(unitTestBlacklist.getVersionControlRoot().equals("versionControlRoot"));
		assertTrue(unitTestBlacklist.getHostname().equals("updatedHostname"));
		assertTrue(unitTestBlacklist.getMessage().equals("message"));
		assertTrue(unitTestBlacklist.getTimeout() == 1);
		assertTrue(unitTestBlacklist.getStartDate() != null);
		assertTrue(unitTestBlacklist.getEndDate() != null);
		
		//Delete Entity
		tx.begin();
		em.remove(unitTestBlacklist);
		tx.commit();
		
		Query queryFindAllUnitTestBlacklists = em.createQuery("SELECT u FROM UnitTestBlacklist u");
		List<UnitTestBlacklist> unitTestBlacklists = queryFindAllUnitTestBlacklists.getResultList();
		assertTrue(unitTestBlacklists.isEmpty());
		
	}
}