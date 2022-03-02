package com.bootcamp.dscatalog.dto;

import java.io.Serializable;

import com.bootcamp.dscatalog.entities.Category;

public class CategoryDTO  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	
	public CategoryDTO() {}
	
	public CategoryDTO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public CategoryDTO(Category categoryEntity) {
		this.id = categoryEntity.getId();
		this.name = categoryEntity.getName();
	}

	public Long getID() {
		return this.id;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CategoryDTO [Id=" + id + ", name=" + name + "]";
	}
	
	
	
	
	
	
}
