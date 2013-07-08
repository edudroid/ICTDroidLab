package hu.edudroid.ict;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import android.util.Log;

public class ZipExporter {
	private static final int BUFFER = 2048;
	
	/**
	 * Compresses the contents of a folder - except .zip files - to a zip file in the folder.
	 * @param folder
	 * @param fileName
	 * @throws IOException 
	 */
	public static void compress(final File[] files, final File output) throws IOException {
		Log.d("zip_export","Exporting " + files.length + " files to " + output);
		// Gets the files - not recursive
		// Creates output, if file exists, overwrites it
		if (output.exists()) {
			output.delete();
		}
		try {
			output.createNewFile();
		} catch (Exception e) {
			throw new IOException("Couldn't create zip file");
		}
		BufferedInputStream origin = null;
		FileOutputStream dest = null;
		try {
			dest = new FileOutputStream(output);
		} catch (Exception e) {
			throw new IOException("Couldn't create output stream.");
		}
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		byte data[] = new byte[BUFFER];
		// Exports files
		Log.d("zip_export","Files " + files.length + " found.");
		for(int i=0;i < files.length;i++) { 
			Log.v("Compress", "Adding: " + files[i]);
			FileInputStream fi = null;
			try {
				fi = new FileInputStream(files[i]);
			} catch (Exception e) {
				throw new IOException("Couldn't read file " + files[i].getAbsolutePath());
			}
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry(files[i].getName());
			try {
				out.putNextEntry(entry);
			} catch (IOException e) {
				fi.close();
				throw new IOException("Couldn't add file.");
			}
			int count;
			try {
				while ((count = origin.read(data, 0, BUFFER)) != -1) { 
					out.write(data, 0, count);
				}
			} catch (IOException e) {
				throw new IOException("Couldn't write data to file.");
			}
			out.closeEntry();
			origin.close();
		}
		out.close();
	}
}