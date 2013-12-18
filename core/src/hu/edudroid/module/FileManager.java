package hu.edudroid.module;

import java.io.File;

import android.os.Environment;

public class FileManager {
	private static final String LOG_FOLDER_NAME = "ictdroidlab_log";
	private static final String TMP_FOLDER_NAME = "capture_compressed_tmp";
	public static final String SEPARATOR = "_";
	public static final String LOG_SUFFIX = ".log";
	public static final String INPROGRESS_SUFFIX = ".inprogress";	
	
	
	public static final File LOG_FOLDER = new File(Environment.getExternalStorageDirectory(), LOG_FOLDER_NAME);
	public static final File TMP_FOLDER = new File(Environment.getExternalStorageDirectory(), TMP_FOLDER_NAME);

	static
	{
		TMP_FOLDER.mkdirs();
		LOG_FOLDER.mkdirs();
	}
	
	public static final void deleteAllLogs() {
		File[] files = LOG_FOLDER.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	public static void deleteAllZip() {
		File[] files = TMP_FOLDER.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	/**
	 * Returns the total size of log files in Bytes
	 * @return
	 */
	public static long getTotalLogFileSize() {
		File[] files = LOG_FOLDER.listFiles();
		long total = 0;
		for (File file : files) {
			total += file.length();
		}
		return total;
	}

	public static float getTotalZipFileSize() {
		File[] files = TMP_FOLDER.listFiles();
		long total = 0;
		for (File file : files) {
			total += file.length();
		}
		return total;
	}
}
