package hu.edudroid.module;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import android.util.Log;

public class AndroidLogger implements Logger {

	private Module mModule;
	private String mJarName;
	private String mClassName;
	
	public AndroidLogger(Module module, String jarName, String className){
		super();
		this.mModule = module;
		this.mJarName = jarName;
		this.mClassName = className;
	}
	
	@Override
	public void e(String tag, String message) {
		Log.e(tag, message);
	}

	@Override
	public void d(String tag, String message) {
		Log.e(tag, message);
	}

	@Override
	public void i(String tag, String message) {
		Log.i(tag, message);
	}

}
