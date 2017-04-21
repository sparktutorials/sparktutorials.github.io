/**
 * 
 */
package com.smatt.cc.auth;

import com.google.gson.Gson;

/**
 * @author Seun Matt
 * Date 27 Mar 2016
 * Year 2016
 * (c) SMATT Corporation
 */
public class MyResponse {

	/**
	 * This models response to be sent to the client
	 */
	
	public String status;
	public String code;
	public String extra;
	
	public MyResponse() {	}
	
	public MyResponse(String status) {
		this.status = status;
		code = "201";
	}
	
	
	public MyResponse(String status, String code) {
		this.status = status;
		this.code = code;
	}
	
	public String toString() {
		return new Gson().toJson(this);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	

}
