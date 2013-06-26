package hu.edudroid.module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class AssetReader {
	private static final String TAG = "AssetReader";
	
	public static File copyAssetToInternalStorage(String assetPath, Context context) {
		Log.d(TAG, "Copying asset " + assetPath);
    	File outFile = new File(context.getFilesDir(),assetPath);
		Log.d(TAG, "Output file: " + outFile.getAbsolutePath());
	    try {
	    	FileOutputStream outStream = context.openFileOutput(assetPath, Context.MODE_PRIVATE);	    	
	        InputStream in = context.getAssets().open(assetPath);
	        int read;
	        byte[] buffer = new byte[4096];
	        while ((read = in.read(buffer)) > 0) {
	        	outStream.write(buffer, 0, read);
	        }
	        outStream.close();
	        in.close();
	        Log.e(TAG,"Asset copied!");
	    } catch (IOException e) {
	    	e.printStackTrace();
	       Log.e(TAG, "Couldn't copy asset " + assetPath + " to internal file " + assetPath + " " + e.getMessage());
	    }
        return outFile;
	}
}