package com.bootcamp.dscatalog.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.services.ProductService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {
	
	@Autowired
	public ProductService productService;

	@GetMapping
	public ResponseEntity<Page<ProductDTO>> findAll(
			Pageable pageable,
			@RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
			@RequestParam(value = "name", defaultValue = "") String name
	) {
	
		Page<ProductDTO> list = productService.findAllPaged(categoryId, name.trim(), pageable);
		
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> findOneById(@PathVariable Long id){
		ProductDTO dto = productService.findById(id);
		
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO productDTO){
		
		productDTO = productService.insert(productDTO);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(productDTO.getId())
				.toUri();
		return ResponseEntity.created(uri).body(productDTO);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO categoryDTO) {
		categoryDTO = productService.update(id, categoryDTO);	
		
		return ResponseEntity.ok().body(categoryDTO);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> delete(@Valid @PathVariable Long id) {
		productService.delete(id);	
		
		return ResponseEntity.noContent().build();
	}
	
}
