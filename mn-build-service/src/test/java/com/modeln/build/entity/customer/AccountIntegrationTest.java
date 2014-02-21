package com.modeln.build.entity.customer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.customer.Account;
import com.modeln.build.enums.customer.BranchType;

public class AccountIntegrationTest extends AbstractEntityIntegrationTest {
		
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		//Count number of Entities
		Query queryCountAccounts = em_bc.createQuery ("SELECT count(x) FROM Account x");
		Number countAccounts = (Number) queryCountAccounts.getSingleResult ();
		assertTrue(!countAccounts.equals(0));
		
		//Retrieve Entities
		Query queryFindAllAccount = em_bc.createQuery("SELECT g FROM Account g");
		List<Account> accounts = queryFindAllAccount.getResultList();
		assertTrue(countAccounts.intValue() == accounts.size());
		
		// Creates Entity
		Account account = new Account();
		account.setBranchType(BranchType.CUSTOMER);
		account.setName("name");
		account.setShortName("shortName");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(account);
		tx_bc.commit();
		
		assertNotNull("Account ID should not be null", account.getId());
		
		//Retreive Entity
		account = em_bc.find(Account.class, account.getId());
		assertTrue(account.getBranchType().equals(BranchType.CUSTOMER));
		assertTrue(account.getName().equals("name"));
		assertTrue(account.getShortName().equals("shortName"));
		
		//Update Entity	
		account.setName("updateName");
		tx_bc.begin();
		em_bc.persist(account);	
		tx_bc.commit();
		
		account = em_bc.find(Account.class, account.getId());
		assertTrue(account.getBranchType().equals(BranchType.CUSTOMER));
		assertTrue(account.getName().equals("updateName"));
		assertTrue(account.getShortName().equals("shortName"));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(account);
		tx_bc.commit();
		
		Query queryFindAllAccounts = em_bc.createQuery("SELECT u FROM Account u");
		accounts = queryFindAllAccounts.getResultList();
		assertTrue(accounts.size() == countAccounts.intValue());			
	}
}
