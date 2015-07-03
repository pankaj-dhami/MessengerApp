package com.example.pankaj.mychatapp.Utility;

import com.example.pankaj.mychatapp.Model.ChatMsgModel;
import com.example.pankaj.mychatapp.Model.UserModel;

/**
 * Created by pankaj on 6/5/2015.
 */
public class ApplicationConstants  {

    public final static String ServerAddress="http://webapitoken.azurewebsites.net";
    public final static String host="http://msgapi.azurewebsites.net";
    public final static String TokenAddress=ServerAddress+"/token";
    public final static String RegisterAddress=ServerAddress+"/api/account/register";
    public final static String UpdateUser=ServerAddress+"/api/account/UpdateUser";


    private static String token_type="bearer";
    private static String access_token="";
    public final static String charset = "UTF-8";
    public final static String contentTypeJson = "application/json";
    public final static String contentTypex_www_form = "application/x-www-form";
    public final static String token_fileName="token_fileName.txt";
    public final static String msgSendToken="?";
    public final static String msgDeliverToken="??";

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    public static final int SELECT_SINGLE_PICTURE = 101;

    public static final int SELECT_MULTIPLE_PICTURE = 201;

    public static final String IMAGE_TYPE = "image/*";

    public static  UserModel thisUser;
    public static ChatMsgModel msgModel;


    public static String getoken_type()
    {
        return  token_type;
    }

    public static void setToken_type(String token_type)
    {
        token_type=token_type;
    }
    public static String getAccess_token()
    {
       // return  new FileIO().readFromfile(token_fileName,1);
        return access_token;
    }

    public static void setAccess_token(String access_token)
    {
        access_token=access_token;
     //  new FileIO().saveToFile(token_fileName,access_token);
    }
}
