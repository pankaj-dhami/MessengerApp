package com.example.pankaj.mychatapp.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pankaj.mychatapp.Model.ChatMsgModel;
import com.example.pankaj.mychatapp.Model.UserModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * public static final String
 * Created by pankaj on 6/6/2015.
 */
public class SqlLiteDb {

    public SqlLiteDb(Context context) {
        this.context = context;
    }

    public SqlLiteDb open() {
        db_helper = new DBHelper(context);
        database = db_helper.getWritableDatabase();
        return SqlLiteDb.this;

    }

    public void close() {
        db_helper.close();
    }

    //region user data methods

    public void createUserEntry(UserModel userModel) {
        ContentValues cv = new ContentValues();
        cv.put("UserID", userModel.UserID);
        cv.put("Name", userModel.Name);
        cv.put("MobileNo", userModel.MobileNo);
        cv.put("MyStatus", userModel.MyStatus);
        cv.put("PictureUrl", userModel.PictureUrl);
        cv.put("PicData", userModel.PicData);
        cv.put("Password", userModel.Password);
        database.insert(DB_TABLE_USER, null, cv);
    }

    public UserModel getUser() {
        UserModel userModel = null;
        try {
            String[] columns = {ROW_ID, "UserID", "Name", "MobileNo", "MyStatus", "PictureUrl", "PicData", "Password"};
            Cursor cr = database.query(DB_TABLE_USER, columns, null, null, null, null, null);
            for (cr.moveToFirst(); !cr.isAfterLast(); cr.moveToNext()) {
                userModel = new UserModel();
                userModel._id = cr.getInt(cr.getColumnIndex(ROW_ID));
                userModel.UserID = cr.getInt(cr.getColumnIndex("UserID"));
                userModel.Name = cr.getString(cr.getColumnIndex("Name"));
                userModel.MobileNo = cr.getString(cr.getColumnIndex("MobileNo"));
                userModel.MyStatus = cr.getString(cr.getColumnIndex("MyStatus"));
                userModel.PictureUrl = cr.getString(cr.getColumnIndex("PictureUrl"));
                userModel.PicData = cr.getBlob(cr.getColumnIndex("PicData"));
                userModel.Password = cr.getString(cr.getColumnIndex("Password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userModel;
    }

    public void updateUser(UserModel userModel) {
        UserModel existingUser = getUser();
        if (existingUser != null) {
            ContentValues cv = new ContentValues();
            cv.put("UserID", userModel.UserID);
            cv.put("Name", userModel.Name);
            cv.put("MobileNo", userModel.MobileNo);
            cv.put("MyStatus", userModel.MyStatus);
            cv.put("PictureUrl", userModel.PictureUrl);
            cv.put("PicData", userModel.PicData);
            cv.put("Password", userModel.Password);
            database.update(DB_TABLE_USER, cv, ROW_ID + "=" + existingUser._id, null);
        } else {
            createUserEntry(userModel);
        }
    }

    public void deleteUser() {
        UserModel existingUser = getUser();
        database.delete(DB_TABLE_USER, ROW_ID + "" + existingUser._id, null);
    }

    //endregion

    //region Chat message curd operations
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public long createChatMsgEntry(ChatMsgModel userModel) {
        //    UserModel existingUser = getFriend(userModel.UserID);
        //    if (existingUser == null) {
        long id = 0;
        try {
            ContentValues cv = new ContentValues();
            cv.put("left", userModel.left == true ? 1 : 0);
            cv.put("UserID", userModel.UserID);
            cv.put("Name", userModel.Name);
            cv.put("MobileNo", userModel.MobileNo);
            cv.put("TextMessage", userModel.TextMessage);
            cv.put("PictureUrl", userModel.PictureUrl);
            cv.put("PicData", userModel.PicData);
            cv.put("IsMyMsg", userModel.IsMyMsg);
            cv.put("IsSendDelv", userModel.IsSendDelv);
            cv.put("CreatedDate", getDateTime());
            id = database.insert(DB_TABLE_CHAT_MSG, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   }
        return id;
    }

    public ArrayList<ChatMsgModel> getChatMsgList(int userID) {
        ArrayList<ChatMsgModel> lstUserModel = new ArrayList<ChatMsgModel>();
        try {
            String[] columns = {ROW_ID, "left", "UserID", "Name", "MobileNo", "TextMessage", "PictureUrl", "PicData", "IsMyMsg", "IsSendDelv", "CreatedDate"};
            Cursor cr = database.query(DB_TABLE_CHAT_MSG, columns, "UserID = " + userID, null, null, null, null);
            for (cr.moveToFirst(); !cr.isAfterLast(); cr.moveToNext()) {
                ChatMsgModel userModel = new ChatMsgModel();
                userModel._id = cr.getInt(cr.getColumnIndex(ROW_ID));
                userModel.left = cr.getInt(cr.getColumnIndex("left")) == 1 ? true : false;
                userModel.UserID = cr.getInt(cr.getColumnIndex("UserID"));
                userModel.Name = cr.getString(cr.getColumnIndex("Name"));
                userModel.MobileNo = cr.getString(cr.getColumnIndex("MobileNo"));
                userModel.TextMessage = cr.getString(cr.getColumnIndex("TextMessage"));
                userModel.PictureUrl = cr.getString(cr.getColumnIndex("PictureUrl"));
                userModel.PicData = cr.getBlob(cr.getColumnIndex("PicData"));
                userModel.IsMyMsg = cr.getInt(cr.getColumnIndex("IsMyMsg"));
                userModel.IsSendDelv = cr.getInt(cr.getColumnIndex("IsSendDelv"));
                userModel.CreatedDate = cr.getString(cr.getColumnIndex("CreatedDate"));
                lstUserModel.add(userModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstUserModel;
    }

    public ChatMsgModel getChatMsg(long _id) {
        ChatMsgModel userModel = null;
        try {
            String[] columns = {ROW_ID,"left", "UserID", "Name", "MobileNo", "TextMessage", "PictureUrl", "PicData", "IsMyMsg", "IsSendDelv", "CreatedDate"};
            Cursor cr = database.query(DB_TABLE_CHAT_MSG, columns, ROW_ID + " = " + _id, null, null, null, null);
            for (cr.moveToFirst(); !cr.isAfterLast(); cr.moveToNext()) {
                userModel = new ChatMsgModel();
                userModel._id = cr.getInt(cr.getColumnIndex(ROW_ID));
                userModel.left = cr.getInt(cr.getColumnIndex("left")) == 1 ? true : false;
                userModel.UserID = cr.getInt(cr.getColumnIndex("UserID"));
                userModel.Name = cr.getString(cr.getColumnIndex("Name"));
                userModel.MobileNo = cr.getString(cr.getColumnIndex("MobileNo"));
                userModel.TextMessage = cr.getString(cr.getColumnIndex("TextMessage"));
                userModel.PictureUrl = cr.getString(cr.getColumnIndex("PictureUrl"));
                userModel.PicData = cr.getBlob(cr.getColumnIndex("PicData"));
                userModel.IsMyMsg = cr.getInt(cr.getColumnIndex("IsMyMsg"));
                userModel.IsSendDelv = cr.getInt(cr.getColumnIndex("IsSendDelv"));
                userModel.CreatedDate = cr.getString(cr.getColumnIndex("CreatedDate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userModel;
    }

    public void updateChatMsg(ChatMsgModel chatMsgModel) {
        ChatMsgModel existingChat = getChatMsg(chatMsgModel._id);
        if (existingChat != null) {
            try {
                ContentValues cv = new ContentValues();
                cv.put("IsSendDelv", chatMsgModel.IsSendDelv);
                database.update(DB_TABLE_CHAT_MSG, cv, ROW_ID + " = " + existingChat._id, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //endregion

    //region friend curd operations

    public void createFriendsEntry(UserModel userModel) {
        //    UserModel existingUser = getFriend(userModel.UserID);
        //    if (existingUser == null) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("UserID", userModel.UserID);
            cv.put("Name", userModel.Name);
            cv.put("MobileNo", userModel.MobileNo);
            cv.put("MyStatus", userModel.MyStatus);
            cv.put("PictureUrl", userModel.PictureUrl);
            cv.put("PicData", userModel.PicData);
            database.insert(DB_TABLE_FRIENDS, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   }
    }

    public ArrayList<UserModel> getFriendsList() {
        ArrayList<UserModel> lstUserModel = new ArrayList<UserModel>();
        try {
            String[] columns = {"UserID", "Name", "MobileNo", "MyStatus", "PictureUrl", "PicData"};
            //  Cursor cr=database.rawQuery("Select * from "+ DB_TABLE_FRIENDS,null);
            Cursor cr = database.query(DB_TABLE_FRIENDS, columns, null, null, null, null, null);
            for (cr.moveToFirst(); !cr.isAfterLast(); cr.moveToNext()) {
                UserModel userModel = new UserModel();
                userModel.UserID = cr.getInt(cr.getColumnIndex("UserID"));
                userModel.Name = cr.getString(cr.getColumnIndex("Name"));
                userModel.MobileNo = cr.getString(cr.getColumnIndex("MobileNo"));
                userModel.MyStatus = cr.getString(cr.getColumnIndex("MyStatus"));
                userModel.PictureUrl = cr.getString(cr.getColumnIndex("PictureUrl"));
                userModel.PicData = cr.getBlob(cr.getColumnIndex("PicData"));
                lstUserModel.add(userModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstUserModel;
    }

    public UserModel getFriend(int userID) {
        UserModel userModel = null;
        try {
            String[] columns = {"UserID", "Name", "MobileNo", "MyStatus", "PictureUrl", "PicData"};
            Cursor cr = database.query(DB_TABLE_FRIENDS, columns, "UserID = " + userID, null, null, null, null);
            for (cr.moveToFirst(); !cr.isAfterLast(); cr.moveToNext()) {
                userModel = new UserModel();
                userModel.UserID = cr.getInt(cr.getColumnIndex("UserID"));
                userModel.Name = cr.getString(cr.getColumnIndex("Name"));
                userModel.MobileNo = cr.getString(cr.getColumnIndex("MobileNo"));
                userModel.MyStatus = cr.getString(cr.getColumnIndex("MyStatus"));
                userModel.PictureUrl = cr.getString(cr.getColumnIndex("PictureUrl"));
                userModel.PicData = cr.getBlob(cr.getColumnIndex("PicData"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userModel;
    }

    public void updateFriends(UserModel userModel) {
        UserModel existingUser = getFriend(userModel.UserID);
        if (existingUser != null) {
            try {
                ContentValues cv = new ContentValues();
                cv.put("UserID", userModel.UserID);
                cv.put("Name", userModel.Name);
                cv.put("MobileNo", userModel.MobileNo);
                cv.put("MyStatus", userModel.MyStatus);
                cv.put("PictureUrl", userModel.PictureUrl);
                cv.put("PicData", userModel.PicData);
                database.update(DB_TABLE_FRIENDS, cv, "UserID = " + userModel.UserID, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            createFriendsEntry(userModel);
        }
    }

    //endregion


    public static final String ROW_ID = "_id";
    public static final String Name = "friend_name";
    public static final String MOBILENO = "mobile_no";
    public static final String PICTURE_DATA = "pic_data";

    public static final String DB_NAME = "db_messenger_4";
    public static final String DB_TABLE_FRIENDS = "table_friends";
    public static final String DB_TABLE_CHAT_MSG = "table_chat_messages";
    public static final String DB_TABLE_USER = "table_user";

    public static final int DB_VERSION = 3;

    private DBHelper db_helper;
    private Context context;
    private SQLiteDatabase database;

    public static class DBHelper extends SQLiteOpenHelper {
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + DB_TABLE_USER + " ("
                            + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + " UserID  INTEGER NOT NULL, "
                            + " Name TEXT NULL ,"
                            + " MobileNo TEXT NOT NULL ,"
                            + " MyStatus TEXT  NULL ,"
                            + " PictureUrl TEXT  NULL ,"
                            + " PicData BLOB ,"
                            + " Password TEXT  NULL );"

            );
            db.execSQL(
                    "CREATE TABLE " + DB_TABLE_FRIENDS + " ("
                            + " UserID  INTEGER PRIMARY KEY NOT NULL, "
                            + " Name TEXT NULL ,"
                            + " MobileNo TEXT NOT NULL ,"
                            + " MyStatus TEXT  NULL ,"
                            + " PictureUrl TEXT  NULL ,"
                            + " PicData BLOB );"

            );

            db.execSQL(
                    "CREATE TABLE " + DB_TABLE_CHAT_MSG + " ("
                            + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + " left  INTEGER  NOT NULL, "
                            + " UserID  INTEGER  NOT NULL, "
                            + " Name TEXT NULL ,"
                            + " MobileNo TEXT NOT NULL ,"
                            + " TextMessage TEXT  NULL ,"
                            + " PictureUrl TEXT  NULL ,"
                            + " PicData BLOB ,"
                            + " IsMyMsg INTEGER ,"
                            + " IsSendDelv INTEGER ,"
                            + " CreatedDate TEXT );"

            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE " + DB_TABLE_USER);
            db.execSQL("DROP TABLE " + DB_TABLE_FRIENDS);
             db.execSQL("DROP TABLE " + DB_TABLE_CHAT_MSG );
            onCreate(db);
        }

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

    }
}
