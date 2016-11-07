package com.utils;

 import java.math.BigInteger;
import java.security.MessageDigest;

public class convertMD5 {
	
	public convertMD5(){
		
	}
    public static String encrypt(String message) {
        try{
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(message.getBytes());
            return String.format("%032x",new BigInteger(1,m.digest()));
        }
        catch(Exception e){
            return null;
        }
    }
}
   
