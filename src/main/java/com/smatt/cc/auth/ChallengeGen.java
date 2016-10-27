/**
 * 
 */
package com.smatt.cc.auth;

import java.util.HashMap;

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSessionSHA256;
import com.google.gson.Gson;
import com.smatt.cc.auth.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Seun Matt
 * Date 23 Mar 2016
 * Year 2016
 * (c) SMATT Corporation
 */
public class ChallengeGen {

	/**
	 * This class is for authentication
	 * it will accept the username and use it to
	 * compute challenge B
	 * it will then send the challenge to the client alongside salt 's' stored for the user 
	 * to the client, it will have to persist the challenge object
	 * for the session
	 */
	
	private String email, salt, B, verifier;
	
	private HashMap<String, String> resp = new HashMap<>();
	
        Logger logger = LoggerFactory.getLogger(ChallengeGen.class);
        
	public ChallengeGen() {	}
	
	public ChallengeGen(String email) {
		this.email = email;		
	}

	
	public HashMap<String,String> getChallenge(SRP6JavascriptServerSessionSHA256 server) {
		
//		fetch the user obj from the db
		
		User user = UserController.getUserByEmail(email);
		
		if(user != null) {
			verifier = user.getVerifier();
			salt = user.getSalt();
                        logger.info("Salt = " + salt + "\nVerifier = " + verifier);

                        String B = server.step1(email, salt, verifier);
                        
                        logger.info("server  challenge B = " + B);
                        
                        if(B != null && !B.isEmpty()) {
                            
                            HashMap<String, String> resp = new HashMap<>();
                            resp.put("salt", salt);
                            resp.put("B",   B);
                            logger.info("Challenge: = \n" + resp.toString());
                            return resp;
                            
                        } else { 
                            //invalid server challenge
                            return null;
                        }
		}
		
		return null;
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

	public String getB() {
		return B;
	}

	public void setB(String b) {
		B = b;
	}

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public HashMap<String, String> getResp() {
		return resp;
	}

	public void setResp(HashMap<String, String> resp) {
		this.resp = resp;
	}

	
	
	
	
	
	
	
}
