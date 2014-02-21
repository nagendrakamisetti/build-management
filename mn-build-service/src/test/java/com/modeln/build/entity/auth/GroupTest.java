package com.modeln.build.entity.auth;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.auth.Group;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;

public class GroupTest extends AbstractEntityTest {
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		//Create Depend Entities
		Group group = new Group();
		group.setGid(0);
		group.setDesc("desc");
		group.setName("group");
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
		
		// Creates Group
		Group group1 = new Group();
		group1.setGid(1);
		group1.setDesc("desc");
		group1.setName("group1");
		group1.setType(Role.ADMIN);
		group1.setPermGroup(permEdit);
		group1.setPermUser(permEditAndDelete);
		group1.setPermSelf(permEdit);
		group1.setPermListing(permEditAndDelete);
		group1.setParent(group);
		
		// Persists to the database
		tx.begin();
		em.persist(group);
		em.persist(group1);	
		tx.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("Group1 ID should not be null", group1.getId());
		
		//Retreive User
		group1 = em.find(Group.class, group1.getId());
		assertTrue(group1.getDesc().equals("desc"));
		assertTrue(group1.getName().equals("group1"));
		assertTrue(group1.getType().equals(Role.ADMIN));
		assertTrue(group1.getPermGroup().equals(permEdit));
		assertTrue(group1.getPermUser().equals(permEditAndDelete));
		assertTrue(group1.getPermSelf().equals(permEdit));
		assertTrue(group1.getParent() != null);
		assertTrue(group1.getParent().getDesc().equals("desc"));
		assertTrue(group1.getParent().getName().equals("group"));
		assertTrue(group1.getParent().getType().equals(Role.ADMIN));
		assertTrue(group1.getParent().getPermGroup().equals(permEdit));
		assertTrue(group1.getParent().getPermUser().equals(permEditAndDelete));
		assertTrue(group1.getParent().getPermListing().equals(permEditAndDelete));
		assertTrue(group1.getParent().getPermSelf().equals(permEdit));
		assertTrue(group1.getParent().getParent() == null);
		
		//Update Entity	
		group1.setName("updateName");
		tx.begin();
		em.persist(group1);	
		tx.commit();
		
		group1 = em.find(Group.class, group1.getId());
		assertTrue(group1.getDesc().equals("desc"));
		assertTrue(group1.getName().equals("updateName"));
		assertTrue(group1.getType().equals(Role.ADMIN));
		assertTrue(group1.getPermGroup().equals(permEdit));
		assertTrue(group1.getPermUser().equals(permEditAndDelete));
		assertTrue(group1.getPermSelf().equals(permEdit));
		assertTrue(group1.getParent() != null);
		assertTrue(group1.getParent().getDesc().equals("desc"));
		assertTrue(group1.getParent().getName().equals("group"));
		assertTrue(group1.getParent().getType().equals(Role.ADMIN));
		assertTrue(group1.getParent().getPermGroup().equals(permEdit));
		assertTrue(group1.getParent().getPermUser().equals(permEditAndDelete));
		assertTrue(group1.getParent().getPermListing().equals(permEditAndDelete));
		assertTrue(group1.getParent().getPermSelf().equals(permEdit));
		assertTrue(group1.getParent().getParent() == null);
		
		//Delete Entity
		tx.begin();
		em.remove(group1);
		tx.commit();
		
		Query queryFindAllGroups = em.createQuery("SELECT g FROM Group g");
		List<Group> groups = queryFindAllGroups.getResultList();
		assertTrue(groups.size() == 1);
		
		tx.begin();
		em.remove(group);
		tx.commit();
		
		groups = queryFindAllGroups.getResultList();
		assertTrue(groups.isEmpty());
		
	}
}
