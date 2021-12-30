package com.jpa.test.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jpa.test.Entity.User;
import com.jpa.test.dao.UserRepository;
import com.jpa.test.helper.Meesages;
import com.jpa.test.service.EmailService;

@Controller
public class ForgotController 
{ 
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	Random random = new Random(1000);
	
	@RequestMapping("/forget")
	public String forgotemailForm()
	{
		
		return"forgotemailform";
	}
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email")String email,HttpSession session)
	{
		System.out.println("email : "+email);
		//generating 4 degit otp
		
		int otp= random.nextInt(9999);
		System.out.println("otp : "+otp);
		//write code for send otp on your email...
		String subj="OTP FROM SCM";
		String message=""
				+ "<div style='border:1px solid #e2e2e2;padding:20px'>"
				+ "<h1>"
				+ "OTP is "
				+ "<b>"+otp
				+ "</b>"
				+ "</h1> "
				
				+ "</div>";
				
		String to=email;
		boolean flag= this.emailService.sendEmail(subj,message,to);
		if(flag)
		{
			session.setAttribute("otp", otp);
			session.setAttribute("email",email);
			return"verify_otp";
		}else
		{
	 		session.setAttribute("name", "check your mail id....");

	 		return"forgotemailform";
		}
		
	}
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyotp(@RequestParam("otp") int otp,HttpSession session)
	{
		int myotp=(int) session.getAttribute("otp");
		String email=(String)session.getAttribute("email");
		if(myotp==otp)
		{
			User user=this.userRepository.getUserByUserName(email);
			if(user==null)
			{

		 		session.setAttribute("name", "user doesnot exist with email....");

		 		return"forgotemailform";
			}
			else
			{
				//send change password form
			}
			
			return "password_change_form";
		}
		else
		{
			
			session.setAttribute("name", "you have entered wrong otp !!");
			return"verify_otp";
		}
		
	}
     //change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword")String newpassword,HttpSession session)
	{
		String email=(String)session.getAttribute("email");
	    User user= this.userRepository.getUserByUserName(email);
	    user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
	    this.userRepository.save(user);
	    return "redirect:/signin?change=password change succesfully.....";
	}
	
}
