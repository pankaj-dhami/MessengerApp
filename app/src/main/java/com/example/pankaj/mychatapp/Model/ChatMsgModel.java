package com.example.pankaj.mychatapp.Model;

import java.util.Date;
import java.util.Locale;

/**
 * Created by pankaj.dhami on 6/16/2015.
 */
public class ChatMsgModel {

    public int _id;
    public int UserID;
    public String Name ;
    public String MobileNo ;
    public String TextMessage ;
    public String PictureUrl ;
    public byte[] PicData ;
    public int IsMyMsg ;
    public int IsSendDelv;
    public String CreatedDate;
    public boolean left;

    public ChatMsgModel(boolean left, int userID, String name, String mobileNo, String textMessage, String pictureUrl, byte[] picData, int isMyMsg, int isSendDelv) {
        UserID = userID;
        Name = name;
        MobileNo = mobileNo;
        TextMessage = textMessage;
        PictureUrl = pictureUrl;
        PicData = picData;
        IsMyMsg = isMyMsg;
        IsSendDelv = isSendDelv;
        this.left=left;
    }

    public ChatMsgModel() {
    }
}
