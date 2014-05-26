package com.example.smartphonesensing2.localization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartphonesensing2.R;

public class Localization extends ActionBarActivity {

	private final static int TWO_MINUTES = 180000;
	private final static int THREE_MINUTES = 180000;
	private final static int FIVE_MINUTES = 300000;
	
	// sample rate at which to sample
	private final static int SAMPLE_RATE = 1000;
	private final static int DURATION = FIVE_MINUTES; 
	
	// keep tracking of scanning time
	private long start, stop; 
		
	// keep track of sample number 
	private int id_sample=0; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_localization);
		
		
		
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
	 * Scan rssi in a cell
	 */
	public void scanCell(View view) {
		Button b = (Button) view;
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
					
					while((stop - start) < DURATION){ 
					//	WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
						wm.startScan();
						
						List<ScanResult> rssiList = wm.getScanResults();
						
						id_sample ++;
						for(int i = 0; i < rssiList.size(); i++) {
							
							writeToFile(
									rssiList.get(i).SSID,
									rssiList.get(i).BSSID,
									rssiList.get(i).level,
									rssiList.get(i).frequency,
									rssiList.get(i).capabilities,
									rssiList.get(i).describeContents()
									);
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
