/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatt.cc.auth;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 *
 * @author smatt
 */
@Entity("user_tbl")
public class User {
    
    @Id
    private ObjectId id;
    private String username = "";
    @Indexed(options = @IndexOptions(unique = true))
    private String email = "";
    private String salt = "";
    private String verifier = "";
    private String token = "";
    
    
    public User() {
        
    }
    
    public User(String username, String email, String salt, String verifier ) {
        
		this.username = username;
		this.salt = salt;
		this.verifier = verifier;
		this.email = email;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    
    
    
    @Override
    public String toString() {
        return "USER " + getId() + " : " + getUsername() + " : " + getEmail();
    }
    
    
    
    
}
