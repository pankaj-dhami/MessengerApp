package com.example.pankaj.mychatapp.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pankaj.mychatapp.Model.UserModel;

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
        UserModel userModel=null;
        try {
            String[] columns = {ROW_ID, "UserID", "Name", "MobileNo", "MyStatus", "PictureUrl", "PicData","Password"};
            Cursor cr = database.query(DB_TABLE_USER, columns, null, null, null, null, null);
            for (  cr.moveToFirst(); !cr.isAfterLast(); cr.moveToNext()) {
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
        if (existingUser!=null) {
            ContentValues cv = new ContentValues();
            cv.put("UserID", userModel.UserID);
            cv.put("Name", userModel.Name);
            cv.put("MobileNo", userModel.MobileNo);
            cv.put("MyStatus", userModel.MyStatus);
            cv.put("PictureUrl", userModel.PictureUrl);
            cv.put("PicData", userModel.PicData);
            cv.put("Password", userModel.Password);
            database.update(DB_TABLE_USER, cv, ROW_ID + "=" + existingUser._id, null);
        }
        else {
            createUserEntry(userModel);
        }
    }

    public void deleteUser() {
        UserModel existingUser = getUser();
        database.delete(DB_TABLE_USER, ROW_ID + "" + existingUser._id, null);
    }

    public static final String ROW_ID = "_id";
    public static final String Name = "friend_name";
    public static final String MOBILENO = "mobile_no";
    public static final String PICTURE_DATA = "pic_data";

    public static final String DB_NAME = "db_messenger_1";
    public static final String DB_TABLE_FRIENDS = "table_friends";
    public static final String DB_TABLE_USER = "table_user";

    public static final int DB_VERSION = 1;

    private DBHelper db_helper;
    private Context context;
    private SQLiteDatabase database;

    public static class DBHelper extends SQLiteOpenHelper {
        @Override
        public void onCreate(SQLiteDatabase db) {

           /* db.execSQL(
                    "CREATE TABLE " + DB_TABLE_FRIENDS + " ("
                            + ROW_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + Name + "TEXT NULL ,"
                            + MOBILENO + "TEXT NOT NULL ,"
                            + PICTURE_DATA + "BLOB );"

            );*/
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

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXIST " + DB_TABLE_USER);
            onCreate(db);
        }

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

    }
}
