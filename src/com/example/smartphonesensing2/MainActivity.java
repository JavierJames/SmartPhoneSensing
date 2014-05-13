package com.example.smartphonesensing2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/* Database libraries */
import com.example.smartphonesensing2.db.TestingTable;
import com.example.smartphonesensing2.db.TestingTable.TestingField;
import com.example.smartphonesensing2.db.TrainingTable;
import com.example.smartphonesensing2.db.TrainingTable.TrainingField;

/* KNN Classification libraries */
import com.example.smartphonesensing2.KnnClassification.App;
import com.example.smartphonesensing2.KnnClassification.ArrayBuff;
import com.example.smartphonesensing2.KnnClassification.Knn_Classification;
import com.example.smartphonesensing2.KnnClassification.Neighbour;

 

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
	
	// Reference to training table
	private TrainingTable trainingTable;
	
	// Reference to testing table
	private TestingTable testingTable;
	
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
		
		
		testingTable = new TestingTable(getApplicationContext());
		trainingTable = new TrainingTable(getApplicationContext());
		 
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
	
	/*Take each dataset for each activity, and append them together with only X,Y,Z,Label as attributes */
/*	protected void create_model() {
		// TODO Auto-generated method stub
		//fetch and store activity Still 
		//fetch and store activity Walking  
		//fetch and store activity Running  
		
	}
*/
	

	/*
	 * This method tests the app for the activities: still, walk, run.
	 */
	public void analyzeData(View view) {
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
	
//	SQLiteDatabase mDatabase;
	private void knn() {
		// TODO Auto-generated method stub
		int recordSetSize= 10; 
		int trainingSetSize =8; //total amount of training instances
		double [] K_neighbours = new double[K]; //save the closes K neighbours 
		int r,t =0; 
		
		
		/*
		 * 1) Fetch the records of both tables, training database and testing database
		 * 2) Compare each record in the testing database with the training database
		 * 3) Save the three closest neighbors
		 * 4) The testing record will be classified as the activity which is saved the most 
		 */
		
		// 1)
		
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

		
		String trainDataID;
		Float trainDataX;
		Float trainDataY;
		Float trainDataZ;
		String trainDataActivity;
		
		
		String testDataID;
		Float testDataX;
		Float testDataY;
		Float testDataZ;
		
		TextView debugView = (TextView) findViewById(R.id.showStoredCoodinates);
		double distance = 0;
		
		
		if(c1.moveToFirst() && c2.moveToFirst()) {
			for(int i = 0; i < c2.getCount(); i++) { // ?? i <= c1.getCount() ??
				
				// move to next record of TrainingTable
				c2.moveToPosition(i);
				
				// fetch test data
				testDataID = c2.getString(c2.getColumnIndex(TestingField._ID));
				testDataX = c2.getFloat(c2.getColumnIndex(TestingField.FIELD_X));
				testDataY = c2.getFloat(c2.getColumnIndex(TestingField.FIELD_Y));
				testDataZ = c2.getFloat(c2.getColumnIndex(TestingField.FIELD_Z));
				
				
				for(int j = 0; j < c1.getCount(); j++) { // ?? j <= c2.getCount() ??
					
					// move to next record of TestingTable
					c1.moveToPosition(j);
					
					// fetch training data
					trainDataID = c1.getString(c1.getColumnIndex(TrainingField._ID));
					trainDataX = c1.getFloat(c1.getColumnIndex(TrainingField.FIELD_X));
					trainDataY = c1.getFloat(c1.getColumnIndex(TrainingField.FIELD_Y));
					trainDataZ = c1.getFloat(c1.getColumnIndex(TrainingField.FIELD_Z));
					trainDataActivity = c1.getString(c1.getColumnIndex(TrainingField.FIELD_ACTIVITY));
					
					distance = EuclideanDistance(trainDataX, trainDataY, trainDataZ,
							testDataX, testDataY, testDataZ);
					
					
					debugView.setText(debugView.getText() + "\n"+
							"Train: X: " +trainDataX+ " Y: "+ trainDataY+ " Z: " +trainDataZ+ "\n"+
							"Test: X: " +testDataX+ " Y: "+ testDataY+ " Z: " +testDataZ+ "\n"+
							"Distance: " +distance);
					
				}
			}
		}
		db.close();

	//	 Cursor c = mDatabase.rawQuery("select * from activity",null);
		// tvX.setText(Float.toString(mlastX));
		// activity_now.setText("BLA BLA " );
		 //activity_now.setText(" " +db_count);
		 
		 //calculate k-nn distance from all objects in database 
		//save k-nn nearest in an array
		
		
	}
	
	
//      private double EuclideanDistance(Float trainingDataX, Float trainingDataY,
//			Float trainingDataZ, Float testDataX, Float testDataY,
//			Float testDataZ) {
//		// TODO Auto-generated method stub
//    	  return Math.sqrt(Math.pow((trainingDataX-testDataX),2) + Math.pow((trainingDataY-testDataY),2) + Math.pow((trainingDataZ-testDataZ),2));
//		
//	}
	
	
	private double EuclideanDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double distance = Math.sqrt(Math.pow(x1, 2) - Math.pow(x2, 2)) + 
				Math.sqrt(Math.pow(y1, 2) - Math.pow(x2, 2)) + 
				Math.sqrt(Math.pow(z1, 2) - Math.pow(z2, 2));
		
		return distance;
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
	  /*private void NearestNeighbours(float distance, String [] label)
	  {
		  
		 float furthest_nn = 0; // keep track of the biggest distance of k-NN distances
		  
		 
		 
		  //store new distance only if valid 
		// if(distance > furthest_nn ) // if data is bigger than biggest data do nothing 
			 //
		// else if(distance< furthest_nn){ 
			 
			 
		// }
		 
	  }*/


	//knn function to count number of instances in database
	// private long fetchPlacesCount() {
//	public long fetchPlacesCount() {
//	     String sql = "SELECT COUNT(*) FROM " + TrainingField.TABLE_NAME;
//	     SQLiteStatement statement = mDatabase.compileStatement(sql);
//	     long count = statement.simpleQueryForLong();
//	     return count;
//	 }

	
	public void storeTrainDataCoordinates(){
		// debug view
//		TextView debug = (TextView) findViewById(R.id.debugView);
		SQLiteDatabase db = null;
//		debug.setText("1");
		
		// Insert the values into the database
		
		try{
			db = trainingTable.getWritableDatabase();
			
			
			// setup query
			ContentValues values = new ContentValues();

//			values.put(TrainingField._ID, " ");
			values.put(TrainingField.FIELD_X, Float.toString(mlastX));
			values.put(TrainingField.FIELD_Y, Float.toString(mlastY));
			values.put(TrainingField.FIELD_Z, Float.toString(mlastZ));
			values.put(TrainingField.FIELD_ACTIVITY, activity);

			

			// send query to db server
			rowId = db.insert(TrainingField.TABLE_NAME, null, values);
			
			// close database
			db.close();
			
		}
		catch(SQLException e){
//			debug.setText("\n\nErrror Store: "+ e.getMessage() +"\n\n");
		}
		


//		debug.setText("rowId: "+ rowId);
		
	}
	
	/* Store coordinates of the test data */
	public void storeTestDataCoordinates(){
		// debug view
//		TextView debug = (TextView) findViewById(R.id.debugView);
			SQLiteDatabase db = null;
//		debug.setText("1");
		
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

				// close database
				db.close();
			}
			catch(SQLException e){
				//			debug.setText("\n\nErrror Store: "+ e.getMessage() +"\n\n");
			}
			
			//		debug.setText("rowId: "+ rowId);
		
	}
	
	
	
	
	public void showCoordinates(){
		try{
			// Read values from database
			
			TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredCoodinates);
//			debug = (TextView) findViewById(R.id.debugView);
			SQLiteDatabase db = null;
//			TextView debug = (TextView) findViewById(R.id.debugView);
			
			
			db = trainingTable.getReadableDatabase();
			
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
			c.moveToFirst();
			String dataX = c.getString(c.getColumnIndex(TrainingField.FIELD_X));
			String dataY = c.getString(c.getColumnIndex(TrainingField.FIELD_Y));
			String dataZ = c.getString(c.getColumnIndex(TrainingField.FIELD_Z));
			String dataActivity = c.getString(c.getColumnIndex(TrainingField.FIELD_ACTIVITY));
			
			
			// close db
			db.close();
			
			
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
	
	

	/*
	 * This function shows all records in the train table
	 */
	public void showTrainRecords(View view) {
		// This view shows the coordinates stored in db
		TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredCoodinates);

		SQLiteDatabase db = trainingTable.getReadableDatabase();
		
//		db = trainingTable.getReadableDatabase();
		
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
//		c.moveToFirst();
		
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
		
		
		// show the stored coordinates in db
		/*showStoredCoordinates.setText("X: "+ dataX +
				" Y: "+ dataY +
				" Z: "+ dataZ +
				" Activity "+ dataActivity);*/
		
		db.close();
	}
	
	
	/*
	 * This function shows all records in the train table
	 */
	public void showTestRecords(View view) {
		// This view shows the coordinates stored in db
		TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredCoodinates);

		SQLiteDatabase db = testingTable.getReadableDatabase();
		
//		db = trainingTable.getReadableDatabase();
		
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
//		c.moveToFirst();
		
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
	
	
	// show the highest row number 
	public void onClick(View view) {
		TextView debug = (TextView) findViewById(R.id.debugView);
		debug.setText("rowId: " +rowId);
	}
	
	
	class Neighbour {
		private double distance;
		private String activity;
		
		public void setDistance(double d) {
			this.distance = d;
		}
		
		public double getDistance() {
			return this.distance;
		}
		
		public void setActivity(String a) {
			this.activity = a;
		}
		
		public String getActivity() {
			return this.activity;
		}
	}

}
