package com.example.smartphonesensing2.activity_monitoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.example.smartphonesensing2.R;
import com.example.smartphonesensing2.KnnClassification.ArrayBuff;
import com.example.smartphonesensing2.KnnClassification.Knn_API;
import com.example.smartphonesensing2.db.TestingTable;
import com.example.smartphonesensing2.db.TestingTable.TestingField;
import com.example.smartphonesensing2.db.TrainingTable;
import com.example.smartphonesensing2.db.TrainingTable.TrainingField;

public class ActivityMonitoring extends ActionBarActivity implements SensorEventListener {

	private TabHost tabHost;
	
	// Flags to keep track the mode the app is running
	private boolean train;
	
	// Flags to keep track the mode the app is running
	private boolean test;
	
	private String activity = "Stil";
	
	// The rate at which the input is sampled
	private final static int SAMPLE_RATE = 1000;
	
	private TrainingTable trainingTable;
	
	// Reference to testing table
	private TestingTable testingTable;
	
	private float mlastX, mlastY, mlastZ;
	
	private long rowId = 0;
	
	private boolean mInitialized;
	
	private final float NOISE = (float)2.0;
	
	private Sensor accelerometer;
	private SensorManager sm;
	
	private ArrayBuff[] trainingDataset, testingDataset;
	
	// set accuracy of amount of neighbours
	int K = 5;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mInitialized = false;
		
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
		
		
		
		testTab.setContent(R.id.testing_tab);
		testTab.setIndicator("Testing");
		
		
		// Add tabs to the tabhost
		tabHost.addTab(trainTab);
		tabHost.addTab(testTab);
		
		
		
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		
		
		trainingTable = new TrainingTable(getApplicationContext());
		testingTable = new TestingTable(getApplicationContext());
	}
	
	
	/*
	 * This method trains the app for the still activity
	 */
	public void trainStillActivity(View view){
	
		Button b = (Button) view;
		
		if(b.getText().equals("Start still")) {
			// When this button is pressed to start the variable activity
			// is set to "still" and the text shown in the button is changed to
			// "Stop still"

			train = true;
			activity = "still";
			b.setText("Stop still");
			
			trainApp();
		}
		else {
			// When this button is pressed to stop the variable activity
			// is set to "none" and the text shown on the button is changed to
			// "Start still"

			train = false;
			activity = "none";
			b.setText("Start still");
		}
	}
	
	
	/*
	 * This method samples the input and stores them in the database.
	 */
	private void trainApp() {
	
		
//		debug.setText("MainActivity.trainApp()");
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				// Keeps the counting the number of samples
//				int sampleCount = 0;
				
				try {
					while(train){
						
						// Increment the count
//						sampleCount++;
						
						// Fetch a new sample
//						fetchSample();
//						
						// If 10 samples have been fetched, then calculate the mean
//						if(sampleCount >= 10) {
//							mean();
//						}
						
						// ?????????????????? should be inside the if-clause ????????????????
						storeTrainDataCoordinates();
//						showCoordinates();
						Thread.sleep(SAMPLE_RATE);
					}
				}
				catch(InterruptedException ie) {
//					TextView debug = (TextView) findViewById(R.id.debugView);
					
//					debug.setText("MainActivity.trainApp() "+ ie.getMessage());
				}
			}
		};
		
		new Thread(runnable).start();
	}
	
	/*
	 * This function stores the training data in db
	 */
	public void storeTrainDataCoordinates(){
		// debug view
		SQLiteDatabase db = null;

		// Insert the values into the database
		
		try{
			db = trainingTable.getWritableDatabase();
			
			
			// setup query
			ContentValues values = new ContentValues();

			values.put(TrainingField.FIELD_X, Float.toString(mlastX));
			values.put(TrainingField.FIELD_Y, Float.toString(mlastY));
			values.put(TrainingField.FIELD_Z, Float.toString(mlastZ));
			values.put(TrainingField.FIELD_ACTIVITY, activity);

			

			// send query to db server
			rowId = db.insert(TrainingField.TABLE_NAME, null, values);

			db.close();
			
		}
		catch(SQLException e){
		}
		
		
	}
	
	
	/*
	 * This method trains the app for the walking activity
	 */
	public void trainWalkActivity(View view) {
		Button b = (Button) view;
		
		if(b.getText().equals("Start walking")){
			// When this button is pressed to start the variable activity
			// is set to "walk" and the text shown on the button is changed to
			// "Stop walking"
			
			train = true;
			activity = "walk";
			b.setText("Stop walking");
			
			trainApp();
		}
		else {
			// When this button is pressed to stop the variable activity
			// is set to "none" and the text shown on the button is changed to
			// "Start walking"
			
			train = false;
			activity = "none";
			b.setText("Start walking");
		}
	}
	
	
	/*
	 * This method trains the app for the running activity
	 */
	public void trainRunActivity(View view) {
		Button b = (Button) view;
		
		if(b.getText().equals("Start running")){
			// When this button is pressed to start the variable activity
			// is set to "run" and the text shown in the button is changed to
			// "Stop running"
			
			train = true;
			activity = "run";
			b.setText("Stop running");
			trainApp();
		}
		else {
			// When this button is pressed to stop the variable activity
			// is set to "none" and the text shown on the button is changed to
			// "Start running"
			
			train = false;
			activity = "none";
			b.setText("Start running");
		}
	}
	
	
	/*
	 * This method tests the app for the activities: still, walk, run.
	 * While pressed the accelerometer coordinates is being saved in a new data based, 
	 * to be processed later 
	 */
	public void testActivity(View view) {
		Button b = (Button) view;
		
		if(b.getText().equals("Start testing")){
			// When this button is pressed to start the variable activity
			// is set to "test" and the text shown in the button is changed to
			// "Stop testing"
			
			test = true;
			activity = "test";
			b.setText("Stop testing");
			
			testApp();
		}
		else {
			// When this button is pressed to stop the variable activity
			// is set to "none" and the text shown on the button is changed to
			// "Start testing"
			
			test = false;
			activity = "none";
			b.setText("Start testing");
		}
	}
	
	
	/*
	 * This method samples the input and matches them with the content in the database.
	 * The matching is done with the KNN algorithm.
	 */
	private void testApp() {
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					while(test){
						storeTestDataCoordinates();
						
						Thread.sleep(SAMPLE_RATE);
					}
				}
				catch(InterruptedException ie) {
//					TextView debug = (TextView) findViewById(R.id.debugView);
					
//					debug.setText("MainActivity.trainApp() "+ ie.getMessage());
				}
			}
		};
		
		new Thread(runnable).start();
	}
	
	
	/* Store coordinates of the test data */
	public void storeTestDataCoordinates(){
			SQLiteDatabase db = null;
		
			// Insert the values into the database
		
			try{
				db = testingTable.getWritableDatabase();
				
				
				// setup query
				ContentValues values = new ContentValues();

				//		values.put(TrainingField._ID, " ");
				values.put(TestingField.FIELD_X, Float.toString(mlastX));
				values.put(TestingField.FIELD_Y, Float.toString(mlastY));
				values.put(TestingField.FIELD_Z, Float.toString(mlastZ));


				// send query to db server
				rowId = db.insert(TestingField.TABLE_NAME, null, values);

				db.close();
			}
			catch(SQLException e){
			}				
	}
	
	
	/*
	 * 
	 */
	public void showActivityOverview(View view) {
		Intent intent = new Intent(getApplicationContext(), ActivityMonitoringOverview.class);
		
		startActivity(intent);
	}
	
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		/*TextView tvX = (TextView)findViewById(R.id.x_axis);
		TextView tvY = (TextView)findViewById(R.id.y_axis);
		TextView tvZ = (TextView)findViewById(R.id.z_axis);*/
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		if(!mInitialized)
		{
			mlastX = x ; 
			mlastY = y ;
			mlastZ = z ;
			
			
			/*tvX.setText(Float.toString(mlastX));
			tvY.setText(Float.toString(mlastY));
			tvZ.setText(Float.toString(mlastZ));*/
			
//			mInitialized = true;
		} else {	
			
			float deltaX = Math.abs(mlastX - x);
			float deltaY = Math.abs(mlastY - y);
			float deltaZ = Math.abs(mlastZ - z);
	
			if(deltaX < NOISE) deltaX = (float)0.0;
			if(deltaY < NOISE) deltaY = (float)0.0;
			if(deltaZ < NOISE) deltaZ = (float)0.0;
		
			mlastX = x ; 
			mlastY = y ;
			mlastZ = z ;
			
			
			/*tvX.setText(Float.toString(deltaX));
			tvY.setText(Float.toString(deltaY));
			tvZ.setText(Float.toString(deltaZ));*/
		}
	}
	
	
	protected void onResume(){
		
		super.onResume();
		sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		
	}
	
	protected void onPause(){
		
		super.onPause();
		sm.unregisterListener(this);
		
		
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		trainingTable.close();
	}
	
	
	/*
	 * This function shows all records in the train table
	 * Debug purposes
	 */
	public void showTrainRecords(View view) {
		// This view shows the coordinates stored in db
		TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredTrainCoordinates);

		SQLiteDatabase db = trainingTable.getReadableDatabase();
		
		String[] data = {
				TrainingField.FIELD_ID,
				TrainingField.FIELD_X,
				TrainingField.FIELD_Y,
				TrainingField.FIELD_Z,
				TrainingField.FIELD_ACTIVITY
		};

		
		String where = TrainingField.FIELD_X +" = "+ mlastX +" AND "+ 
				TrainingField.FIELD_Y +" = "+ mlastY +" AND "+ 
				TrainingField.FIELD_Z +" = "+ mlastZ;
		

		String orderBy = TrainingField.FIELD_ACTIVITY + " ASC";


		Cursor c = db.query(TrainingField.TABLE_NAME,		// Name of the table 
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
		String dataActivity;
		
		if(c.moveToFirst()) {
			showStoredCoordinates.setText("");
			
			do {
				dataID = c.getString(c.getColumnIndex(TrainingField._ID));
				dataX = c.getString(c.getColumnIndex(TrainingField.FIELD_X));
				dataY = c.getString(c.getColumnIndex(TrainingField.FIELD_Y));
				dataZ = c.getString(c.getColumnIndex(TrainingField.FIELD_Z));
				dataActivity = c.getString(c.getColumnIndex(TrainingField.FIELD_ACTIVITY));
				
				
				showStoredCoordinates.setText(showStoredCoordinates.getText()+
						dataID + ":"+
						" X: "+ dataX +
						" Y: "+ dataY +
						" Z: "+ dataZ +
						" A: "+ dataActivity +"\n"
						);
			} while(c.moveToNext());
		}
		
			
		db.close();
	}
	
	
	/*
	 * This function shows all records in the test table
	 */
	public void showTestRecords(View view) {
		// This view shows the coordinates stored in db
		TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredTestCoordinates);

		SQLiteDatabase db = testingTable.getReadableDatabase();
		
		
		String[] data = {
				TestingField.FIELD_ID,
				TestingField.FIELD_X,
				TestingField.FIELD_Y,
				TestingField.FIELD_Z
		};

		
		String where = TestingField.FIELD_X +" = "+ mlastX +" AND "+ 
				TestingField.FIELD_Y +" = "+ mlastY +" AND "+ 
				TestingField.FIELD_Z +" = "+ mlastZ;



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
			showStoredCoordinates.setText("");
			
			do {
				dataID = c.getString(c.getColumnIndex(TestingField._ID));
				dataX = c.getString(c.getColumnIndex(TestingField.FIELD_X));
				dataY = c.getString(c.getColumnIndex(TestingField.FIELD_Y));
				dataZ = c.getString(c.getColumnIndex(TestingField.FIELD_Z));
				
				
				showStoredCoordinates.setText(showStoredCoordinates.getText()+
						dataID + ":"+
						" X: "+ dataX +
						" Y: "+ dataY +
						" Z: "+ dataZ +"\n"
						);
			} while(c.moveToNext());
		}
		
		db.close();
	}
	
	
	/*
	 * This function analyses data
	 */
	public void analyzeData(View view) {
		Button b = (Button) view;
		
		
		
		if(b.getText().equals("Analyze")){
			
					
			test = true;
			activity = "test";
			TextView showStoredTestCoordinates = (TextView) findViewById(R.id.showStoredTestCoordinates);
			showStoredTestCoordinates.setText("");

			getData();
			
//			store data in internal memory for debugging 
			
			Knn_API knn = new Knn_API(K,trainingDataset, testingDataset);
			String[] activities = knn.get_activities();
			
			for(int i = 0; i < activities.length; i++) {
				showStoredTestCoordinates.setText(showStoredTestCoordinates.getText()+ "\n"+
						activities[i]
						);
			}

		}
		else {
			
			test = false;
			activity = "none";
			b.setText("Analyze");
			
			
		}
	}
	
	
	/*
	 * This function fetches training and testing data from the DB and stores them
	 * in the arrays trainingDataset and testingDataset, respectively
	 */
	private void getData() {
		// TODO Auto-generated method stub
		

		SQLiteDatabase db = trainingTable.getReadableDatabase();

		String[] trainingData = {
				TrainingField.FIELD_ID,
				TrainingField.FIELD_X,
				TrainingField.FIELD_Y,
				TrainingField.FIELD_Z,
				TrainingField.FIELD_ACTIVITY
		};
		
		
		
		String[] testingData = {
				TestingField.FIELD_ID,
				TestingField.FIELD_X,
				TestingField.FIELD_Y,
				TestingField.FIELD_Z
		};
		
		
		// send query of traindata to db
		Cursor c1 = db.query(TrainingField.TABLE_NAME,		// Name of the table 
				trainingData, 								// Fields to be fetched
				null,								// where-clause
				null, 								// arguments for the where-clause
				null, 								// groupBy
				null, 								// having
				null								// orderBy
				);

		db = testingTable.getReadableDatabase();
		
		// send query of testdata to db
		Cursor c2 = db.query(TestingField.TABLE_NAME,		// Name of the table 
				testingData, 								// Fields to be fetched
				null,								// where-clause
				null, 								// arguments for the where-clause
				null, 								// groupBy
				null, 								// having
				null								// orderBy
				);

		
		String trainDataID = "";
		float trainDataX = 0;
		float trainDataY = 0;
		float trainDataZ = 0;
		String trainDataActivity = "";
		
		
		String testDataID = "";
		float testDataX = 0;
		float testDataY = 0;
		float testDataZ = 0;
		
		TextView debugView = (TextView) findViewById(R.id.showStoredTestCoordinates);
		double distance = 0;
		
		trainingDataset = new ArrayBuff[c1.getCount()];
		testingDataset = new ArrayBuff[c2.getCount()];
		
		if(c1.moveToFirst() && c2.moveToFirst()) {
			
			for(int j = 0; j < c1.getCount(); j++) { // ?? j <= c2.getCount() ??
				
				// move to next record of TestingTable
				c1.moveToPosition(j);
				
				// fetch training data
				trainDataID = c1.getString(c1.getColumnIndex(TrainingField._ID));
				trainDataX = c1.getFloat(c1.getColumnIndex(TrainingField.FIELD_X));
				trainDataY = c1.getFloat(c1.getColumnIndex(TrainingField.FIELD_Y));
				trainDataZ = c1.getFloat(c1.getColumnIndex(TrainingField.FIELD_Z));
				trainDataActivity = c1.getString(c1.getColumnIndex(TrainingField.FIELD_ACTIVITY));
				
				
				trainingDataset[j] = new ArrayBuff(j, trainDataX, trainDataY, trainDataZ, trainDataActivity);
			}
			
			for(int i = 0; i < c2.getCount(); i++) { // ?? i <= c1.getCount() ??
				
				// move to next record of TrainingTable
				c2.moveToPosition(i);
				
				// fetch test data
				testDataID = c2.getString(c2.getColumnIndex(TestingField._ID));
				testDataX = c2.getFloat(c2.getColumnIndex(TestingField.FIELD_X));
				testDataY = c2.getFloat(c2.getColumnIndex(TestingField.FIELD_Y));
				testDataZ = c2.getFloat(c2.getColumnIndex(TestingField.FIELD_Z));
				
				testingDataset[i] = new ArrayBuff(i, testDataX, testDataY, testDataZ, "");
			}
			
			
		}
		
		/*if(isExternalStorageWritable()) {
			generateCsvFile("TrainData.txt", trainingDataset);
			generateCsvFile("TestData.txt", testingDataset);
		}
		else {
			debugView.setText("MainActivity.getData (540):Cannot create file!");
		}*/
		db.close();
	}
	
	
	/* Checks if external storage is available for read and write */
	 public boolean isExternalStorageWritable() {
	     String state = Environment.getExternalStorageState();
	     if (Environment.MEDIA_MOUNTED.equals(state)) {
	         return true;
	       	       
	     }
	     return false;
	 }
	 
	 
	 /*
	  * This function generates a CSV file from the data
	  */
	 private static void generateCsvFile(String sFileName, ArrayBuff[] Data) {
			try
			{
				File path = Environment.getExternalStoragePublicDirectory(
			            Environment.DIRECTORY_DOWNLOADS);

				path.mkdirs();
				
				File file = new File(path, sFileName);

				FileWriter writer = new FileWriter(file);

				writer.append("ID");
				writer.append(',');
				writer.append("X");
				writer.append(',');
				writer.append("Y");
				writer.append(',');
				writer.append("Z");
				writer.append(',');
				writer.append("Activity");
				writer.append('\n');

				/* 
				 *   ID, X, Y, Z, Activity
				 * */
				for(int i=0; i<Data.length; i++){

					writer.append(""+ i);
					writer.append(',');
					writer.append(""+Data[i].getX());
					writer.append(',');
					writer.append(""+Data[i].getY());
					writer.append(',');
					writer.append(""+Data[i].getZ());
					writer.append(',');
					writer.append(""+Data[i].getActivity());   
					writer.append('\n');

				}
				//generate whatever data you want

				if(Data.length > 0)
					writer.flush();
				
				writer.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			} 
		  
		    
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
