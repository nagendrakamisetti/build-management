package com.modeln.build.entity.customer;

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

public class EnvironmentTest extends AbstractEntityTest {
	
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
		try{
			tx.begin();
			em.persist(group);
			em.persist(user);
			em.persist(build);
			em.persist(account);
			em.persist(product);
			em.persist(environment);
			tx.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Build ID should not be null", build.getId());
		assertNotNull("Account ID should not be null", account.getId());
		assertNotNull("Product ID should not be null", product.getId());
		assertNotNull("Environment ID should not be null", environment.getId());
		
		environment = em.find(Environment.class, environment.getId());
		assertTrue(environment.getName().equals("name"));
		assertTrue(environment.getShortName().equals("shortName"));
		assertTrue(environment.getBuild().getId() == build.getId());
		assertTrue(environment.getAccount().getId() == account.getId());
		assertTrue(environment.getProduct().getId() == product.getId());
		
		//Update Entity	
		environment.setName("updateName");
		tx.begin();
		em.persist(environment);	
		tx.commit();
		
		environment = em.find(Environment.class, environment.getId());
		assertTrue(environment.getName().equals("updateName"));
		assertTrue(environment.getShortName().equals("shortName"));
		assertTrue(environment.getBuild().getId() == build.getId());
		assertTrue(environment.getAccount().getId() == account.getId());
		assertTrue(environment.getProduct().getId() == product.getId());
		
		//Delete Entity
		tx.begin();
		em.remove(environment);
		em.remove(account);
		em.remove(product);
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
		
		Query queryFindAllAccounts = em.createQuery("SELECT u FROM Account u");
		List<Account> accounts = queryFindAllAccounts.getResultList();
		assertTrue(accounts.isEmpty());
		
		Query queryFindAllProducts = em.createQuery("SELECT u FROM Product u");
		List<Product> products = queryFindAllProducts.getResultList();
		assertTrue(products.isEmpty());
		
		Query queryFindAllEnvironments = em.createQuery("SELECT u FROM Environment u");
		List<Environment> environments = queryFindAllEnvironments.getResultList();
		assertTrue(environments.isEmpty());
		
	}
}
