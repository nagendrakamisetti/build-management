package com.modeln.build.entity.auth;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.auth.Group;
import com.modeln.build.enums.auth.Role;
import com.modeln.build.enums.auth.Permission;

public class GroupIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {
		Query queryCountGroups = em_bc.createQuery ("SELECT count(x) FROM Group x");
		Number countGroups = (Number) queryCountGroups.getSingleResult ();
		assertTrue(!countGroups.equals(0));
		
		Query queryFindAllGroup = em_bc.createQuery("SELECT g FROM Group g");
		List<Group> groups = queryFindAllGroup.getResultList();
		assertTrue(countGroups.intValue() == groups.size());
		
		//Create Depend Entities
		Group group = new Group();
		group.setGid(countGroups.intValue()+1);
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
		group1.setGid(countGroups.intValue()+2);
		group1.setDesc("desc");
		group1.setName("group1");
		group1.setType(Role.ADMIN);
		group1.setPermGroup(permEdit);
		group1.setPermUser(permEditAndDelete);
		group1.setPermSelf(permEdit);
		group1.setParent(group);
		group1.setPermListing(permEditAndDelete);
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(group);
		em_bc.persist(group1);	
		tx_bc.commit();
		
		assertNotNull("Group ID should not be null", group.getId());
		assertNotNull("Group1 ID should not be null", group1.getId());
		
		//Retreive User
		group1 = em_bc.find(Group.class, group1.getId());
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
		tx_bc.begin();
		em_bc.persist(group1);	
		tx_bc.commit();
		
		group1 = em_bc.find(Group.class, group1.getId());
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
		tx_bc.begin();
		em_bc.remove(group1);
		tx_bc.commit();
		
		tx_bc.begin();
		em_bc.remove(group);
		tx_bc.commit();
		
		Query queryFindAllGroups = em_bc.createQuery("SELECT g FROM Group g");
		groups = queryFindAllGroups.getResultList();
		assertTrue(groups.size() == countGroups.intValue());
			
	}
}
