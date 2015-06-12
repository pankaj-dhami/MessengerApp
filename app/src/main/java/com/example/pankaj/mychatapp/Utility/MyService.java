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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

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
    public static boolean ChatBubbleActivity_active;
    public static ArrayList<UserModel> FriendsList = new ArrayList<UserModel>();
    public static  UserModel thisUser;
    public static boolean Tab1Activity_active;
    private boolean isReallyStop;

    //region hub notification variables
    private String SENDER_ID = "863748063039";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private final String HubName = "messengerapihub";
    private final String HubListenConnectionString = "Endpoint=sb://messengerapihub-ns.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=VcUULvdA/EK3KWO7K1IAySQYWJt96zfKc2H+BcLMotI=";
    private RegisterClient registerClient;
    public static final String BACKEND_ENDPOINT = "http://msgapi.azurewebsites.net";
    //endregion


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
        MyHandler.mainActivity = MyService.this;
        NotificationsManager.handleNotifications(MyService.this, SENDER_ID, MyHandler.class);
        gcm = GoogleCloudMessaging.getInstance(MyService.this);
      //  hub = new NotificationHub(HubName, HubListenConnectionString, MyService.this);
        //registerWithNotificationHubs();
        registerClient = new RegisterClient(this, BACKEND_ENDPOINT);
        registerWithNotificationHubs();

    }
    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {

                    String regid = gcm.register(SENDER_ID);
                    registerClient.register(thisUser.MobileNo, regid, new HashSet<String>());
                     sendPush("gcm", thisUser.MobileNo, "Welcome user");
                } catch (Exception e) {
                    DialogNotify("Exception",e.getMessage());
                    return e;
                }
                return null;
            }
        }.execute(null, null, null);
    }

    public static void sendPush(final String pns, final String userTag,  String message)
            throws ClientProtocolException, IOException {
        final String nMessage= "\"" + message + "\"";
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {

                    String uri = BACKEND_ENDPOINT + "/api/notifications";
                    uri += "?pns=" + pns;
                    uri += "&to_tag=" + userTag;
                    uri += "&userName=" + thisUser.MobileNo;


                    HttpPost request = new HttpPost(uri);
                    // request.addHeader("Authorization", "Basic "+ getAuthorizationHeader());
                    request.setEntity(new StringEntity(nMessage));
                    request.addHeader("Content-Type", "application/json");

                    HttpResponse response = new DefaultHttpClient().execute(request);

                    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                   //     DialogNotify("MainActivity - Error sending " + pns + " notification",
                    //            response.getStatusLine().toString());
                        throw new RuntimeException("Error sending notification");
                    }
                } catch (Exception e) {
                   // DialogNotify("MainActivity - Failed to send " + pns + " notification ", e.getMessage());
                    return e;
                }

                return null;
            }
        }.execute(null, null, null);
    }

    public void DialogNotify(final String title,final String message)
    {
        final NotificationManager mgr =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification note = new Notification(R.drawable.ic_action_microphone,
                "Android Example Status message!",
                System.currentTimeMillis());

        // This pending intent will open after notification click
      //  Intent intent = new Intent(this, ChatBubbleActivity.class);
      //  ApplicationConstants.chatUser = msgModel.UserModel;

     //   PendingIntent i = PendingIntent.getActivity(this, 0,
       //         intent,
       //         0);

        note.setLatestEventInfo(this, title,
                message,null
        );

        //After uncomment this line you will see number of notification arrived
        //note.number=2;
        mgr.notify(10, note);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void publishFriendsListResults(ArrayList<UserModel> friendsList) {
        if (Tab1Activity_active) {
            Intent intent = new Intent("com.example.pankaj.mychatapp");
            intent.putExtra("code", "friendsList");
            FriendsList = friendsList;
            sendBroadcast(intent);
        }
    }

    private void publishMessageResults(MsgModel msgModel) {
        if (ChatBubbleActivity_active) {
            Intent intent = new Intent("com.example.pankaj.mychatapp");
            intent.putExtra("code", "msgModel");
            ApplicationConstants.msgModel = msgModel;
            sendBroadcast(intent);
        } else {
            /*********** Create notification ***********/

            final NotificationManager mgr =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note = new Notification(R.drawable.ic_action_microphone,
                    "Android Example Status message!",
                    System.currentTimeMillis());

            // This pending intent will open after notification click
            Intent intent = new Intent(this, ChatBubbleActivity.class);
            ApplicationConstants.chatUser = msgModel.UserModel;

            PendingIntent i = PendingIntent.getActivity(this, 0,
                    intent,
                    0);

            note.setLatestEventInfo(this, "New message from " + msgModel.UserModel.Name,
                    msgModel.TextMessage, i
            );

            //After uncomment this line you will see number of notification arrived
            //note.number=2;
            mgr.notify(msgModel.UserModel.UserID, note);
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
            //    if (!isReallyStop) {
              //      startConnection();
             //   }
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
            connection = new HubConnection(ApplicationConstants.host);
            proxy = connection.createHubProxy("MessengerHub");
            registerListeners();
            awaitConnection = connection.start();
            try {
                awaitConnection.get();
                proxy.invoke("connectUser", thisUser);
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

        proxy.invoke("sendMessageToGroup", thisUser, msgModel);
    }
}
