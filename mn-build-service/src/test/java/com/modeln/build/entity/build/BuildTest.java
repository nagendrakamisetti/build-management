package com.modeln.build.entity.build;

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
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;

public class BuildTest extends AbstractEntityTest {
	
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
		tx.begin();
		em.persist(group);
		em.persist(user);
		em.persist(build);
		tx.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		
		build = em.find(Build.class, build.getId());
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
		tx.begin();
		em.persist(build);	
		tx.commit();
		
		build = em.find(Build.class, build.getId());
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
		tx.begin();
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
		
	}
}
