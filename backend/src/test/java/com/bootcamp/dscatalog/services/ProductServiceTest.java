package com.bootcamp.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.entities.Product;
import com.bootcamp.dscatalog.repositories.CategoryRepository;
import com.bootcamp.dscatalog.repositories.ProductRepository;
import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;
import com.bootcamp.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private Long existingId;
	private Long nonExistsId;
	private Long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistsId = 5000L;	
		dependentId = 4L;
		
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		category = Factory.createCategory();
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
			
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistsId)).thenReturn(Optional.empty());
		
		
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistsId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistsId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.doNothing().when(repository).deleteById(existingId);		
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistsId);		
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	@DisplayName(value = "Update should return ProductDTO when id Exists")
	public void updateShouldReturnProductDTOwhenIdExists() {
		ProductDTO dto = service.update(existingId, Factory.createProductDto());
		Assertions.assertNotNull(dto);
	}
	
	@Test
	@DisplayName(value= "update should return ResourceNotFoundException when id not exists")
	public void updateShouldReturnResourceNotFoundExceptionWhenIdNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistsId, Factory.createProductDto());
		});
	}
	
	@Test
	@DisplayName(value = "FindById Should return ProductDTO when id exists")
	public void findByIdShouldReturnProductDtoWhenIdExists() {		
		ProductDTO product = service.findById(existingId);
		Assertions.assertNotNull(product);
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}
	
	@Test
	@DisplayName(value = "findById should throws ResourceNotFoundException when id not exists ")
	public void findByIdShouldResourceNotFoundExceptionWhenIdNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistsId);
		});
		Mockito.verify(repository, Mockito.times(1)).findById(nonExistsId);
		
	}
	
	@Test
	@DisplayName(value = "Find By Page should return page")
	public void findAllPageShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> page = service.findAllPaged(pageable);
		Assertions.assertNotNull(page);
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
		
	}
	
	@Test
	@DisplayName("Delete should Do Nothing When id existing")
	public void deleteShouldDoNothingWhenIdExisting() {			
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);		
	}
	
	@Test
	@DisplayName(value = "Delete shold throw DatabaseException when dependent id")
	public void deleteShouldThrowDataBaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);		
	}
	
	@Test
	@DisplayName("Delete Should Throw ResourceNotFountException when Id not exists")
	public void deleteShouldThrowResourceNotFountExceptionWhenIdDoesNotExisting() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistsId);
		});		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistsId);
	}	
	
}
