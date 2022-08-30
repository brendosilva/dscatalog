package com.bootcamp.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.repositories.ProductRepository;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceTestIT {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductRepository productRepository;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 5000L;
		countTotalProducts = 25L;
	}
	
	@Test
	@DisplayName(value = "Delete should delete resource when id exists")
	public void deleteShouldDeleteResourceWhenIdExists() {
		productService.delete(existingId);
		Assertions.assertEquals(countTotalProducts -1, productRepository.count());
	}
	
	@Test
	@DisplayName(value = "Delete Should Throw ResourceNotFoundException when id does not exists")
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			productService.delete(nonExistingId);
		});
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0size10() {
		PageRequest pageRequest = PageRequest.of(0,10);
		Page<ProductDTO> result = productService.findAllPaged(0L, "",pageRequest);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(0, result.getNumber());
		Assertions.assertEquals(10, result.getSize());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {
		PageRequest pageRequest = PageRequest.of(50,10);
			
		Page<ProductDTO> result = productService.findAllPaged(0L, "",pageRequest);
		
		Assertions.assertTrue(result.isEmpty());
		
	}
	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenSortByName() {
		PageRequest pageRequest = PageRequest.of(0,10, Sort.by("name"));
			
		Page<ProductDTO> result = productService.findAllPaged(0L, "",pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
		
	}
	
	
}
