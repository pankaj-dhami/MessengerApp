package com.example.pankaj.mychatapp.Utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.example.pankaj.mychatapp.ChatBubbleActivity;
import com.example.pankaj.mychatapp.Model.MsgModel;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.R;

import java.util.ArrayList;
import java.util.Objects;

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
    public static HubConnection connection;
    static HubProxy proxy;
    public static boolean ChatBubbleActivity_active;
    public static ArrayList<UserModel> FriendsList = new ArrayList<UserModel>();
    public static boolean Tab1Activity_active;
    public static boolean HomeActivity_active;
    private boolean isReallyStop;
    static UserModel thisUser;
    public static MyService myService;

    @Override
    public void onDestroy() {
        super.onDestroy();
        isReallyStop = true;
        connection.stop();
        // Toast.makeText(this, "service destroyed ", Toast.LENGTH_LONG);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        MyService.myService=MyService.this;
        new AsyncTask<Objects, Objects, Objects>() {
            @Override
            protected Objects doInBackground(Objects... params) {
                connectSignalR();
                return null;
            }
        }.execute(null, null, null);

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public void publishFriendsListResults(ArrayList<UserModel> friendsList) {

       // FriendsList = friendsList;
        if (Tab1Activity_active) {
            Intent intent = new Intent("com.example.pankaj.mychatapp");
            intent.putExtra("code", "friendsList");
            sendBroadcast(intent);
        }
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
                //connection.stop();

            }
        });


    }

    private void startConnection() {
        boolean isConnected = false;
        while (!isConnected) {
            connection = new HubConnection(ApplicationConstants.ServerAddress);
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
             //   publishFriendsListResults(userList);
            }
        }, UserModel[].class);

        proxy.on("getMessages", new SubscriptionHandler1<MsgModel>() {
            @Override
            public void run(MsgModel msgModel) {
               // publishMessageResults(msgModel);
                //    Toast.makeText(MyService.this, "onConnected ", Toast.LENGTH_LONG);
            }
        }, MsgModel.class);

    }

    public static void sendMessage(MsgModel msgModel) {

        proxy.invoke("sendMessage", ApplicationConstants.thisUser, msgModel);
    }
    public static void sendVoice(byte[] buffer) {

        proxy.invoke("sendMessage", ApplicationConstants.thisUser, buffer);
    }
    public void disconnectUser()
    {
        isReallyStop=true;
        proxy.invoke("disconnectUser", ApplicationConstants.thisUser);
    }
}
