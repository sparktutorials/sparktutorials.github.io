/**
 * 
 */
package com.smatt.cc.db;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.smatt.cc.auth.User;
import com.smatt.cc.contact.Contact;
import com.smatt.cc.util.Path;

/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 */
public class DatabaseHelper {

	/**
	 * Constructor 
	 * cloud contacts
	 */
	private static Morphia morphia = new Morphia();
	private static Datastore datastore = null;
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);
	
	public DatabaseHelper() {
		if(!morphia.isMapped(Contact.class)) {
			morphia.map(Contact.class);
                        morphia.map(User.class);
			initDatastore();
		} else {
			logger.info("Database Class Mapped Already!");
		}
	}


	
	void initDatastore() {
            
              ProcessBuilder processBuilder = new ProcessBuilder();
            if (processBuilder.environment().get("MONGODB_URI") != null) {
             //heroku environ
		datastore = morphia.createDatastore(new MongoClient(processBuilder.environment().get("MONGODB_URI")),
                        Path.Database.HEROKU_DBNAME);
            } else {
                datastore = morphia.createDatastore(new MongoClient(Path.Database.HOST, Path.Database.PORT), 
                        Path.Database.DBNAME);
            }
            
            datastore.ensureIndexes();
            logger.info("Datastore initiated");
	}

	
	public Datastore getDataStore() {
		if(datastore == null) {
			initDatastore();
		}
		
		return datastore;
	}
	
	
        
}
