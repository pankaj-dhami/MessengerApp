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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pankaj.mychatapp.CustomUI.RoundedImageView;
import com.example.pankaj.mychatapp.Model.ChatMsgModel;
import com.example.pankaj.mychatapp.Model.MsgModel;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.Utility.AppEnum;
import com.example.pankaj.mychatapp.Utility.Common;
import com.example.pankaj.mychatapp.Utility.HubNotificationService;
import com.example.pankaj.mychatapp.Utility.SqlLiteDb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatBubbleActivity extends ActionBarActivity implements ActionMode.Callback {
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

    ArrayList<ChatMsgModel> selectedItems = new ArrayList<ChatMsgModel>();
    boolean toggleSelection;
    protected ActionMode mActionMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        thisChatUser = HubNotificationService.chatUser;
        setTitle(thisChatUser.Name);
        ActionBar actionBar = getSupportActionBar();
        View row = null;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_actionbar_customview,null, false);
        }
        RoundedImageView chatUserImage = (RoundedImageView) row.findViewById(R.id.chatUserImage);
        TextView  txtUserName = (TextView) row.findViewById(R.id.txtUserName);
        TextView txtOnlineStatus = (TextView) row.findViewById(R.id.txtOnlineStatus);
        if (thisChatUser.PicData!=null && thisChatUser.PicData.length >3) {
            try {
                chatUserImage.setImageBitmap(BitmapFactory.decodeByteArray(thisChatUser.PicData, 0, thisChatUser.PicData.length));
            } catch (Exception e) {
                chatUserImage.setImageResource(R.drawable.nouser);
            }
        }
        txtUserName.setText(thisChatUser.Name);
        txtOnlineStatus.setText(thisChatUser.MobileNo);
        actionBar.setCustomView(row);
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

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                toggleSelection = true;
                ChatMsgModel item = getSelectedItem(chatArrayAdapter.getItem(position)._id);
                if (item != null && selectedItems.contains(item)) {
                    chatArrayAdapter.getItem(position).isChecked = false;
                    selectedItems.remove(item);
                } else {
                    chatArrayAdapter.getItem(position).isChecked = true;
                    selectedItems.add(chatArrayAdapter.getItem(position));
                    if (mActionMode==null) {
                        mActionMode = ChatBubbleActivity.this.startActionMode(ChatBubbleActivity.this);
                        getSupportActionBar().hide();
                    }

                }
                if (selectedItems.isEmpty()) {
                    if(mActionMode!=null) {
                        mActionMode.finish();
                        mActionMode = null;
                    }
                    toggleSelection = false;
                }
                chatArrayAdapter.notifyDataSetChanged();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (toggleSelection) {
                    ChatMsgModel item = getSelectedItem(chatArrayAdapter.getItem(position)._id);
                    if (item != null && selectedItems.contains(item)) {
                        chatArrayAdapter.getItem(position).isChecked = false;
                        selectedItems.remove(item);

                    } else {
                        chatArrayAdapter.getItem(position).isChecked = true;
                        selectedItems.add(chatArrayAdapter.getItem(position));
                        if (mActionMode==null) {
                            mActionMode = ChatBubbleActivity.this.startActionMode(ChatBubbleActivity.this);
                            getSupportActionBar().hide();
                        }
                    }

                }
                if (selectedItems.isEmpty()) {
                    if(mActionMode!=null) {
                        mActionMode.finish();
                        mActionMode = null;
                    }
                    toggleSelection = false;
                }
                chatArrayAdapter.notifyDataSetChanged();
            }
        });

        //  registerForContextMenu(listView);


    }

    public ChatMsgModel getSelectedItem(long _id) {
        for (ChatMsgModel item : selectedItems) {
            if (item._id == _id)
                return item;
        }

        return null;
    }

    //region send receive methods
    private boolean sendChatMessage() {

        MsgModel msgModel = new MsgModel();
        msgModel.UserModel = thisChatUser;
        msgModel.TextMessage = chatText.getText().toString();
        ChatMsgModel chatMsgModel = new ChatMsgModel(false, 0, thisChatUser.UserID, thisChatUser.Name, thisChatUser.MobileNo
                , msgModel.TextMessage, msgModel.AttachmentUrl, msgModel.AttachmentData
                , AppEnum.SEND_BY_ME, AppEnum.Trying_SEND);
        chatMsgModel.CreatedDate = Common.getDateTime();
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
                            ChatMsgModel existingItem = chatArrayAdapter.getItemFromID(chatMsgModel._id);
                            existingItem.IsSendDelv = chatMsgModel.IsSendDelv;
                            chatArrayAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    //endregion


    //region show menu on long select code
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewChat) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(chatArrayAdapter.getItem(info.position).TextMessage);
            String[] menuItems = getResources().getStringArray(R.array.menu_demo);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu_demo);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = chatArrayAdapter.getItem(info.position).TextMessage;

        Toast.makeText(ChatBubbleActivity.this, String.format("Selected %s for item %s", menuItemName, listItemName), Toast.LENGTH_LONG).show();
        //  TextView text = (TextView)findViewById(R.id.footer);
        //  text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
    }
    //endregion

    //region on start and stop override
    @Override
    public void onStart() {
        super.onStart();
        HubNotificationService.ChatBubbleActivity_active = true;
        registerReceiver(receiver, new IntentFilter("com.example.pankaj.mychatapp"));
    }

    @Override
    public void onStop() {
        super.onStop();
        HubNotificationService.ChatBubbleActivity_active = false;
        unregisterReceiver(receiver);
    }
    //endregion


    //region startActionMode methods
    @Override
    // Called when the action mode is created; startActionMode() was called
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        // Assumes that you have "contexual.xml" menu resources
        inflater.inflate(R.menu.menu_home, menu);

        return true;
    }

    // Called each time the action mode is shown. Always called after
    // onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_speech:
                // show();
                // Action picked, so close the CAB
                mode.finish();
                return true;
            default:
                return false;
        }

    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        getSupportActionBar().show();
        for (ChatMsgModel item:selectedItems)
        {
            item.isChecked=false;
        }
        selectedItems.clear();
        chatArrayAdapter.notifyDataSetChanged();
        mActionMode = null;
    }
    //endregion
}