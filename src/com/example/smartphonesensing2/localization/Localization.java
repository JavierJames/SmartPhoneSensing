package com.example.smartphonesensing2.localization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartphonesensing2.R;
import com.example.smartphonesensing2.table.Table;

public class Localization extends ActionBarActivity {

	private final static int TWO_MINUTES = 180000;
	private final static int THREE_MINUTES = 180000;
	private final static int FIVE_MINUTES = 300000;
	
	// sample rate at which to sample
	private final static int SAMPLE_RATE = 1000;

	private final static int DURATION = TWO_MINUTES; //in ms: 5 min

	
	// keep tracking of scanning time
	private long start, stop; 
		
	// keep track of sample number 
	private int id_sample=0;
	
	private ArrayList<Table> tables = new ArrayList<Table>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_localization);
		
		
		createPMFTable();
	}
	
	
	/*
	 * This method scans for the available RSSI signals and shows them on the textview 'scanRssi'.
	 */
	/*public void scanRssi(View view) {
		 WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> rssiList = wm.getScanResults();
		
		TextView apList = (TextView) findViewById(R.id.apList);
		
		apList.setText("");
		
		for(int i = 0; i < rssiList.size(); i++) {
			
			apList.setText(apList.getText() +""
					+i+": "+
					rssiList.get(i).BSSID +" | "+
					rssiList.get(i).capabilities +" | "+
					rssiList.get(i).frequency +" | "+
					rssiList.get(i).level +" | "+
					rssiList.get(i).SSID +" | "+
					rssiList.get(i).describeContents() +
					"\n");
		}
	}*/
	
	
	/*
	 * This function creates a pmf table for each access-point
	 */
	private void createPMFTable() {
		// Fetch the path
		String path = Environment.getExternalStorageDirectory().toString()+"/Download/histogram/";
		
		// print on the screen
		Log.d("Files", "Path: " + path);
		
		File dir = new File(path);        
		File dirs[] = dir.listFiles();
		
		File subdir = null;
		File file[] = null;
		
		// cell name
		String cell = "";
		
		// table name
		String accesspoint = "";
		
		Table table = null;
		
//		Log.d("Files", "Size: "+ dirs.length);
		
		for (int i = 0; i < dirs.length; i++){
			
//		    Log.d("Dir", "DirName:" + dirs[i].getName());
		    
		    // subdirectory
		    subdir = new File(dirs[i].getPath());
		    
		    // list of files in the subdirectory
		    file = subdir.listFiles();
//		    
//		    Log.d("Dir", "file[i].getPath():" + file[i].getPath());
//		    Log.d("Dir", "File[i].getName():" + file[i].getName());
//		    Log.d("Dir", "File[i].getParent():" + file[i].getParent());
//		    
//		    Log.d("File", "\t FileName:" +subdir.getName());
		    
		    // Iterate over each file
		    for(File f : file) {
//		    	Log.d("File", "\t FileName:" +f.getName());
		    	
		    	// retrieving the cell name from the parent path
		    	// by removing the path from the folder name
		    	cell = f.getParent().substring(39);
		    	
		    	// retrieving the name of the accesspoint from the filename
		    	accesspoint = f.getName().substring(0, f.getName().length()-4);
//		    	Log.d("Accesspoint", accesspoint);
		    	
		    	// Fetch the table with the cell name
		    	table = getTable(accesspoint);
		    	
		    	// Creates a new table if it does not exist yet
		    	if(table == null) {
		    		table = new Table(accesspoint);
		    	}
		    	
		    	// Read file
		    	putPMFIntoTable(f, table, cell);
		    }   
		}

	}

	
	/*
	 * Returns a table with the name cell
	 */
	private Table getTable(String cell) {
		Table table = null;
		
		for(int i = 0; i < tables.size(); i++) {
			
			if(tables.get(i).getName().equalsIgnoreCase(cell)) {
				table = tables.get(i);
				break;
			}
		}
		
		return table;
	}


	/*
	 * This function converts the histogram to pmf table
	 */
	private void putPMFIntoTable(File f, Table table, String cell) {

//		Log.d("ReadFile", "Filename:"+f.getName());
		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(f));
		    String line;
		    String [] tokens;

		    while ((line = br.readLine()) != null) {
//		        text.append(line);
//		        text.append('\n');
		    	tokens = line.split(",");
		    	
//		    	Log.d("Token0", tokens[0]);
//		    	Log.d("Token1", tokens[1]);
//		    	Log.d("Token2", tokens[2]);
		    	
//		    	Log.d("Cell", ""+Integer.parseInt(cell.substring(1)));
//		    	Log.d("Cell-1", ""+(Integer.parseInt(cell.substring(1))-1));
		    	
		    	table.setPMF(
		    			Integer.parseInt(cell.substring(1))-1, 
		    			Math.abs(Integer.parseInt(tokens[0])), 
		    			Float.parseFloat(tokens[2]));
		    	
		    	table.printTable();
		    }
		    
//		    Log.d("ReadFile", text.toString());
		}
		catch (IOException e) {
		    Log.d("Error", "Error: "+e.getMessage());
		}
	}

	/*
	 * Scan rssi in a cell
	 */
	public void scanCell(View view) {
		Button b = (Button) view;
		id_sample = 0;
		
		scanningCell(b);
	}
	
	
	/*
	 * Scan cell
	 */
	private void scanningCell(final Button button) {

		Runnable runnable = new Runnable() {
			Time t = new Time();
			

			@Override
			public void run() {
				
			   	runOnUiThread(new Runnable () {
		    		@Override 
		    		public void run(){
		    			button.setEnabled(false);
		    		}
		    	});


				start = System.currentTimeMillis();
				stop = System.currentTimeMillis();
				
				try {

					WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE); //new
					List<ScanResult> rssiList = null;
					
					while((stop - start) < DURATION){ 
					//	WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
						wm.startScan();
						

						rssiList = wm.getScanResults();
						
						final TextView apList = (TextView) findViewById(R.id.apList);
						
						
						
						id_sample ++;
						for(int i = 0; i < rssiList.size(); i++) {
							final String SSID = rssiList.get(i).SSID;
							final String BSSID =  rssiList.get(i).BSSID;
							final int level = rssiList.get(i).level;
							final int frequency = rssiList.get(i).frequency;
							final String capabilities = rssiList.get(i).capabilities;
							final int describeContents = rssiList.get(i).describeContents();
							
							
							// write the access-points to a file
							writeToFile(
									SSID,
									BSSID,
									level,
									frequency,
									capabilities,
									describeContents
									);
							
							
							// show the access-points on the screen
							runOnUiThread(new Runnable () {
					    		@Override 
					    		public void run(){
					    			apList.setText(apList.getText()+
					    					SSID+
											BSSID+
											level+
											frequency+
											capabilities+
											describeContents+
											"\n"
			    						);
					    		}
					    	});
						}
						
						
						
						Thread.sleep(SAMPLE_RATE);
						
						stop = System.currentTimeMillis();
						
						
						
					}
					
					
					
				   	runOnUiThread(new Runnable () {
			    		@Override 
			    		public void run(){
			    			button.setEnabled(true);
			    		}
			    	});

					
				}//end of try
				catch(InterruptedException ie) {}
			}//end of run
		};
		
		

	new Thread(runnable).start();
}
	
	
	/*
	 * Write data of access point to a file
	 */
	private void writeToFile(String SSID, String BSSID, int level, int frequency, String capabilities, int content) {
		try
		{
			TextView cell = (TextView) findViewById(R.id.cell_name);
			
			String cell_name = cell.getText().toString();
			
			
			File path = Environment.getExternalStoragePublicDirectory(
		            Environment.DIRECTORY_DOWNLOADS
		            );

			path.mkdirs();
			
			
			File file = new File(path, cell_name+".txt");

			FileWriter writer = new FileWriter(file, true);

			long size = file.length();
			
			if(size <= 0) {
				writer.append("SampleID ");
				writer.append(",");
				writer.append("SSID");
				writer.append(",");
				writer.append("BSSID");
				writer.append(",");
				writer.append("level");
				writer.append(",");
				writer.append("frequency");
				writer.append(",");
				writer.append("Capabilities");
				writer.append(",");
				writer.append("describeContents");
				writer.append("\n");
			}
			writer.append(""+id_sample);
			writer.append(",");
			writer.append(SSID);
			writer.append(",");
			writer.append(BSSID);
			writer.append(",");
			writer.append(""+level);
			writer.append(",");
			writer.append(""+frequency);
			writer.append(",");
			writer.append(capabilities);
			writer.append(",");
			writer.append(""+content);
			writer.append("\n\n");
			//writer.append("------------------------------------------------------------------------------------------------\n\n");


//			writer.flush();
			
			writer.close();
			
			////////////// debug ///////////////////////////
			
//			TextView debug = (TextView) findViewById(R.id.apList);
//			
//			debug.setText(debug.getText()+
//					SSID +
//					BSSID +
//					""+level +
//					""+frequency +
//					capabilities +
//					""+content +
//					"\n"
//					);
//			
			///////////////////////////////////////////////
			
		}
		catch(IOException e){
			e.printStackTrace();
		} 
	}
	
}
