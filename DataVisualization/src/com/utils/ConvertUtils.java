package com.utils;

 import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

public class ConvertUtils {
	
	public ConvertUtils(){
		
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
    
	/////*******   METHOD TO GENERATE RANDOM STRING ***********
	public static String generateRandomString() {
		String Alfabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder randomString = new StringBuilder();
		Random rnd = new Random();
		while (randomString.length() < 8) {
			int index = (int) (rnd.nextFloat() * Alfabet.length());
			randomString.append(Alfabet.charAt(index));
		}
		return randomString.toString();

	}
}
   
