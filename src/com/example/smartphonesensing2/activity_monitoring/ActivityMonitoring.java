package com.example.smartphonesensing2.activity_monitoring;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.smartphonesensing2.R;

public class ActivityMonitoring extends ActionBarActivity {

	private TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_activity_monitoring_layout);
		
		
		// Create a tabhost that will contain the tabs
		tabHost = (TabHost)findViewById(R.id.tabhost);
//		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		tabHost.setup();

		
		// Create the tabs
		TabSpec trainTab = tabHost.newTabSpec("Training");
		TabSpec testTab = tabHost.newTabSpec("Testing");
		
		
		// Set the tabname and corresponding Activity that will be opened when particular Tab will be selected
		
		trainTab.setContent(R.id.training_tab);
		trainTab.setIndicator("Training");
		
		/*Intent intentTrain = new Intent(this, TrainActivity.class);
		trainTab.setContent(intentTrain);*/
		
		
		testTab.setContent(R.id.testing_tab);
		testTab.setIndicator("Testing");
		/*Intent intentTest = new Intent(this, TestActivity.class);
		testTab.setContent(intentTest);*/
		
		// Add tabs to the tabhost
		tabHost.addTab(trainTab);
		tabHost.addTab(testTab);
		
//		tabHost.setCurrentTab(0);
		 
	}
//
//	
//	protected void onResume(){
//		super.onResume();
//	}
//	
//	protected void onPause(){
//		
//		super.onPause();
//		
//		
//	}
//	
//	
//	@Override
//	public void onDestroy(){
//		super.onDestroy();
//	}
//	
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	/**
//	 * A placeholder fragment containing a simple view.
//	 */
//	public static class PlaceholderFragment extends Fragment {
//
//		public PlaceholderFragment() {
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_main, container,
//					false);
//			return rootView;
//		}
//	}
//
//	
//	// show the highest row number 
//	/*public void onClick(View view) {
//		TextView debug = (TextView) findViewById(R.id.debugView);
//		debug.setText("rowId: " +rowId);
//	}*/
//	 
//
//	 /* Checks if external storage is available to at least read */
//	 public boolean isExternalStorageReadable() {
//	     String state = Environment.getExternalStorageState();
//	     if (Environment.MEDIA_MOUNTED.equals(state) ||
//	         Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//	         return true;
//	     }
//	     return false;
//	 }
//	 
	 
	 /*public void rssi(View view) {
		 Intent intent = new Intent(getApplicationContext(), Localization.class);
		 
		 intent.putExtra("rssi", true);
		 startActivity(intent);
		 
	 }*/
}
