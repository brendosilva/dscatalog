package com.bootcamp.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.bootcamp.dscatalog.dto.RoleDTO;
import com.bootcamp.dscatalog.dto.UserDTO;
import com.bootcamp.dscatalog.entities.Role;
import com.bootcamp.dscatalog.entities.User;
import com.bootcamp.dscatalog.repositories.RoleRepository;
import com.bootcamp.dscatalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.bootcamp.dscatalog.services.exceptions.DatabaseException;
import com.bootcamp.dscatalog.services.exceptions.ResourceNotFoundException;

public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	

	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = userRepository.findAll(pageable);
		return list.map(x -> new UserDTO(x));
	}
	
	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = userRepository.findById(id);
		User entity = obj.orElseThrow(()-> new ResourceNotFoundException("User not found"));
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO insert(UserDTO userDTO) {
		
		User entity = new User();
		copyDtoToEntity(userDTO, entity);
		
		entity = userRepository.save(entity);
		
		
		return new UserDTO(entity);
	}

	

	@Transactional
	public UserDTO update(Long id, UserDTO dto) {
		try {
			User entity = userRepository.getOne(id);
			copyDtoToEntity(dto, entity);	
			entity = userRepository.save(entity);
			return new UserDTO(entity);
			
		}catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found "+id);
		}
	}

	public void delete(Long id) {
		try {
			userRepository.deleteById(id);
		} catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("id not found "+id);
		} catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
		
	}
	
	private void copyDtoToEntity(UserDTO userDTO, User entity) {
		entity.setFirstName(userDTO.getFirstName());
		entity.setLastName(userDTO.getLastName());
		entity.setEmail(userDTO.getEmail());

		entity.getRoles().clear();
		for (RoleDTO dto : userDTO.getRoles()) {
			Role role = roleRepository.getOne(dto.getId());
			entity.getRoles().add(role);
		}
	}
}
