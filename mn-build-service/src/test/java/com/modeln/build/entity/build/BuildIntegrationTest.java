package com.modeln.build.entity.build;

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
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class BuildIntegrationTest extends AbstractEntityIntegrationTest {
	
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
		
		// Creates Entity
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
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(group);
		em_bc.persist(user);
		em_bc.persist(build);
		tx_bc.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		
		build = em_bc.find(Build.class, build.getId());
		assertTrue(build.getBuildComments().equals("buildComments"));
		assertTrue(build.getBuildStatus().equals(buildStatus));
		assertTrue(build.getSupportStatus().equals(SupportStatus.ACTIVE));
		assertTrue(build.getComments().equals("comments"));
		assertTrue(build.getDownloadUri().equals("downloadUri"));
		assertTrue(build.getStartTime() != null);
		assertTrue(build.getEndTime() != null);
		assertTrue(build.getHostname().equals("hostname"));
		assertTrue(build.getJdkVendor().equals("jdkVendor"));
		assertTrue(build.getJdkVersion().equals("jdkVersion"));
		assertTrue(build.getJobUrl().equals("jobUrl"));
		assertTrue(build.getOsArch().equals("osArch"));
		assertTrue(build.getOsName().equals("osName"));
		assertTrue(build.getOsVersion().equals("osVersion"));
		assertTrue(build.getStatus().equals("status"));
		assertTrue(build.getKeyAlgorithm().equals("keyAlgorithm"));
		assertTrue(build.getVerPublicKey().equals("verPublicKey"));
		assertTrue(build.getVerPrivateKey().equals("verPrivateKey"));
		assertTrue(build.getUser().getId() == user.getId());
		assertTrue(build.getUsername().equals("username"));
		assertTrue(build.getVersion().equals("version"));
		assertTrue(build.getVersionControlId().equals("versionControlId"));
		assertTrue(build.getVersionControlRoot().equals("versionControlRoot"));
		assertTrue(build.getVersionControlType().equals(SourceVersionControlSystem.GIT));
		
		//Update Entity	
		build.setBuildComments("updateBuildComments");
		tx_bc.begin();
		em_bc.persist(build);	
		tx_bc.commit();
		
		build = em_bc.find(Build.class, build.getId());
		assertTrue(build.getBuildComments().equals("updateBuildComments"));
		assertTrue(build.getBuildStatus().equals(buildStatus));
		assertTrue(build.getSupportStatus().equals(SupportStatus.ACTIVE));
		assertTrue(build.getComments().equals("comments"));
		assertTrue(build.getDownloadUri().equals("downloadUri"));
		assertTrue(build.getStartTime() != null);
		assertTrue(build.getEndTime() != null);
		assertTrue(build.getHostname().equals("hostname"));
		assertTrue(build.getJdkVendor().equals("jdkVendor"));
		assertTrue(build.getJdkVersion().equals("jdkVersion"));
		assertTrue(build.getJobUrl().equals("jobUrl"));
		assertTrue(build.getOsArch().equals("osArch"));
		assertTrue(build.getOsName().equals("osName"));
		assertTrue(build.getOsVersion().equals("osVersion"));
		assertTrue(build.getStatus().equals("status"));
		assertTrue(build.getKeyAlgorithm().equals("keyAlgorithm"));
		assertTrue(build.getVerPublicKey().equals("verPublicKey"));
		assertTrue(build.getVerPrivateKey().equals("verPrivateKey"));
		assertTrue(build.getUser().getId() == user.getId());
		assertTrue(build.getUsername().equals("username"));
		assertTrue(build.getVersion().equals("version"));
		assertTrue(build.getVersionControlId().equals("versionControlId"));
		assertTrue(build.getVersionControlRoot().equals("versionControlRoot"));
		assertTrue(build.getVersionControlType().equals(SourceVersionControlSystem.GIT));
		
		//Delete Entity
		tx_bc.begin();
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
	}
}
