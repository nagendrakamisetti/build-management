package com.modeln.build.entity.customer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import com.modeln.build.entity.AbstractEntityIntegrationTest;
import com.modeln.build.entity.customer.Product;

public class ProductIntegrationTest extends AbstractEntityIntegrationTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleCRUDOperationOnLegacyTable() throws Exception {		
		//Count number of Entities
		Query queryCountProducts = em_bc.createQuery ("SELECT count(x) FROM Product x");
		Number countProducts = (Number) queryCountProducts.getSingleResult ();
		assertTrue(!countProducts.equals(0));
		
		//Retrieve Entities
		Query queryCountProduct = em_bc.createQuery("SELECT g FROM Product g");
		List<Product> products = queryCountProduct.getResultList();
		assertTrue(countProducts.intValue() == products.size());
		
		// Creates Entity
		Product product = new Product();
		product.setName("name");
		product.setDescription("description");
		
		// Persists to the database
		tx_bc.begin();
		em_bc.persist(product);
		tx_bc.commit();
		
		assertNotNull("Product ID should not be null", product.getId());
		
		//Retreive Entity
		product = em_bc.find(Product.class, product.getId());
		assertTrue(product.getName().equals("name"));
		assertTrue(product.getDescription().equals("description"));
		
		//Update Entity	
		product.setName("updateName");
		tx_bc.begin();
		em_bc.persist(product);	
		tx_bc.commit();
		
		product = em_bc.find(Product.class, product.getId());
		assertTrue(product.getName().equals("updateName"));
		assertTrue(product.getDescription().equals("description"));
		
		//Delete Entity
		tx_bc.begin();
		em_bc.remove(product);
		tx_bc.commit();
		
		Query queryFindAllProducts = em_bc.createQuery("SELECT u FROM Product u");
		products = queryFindAllProducts.getResultList();
		assertTrue(products.size() == countProducts.intValue());			
	}

}
