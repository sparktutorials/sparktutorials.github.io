/**
 * 
 */
package com.smatt.cc.auth;

import java.util.HashMap;
import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSessionSHA256;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Seun Matt
 * Date 23 Mar 2016
 * Year 2016
 * (c) SMATT Corporation
 */
public class Authenticate {

	/**
	 * This class will execute the step two of the server authentication
	 * it will accept the 
	 * @param A from the client
	 * @param M1 from the client
	 * it will return M2 which is a token generated
	 * after successful authentication of the client
	 */
	
	private String A, M1;
	
	private String M2 = null;
	
	public boolean authenticated = false;
	
        static Logger logger = LoggerFactory.getLogger(Authenticate.class);
	
	
	public Authenticate() { }
	
	public Authenticate(String M1A) {
//		the concatenated string will be splice
//		the format is M1:A
		
		String [] m1a = M1A.split(":");
		M1 = m1a[0];
		A = m1a[1];
		
		logger.info("Client Credentials Sent to Authenticate = \n: M1 = " + M1 + " \nA = " + A);
		
		//now a call to getM2 will be made that will authicate the user
		
	}
	
	
	
        private boolean authenticate(SRP6JavascriptServerSessionSHA256 server) {
		
	    try {
		this.M2 = server.step2(A, M1);
		logger.info("M2 Generated in Authenticate = " + M2);
		return true;
		
            } catch (Exception e) {
		e.printStackTrace();
		return false;
	    }
        }

	
	public String getM2(SRP6JavascriptServerSessionSHA256 server) {
		
		if(authenticate(server)) {
                	authenticated = true;
			return M2;	
		}
		
	  return null;
	}

	
	public boolean isAuthenticated() {
		return authenticated;
	}

	
	
	
	
	
}
