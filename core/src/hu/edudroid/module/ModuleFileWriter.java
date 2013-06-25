package hu.edudroid.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class ModuleFileWriter extends AsyncTask<String, Void, Boolean> {

	private static final String DELIMITTER = ";";
	
	
	private String				mFileName;
	private FileWriterResultHandler	mResultListener;
	private String				mNewLine;

	public ModuleFileWriter(String fileName, FileWriterResultHandler result) {
		mFileName = fileName;
		mResultListener = result;
	}

	@Override
	protected Boolean doInBackground(String... params){
		if (params[0] == null)
			return false;
		mNewLine = params[0];
		StringBuilder contents = new StringBuilder();
		File root = Environment.getExternalStorageDirectory();
		File logFile = new File(root, mFileName);
		if (root.canWrite()){
			try{
				if (logFile.exists()) {
					BufferedReader input = new BufferedReader(new FileReader(logFile));
					String line = null;
					while ((line = input.readLine()) != null){
						contents.append(line + "\n");
					}
					input.close();
				}
				else
					logFile.createNewFile();
				
				contents.append(System.currentTimeMillis() + DELIMITTER + params[0]);
				FileWriter logWriter = new FileWriter(logFile);
				BufferedWriter out = new BufferedWriter(logWriter);
				out.write(contents.toString());
				out.close();
				return true;
			}

			catch (IOException e){
				Log.e("test", "Could not read/write file " + e.getMessage());
				return false;
			}
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result){
		if (mResultListener == null)
			return;
		if (result)
			mResultListener.successFileWrite();
		else
			mResultListener.fileWriteError(mNewLine);
	}

	public interface FileWriterResultHandler {

		public void successFileWrite();

		public void fileWriteError(String newData);
	}

}
