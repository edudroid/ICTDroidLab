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

    	File outFile = new File(context.getFilesDir(),assetPath);

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
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
        return outFile;
	}
}