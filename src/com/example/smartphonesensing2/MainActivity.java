package com.example.smartphonesensing2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;

import com.example.smartphonesensing2.DB.ActivityTable;

public class MainActivity extends ActionBarActivity implements SensorEventListener {
	
	private Sensor accelerometer;
	private SensorManager sm;
//	private EditText acceleration;
	private String activity = "Stil";

	private float mlastX, mlastY, mlastZ;
	
	// ???
	private boolean mInitialized;
	
	// ???
	private final float NOISE = (float)2.0;
	
	private DB activityDB;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mInitialized = false;

		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
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
		
/*		acceleration.setHint("X: "+ event.values[0]
				+"\nY: "+ event.values[1]
				+"\nZ:"+ event.values[2]
				);
*/
		
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
	
	
	public void onClick(View view){
		TextView debug = (TextView) findViewById(R.id.debugView);
		debug.setText("a");
		storeCoordinates();
		
//		debug.setText("b");
		
//		showCoordinates();
		
//		debug.setText("c");
	}
	
	
	public void storeCoordinates(){
		// debug view
		TextView debug = (TextView) findViewById(R.id.debugView);
		SQLiteDatabase db = null;
		debug.setText("1");
		
		// Insert the values into the database
		
		try{
			db = activityDB.getWritableDatabase();
		}
		catch(SQLException e){
			debug.setText("\n\nErrror Store: "+ e.getMessage() +"\n\n");
		}
		

		ContentValues values = new ContentValues();

		//values.put(ActivityTable.ID, " ");
		values.put(ActivityTable.X, Float.toString(mlastX));
		values.put(ActivityTable.Y, Float.toString(mlastY));
		values.put(ActivityTable.Z, Float.toString(mlastZ));
		values.put(ActivityTable.ACTIVITY, activity);

		long rowId;

		rowId = db.insert(ActivityTable.NAME, null, values);
		
		debug.setText("rowId: "+ rowId);
	}
	
	
	public void showCoordinates(){
		// Read values from database
		
		TextView showStoredCoordinates = (TextView) findViewById(R.id.showStoredCoodinates);
		SQLiteDatabase db = null;
		TextView debug = (TextView) findViewById(R.id.debugView);
		
		
		try{
			db = activityDB.getReadableDatabase();
		}
		catch(SQLException e){
			debug.setText("\n\nErrror showCoordiantes: "+ e.getMessage() +"\n\n");
		}
		

		String[] data = {
				ActivityTable._ID,
				ActivityTable.ACTIVITY,
				ActivityTable.X,
				ActivityTable.Y,
				ActivityTable.Z
		};

		
		String where = ActivityTable.X +" = "+ mlastX +" AND "+ 
				ActivityTable.Y +" = "+ mlastY +" AND "+ 
				ActivityTable.Z +" = "+ mlastZ;
		

		String orderBy = ActivityTable.ACTIVITY + " ASC";


		Cursor c = db.query(ActivityTable.NAME,		// Name of the table 
				data, 								// Fields to be fetched
				where, 								// where-clause
				null, 								// arguments for the where-clause
				null, 								// groupBy
				null, 								// having
				orderBy								// orderBy
				);

		
		// Read the values in each field
		c.moveToFirst();
		String dataX = c.getString(c.getColumnIndex(ActivityTable.X));
		String dataY = c.getString(c.getColumnIndex(ActivityTable.Y));
		String dataZ = c.getString(c.getColumnIndex(ActivityTable.Z));
		
		
		// show the stored coordinates in db
		showStoredCoordinates.setText("X: "+ dataX +"Y: "+ dataY +"Z: "+ dataZ);
	}

}
