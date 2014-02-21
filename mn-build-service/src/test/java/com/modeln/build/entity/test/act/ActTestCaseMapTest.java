package com.modeln.build.entity.test.act;

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
import com.modeln.build.entity.test.act.Act;
import com.modeln.build.entity.test.act.ActSuite;
import com.modeln.build.entity.test.act.ActTestCaseMap;
import com.modeln.build.entity.test.act.id.ActTestCaseMapId;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class ActTestCaseMapTest extends AbstractEntityTest {
	
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
		
		// Creates Entity
		ActTestCaseMap actTestCaseMap = new ActTestCaseMap();
		actTestCaseMap.setAct(act);
		actTestCaseMap.setTestcase("testcase");
		
		// Persists to the database
		tx.begin();
		em.persist(group);
		em.persist(user);
		em.persist(build);
		em.persist(actSuite);
		em.persist(act);
		em.persist(actTestCaseMap);
		tx.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("ActSuite ID should not be null", actSuite.getId());
		assertNotNull("Act ID should not be null", act.getId());
		assertNotNull("ActTestCaseMap ID should not be null", actTestCaseMap.getTestcase());
		
		actTestCaseMap = em.find(ActTestCaseMap.class, new ActTestCaseMapId(act.getId(), actTestCaseMap.getTestcase()));
		assertTrue(actTestCaseMap != null);
		
		//Update Entity	Not Possible
		
		//Delete Entity
		tx.begin();
		em.remove(actTestCaseMap);
		em.remove(act);
		em.remove(actSuite);
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
		
		Query queryFindAllActSuites = em.createQuery("SELECT u FROM ActSuite u");
		List<ActSuite> actSuites = queryFindAllActSuites.getResultList();
		assertTrue(actSuites.isEmpty());
		
		Query queryFindAllActs = em.createQuery("SELECT u FROM Act u");
		List<Act> acts = queryFindAllActs.getResultList();
		assertTrue(acts.isEmpty());
		
		Query queryFindAllActTestCaseMaps = em.createQuery("SELECT u FROM ActTestCaseMap u");
		List<Act> actTestCaseMaps = queryFindAllActTestCaseMaps.getResultList();
		assertTrue(actTestCaseMaps.isEmpty());
		
	}
	
}
