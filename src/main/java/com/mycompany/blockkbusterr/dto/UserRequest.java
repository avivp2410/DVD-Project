package com.mycompany.blockkbusterr.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRequest {
    
    @XmlElement(required = true)
    private String firstName;
    
    @XmlElement(required = true)
    private String lastName;
    
    @XmlElement(required = true)
    private String email;
    
    @XmlElement(required = true)
    private String username;
    
    @XmlElement(required = true)
    private String password;
    
    // Default constructor for JAXB
    public UserRequest() {}
    
    public UserRequest(String firstName, String lastName, String email, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    
    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}