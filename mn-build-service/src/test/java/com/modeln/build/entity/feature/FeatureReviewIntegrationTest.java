package com.modeln.build.entity.feature;

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

public class FeatureReviewIntegrationTest extends AbstractEntityIntegrationTest {
	
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
		Query queryCountFeatureAreas = em_bc.createQuery ("SELECT count(x) FROM FeatureArea x");
		Number countFeatureAreas = (Number) queryCountFeatureAreas.getSingleResult ();
		assertTrue(!countFeatureAreas.equals(0));
		
		//Retrieve Entities
		Query queryFindAllFeatureArea = em_bc.createQuery("SELECT g FROM FeatureArea g");
		List<FeatureArea> featureAreas = queryFindAllFeatureArea.getResultList();
		assertTrue(countFeatureAreas.intValue() == featureAreas.size());
		
		//Count number of Entities
		Query queryCountFeatureReviews = em_bc.createQuery ("SELECT count(x) FROM FeatureReview x");
		Number countFeatureReviews = (Number) queryCountFeatureReviews.getSingleResult ();
		assertTrue(!countFeatureReviews.equals(0));
		
		//Retrieve Entities
		Query queryFindAllFeatureReview = em_bc.createQuery("SELECT g FROM FeatureReview g");
		List<FeatureReview> featureReviews = queryFindAllFeatureReview.getResultList();
		assertTrue(countFeatureReviews.intValue() == featureReviews.size());
		
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
		
		//Create Dependent Entity
		FeatureArea featureArea = new FeatureArea();
		featureArea.setDesc("desc");
		featureArea.setEmail("email@company.com");
		featureArea.setName("name");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(group);
		em_bc.persist(user);
		em_bc.persist(build);
		em_bc.persist(featureArea);
		tx_bc.commit();
		
		FeatureReview featureReview = new FeatureReview();
		featureArea = em_bc.find(FeatureArea.class, featureArea.getId());
		featureReview.setArea(featureArea);
		featureReview.setBuildId(build.getId());
		featureReview.setComment("comment");
		featureReview.setUser(user);
		featureReview.setReviewDate(new Date());
		featureReview.setStatus(ApprovalStatus.APPROVED);
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(featureReview);
		tx_bc.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("FeatureArea ID should not be null", featureArea.getId());
		assertNotNull("FeatureReview (FeatureArea) ID should not be null", featureReview.getArea());
		assertNotNull("FeatureReview (Build) ID should not be null", featureReview.getBuildId());
		
		featureReview = em_bc.find(FeatureReview.class, new FeatureReviewId(featureArea.getId(), build.getId()));
		assertTrue(featureReview.getArea().getId() == featureArea.getId());
		assertTrue(featureReview.getBuildId() == build.getId());
		assertTrue(featureReview.getComment().equals("comment"));
		assertTrue(featureReview.getUser().getId() == user.getId());
		assertTrue(featureReview.getReviewDate() != null);
		assertTrue(featureReview.getStatus().equals(ApprovalStatus.APPROVED));
		
		//Update Entity	
		featureReview.setComment("updateComment");
		tx_bc.begin();
		em_bc.persist(featureReview);	
		tx_bc.commit();
		
		featureReview = em_bc.find(FeatureReview.class, new FeatureReviewId(featureArea.getId(), build.getId()));
		assertTrue(featureReview.getArea().getId() == featureArea.getId());
		assertTrue(featureReview.getBuildId() == build.getId());
		assertTrue(featureReview.getComment().equals("updateComment"));
		assertTrue(featureReview.getUser().getId() == user.getId());
		assertTrue(featureReview.getReviewDate() != null);
		assertTrue(featureReview.getStatus().equals(ApprovalStatus.APPROVED));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(featureReview);
		em_bc.remove(featureArea);
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
		
		Query queryFindAllFeatureAreas = em_bc.createQuery("SELECT u FROM FeatureArea u");
		featureAreas = queryFindAllFeatureAreas.getResultList();
		assertTrue(featureAreas.size() == countFeatureAreas.intValue());
		
		Query queryFindAllFeatureReviews = em_bc.createQuery("SELECT u FROM FeatureReview u");
		featureReviews = queryFindAllFeatureReviews.getResultList();
		assertTrue(featureReviews.size() == countFeatureReviews.intValue());
	}
}
