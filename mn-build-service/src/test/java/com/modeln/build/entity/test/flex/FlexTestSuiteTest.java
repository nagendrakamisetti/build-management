package com.modeln.build.entity.test.flex;

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
import com.modeln.build.entity.test.flex.FlexTestSuite;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class FlexTestSuiteTest extends AbstractEntityTest {
	
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
		FlexTestSuite flexTestSuite = new FlexTestSuite();
		flexTestSuite.setBuild(build);
		flexTestSuite.setEnvName("envName");
		flexTestSuite.setGroupId(1l);
		flexTestSuite.setGroupName("groupName");
		flexTestSuite.setHostname("hostname");
		flexTestSuite.setJdbcUrl("jdbcUrl");
		flexTestSuite.setJdkVendor("jdkVendor");
		flexTestSuite.setJdkVersion("jdkVersion");
		flexTestSuite.setMaxThreads(3);
		flexTestSuite.setName("name");
		flexTestSuite.setOsArch("osArch");
		flexTestSuite.setOsName("osName");
		flexTestSuite.setOsVersion("osVersion");
		flexTestSuite.setStartDate(new Date());
		flexTestSuite.setEndDate(new Date());
		flexTestSuite.setSuiteOptions("suiteOptions");
		flexTestSuite.setTestCount(1);
		flexTestSuite.setUsername("username");
		flexTestSuite.setFailureSrc("failureSrc");
		
		// Persists to the database
		tx.begin();
		em.persist(group);
		em.persist(user);
		em.persist(build);
		em.persist(flexTestSuite);
		tx.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("FlexTestSuite ID should not be null", flexTestSuite.getId());
		
		flexTestSuite = em.find(FlexTestSuite.class, flexTestSuite.getId());
		assertTrue(flexTestSuite.getBuild().getId() == build.getId());
		assertTrue(flexTestSuite.getEnvName().equals("envName"));
		assertTrue(flexTestSuite.getGroupId().equals(1l));
		assertTrue(flexTestSuite.getGroupName().equals("groupName"));
		assertTrue(flexTestSuite.getHostname().equals("hostname"));
		assertTrue(flexTestSuite.getJdbcUrl().equals("jdbcUrl"));
		assertTrue(flexTestSuite.getJdkVendor().equals("jdkVendor"));
		assertTrue(flexTestSuite.getJdkVersion().equals("jdkVersion"));
		assertTrue(flexTestSuite.getMaxThreads().equals(3));
		assertTrue(flexTestSuite.getName().equals("name"));
		assertTrue(flexTestSuite.getOsArch().equals("osArch"));
		assertTrue(flexTestSuite.getOsName().equals("osName"));
		assertTrue(flexTestSuite.getOsVersion().equals("osVersion"));
		assertTrue(flexTestSuite.getStartDate() != null);
		assertTrue(flexTestSuite.getEndDate() != null);
		assertTrue(flexTestSuite.getSuiteOptions().equals("suiteOptions"));
		assertTrue(flexTestSuite.getTestCount().equals(1));
		assertTrue(flexTestSuite.getUsername().equals("username"));
		assertTrue(flexTestSuite.getFailureSrc().equals("failureSrc"));
		
		//Update Entity	
		flexTestSuite.setName("updateName");
		tx.begin();
		em.persist(flexTestSuite);	
		tx.commit();
		
		flexTestSuite = em.find(FlexTestSuite.class, flexTestSuite.getId());
		assertTrue(flexTestSuite.getBuild().getId() == build.getId());
		assertTrue(flexTestSuite.getEnvName().equals("envName"));
		assertTrue(flexTestSuite.getGroupId().equals(1l));
		assertTrue(flexTestSuite.getGroupName().equals("groupName"));
		assertTrue(flexTestSuite.getHostname().equals("hostname"));
		assertTrue(flexTestSuite.getJdbcUrl().equals("jdbcUrl"));
		assertTrue(flexTestSuite.getJdkVendor().equals("jdkVendor"));
		assertTrue(flexTestSuite.getJdkVersion().equals("jdkVersion"));
		assertTrue(flexTestSuite.getMaxThreads().equals(3));
		assertTrue(flexTestSuite.getName().equals("updateName"));
		assertTrue(flexTestSuite.getOsArch().equals("osArch"));
		assertTrue(flexTestSuite.getOsName().equals("osName"));
		assertTrue(flexTestSuite.getOsVersion().equals("osVersion"));
		assertTrue(flexTestSuite.getStartDate() != null);
		assertTrue(flexTestSuite.getEndDate() != null);
		assertTrue(flexTestSuite.getSuiteOptions().equals("suiteOptions"));
		assertTrue(flexTestSuite.getTestCount().equals(1));
		assertTrue(flexTestSuite.getUsername().equals("username"));
		assertTrue(flexTestSuite.getFailureSrc().equals("failureSrc"));
		
		//Delete Entity
		tx.begin();
		em.remove(flexTestSuite);
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
		
		Query queryFindAllFlexTestSuites = em.createQuery("SELECT u FROM FlexTestSuite u");
		List<FlexTestSuite> flexTestSuites = queryFindAllFlexTestSuites.getResultList();
		assertTrue(flexTestSuites.isEmpty());
		
	}

}
