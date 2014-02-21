package com.modeln.build.entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractEntityIntegrationTest {

	protected static EntityManagerFactory emf_bc = Persistence.createEntityManagerFactory("backwardCompatibilityTestPU");
	protected EntityManager em_bc;
	protected EntityTransaction tx_bc;
			
	@Before
	public void initEntityManager() throws Exception {
		em_bc = emf_bc.createEntityManager();
		tx_bc = em_bc.getTransaction();
	}
	
	@After
	public void closeEntityManager() throws Exception {
		if (em_bc != null) em_bc.close();
	}
}
