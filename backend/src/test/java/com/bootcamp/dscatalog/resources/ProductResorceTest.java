package com.bootcamp.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.services.ProductService;
import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;
import com.bootcamp.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResorceTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private ProductService productService;
	
	private ProductDTO productoDTO;
	private PageImpl<ProductDTO> page;
	private Long existsId;
	private Long notExistsId;
	private Long dependentId;
	
	@BeforeEach
	void setUp() throws Exception {
		existsId = 1L;
		notExistsId = 1000L;
		dependentId = 2L;
		productoDTO = Factory.createProductDto();
		page = new PageImpl<>(List.of(productoDTO));
		
		when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		when(productService.findById(existsId)).thenReturn(productoDTO);
		when(productService.findById(notExistsId)).thenThrow(ResourceNotFoundException.class);
		
		when(productService.update(eq(existsId), any())).thenReturn(productoDTO);
		when(productService.update(eq(notExistsId), any())).thenThrow(ResourceNotFoundException.class);
		
		when(productService.insert(any())).thenReturn(productoDTO);
		
		doNothing().when(productService).delete(existsId);
		doThrow(ResourceNotFoundException.class).when(productService).delete(notExistsId);
		doThrow(DatabaseException.class).when(productService).delete(dependentId);
	}
	
	@Test
	@DisplayName(value = "Insert should return created")
	public void insertShouldReturnCreated() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productoDTO);
		ResultActions result = mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());		
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	@DisplayName(value = "Delete should return no cotent when id exists")
	public void deleteShouldReturnNoCotentWhenIdExists() throws Exception {
		mockMvc.perform(delete("/products/{id}", existsId)).andExpect(status().isNoContent());
	}
	@Test
	@DisplayName(value = "Delete should return Not Found when id does not exists")
	public void deleteShouldReturnNotFoundWhenIdNotExists() throws Exception {
		mockMvc.perform(delete("/products/{id}", notExistsId)).andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Update should Return ProductDTO when Id exists")
	public void updateShouldReturnProductDTOWhenIdexists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productoDTO);
		ResultActions result = mockMvc.perform(put("/products/{id}", existsId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	@DisplayName(value = "Update should return Not Found Exception When id does not exists")
	public void updateShouldReturnNotFoundExceptionWhenIdDoesNotExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productoDTO);
		ResultActions result = mockMvc.perform(put("/products/{id}", notExistsId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	
	@Test
	@DisplayName("findAll should return page")
	public void findAllShouldReturnPage() throws Exception {
		mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
	}
	
	
	@Test
	@DisplayName(value = "findById should return product when id exits")
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", existsId).accept(MediaType.APPLICATION_JSON));
		
		
			result.andExpect(status().isOk());
			result.andExpect(jsonPath("$.id").exists());
			result.andExpect(jsonPath("$.name").exists());
			result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	@DisplayName(value = "findById should return ResorceNotFountException when id does not exits")
	public void findByIdShouldReturnResorceNotFountExceptionWhenIdNotExists() throws Exception{
		ResultActions result = mockMvc.perform(get("/products/{id}", notExistsId).accept(MediaType.APPLICATION_JSON));
		
		
		result.andExpect(status().isNotFound());
	}
	
	
}
