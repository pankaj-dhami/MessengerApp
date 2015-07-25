package com.example.pankaj.mychatapp.Utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;

import com.example.pankaj.mychatapp.CustomUI.UserPicture;
import com.example.pankaj.mychatapp.Model.AppResultModel;
import com.example.pankaj.mychatapp.Model.ChatMsgModel;
import com.example.pankaj.mychatapp.Model.MsgModel;
import com.example.pankaj.mychatapp.Model.SendMsgModel;
import com.example.pankaj.mychatapp.Model.TelephoneNumberModel;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.R;
import com.example.pankaj.mychatapp.WebApiRequest.APIHandler;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class HubNotificationService extends Service {

    //region hub notification variables
    private String SENDER_ID = "863748063039";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private final String HubName = "messengerhub";
    private final String HubListenConnectionString = "Endpoint=sb://messengerhub-ns.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=ubfkQbBJFyOVBQuBkM/WAlzogcC6bSKLJhsk6snpDW8==";
    private RegisterClient registerClient;
    public static UserModel thisUser;
    public static HubNotificationService thisServiceContext;
    public static ChatMsgModel chatMsgModel;
    //endregion

    //region variables
    public static boolean ChatBubbleActivity_active;
    public static boolean Tab1Activity_active;
    public static UserModel chatUser;

    //endregion

    public HubNotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thisServiceContext = HubNotificationService.this;
        MyHandler.mainActivity = HubNotificationService.this;
        NotificationsManager.handleNotifications(HubNotificationService.this, SENDER_ID, MyHandler.class);
        gcm = GoogleCloudMessaging.getInstance(HubNotificationService.this);
        //  hub = new NotificationHub(HubName, HubListenConnectionString, MyService.this);
        //registerWithNotificationHubs();
        registerClient = new RegisterClient(this, ApplicationConstants.ServerAddress);
        registerWithNotificationHubs();
    }

    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {

                    String regid = gcm.register(SENDER_ID);
                    SqlLiteDb entity = new SqlLiteDb(HubNotificationService.this);
                    entity.open();
                    HubNotificationService.this.thisUser = entity.getUser();
                    entity.close();
                    registerClient.register(HubNotificationService.this.thisUser.MobileNo, regid, new HashSet<String>());
                    sendPush("gcm", HubNotificationService.this.thisUser.MobileNo, "Welcome user");
                } catch (Exception e) {
                    DialogNotify("Exception", e.getMessage());
                    return e;
                }
                return null;
            }
        }.execute(null, null, null);
    }

    public static void sendPush(final String pns, final String userTag, String message)
            throws ClientProtocolException, IOException {
        final String nMessage = "\"" + message + "\"";
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {

                    String uri = ApplicationConstants.ServerAddress + "/api/notifications";
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

    public void DialogNotify(final String title, final String message) {
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
                message, null
        );

        //After uncomment this line you will see number of notification arrived
        //note.number=2;
        mgr.notify(10, note);
    }

    public void publishMessageResults(ChatMsgModel msgModel, String code, boolean doCreateNotification) {
        if (msgModel != null && msgModel.remoteUrl != null && msgModel.remoteUrl != "") {
            doDownloadAttachment(msgModel);
        }
        if (ChatBubbleActivity_active) {
            Intent intent = new Intent("com.example.pankaj.mychatapp");
            intent.putExtra("code", code);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String query = gson.toJson(msgModel);
            // ApplicationConstants.msgModel = msgModel;
            intent.putExtra("message", query);
            sendBroadcast(intent);
        } else if (doCreateNotification) {
            /*********** Create notification ***********/

            final NotificationManager mgr =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note = new Notification(R.drawable.ic_action_microphone,
                    "Android Example Status message!",
                    System.currentTimeMillis());

            // This pending intent will open after notification click
            //   Intent intent = new Intent(this, ChatBubbleActivity.class);
            //   ApplicationConstants.chatUser = msgModel.UserModel;

            //   PendingIntent i = PendingIntent.getActivity(this, 0,
            //           intent,
            //          0);

            note.setLatestEventInfo(this, "New message from " + msgModel.Name,
                    msgModel.TextMessage, null
            );

            //After uncomment this line you will see number of notification arrived
            //note.number=2;
            mgr.notify(msgModel.UserID, note);
        }
    }

    public void publishFriendsListResults(ArrayList<UserModel> friendsList) {

        if (friendsList != null && friendsList.size() > 0) {
            SqlLiteDb db = new SqlLiteDb(thisServiceContext);
            db.open();
            ArrayList<UserModel> updatedFriends = new ArrayList<>();
            for (UserModel newFriend : friendsList) {
                UserModel existingFriend = db.getFriend(newFriend.UserID);
                if (existingFriend.PictureUrl != newFriend.PictureUrl) {
                    updatedFriends.add(newFriend);
                }
            }
            db.close();
            doDownload(updatedFriends);
        }

        // FriendsList = friendsList;
        if (Tab1Activity_active) {
            Intent intent = new Intent("com.example.pankaj.mychatapp");
            intent.putExtra("code", "friendsList");
            sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public ArrayList<UserModel> updateNewFriendsList(ArrayList<TelephoneNumberModel> arrayContacts, int userID) {
        JSONArray jsonarr = null;
        final ArrayList<UserModel> resultList = new ArrayList<UserModel>();
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
            new AsyncTask<Objects, Objects, Objects>() {
                @Override
                protected Objects doInBackground(Objects... params) {
                    AppResultModel response = APIHandler.createPost(uri, query, ApplicationConstants.contentTypeJson);
                    if (response.ResultCode == HttpURLConnection.HTTP_OK)//successful
                    {
                        try {
                            SqlLiteDb entity = new SqlLiteDb(HubNotificationService.this);
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

                        publishFriendsListResults(resultList);
                    }
                    return null;
                }
            }.execute(null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public ArrayList<UserModel> getUpdatedFriendData(int userID) {
        JSONArray jsonarr = null;
        final ArrayList<UserModel> resultList = new ArrayList<UserModel>();
        try {
            final String uri = ApplicationConstants.ServerAddress + "/api/Friends?userID=" + userID;
            AppResultModel result = new AppResultModel();

            new AsyncTask<Objects, Objects, Objects>() {
                @Override
                protected Objects doInBackground(Objects... params) {
                    AppResultModel response = APIHandler.getData(uri);
                    if (response.ResultCode == HttpURLConnection.HTTP_OK)//successful
                    {
                        try {
                            SqlLiteDb entity = new SqlLiteDb(HubNotificationService.this);
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
                        publishFriendsListResults(resultList);
                    }
                    return null;
                }
            }.execute(null, null, null);

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

        //user.PicData = Base64.decode(user.PictureUrl, Base64.DEFAULT);
        return user;
    }

    public static ChatMsgModel getChatModel(JSONObject obj) throws JSONException {
        ChatMsgModel user = new ChatMsgModel();
        user._id = obj.getInt("_id");
        user.UserID = obj.getInt("UserID");
        user.Name = obj.getString("Name");
        user.MobileNo = obj.getString("MobileNo");
        user.TextMessage = obj.getString("TextMessage");
        user.IsMyMsg = obj.getInt("IsMyMsg");
        user.IsSendDelv = obj.getInt("IsSendDelv");
        if (obj.has("PictureUrl")) {
            user.PictureUrl = obj.getString("PictureUrl");
        }
        if (obj.has("CreatedDate")) {
            user.CreatedDate = obj.getString("CreatedDate");
        }
        user.left = obj.getBoolean("left");
        return user;
    }

    public void sendMessageToUser(MsgModel msgModel, ChatMsgModel chatMsgModel) {

        if (TextUtils.isEmpty(chatMsgModel.PictureUrl)) {
            Thread thread = new Thread(new SendMessageThread(msgModel, chatMsgModel));
            thread.start();
        } else {
            msgModel.IsAttchment = true;
            Thread thread = new Thread(new SendImageMessageThread(msgModel, chatMsgModel));
            thread.start();
        }
    }

    protected void doDownload(final ArrayList<UserModel> downloadModels) {

        Thread dx = new Thread() {

            public void run() {

                for (UserModel model : downloadModels) {
                    final String urlLink = model.PictureUrl;
                    try {
                        URL url = new URL(urlLink);
                        URLConnection connection = url.openConnection();
                        connection.connect();
                        // this will be useful so that you can show a typical 0-100% progress bar
                        int fileLength = connection.getContentLength();

                        // download the file
                        InputStream input = new BufferedInputStream(url.openStream());

                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        int nRead;
                        byte[] data = new byte[16384];

                        while ((nRead = input.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                        }

                        buffer.flush();
                        model.PicData = buffer.toByteArray();
                        input.close();
                        SqlLiteDb entity = new SqlLiteDb(thisServiceContext);
                        entity.open();
                        entity.updateFriendImage(model);
                        entity.close();
                        publishFriendsListResults(null);

                    } catch (Exception e) {
                        e.printStackTrace();
                        // Log.i("ERROR ON DOWNLOADING FILES", "ERROR IS" +e);
                    }
                }
            }
        };
        dx.start();

    }

    protected void doDownloadAttachment(final ChatMsgModel chatMsgModel) {

        Thread dx = new Thread() {

            public void run() {

                String file = chatMsgModel.remoteUrl.substring(chatMsgModel.remoteUrl.lastIndexOf('/') + 1);
                // File directory;
                try {
                    //  if (Environment.getExternalStorageState() == null) {
                    //create new file directory object
                    //  directory = new File(Environment.getDataDirectory()
                    //         + "/messenger/");

                    // if no directory exists, create new directory


                    //   File mypath = new File(directory, file);
                    //  String localFilePath=mypath.getAbsolutePath();

                    URL url = new URL(chatMsgModel.remoteUrl);
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    // this will be useful so that you can show a typical 0-100% progress bar
                    int fileLength = connection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream());

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[16384];

                    while ((nRead = input.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }

                    buffer.flush();
                  //  Bitmap bitmapImage = BitmapFactory.decodeByteArray(buffer.toByteArray(), 0, buffer.toByteArray().length);
                   // ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    // path to /data/data/yourapp/app_data/imageDir
                   // File directory = cw.getDir("messenger", Context.MODE_PRIVATE);
                   // if (!directory.exists()) {
                    //    directory.mkdir();
                   // }
                    // Create imageDir
                    File dir = new File(Environment.getExternalStorageDirectory(), "IM_Images");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File downloads=  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File mypath = new File(dir, file);

                    FileOutputStream fos = null;
                    try {

                        fos = new FileOutputStream(mypath);
                        fos.write(buffer.toByteArray());
                        fos.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String localFilePath= mypath.getAbsolutePath();

                    SqlLiteDb entity = new SqlLiteDb(thisServiceContext);
                    entity.open();
                    chatMsgModel.PictureUrl = "file://" + localFilePath;
                    chatMsgModel.remoteUrl = "";
                    entity.updateChatMsgAttachUrl(chatMsgModel);
                    entity.close();
                    HubNotificationService.thisServiceContext.publishMessageResults(chatMsgModel, AppEnum.MsgReceivedAttachment, false);
                    //  }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Log.i("ERROR ON DOWNLOADING FILES", "ERROR IS" +e);
                }

            }
        };
        dx.start();

    }


    class SendMessageThread implements Runnable {
        SendMsgModel msg = new SendMsgModel();
        ChatMsgModel chatMsgModel;

        public SendMessageThread(MsgModel msgModel, ChatMsgModel chatMsgModel) {
            SqlLiteDb entity = new SqlLiteDb(HubNotificationService.thisServiceContext);
            entity.open();
            msg.FromUser = entity.getUser();
            msg.Message = msgModel;
            entity.close();
            this.chatMsgModel = chatMsgModel;

        }

        @Override
        public void run() {
            new AsyncTask<Objects, Objects, Objects>() {
                @Override
                protected Objects doInBackground(Objects... params) {

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    final String query = gson.toJson(msg);
                    ChatMsgModel finalMsg = chatMsgModel;
                    int n = 0;
                    int resultCode = 0;
                    while (n < 2) {
                        AppResultModel response = APIHandler.createHttpPost
                                (ApplicationConstants.ServerAddress + "/api/Notifications/SendMessage",
                                        query, ApplicationConstants.contentTypeJson, 20000);
                        resultCode = response.ResultCode;
                        if (response.ResultCode == HttpURLConnection.HTTP_OK)//successful
                        {
                            finalMsg.IsSendDelv = AppEnum.SEND;
                            break;
                        }
                        n++;
                    }
                    if (n == 2 && resultCode != 200) {
                        finalMsg.IsSendDelv = AppEnum.UNDELIVERED;
                    }
                    SqlLiteDb entity = new SqlLiteDb(HubNotificationService.thisServiceContext);
                    entity.open();
                    entity.updateChatMsg(finalMsg);
                    entity.close();
                    HubNotificationService.thisServiceContext.publishMessageResults(finalMsg, AppEnum.MsgSendNotify, false);

                    return null;
                }
            }.execute(null, null, null);
        }
    }

    private class SendImageMessageThread implements Runnable {
        SendMsgModel msg = new SendMsgModel();
        ChatMsgModel chatMsgModel;

        public SendImageMessageThread(MsgModel msgModel, ChatMsgModel chatMsgModel) {

            try {
                Uri selectedImageUri = Uri.parse(msgModel.AttachmentUrl);
                Bitmap bmp = new UserPicture(selectedImageUri, thisServiceContext.getContentResolver()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                byte[] imageByteArray = stream.toByteArray();
                msgModel.Pic64Data = new ArrayList<String>();
                if (imageByteArray != null && imageByteArray.length > 0) {
                    String sb = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
                    int n = 0;
                    for (String str : sb.split("/")) {
                        msgModel.Pic64Data.add(str);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            SqlLiteDb entity = new SqlLiteDb(HubNotificationService.thisServiceContext);
            entity.open();
            msg.FromUser = entity.getUser();
            msg.Message = msgModel;
            entity.close();
            this.chatMsgModel = chatMsgModel;

        }

        @Override
        public void run() {
            new AsyncTask<Objects, Objects, Objects>() {
                @Override
                protected Objects doInBackground(Objects... params) {

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    final String query = gson.toJson(msg);
                    ChatMsgModel finalMsg = chatMsgModel;
                    int n = 0;
                    int resultCode = 0;
                    while (n < 3) {
                        AppResultModel response = APIHandler.createAttachmentHttpPost
                                (ApplicationConstants.ServerAddress + "/api/Notifications/SendMessage",
                                        query, ApplicationConstants.contentTypeJson, 20000);
                        resultCode = response.ResultCode;
                        if (response.ResultCode == HttpURLConnection.HTTP_OK)//successful
                        {
                            finalMsg.IsSendDelv = AppEnum.SEND;
                            break;
                        }
                        n++;
                    }
                    if (n == 3 && resultCode != 200) {
                        finalMsg.IsSendDelv = AppEnum.UNDELIVERED;
                    }
                    SqlLiteDb entity = new SqlLiteDb(HubNotificationService.thisServiceContext);
                    entity.open();
                    entity.updateChatMsg(finalMsg);
                    entity.close();
                    HubNotificationService.thisServiceContext.publishMessageResults(finalMsg, AppEnum.MsgSendNotify, false);

                    return null;
                }
            }.execute(null, null, null);
        }
    }
}

