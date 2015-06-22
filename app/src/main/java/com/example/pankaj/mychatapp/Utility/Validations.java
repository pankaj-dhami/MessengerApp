package com.example.pankaj.mychatapp.Utility;

/**
 * Created by pankaj.dhami on 6/2/2015.
 */
public class Validations {

    public static boolean isEmailValid(String email) {
        //add your own logic
        return email.contains("@");
    }
    public static boolean isMobileValid(String mobile) {
        //add your own logic
        return  true; //android.util.Patterns.PHONE.matcher(mobile).matches() && mobile.length()==10;
      //  return mobile.length()==10;
    }
    public static boolean isPasswordValid(String password) {
        //add your own logic
        return password.length() > 4;
    }
    public static boolean isConformPassword(String password,String conformPassword) {
        //add your own logic
        return password.equals(conformPassword);
    }


}

