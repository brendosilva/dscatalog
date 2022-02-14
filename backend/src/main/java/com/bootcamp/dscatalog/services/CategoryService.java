package com.bootcamp.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.DTO.CategoryDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.repositories.CategoryRepository;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;


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
		Category categoryEntitie = obj.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		return new CategoryDTO(categoryEntitie);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO categorydto) {
		Category entity = new Category();
		entity.setName(categorydto.getName());
		entity = categoryRepository.save(entity);
		
		
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			Category entity = categoryRepository.getOne(id);
			entity.setName(dto.getName());
			entity = categoryRepository.save(entity);
			return new CategoryDTO(entity);
			
		}catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found "+id);
		}
	}
	
}
