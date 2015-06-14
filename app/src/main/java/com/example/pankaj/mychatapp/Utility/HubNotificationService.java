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
import java.util.HashSet;

public class HubNotificationService extends Service {

    //region hub notification variables
    private String SENDER_ID = "863748063039";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private final String HubName = "messengerapihub";
    private final String HubListenConnectionString = "Endpoint=sb://messengerapihub-ns.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=VcUULvdA/EK3KWO7K1IAySQYWJt96zfKc2H+BcLMotI=";
    private RegisterClient registerClient;
    public static final String BACKEND_ENDPOINT = "http://apitoken.azurewebsites.net";
    static UserModel thisUser;
    //endregion

    //region variables
    public static boolean ChatBubbleActivity_active;
    //endregion

    public HubNotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyHandler.mainActivity = HubNotificationService.this;
        NotificationsManager.handleNotifications(HubNotificationService.this, SENDER_ID, MyHandler.class);
        gcm = GoogleCloudMessaging.getInstance(HubNotificationService.this);
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
                    SqlLiteDb entity=new SqlLiteDb(HubNotificationService.this);
                    entity.open();
                    HubNotificationService.this.thisUser= entity.getUser();
                    entity.close();
                    registerClient.register(HubNotificationService.this.thisUser.MobileNo, regid, new HashSet<String>());
                    sendPush("gcm",HubNotificationService.this.thisUser.MobileNo,"Welcome user");
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
                    uri += "&userName=" + ApplicationConstants.thisUser.MobileNo;


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
    public void publishMessageResults(MsgModel msgModel) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      return   START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }
}
