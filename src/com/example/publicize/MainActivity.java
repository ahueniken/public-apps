package com.example.publicize;

import java.io.UnsupportedEncodingException;
import java.net.ContentHandler;
import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private Intent mServiceIntent;
	private ConnectedThread mConnectedThread;
	PendingIntent mPintent;
	Intent mIntent;
	AlarmManager mAlarm;
	TextView mTitle;
	int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAlarm = (AlarmManager) getSystemService(this.ALARM_SERVICE);
		mIntent = new Intent(this, CurrentAppService.class);
		mPintent = PendingIntent.getService(this, 0, mIntent, 0);
		mTitle = (TextView) findViewById(R.id.title);

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
		mTitle.setText("Service is Running");

		// mServiceIntent = new Intent(this, CurrentAppService.class);
		// this.startService(mServiceIntent);
		// mServiceIntent.setData(Uri.parse(dataUrl));
		Log.i("publicize", "Start Headless App!");

		Calendar cal = Calendar.getInstance();

		// Start every 5 seconds
		mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				5 * 1000, mPintent);
	}

	public void stopHeadlessApp(View view) {
		mAlarm.cancel(mPintent);
		Log.i("publicize", "all services stopped");
	}

	public void startBluetoothServer(View view) {
		createBluetoothConnection();
	}

	private void createBluetoothConnection() {
		AcceptThread thread = new AcceptThread(new ConnectedListener() {

			@Override
			public void onConnected(BluetoothSocket socket) {
				// TODO Auto-generated method stub
				Log.i(Constants.TAG, "Main activity connected!");
				mConnectedThread = new ConnectedThread(socket, myHandler);
				mConnectedThread.start();
			}
		});
		thread.start();
	}

	private final Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			final int what = msg.what;
			switch (what) {
			case 0:
				doUpdate((byte[])msg.obj);
				break;
			default: 
				break;
			}
		}
	};

	private void doUpdate(byte[] bytes) {
		mConnectedThread.cancel();
		String appName;
		try {
			appName = new String(bytes, "UTF-8");
			mTitle.setText(appName);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createBluetoothConnection();
	}
}
