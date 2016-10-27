/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatt.cc.auth;

import com.mongodb.DuplicateKeyException;
import com.smatt.cc.db.DatabaseHelper;
import java.util.List;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smatt
 */

public class UserController {
    
   static DatabaseHelper dbHelper = new DatabaseHelper();
   static Datastore ds;
   static Logger logger = LoggerFactory.getLogger(UserController.class);
   
    public UserController() {
        
    }
  
    
    
    public static User getUserByEmail(String email) {
        
        ds = dbHelper.getDataStore();
        List<User> userList = ds.createQuery(User.class)
                    .field("email").equal(email)
                    .limit(1)
                    .asList();
        if(null != userList && userList.size() > 0) {
         return userList.get(0);
        }
        return null;
    }
    
    
    public static int createUser(User user) {
       
        //returns -1 on error
       //return 1 on success
       
       if(user == null) return -1;
       ds = dbHelper.getDataStore();
       try {
        ds.save(user);
        logger.info("New User " + user.toString() + " Created!");
       } catch(DuplicateKeyException e) {
           logger.error("ERROR! user " + user.toString() + " exists already " + e.toString());
           return -2;
       }
       return 1;
   
   } 
    
    
    
    
}
