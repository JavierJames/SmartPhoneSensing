package com.example.smartphonesensing2.activity_monitoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartphonesensing2.R;
import com.example.smartphonesensing2.KnnClassification.ArrayBuff;
import com.example.smartphonesensing2.KnnClassification.Knn_API;
import com.example.smartphonesensing2.db.TestingTable;
import com.example.smartphonesensing2.db.TestingTable.TestingField;
import com.example.smartphonesensing2.db.TrainingTable;
import com.example.smartphonesensing2.db.TrainingTable.TrainingField;

public class TestActivity extends FragmentActivity {
	
	// Flags to keep track the mode the app is running
	/*private boolean test;*/
	
	/*// Reference to testing table
	private TestingTable testingTable;*/
	
	private String activity = "Stil";
	
	// The rate at which the input is sampled
	private final static int SAMPLE_RATE = 1000;
	
	// Variables to hold the values of the accelerometer
	private float mlastX, mlastY, mlastZ;
	
	// rowId of the last inserted data in db
	private long rowId = 0;
	
	private ArrayBuff[] trainingDataset, testingDataset;
	
	// set accuracy of amount of neighbours
	int K = 5;
	
	// Reference to training table
	private TrainingTable trainingTable;
	
		
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_monitoring_test_layout);
        
        /*testingTable = new TestingTable(getApplicationContext());*/
    }
	
	
	
	/*
	 * This method tests the app for the activities: still, walk, run.
	 * While pressed the accelerometer coordinates is being saved in a new data based, 
	 * to be processed later 
	 
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
	}*/
	
	

	/*
	 * This method samples the input and matches them with the content in the database.
	 * The matching is done with the KNN algorithm.
	 
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
	}*/
	
	
/*	 Store coordinates of the test data 
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
	}*/
	
	
	// Delete all records of the testing table
	/*public void deleteTestRecords(View view) {
		SQLiteDatabase db = testingTable.getWritableDatabase();

		int n = db.delete(TestingField.TABLE_NAME, 
				null, 
				null);

		TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredCoordinates);

		showStoredCoordinates.setText("Deleted " +n+ " records");
	}*/
	
	
	/*
	 * This method tests the app for the activities: still, walk, run.
	 */
	/*public void analyzeData(View view) {
		Button b = (Button) view;
		
		
		
		if(b.getText().equals("Analyze")){
			
					
			test = true;
			activity = "test";
			TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredCoordinates);
			showStoredCoordinates.setText("");

			getData();
			
			store data in internal memory for debugging 
			
			Knn_API knn = new Knn_API(K,trainingDataset, testingDataset);
			String[] activities = knn.get_activities();
			
			for(int i = 0; i < activities.length; i++) {
				showStoredCoordinates.setText(showStoredCoordinates.getText()+ "\n"+
						activities[i]
						);
			}

		}
		else {
			
			test = false;
			activity = "none";
			b.setText("Analyze");
			
			
		}
	}*/
	
	
	/*
	 * This method matches the input samples against the data in the database
	 * by applying KNN algorithm. 
	 */
	
//	SQLiteDatabase mDatabase;
	/*private void getData() {
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
		
		TextView debugView = (TextView) findViewById(R.id.showStoredCoordinates);
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
		
		if(isExternalStorageWritable()) {
			generateCsvFile("TrainData.txt", trainingDataset);
			generateCsvFile("TestData.txt", testingDataset);
		}
		else {
			debugView.setText("MainActivity.getData (540):Cannot create file!");
		}
		
		
		db.close();
	}*/
	
	
	/* Checks if external storage is available for read and write */
	 public boolean isExternalStorageWritable() {
	     String state = Environment.getExternalStorageState();
	     if (Environment.MEDIA_MOUNTED.equals(state)) {
	         return true;
	       	       
	     }
	     return false;
	 }
	 
	 
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
	 
	 
	 /*
		 * This function shows all records in the test table
		 */
		/*public void showTestRecords(View view) {
			// This view shows the coordinates stored in db
			TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredCoordinates);

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
		}*/
}
