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
import com.modeln.build.entity.test.uit.UITestSuite;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class UITestSuiteIntegrationTest extends AbstractEntityIntegrationTest {
	
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
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(group);
		em_bc.persist(user);
		em_bc.persist(build);
		em_bc.persist(uiTestSuite);
		tx_bc.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("UITestSuite ID should not be null", uiTestSuite.getId());
		
		uiTestSuite = em_bc.find(UITestSuite.class, uiTestSuite.getId());
		assertTrue(uiTestSuite.getBuild().getId() == build.getId());
		assertTrue(uiTestSuite.getEnvName().equals("envName"));
//		assertTrue(uiTestSuite.getGroupId().equals(1l));
//		assertTrue(uiTestSuite.getGroupName().equals("groupName"));
		assertTrue(uiTestSuite.getHostname().equals("hostname"));
		assertTrue(uiTestSuite.getJdbcUrl().equals("jdbcUrl"));
		assertTrue(uiTestSuite.getJdkVendor().equals("jdkVendor"));
		assertTrue(uiTestSuite.getJdkVersion().equals("jdkVersion"));
//		assertTrue(uiTestSuite.getMaxThreads().equals(3));
		assertTrue(uiTestSuite.getName().equals("name"));
		assertTrue(uiTestSuite.getOsArch().equals("osArch"));
		assertTrue(uiTestSuite.getOsName().equals("osName"));
		assertTrue(uiTestSuite.getOsVersion().equals("osVersion"));
		assertTrue(uiTestSuite.getStartDate() != null);
		assertTrue(uiTestSuite.getEndDate() != null);
		assertTrue(uiTestSuite.getSuiteOptions().equals("suiteOptions"));
//		assertTrue(uiTestSuite.getTestCount().equals(1));
		assertTrue(uiTestSuite.getUsername().equals("username"));
		assertTrue(uiTestSuite.getAppServer().equals("appServer"));
		assertTrue(uiTestSuite.getAppServerVersion().equals("Version"));
		assertTrue(uiTestSuite.getAppUrl().equals("appUrl"));
		assertTrue(uiTestSuite.getClient().equals("client"));
		assertTrue(uiTestSuite.getClientVersion().equals("Version"));
		assertTrue(uiTestSuite.getWebServer().equals("webServer"));
		assertTrue(uiTestSuite.getWebServerVersion().equals("webServerVersion"));
		
		//Update Entity	
		uiTestSuite.setName("updateName");
		tx_bc.begin();
		em_bc.persist(uiTestSuite);	
		tx_bc.commit();
		
		uiTestSuite = em_bc.find(UITestSuite.class, uiTestSuite.getId());
		assertTrue(uiTestSuite.getBuild().getId() == build.getId());
		assertTrue(uiTestSuite.getEnvName().equals("envName"));
//		assertTrue(uiTestSuite.getGroupId().equals(1l));
//		assertTrue(uiTestSuite.getGroupName().equals("groupName"));
		assertTrue(uiTestSuite.getHostname().equals("hostname"));
		assertTrue(uiTestSuite.getJdbcUrl().equals("jdbcUrl"));
		assertTrue(uiTestSuite.getJdkVendor().equals("jdkVendor"));
		assertTrue(uiTestSuite.getJdkVersion().equals("jdkVersion"));
//		assertTrue(uiTestSuite.getMaxThreads().equals(3));
		assertTrue(uiTestSuite.getName().equals("updateName"));
		assertTrue(uiTestSuite.getOsArch().equals("osArch"));
		assertTrue(uiTestSuite.getOsName().equals("osName"));
		assertTrue(uiTestSuite.getOsVersion().equals("osVersion"));
		assertTrue(uiTestSuite.getStartDate() != null);
		assertTrue(uiTestSuite.getEndDate() != null);
		assertTrue(uiTestSuite.getSuiteOptions().equals("suiteOptions"));
//		assertTrue(uiTestSuite.getTestCount().equals(1));
		assertTrue(uiTestSuite.getUsername().equals("username"));
		assertTrue(uiTestSuite.getAppServer().equals("appServer"));
		assertTrue(uiTestSuite.getAppServerVersion().equals("Version"));
		assertTrue(uiTestSuite.getAppUrl().equals("appUrl"));
		assertTrue(uiTestSuite.getClient().equals("client"));
		assertTrue(uiTestSuite.getClientVersion().equals("Version"));
		assertTrue(uiTestSuite.getWebServer().equals("webServer"));
		assertTrue(uiTestSuite.getWebServerVersion().equals("webServerVersion"));
		
		//Delete Entity
		tx_bc.begin();
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
	}
}

