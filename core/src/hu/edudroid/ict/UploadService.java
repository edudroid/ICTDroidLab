package hu.edudroid.ict;

import hu.edudroid.ict.utils.ServerUtilities;

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
import android.telephony.TelephonyManager;
import android.util.Log;

public class UploadService extends IntentService { 

	private static final String TAG = "UploadService"; 
	private static final String TMP_FOLDER = "capture_compressed_tmp";
	public static final String INPROGRESS_SUFFIX = "inprogress";
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS", Locale.UK);
	
	public static final File OUTPUT_FOLDER = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ictdroidlab_log");

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
	
	public static void upload(Context context, String imei) {
		
		try {
			synchronized (context) {
				boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
				if(!isSDPresent) {
					return;
				}
				
				try {
					if (!OUTPUT_FOLDER.exists()) {
						Log.e(TAG, "Path " + OUTPUT_FOLDER.getAbsolutePath() + " does not exist.");
						return;
					}else if (!OUTPUT_FOLDER.isDirectory()) {
						Log.e(TAG, "Path " + OUTPUT_FOLDER.getAbsolutePath() + " is not a directory.");
						return;
					} else if (OUTPUT_FOLDER.listFiles().length == 0) {
						Log.e(TAG, "Folder " + OUTPUT_FOLDER.getAbsolutePath() + " is empty.");
						return;
					} else {
						Log.d(TAG, OUTPUT_FOLDER.listFiles().length + " files found.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				
				String sdPath = Environment.getExternalStorageDirectory().toString();		
				File tmpFolder = new File(sdPath, TMP_FOLDER);
				if (!tmpFolder.exists()){
					tmpFolder.mkdirs();
				}
				String deviceName = "ICTDroidLab";
				String fileName = deviceName + "_" +  formatter.format(new Date(System.currentTimeMillis())) + ".zip";
				File zip = new File(tmpFolder, fileName);
				File[] files = OUTPUT_FOLDER.listFiles(progressFilter);
				try {
					if ((files!=null) && (files.length > 0)) {
						ZipExporter.compress(files, zip);
					} else {
						Log.d(TAG,"No files to zip");
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				
				String uploadURL=ServerUtilities.get("http://ictdroidlab.appspot.com/getLogUrl");
				
				LinkedList<BasicNameValuePair> postParameters = new LinkedList<BasicNameValuePair>();
				String blobKey = HttpUtils.postMultipartWithFile(uploadURL, postParameters, "logFile", zip, null, null);
				ServerUtilities.get("http://ictdroidlab.appspot.com/uploadLog?imei="+imei+"&blobkey="+blobKey);
				Log.i(TAG,"Logs has been uploaded succesfully to: http://ictdroidlab.appspot.com/uploadLog?imei="+imei+"&blobkey="+blobKey);
				zip.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		synchronized (this) {
			
			TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
	        String imei=mngr.getDeviceId(); 
			
			upload(this, imei);
		}
	}
}