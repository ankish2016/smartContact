 package com.jpa.test.Entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class User 
{
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	private int id;
	@NotBlank(message="Name field is required!!")
	@Size(min=4,max=15,message="min 4 and max 10 character are allow!!")
	private String name;
	@Column(unique = true)
	//@Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\\\.[a-zA-Z0-9-]+)*$",message="invalid email")
	private String email;
	
	
	//@NotBlank(message="password field cannot be blank")
	//@Size(min=4,max=12,message="min 4 and max 12 character are allow!!")
	@Pattern(regexp="^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",message="inavalid Password user Format Pqrs@12")
	private String password;
	private String roll;
	private boolean enable;
	private String imageurl;
	@Column(length=5000)
	private String about;	
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,mappedBy ="user",orphanRemoval =true)
	private List<Contact> contacts=new ArrayList<Contact>();
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRoll() {
		return roll;
	}
	public void setRoll(String roll) {
		this.roll = roll;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getImageurl() {
		return imageurl;
	}
	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	public User(int id, String name, String email, String password, String roll, boolean enable, String imageurl,
			String about, List<Contact> contacts) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.roll = roll;
		this.enable = enable;
		this.imageurl = imageurl;
		this.about = about;
		this.contacts = contacts;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", roll=" + roll
				+ ", enable=" + enable + ", imageurl=" + imageurl + ", about=" + about + ", contacts=" + contacts + "]";
	}
	 

}
