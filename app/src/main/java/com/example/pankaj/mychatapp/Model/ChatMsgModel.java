package com.example.pankaj.mychatapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pankaj.dhami on 6/16/2015.
 */
public class ChatMsgModel implements Parcelable {

    public long _id;
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

    public ChatMsgModel(boolean left,long _id, int userID, String name, String mobileNo, String textMessage, String pictureUrl, byte[] picData, int isMyMsg, int isSendDelv) {
        this. _id=_id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeInt(UserID);
        dest.writeString(Name);
        dest.writeString(MobileNo);
        dest.writeString(TextMessage);
        dest.writeString(PictureUrl);
        dest.writeByteArray(PicData);
        dest.writeString(CreatedDate);
        dest.writeInt(IsMyMsg);
        dest.writeInt(IsSendDelv);

    }
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<ChatMsgModel> CREATOR = new Parcelable.Creator<ChatMsgModel>() {
        public ChatMsgModel createFromParcel(Parcel in) {
            ChatMsgModel model=   new ChatMsgModel();
            model. _id=in.readLong();
            model. Name=in.readString();
            model. MobileNo=in.readString();
            model.TextMessage=in.readString();
            model.PictureUrl=in.readString();
            model.CreatedDate=in.readString();
            in.readByteArray(model.PicData);
            model.IsMyMsg = in.readInt();
            model. IsSendDelv = in.readInt();
            model. UserID = in.readInt();
            return model;
        }

        public ChatMsgModel[] newArray(int size) {
            return new ChatMsgModel[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private ChatMsgModel(Parcel in) {
        _id=in.readLong();
        UserID = in.readInt();
        Name=in.readString();
        MobileNo=in.readString();
        TextMessage=in.readString();
        PictureUrl=in.readString();
        CreatedDate=in.readString();
        in.readByteArray(PicData);
        IsMyMsg = in.readInt();
        IsSendDelv = in.readInt();
    }
}

class MyParcelable implements Parcelable {
    private int mData;

    /* everything below here is for implementing Parcelable */

    // 99.9% of the time you can just ignore this
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<MyParcelable> CREATOR = new Parcelable.Creator<MyParcelable>() {
        public MyParcelable createFromParcel(Parcel in) {
            return new MyParcelable(in);
        }

        public MyParcelable[] newArray(int size) {
            return new MyParcelable[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private MyParcelable(Parcel in) {
        mData = in.readInt();
    }
}