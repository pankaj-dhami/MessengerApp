package com.example.pankaj.mychatapp.Utility;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.pankaj.mychatapp.Model.MsgModel;
import com.example.pankaj.mychatapp.Model.UserModel;

import java.util.ArrayList;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;

/**
 * Created by pankaj on 6/7/2015.
 */
public class MyService extends Service {

    SignalRFuture<Void> awaitConnection;
    HubConnection connection;
    static HubProxy proxy;
    private boolean isReallyStop;

    @Override
    public void onDestroy() {
        super.onDestroy();
        isReallyStop = true;
        connection.stop();
        // Toast.makeText(this, "service destroyed ", Toast.LENGTH_LONG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // String str;
        // str= intent.getStringExtra("message");
        // publishResults("service started");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        connectSignalR();

        //  publishResults("service created");
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void publishFriendsListResults(ArrayList<UserModel> friendsList) {
        Intent intent = new Intent("com.example.pankaj.mychatapp");
        intent.putExtra("code", "friendsList");
        ApplicationConstants.friendsList = friendsList;
        sendBroadcast(intent);
    }

    private void publishMessageResults(MsgModel msgModel) {
        Intent intent = new Intent("com.example.pankaj.mychatapp");
        intent.putExtra("code", "msgModel");
        ApplicationConstants.msgModel=msgModel;
        sendBroadcast(intent);
    }

    public void connectSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        startConnection();

        // equal to `connection.disconnected(function ()` from javascript
        connection.closed(new Runnable() {
            @Override
            public void run() {
                // Log.d("result :=", " closed");
                if (!isReallyStop) {
                    startConnection();
                }
                // ADD CODE TO HANDLE DISCONNECTED EVENT
            }
        });

        connection.reconnecting(new Runnable() {
            @Override
            public void run() {
                // Log.d("result :=", " closed");
                if (!isReallyStop) {
                  //  awaitConnection = connection.start();
                    try {
                    //    awaitConnection.get();
                    //    proxy.invoke("connectUser", ApplicationConstants.thisUser);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                    // ADD CODE TO HANDLE DISCONNECTED EVENT
                } else {
                    isReallyStop = false;
                }
            }
        });


    }

    private void startConnection() {
        boolean isConnected = false;
        while (!isConnected) {
            connection = new HubConnection(ApplicationConstants.host);
            proxy = connection.createHubProxy("MessengerHub");
            registerListeners();
            awaitConnection = connection.start();
            try {
                awaitConnection.get();
                proxy.invoke("connectUser", ApplicationConstants.thisUser);
                isConnected = true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isConnected = false;
            }
        }
    }

    private void registerListeners() {

        proxy.on("onConnected", new SubscriptionHandler1<UserModel>() {
            @Override
            public void run(UserModel user) {
                proxy.invoke("getAllUserList");
                //     Toast.makeText(MyService.this, "onConnected ", Toast.LENGTH_LONG);
            }
        }, UserModel.class);

        proxy.on("refreshUserList", new SubscriptionHandler1<UserModel[]>() {
            @Override
            public void run(UserModel[] activeUserList) {
                //  Toast.makeText(MyService.this, "refreshUserList ", Toast.LENGTH_LONG);
                ArrayList<UserModel> userList = new ArrayList<UserModel>();
                for (UserModel user : activeUserList) {
                    userList.add(user);
                }
                publishFriendsListResults(userList);
            }
        }, UserModel[].class);

        proxy.on("getMessages", new SubscriptionHandler1<MsgModel>() {
            @Override
            public void run(MsgModel msgModel) {
                publishMessageResults(msgModel);
                //    Toast.makeText(MyService.this, "onConnected ", Toast.LENGTH_LONG);
            }
        }, MsgModel.class);

    }

    public static void sendMessage(MsgModel msgModel) {

        proxy.invoke("sendMessageToGroup", ApplicationConstants.thisUser, msgModel);
    }
}
