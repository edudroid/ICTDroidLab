package hu.edudroid.module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class AssetReader {
	
	/**
	 * Copy a file to the destination folder
	 * @param assetPath
	 * @param folder
	 * @param context
	 * @return
	 */
	public static File copyAsset(String assetPath, File folder, Context context) {
		Log.i(TAG, "Copying asset " + assetPath);
		if (assetPath.startsWith("/")) {
			assetPath = assetPath.substring(1);
		}
		String fileName = new File(assetPath).getName();
		folder.mkdirs();
    	File outFile = new File(folder, fileName);
	    try {
	        InputStream in = context.getAssets().open(assetPath);
	    	FileOutputStream outStream = new FileOutputStream(outFile);	    	
	        int read;
	        byte[] buffer = new byte[4096];
	        while ((read = in.read(buffer)) > 0) {
	        	outStream.write(buffer, 0, read);
	        }
	        outStream.close();
	        in.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
        return outFile;
	}
}