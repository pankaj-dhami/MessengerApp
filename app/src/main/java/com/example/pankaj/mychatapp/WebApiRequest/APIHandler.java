package com.example.pankaj.mychatapp.WebApiRequest;

import com.example.pankaj.mychatapp.Model.AppResultModel;
import com.example.pankaj.mychatapp.Model.MsgModel;
import com.example.pankaj.mychatapp.Utility.ApplicationConstants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pankaj on 6/5/2015.
 */
public class APIHandler {

    public static AppResultModel createPost(String uri, String query, String contentType) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        int responseCode = 0;
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true); // Triggers POST.
            con.setRequestProperty("Content-Type", contentType);
            con.setRequestMethod("POST");
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(query);
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + query);
            System.out.println("Response Code : " + responseCode);


            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            System.out.println(sb.toString());
        } catch (Exception ex) {

        }
        AppResultModel result = new AppResultModel();
        result.RawResponse = sb.toString();
        result.ResultCode = responseCode;
        return result;
    }

    public static AppResultModel createPostWithAuth(String uri, String query, String contentType) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        int responseCode = 0;
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true); // Triggers POST.
            String basicAuth = ApplicationConstants.getoken_type() + " " + ApplicationConstants.getAccess_token();
            con.setRequestProperty("Authorization", basicAuth);
            con.setRequestProperty("Content-Type", contentType);
            con.setRequestMethod("POST");
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(query);
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + query);
            System.out.println("Response Code : " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                System.out.println(sb.toString());
            } else {
                sb.append(con.getResponseMessage());
              /*  if (responseCode ==HttpURLConnection.HTTP_BAD_REQUEST)
                {
                    sb.append(con.getResponseMessage());
                }
                else if(responseCode ==HttpURLConnection.HTTP_CONFLICT )
                {
                    sb.append(con.getResponseMessage());
                }*/
            }
        } catch (Exception ex) {

        }
        AppResultModel result = new AppResultModel();
        result.ResultCode = responseCode;
        result.RawResponse = sb.toString();
        return result;
    }

    public static AppResultModel createGetWithAuth(String uri, String query, String contentType) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        int responseCode = 0;
        try {
            URL url = new URL(uri + query);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String basicAuth = ApplicationConstants.getoken_type() + " " + ApplicationConstants.getAccess_token();
            // con.setRequestProperty("Authorization", basicAuth);
            con.setRequestProperty("Content-Type", contentType);
            con.setRequestMethod("GET");
            responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                System.out.println(sb.toString());
            } else {
                sb.append(con.getResponseMessage());
              /*  if (responseCode ==HttpURLConnection.HTTP_BAD_REQUEST)
                {
                    sb.append(con.getResponseMessage());
                }
                else if(responseCode ==HttpURLConnection.HTTP_CONFLICT )
                {
                    sb.append(con.getResponseMessage());
                }*/
            }
        } catch (Exception ex) {

        }
        AppResultModel result = new AppResultModel();
        result.ResultCode = responseCode;
        result.RawResponse = sb.toString();
        return result;
    }

}
