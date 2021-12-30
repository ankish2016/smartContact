package com.jpa.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.jpa.test.Entity.User;
import com.jpa.test.dao.UserRepository;

public class UserDetailServiceImpl implements UserDetailsService
{
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user= this.userRepository.getUserByUserName(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("user not found");
		}
		
		   CustomUserDetails customUserDetails=new CustomUserDetails(user);
		    
		
		return customUserDetails;
	}

}
