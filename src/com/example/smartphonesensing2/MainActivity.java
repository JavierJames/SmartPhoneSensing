package com.example.smartphonesensing2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartphonesensing2.DB.ActivityTable;
import com.example.smartphonesensing2.DB.TestData;

public class MainActivity extends ActionBarActivity implements SensorEventListener {
	
	private Sensor accelerometer;
	private SensorManager sm;
//	private EditText acceleration;
	private String activity = "Stil";

	// Variables to hold the values of the accelerometer
	private float mlastX, mlastY, mlastZ;
	
	// ???
	private boolean mInitialized;
	
	// ???
	private final float NOISE = (float)2.0;
	
	// Reference to the database
	private DB activityDB;
	
	// Flags to keep track the mode the app is running
	private boolean train, test;
	
	// The rate at which the input is sampled
	private final static int SAMPLE_RATE = 1000;
	
	// This view is for debugging purposes
//	private TextView debug;
	
	// rowId of the last inserted data in db
	private long rowId = 0;
	
	// set accuracy of amount of neighbours
	int K = 5;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mInitialized = false;

		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
//		debug = (TextView) findViewById(R.id.debugView);
		
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		activityDB = new DB(getApplicationContext());
		//acceleration = (EditText) findViewById(R.id.acceleration);
		
		 
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
		activityDB.close();
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

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		TextView tvX = (TextView)findViewById(R.id.x_axis);
		TextView tvY = (TextView)findViewById(R.id.y_axis);
		TextView tvZ = (TextView)findViewById(R.id.z_axis);
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		if(!mInitialized)
		{
			mlastX = x ; 
			mlastY = y ;
			mlastZ = z ;
			
			
			tvX.setText(Float.toString(mlastX));
			tvY.setText(Float.toString(mlastY));
			tvZ.setText(Float.toString(mlastZ));
			
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
			
			
			tvX.setText(Float.toString(deltaX));
			tvY.setText(Float.toString(deltaY));
			tvZ.setText(Float.toString(deltaZ));
		}
	}
	
	
	/*
	 * This method trains the app for the sitting activity
	 */
	public void trainSitActivity(View view){
	
		Button b = (Button) view;
		
		if(b.getText().equals("Start sitting")) {
			// When this button is pressed to start the variable activity
			// is set to "sit" and the text shown in the button is changed to
			// "Stop sitting"

			train = true;
			activity = "sit";
			b.setText("Stop sitting");
			
			trainApp();
		}
		else {
			// When this button is pressed to stop the variable activity
			// is set to "none" and the text shown on the button is changed to
			// "Start sitting"

			train = false;
			activity = "none";
			b.setText("Start sitting");
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
	 * This method samples the input and stores them in the database.
	 */
	private void trainApp() {
	
		
//		debug.setText("MainActivity.trainApp()");
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					while(train){
						
						storeCoordinates();
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
	 * This method tests the app for the activities: sit, walk, run.
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
	
	/*Take each dataset for each activity, and append them together with only X,Y,Z,Label as attributes */
/*	protected void create_model() {
		// TODO Auto-generated method stub
		//fetch and store activity Still 
		//fetch and store activity Walking  
		//fetch and store activity Running  
		
	}
*/
	

	/*
	 * This method tests the app for the activities: sit, walk, run.
	 */
	public void AnalyzeData(View view) {
		Button b = (Button) view;
		
		if(b.getText().equals("Analyze")){
			// When this button is pressed to start the variable activity
			// is set to "test" and the text shown in the button is changed to
			// "Stop testing"
			
			test = true;
			activity = "test";
			b.setText("Analyzing");
			knn();
			//get model in database 1 
			
			//get records in database 2 
			
			//for each record apply knn 
		//	for(r=0; r<record_count; r++){
				//for(t=0; t<training_count; t++){
			//		knn();
		
				//}
				
				
		//	} 
			
		
		}
		else {
			// When this button is pressed to stop the variable activity
			// is set to "none" and the text shown on the button is changed to
			// "Start testing"
			
			test = false;
			activity = "none";
			b.setText("Analyze");
			
			//display results
			
		}
	}
	
	
		/*
	 * This method matches the input samples against the data in the database
	 * by applying KNN algorithm. 
	 */
	
	SQLiteDatabase mDatabase;
	private void knn() {
		// TODO Auto-generated method stub
		int recordSetSize= 10; 
		int trainingSetSize =8; //total amount of training instances
		double [] K_neighbours = new double[K]; //save the closes K neighbours 
		int r,t =0; 
		
		//TODO get reference of Test Data Database and Training Data Database 
		
	// TODO Auto-generated method stub
		
		/*
		 * 1) Fetch the records of both tables, training database and testing database
		 * 2) Compare each record in the testing database with the training database
		 * 3) Save the three closest neighbors
		 * 4) The testing record will be classified as the activity which is saved the most 
		 */
		
		// 1)
		
		SQLiteDatabase db = activityDB.getReadableDatabase();

		String[] trainingData = {
				ActivityTable.FIELD_ID,
				ActivityTable.FIELD_X,
				ActivityTable.FIELD_Y,
				ActivityTable.FIELD_Z,
				ActivityTable.FIELD_ACTIVITY
		};
		
		
		
		String[] testingData = {
				TestData.FIELD_ID,
				TestData.FIELD_X,
				TestData.FIELD_Y,
				TestData.FIELD_Z
		};
		
		
		Cursor c1 = db.query(ActivityTable.TABLE_NAME,		// Name of the table 
				trainingData, 								// Fields to be fetched
				null,								// where-clause
				null, 								// arguments for the where-clause
				null, 								// groupBy
				null, 								// having
				null								// orderBy
				);


		
		Cursor c2 = db.query(TestData.TABLE_NAME,		// Name of the table 
				testingData, 								// Fields to be fetched
				null,								// where-clause
				null, 								// arguments for the where-clause
				null, 								// groupBy
				null, 								// having
				null								// orderBy
				);

		
		String trainingDataID;
		Float trainingDataX;
		Float trainingDataY;
		Float trainingDataZ;
		String trainingDataActivity;
		
		
		String testDataID;
		Float testDataX;
		Float testDataY;
		Float testDataZ;
		
		
		if(c1.moveToFirst() && c2.moveToFirst()) {
			for(int i = 0; i < c1.getCount(); i++) { // ?? i <= c1.getCount() ??
				
				// fetch test data
				testDataID = c2.getString(c2.getColumnIndex(ActivityTable._ID));
				testDataX = c2.getFloat(c2.getColumnIndex(ActivityTable.FIELD_X));
				testDataY = c2.getFloat(c2.getColumnIndex(ActivityTable.FIELD_Y));
				testDataZ = c2.getFloat(c2.getColumnIndex(ActivityTable.FIELD_Z));
				
				
				
				for(int j = 0; j < c2.getCount(); j++) { // ?? j <= c2.getCount() ??
					
					// fetch training data
					trainingDataID = c1.getString(c1.getColumnIndex(ActivityTable._ID));
					trainingDataX = c1.getFloat(c1.getColumnIndex(ActivityTable.FIELD_X));
					trainingDataY = c1.getFloat(c1.getColumnIndex(ActivityTable.FIELD_Y));
					trainingDataZ = c1.getFloat(c1.getColumnIndex(ActivityTable.FIELD_Z));
					trainingDataActivity = c1.getString(c1.getColumnIndex(ActivityTable.FIELD_ACTIVITY));
					
					EuclideanDistance(trainingDataX, trainingDataY, trainingDataZ,
							testDataX, testDataY, testDataZ
							);
					
					//TODO iterate cursor 
					c1.moveToPosition(j+1);
				}
				c2.moveToPosition(i+1);
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//TODO label each record	
		for(r=0; r<recordSetSize; r++){
			for(t=0; t<trainingSetSize; t++){
				
				knnDistance();
			}
			
			
		}
		

	//	 Cursor c = mDatabase.rawQuery("select * from activity",null);
		// tvX.setText(Float.toString(mlastX));
		// activity_now.setText("BLA BLA " );
		 //activity_now.setText(" " +db_count);
		 
		 //calculate k-nn distance from all objects in database 
		//save k-nn nearest in an array
		
		
		
		
		
		
	}
      private double EuclideanDistance(Float trainingDataX, Float trainingDataY,
			Float trainingDataZ, Float testDataX, Float testDataY,
			Float testDataZ) {
		// TODO Auto-generated method stub
    	  return Math.sqrt(Math.pow((trainingDataX-testDataX),2) + Math.pow((trainingDataY-testDataY),2) + Math.pow((trainingDataZ-testDataZ),2));
		
	}


	/* Calculate the Euclidean distance between two instance*/
	  private float knnDistance() {
		// TODO Auto-generated method stub
		  float e_distance =(float) 0.0;
		  int i=0;
		  
		  for(i=0; i<K; i++)
		  {
			  //get K elements in Kspace instances in both training and test data set
			  // e_distance = sqrt( enum[1:K](Ai-Bi) )
			  e_distance += 5;//test summation
			  
		  }
		  return e_distance;
		
	}
	  /* Determine the nearest K neighbours for a given object
	   * save results 
	   * 
	   * Steps. 
	   * 1. Get a record 
	   * 2. Have a reference to the Training Data database 
	   * 3. Calculate the Eucludean distance from record and all training data
	   * 4. Sort the K-Nearest Neighbours 
	   * */
	  private void NearestNeighbours(float distance, String [] label)
	  {
		  
		 float furthest_nn = 0; // keep track of the biggest distance of k-NN distances
		  
		 
		 
		  //store new distance only if valid 
		// if(distance > furthest_nn ) // if data is bigger than biggest data do nothing 
			 //
		// else if(distance< furthest_nn){ 
			 
			 
		// }
		 
	  }


	//knn function to count number of instances in database
	// private long fetchPlacesCount() {
	public long fetchPlacesCount() {
	     String sql = "SELECT COUNT(*) FROM " + ActivityTable.TABLE_NAME;
	     SQLiteStatement statement = mDatabase.compileStatement(sql);
	     long count = statement.simpleQueryForLong();
	     return count;
	 }

	
	
	
	public void storeCoordinates(){
		try {
			
		
		// debug view
//		TextView debug = (TextView) findViewById(R.id.debugView);
		SQLiteDatabase db = null;
//		debug.setText("1");
		
		// Insert the values into the database
		
		try{
			db = activityDB.getWritableDatabase();
		}
		catch(SQLException e){
//			debug.setText("\n\nErrror Store: "+ e.getMessage() +"\n\n");
		}
		

		ContentValues values = new ContentValues();

//		values.put(ActivityTable._ID, " ");
		values.put(ActivityTable.FIELD_X, Float.toString(mlastX));
		values.put(ActivityTable.FIELD_Y, Float.toString(mlastY));
		values.put(ActivityTable.FIELD_Z, Float.toString(mlastZ));
		values.put(ActivityTable.FIELD_ACTIVITY, activity);

		

		rowId = db.insert(ActivityTable.TABLE_NAME, null, values);
		
		// close database
		db.close();
		
//		debug.setText("rowId: "+ rowId);
		}
		catch(Exception e) {
//			debug.setText("storeCoordinates() "+e.getMessage());
		}
	}
	
	/* Store coordinates of the test data */
	public void storeTestDataCoordinates(){
		try {
			
		
		// debug view
//		TextView debug = (TextView) findViewById(R.id.debugView);
		SQLiteDatabase db = null;
//		debug.setText("1");
		
		// Insert the values into the database
		
		try{
			db = activityDB.getWritableDatabase();
		}
		catch(SQLException e){
//			debug.setText("\n\nErrror Store: "+ e.getMessage() +"\n\n");
		}
		

		ContentValues values = new ContentValues();

//		values.put(ActivityTable._ID, " ");
		values.put(TestData.FIELD_X, Float.toString(mlastX));
		values.put(TestData.FIELD_Y, Float.toString(mlastY));
		values.put(TestData.FIELD_Z, Float.toString(mlastZ));
				

		rowId = db.insert(TestData.TABLE_NAME, null, values);
		
		// close database
		db.close();
		
//		debug.setText("rowId: "+ rowId);
		}
		catch(Exception e) {
//			debug.setText("storeCoordinates() "+e.getMessage());
		}
	}
	
	
	
	
	public void showCoordinates(){
		try{
			// Read values from database
			
			TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredCoodinates);
//			debug = (TextView) findViewById(R.id.debugView);
			SQLiteDatabase db = null;
//			TextView debug = (TextView) findViewById(R.id.debugView);
			
			
			db = activityDB.getReadableDatabase();
			
			String[] data = {
					ActivityTable.FIELD_ID,
					ActivityTable.FIELD_X,
					ActivityTable.FIELD_Y,
					ActivityTable.FIELD_Z,
					ActivityTable.FIELD_ACTIVITY
			};

			
			String where = ActivityTable.FIELD_X +" = "+ mlastX +" AND "+ 
					ActivityTable.FIELD_Y +" = "+ mlastY +" AND "+ 
					ActivityTable.FIELD_Z +" = "+ mlastZ;
			

			String orderBy = ActivityTable.FIELD_ACTIVITY + " ASC";


			Cursor c = db.query(ActivityTable.TABLE_NAME,		// Name of the table 
					data, 								// Fields to be fetched
					null,								// where-clause
					null, 								// arguments for the where-clause
					null, 								// groupBy
					null, 								// having
					null								// orderBy
					);

			// close db
			db.close();
			
			// Read the values in each field
			c.moveToFirst();
			String dataX = c.getString(c.getColumnIndex(ActivityTable.FIELD_X));
			String dataY = c.getString(c.getColumnIndex(ActivityTable.FIELD_Y));
			String dataZ = c.getString(c.getColumnIndex(ActivityTable.FIELD_Z));
			String dataActivity = c.getString(c.getColumnIndex(ActivityTable.FIELD_ACTIVITY));
			
			
			// show the stored coordinates in db
			showStoredCoordinates.setText("X: "+ dataX +
					" Y: "+ dataY +
					" Z: "+ dataZ +
					" Activity "+ dataActivity);
		}
		catch(Exception e){
//			debug.setText("\n\nErrror showCoordiantes: "+ e.getMessage() +"\n\n");
		}
	}
	
	
	
	
	public void onClick(View view) {
		TextView debug = (TextView) findViewById(R.id.debugView);
		debug.setText("rowId: " +rowId);
	}

}
