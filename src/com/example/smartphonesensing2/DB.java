package com.example.smartphonesensing2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DB extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "accelerometer.db";
    
    // query statements to create table
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
    	    "CREATE TABLE " + ActivityTable.NAME + " (" +
    	    ActivityTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    	    ActivityTable.ID + TEXT_TYPE + COMMA_SEP +
    	    ActivityTable.X + TEXT_TYPE + COMMA_SEP +
    	    ActivityTable.Y + TEXT_TYPE + COMMA_SEP +
    	    ActivityTable.Z + TEXT_TYPE +
    	    " );";
    
    // query statement to delete entries
    private static final String SQL_DELETE_ENTRIES =
    	    "DROP TABLE IF EXISTS " + ActivityTable.NAME;


    
    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    
    /* Inner class that defines the table activity */
    public static abstract class ActivityTable implements BaseColumns {
        public static final String NAME = "activity";
        public static final String ID = "id";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String Z = "z";
        public static final String ACTIVITY = "activity";
    }
}
