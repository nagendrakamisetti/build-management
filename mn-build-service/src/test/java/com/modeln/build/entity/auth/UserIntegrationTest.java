package com.modeln.build.entity.auth;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.auth.Group;
import com.modeln.build.entity.auth.User;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;

public class UserIntegrationTest extends AbstractEntityIntegrationTest {
	
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
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(group);
		em_bc.persist(user);	
		tx_bc.commit();
		
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Group ID should not be null", group.getId());
		
		//Retreive User
		user = em_bc.find(User.class, user.getId());
		assertTrue(user.getUsername().equals("username"));
		assertTrue(user.getFirstName().equals("Firstname"));
		assertTrue(user.getLastName().equals("Lastname"));
		assertTrue(user.getCountry().equals("us"));
		assertTrue(user.getLanguage().equals("en"));
		assertTrue(user.getAccountStatus().equals(Status.ACTIVE));
		assertTrue(user.getEmailAddress().equals("email@company.com"));
		assertTrue(user.getPassword().equals("password"));
		assertTrue(user.getPasswordEncryption().equals(Encryption.CRYPT));
		assertTrue(user.getTitle().equals(Title.MR));
		assertTrue(user.getPrimaryGroup().getId().equals(group.getId()));
		assertTrue(user.getLoginFailure() != null);
		assertTrue(user.getLoginSuccess() != null);
		assertTrue(user.getLoginFailureCount() == 2);
		
		//Update Entity	
		user.setUsername("updateUserName");
		user.setLoginFailureCount(user.getLoginFailureCount()+1);
		tx_bc.begin();
		em_bc.persist(user);	
		tx_bc.commit();
		
		user = em_bc.find(User.class, user.getId());
		assertTrue(user.getUsername().equals("updateUserName"));
		assertTrue(user.getFirstName().equals("Firstname"));
		assertTrue(user.getLastName().equals("Lastname"));
		assertTrue(user.getCountry().equals("us"));
		assertTrue(user.getLanguage().equals("en"));
		assertTrue(user.getAccountStatus().equals(Status.ACTIVE));
		assertTrue(user.getEmailAddress().equals("email@company.com"));
		assertTrue(user.getPassword().equals("password"));
		assertTrue(user.getPasswordEncryption().equals(Encryption.CRYPT));
		assertTrue(user.getTitle().equals(Title.MR));
		assertTrue(user.getPrimaryGroup().getId().equals(group.getId()));
		assertTrue(user.getLoginFailure() != null);
		assertTrue(user.getLoginSuccess() != null);
		assertTrue(user.getLoginFailureCount() == 3);
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(user);
		tx_bc.commit();
		
		Query queryFindAllUsers = em_bc.createQuery("SELECT u FROM User u");
		users = queryFindAllUsers.getResultList();
		assertTrue(users.size() == countUsers.intValue());
		
		tx_bc.begin();
		em_bc.remove(group);
		tx_bc.commit();
		
		Query queryFindAllGroups = em_bc.createQuery("SELECT g FROM Group g");
		groups = queryFindAllGroups.getResultList();
		assertTrue(groups.size() == countGroups.intValue());
			
	}

}
