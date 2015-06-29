package com.example.pankaj.mychatapp.WebApiRequest;

import android.os.StrictMode;
import android.util.Log;

import com.example.pankaj.mychatapp.Model.AppResultModel;
import com.example.pankaj.mychatapp.Utility.ApplicationConstants;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        final String BOUNDARY = "3C3F786D6C2076657273696F6E2E302220656E636F64696E673D662D38223F3E0A3C6D616E6966";
        int responseCode = 0;
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true); // Triggers POST.
            con.setRequestProperty("Content-Type", contentType);
            con.setRequestMethod("POST");
            // con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            // con.setRequestProperty("Accept", "text/json");
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
            ex.printStackTrace();
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

            // responseCode = con.getResponseCode();

            // if(responseCode ==HttpURLConnection.HTTP_OK) {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
            // }
            // else {
            //     sb.append(con.getResponseMessage());
              /*  if (responseCode ==HttpURLConnection.HTTP_BAD_REQUEST)
                {
                    sb.append(con.getResponseMessage());
                }
                else if(responseCode ==HttpURLConnection.HTTP_CONFLICT )
                {
                    sb.append(con.getResponseMessage());
                }*/
            // }
        } catch (Exception ex) {

        }
        AppResultModel result = new AppResultModel();
        result.ResultCode = responseCode;
        result.RawResponse = sb.toString();
        return result;
    }

    public static AppResultModel getData(String uri) {
        HttpClient httpClient;
        String raw = "";
        int responseCode = 0;
        httpClient = new DefaultHttpClient();
        try {
            HttpUriRequest request = new HttpGet(uri);
            // request.addHeader("Authorization", "Basic "+authorizationHeader);
            HttpResponse response = httpClient.execute(request);
            if ((responseCode = response.getStatusLine().getStatusCode()) != HttpStatus.SC_OK) {
                Log.e("RegisterClient", "Error creating registrationId: " + response.getStatusLine().getStatusCode());
                //  throw new RuntimeException("Error creating Notification Hubs registrationId");
            }
            raw = EntityUtils.toString(response.getEntity());


        } catch (Exception ex) {
            System.out.println(ex);
        }
        AppResultModel result = new AppResultModel();
        result.ResultCode = responseCode;
        result.RawResponse = raw;
        return result;

    }

    public synchronized static AppResultModel createHttpPost(String uri, String query, String contentType,int timeout) {
        HttpClient httpClient;
        String raw = "";
        int responseCode = 0;
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = timeout;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = timeout;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        httpClient = new DefaultHttpClient(httpParameters);
        try {
            HttpPost request = new HttpPost(uri);
            request.setEntity(new StringEntity(query.toString()));
            // request.addHeader("Authorization", "Basic "+authorizationHeader);

            HttpResponse response = httpClient.execute(request);
            if ((responseCode = response.getStatusLine().getStatusCode()) != HttpStatus.SC_OK) {
                Log.e("RegisterClient", "Error creating registrationId: " + response.getStatusLine().getStatusCode());
                //  throw new RuntimeException("Error creating Notification Hubs registrationId");
            }
            raw = EntityUtils.toString(response.getEntity());


        } catch (Exception ex) {
            System.out.println(ex);
        }
        AppResultModel result = new AppResultModel();
        result.ResultCode = responseCode;
        result.RawResponse = raw;
        return result;
    }





}
