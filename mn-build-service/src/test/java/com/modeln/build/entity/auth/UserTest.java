package com.modeln.build.entity.auth;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.auth.Group;
import com.modeln.build.entity.auth.User;
import com.modeln.build.enums.auth.Encryption;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;
import com.modeln.build.enums.auth.Status;
import com.modeln.build.enums.auth.Title;

public class UserTest extends AbstractEntityTest {
	
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
		tx.begin();
		em.persist(group);
		em.persist(user);	
		tx.commit();
		
		assertNotNull("User ID should not be null", user.getId());
		assertNotNull("Group ID should not be null", group.getId());
		
		//Retreive User
		user = em.find(User.class, user.getId());
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
		tx.begin();
		em.persist(user);	
		tx.commit();
		
		user = em.find(User.class, user.getId());
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
		tx.begin();
		em.remove(user);
		tx.commit();
		
		Query queryFindAllUsers = em.createQuery("SELECT u FROM User u");
		List<User> users = queryFindAllUsers.getResultList();
		assertTrue(users.isEmpty());
		
		tx.begin();
		em.remove(group);
		tx.commit();
		
		Query queryFindAllGroups = em.createQuery("SELECT g FROM Group g");
		List<Group> groups = queryFindAllGroups.getResultList();
		assertTrue(groups.isEmpty());
		
	}

}
