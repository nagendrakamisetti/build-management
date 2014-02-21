package com.modeln.build.entity.customer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityTest;
import com.modeln.build.entity.customer.Product;

public class ProductTest extends AbstractEntityTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperations() throws Exception {
		
		// Creates Entity
		Product product = new Product();
		product.setName("name");
		product.setDescription("description");
		
		// Persists to the database
		tx.begin();
		em.persist(product);
		tx.commit();
		
		assertNotNull("Product ID should not be null", product.getId());
		
		//Retreive Entity
		product = em.find(Product.class, product.getId());
		assertTrue(product.getName().equals("name"));
		assertTrue(product.getDescription().equals("description"));
		
		//Update Entity	
		product.setName("updateName");
		tx.begin();
		em.persist(product);	
		tx.commit();
		
		product = em.find(Product.class, product.getId());
		assertTrue(product.getName().equals("updateName"));
		assertTrue(product.getDescription().equals("description"));
		
		//Delete Entity
		tx.begin();
		em.remove(product);
		tx.commit();
		
		Query queryFindAllProducts = em.createQuery("SELECT u FROM Product u");
		List<Product> products = queryFindAllProducts.getResultList();
		assertTrue(products.isEmpty());
		
	}

}
