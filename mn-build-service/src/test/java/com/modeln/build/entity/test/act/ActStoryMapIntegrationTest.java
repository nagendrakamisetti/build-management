package com.modeln.build.entity.test.act;

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
import com.modeln.build.entity.test.act.Act;
import com.modeln.build.entity.test.act.ActStoryMap;
import com.modeln.build.entity.test.act.ActSuite;
import com.modeln.build.entity.test.act.id.ActStoryMapId;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class ActStoryMapIntegrationTest extends AbstractEntityIntegrationTest {
	
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
		Query queryCountActSuites = em_bc.createQuery ("SELECT count(x) FROM ActSuite x");
		Number countActSuites = (Number) queryCountActSuites.getSingleResult ();
		assertTrue(!countActSuites.equals(0));
		
		//Retrieve Entities
		Query queryFindAllActSuite = em_bc.createQuery("SELECT g FROM ActSuite g");
		queryFindAllActSuite.setMaxResults(10);
		List<ActSuite> actSuites = queryFindAllActSuite.getResultList();
		assertTrue(actSuites.size() != 0);
		
		//Count number of Entities
		Query queryCountActs = em_bc.createQuery ("SELECT count(x) FROM Act x");
		Number countActs = (Number) queryCountActs.getSingleResult ();
		assertTrue(!countActs.equals(0));
		
		//Retrieve Entities
		Query queryFindAllAct = em_bc.createQuery("SELECT g FROM Act g");
		queryFindAllAct.setMaxResults(10);
		List<ActSuite> acts = queryFindAllAct.getResultList();
		assertTrue(acts.size() != 0);
		
		//Count number of Entities
		Query queryCountActStoryMaps = em_bc.createQuery ("SELECT count(x) FROM ActStoryMap x");
		Number countActStoryMap = (Number) queryCountActStoryMaps.getSingleResult ();
		assertTrue(!countActStoryMap.equals(0));
		
		//Retrieve Entities
		Query queryFindAllActStoryMap = em_bc.createQuery("SELECT g FROM ActStoryMap g");
		queryFindAllActStoryMap.setMaxResults(10);
		List<ActSuite> actStoryMaps = queryFindAllActStoryMap.getResultList();
		assertTrue(actStoryMaps.size() != 0);

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
		ActSuite actSuite = new ActSuite();
		actSuite.setBuild(build);
		actSuite.setEnvName("envName");
		actSuite.setGroupId(1l);
		actSuite.setGroupName("groupName");
		actSuite.setHostname("hostname");
		actSuite.setJdbcUrl("jdbcUrl");
		actSuite.setJdkVendor("jdkVendor");
		actSuite.setJdkVersion("jdkVersion");
		actSuite.setMaxThreads(3);
		actSuite.setName("name");
		actSuite.setOsArch("osArch");
		actSuite.setOsName("osName");
		actSuite.setOsVersion("osVersion");
		actSuite.setStartDate(new Date());
		actSuite.setEndDate(new Date());
		actSuite.setSuiteOptions("suiteOptions");
		actSuite.setTestCount(1);
		actSuite.setUsername("username");
		
		// Creates Dependent Entity
		Act act = new Act();
		act.setAuthor("author");
		act.setFilename("filename");
		act.setGroupName("groupName");
		act.setMessage("message");
		act.setStatus(com.modeln.build.enums.test.Status.PASS);
		act.setSuite(actSuite);
		act.setSummary("summary");
		act.setStartDate(new Date());
		act.setEndDate(new Date());
		
		ActStoryMap actStoryMap = new ActStoryMap();
		actStoryMap.setAct(act);
		actStoryMap.setStory("story");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(group);
		em_bc.persist(user);
		em_bc.persist(build);
		em_bc.persist(actSuite);
		em_bc.persist(act);
		em_bc.persist(actStoryMap);
		tx_bc.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("ActSuite ID should not be null", actSuite.getId());
		assertNotNull("Act ID should not be null", act.getId());
		
		actStoryMap = em_bc.find(ActStoryMap.class, new ActStoryMapId(act.getId(), actStoryMap.getStory()));
		assertTrue(actStoryMap != null);
		
		//Update Entity	Not Possible
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(actStoryMap);
		em_bc.remove(act);
		em_bc.remove(actSuite);
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
		
		Query queryCountActSuiteAfter = em_bc.createQuery ("SELECT count(x) FROM ActSuite x");
		Number countActSuiteAfter = (Number) queryCountActSuiteAfter.getSingleResult ();
		assertTrue(countActSuites.intValue() == countActSuiteAfter.intValue());
		
		Query queryCountActAfter = em_bc.createQuery ("SELECT count(x) FROM Act x");
		Number countActAfter = (Number) queryCountActAfter.getSingleResult ();
		assertTrue(countActs.intValue() == countActAfter.intValue());
		
		Query queryCountActStoryMapAfter = em_bc.createQuery ("SELECT count(x) FROM ActStoryMap x");
		Number countActStoryMapAfter = (Number) queryCountActStoryMapAfter.getSingleResult ();
		assertTrue(countActStoryMap.intValue() == countActStoryMapAfter.intValue());
		
	}
}


