package com.example.smartphonesensing2.activity_monitoring;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.example.smartphonesensing2.R;
import com.example.smartphonesensing2.db.TestingTable;
import com.example.smartphonesensing2.db.TestingTable.TestingField;

public class ActivityMonitoringOverview extends ActionBarActivity {
	
	// Reference to testing table
	private TestingTable testingTable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_activity_monitoring_overview_layout);
	}
	
	
	/*
	 * This function calculates the percentage of activity
	 * 
	 * #activity_i/total_activity
	 * 
	 * 1) Fetch all activities
	 * 
	 * 2) Count each activity separately, Ai, where i = 1, 2, 3 representing activity still, walk, run 
	 * 
	 * 3) Count the total number of activities, TA
	 * 
	 * 4) Calculate the percentage of each activity, Ai/TA
	 * 
	 * 5) Show the results
	 * 
	 */
	private void calculateActivity() {
		
		// Textview to show the percentage of each activity
		TextView showCurrentActivity = (TextView) findViewById(R.id.overviewActivity);
		
		
		SQLiteDatabase db = testingTable.getReadableDatabase();
		
		
		String[] data = {
				TestingField.FIELD_ID,
				TestingField.FIELD_X,
				TestingField.FIELD_Y,
				TestingField.FIELD_Z
		};



		Cursor c = db.query(TestingField.TABLE_NAME,		// Name of the table 
				data, 								// Fields to be fetched
				null,								// where-clause
				null, 								// arguments for the where-clause
				null, 								// groupBy
				null, 								// having
				null								// orderBy
				);

		
		// Read the values in each field
		
		String dataID;
		String dataX;
		String dataY;
		String dataZ;
		
		if(c.moveToFirst()) {
//			showStoredCoordinates.setText("");
			
			do {
				dataID = c.getString(c.getColumnIndex(TestingField._ID));
				dataX = c.getString(c.getColumnIndex(TestingField.FIELD_X));
				dataY = c.getString(c.getColumnIndex(TestingField.FIELD_Y));
				dataZ = c.getString(c.getColumnIndex(TestingField.FIELD_Z));
				
				
				/*showStoredCoordinates.setText(showStoredCoordinates.getText()+
						dataID + ":"+
						" X: "+ dataX +
						" Y: "+ dataY +
						" Z: "+ dataZ +"\n"
						);*/
			} while(c.moveToNext());
		}
		
		db.close();
	}
}
