package hu.edudroid.ict;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class UploadService extends IntentService { 

	private static final String LOG_TAG = "UploadService"; 
	private static final String TMP_FOLDER = "capture_compressed_tmp";
	public static final String INPROGRESS_SUFFIX = "inprogress";
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS", Locale.UK);
	
	public static final File OUTPUT_FOLDER = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ictdroidlab_log");
	
	public static final String SERVER_URL = "http://152.66.244.83/pages/mobile_api/uploadLog.php";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin"; 

	private static final FilenameFilter progressFilter = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String filename) {
			return !filename.endsWith(INPROGRESS_SUFFIX);
		}
	};

	public UploadService() {
		super("UploadService");
	}
	
	public static int getFileCount(){
		if (!OUTPUT_FOLDER.exists()) {
			return -1;
		}
		File[] files = OUTPUT_FOLDER.listFiles(progressFilter);
		return files.length;
	}
	
	public static void upload(Context context) {
		try {
			synchronized (context) {
				//SharedPreferences prefs = context.getSharedPreferences(MeasurementUploaderActivity.PREF_NAME, MODE_PRIVATE);
				
				// Check SD card
				boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
				if(!isSDPresent) {
					Log.e(LOG_TAG, "No SD card present.");
					// File should be on SD card, can do nothing
					return;
				} else {
					Log.d(LOG_TAG, "SD card present");			
				}
				
				try {
					if (!OUTPUT_FOLDER.exists()) {
						Log.e(LOG_TAG, "Path " + OUTPUT_FOLDER.getAbsolutePath() + " does not exist.");
						return;
					}else if (!OUTPUT_FOLDER.isDirectory()) {
						Log.e(LOG_TAG, "Path " + OUTPUT_FOLDER.getAbsolutePath() + " is not a directory.");
						return;
					} else if (OUTPUT_FOLDER.listFiles().length == 0) {
						Log.d(LOG_TAG, "Folder " + OUTPUT_FOLDER.getAbsolutePath() + " is empty.");
						return;
					} else {
						Log.d(LOG_TAG, OUTPUT_FOLDER.listFiles().length + " files found.");
					}
				} catch (Exception e) {
					Log.e(LOG_TAG, "Error reading files.", e);
					return;
				}
				
				// Compresses and send each measurement
				// Compresses folder
				String sdPath = Environment.getExternalStorageDirectory().toString();		
				File tmpFolder = new File(sdPath, TMP_FOLDER);
				if (!tmpFolder.exists()){
					tmpFolder.mkdirs();
				}
				//String deviceName = prefs.getString(MeasurementUploaderActivity.DEVICE_NAME_KEY, "");
				String deviceName = "ICTDroidLab";
				String fileName = deviceName + "_" +  formatter.format(new Date(System.currentTimeMillis())) + ".zip";
				File zip = new File(tmpFolder, fileName);
				File[] files = OUTPUT_FOLDER.listFiles(progressFilter);
				try {
					if ((files!=null) && (files.length > 0)) {
						ZipExporter.compress(files, zip);
					} else {
						Log.d(LOG_TAG,"No files to zip");
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				
				if (SERVER_URL == null || USERNAME == null || PASSWORD == null){
					Log.w(LOG_TAG, "Invalid settings or no network access.");
					//SharedPrefsLogger.log("Invalid settings or no network access: \n Server URL " + (serverUrl==null?"NULL":serverUrl) + "\nUser name " + (userName==null?"NULL":userName) + "\nPassword " + (password==null?"NULL":"PRESENT"), context);
					return;
				}
				
				LinkedList<BasicNameValuePair> postParameters = new LinkedList<BasicNameValuePair>();
				postParameters.add(new BasicNameValuePair("userName", USERNAME));
				postParameters.add(new BasicNameValuePair("password", PASSWORD));		
				String uploadResult = HttpUtils.postMultipartWithFile(SERVER_URL, postParameters, "userFile", zip, null, null);
				zip.delete();
				if (uploadResult != null) {
					if (uploadResult.startsWith("OK")) {
						// If upload successful, delete files
						for (File file : files){
							try{
								file.delete();
							} catch(Exception e) {
								Log.e(LOG_TAG, "Couldn't delete file " + file.getName());					
							}
						}
						//prefs.edit().putLong(MeasurementUploaderActivity.LAST_UPLOAD_KEY, System.currentTimeMillis()).commit();
						Log.d(LOG_TAG, "Upload successful with result: " + uploadResult);
						//SharedPrefsLogger.log("Upload successful with result " + uploadResult + ".", context);
					} else {
						Log.d(LOG_TAG, "Upload failed with result: " + uploadResult);
						//SharedPrefsLogger.log("Upload failed with result " + uploadResult + ".", context);
					}
				} else {
					//SharedPrefsLogger.log("Upload failed.", context);
					Log.d(LOG_TAG,"Something went wrong");
				}
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error uploading files.", e);
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		synchronized (this) {
			Log.d(LOG_TAG, "Upload service called."); 
			/*
			SharedPreferences prefs = getSharedPreferences(MeasurementUploaderActivity.PREF_NAME, MODE_PRIVATE);
			long periodicity = prefs.getLong(MeasurementUploaderActivity.PREFERRED_PERIODICITY, SettingsActivity.PERIODICITIES[0]);
			long lastUpload = prefs.getLong(MeasurementUploaderActivity.LAST_UPLOAD_KEY, 0);
			
			long periodicity = 0;
			long lastUpload = 0;
			
			long timeTilNextUpload = (lastUpload + periodicity) - System.currentTimeMillis();
			if (timeTilNextUpload > 0) {
				timeTilNextUpload = timeTilNextUpload / 1000;
				String timeDiffString = (timeTilNextUpload % 60 > 9?"":"0") + timeTilNextUpload % 60 + " secs";
				timeTilNextUpload = timeTilNextUpload / 60;
				timeDiffString = (timeTilNextUpload % 60 > 9?"":"0") + timeTilNextUpload % 60 + " mins, " + timeDiffString;
				timeTilNextUpload = timeTilNextUpload / 60;
				timeDiffString = timeTilNextUpload + " hours, " + timeDiffString;
				//SharedPrefsLogger.log("Time till next upload: " + timeDiffString , this);
				return;
			}
			*/
			upload(this);
		}
	}
}