package com.example.pankaj.mychatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pankaj.mychatapp.Model.ChatMsgModel;
import com.example.pankaj.mychatapp.Utility.AppEnum;
import com.example.pankaj.mychatapp.Utility.Common;
import com.example.pankaj.mychatapp.Utility.HubNotificationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pankaj.dhami on 6/19/2015.
 */


public class ChatArrayAdapter extends ArrayAdapter {

    private TextView chatText;
    private TextView infoText;
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
        infoText = (TextView) row.findViewById(R.id.txtInfo);

        String textMessage = chatMessageObj.TextMessage;
        String infoTextStr = " " + Common.getTime(chatMessageObj.CreatedDate);
        if (chatMessageObj.IsMyMsg == AppEnum.SEND_BY_ME) {
            infoTextStr = " " + Common.getTime(chatMessageObj.CreatedDate);
            String tick = HubNotificationService.thisServiceContext.getResources().getString(R.string.tickMark);
            switch (chatMessageObj.IsSendDelv) {
                case AppEnum.Trying_SEND:
                    textMessage += "  ...";
                    break;
                case AppEnum.SEND:
                    textMessage += "  " + tick;
                    break;
                case AppEnum.DELIVERED:
                    textMessage += "  " + tick + tick;
                    break;
                case AppEnum.UNDELIVERED:
                    textMessage += "  !!";
                    break;
            }
        }
        if (chatMessageObj.isChecked) {
            singleMessageContainer.setBackgroundColor(Color.parseColor("#C4C4C4"));
        } else {
            singleMessageContainer.setBackgroundColor(Color.parseColor("#00000000"));
        }
        chatText.setText(textMessage);
        infoText.setText(infoTextStr);
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.out_message_bg : R.drawable.in_message_bg);
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}