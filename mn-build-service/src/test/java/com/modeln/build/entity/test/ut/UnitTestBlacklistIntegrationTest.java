package com.modeln.build.entity.test.ut;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.test.act.ActBlacklist;
import com.modeln.build.entity.test.ut.UnitTestBlacklist;
import com.modeln.build.entity.test.ut.id.UnitTestBlacklistId;

public class UnitTestBlacklistIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountUnitTestBlacklists = em_bc.createQuery ("SELECT count(x) FROM UnitTestBlacklist x");
		Number countUnitTestBlacklists = (Number) queryCountUnitTestBlacklists.getSingleResult ();
		assertTrue(!countUnitTestBlacklists.equals(0));
		
		Query queryCountUnitTestBlacklist = em_bc.createQuery("SELECT g FROM UnitTestBlacklist g");
		List<ActBlacklist> unitTestBlacklists = queryCountUnitTestBlacklist.getResultList();
		assertTrue(countUnitTestBlacklists.intValue() == unitTestBlacklists.size());
		
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
		tx_bc.begin();
		em_bc.persist(unitTestBlacklist);
		tx_bc.commit();
		
		assertNotNull("UnitTestBlacklist ID should not be null", unitTestBlacklist.getTestClass());
		
		unitTestBlacklist = em_bc.find(UnitTestBlacklist.class, new UnitTestBlacklistId(unitTestBlacklist.getTestClass(), unitTestBlacklist.getTestMethod(), unitTestBlacklist.getVersionControlRoot()));
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
		tx_bc.begin();
		em_bc.persist(unitTestBlacklist);	
		tx_bc.commit();
		
		unitTestBlacklist = em_bc.find(UnitTestBlacklist.class, new UnitTestBlacklistId(unitTestBlacklist.getTestClass(), unitTestBlacklist.getTestMethod(), unitTestBlacklist.getVersionControlRoot()));
		assertTrue(unitTestBlacklist.getTestClass().equals("testClass"));
		assertTrue(unitTestBlacklist.getTestMethod().equals("testMethod"));
		assertTrue(unitTestBlacklist.getVersionControlRoot().equals("versionControlRoot"));
		assertTrue(unitTestBlacklist.getHostname().equals("updatedHostname"));
		assertTrue(unitTestBlacklist.getMessage().equals("message"));
		assertTrue(unitTestBlacklist.getTimeout() == 1);
		assertTrue(unitTestBlacklist.getStartDate() != null);
		assertTrue(unitTestBlacklist.getEndDate() != null);
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(unitTestBlacklist);
		tx_bc.commit();
		
		Query queryFindAllUnitTestBlacklists = em_bc.createQuery("SELECT u FROM UnitTestBlacklist u");
		unitTestBlacklists = queryFindAllUnitTestBlacklists.getResultList();
		assertTrue(unitTestBlacklists.size() == countUnitTestBlacklists.intValue());
	}
}