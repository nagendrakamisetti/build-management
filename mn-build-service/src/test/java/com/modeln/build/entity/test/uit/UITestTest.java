package com.modeln.build.entity.test.uit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.auth.Group;
import com.modeln.build.entity.auth.User;
import com.modeln.build.entity.build.Build;
import com.modeln.build.entity.test.uit.UITest;
import com.modeln.build.entity.test.uit.UITestSuite;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class UITestTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		//Create Depend Entities
		Group group = new Group();
		group.setGid(0);
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
		
		// Creates Dependent Entity
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
		
		// Creates Dependent Entity
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
		
		// Persists to the database
		tx.begin();
		em.persist(group);
		em.persist(user);
		em.persist(build);
		em.persist(uiTestSuite);
		em.persist(uiTest);
		tx.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("UITestSuite ID should not be null", uiTestSuite.getId());
		assertNotNull("UITest ID should not be null", uiTest.getId());
		
		uiTest = em.find(UITest.class, uiTest.getId());
		assertTrue(uiTestSuite.getBuild().getId() == build.getId());
		assertTrue(uiTest.getAppUsername().equals("appUsername"));
		assertTrue(uiTest.getFailureCount().equals(1));
//		assertTrue(uiTest.getGroupName().equals("groupName"));
		assertTrue(uiTest.getMessage().equals("message"));
		assertTrue(uiTest.getStartDate() != null);
		assertTrue(uiTest.getEndDate() != null);
		assertTrue(uiTest.getStatus().equals(com.modeln.build.enums.test.Status.PASS));
		assertTrue(uiTest.getStepCount().equals(1));
		assertTrue(uiTest.getSuccessCount().equals(1));
		assertTrue(uiTest.getSuite().getId() == uiTestSuite.getId());
		
		//Update Entity	
		uiTest.setMessage("updateMessage");
		tx.begin();
		em.persist(uiTest);	
		tx.commit();
		
		uiTest = em.find(UITest.class, uiTest.getId());
		assertTrue(uiTestSuite.getBuild().getId() == build.getId());
		assertTrue(uiTest.getAppUsername().equals("appUsername"));
		assertTrue(uiTest.getFailureCount().equals(1));
//		assertTrue(uiTest.getGroupName().equals("groupName"));
		assertTrue(uiTest.getMessage().equals("updateMessage"));
		assertTrue(uiTest.getStartDate() != null);
		assertTrue(uiTest.getEndDate() != null);
		assertTrue(uiTest.getStatus().equals(com.modeln.build.enums.test.Status.PASS));
		assertTrue(uiTest.getStepCount().equals(1));
		assertTrue(uiTest.getSuccessCount().equals(1));
		assertTrue(uiTest.getSuite().getId() == uiTestSuite.getId());
		
		//Delete Entity
		tx.begin();
		em.remove(uiTest);
		em.remove(uiTestSuite);
		em.remove(build);
		em.remove(user);
		em.remove(group);
		tx.commit();
		
		Query queryFindAllUsers = em.createQuery("SELECT u FROM User u");
		List<User> users = queryFindAllUsers.getResultList();
		assertTrue(users.isEmpty());
		
		Query queryFindAllGroups = em.createQuery("SELECT u FROM Group u");
		List<Group> groups = queryFindAllGroups.getResultList();
		assertTrue(groups.isEmpty());
		
		Query queryFindAllBuilds = em.createQuery("SELECT u FROM Build u");
		List<Build> builds = queryFindAllBuilds.getResultList();
		assertTrue(builds.isEmpty());
		
		Query queryFindAllUITestSuites = em.createQuery("SELECT u FROM UITestSuite u");
		List<UITestSuite> uiTestSuites = queryFindAllUITestSuites.getResultList();
		assertTrue(uiTestSuites.isEmpty());
		
		Query queryFindAllUITests = em.createQuery("SELECT u FROM UITest u");
		List<UITest> uiTests = queryFindAllUITests.getResultList();
		assertTrue(uiTests.isEmpty());
		
	}
	
}
