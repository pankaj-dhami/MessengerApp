package com.example.pankaj.mychatapp.WebApiRequest;

import android.os.StrictMode;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
public class SignalRManager {

    public SignalRManager() {

    }


    public  void connectSignalR()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        String host = "http://signalrapi.azurewebsites.net/";
        HubConnection connection = new HubConnection( host );
        HubProxy proxy = connection.createHubProxy( "Chat" );
        SignalRFuture<Void> awaitConnection = connection.start();
        try {
            awaitConnection.get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //--HERE IS MY SOLUTION-----------------------------------------------------------
        //Invoke JoinGroup to start receiving broadcast messages
        proxy.invoke("Send", "Group1");

        //Then call on() to handle the messages when they are received.
        proxy.on( "broadcastMessage", new SubscriptionHandler1<String>() {
            @Override
            public void run(String msg) {
                Log.d("result := ", msg);
            }
        }, String.class);


        // equal to `connection.disconnected(function ()` from javascript
        connection.closed(new Runnable() {

            @Override
            public void run() {
                Log.d("result :="," closed");
                // ADD CODE TO HANDLE DISCONNECTED EVENT
            }
        });



    }
}
