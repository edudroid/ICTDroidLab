package hu.edudroid.ict.logs;

import hu.edudroid.interfaces.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class AndroidLogger implements Logger {

	public final static String TAG = AndroidLogger.class.getName();
	
	private Date date = new Date();
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS", Locale.UK);
	
	private String moduleName;
	
	public AndroidLogger(String moduleName){
		this.moduleName = moduleName;
	}
	
	@Override
	public void e(String tag, String message) {
		Log.e(TAG+":"+tag, message);
		log(moduleName, System.currentTimeMillis(), "error: "+ message);
	}

	@Override
	public void d(String tag, String message) {
		Log.d(TAG+":"+tag, message);
		log(moduleName, System.currentTimeMillis(), "debug: "+ message);
	}

	@Override
	public void i(String tag, String message) {
		Log.i(TAG+":"+tag, message);
		log(moduleName, System.currentTimeMillis(), "info: "+ message);
	}
	
	private void log(String task, long timestamp, String message) {
		// TODO Try uploading log line
		
	}

}
