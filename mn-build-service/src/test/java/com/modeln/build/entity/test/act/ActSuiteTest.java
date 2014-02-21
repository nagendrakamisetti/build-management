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
import com.modeln.build.entity.test.act.ActSuite;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class ActSuiteTest extends AbstractEntityTest {
	
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
		
		// Persists to the database
		tx.begin();
		em.persist(group);
		em.persist(user);
		em.persist(build);
		em.persist(actSuite);
		tx.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("ActSuite ID should not be null", actSuite.getId());
		
		actSuite = em.find(ActSuite.class, actSuite.getId());
		assertTrue(actSuite.getBuild().getId() == build.getId());
		assertTrue(actSuite.getEnvName().equals("envName"));
		assertTrue(actSuite.getGroupId().equals(1l));
		assertTrue(actSuite.getGroupName().equals("groupName"));
		assertTrue(actSuite.getHostname().equals("hostname"));
		assertTrue(actSuite.getJdbcUrl().equals("jdbcUrl"));
		assertTrue(actSuite.getJdkVendor().equals("jdkVendor"));
		assertTrue(actSuite.getJdkVersion().equals("jdkVersion"));
		assertTrue(actSuite.getMaxThreads().equals(3));
		assertTrue(actSuite.getName().equals("name"));
		assertTrue(actSuite.getOsArch().equals("osArch"));
		assertTrue(actSuite.getOsName().equals("osName"));
		assertTrue(actSuite.getOsVersion().equals("osVersion"));
		assertTrue(actSuite.getStartDate() != null);
		assertTrue(actSuite.getEndDate() != null);
		assertTrue(actSuite.getSuiteOptions().equals("suiteOptions"));
		assertTrue(actSuite.getTestCount().equals(1));
		assertTrue(actSuite.getUsername().equals("username"));
		
		//Update Entity	
		actSuite.setName("updateName");
		tx.begin();
		em.persist(actSuite);	
		tx.commit();
		
		actSuite = em.find(ActSuite.class, actSuite.getId());
		assertTrue(actSuite.getBuild().getId() == build.getId());
		assertTrue(actSuite.getEnvName().equals("envName"));
		assertTrue(actSuite.getGroupId().equals(1l));
		assertTrue(actSuite.getGroupName().equals("groupName"));
		assertTrue(actSuite.getHostname().equals("hostname"));
		assertTrue(actSuite.getJdbcUrl().equals("jdbcUrl"));
		assertTrue(actSuite.getJdkVendor().equals("jdkVendor"));
		assertTrue(actSuite.getJdkVersion().equals("jdkVersion"));
		assertTrue(actSuite.getMaxThreads().equals(3));
		assertTrue(actSuite.getName().equals("updateName"));
		assertTrue(actSuite.getOsArch().equals("osArch"));
		assertTrue(actSuite.getOsName().equals("osName"));
		assertTrue(actSuite.getOsVersion().equals("osVersion"));
		assertTrue(actSuite.getStartDate() != null);
		assertTrue(actSuite.getEndDate() != null);
		assertTrue(actSuite.getSuiteOptions().equals("suiteOptions"));
		assertTrue(actSuite.getTestCount().equals(1));
		assertTrue(actSuite.getUsername().equals("username"));
		
		//Delete Entity
		tx.begin();
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
		
	}

}
