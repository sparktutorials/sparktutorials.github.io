/**
 * 
 */
package com.smatt.cc.contact;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 */

@Entity("contacts")
public class Contact {

	/**
	 * Constructor 
	 * cloud contacts
	 */
	
	@Id
	private ObjectId id;
	
        @Property("first_name")
	private String firstName = "";
        @Property("middle_name")
	private String middleName = "";
        @Property("last_name")
	private String lastName = "";
        
	private String mobile = "";
	private String work = "", fax = "", home = "";
	private String email = "";
	private String address = "";
        private String note =  "";
        
        @Property("_created_at")
	private Date createdAt = new Date();
        @Property("_updated_at")
        private Date updatedAt = new Date();
	
	@Property("user_id")
	private String userId = "";
        
        /**
         * WARNING: This class is mapped by morphia
         * if you create an instance of logger here
         * there will be error by morphia
         */
	
	public Contact() {	}

	
	public Contact(String firstName, String lastName, String mobile) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobile = mobile;
	}
	

	public ObjectId getId() {
		return (id != null) ? id : null;
	}


	public void setId(String id) {
            //this will prevent class cast exception
            //by morphia and in case it is null
            //nothing will happen
            if(id != null && !id.isEmpty()) {
                this.id = new ObjectId(id);
            }
   	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getMiddleName() {
		return middleName;
	}


	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getWork() {
		return work;
	}


	public void setWork(String work) {
		this.work = work;
	}


	public String getFax() {
		return fax;
	}


	public void setFax(String fax) {
		this.fax = fax;
	}


	public String getHome() {
		return home;
	}


	public void setHome(String home) {
		this.home = home;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getNote() {
                return note;
	}


	public void setNote(String note) {
		this.note = note;
	}

	
	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public Date getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public Date getUpdatedAt() {
		return updatedAt;
	}


	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}


        @Override
	public String toString() {
		
		return "id = " + id +
				"\nfirst_name = " + getFirstName() + 
				"\nmiddle_name = " + getMiddleName() +
				"\nlast_name = " + getLastName() +
				"\nmobile = " + getMobile() +
				"\nfax = " + getFax() +
				"\nwork = " + getWork() + 
				"\nhome = " + getHome() + 
				"\nemail = " + getEmail() +
				"\naddress = " + getAddress() +
				"\nnote = " +  getNote() +
				"\n";				
	}
	
	
	public String toJson() {
		
		return  "{" +
                        "\"id\": " + "\"" + getId() + "\", " +
                        "\"first_name\":" + "\"" + getFirstName() + "\", " +
                        "\"middle_name\":" + "\"" + getMiddleName() + "\", " +
                        "\"last_name\":" + "\"" + getLastName() + "\", " +
                        "\"mobile\":" + "\"" + getMobile() + "\", " +
                        "\"fax\":" + "\"" + getFax() + "\", " +
                        "\"work\":" + "\"" + getWork() + "\", " +
                        "\"home\":" + "\"" + getHome() + "\", " +
                        "\"email\":" + "\"" + getEmail() + "\", " +
                        "\"address\":" + "\"" + getAddress() + "\", " +
                        "\"note\":" + "\"" + getNote() + "\"" +
                         "}" ;
	}
	
	
	public String toTableRow() {
		
		return  "<tr>" + "\n" +
				"<td>" + getFirstName() + " " + getLastName() + "</td>" + "\n" +
				"<td>" + getMobile() + "</td>" + "\n" + 
				"<td>" + getEmail() + "</td>" + "\n" +
				"<td class=\"view\">" + "<span class=\"glyphicon glyphicon-search\"></span> </td> \n" +
				"<td class=\"del\">" + "<span class=\"glyphicon glyphicon-trash\"></span> </td> " + 
				"</tr> \n";
		
	}
        
        
        public String toTableRow(int index) {
		
		return  "<tr>" + "\n" +
                                "<td>" + index + "</td>" + "\n" +
				"<td class=\"ncol\">" + getFirstName() + " " + getLastName() + "</td>" + "\n" +
				"<td class=\"mcol\">" + getMobile() + "</td>" + "\n" + 
				"<td class=\"ecol\">" + getEmail() + "</td>" + "\n" +
				"<td class=\"view\">" + "<span class=\"glyphicon glyphicon-search\"></span> </td> \n" +
				"<td class=\"del\">" + "<span class=\"glyphicon glyphicon-trash\"></span> </td> " + 
				"</tr> \n";
	}
        
	
	
}
