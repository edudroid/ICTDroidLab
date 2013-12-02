package hu.edudroid.ictpluginsample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class EventService extends Service {

	public static final String PLUGIN_NAME="WiFi Plugin";
	public static final String PING_METHOD_NAME="ping";
	public static final String VERSION_CODE="v1.0";
	
	private boolean started = false;
	
	private EventBinder binder = new EventBinder(this);
	
	public static class EventBinder extends Binder {
		private EventService service;
		
		public EventBinder(EventService service) {
			this.service = service;
		}
		
		public EventService getService() {
			return service;
		}
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
		if (!started) {
			SamplePlugin samplePlugin = SamplePlugin.getInstance();
			started = true;
			while(true) {
				try {
					Thread.sleep((long)(Math.random() * 1000));
					samplePlugin.event(SamplePlugin.FIRST_SAMPLE_EVENT_NAME, null, this);
					Thread.sleep((long)(Math.random() * 1000));
					samplePlugin.event(SamplePlugin.SECOND_SAMPLE_EVENT_NAME, null, this);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
}