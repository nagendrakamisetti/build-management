package com.modeln.build.entity.test.uit;

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
import com.modeln.build.entity.test.uit.UITest;
import com.modeln.build.entity.test.uit.UITestStep;
import com.modeln.build.entity.test.uit.UITestSuite;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class UITestStepIntegrationTest extends AbstractEntityIntegrationTest {

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
		Query queryCountUITestSuites = em_bc.createQuery ("SELECT count(x) FROM UITestSuite x");
		Number countUITestSuites = (Number) queryCountUITestSuites.getSingleResult ();
		assertTrue(!countUITestSuites.equals(0));
		
		//Retrieve Entities
		Query queryFindAllUITestSuite = em_bc.createQuery("SELECT g FROM UITestSuite g");
		queryFindAllUITestSuite.setMaxResults(10);
		List<UITestSuite> uiTestSuites = queryFindAllUITestSuite.getResultList();
		assertTrue(uiTestSuites.size() != 0);
		
		//Count number of Entities
		Query queryCountUITests = em_bc.createQuery ("SELECT count(x) FROM UITest x");
		Number countUITests = (Number) queryCountUITests.getSingleResult ();
		assertTrue(!countUITests.equals(0));
		
		//Retrieve Entities
		Query queryFindAllUITest = em_bc.createQuery("SELECT g FROM UITest g");
		queryFindAllUITest.setMaxResults(10);
		List<UITest> uiTests = queryFindAllUITest.getResultList();
		assertTrue(uiTests.size() != 0);
		
		//Count number of Entities
		Query queryCountUITestSteps = em_bc.createQuery ("SELECT count(x) FROM UITestStep x");
		Number countUITestSteps = (Number) queryCountUITestSteps.getSingleResult ();
		assertTrue(!countUITestSteps.equals(0));
		
		//Retrieve Entities
		Query queryFindAllUITestStep = em_bc.createQuery("SELECT g FROM UITestStep g");
		queryFindAllUITestStep.setMaxResults(10);
		List<UITestStep> uiTestSteps = queryFindAllUITestStep.getResultList();
		assertTrue(uiTestSteps.size() != 0);
		
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
		UITestSuite uiTestSuite = new UITestSuite();
		uiTestSuite.setBuild(build);
		uiTestSuite.setEnvName("envName");
//		uiTestSuite.setGroupId(1l);
//		uiTestSuite.setGroupName("groupName");
		uiTestSuite.setHostname("hostname");
		uiTestSuite.setJdbcUrl("jdbcUrl");
		uiTestSuite.setJdkVendor("jdkVendor");
		uiTestSuite.setJdkVersion("jdkVersion");
//		uiTestSuite.setMaxThreads(3);
		uiTestSuite.setName("name");
		uiTestSuite.setOsArch("osArch");
		uiTestSuite.setOsName("osName");
		uiTestSuite.setOsVersion("osVersion");
		uiTestSuite.setStartDate(new Date());
		uiTestSuite.setEndDate(new Date());
		uiTestSuite.setSuiteOptions("suiteOptions");
//		uiTestSuite.setTestCount(1);
		uiTestSuite.setUsername("username");
		uiTestSuite.setAppServer("appServer");
		uiTestSuite.setAppServerVersion("Version");
		uiTestSuite.setAppUrl("appUrl");
		uiTestSuite.setClient("client");
		uiTestSuite.setClientVersion("Version");
		uiTestSuite.setWebServer("webServer");
		uiTestSuite.setWebServerVersion("webServerVersion");
		
		// Creates Entity
		UITest uiTest = new UITest();
		uiTest.setAppUsername("appUsername");
		uiTest.setFailureCount(1);
//		uiTest.setGroupName("groupName");
		uiTest.setMessage("message");
		uiTest.setStartDate(new Date());
		uiTest.setEndDate(new Date());
		uiTest.setStatus(com.modeln.build.enums.test.Status.PASS);
		uiTest.setStepCount(1);
		uiTest.setSuccessCount(1);
		uiTest.setSuite(uiTestSuite);
		
		// Creates Entity
		UITestStep uiTestStep = new UITestStep();
		uiTestStep.setMessage("message");
		uiTestStep.setStatus(com.modeln.build.enums.test.Status.PASS);
		uiTestStep.setStepName("stepName");
		uiTestStep.setTest(uiTest);
		uiTestStep.setStartDate(new Date());
		uiTestStep.setEndDate(new Date());
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(group);
		em_bc.persist(user);
		em_bc.persist(build);
		em_bc.persist(uiTestSuite);
		em_bc.persist(uiTest);
		em_bc.persist(uiTestStep);
		tx_bc.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("UITestSuite ID should not be null", uiTestSuite.getId());
		assertNotNull("UITest ID should not be null", uiTest.getId());
		assertNotNull("UITestStep ID should not be null", uiTestStep.getId());
		
		uiTestStep = em_bc.find(UITestStep.class, uiTestStep.getId());
		assertTrue(uiTest.getAppUsername().equals("appUsername"));
		assertTrue(uiTestStep.getMessage().equals("message"));
		assertTrue(uiTestStep.getStatus().equals(com.modeln.build.enums.test.Status.PASS));
		assertTrue(uiTestStep.getStepName().equals("stepName"));
		assertTrue(uiTestStep.getTest().getId() == uiTest.getId());
		assertTrue(uiTestStep.getStartDate() != null);
		assertTrue(uiTestStep.getEndDate() != null);
		
		//Update Entity	
		uiTestStep.setMessage("updateMessage");
		tx_bc.begin();
		em_bc.persist(uiTestStep);	
		tx_bc.commit();
		
		uiTestStep = em_bc.find(UITestStep.class, uiTestStep.getId());
		assertTrue(uiTest.getAppUsername().equals("appUsername"));
		assertTrue(uiTestStep.getMessage().equals("updateMessage"));
		assertTrue(uiTestStep.getStatus().equals(com.modeln.build.enums.test.Status.PASS));
		assertTrue(uiTestStep.getStepName().equals("stepName"));
		assertTrue(uiTestStep.getTest().getId() == uiTest.getId());
		assertTrue(uiTestStep.getStartDate() != null);
		assertTrue(uiTestStep.getEndDate() != null);
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(uiTestStep);
		em_bc.remove(uiTest);
		em_bc.remove(uiTestSuite);
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
		
		Query queryCountUITestSuiteAfter = em_bc.createQuery ("SELECT count(x) FROM UITestSuite x");
		Number countUITestSuiteAfter = (Number) queryCountUITestSuiteAfter.getSingleResult ();
		assertTrue(countUITestSuites.intValue() == countUITestSuiteAfter.intValue());	
		
		Query queryCountUITestAfter = em_bc.createQuery ("SELECT count(x) FROM UITest x");
		Number countUITestAfter = (Number) queryCountUITestAfter.getSingleResult ();
		assertTrue(countUITests.intValue() == countUITestAfter.intValue());	
		
		Query queryCountUITestStepAfter = em_bc.createQuery ("SELECT count(x) FROM UITestStep x");
		Number countUITestStepAfter = (Number) queryCountUITestStepAfter.getSingleResult ();
		assertTrue(countUITestSteps.intValue() == countUITestStepAfter.intValue());	
	}
}
