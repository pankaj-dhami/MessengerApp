package com.example.pankaj.mychatapp;

/**
 * Created by pankaj on 6/8/2015.
 */

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pankaj.mychatapp.Model.ChatMsgModel;
import com.example.pankaj.mychatapp.Model.MsgModel;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.Utility.AppEnum;
import com.example.pankaj.mychatapp.Utility.ApplicationConstants;
import com.example.pankaj.mychatapp.Utility.HubNotificationService;
import com.example.pankaj.mychatapp.Utility.MyService;
import com.example.pankaj.mychatapp.Utility.SqlLiteDb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatBubbleActivity extends ActionBarActivity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    int userID;
    String mobile;
    UserModel thisChatUser;
    Intent intent;
    private boolean side = false;

    @Override
    public void onStart() {
        super.onStart();
        HubNotificationService.ChatBubbleActivity_active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        HubNotificationService.ChatBubbleActivity_active = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        thisChatUser = HubNotificationService.chatUser;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(thisChatUser.UserID);

        setContentView(R.layout.activity_chat);

        buttonSend = (Button) findViewById(R.id.buttonSend);

        listView = (ListView) findViewById(R.id.listViewChat);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_single_msg);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);
        chatText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        SqlLiteDb entity = new SqlLiteDb(this);
        entity.open();
        ArrayList<ChatMsgModel> msgList = entity.getChatMsgList(thisChatUser.UserID);
        entity.close();
        for (ChatMsgModel msg : msgList)
            chatArrayAdapter.add(msg);
    }


    private boolean sendChatMessage() {

        MsgModel msgModel = new MsgModel();
        msgModel.UserModel = thisChatUser;
        msgModel.TextMessage = chatText.getText().toString();
        ChatMsgModel chatMsgModel = new ChatMsgModel(false, 0, thisChatUser.UserID, thisChatUser.Name, thisChatUser.MobileNo
                , msgModel.TextMessage, msgModel.AttachmentUrl, msgModel.AttachmentData
                , AppEnum.SEND_BY_ME, AppEnum.Trying_SEND);

        SqlLiteDb entity = new SqlLiteDb(this);
        entity.open();
        chatMsgModel._id = entity.createChatMsgEntry(chatMsgModel);
        entity.close();
        chatArrayAdapter.add(chatMsgModel);
        HubNotificationService.thisServiceContext.sendMessageToUser(msgModel, chatMsgModel);
        chatText.setText("");
        return true;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String code = bundle.getString("code");
                if (TextUtils.equals(code, AppEnum.MsgReceivedNotify)) {
                    try {
                        String query = bundle.getString("message");
                        ChatMsgModel chatMsgModel = HubNotificationService.getChatModel(new JSONObject(query));
                        if (chatMsgModel.UserID == thisChatUser.UserID) {
                            chatArrayAdapter.add(chatMsgModel);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (TextUtils.equals(code, AppEnum.MsgSendNotify)) {
                    try {
                        String query = bundle.getString("message");
                        ChatMsgModel chatMsgModel = HubNotificationService.getChatModel(new JSONObject(query));
                        if (chatMsgModel.UserID == thisChatUser.UserID) {
                            ChatMsgModel existingItem=  chatArrayAdapter.getItemFromID(chatMsgModel._id);
                            existingItem.IsSendDelv=chatMsgModel.IsSendDelv;
                            chatArrayAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public void onResume() {
        MyService.ChatBubbleActivity_active = true;
        super.onResume();
        registerReceiver(receiver, new IntentFilter("com.example.pankaj.mychatapp"));
    }

    @Override
    public void onPause() {
        MyService.ChatBubbleActivity_active = false;
        super.onPause();
        unregisterReceiver(receiver);
    }
}

class ChatArrayAdapter extends ArrayAdapter {

    private TextView chatText;
    private List<ChatMsgModel> chatMessageList = new ArrayList<ChatMsgModel>();
    private LinearLayout singleMessageContainer;

    @Override
    public void clear() {
        chatMessageList.clear();
        super.clear();
    }

    public void add(ChatMsgModel object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatMsgModel getItemFromID(long _id) {
        ChatMsgModel result = null;
        for (ChatMsgModel item : chatMessageList) {
            if (item._id == _id) {
                result = item;
                break;
            }
        }
        return result;
    }

    @Override
    public int getPosition(Object item) {
        return super.getPosition(item);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMsgModel getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_single_msg, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMsgModel chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        String textMessage = chatMessageObj.TextMessage;

        if (chatMessageObj.IsMyMsg == AppEnum.SEND_BY_ME ) {

            switch (chatMessageObj.IsSendDelv)
            {
                case AppEnum.Trying_SEND :
                    textMessage += "\n ...";
                    break;
                case AppEnum.SEND :
                    textMessage += "\n +";
                    break;
                case AppEnum.DELIVERED :
                    textMessage += "\n ++";
                    break;
                case AppEnum.UNDELIVERED :
                    textMessage += "\n !!";
                    break;
            }
        }
        chatText.setText(textMessage);
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_b : R.drawable.bubble_a);
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}

class ChatMessage {
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
}