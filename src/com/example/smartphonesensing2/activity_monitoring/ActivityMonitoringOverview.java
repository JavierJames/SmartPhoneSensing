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
	
	// Array of classified activities
	private String[] activities;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_activity_monitoring_overview_layout);
		
		Bundle extras = getIntent().getExtras();
		
		activities = extras.getStringArray("activities");
		
		calculateActivity();
	}
	
	
	/*
	 * This function calculates the percentage of activity
	 * 
	 * #activity_i/total_activity
	 * 
	 * 1) Fetch all activities
	 * 
	 * 2) Count each activity separately, C(Ai), where i = 1, 2, 3 representing activity still, walk, run 
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
		
		int count_still = 0;
		int count_walk = 0;
		int count_run = 0;
		int count_total = 0;
		
		
		// 1) Fetch all activities
		
		for(int i = 0; i < activities.length; i++) {
			
			// 2) Count each activity separately, C(Ai), where i = 1, 2, 3 representing activity still, walk, run
			
			if(activities[i].equalsIgnoreCase("still")) {
				count_still++;
			}
			else if(activities[i].equalsIgnoreCase("walk")) {
				count_walk++;
			}
			else if(activities[i].equalsIgnoreCase("run")) {
				count_run++;
			}
			
			// 3) Count the total number of activities, TA
			count_total++;
		}
		
		
		
		
		
/*		
		
		// 1) Fetch all activities
		
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
		String activity;
		
		int count_still = 0;
		int count_walk = 0;
		int count_run = 0;
		int count_total = 0;
		
		
		
		if(c.moveToFirst()) {
			showCurrentActivity.setText("");2) Count each activity separately, C(Ai), where i = 1, 2, 3 representing activity still, walk, run
			
			do {
				dataID = c.getString(c.getColumnIndex(TestingField._ID));
				dataX = c.getString(c.getColumnIndex(TestingField.FIELD_X));
				dataY = c.getString(c.getColumnIndex(TestingField.FIELD_Y));
				dataZ = c.getString(c.getColumnIndex(TestingField.FIELD_Z));
				activity = c.getString(c.getColumnIndex(TestingField.FIELD_ACTIVITY));
				
				
				// 2) Count each activity separately, C(Ai), where i = 1, 2, 3 representing activity still, walk, run
				
				if(activity.equalsIgnoreCase("still")) {
					count_still++;
				}
				else if(activity.equalsIgnoreCase("walk")) {
					count_walk++;
				}
				else if(activity.equalsIgnoreCase("run")) {
					count_run++;
				}
				
				// 3) Count the total number of activities, TA
				count_total++;
				
				
			} while(c.moveToNext());
		}
		db.close();
*/		
		
		// 4) Calculate the percentage of each activity, Ai/TA
		
		float still_per = ((float)count_still/count_total)*100;
		float walk_per = ((float)count_walk/count_total)*100;
		float run_per = ((float)count_run/count_total)*100;
		
		
		// 5) Show the results
		
		showCurrentActivity.setText(showCurrentActivity.getText()+
				" Still: "+ still_per +"%\n"+
				" Walk: "+ walk_per +"%\n"+
				" Run: "+ run_per +"%\n"
				);
	}
}
