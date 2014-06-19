package com.example.smartphonesensing2.localization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.example.smartphonesensing2.R;
import com.example.smartphonesensing2.localization.classification.Bayesian;
import com.example.smartphonesensing2.localization.classification.LaplaceBayesian;
import com.example.smartphonesensing2.localization.classification.NaiveBayesian;
import com.example.smartphonesensing2.localization.classification.ProbabilisticBayesian;
import com.example.smartphonesensing2.localization.filter.AccessPointOccurrence;
import com.example.smartphonesensing2.localization.filter.AccessPointRSSIStrength;
import com.example.smartphonesensing2.localization.filter.SelectionAverage;
import com.example.smartphonesensing2.localization.filter.SelectionCoverage;
import com.example.smartphonesensing2.localization.histogram.Histogram;
import com.example.smartphonesensing2.localization.histogram.TrainingData;
import com.example.smartphonesensing2.table.Table;

public class Localization extends ActionBarActivity {

	private final static int TWO_MINUTES = 180000;
	private final static int THREE_MINUTES = 180000;
	private final static int FIVE_MINUTES = 300000;
	
	// sample rate at which to sample
	private final static int SAMPLE_RATE = 1000;
	private final static int DURATION = FIVE_MINUTES/5; 
	
	// keep tracking of scanning time
	private long start, stop; 
		
	// keep track of sample number 
	private int id_sample=0;
	
	
	private ArrayList<Table> tables = new ArrayList<Table>();
	
	// Tabhost to hold the tab
	private TabHost tabHost;
	
	// Classifiers
	private NaiveBayesian naiveBayesian;
	private LaplaceBayesian laplaceClassifier;
	private ProbabilisticBayesian probabilisticClassifier;
	
	

	// The path to the main directory
	
	private String filepath;
	
	// Set of training data. Each training data is associated to one access-point
    ArrayList<TrainingData> tds = new ArrayList<TrainingData>();
    
    // Filter to be applied on the AP, based on average rssi strength over entire platform
    AccessPointRSSIStrength rssi_filter= new AccessPointRSSIStrength(filepath);
    
    // Holds the rssi values of the chosen AP
    ArrayList<Integer> observations = new ArrayList<Integer>();

    //keep track of how many times Button SenseNewAP has been pressed, prevent overpressing
    private int SenseNewAP_buttonPressCount=0;
	
    /*AP selection */
    ArrayList<String> ToBeSelectedAP = new ArrayList<String>();
    ArrayList<String> SelectedAP = new ArrayList<String>();
	// list of chosen APs in arraylist to be added in the ArrayAdapter
	ArrayList<String> chosen_ap_names = new ArrayList<String>();
    
    //need a list of chosenAP Names
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_localization);
		
		
//		createPMFTable();
		
		/* *****************************************
		 * Set up the User Interface with Tab buttons
		 * 
		 *  ***************************************** */
		// Create a tabhost that will contain the tabs
		tabHost = (TabHost)findViewById(R.id.tabhost);
		//				tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		tabHost.setup();


		// Create the tabs
		TabSpec trainTab = tabHost.newTabSpec("Training");
		TabSpec testTab = tabHost.newTabSpec("Testing");
		TabSpec listAPTab = tabHost.newTabSpec("Select AP");


		// Set Activity that will be opened when Training Tab is selected
		// Set the tabname

		trainTab.setContent(R.id.training_tab);
		trainTab.setIndicator("Training");


		// Set Activity that will be opened when Testing Tab is selected
		// Set the tabname

		testTab.setContent(R.id.testing_tab);
		testTab.setIndicator("Testing");
		
		
		// Set Activity that will be opened when Select AP Tab is selected
		// Set the tabname
		
		listAPTab.setContent(R.id.listAP_tab);
		listAPTab.setIndicator("Select AP");


		// Add tabs to the tabhost
		tabHost.addTab(trainTab);
		tabHost.addTab(testTab);
		tabHost.addTab(listAPTab);
		
		
		
		
		/* 
		 * Set base Path where files should be fetched and stored
		 * */
		int User=2;  //0 = Javier pc, 1=Luis pc, 2=all phones,
		
		
		String folder_base_path = null;
		
		if(User==0){
			folder_base_path = "/home/swifferayubu/Dropbox/Test/";
			//folder_base_path = "/home/swifferayubu/Dropbox/Doc/";
		}
		else if (User==1)
			folder_base_path =	"/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/";
		else if (User==2)
			folder_base_path =	Environment.getExternalStorageDirectory().toString()+"/Download/";
		
		String root_folder_name = "cellsdata/";		//main folder
	
		filepath = folder_base_path + root_folder_name;	
		 
		
		
	/* 
	 * */
		
		ToBeSelectedAP.add("Conferentie-TUD_00_1b_90_76_d3_f6");
		ToBeSelectedAP.add("TUvisitor_00_1b_90_76_d3_f3 ");
		ToBeSelectedAP.add("eduroam_00_1b_90_76_d3_f0  ");
		ToBeSelectedAP.add("tudelft-dastud_00_1b_90_76_ce_14 ");
		
		
		
	     ListView listAvailableAP;
	     ArrayAdapter<String> adapter_listAvailableAP;
	     
	    
	     listAvailableAP = (ListView) findViewById(R.id.listAllAP);
	    
	     adapter_listAvailableAP= new ArrayAdapter<String>(this, R.layout.frament_localization_listview_item, ToBeSelectedAP);
	     
	     listAvailableAP.setAdapter(adapter_listAvailableAP);
	     
	     
	     	     
	 
			
			// Create the adapter to translate the array of strings to list items
			ArrayAdapter<String> adapterAllAP = new ArrayAdapter<String>(
					this,
					R.layout.frament_localization_listview_item, ToBeSelectedAP   //not good, just for debugging purposes
					//allAP
					);
			
			
			// Create the adapter to translate the array of strings to list items
			ArrayAdapter<String> adapterChosenAP = new ArrayAdapter<String>(
					this,
					R.layout.frament_localization_listview_item,
					chosen_ap_names
					);
			
			//Get listView Object of all AP from xml file 
			// Add adapter to listview
			ListView listAllAP = (ListView) findViewById(R.id.listAllAP);
			listAllAP.setAdapter(adapterAllAP);
			
			
			//Get listView Object of all selected AP from xml file 
			// Add adapter to listview
			ListView listChosenAp = (ListView) findViewById(R.id.listSelectedAP);
			listChosenAp.setAdapter(adapterChosenAP);
			
			
			// Add click listener to each item
			listAllAP.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked, int position,
						long id) {
					
					chooseAP(viewClicked);
				}
			});
			
			
			listChosenAp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked, int position,
						long id) {
					
					unChooseAP(viewClicked);
				}
				
			});
			
	
	     
	  
	     
	     
	     
		
		
		
	} //end onCreate
	

	
	
	
	
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
		    	
		    	table.setValue(
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
						
//						final TextView apList = (TextView) findViewById(R.id.apList);
						
						String SSID;
						String BSSID;
						int level;
						int frequency;
						String capabilities;
						int describeContents;
						
						id_sample ++;
						
						for(int i = 0; i < rssiList.size(); i++) {
							SSID = rssiList.get(i).SSID;
							BSSID =  rssiList.get(i).BSSID;
							level = rssiList.get(i).level;
							frequency = rssiList.get(i).frequency;
							capabilities = rssiList.get(i).capabilities;
							describeContents = rssiList.get(i).describeContents();
							
							
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
							/*runOnUiThread(new Runnable () {
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
					    	});*/
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
	 * Fetch list of AP with their corresponding rssi values
	 */
	private ArrayList<Integer> fetchRSSIChosenAP(ArrayList<String> chosenAP) {
		// List of RSSI of each chosen AP
				ArrayList<Integer> observation = new ArrayList<Integer>();
				
		
		//hardcode for testing
		/*		1st Conferentie-TUD_00_1b_90_76_d3_f6      -73;
				2nd  TUvisitor_00_1b_90_76_d3_f3            -72;
				3rd  eduroam_00_1b_90_76_d3_f0                 -74; 
				[4th]: tudelft-dastud_00_1b_90_76_ce_14       -83;
*/
		
				observation.add(-73);
				observation.add(-72);
				observation.add(-74);
				observation.add(-83);
		
		// TODO: create treemap for each AP having their name as key and rssi as value 
		
	/*	WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> rssiList = null;


		wm.startScan();

		rssiList = wm.getScanResults();

		String[] ssid = new String[rssiList.//hsize()];
		
		String SSID;
		String BSSID;
		int level;
		int frequency;
		String capabilities;
		int describeContents;
		
		// These variables are used for sorting the list of APs
		int highestRSSI = 0;
		int highestIndex = -1;
		
		
		
		for(int i = 0; i < rssiList.size(); i++) {
			
			ssid[i] = rssiList.get(i).SSID;
			SSID = rssiList.get(i).SSID;
			BSSID =  rssiList.get(i).BSSID;
			level = rssiList.get(i).level;
			
			
			// Iterate each chosen AP 
			for(int j = 0; j < chosenAP.size(); j++) {
				
				// if the SSID+BSSID matches the name of the chosen AP
				// then add it to the list
				if((SSID+BSSID).equalsIgnoreCase(chosenAP.get(j))) {
					observation.add(j, Integer.valueOf(level));
				}
			}
		*/	
			
			/*SSID = rssiList.get(i).SSID;
			BSSID =  rssiList.get(i).BSSID;
			level = rssiList.get(i).level;
			frequency = rssiList.get(i).frequency;
			capabilities = rssiList.get(i).capabilities;
			describeContents = rssiList.get(i).describeContents();*/

			
			
			// sort the list of APs by rssi values ascending
			/*if(level > highestRSSI) {
				highestRSSI = level;
				highestIndex = i;
				continue;
			}*/
			
			// TODO: sort APs

			// write the list of access-points to a file
			/*writeToFile(
					"fetchedlistAP",
					SSID,
					BSSID,
					level,
					frequency,
					capabilities,
					describeContents
					);*/
	//	} // end for(int i = 0; i < rssiList.size(); i++)
		
		return observation;
	}
	
	
	/*
	 * This function generates the initial belief
	 * i.e. it assigns an uniform probability to each cell
	 */
	public void initialBelief(View view) {
		String filepath = "/Downloads/cellsdata/";
		
		
		// Set of training data. Each training data is associated to one access-point
//	    ArrayList<TrainingData> tds = new ArrayList<TrainingData>();

	    
	    /************************
	     * Create training data
	     **************************/
	    
	    
	    // new training data
	    TrainingData td;
	    
	    // new access-point name to be associated with the training data
	    String name = null;
	    

	    // create training data for each AP

	    /*for(int i = 0; i < chosen_ap_names.size(); i++) {

	    	name = chosen_ap_names.get(i);

	    	td = new TrainingData(name, filepath);

	    	td.createPMFTable();
	    	td.createHistogramTable();

	    	tds.add(td);
	    }*/
		
		
		naiveBayesian = new NaiveBayesian(filepath); //create classifier 
		naiveBayesian.trainClassifier(tds); //train classifier 	   
		naiveBayesian.setInitialBelieve();    //set the initial believe to uniform
		
		
		laplaceClassifier = new LaplaceBayesian(filepath);
		laplaceClassifier.trainClassifier(tds); //train classifier by updating training data. correction done automatically 
		laplaceClassifier.setInitialBelieve();
		
//		TODO: show current location, all cells should be a candidate.
	}
	
	
	/*
	 * This function fetches the next AP, from the list of chosen AP, with the highest RSSI value
	 */
	public void senseNewAP(View view) {
		
		int cellID = 0; 
		
		System.out.println("About to classify");
		int current_cell = 0;
		
		int current_cell2 = 0;
		
		//dummy classifier
		LaplaceBayesian lpclassifier = new LaplaceBayesian(null); 
		
		
		//get the list of observations that belongs to the chosen AP
	//	ArrayList<Integer> observations = new ArrayList<Integer>();


	/*	System.out.println("Observations size: "+observations.size());
	      
	      /*
	       * End
	       */
	      
		
		
				
		
		
		
		// list of APs in arraylist to be added in the ArrayAdapter
//		ArrayList<String> allAP = new ArrayList<String>(Arrays.asList(fetchListAP()));
		
		// list of chosen APs in arraylist to be added in the ArrayAdapter
//		ArrayList<String> chosenAP = new ArrayList<String>();
		
		
		for(int i=0; i<observations.size(); i++)
		{
			System.out.println("Value  "+observations.get(i));
		}
		
		//Get AP id for the next strongest RSSI value 
		//call only if button not pressed amount of times of AP
		if(SenseNewAP_buttonPressCount<observations.size()){
		cellID = Bayesian.NextStrongestAP(observations);
		System.out.println("highest rssi value: "+observations.get(cellID));
		}
		else{
			System.out.println("No More AccessPoints to fetch rssi values");
		}
		
		SenseNewAP_buttonPressCount++;
		
		//add only one integer to the arraylist for now
		//ArrayList<Integer> test = new ArrayList<Integer>();
		//test.add(observations.get(cellID));
		
		//classify observation based on this AP training data.
		// Create the adapter to translate the array of strings to list items
		/*ArrayAdapter<String> adapterAllAP = new ArrayAdapter<String>(
				this,
				R.layout.frament_localization_listview_item,
				allAP
				);*/
		
		//cellID=lpclassifier.classifyObservation(test);
		
		//System.out.println("highest rssi value: "+observations.get(cellID));
		
		
		//call necessary functions 
		// Create the adapter to translate the array of strings to list items
		/*ArrayAdapter<String> adapterChosenAP = new ArrayAdapter<String>(
				this,
				R.layout.frament_localization_listview_item,
				chosenAP
				);*/
		
		
		// Add adapter to listview
		/*ListView listAllAP = (ListView) findViewById(R.id.listAllAP);
		listAllAP.setAdapter(adapterAllAP);*/
		
		//current_cell=   naiveBayesian.classifyObservation(observations); 

	     
		//current_cell2 = laplaceClassifier.classifyObservation(observations);
		
		// TODO: show current location
	}
	
	
	/*
	 * This functions fetches rssi values for the list of chosen AP, selected by the user
	 */
	public void senseNewScan(View view) {
		
		
	    // fetch only the RSSI value for the chosen AP
	    observations = fetchRSSIChosenAP(chosen_ap_names); //oberserveNewRssi(keyboard,tds);  
	 
		for(int i=0; i<observations.size(); i++)
		{
			System.out.println("Observation"+observations.get(i));
		}
	    
	      
			 
		
			      
	} //end SenseNewScan Function


	/*
	 * This function passes the chosen AP from the all list and passe
	 */
	@SuppressWarnings("unchecked")
	protected void chooseAP(View viewClicked) {
		
		// Cast view to textview, so that its text can be retrieved
		TextView chosenItem = (TextView) viewClicked;
		
		
		// Fetch the listSelectedAP to add the clicked item
		ListView listChosenAP = (ListView) findViewById(R.id.listSelectedAP);
		
		// Get the adapter of the chosen list
		ArrayAdapter<String> adapterChosenAP = (ArrayAdapter<String>) listChosenAP.getAdapter();
		
		
		// add the chosen AP to the adapter
		adapterChosenAP.add(chosenItem.getText().toString());
		
		// Add the adapter back to the listview
		listChosenAP.setAdapter(adapterChosenAP);
		
		
		
		// remove item from listAllAP
		ListView listAllAP = (ListView) findViewById(R.id.listAllAP);
		
		// Get the adapter of the all list
		ArrayAdapter<String> adapterAllAP = (ArrayAdapter<String>) listAllAP.getAdapter();
		
		// Remove the chosen AP from the adapter
		adapterAllAP.remove(chosenItem.getText().toString());
		
		// Add the adapter back to the listview
		listAllAP.setAdapter(adapterAllAP);
	}
	
	
	protected void unChooseAP(View viewClicked) {
		
		// Cast view to textview, so that its text can be retrieved
		TextView chosenItem = (TextView) viewClicked;


		// Fetch the listAllAP to add the clicked item
		ListView listAllAP = (ListView) findViewById(R.id.listAllAP);

		// Get the adapter of the chosen list
		@SuppressWarnings("unchecked")
		ArrayAdapter<String> adapterAllAP = (ArrayAdapter<String>) listAllAP.getAdapter();


		// add the chosen AP to the adapter
		adapterAllAP.add(chosenItem.getText().toString());

		// Add the adapter back to the listview
		listAllAP.setAdapter(adapterAllAP);



		// remove item from listChosenAP
		ListView listChosenAP = (ListView) findViewById(R.id.listSelectedAP);

		// Get the adapter of the chosen list
		ArrayAdapter<String> adapterChosenAP = (ArrayAdapter<String>) listChosenAP.getAdapter();

		// Remove the chosen AP from the adapter
		adapterChosenAP.remove(chosenItem.getText().toString());

		// Add the adapter back to the listview
		listChosenAP.setAdapter(adapterChosenAP);
	}


	/*
	 * This function shows the current location of the user
	 */
	public void showCurrentLocation(View view) {
		TextView text = (TextView) findViewById(R.id.showLocationView);
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
	
	
	/*
	 * Write data of access point to a file
	 */
	private void writeToFile(String filename, String SSID, String BSSID, int level, int frequency, String capabilities, int content) {
		try
		{
			
			String cell_name = filename;
			
			
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
			
			writer.close();
			
			
		}
		catch(IOException e){
			e.printStackTrace();
		} 
	}
	
	
	
	
	/*
	 * Sort the treemap by its values
	 */
	static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> sortTreeMapByValues(Map<K,V> map) {
		
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(new Comparator<Map.Entry<K,V>>() {
	        	
	            @Override 
	            public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                return e1.getValue().compareTo(e2.getValue());
	            }
	        }
	    );
	    
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
	
	
	
	/*
	 * This function selects AP from the all list
	 */
//	public void selectAP(View view) {
//		
//	}
	
	
	/*
	 * This function unselects AP from the chosen list
	 */
//	public void unselectAP(View view) {
//		
//	}
}
