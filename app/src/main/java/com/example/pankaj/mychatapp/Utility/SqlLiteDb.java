package com.example.pankaj.mychatapp.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * public static final String
 * Created by pankaj on 6/6/2015.
 */
public class SqlLiteDb {

    public SqlLiteDb(Context context) {
        this.context = context;
    }

    public SqlLiteDb open()
    {
        db_helper=new DBHelper(context);
        database=db_helper.getWritableDatabase();
        return SqlLiteDb.this;

    }
    public void close()
    {
        db_helper.close();
    }

    public void createEntry()
    {
        ContentValues cv=new ContentValues();
        cv.put(Name,"");
        database.insert(DB_TABLE_FRIENDS,null,cv);
    }
    public static final String ROW_ID = "_id";
    public static final String Name = "friend_name";
    public static final String MOBILENO = "mobile_no";
    public static final String PICTURE_DATA = "pic_data";

    public static final String DB_NAME = "db_messenger";
    public static final String DB_TABLE_FRIENDS = "table_friends";
    public static final int DB_VERSION = 1;

    private DBHelper db_helper;
    private Context context;
    private SQLiteDatabase database;

    private static class DBHelper extends SQLiteOpenHelper {
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(
                    "CREATE TABLE " + DB_NAME + " ("
                            + ROW_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + Name + "TEXT NULL ,"
                            + MOBILENO + "TEXT NOT NULL ,"
                            + PICTURE_DATA + "BLOB );"


            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXIST "+DB_NAME);
            onCreate(db);
        }

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

    }
}
