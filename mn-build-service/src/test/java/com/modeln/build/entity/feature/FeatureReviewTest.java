package com.modeln.build.entity.feature;

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
import com.modeln.build.entity.feature.FeatureArea;
import com.modeln.build.entity.feature.FeatureReview;
import com.modeln.build.entity.feature.id.FeatureReviewId;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;
import com.modeln.build.enums.feature.ApprovalStatus;

public class FeatureReviewTest extends AbstractEntityTest {
	
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
		
		//Create Dependent Entity
		FeatureArea featureArea = new FeatureArea();
		featureArea.setDesc("desc");
		featureArea.setEmail("email@company.com");
		featureArea.setName("name");
		
		// Persists to the database
		tx.begin();
		em.persist(group);
		em.persist(user);
		em.persist(build);
		em.persist(featureArea);
		tx.commit();
		
		FeatureReview featureReview = new FeatureReview();
		featureReview.setArea(featureArea);
		featureReview.setBuildId(build.getId());
		featureReview.setComment("comment");
		featureReview.setUser(user);
		featureReview.setReviewDate(new Date());
		featureReview.setStatus(ApprovalStatus.APPROVED);
		
		// Persists to the database
		tx.begin();
		em.persist(featureReview);
		tx.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("FeatureArea ID should not be null", featureArea.getId());
		assertNotNull("FeatureReview (FeatureArea) ID should not be null", featureReview.getArea());
		assertNotNull("FeatureReview (Build) ID should not be null", featureReview.getBuildId());
		
		featureReview = em.find(FeatureReview.class, new FeatureReviewId(featureArea.getId(), build.getId()));
		assertTrue(featureReview.getArea().getId() == featureArea.getId());
		assertTrue(featureReview.getBuildId() == build.getId());
		assertTrue(featureReview.getComment().equals("comment"));
		assertTrue(featureReview.getUser().getId() == user.getId());
		assertTrue(featureReview.getReviewDate() != null);
		assertTrue(featureReview.getStatus().equals(ApprovalStatus.APPROVED));
		
		//Update Entity	
		featureReview.setComment("updateComment");
		tx.begin();
		em.persist(featureReview);	
		tx.commit();
		
		featureReview = em.find(FeatureReview.class, new FeatureReviewId(featureArea.getId(), build.getId()));
		assertTrue(featureReview.getArea().getId() == featureArea.getId());
		assertTrue(featureReview.getBuildId() == build.getId());
		assertTrue(featureReview.getComment().equals("updateComment"));
		assertTrue(featureReview.getUser().getId() == user.getId());
		assertTrue(featureReview.getReviewDate() != null);
		assertTrue(featureReview.getStatus().equals(ApprovalStatus.APPROVED));
		
		//Delete Entity
		tx.begin();
		em.remove(featureReview);
		em.remove(featureArea);
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
		
		Query queryFindAllFeatureAreas = em.createQuery("SELECT u FROM FeatureArea u");
		List<FeatureArea> featureAreas = queryFindAllFeatureAreas.getResultList();
		assertTrue(featureAreas.isEmpty());
		
		Query queryFindAllFeatureReviews = em.createQuery("SELECT u FROM FeatureReview u");
		List<FeatureReview> featureReviews = queryFindAllFeatureReviews.getResultList();
		assertTrue(featureReviews.isEmpty());
		
	}
}
