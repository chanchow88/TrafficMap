package com.cyber.trafficmap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service{
	private static final String TAG = UpdaterService.class.getSimpleName();
	private Updater updater;
	public boolean isRunning = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		// this is another process talking to this process
		
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		updater = new Updater();

		Log.d(TAG,"onCreate'd");
	}
	
	@Override
	public synchronized void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		updater.start();
		this.isRunning = true;
		Log.d(TAG,"onStart'd");
	}

	@Override
	public synchronized void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(this.isRunning){
			updater.interrupt();
		}
		updater = null;
		Log.d(TAG,"onDestroy'd");
	}

	////updater thread
	class Updater extends Thread {
		static final long DELAY = 60000;
		@Override
		public void run() {
			while (isRunning) {
				// TODO Auto-generated method stub

				try {
					Log.d(TAG, "updater running");
					MapUpdater mapupdater = new MapUpdater();
					mapupdater.update();
					this.sleep(DELAY);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					isRunning = false;
					e.printStackTrace();
				}
			}
		}
		
	}
}
