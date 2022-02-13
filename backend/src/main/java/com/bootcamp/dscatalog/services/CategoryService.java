package com.bootcamp.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.DTO.CategoryDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.repositories.CategoryRepository;
import com.bootcamp.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly=true)
	public List<CategoryDTO> findAll(){
		List<Category> listCategory = categoryRepository.findAll();
		List<CategoryDTO> listCategoryDto = listCategory.stream()
				.map(category -> new CategoryDTO(category))
				.collect(Collectors.toList());
		
		
		return listCategoryDto;
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = categoryRepository.findById(id);
		Category categoryEntitie = obj.orElseThrow(() -> new EntityNotFoundException("Category not found"));
		return new CategoryDTO(categoryEntitie);
	}
	
	
}
