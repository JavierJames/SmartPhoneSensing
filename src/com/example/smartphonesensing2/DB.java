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
    	    "CREATE TABLE " + ActivityTable.TABLE_NAME + " (" +
    	    ActivityTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    	    ActivityTable.FIELD_ID + TEXT_TYPE + COMMA_SEP +
    	    ActivityTable.FIELD_X + TEXT_TYPE + COMMA_SEP +
    	    ActivityTable.FIELD_Y + TEXT_TYPE + COMMA_SEP +
    	    ActivityTable.FIELD_Z + TEXT_TYPE + COMMA_SEP +
    	    ActivityTable.FIELD_ACTIVITY + TEXT_TYPE +
    	    " );";
    
    // query statement to delete entries
    private static final String SQL_DELETE_ENTRIES =
    	    "DROP TABLE IF EXISTS " + ActivityTable.TABLE_NAME;


    
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
        public static final String TABLE_NAME = "activity";
        public static final String FIELD_ID = "id";
        public static final String FIELD_X = "x";
        public static final String FIELD_Y = "y";
        public static final String FIELD_Z = "z";
        public static final String FIELD_ACTIVITY = "activity";
    }
}
