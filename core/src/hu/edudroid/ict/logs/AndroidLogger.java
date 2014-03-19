package hu.edudroid.ict.logs;

import hu.edudroid.interfaces.Logger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AndroidLogger implements Logger {

	public final static String TAG = AndroidLogger.class.getName();

	private static final String INFO = "i";
	private static final String DEBUG = "d";
	private static final String ERROR = "e";
	
	private String moduleName;
	private Context context;
	
	public AndroidLogger(String moduleName, Context context){
		this.moduleName = moduleName;
		this.context = context;
	}
	
	@Override
	public void e(String tag, String message) {
		Log.e(TAG+":"+tag, message);
		log(ERROR, System.currentTimeMillis(),  message);
	}

	@Override
	public void d(String tag, String message) {
		Log.d(TAG+":"+tag, message);
		log(DEBUG, System.currentTimeMillis(), message);
	}

	@Override
	public void i(String tag, String message) {
		Log.i(TAG+":"+tag, message);
		log(INFO, System.currentTimeMillis(), message);
	}
	
	private void log(String level, long timestamp, String message) {
		Intent intent = new Intent(context, UploadService.class);
		intent.putExtra(LogRecord.COLUMN_NAME_MODULE, moduleName);
		intent.putExtra(LogRecord.COLUMN_NAME_LOG_LEVEL, level);
		intent.putExtra(LogRecord.COLUMN_NAME_DATE, timestamp);
		intent.putExtra(LogRecord.COLUMN_NAME_MESSAGE, message);
		context.startService(intent);
	}
}