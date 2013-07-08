package hu.edudroid.module;

import hu.edudroid.ict.UploadService;
import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

public class AndroidLogger implements Logger {

	public final static String TAG = "Android Logger";
	private static final long FILE_TIME = 60000;
	private static final File OUTPUT_FOLDER=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ictdroidlab_log");
	
	private Date date = new Date();
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS", Locale.UK);
	
	
	private HashMap<String, Long> fileStartTimes = new HashMap<String, Long>();
	private HashMap<String, FileWriter> writers = new HashMap<String, FileWriter>();
	private String timeLabel;
	
	private Module mModule;
	private String mJarName;
	private String mClassName;
	
	public AndroidLogger(Module module, String jarName, String className){
		super();
		this.mModule = module;
		this.mJarName = jarName;
		this.mClassName = className;
	}
	
	@Override
	public void e(String tag, String message) {
		Log.e(tag, message);		
	}

	@Override
	public void d(String tag, String message) {
		Log.e(tag, message);
	}

	@Override
	public void i(String tag, String message) {
		Log.i(tag, message);
	}
	
	public void saveLogLine(String task, long timestamp, String message) {
		
		File base_folder=new File(Environment.getExternalStorageDirectory()+"/ictdroidlab_log");
		if(!base_folder.isDirectory()){
			base_folder.mkdir();
		}
		
		File file = new File(OUTPUT_FOLDER, task + "." + UploadService.INPROGRESS_SUFFIX);
		date.setTime(timestamp);
		timeLabel = formatter.format(date);
		// Find writer
		FileWriter writer = writers.get(task);
		if (writer == null) {
			fileStartTimes.put(task, System.currentTimeMillis());
			try {
				file.createNewFile();
				Log.d(TAG,"New file created for " + task);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			try {
				writer = new FileWriter(file);
				writers.put(task, writer);
				Log.d(TAG,"FileWriter" + task);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		// Check if file's time has ended
		if (!fileStartTimes.containsKey(task)) {
			fileStartTimes.put(task, System.currentTimeMillis());
		}
		if (fileStartTimes.get(task) + FILE_TIME < System.currentTimeMillis()) {
			Log.d(TAG,"File for " + task + " reached max time.");
			// If over limit, rename file, start new in progress file
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writers.remove(task);
			File oldFile = new File(OUTPUT_FOLDER, task + "_" +timeLabel + ".log"); 
			file.renameTo(oldFile);
			File newOldFile = new File(OUTPUT_FOLDER, task + "_" +timeLabel + ".log");
			newOldFile.exists();
			file = new File(OUTPUT_FOLDER, task + "." + UploadService.INPROGRESS_SUFFIX);
			fileStartTimes.put(task, System.currentTimeMillis());
			try {
				writer = new FileWriter(file);
				writers.put(task, writer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		try {
			writer.write(timeLabel);
			writer.write(" ");
			writer.write(message);
			writer.write("\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
