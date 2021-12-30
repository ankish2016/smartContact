package com.jpa.test.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpa.test.Entity.Contact;
import com.jpa.test.Entity.User;
import com.jpa.test.dao.ContactRepository;
import com.jpa.test.dao.UserRepository;

@RestController
public class SearchController 
{
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
         
	//search controller
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal)
	{
		System.out.println(query);
		User user=this.userRepository.getUserByUserName(principal.getName());
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query,user);
	    return ResponseEntity.ok(contacts);
	}
	
	
}
