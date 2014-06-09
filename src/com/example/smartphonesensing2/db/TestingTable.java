package com.example.smartphonesensing2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class TestingTable extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "accelerometer.db2";
    
    // query statements to create table
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    
    public static final String SQL_CREATE_TABLE =		
    	    "CREATE TABLE " + TestingField.TABLE_NAME + " (" +
    	    TestingField.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    	    TestingField.FIELD_X + TEXT_TYPE + COMMA_SEP +
    	    TestingField.FIELD_Y + TEXT_TYPE + COMMA_SEP +
    	    TestingField.FIELD_Z + TEXT_TYPE + COMMA_SEP +
    	    TestingField.FIELD_ACTIVITY + TEXT_TYPE +
    	    " );";
    
    // query statement to delete table
    private static final String SQL_DELETE_TABLE =
    	    "DROP TABLE IF EXISTS " + TestingField.TABLE_NAME;
	
	
	public TestingTable(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// 
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
	}
	
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
	
	
	/*Table 2 to store new records during testing*/
    public static abstract class TestingField implements BaseColumns {
        public static final String TABLE_NAME = "test";
        public static final String FIELD_ID = "_id";
        public static final String FIELD_X = "x";
        public static final String FIELD_Y = "y";
        public static final String FIELD_Z = "z";
        public static final String FIELD_ACTIVITY = "Activity";
    }

}
