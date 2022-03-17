package com.bootcamp.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.bootcamp.dscatalog.entities.Product;
import com.bootcamp.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	private Long idExisting;
	private Long nonExistingId;
	private Long countTotalProduct;
	
	
	@Autowired
	private ProductRepository productRepository;
	
	@BeforeEach
	void setUp() throws Exception {
		idExisting = 1L;
		nonExistingId = 1500L;
		countTotalProduct = 25L;
	}
	
	@Test
	@DisplayName("Save should persist With Auto increment when id is null")
	public void saveSholdPersisitWithAutoIncrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		
		product.setId(null);
		
		product = productRepository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProduct + 1, product.getId());
	}
	
	
	@Test
	@DisplayName(value = "delete should delete object when id exists")
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		
		productRepository.deleteById(idExisting);
		
		Optional<Product> resultado = productRepository.findById(idExisting);
		
		Assertions.assertFalse(resultado.isPresent());
	}
	
	@Test
	@DisplayName("Delete should throw empty result data access exceptions when id not exists")
	public void deleteShouldThrowEmptyResultDataAccessExceptionsWhenIdNotExist() {		
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {			
			productRepository.deleteById(nonExistingId);			
		});
	}
	
	@Test
	@DisplayName("Find by id Should return optional not empty when exists")
	public void shouldReturnOptionNotEmptyWhenIdExists ( ) {
		Optional<Product> product = productRepository.findById(idExisting);
		Assertions.assertTrue(product.isPresent());
	}
	
	@Test
	@DisplayName("Find by id should return option empty when id does not exists")
	public void findByIdShouldReturnOptionalEmptyWhenIdDoesNotExists( ) {
		Optional<Product> product = productRepository.findById(nonExistingId);
		Assertions.assertTrue(product.isEmpty());
	}
	
}
