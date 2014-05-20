package com.example.smartphonesensing2.localization;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.example.smartphonesensing2.R;

public class Localization extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_localization);
		
		
		
	}
	
	
	/*
	 * This method scans for the available RSSI signals and shows them on the textview 'scanRssi'.
	 */
	public void scanRssi(View view) {
		 WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> rssiList = wm.getScanResults();
		
		TextView apList = (TextView) findViewById(R.id.apList);
		
		apList.setText("");
		
		for(int i = 0; i < rssiList.size(); i++) {
			
			apList.setText(apList.getText() +""
					+i+": "+
					rssiList.get(i).BSSID +" | "+
//					rssiList.get(i).capabilities +" | "+
//					rssiList.get(i).frequency +" | "+
//					rssiList.get(i).level +" | "+
					rssiList.get(i).SSID +" | "+
//					rssiList.get(i).describeContents() +
					"\n");
		}
	}
}
