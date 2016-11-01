/**
 * 
 */
package com.smatt.cc.util;

/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 * 
 * This is a class that will contain static strings
 * for our paths
 */
public class Path {


	
	
	
	
	/**
	 * Constructor 
	 * cloud contacts
	 */
	public Path() {
	}
	
	public static class Web {
                
//            this subclass holds the Web related static properties like routes
//            and others.
            
		public static String HOME = "/";
		
//		log in routes
		public static String GET_LOGIN_PAGE = "/login";
                public static String DO_LOGIN = "/login";
                public static String DO_AUTH = "/auth";
		public static String LOGOUT = "/logout";
		
//		routes for performing crud on contact
		public static String DASHBOARD = "/contacts/";
		public static String DELETE = "/contact/:id"; //uses delete http method
		public static String UPDATE = "/contact/:id"; //uses put http method; data is contained in req body
		public static String NEW = "/contact/"; //uses post http method; data is contained in req body
		
//		routes for managing users / authentication
		public static String GET_SIGN_UP = "/signup"; //uses get method
                public static String DO_SIGN_UP = "/signup"; //uses post method
		public static String NEW_USER = "/user"; //uses post method
		public static String UPDATE_PWD = "/s/user/:id"; //uses put method
                
                public static String ATTR_USER_ID = "userId";
                public static String ATTR_USER_NAME = "username";
                public static String ATTR_EMAIL = "email";
		
                public static String OK_PATTERN = "[^a-zA-Z0-9:\",{}@_.\\- ]"; 
                public static int SESSION_TIMEOUT = 60 * 30; //30 mins
                public static String JSON_TYPE = "application/json";
                public static String AUTH_STATUS = "AUTH_STATUS";
	}
	
	
	public static class Templates {

		public static String INDEX = "index.hbs"; 
		public static String DASHBOARD = "main.hbs";
		public static String LOGIN = "signin.hbs";
		public static String SIGN_UP = "signup.hbs";
		
	}
	
	
	public static class Database {
		
		public static String LOCAL_DBNAME = "contacts_db";
                public static String HOST = "127.0.0.1";
		public static int PORT = 27017;
                
                //the db uri is from the heroku platform
                public static String HEROKU_DB_URI = "mongodb://heroku_n35m7bx6:vf99qjg9otp744biaqjtepvurd@ds011725.mlab.com:11725/heroku_n35m7bx6";
                public static String HEROKU_DB_NAME = "heroku_n35m7bx6"; //this is the last part of the HEROKU_DB_URI
	
	}
        
        
        public static class Reply {
            
            public static int OK = 200;
            public static String OK_MSG = "Hurray! Operation Successful";
            public static int CONTACT_NOT_FOUND = 601;
            public static String CONTACT_NOT_FOUND_MSG = "Ooops! The resource is not found on the server";
        }

}
