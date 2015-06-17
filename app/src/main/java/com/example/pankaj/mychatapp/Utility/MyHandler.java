package com.example.pankaj.mychatapp.Utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.pankaj.mychatapp.Model.AppResultModel;
import com.example.pankaj.mychatapp.Model.ChatMsgModel;
import com.example.pankaj.mychatapp.Model.MsgModel;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.R;
import com.example.pankaj.mychatapp.SplashScreen;
import com.example.pankaj.mychatapp.WebApiRequest.APIHandler;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by pankaj.dhami on 6/11/2015.
 */
public class MyHandler extends NotificationsHandler {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;

    static public HubNotificationService mainActivity;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");
        if (nhMessage.equals("pendingMessage")) {

            new AsyncTask<Object, Object, Object>() {

                @Override
                protected Object doInBackground(Object... params) {
                    int responseCode=0;
                    while (responseCode!=200) {
                        AppResultModel resultModel = APIHandler.getData(mainActivity.BACKEND_ENDPOINT + "/api/Notifications/GetPendingMsg?userID=" + mainActivity.thisUser.UserID);
                        if (resultModel.ResultCode == HttpURLConnection.HTTP_OK) {
                            try {
                                responseCode=resultModel.ResultCode;
                                SqlLiteDb entity=new SqlLiteDb(HubNotificationService.thisServiceContext);
                                entity.open();
                                JSONArray arr = new JSONArray(resultModel.RawResponse);
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject obj = arr.getJSONObject(i);
                                    String msg= obj.getString("TextMessage");
                                    JSONObject fromUser=obj.getJSONObject("UserModel");
                                    UserModel user =new UserModel();
                                    user.Name=fromUser.getString("Name");
                                    user.MobileNo=fromUser.getString("MobileNo");
                                  //  user.Password=fromUser.getString("Password");
                                    user.UserID=fromUser.getInt("UserID");
                                    MsgModel msgModel=new MsgModel();
                                    msgModel.UserModel=user;
                                    msgModel.TextMessage=msg;
                                    ChatMsgModel chatMsgModel=new ChatMsgModel(true,0, user.UserID, user.Name, user.MobileNo
                                            , msgModel.TextMessage, msgModel.AttachmentUrl, msgModel.AttachmentData
                                            , AppEnum.SEND_BY_OTHER  , AppEnum.RECEIVED);
                                    chatMsgModel._id= entity.createChatMsgEntry(chatMsgModel);

                                   mainActivity.publishMessageResults(chatMsgModel,AppEnum.MsgReceivedNotify,true);
                                }
                                entity.close();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return null;
                }
            }.execute(null,null,null);

        }
      //  sendNotification(nhMessage);
        //  mainActivity.DialogNotify("Received Notification",nhMessage);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, SplashScreen.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification Hub Demo")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


}