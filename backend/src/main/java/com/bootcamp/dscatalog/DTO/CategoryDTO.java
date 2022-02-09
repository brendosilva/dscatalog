package com.bootcamp.dscatalog.DTO;

import java.io.Serializable;

import com.bootcamp.dscatalog.entities.Category;

public class CategoryDTO  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long Id;
	private String name;
	
	public CategoryDTO() {}
	
	public CategoryDTO(Long id, String name) {
		super();
		Id = id;
		this.name = name;
	}
	
	public CategoryDTO(Category categoryEntity) {
		this.Id = categoryEntity.getId();
		this.name = categoryEntity.getName();
	}

	public Long getID() {
		return Id;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}
