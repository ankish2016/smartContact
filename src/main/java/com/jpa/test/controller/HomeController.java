package com.jpa.test.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jpa.test.Entity.User;
import com.jpa.test.dao.UserRepository;
import com.jpa.test.helper.Meesages;

@Controller
public class HomeController 
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping("/")
	public String Home(Model m) {
		m.addAttribute("title", "Smart - Contact Manger");
		return "home";
	}

	@RequestMapping("/about")
	public String About(Model m) {
		m.addAttribute("title", "About Smart - Contact Manger");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("title","Register Smart - Contact Manger");
		m.addAttribute("user",new User());
		return "signup";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user")User user,BindingResult result1,@RequestParam(value="agreement",defaultValue = "false")boolean agreement,Model m,HttpSession session)
	{
		try 
		{
			if(result1.hasErrors())
			{
				System.out.println("ERROR"+result1.toString());
			   m.addAttribute("user",user);
			   return "signup";
			}

			if(!agreement)
			{
			System.out.println("you not accept the terms and conditions");
			throw new Exception("you have agreed the term and condition");
			}
			user.setRoll("ROLE_USER");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setEnable(true);
			user.setImageurl("default.png"); 
			System.out.println("agrement : "+agreement);
			System.out.println("user : "+user);
			User result = this.userRepository.save(user);
			m.addAttribute("user",new User());
			session.setAttribute("message",new Meesages("Sucessfully Registered !! ","alert-success"));


			return "signup";
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			m.addAttribute("user",user);
			session.setAttribute("message",new Meesages("soory something went wrong !!  ","alert-danger"));
		}
		
		return"signup";
	}
	
	@GetMapping("/signin")
	public String customLogin(Model m)
	{
		m.addAttribute("title", "Login Smart - Contact Manger");
		return "login";
	}
	
	
}
