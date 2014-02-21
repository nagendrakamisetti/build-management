package com.modeln.build.entity.test.ut;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.auth.Group;
import com.modeln.build.entity.auth.User;
import com.modeln.build.entity.build.Build;
import com.modeln.build.entity.test.ut.UnitTest;
import com.modeln.build.entity.test.ut.UnitTestSuite;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class UnitTestIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		Query queryCountGroups = em_bc.createQuery ("SELECT count(x) FROM Group x");
		Number countGroups = (Number) queryCountGroups.getSingleResult ();
		assertTrue(!countGroups.equals(0));
		
		Query queryFindAllGroup = em_bc.createQuery("SELECT g FROM Group g");
		List<Group> groups = queryFindAllGroup.getResultList();
		assertTrue(countGroups.intValue() == groups.size());
		
		//Count number of Entities
		Query queryCountUsers = em_bc.createQuery ("SELECT count(x) FROM User x");
		Number countUsers = (Number) queryCountUsers.getSingleResult ();
		assertTrue(!countUsers.equals(0));
		
		//Retrieve Entities
		Query queryFindAllUser = em_bc.createQuery("SELECT g FROM User g");
		List<User> users = queryFindAllUser.getResultList();
		assertTrue(countUsers.intValue() == users.size());
		
		//Count number of Entities
		Query queryCountBuilds = em_bc.createQuery ("SELECT count(x) FROM Build x");
		Number countBuilds = (Number) queryCountBuilds.getSingleResult ();
		assertTrue(!countBuilds.equals(0));
		
		//Retrieve Entities
		Query queryFindAllBuild = em_bc.createQuery("SELECT g FROM Build g");
		List<Build> builds = queryFindAllBuild.getResultList();
		assertTrue(countBuilds.intValue() == builds.size());
		
		//Count number of Entities
		Query queryCountUnitTestSuites = em_bc.createQuery ("SELECT count(x) FROM UnitTestSuite x");
		Number countUnitTestSuites = (Number) queryCountUnitTestSuites.getSingleResult ();
		assertTrue(!countUnitTestSuites.equals(0));
		
		//Retrieve Entities
		Query queryFindAllUnitTestSuite = em_bc.createQuery("SELECT g FROM UnitTestSuite g");
		queryFindAllUnitTestSuite.setMaxResults(10);
		List<UnitTestSuite> unitTestSuites = queryFindAllUnitTestSuite.getResultList();
		assertTrue(unitTestSuites.size() != 0);
		
		//Count number of Entities
		Query queryCountUnitTests = em_bc.createQuery ("SELECT count(x) FROM UnitTest x");
		Number countUnitTests = (Number) queryCountUnitTests.getSingleResult ();
		assertTrue(!countUnitTests.equals(0));
		
		//Retrieve Entities
		Query queryFindAllUnitTest = em_bc.createQuery("SELECT g FROM UnitTest g");
		queryFindAllUnitTest.setMaxResults(10);
		List<UnitTest> unitTests = queryFindAllUnitTest.getResultList();
		assertTrue(unitTests.size() != 0);
		
		//Create Depend Entities
		Group group = new Group();
		group.setGid((countGroups.intValue() * 10));
		group.setDesc("desc");
		group.setName("group1");
		group.setType(Role.ADMIN);
		Set<Permission> permEdit = new HashSet<Permission>();
		permEdit.add(Permission.EDIT);
		group.setPermGroup(permEdit);
		Set<Permission> permEditAndDelete = new HashSet<Permission>();
		permEditAndDelete.add(Permission.EDIT);
		permEditAndDelete.add(Permission.DELETE);
		group.setPermUser(permEditAndDelete);
		group.setPermSelf(permEdit);
		group.setPermListing(permEditAndDelete);
		
		// Creates Dependent Entity
		User user = new User();
		user.setUsername("username");
		user.setFirstName("Firstname");
		user.setLastName("Lastname");
		user.setCountry("us");
		user.setLanguage("en");
		user.setAccountStatus(Status.ACTIVE);
		user.setEmailAddress("email@company.com");
		user.setPassword("password");
		user.setPasswordEncryption(Encryption.CRYPT);
		user.setTitle(Title.MR);
		user.setPrimaryGroup(group);
		user.setLoginSuccess(new Date());
		user.setLoginFailure(new Date());
		user.setLoginFailureCount(2);
		
		// Creates Depend Entity
		Build build = new Build();
		build.setBuildComments("buildComments");
		Set<com.modeln.build.enums.build.Status> buildStatus = new HashSet<com.modeln.build.enums.build.Status>();
		buildStatus.add(com.modeln.build.enums.build.Status.PASSING);
		build.setBuildStatus(buildStatus);
		build.setSupportStatus(SupportStatus.ACTIVE);
		build.setComments("comments");
		build.setDownloadUri("downloadUri");
		build.setStartTime(new Date());
		build.setEndTime(new Date());
		build.setHostname("hostname");
		build.setJdkVendor("jdkVendor");
		build.setJdkVersion("jdkVersion");
		build.setJobUrl("jobUrl");
		build.setOsArch("osArch");
		build.setOsName("osName");
		build.setOsVersion("osVersion");
		build.setStatus("status");
		build.setKeyAlgorithm("keyAlgorithm");
		build.setVerPublicKey("verPublicKey");
		build.setVerPrivateKey("verPrivateKey");
		build.setUser(user);
		build.setUsername("username");
		build.setVersion("version");
		build.setVersionControlId("versionControlId");
		build.setVersionControlRoot("versionControlRoot");
		build.setVersionControlType(SourceVersionControlSystem.GIT);
		
		// Creates Entity
		UnitTestSuite unitTestSuite = new UnitTestSuite();
		unitTestSuite.setBuild(build);
		unitTestSuite.setEnvName("envName");
		unitTestSuite.setGroupId(1l);
		unitTestSuite.setGroupName("groupName");
		unitTestSuite.setHostname("hostname");
		unitTestSuite.setJdbcUrl("jdbcUrl");
		unitTestSuite.setJdkVendor("jdkVendor");
		unitTestSuite.setJdkVersion("jdkVersion");
		unitTestSuite.setMaxThreads(3);
		unitTestSuite.setName("name");
		unitTestSuite.setOsArch("osArch");
		unitTestSuite.setOsName("osName");
		unitTestSuite.setOsVersion("osVersion");
		unitTestSuite.setStartDate(new Date());
		unitTestSuite.setEndDate(new Date());
		unitTestSuite.setSuiteOptions("suiteOptions");
		unitTestSuite.setTestCount(1);
		unitTestSuite.setUsername("username");
		unitTestSuite.setFailureSrc("failureSrc");
		
		// Creates Entity
		UnitTest unitTest = new UnitTest();
		unitTest.setGroupName("groupName");
		unitTest.setMessage("message");
		unitTest.setStartDate(new Date());
		unitTest.setEndDate(new Date());
		unitTest.setStatus(com.modeln.build.enums.test.Status.PASS);
		unitTest.setSuite(unitTestSuite);
		unitTest.setTestClass("testClass");
		unitTest.setTestMethod("testMethod");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(group);
		em_bc.persist(user);
		em_bc.persist(build);
		em_bc.persist(unitTestSuite);
		em_bc.persist(unitTest);
		tx_bc.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("UnitTestSuite ID should not be null", unitTestSuite.getId());
		assertNotNull("UnitTest ID should not be null", unitTest.getId());
		
		unitTest = em_bc.find(UnitTest.class, unitTest.getId());
		assertTrue(unitTestSuite.getBuild().getId() == build.getId());
		assertTrue(unitTest.getGroupName().equals("groupName"));
		assertTrue(unitTest.getMessage().equals("message"));
		assertTrue(unitTest.getStartDate() != null);
		assertTrue(unitTest.getEndDate() != null);
		assertTrue(unitTest.getStatus().equals(com.modeln.build.enums.test.Status.PASS));
		assertTrue(unitTest.getSuite().getId() == unitTestSuite.getId());
		assertTrue(unitTest.getTestClass().equals("testClass"));
		assertTrue(unitTest.getTestMethod().equals("testMethod"));
		
		//Update Entity	
		unitTest.setMessage("updateMessage");
		tx_bc.begin();
		em_bc.persist(unitTest);	
		tx_bc.commit();
		
		unitTest = em_bc.find(UnitTest.class, unitTest.getId());
		assertTrue(unitTestSuite.getBuild().getId() == build.getId());
		assertTrue(unitTest.getGroupName().equals("groupName"));
		assertTrue(unitTest.getMessage().equals("updateMessage"));
		assertTrue(unitTest.getStartDate() != null);
		assertTrue(unitTest.getEndDate() != null);
		assertTrue(unitTest.getStatus().equals(com.modeln.build.enums.test.Status.PASS));
		assertTrue(unitTest.getSuite().getId() == unitTestSuite.getId());
		assertTrue(unitTest.getTestClass().equals("testClass"));
		assertTrue(unitTest.getTestMethod().equals("testMethod"));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(unitTest);
		em_bc.remove(unitTestSuite);
		em_bc.remove(build);
		em_bc.remove(user);
		em_bc.remove(group);
		tx_bc.commit();
		
		Query queryFindAllBuilds = em_bc.createQuery("SELECT u FROM Build u");
		builds = queryFindAllBuilds.getResultList();
		assertTrue(builds.size() == countBuilds.intValue());	
		
		Query queryFindAllUsers = em_bc.createQuery("SELECT u FROM User u");
		users = queryFindAllUsers.getResultList();
		assertTrue(users.size() == countUsers.intValue());	
		
		Query queryFindAllGroups = em_bc.createQuery("SELECT u FROM Group u");
		groups = queryFindAllGroups.getResultList();
		assertTrue(groups.size() == countGroups.intValue());
		
		Query queryCountUnitTestSuiteAfter = em_bc.createQuery ("SELECT count(x) FROM UnitTestSuite x");
		Number countUnitTestSuiteAfter = (Number) queryCountUnitTestSuiteAfter.getSingleResult ();
		assertTrue(countUnitTestSuites.intValue() == countUnitTestSuiteAfter.intValue());	
		
		Query queryCountUnitTestAfter = em_bc.createQuery ("SELECT count(x) FROM UnitTest x");
		Number countUnitTestAfter = (Number) queryCountUnitTestAfter.getSingleResult ();
		assertTrue(countUnitTests.intValue() == countUnitTestAfter.intValue());	
	}
}
