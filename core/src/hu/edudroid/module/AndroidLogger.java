package hu.edudroid.module;

import hu.edudroid.interfaces.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.util.Log;

public class AndroidLogger implements Logger {

	public final static String TAG = AndroidLogger.class.getName();
	private static final long FILE_TIME = 60000;
	
	private Date date = new Date();
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS", Locale.UK);
	
	private String moduleName;
	
	private HashMap<String, Long> fileStartTimes = new HashMap<String, Long>();
	private HashMap<String, FileWriter> writers = new HashMap<String, FileWriter>();
	private String timeLabel;
	
	public AndroidLogger(String moduleName){
		this.moduleName = moduleName;
	}
	
	@Override
	public void e(String tag, String message) {
		Log.e(TAG+":"+tag, message);
		saveLogLine(moduleName, System.currentTimeMillis(), "error: "+ message);
	}

	@Override
	public void d(String tag, String message) {
		Log.d(TAG+":"+tag, message);
		saveLogLine(moduleName, System.currentTimeMillis(), "debug: "+ message);
	}

	@Override
	public void i(String tag, String message) {
		Log.i(TAG+":"+tag, message);
		saveLogLine(moduleName, System.currentTimeMillis(), "info: "+ message);
	}
	
	public void saveLogLine(String task, long timestamp, String message) {
		
		File baseFolder = FileManager.BASE_FOLDER;
		if(!baseFolder.exists()){
			baseFolder.mkdir();
		}
		
		File file = new File(baseFolder, task + FileManager.INPROGRESS_SUFFIX);
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
			File oldFile = new File(baseFolder, task + FileManager.SEPARATOR + timeLabel + FileManager.LOG_SUFFIX); 
			file.renameTo(oldFile);
			file = new File(baseFolder, task + FileManager.INPROGRESS_SUFFIX);
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
