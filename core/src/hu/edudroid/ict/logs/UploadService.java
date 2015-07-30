package hu.edudroid.ict.logs;

import hu.edudroid.ict.utils.CoreConstants;
import hu.edudroid.ict.utils.HttpUtils;
import hu.edudroid.ict.utils.ServerUtilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.gcm.GCMRegistrar;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class UploadService extends IntentService {

	public static final String NAME = UploadService.class.getName();

	private static final String TAG = UploadService.class.getName();

	private static final int UPLOAD_BATCH_SIZE = 20;	
	private static final int UPLOAD_SIZE_ZIP = 10;
	private static final String LOG_COUNT = "log_count";
	
	private LogDatabaseManager databaseManager;
	
	public UploadService() {
		super(NAME);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Upload service created.");
		databaseManager = new LogDatabaseManager(this.getApplicationContext());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");
		
		if (intent.getExtras()==null) {
			Log.d(TAG, "Intent is null, no log");						
		} else {
			LogRecord logRecord = new LogRecord(
					intent.getStringExtra(LogRecord.COLUMN_NAME_MODULE),
					intent.getStringExtra(LogRecord.COLUMN_NAME_LOG_LEVEL),
					intent.getLongExtra(LogRecord.COLUMN_NAME_DATE, 0),
					intent.getStringExtra(LogRecord.COLUMN_NAME_MESSAGE));			
			databaseManager.saveRecord(logRecord);
		}
		
		//DEFAULT MODE IS INSTANT
		int mode = CoreConstants.getInt(CoreConstants.UPLOAD_MODE, 0, getApplicationContext());
		
		try {
			boolean result = false;
			List<LogRecord> recordsToUpload = null;
			if (mode==0) {
				Log.d(TAG, "Upload mode: instant");
				recordsToUpload = databaseManager.getRecords(UPLOAD_BATCH_SIZE);
				result = uploadLogs(recordsToUpload, this.getApplicationContext());
				if (result) {
					saveUploadDate();
					Log.d(TAG, "Log uploaded");
					for (LogRecord record : recordsToUpload) {
						databaseManager.purgeRecord(record.getId());
					}
				}
			} else if (mode==1) {
				Log.d(TAG, "Upload mode: zip");
				//converting logs to zips
				while(databaseManager.getDatabaseRowCount() > UPLOAD_SIZE_ZIP -1) {
					recordsToUpload = databaseManager.getRecords(UPLOAD_SIZE_ZIP);
					if (makingZips(recordsToUpload, this.getApplicationContext())) {
						for (LogRecord record : recordsToUpload) {
							databaseManager.purgeRecord(record.getId());
						}
					}
				}
				recordsToUpload = databaseManager.getRecords(UPLOAD_SIZE_ZIP);
				
				//uploading and deleting zips if upload is successful				
				uploadZips(this.getApplicationContext());
			}
			
		} catch (Exception e) {
			Log.e(TAG, "Couldn't upload log.",e);
		}	
	
	}
	
	public static void uploadZips(Context context) {
		uploadZips(context, 1);
	}
	
	public static void uploadZips(Context context, int runCounter) {		
		ArrayList<File> fileList = LogZipper.getZipFileList(context);
		for (int i=0;i<fileList.size();i++) {
			String uploadUrl = HttpUtils.get(ServerUtilities.PORTAL_URL+"getLogUrl");
			String response = HttpUtils.post(uploadUrl, fileList.get(i), context);
			if (response==null) {
				Log.i(TAG,"No response from the server");
				return;
			}
			response = response.trim();
			if (response.endsWith(" logs were uploaded succesfully")) {
				int uploadedRecords = -1;
				try {
					uploadedRecords = Integer.parseInt(response.substring(0, response.length() - " logs were uploaded succesfully".length()));
				} catch (Exception e) {
					Log.e(TAG,"Error parsing server response " + response, e);
				}
				if (uploadedRecords == UPLOAD_SIZE_ZIP) {
					Log.d(TAG,"Uploaded " + uploadedRecords + " log lines.");
					// deleting uploaded zips
					try {
						fileList.get(i).delete();
					} catch (Exception e) {
						Log.e(TAG, "Can't delete the following file: "+ fileList.get(i).getName());
					}
					Log.d(TAG,"Deleted ZIP: " + fileList.get(i).getName());
				}
			} /*else if (response.equals("ERROR[3]: No device key in session.")) {
		    	// Register device with server
				TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		        String imei = mngr.getDeviceId();
		        String sdkVersion=String.valueOf(android.os.Build.VERSION.SDK_INT);
				String deviceName = CoreConstants.getString(CoreConstants.DEVICE_NAME_KEY, CoreConstants.DEFAULT_DEVICE_NAME, context);
		    	boolean registered = ServerUtilities.registerDevice(context, imei, deviceName, GCMRegistrar.getRegistrationId(context), sdkVersion, null);
				Log.i("Device registered", "Success: " + registered);
				if (registered && runCounter>0) {
					Log.d(TAG,"COUNTER: " + runCounter);
					uploadZips(context, runCounter-1);
				}
				else {
					Log.e(TAG,"Unexpected server response " + response);
				}
			} */else {
				Log.e(TAG,"Unexpected server response " + response);
			}
		}		
	}
	
	public static boolean makingZips (List<LogRecord> recordsToUpload, Context context) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOG_COUNT, Integer.toString(recordsToUpload.size()));
		for (int i = 0; i < recordsToUpload.size(); i++) {
			LogRecord record = recordsToUpload.get(i);
			params.put(i + " " + LogRecord.COLUMN_NAME_MODULE, record.getModule());
			params.put(i + " " + LogRecord.COLUMN_NAME_LOG_LEVEL, record.getLogLevel());
			params.put(i + " " + LogRecord.COLUMN_NAME_DATE, Long.toString(record.getDate()));
			params.put(i + " " + LogRecord.COLUMN_NAME_MESSAGE, record.getMessage());
		}
		// Add imei
		TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
        String imei = mngr.getDeviceId();
        Log.d(TAG, "IMEI: " + imei);
		params.put(ServerUtilities.IMEI, imei);
		
		if (!LogZipper.mapToZip(context, params)) {
			Log.e(TAG, "Something went wrong while making upload files!");
		}
		
		return true;
	}
	
	public static boolean uploadLogs(List<LogRecord> recordsToUpload, Context context) {
		return uploadLogs(recordsToUpload, context, 1);
	}
	
	public static boolean uploadLogs(List<LogRecord> recordsToUpload, Context context, int runCounter) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOG_COUNT, Integer.toString(recordsToUpload.size()));
		for (int i = 0; i < recordsToUpload.size(); i++) {
			LogRecord record = recordsToUpload.get(i);
			params.put(i + " " + LogRecord.COLUMN_NAME_MODULE, record.getModule());
			params.put(i + " " + LogRecord.COLUMN_NAME_LOG_LEVEL, record.getLogLevel());
			params.put(i + " " + LogRecord.COLUMN_NAME_DATE, Long.toString(record.getDate()));
			params.put(i + " " + LogRecord.COLUMN_NAME_MESSAGE, record.getMessage());
		}
		// Add imei
		TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
        String imei = mngr.getDeviceId(); 
        Log.d(TAG, "IMEI:" + imei);
		params.put(ServerUtilities.IMEI, imei);
		Log.e(TAG,"Uploading " + recordsToUpload.size() + " records.");		
		String response = HttpUtils.post(ServerUtilities.PORTAL_URL + "uploadLog", params, context);	
		if (response==null) {
			Log.i(TAG,"No response from the server");
			return false;
		}		
		response = response.trim();
		if (response.endsWith(" logs were uploaded succesfully")) {
			int uploadedRecords = -1;
			try {
				uploadedRecords = Integer.parseInt(response.substring(0, response.length() - " logs were uploaded succesfully".length()));
			} catch (Exception e) {
				Log.e(TAG,"Error parsing server response " + response, e);
			}
			if (uploadedRecords == recordsToUpload.size()) {
				Log.d(TAG,"Uploaded " + uploadedRecords + " log lines.");
				return true;
			}
		} /*else if (response.equals("ERROR[3]: No device key in session.")) {
	    	// Register device with server
	        String sdkVersion=String.valueOf(android.os.Build.VERSION.SDK_INT);
			String deviceName = CoreConstants.getString(CoreConstants.DEVICE_NAME_KEY, CoreConstants.DEFAULT_DEVICE_NAME, context);
	    	boolean registered = ServerUtilities.registerDevice(context, imei, deviceName, GCMRegistrar.getRegistrationId(context), sdkVersion, null);
			Log.i("Device registered", "Success: " + registered);
			if (registered && runCounter>0) {
				Log.d(TAG,"COUNTER: " + runCounter);
				uploadLogs(recordsToUpload,context, runCounter-1);
			}
			else {
				Log.e(TAG,"Unexpected server response " + response);
			}
		}*/else {
			Log.e(TAG,"Unexpected server response " + response);
		}
		return false;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Upload service destroyed.");
		databaseManager.destroy();
		super.onDestroy();
	}
	
	private void saveUploadDate() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
		String dateString = fmt.format(date);
		
		CoreConstants.saveString(CoreConstants.LAST_LOG_UPDATE_DATE, dateString, getApplicationContext());
	}
}