///**
// * 
// */
//package com.smatt.cc.auth;
//
//import com.mysinno.mail.MailAuth;
//import com.mysinno.mail.SendMail;
//import com.mysinno.utils.AppParams;
//
///**
// * @author Seun Matt
// * Date 10 Apr 2016
// * Year 2016
// * (c) SMATT Corporation
// */
//public class PasswordRecovery {
//
//	/**
//	 * Constructor 
//	 * myledger
//	 */
//	
//	public PasswordRecovery() { 
//		
//	}
//	
//	
//	
//	
//	
//	public static boolean verifyToken(User user) {
//		
//		String token = user.getToken();
//		
//		long expireTime = Long.parseLong(token.split("_")[1]);
//		
//		long currentTime = System.currentTimeMillis();
//		
//		return currentTime < expireTime;
////		if true then the expireTime is greater than the current time and has not expired
////		if false the currentTime is greater than expireTime and token has expired
//	}
//
//	
//	public static int sendRecoveryMail(String email) {
//		
//	    UserManager userMan = new UserManager();
//		
//	    User user = userMan.getEntryByEmail(email);
//		
//		if(user != null) {
//			
//			//build the link
//			long startTime = System.currentTimeMillis();
//			
//			long expireTime = startTime + (1000 * 60 * 60 * 60 * 24); //expires in 24 hours
//			
//			String separator="_";
//			
//			String token = startTime+separator+expireTime;
//			
//			String username = user.getUsername();
//			
//			user.setToken(token);
//			
//			int i = userMan.updateEntry(user);
//			
//			
//			if(i >= 0) {
//				
//			
//			String link = "http://" + AppParams.webaddress + "resetform?username="+username+"&token="+token;
//		
////			send email message			
//			
//			String emailMessage = "YOU REQUESTED FOR A PASSWORD RECOVERY FOR CLOUD LEDGER \n"
//					+ " CLICK ON THE FOLLOWING LINK TO RESET YOUR PASSWORD OR COPY IT INTO YOUR BROWSER'S ADDRESS BAR "
//					+ "\n\n\n  " + link + "  \n\n\n"
//					+ " IF YOU DID NOT REQUEST FOR THIS SERVICE, KINDLY IGNORE THIS MESSAGE"
//					+ "\nTHANKS"
//					+ "\nCLOUD LEDGER TEAM";
//			
//			MailAuth mailAuth = new MailAuth();
//			
//			boolean sent = SendMail.sendMail(mailAuth.getFROM_EMAIL(),
//					email, 
//				   mailAuth.getFROM_EMAIL(),
//				   mailAuth.getFROM_PASSWORD(),
//				   mailAuth.getMAIL_SUBJECT(),
//				   emailMessage);
//					
//			if(sent) { return 1; }
//		   
//		}
//		
//		return -1;
//	} else {
//		
//		return -2; //email doesnt exist
//	}
//}
//	
//}
