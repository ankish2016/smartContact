package com.jpa.test.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jpa.test.Entity.Contact;
import com.jpa.test.Entity.User;
import com.jpa.test.dao.ContactRepository;
import com.jpa.test.dao.UserRepository;
import com.jpa.test.helper.Meesages;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void CommonData(Model m, Principal principal) {
		String name = principal.getName();
		System.out.println("username : " + name);
		User user = userRepository.getUserByUserName(name);
		System.out.println(user);
		m.addAttribute("user", user);
	}

	// homedashboard
	@RequestMapping("/index")
	public String DashBoard(Model m, Principal principal) {
		String name = principal.getName();
		m.addAttribute("title", name + "-dashboard");
		return "/normal/dashboard";
	}

	@GetMapping("/addcontact")
	public String openaddContactForm(Model m) {
		m.addAttribute("title", "Add Contact");

		m.addAttribute("cat", new Contact());
		return "normal/addcontactform";
	}

	@PostMapping("/contact-form")
	public String processContact(@ModelAttribute("contact") Contact conatct,
			@RequestParam("image") MultipartFile file, Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			if (file.isEmpty()) {
				System.out.println("image is empty");
				conatct.setImage("contact1.png");
			} else {
				conatct.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is uploaded");
			}

			conatct.setUser(user);
			user.getContacts().add(conatct);

			this.userRepository.save(user);
			System.out.println(conatct);
			System.out.println("Added data to database");

			session.setAttribute("name", new Meesages("your contact is Added !! add More..", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("name", new Meesages("Something went wrong!! Try Again...", "danger"));

		}
		return "normal/addcontactform";
	}
	//showContact
	//per page=5[n] 
	//curent page=0 page  
	@GetMapping("/show-contact/{page}")
	public String showContacts(@PathVariable("page")Integer page,Model m,Principal principal)
	{
		m.addAttribute("title","Contacts");
		String userName = principal.getName();
		User user= this.userRepository.getUserByUserName(userName);
		//contactPage-page
		//contactperpage-5
		Pageable pageable = PageRequest.of(page,5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contact",contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		return	"normal/viewContacts";		
	}
	
	//show particular contact detail
	
	@GetMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid")Integer cid,Model m,Principal principal)
	{
		System.out.println(cid);
		Optional<Contact> contactoptional= this.contactRepository.findById(cid);
		Contact contact = contactoptional.get();
		String userName=principal.getName();
		User user= this.userRepository.getUserByUserName(userName);
	
		if(user.getId()==contact.getUser().getId())
		{
			m.addAttribute("contact",contact);
			m.addAttribute("title",contact.getName());
		}
		return "normal/contact_detail";
	}
	
	//delete contact handler
	@GetMapping("/delete/{cid}")
	public String delteContact(@PathVariable("cid")Integer cid,Principal principal,HttpSession session)
	{
		Optional<Contact> optionalcontact = this.contactRepository.findById(cid);
		Contact contact = optionalcontact.get();
		//contact.setUser(null);
		//check assigment contact bug
		//image delete
		User user= this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		session.setAttribute("name", new Meesages("your contact is deleted!!..", "success"));
		return "redirect:/user/show-contact/0";
	}
	
	//update form
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid")Integer cid,Model m)
	{
		m.addAttribute("title","update contact");
		
		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		
		return "normal/update_form";
	}
	
	//update contact handler
	
	@PostMapping("/update-form")
	public String updateHandler(@ModelAttribute("contact") Contact contact, BindingResult result1,
			@RequestParam("image") MultipartFile file, Principal principal, HttpSession session,Model m)
	{
		try
		{
			Contact oldContactImage= this.contactRepository.findById(contact.getCid()).get();
			if(!file.isEmpty())
			{
			      //img
				//delete
				File delteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(delteFile,oldContactImage.getImage());
				file1.delete();
				//update new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			       contact.setImage(file.getOriginalFilename());
			
			}
			else
			{
				contact.setImage(oldContactImage.getImage());
			}
	         User user= this.userRepository.getUserByUserName(principal.getName());
			     contact.setUser(user);
	         this.contactRepository.save(contact);
	 		session.setAttribute("name", new Meesages("your contact is Updated!!..", "success"));

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("contact " +contact.getName());
		return "redirect:/user/"+contact.getCid()+"/contact/"; 
	}
	
   //your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model m)
	{
		m.addAttribute("title","Profile");
		return "normal/profile";
	}
	
	//open setting handler
	@GetMapping("/setting")
	public String openSetting()
	{
      return "normal/settings";
	}
	
	@PostMapping("/change-password")
	public String ChangePasword(@RequestParam("oldpassword")String oldpassword,@RequestParam("newpassword")String newpassword,Principal principal,HttpSession session)
	{
		System.out.println("old password"+oldpassword);
	    System.out.println("new password"+newpassword);  	
	    String username = principal.getName();
	    User currentuser= this.userRepository.getUserByUserName(username);
	    System.out.println(currentuser.getPassword());
	    if(this.bCryptPasswordEncoder.matches(oldpassword,currentuser.getPassword()))
	    {
	    	//change password
	    	currentuser.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
	    	this.userRepository.save(currentuser);
	 		session.setAttribute("name", new Meesages("your Password change  Sucessfully!!..", "success"));

	    }
	    else
	    {
	    	//error  
	 		session.setAttribute("name", new Meesages("Please enter correct oldPassword !!..", "danger"));
	 		return"redirect:/user/setting";
	    }
	    
	    
	return"redirect:/user/index";	
	}
	
}