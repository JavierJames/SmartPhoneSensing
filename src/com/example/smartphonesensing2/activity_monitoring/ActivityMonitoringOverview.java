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
	
	// confusion of each activity
	private int[] confusion_still;
	private int[] confusion_walk;
	private int[] confusion_run;
	private int[] confusion_jump;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_activity_monitoring_overview_layout);
		
		Bundle extras = getIntent().getExtras();
		
		activities = extras.getStringArray("activities");
		
		confusion_still = extras.getIntArray("confusion_still");
		confusion_walk = extras.getIntArray("confusion_walk");
		confusion_run = extras.getIntArray("confusion_run");
		confusion_jump = extras.getIntArray("confusion_jump");
		
		calculateActivity();
		
		createConfusionMatrix();
	}
	
	
	/*
	 * This method creates the confusion matrix
	 */
	private void createConfusionMatrix() {
		TextView stillvsStill = (TextView) findViewById(R.id.stillvsStill);
		TextView stillvsWalk = (TextView) findViewById(R.id.stillvsWalk);
		TextView stillvsRun = (TextView) findViewById(R.id.stillvsRun);
		TextView stillvsJump = (TextView) findViewById(R.id.stillvsJump);
		
		TextView walkvsStill = (TextView) findViewById(R.id.walkvsStill);
		TextView walkvsWalk = (TextView) findViewById(R.id.walkvsWalk);
		TextView walkvsRun = (TextView) findViewById(R.id.walkvsRun);
		TextView walkvsJump = (TextView) findViewById(R.id.walkvsJump);
		
		TextView runvsStill = (TextView) findViewById(R.id.runvsStill);
		TextView runvsWalk = (TextView) findViewById(R.id.runvsWalk);
		TextView runvsRun = (TextView) findViewById(R.id.runvsRun);
		TextView runvsJump = (TextView) findViewById(R.id.runvsJump);
		
		TextView jumpvsStill = (TextView) findViewById(R.id.jumpvsStill);
		TextView jumpvsWalk = (TextView) findViewById(R.id.jumpvsWalk);
		TextView jumpvsRun = (TextView) findViewById(R.id.jumpvsRun);
		TextView jumpvsJump = (TextView) findViewById(R.id.jumpvsJump);
		
		int total_still = 0;
		int total_walk = 0;
		int total_run = 0;
		int total_jump = 0;
		
		
		// There are 4 fixed activities: still, walk, run, jump
		for(int i = 0; i < 4; i++) {
			total_still += confusion_still[i];
			total_walk += confusion_walk[i];
			total_run += confusion_run[i];
			total_jump += confusion_jump[i];
		}
		
		// calculate percentage
		float stillvsStill_percentage = (float) (confusion_still[0]) / total_still;
		float stillvsWalk_percentage = (float) (confusion_still[1]) / total_still;
		float stillvsRun_percentage = (float) (confusion_still[2]) / total_still;
		float stillvsJump_percentage = (float) (confusion_still[3]) / total_still;
		
		float walkvsStill_percentage = (float) (confusion_walk[0]) / total_walk;
		float walkvsWalk_percentage = (float) (confusion_walk[1]) / total_walk;
		float walkvsRun_percentage = (float) (confusion_walk[2]) / total_walk;
		float walkvsJump_percentage = (float) (confusion_walk[3]) / total_walk;
		
		float runvsStill_percentage = (float)(confusion_run[0]) / total_run;
		float runvsWalk_percentage = (float) (confusion_run[1]) / total_run;
		float runvsRun_percentage = (float) (confusion_run[2]) / total_run;
		float runvsJump_percentage = (float) (confusion_run[3]) / total_run;
		
		float jumpvsStill_percentage = (float) (confusion_jump[0]) / total_jump;
		float jumpvsWalk_percentage = (float) (confusion_jump[1]) / total_jump;
		float jumpvsRun_percentage = (float) (confusion_jump[2]) / total_jump;
		float jumpvsJump_percentage = (float) (confusion_jump[3]) / total_jump;
		
		
		stillvsStill.setText(String.format("%.2f", stillvsStill_percentage));
		stillvsWalk.setText(String.format("%.2f", stillvsWalk_percentage));
		stillvsRun.setText(String.format("%.2f", stillvsRun_percentage));
		stillvsJump.setText(String.format("%.2f", stillvsJump_percentage));
		
		walkvsStill.setText(String.format("%.2f", walkvsStill_percentage));
		walkvsWalk.setText(String.format("%.2f", walkvsWalk_percentage));
		walkvsRun.setText(String.format("%.2f", walkvsRun_percentage));
		walkvsJump.setText(String.format("%.2f", walkvsJump_percentage));
		
		runvsStill.setText(String.format("%.2f", runvsStill_percentage));
		runvsWalk.setText(String.format("%.2f", runvsWalk_percentage));
		runvsRun.setText(String.format("%.2f", runvsRun_percentage));
		runvsJump.setText(String.format("%.2f", runvsJump_percentage));
		
		jumpvsStill.setText(String.format("%.2f", jumpvsStill_percentage));
		jumpvsWalk.setText(String.format("%.2f", jumpvsWalk_percentage));
		jumpvsRun.setText(String.format("%.2f", jumpvsRun_percentage));
		jumpvsJump.setText(String.format("%.2f", jumpvsJump_percentage));
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
		int count_jump = 0;
		int count_total = activities.length;
		
		
		// 1) Fetch all activities
		
		for(int i = 0; i < activities.length; i++) {
			
			// 2) Count each activity separately, C(Ai), where i = 1, 2, 3, 4 representing activity still, walk, run, jump
			
			if(activities[i].equalsIgnoreCase("still")) {
				count_still++;
			}
			else if(activities[i].equalsIgnoreCase("walk")) {
				count_walk++;
			}
			else if(activities[i].equalsIgnoreCase("run")) {
				count_run++;
			}
			else if(activities[i].equalsIgnoreCase("jump")) {
				count_jump++;
			}
			
			// 3) Count the total number of activities, TA
//			count_total++;
		}
		
		
		
		// 4) Calculate the percentage of each activity, Ai/TA
		
		float still_per = ((float)count_still/count_total)*100;
		float walk_per = ((float)count_walk/count_total)*100;
		float run_per = ((float)count_run/count_total)*100;
		float jump_per = ((float)count_jump/count_total)*100;
		
		// 5) Show the results
		
		showCurrentActivity.setText(showCurrentActivity.getText()+
				" Still: "+ still_per +"%\n"+
				" Walk: "+ walk_per +"%\n"+
				" Run: "+ run_per +"%\n"+
				" Jump: "+ jump_per +"%\n"
				);
	}
}
