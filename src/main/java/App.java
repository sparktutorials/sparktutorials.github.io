
/**
 * 
 */

/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 */

import com.bitbucket.thinbus.srp6.js.SRP6JavascriptServerSessionSHA256;
import static spark.Spark.*;

import com.smatt.cc.auth.AuthController;
import com.smatt.cc.auth.ChallengeGen;
import com.smatt.cc.contact.ContactController;
import com.smatt.cc.db.DatabaseHelper;
import com.smatt.cc.index.IndexController;
import com.smatt.cc.util.JsonTransformer;
import com.smatt.cc.util.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Session;

import spark.template.handlebars.HandlebarsTemplateEngine;

public class App {
    
   private ChallengeGen gen;
   private SRP6JavascriptServerSessionSHA256 server; 

	/**
	 * Constructor 
	 * cloud contacts
	 */
    Logger logger = LoggerFactory.getLogger(App.class);
    
    
	public App() {
		
		
		//setup Sparkjava
		staticFileLocation("/public/");
		port(getHerokuAssignedPort());
		
		//Map our class
		new DatabaseHelper();
		
           
                
                //store user attribute in session
                before("/*/", (req, res) -> {
                    Session session = req.session(true);
                    boolean auth = session.attribute(Path.Web.AUTH_STATUS) != null  ? 
                                    session.attribute(Path.Web.AUTH_STATUS) : false;
                    logger.info("auth status = " + auth);
                    if(!auth) {
                        logger.warn("Secured Area! Login is REQUIRED");
                        res.redirect(Path.Web.GET_LOGIN_PAGE);
                       halt(401);
                    }
                });
                
//		Handle routes
		get(Path.Web.HOME, (req, res) -> IndexController.serveHomePage(req, res), new HandlebarsTemplateEngine());

//		handle auth routes
		get(Path.Web.GET_LOGIN_PAGE, (req, res) -> { return AuthController.serveLoginPage(req, res); }, new HandlebarsTemplateEngine());
		post(Path.Web.DO_LOGIN, (req, res) -> { return AuthController.handleLogin(req, res);} );
                post(Path.Web.DO_AUTH, (req, res) -> {return AuthController.handleAuth(req, res); } );
                get(Path.Web.GET_SIGN_UP, (req, res) -> { return AuthController.serveSignUpPage(req, res); }, new HandlebarsTemplateEngine());
		post(Path.Web.DO_SIGN_UP, (req, res) -> {return AuthController.handleSignUp(req, res);});
                get(Path.Web.LOGOUT, (req, res) -> { return AuthController.handleSignOut(req, res); });
		
		
//		handle crud routes
		get(Path.Web.DASHBOARD, (req, res) -> {return ContactController.serveDashboard(req, res);}, new HandlebarsTemplateEngine());
		delete(Path.Web.DELETE, (req, res)-> {return ContactController.handleDeleteContact(req, res);}, new JsonTransformer());
                put(Path.Web.UPDATE, "application/json", (req, res) -> {return ContactController.handleUpdateContact(req, res); });
                post(Path.Web.NEW, "application/json", (req, res) -> { return ContactController.handleNewContact(req, res);} );
                
        }
	

        
     public static int getHerokuAssignedPort() {
//         this will get the heroku assigned port in production
//         or return 8080 for use in local dev
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 8080; //return 8080 on localhost
    }

		
	public static void main(String[] args) {
		new App();
	}

	
}
