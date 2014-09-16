package com.example.publicize;

import java.net.ContentHandler;
import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    // Message types sent from the Service Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
	private Intent mServiceIntent;
	
	PendingIntent mPintent ;
	Intent mIntent;
	AlarmManager mAlarm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAlarm = (AlarmManager)getSystemService(this.ALARM_SERVICE);
		mIntent = new Intent(this, CurrentAppService.class);
		mPintent = PendingIntent.getService(this, 0, mIntent, 0);

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
	
	public void startHeadlessApp(View view) {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("Service is Running");
		
		//mServiceIntent = new Intent(this, CurrentAppService.class);
		//this.startService(mServiceIntent);
		//mServiceIntent.setData(Uri.parse(dataUrl));
		Log.i("publicize", "Start Headless App!");

		Calendar cal = Calendar.getInstance();

	
		// Start every 30 seconds
		mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30*1000, mPintent); 
	}
	
	public void stopHeadlessApp(View view) {
		mAlarm.cancel(mPintent);
		Log.i("publicize", "all services stopped");
	}
	
	public void startBluetoothServer(View view) {
		new AcceptThread().start();
	}
	
}
