package hu.edudroid.module;

import java.io.File;

import android.os.Environment;

public class FileManager {
	private static final String LOG_FOLDER_NAME = "ictdroidlab_log";
	public static final String SEPARATOR = "_";
	public static final String LOG_SUFFIX = ".log";
	public static final String INPROGRESS_SUFFIX = ".inprogress";	
	
	public static final File BASE_FOLDER = new File(Environment.getExternalStorageDirectory(), FileManager.LOG_FOLDER_NAME);

	static
	{
		BASE_FOLDER.mkdirs();
	}
	
	public static final void deleteAllLogs() {
		File[] files = BASE_FOLDER.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	/**
	 * Returns the total size of log files in Bytes
	 * @return
	 */
	public static long getTotalLogFileSize() {
		File[] files = BASE_FOLDER.listFiles();
		long total = 0;
		for (File file : files) {
			total += file.length();
		}
		return total;
	}
}
