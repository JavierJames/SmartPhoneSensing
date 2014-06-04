package com.example.smartphonesensing2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartphonesensing2.activity_monitoring.ActivityMonitoring;
import com.example.smartphonesensing2.localization.Localization;
/* Database libraries */
/* KNN Classification libraries */
 

public class MainActivity extends ActionBarActivity {
	
	private final static String MESSAGE_ACTIVITY = "activity";
	
	private final static String MESSAGE_LOCALISATION = "localisation";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		} 
	}

	
	protected void onResume(){
		super.onResume();
	}
	
	
	protected void onPause(){
		super.onPause();
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	
	/*
	 * This function opens the activity monitoring activity
	 */
	public void activityMonitoring(View view) {
		Intent intent = new Intent(getApplicationContext(), ActivityMonitoring.class);
		 
		
//		 intent.putExtra(MESSAGE_ACTIVITY, true);
		 startActivity(intent);
	}
	
	
	/*
	 * This function opens the indoor localisation activity
	 */
	public void indoorLocalisation(View view) {
		Intent intent = new Intent(getApplicationContext(), Localization.class);
		 
//		 intent.putExtra(MESSAGE_LOCALISATION, true);
		 startActivity(intent);
	}
}
