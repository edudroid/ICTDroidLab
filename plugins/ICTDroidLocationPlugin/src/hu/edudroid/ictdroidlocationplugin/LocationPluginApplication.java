package hu.edudroid.ictdroidlocationplugin;

import android.app.Application;

public class LocationPluginApplication extends Application {

	private static LocationPluginApplication	singleton;

	public static LocationPluginApplication getInstance(){
		return singleton;
	}

	@Override
	public void onCreate(){
		super.onCreate();
		singleton = this;
	}
}
