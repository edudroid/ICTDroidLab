package hu.edudroid.module;

import hu.edudroid.ict.HttpUtils;
import java.io.File;
import java.util.LinkedList;
import org.apache.http.message.BasicNameValuePair;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class ModuleFileUploader extends AsyncTask<String, Void, Boolean> {

	private static final String	URL				= "http://innoid.hu/CellInfo/fileupload.php";
	private static final String	KEY_MODULNAME	= "modulename";
	private static final String	KEY_DEVICE_ID	= "deviceid";
	

	private UploaderResultHandler	mUploaderResult;
	private String				mFileName;

	public ModuleFileUploader(UploaderResultHandler result) {
		mUploaderResult = result;
	}
	
	
	@Override
	protected Boolean doInBackground(String... params){
		if (params[0] == null)
			return false;

		mFileName = params[0];
		File root = Environment.getExternalStorageDirectory();
		File logFile = new File(root, params[0]);
		if (root.canWrite()){
			try{
				LinkedList<BasicNameValuePair> pairs = new LinkedList<BasicNameValuePair>();
				pairs.add(new BasicNameValuePair(KEY_MODULNAME, params[0]));
				pairs.add(new BasicNameValuePair(KEY_DEVICE_ID, params[1]));
				final String response = HttpUtils.postMultipartWithFile(URL,
																		pairs,
																		logFile);
				if (response == null || response.equals("Error")) {
					if (response != null)
						Log.e("RESPONSE", response);
					return false;
				}
				Log.e("RESPONSE", response);
				return true;
			}
			catch (Exception e){
				return false;
			}
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success){
		if (mUploaderResult == null)
			return;

		if (success)
			mUploaderResult.successUpload(mFileName);
		else
			mUploaderResult.uploadError(mFileName);
	}

	public interface UploaderResultHandler {

		public void successUpload(String fileName);

		public void uploadError(String fileName);
	}

}
