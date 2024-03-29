package com.example.publicize;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CurrentAppService extends IntentService {

	public CurrentAppService() {
		super(null);
		Log.i(Constants.TAG, "Service is being constructed");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent workIntent) {
		// Gets data from the incoming Intent
		Log.i(Constants.TAG, "Intent Starting");

		String dataString = workIntent.getDataString();

		ActivityManager am = (ActivityManager) this
				.getSystemService(Activity.ACTIVITY_SERVICE);
		final String packageName = am.getRunningTasks(1).get(0).topActivity
				.getPackageName();

		//notify(packageName);

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			notify("Doesn't support bluetooth");
		}
		if (!mBluetoothAdapter.isEnabled()) {
			//Intent enableBtIntent = new Intent(
			//BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			int count = 0;
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				count = count+1;
				Log.i("publicize", device.getName() + "\n" + device.getAddress());
				new ConnectThread(device, new ConnectedListener() {
					
					@Override
					public void onConnected(BluetoothSocket socket) {
						// TODO Auto-generated method stub
						Log.i(Constants.TAG, "connected event from service!");
						ConnectedThread thread = new ConnectedThread(socket, null);
						byte[] bytes;
						try {
							Log.i(Constants.TAG, packageName);
							bytes = packageName.getBytes("UTF-8");
							thread.write(bytes);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}
			Log.i("publicize", "There are " + count + "devices connected");
		}
	}

	private void notify(String notificationText) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Publicize Notification")
				.setContentText(notificationText);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(24, mBuilder.build());
	}

}
