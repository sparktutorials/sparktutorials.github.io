/**
 * 
 */
package com.smatt.cc.index;

import com.smatt.cc.util.Path;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 */
public class IndexController {

	/**
	 * Constructor 
	 * cloud contacts
	 */
	public IndexController() {
		
	}
	
	
	public static ModelAndView serveHomePage (Request re, Response res) {
			return new ModelAndView(null, Path.Templates.INDEX);
    }
	

}
