package com.modeln.build.entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractEntityTest {
	protected static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaTestPU");
	protected EntityManager em;
	protected EntityTransaction tx;
			
	@Before
	public void initEntityManager() throws Exception {
		em = emf.createEntityManager();
		tx = em.getTransaction();
	}
	
	@After
	public void closeEntityManager() throws Exception {
		if (em != null) em.close();
	}
}
