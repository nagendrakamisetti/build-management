package com.modeln.build.entity.customer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.customer.Account;
import com.modeln.build.enums.customer.BranchType;

public class AccountTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		
		// Creates Entity
		Account account = new Account();
		account.setBranchType(BranchType.CUSTOMER);
		account.setName("name");
		account.setShortName("shortName");
		
		// Persists to the database
		tx.begin();
		em.persist(account);
		tx.commit();
		
		assertNotNull("Account ID should not be null", account.getId());
		
		//Retreive Entity
		account = em.find(Account.class, account.getId());
		assertTrue(account.getBranchType().equals(BranchType.CUSTOMER));
		assertTrue(account.getName().equals("name"));
		assertTrue(account.getShortName().equals("shortName"));
		
		//Update Entity	
		account.setName("updateName");
		tx.begin();
		em.persist(account);	
		tx.commit();
		
		account = em.find(Account.class, account.getId());
		assertTrue(account.getBranchType().equals(BranchType.CUSTOMER));
		assertTrue(account.getName().equals("updateName"));
		assertTrue(account.getShortName().equals("shortName"));
		
		//Delete Entity
		tx.begin();
		em.remove(account);
		tx.commit();
		
		Query queryFindAllAccounts = em.createQuery("SELECT u FROM Account u");
		List<Account> accounts = queryFindAllAccounts.getResultList();
		assertTrue(accounts.isEmpty());
		
	}
	
}
