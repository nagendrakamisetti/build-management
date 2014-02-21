package com.modeln.build.entity.customer;

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
import com.modeln.build.entity.customer.Account;
import com.modeln.build.entity.customer.Environment;
import com.modeln.build.entity.customer.Product;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;
import com.modeln.build.enums.build.SupportStatus;
import com.modeln.build.enums.build.SourceVersionControlSystem;
import com.modeln.build.enums.customer.BranchType;

public class EnvironmentIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		//Count number of Entities
		Query queryCountAccounts = em_bc.createQuery ("SELECT count(x) FROM Account x");
		Number countAccounts = (Number) queryCountAccounts.getSingleResult ();
		assertTrue(!countAccounts.equals(0));
		
		//Retrieve Entities
		Query queryCountAccount = em_bc.createQuery("SELECT g FROM Account g");
		List<Account> accounts = queryCountAccount.getResultList();
		assertTrue(countAccounts.intValue() == accounts.size());
		
		//Count number of Entities
		Query queryCountProducts = em_bc.createQuery ("SELECT count(x) FROM Product x");
		Number countProducts = (Number) queryCountProducts.getSingleResult ();
		assertTrue(!countProducts.equals(0));
		
		//Retrieve Entities
		Query queryCountProduct = em_bc.createQuery("SELECT g FROM Product g");
		List<Product> products = queryCountProduct.getResultList();
		assertTrue(countProducts.intValue() == products.size());
		
		//Count number of Entities
		Query queryCountEnvironments = em_bc.createQuery ("SELECT count(x) FROM Environment x");
		Number countEnvironments = (Number) queryCountEnvironments.getSingleResult ();
		assertTrue(!countEnvironments.equals(0));
		
		//Retrieve Entities
		Query queryCountEnvironment = em_bc.createQuery("SELECT g FROM Environment g");
		List<Environment> environments = queryCountEnvironment.getResultList();
		assertTrue(countEnvironments.intValue() == environments.size());
		
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
		
		// Creates User
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
		Account account = new Account();
		account.setBranchType(BranchType.CUSTOMER);
		account.setName("name");
		account.setShortName("shortName");
			
		Product product = new Product();
		product.setName("name");
		product.setDescription("description");
				
		// Creates Entity
		Environment environment = new Environment();
		environment.setName("name");
		environment.setShortName("shortName");
		environment.setProduct(product);
		environment.setAccount(account);
		environment.setBuild(build);
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(group);
		em_bc.persist(user);
		em_bc.persist(build);
		em_bc.persist(account);
		em_bc.persist(product);
		em_bc.persist(environment);
		tx_bc.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("Account ID should not be null", account.getId());
		assertNotNull("Product ID should not be null", product.getId());
		assertNotNull("Environment ID should not be null", environment.getId());
		
		//Retreive Entity
		environment = em_bc.find(Environment.class, environment.getId());
		assertTrue(environment.getName().equals("name"));
		assertTrue(environment.getShortName().equals("shortName"));
		assertTrue(environment.getBuild().getId() == build.getId());
		assertTrue(environment.getAccount().getId() == account.getId());
		assertTrue(environment.getProduct().getId() == product.getId());
		
		//Update Entity	
		environment.setName("updateName");
		tx_bc.begin();
		em_bc.persist(environment);	
		tx_bc.commit();
		
		environment = em_bc.find(Environment.class, environment.getId());
		assertTrue(environment.getName().equals("updateName"));
		assertTrue(environment.getShortName().equals("shortName"));
		assertTrue(environment.getBuild().getId() == build.getId());
		assertTrue(environment.getAccount().getId() == account.getId());
		assertTrue(environment.getProduct().getId() == product.getId());
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(environment);
		em_bc.remove(account);
		em_bc.remove(product);
		em_bc.remove(build);
		em_bc.remove(user);
		em_bc.remove(group);
		tx_bc.commit();
		
		Query queryFindAllAccounts = em_bc.createQuery("SELECT u FROM Account u");
		accounts = queryFindAllAccounts.getResultList();
		assertTrue(accounts.size() == countAccounts.intValue());
		
		Query queryFindAllProducts = em_bc.createQuery("SELECT u FROM Product u");
		products = queryFindAllProducts.getResultList();
		assertTrue(products.size() == countProducts.intValue());
		
		Query queryFindAllEnvironments = em_bc.createQuery("SELECT u FROM Environment u");
		environments = queryFindAllEnvironments.getResultList();
		assertTrue(environments.size() == countEnvironments.intValue());
		
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
