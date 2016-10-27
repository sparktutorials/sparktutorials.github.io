/**
 * 
 */
package com.smatt.cc.contact;

import com.smatt.cc.db.DatabaseHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smatt.cc.util.Message;
import java.util.HashMap;
import java.util.List;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smatt.cc.util.Path;
import java.util.Date;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;

/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 */
public class ContactController {

	/**
	 * Constructor 
	 * cloud contacts
	 */
	
	static DatabaseHelper dbHelper = new DatabaseHelper();
	static Datastore ds;
	static Logger logger = LoggerFactory.getLogger(ContactController.class);

	public ContactController() { }
	
	
	
	public static ModelAndView serveDashboard(Request req, Response res) {
		
		//get userId from req must not be null
                
		String userId = req.session(false).attribute(Path.Web.ATTR_USER_ID).toString();
                String username = req.session(false).attribute(Path.Web.ATTR_USER_NAME).toString();
                String email = req.session(false).attribute(Path.Web.ATTR_EMAIL).toString();
                
//                String userId = "1234fff";
//                String username = "Smatt";
                
              if(userId != null && !userId.isEmpty() && username != null && !username.isEmpty()) {
                    
		//get all contacts and send it back
		HashMap<String, Object> model = new HashMap<>();
		model.putAll(prepareData(userId));
		model.put("username", username);
                model.put("email", (email == null) ? "" : email);
		
		ModelAndView mv = new ModelAndView(model, Path.Templates.DASHBOARD);
                
//                logger.info("userId = " + userId + "\nusername = " + username + "\nmodel returned = " + mv.getModel().toString());
                
		return mv;
                
              } else {
                    logger.warn("userID and username not found in Session"); //session expired
                    res.redirect(Path.Web.GET_LOGIN_PAGE);
                    return null;
             }
	}
	
	
	public static int handleNewContact(Request req, Response res) {
	
           String data = Jsoup.parse(req.body()).text()
                    .replaceAll(Path.Web.OK_PATTERN, "");
            
            try {
                
            logger.info("Parsed and Escaped data passed to new Contact = \n" + req.body());
       
            ObjectMapper objectMapper = new ObjectMapper();
            Contact c = objectMapper.readValue(data, Contact.class);
            logger.info("contact after conversion for new = \n" + c.toString());
            
            String userIdNew = req.session(false).attribute(Path.Web.ATTR_USER_ID);
            
                if(userIdNew != null && !userIdNew.isEmpty()) {
                     c.setUserId(userIdNew);
                     ds = dbHelper.getDataStore();
	             ds.save(c);
                     res.status(200);
                     
                 } else {
                     //you managed not to login and get here
                     logger.info("No user id found for the update operation");
                     res.status(500);
                }
            
            
            } catch(Exception e) {
                e.printStackTrace();
//                logger.info("error parsing data from handleNewContact \n" + e.toString());
                res.status(500);
            }
            
            return res.status();
	}
	
	
	public static int handleUpdateContact(Request req, Response res) {
           //parse our input to remove malicious html/javascript stuff
           //and then I used patterns to specify the characters we want
           //we are using the Jsoup library here.
            
           try {
             
            ObjectMapper objectMapper = new ObjectMapper();
            
            logger.info("raw body in handleUpdate = \n" + req.body());
            
            String data = Jsoup.parse(req.body()).text()
                    .replaceAll(Path.Web.OK_PATTERN, "");
            
            logger.info("Jsoup parsed and escaped data = \n" + data);
            
                       
            Contact c = objectMapper.readValue(data, Contact.class);
            
            String userIdNew = req.session(false).attribute(Path.Web.ATTR_USER_ID);
            String id = req.params("id");
            
                if(userIdNew != null && !userIdNew.isEmpty() && id != null && !id.isEmpty()) {
                    ObjectId objectId = new ObjectId(id); 
                    c.setUserId(userIdNew);
                     c.setId(objectId.toString());
                     c.setUpdatedAt(new Date());
                     ds = dbHelper.getDataStore();
	             ds.save(c);
                     res.status(200);
                     logger.info("updated contact object after mapping and setting id = \n" + c.toString());
           
                } else {
                     //you managed not to login and get here
                     logger.info("No user id found for the update operation");
                     res.status(500);
                 }
            
            } catch(Exception e) {
                logger.info("error parsing data from handleUpdateContact \n");
                e.printStackTrace();
                res.status(500);
            }
            
            return res.status();
	}
	
	
	public static Object handleDeleteContact(Request req, Response res) {
                
                String id = req.params("id"); //id of contact to be deleted
                String userId = req.session(false).attribute(Path.Web.ATTR_USER_ID); 
                
            if(id != null && !id.isEmpty() && userId != null && !userId.isEmpty()) { 
               
                Datastore ds = dbHelper.getDataStore();
               
                Contact c = ds.createQuery(Contact.class)
                            .field("id").equal(new ObjectId(id))
                            .field("userId").equal(userId)
                            .get();
                 
                if(c != null) {
                  
                    logger.info("Contact not null " + c.toString());
                    ds.delete(c);
                    res.status(200); //this is very important so that ajax can have an idea what's going on
                    return new Message(Path.Reply.CONTACT_NOT_FOUND, Path.Reply.CONTACT_NOT_FOUND_MSG);
                    
                } else { 
                    logger.info("CONTACT IS NULL, NOT FOUND!");
                    res.status(500); //can't be overemphasized, c'est vrai important!
                    return new Message(Path.Reply.CONTACT_NOT_FOUND, Path.Reply.CONTACT_NOT_FOUND_MSG);
                }
                
            } else {
                 logger.info("INVALID ID/USERID!");
                 res.status(500);
                 return new Message(Path.Reply.OK, Path.Reply.OK_MSG);
            }
	}
	

	
	
	public static HashMap<String, Object> prepareData(String userId) {

		//		fetch all the contacts in the database and return it
		
		ds = dbHelper.getDataStore();
		
		List<Contact> list = ds.createQuery(Contact.class).field("userId")
				.equal(userId)
				.order("first_name")
				.asList();
		
		StringBuilder tableRows = new StringBuilder();
		StringBuilder jsonData = new StringBuilder();
		 
                if(list != null && list.size() > 0) {
                      
                    logger.info("found " + list.size() + " contacts");
                            
                    for(int i = 0; i < list.size(); i++) {
                        Contact c = list.get(i);
			tableRows.append(c.toTableRow(i));
			jsonData.append("\"" + i + "\":" + c.toJson()).append(",");
//                         logger.info("jsonData " + i + " = " + c.toJson());
                    }
		
                    jsonData.deleteCharAt(jsonData.lastIndexOf(",")); //removes the last comma
                    jsonData.insert(0, "{" ).append("}"); //name the array

//                    logger.info("jsonData from prepareData = \n" + jsonData);
                    
                } else {
                    logger.warn("No user data found for userId " + userId);
                    jsonData.append("{}");
                }
                
		HashMap<String, Object> map = new HashMap<>();
		map.put("tableData", tableRows.toString());
		map.put("jsonData", jsonData.toString());
		
		return map;
	}
	
}
 