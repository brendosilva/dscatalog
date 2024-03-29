package com.bootcamp.dscatalog.services;

import com.bootcamp.dscatalog.dto.CategoryDTO;
import com.bootcamp.dscatalog.dto.ProductDTO;
import com.bootcamp.dscatalog.entities.Category;
import com.bootcamp.dscatalog.entities.Product;
import com.bootcamp.dscatalog.repositories.CategoryRepository;
import com.bootcamp.dscatalog.repositories.ProductRepository;
import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Long categoryId, String name, Pageable pageable) {
        List<Category> categories = (categoryId == 0) ? null : List.of(categoryRepository.getOne(categoryId));
        Page<Product> list = productRepository.search(categories, name, pageable);
        productRepository.findProductsWithCategories(list.getContent());
        return list.map(x -> new ProductDTO(x, x.getCategories()));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = productRepository.findById(id);
        Product entity = obj.orElseThrow(()-> new ResourceNotFoundException("Product not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO productdto) {

        Product entity = new Product();
        copyDtoToEntity(productdto, entity);

        entity = productRepository.save(entity);


        return new ProductDTO(entity);
    }



    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = productRepository.getOne(id);
            copyDtoToEntity(dto, entity);
            entity = productRepository.save(entity);
            return new ProductDTO(entity);

        }catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found "+id);
        }
    }

    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch(EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("id not found "+id);
        } catch(DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }

    }

    private void copyDtoToEntity(ProductDTO productdto, Product entity) {

        entity.setName(productdto.getName());
        entity.setDescription(productdto.getDescription());
        entity.setDate(productdto.getDate());
        entity.setImgUrl(productdto.getImgUrl());
        entity.setPrice(productdto.getPrice());

        entity.getCategories().clear();
        for(CategoryDTO cat : productdto.getCategories() ) {
            Category category = categoryRepository.getOne(cat.getID());
            entity.getCategories().add(category);
        }

    }
}
