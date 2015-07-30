package hu.edudroid.ict.logs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.util.Log;

public class  LogZipper {
	
	private static final String TAG = LogZipper.class.getName();
	private static final int BUFFER = 2048;
	
	public static boolean  mapToCSVfile(Map<String, String> map, String fileName) {
		try {			
			OutputStream outputStream = new FileOutputStream(fileName);
			Writer       out       = new OutputStreamWriter(outputStream,"UTF-8");
			
			String eol = System.getProperty("line.separator");
			Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				String replacedKey = entry.getKey().replaceAll("\\t", "   ");
				String replacedValue = entry.getValue().replaceAll("\\t", "   ");
				out.write(replacedKey);
				out.write("\t");
				out.write(replacedValue);		
				
				//for not writing an empty line at the end
				if (it.hasNext()) {
					out.write(eol);
				}				
			}
			out.flush();
			out.close();
			outputStream.close();
			return true;
	    } catch(IOException i) {
	    	Log.e(TAG, "Error when making file from map!", i);
	    	
	    	//delete file, maybe there is a corrupted version of it
			int pos = fileName.lastIndexOf("/") + 1;			
			File file = new File(fileName.substring(0, pos-1), fileName.substring(pos));
			file.delete();	    	
	    	return false;
	    } catch(Exception e) {
	    	Log.e(TAG, "Corrupted map or some kind of error when making file from map!", e);
	    	
	    	//delete file, maybe there is a corrupted version of it
			int pos = fileName.lastIndexOf("/") + 1;			
			File file = new File(fileName.substring(0, pos-1), fileName.substring(pos));
			file.delete();
	    	return false;
	    }
	}

	public static boolean zipFile (String inputFileName, String zipFileName) {
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[BUFFER];

			FileInputStream fi = new FileInputStream(inputFileName);
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(inputFileName.substring(inputFileName.lastIndexOf("/") + 1));
			out.putNextEntry(entry);
			int count;

			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();
			out.close();
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Error when zipping file!", e);
			
			//try to delete zip, maybe there is a corrupted version of it
			int pos = zipFileName.lastIndexOf("/") + 1;			
			File zipFile = new File(zipFileName.substring(0, pos-1), zipFileName.substring(pos));
			zipFile.delete();
			return false;
		}
	}
	
	public static boolean mapToZip(Context context, Map<String, String> map) {	
		File myDir = new File(context.getFilesDir(),"log");
		if (!myDir.exists()) {			
			myDir.mkdir();
		} 			
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.getDefault());
		Date date = new Date(System.currentTimeMillis());
		String dateString = fmt.format(date);
		
		String fileName = myDir.getAbsolutePath()+"/"+ dateString;		
		if (!mapToCSVfile(map, fileName+".log")) {
			return false;
		}
		if (!zipFile(fileName+".log",fileName+".zip")) {
			return false;
		}
		 
		File deleteFile = new File(myDir.getAbsolutePath(), dateString+".log");
		
		if (!deleteFile.delete()) {
			Log.e(TAG, "Error when trying to remove temporary file used for zipping!");
		}
		 
		return true;			
	}
	
	public static ArrayList<File> getZipFileList(Context context) {
		ArrayList<File> logFileList = new ArrayList<File>();
		File myDir = new File(context.getFilesDir(),"log");
		if (!myDir.exists()) {			
			return logFileList;
		}		
		for (File f : myDir.listFiles()) {
			String fileName = f.getName();
			if (fileName.substring(fileName.length()-3).equals("zip")) {
				logFileList.add(f);
			}
		}
		
		return logFileList;
	}
}
