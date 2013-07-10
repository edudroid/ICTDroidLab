package hu.edudroid.ict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {
	public static String readFile(String filePath) {
		return readFile(new File(filePath));
	}
	
	public static String readFile(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuffer result = new StringBuffer();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				result.append(line);
				result.append("\n");
			}
			
			reader.close();
			return result.toString();
		} catch (Exception e) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return null;
		}
	}

}
