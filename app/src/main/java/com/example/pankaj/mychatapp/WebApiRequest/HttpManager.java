package com.example.pankaj.mychatapp.WebApiRequest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.example.pankaj.mychatapp.Model.AppResultModel;
import com.example.pankaj.mychatapp.Model.MsgModel;
import com.example.pankaj.mychatapp.Model.SendMsgModel;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.Utility.ApplicationConstants;
import com.example.pankaj.mychatapp.Utility.MyService;
import com.example.pankaj.mychatapp.Utility.SqlLiteDb;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by pankaj on 6/5/2015.
 */
public class HttpManager {

    Context context;

    public HttpManager(Context context) {
        this.context = context;
    }

    public AppResultModel getToken(String mobile, String password) throws Exception {
        String uri = ApplicationConstants.TokenAddress;
        String query = String.format("userName=%s&password=%s&grant_type=password", mobile, password);
        AppResultModel result = new AppResultModel();
        AppResultModel response = APIHandler.createPost(uri, query, ApplicationConstants.contentTypex_www_form);
        if (response.ResultCode == HttpURLConnection.HTTP_OK)//successful
        {
            JSONObject obj = new JSONObject(response.RawResponse);
            ApplicationConstants.setAccess_token(obj.getString("access_token"));
            ApplicationConstants.setToken_type(obj.getString("token_type"));
            AppResultModel userResponse = APIHandler.getData(ApplicationConstants.ServerAddress + "/api/account/GetUserInfo?mobileNo=" + mobile);
            if (userResponse.ResultCode == HttpURLConnection.HTTP_OK) {
                obj = new JSONObject(userResponse.RawResponse);
                UserModel user = getUserModel(obj);

                ApplicationConstants.thisUser = user;
                result.ResultCode = response.ResultCode;
                result.RawResponse = "Successful";
                result.IsValid = true;
            } else {
                result.ResultCode = response.ResultCode;
                result.RawResponse = "Network error please try again.";
                result.IsValid = false;
            }
        } else if (response.ResultCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            result.ResultCode = response.ResultCode;
            result.RawResponse = "Invalid mobile number or password";
            result.IsValid = false;
        } else if (response.ResultCode == HttpURLConnection.HTTP_CONFLICT) {
            // result.ResultCode=response.ResultCode;
            // result.RawResponse="Mobile number already exists.";
            result.IsValid = false;
        } else {
            result.ResultCode = response.ResultCode;
            result.RawResponse = "Network error please try again.";
            result.IsValid = false;
        }


        // return  sb.toString();
        return result;
    }

    public static AppResultModel registerUser(String mobile, String password) throws Exception {
        String uri = ApplicationConstants.RegisterAddress;
        AppResultModel result = new AppResultModel();
        JSONObject item = new JSONObject();
        item.put("mobileNo", mobile);
        item.put("password", password);
        item.put("confirmPassword", password);
        item.put("name", "");


        AppResultModel response = APIHandler.createPost(uri, item.toString(), ApplicationConstants.contentTypeJson);
        if (response.ResultCode == HttpURLConnection.HTTP_OK)//successful
        {
            result.ResultCode = response.ResultCode;
            response.RawResponse = "Successful";
            result.IsValid = true;
        } else if (response.ResultCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            result.ResultCode = response.ResultCode;
            result.RawResponse = "Error in form data.";
            result.IsValid = false;
        } else if (response.ResultCode == HttpURLConnection.HTTP_CONFLICT) {
            result.ResultCode = response.ResultCode;
            result.RawResponse = "Mobile number already exists.";
            result.IsValid = false;
        }

        // return  sb.toString();
        return result;
    }

    public  ArrayList<UserModel> updateNewFriendsList(ArrayList<UserModel> arrayContacts, int userID) {
        JSONArray jsonarr = null;
        final ArrayList<UserModel> resultList  = new ArrayList<UserModel>();
        try {
            final String uri = ApplicationConstants.ServerAddress + "/api/Friends?userID=" + userID;
            AppResultModel result = new AppResultModel();
//            if (arrayContacts.size() == 0) {
//                query = "";
//            } else {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
            final String query = gson.toJson(arrayContacts);
            //}
             new AsyncTask<Objects,Objects,Objects>() {
                 @Override
                 protected Objects doInBackground(Objects... params) {
                     AppResultModel response = APIHandler.createPost(uri, query, ApplicationConstants.contentTypeJson);
                     if (response.ResultCode == HttpURLConnection.HTTP_OK)//successful
                     {
                         try {
                             SqlLiteDb entity=new SqlLiteDb(context);
                             entity.open();
                             JSONArray jsonarr = new JSONArray(response.RawResponse);

                             for (int i = 0; i < jsonarr.length(); i++) {
                                 JSONObject obj = jsonarr.getJSONObject(i);
                                 UserModel user = getUserModel(obj);
                                 resultList.add(user);
                                 entity.createFriendsEntry(user);
                             }
                             entity.close();
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }

                         MyService.myService.publishFriendsListResults(resultList);
                     }
                     return null;
                 }
            }.execute(null,null,null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public  ArrayList<UserModel> getUpdatedFriendData( int userID) {
        JSONArray jsonarr = null;
        final ArrayList<UserModel> resultList  = new ArrayList<UserModel>();
        try {
            final String uri = ApplicationConstants.ServerAddress + "/api/Friends?userID=" + userID;
            AppResultModel result = new AppResultModel();

            new AsyncTask<Objects,Objects,Objects>() {
                @Override
                protected Objects doInBackground(Objects... params) {
                    AppResultModel response = APIHandler.getData(uri);
                    if (response.ResultCode == HttpURLConnection.HTTP_OK)//successful
                    {
                        try {
                            SqlLiteDb entity=new SqlLiteDb(context);
                            entity.open();
                            JSONArray jsonarr = new JSONArray(response.RawResponse);

                            for (int i = 0; i < jsonarr.length(); i++) {
                                JSONObject obj = jsonarr.getJSONObject(i);
                                UserModel user = getUserModel(obj);
                                resultList.add(user);
                                entity.updateFriends(user);
                            }
                            entity.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MyService.myService.publishFriendsListResults(resultList);
                    }
                    return null;
                }
            }.execute(null,null,null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static UserModel getUserModel(JSONObject obj) throws JSONException {
        UserModel user = new UserModel();
        user.Name = obj.getString("Name");
        user.MobileNo = obj.getString("MobileNo");
        user.Password = obj.getString("Password");
        user.UserID = obj.getInt("UserID");
        user.MyStatus = obj.getString("MyStatus");
        user.PictureUrl = obj.getString("PictureUrl");
        user.PicData = Base64.decode(user.PictureUrl, Base64.DEFAULT);
        return user;
    }

    public  void  sendMessageToUser(MsgModel msgModel)
    {
        SendMsgModel msg=new SendMsgModel();
        SqlLiteDb entity=new SqlLiteDb(context);
        entity.open();
        msg.FromUser= entity.getUser();
        msg.Message=msgModel;
        entity.close();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        final String query = gson.toJson(msg);

        new AsyncTask<Objects,Objects,Objects>() {
            @Override
            protected Objects doInBackground(Objects... params) {
                AppResultModel response = APIHandler.createPost(ApplicationConstants.ServerAddress+"/api/Notifications/SendMessage",
                        query, ApplicationConstants.contentTypeJson);
                if (response.ResultCode == HttpURLConnection.HTTP_OK)//successful
                {

                }
                return null;
            }
        }.execute(null, null, null);


    }
}
