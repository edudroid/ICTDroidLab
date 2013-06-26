package hu.edudroid.module;

import hu.edudroid.interfaces.Logger;
import android.util.Log;

public class AndroidLogger implements Logger {

	@Override
	public void e(String tag, String message) {
		Log.e(tag, message);
	}

	@Override
	public void d(String tag, String message) {
		Log.e(tag, message);
	}

}
