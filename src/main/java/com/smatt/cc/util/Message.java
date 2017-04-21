/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatt.cc.util;

/**
 *
 * @author smatt
 */
public class Message {
    
    private String status;
    private int code;
    
    public Message() {}
    
    public Message(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    
    
    @Override
    public String toString() {
        return "code: " + getCode() + " status: " + getStatus();
    }
    
    
    
    
    
}
