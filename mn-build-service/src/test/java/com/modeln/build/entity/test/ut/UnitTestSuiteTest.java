package com.modeln.build.entity.test.ut;

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
import com.modeln.build.entity.test.ut.UnitTestSuite;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class UnitTestSuiteTest extends AbstractEntityTest {
	
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
		
		// Persists to the database
		tx.begin();
		em.persist(group);
		em.persist(user);
		em.persist(build);
		em.persist(unitTestSuite);
		tx.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("UnitTestSuite ID should not be null", unitTestSuite.getId());
		
		unitTestSuite = em.find(UnitTestSuite.class, unitTestSuite.getId());
		assertTrue(unitTestSuite.getBuild().getId() == build.getId());
		assertTrue(unitTestSuite.getEnvName().equals("envName"));
		assertTrue(unitTestSuite.getGroupId().equals(1l));
		assertTrue(unitTestSuite.getGroupName().equals("groupName"));
		assertTrue(unitTestSuite.getHostname().equals("hostname"));
		assertTrue(unitTestSuite.getJdbcUrl().equals("jdbcUrl"));
		assertTrue(unitTestSuite.getJdkVendor().equals("jdkVendor"));
		assertTrue(unitTestSuite.getJdkVersion().equals("jdkVersion"));
		assertTrue(unitTestSuite.getMaxThreads().equals(3));
		assertTrue(unitTestSuite.getName().equals("name"));
		assertTrue(unitTestSuite.getOsArch().equals("osArch"));
		assertTrue(unitTestSuite.getOsName().equals("osName"));
		assertTrue(unitTestSuite.getOsVersion().equals("osVersion"));
		assertTrue(unitTestSuite.getStartDate() != null);
		assertTrue(unitTestSuite.getEndDate() != null);
		assertTrue(unitTestSuite.getSuiteOptions().equals("suiteOptions"));
		assertTrue(unitTestSuite.getTestCount().equals(1));
		assertTrue(unitTestSuite.getUsername().equals("username"));
		assertTrue(unitTestSuite.getFailureSrc().equals("failureSrc"));
		
		//Update Entity	
		unitTestSuite.setName("updateName");
		tx.begin();
		em.persist(unitTestSuite);	
		tx.commit();
		
		unitTestSuite = em.find(UnitTestSuite.class, unitTestSuite.getId());
		assertTrue(unitTestSuite.getBuild().getId() == build.getId());
		assertTrue(unitTestSuite.getEnvName().equals("envName"));
		assertTrue(unitTestSuite.getGroupId().equals(1l));
		assertTrue(unitTestSuite.getGroupName().equals("groupName"));
		assertTrue(unitTestSuite.getHostname().equals("hostname"));
		assertTrue(unitTestSuite.getJdbcUrl().equals("jdbcUrl"));
		assertTrue(unitTestSuite.getJdkVendor().equals("jdkVendor"));
		assertTrue(unitTestSuite.getJdkVersion().equals("jdkVersion"));
		assertTrue(unitTestSuite.getMaxThreads().equals(3));
		assertTrue(unitTestSuite.getName().equals("updateName"));
		assertTrue(unitTestSuite.getOsArch().equals("osArch"));
		assertTrue(unitTestSuite.getOsName().equals("osName"));
		assertTrue(unitTestSuite.getOsVersion().equals("osVersion"));
		assertTrue(unitTestSuite.getStartDate() != null);
		assertTrue(unitTestSuite.getEndDate() != null);
		assertTrue(unitTestSuite.getSuiteOptions().equals("suiteOptions"));
		assertTrue(unitTestSuite.getTestCount().equals(1));
		assertTrue(unitTestSuite.getUsername().equals("username"));
		assertTrue(unitTestSuite.getFailureSrc().equals("failureSrc"));
		
		//Delete Entity
		tx.begin();
		em.remove(unitTestSuite);
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
		
		Query queryFindAllUnitTestSuites = em.createQuery("SELECT u FROM UnitTestSuite u");
		List<UnitTestSuite> unitTestSuites = queryFindAllUnitTestSuites.getResultList();
		assertTrue(unitTestSuites.isEmpty());
		
	}

}

