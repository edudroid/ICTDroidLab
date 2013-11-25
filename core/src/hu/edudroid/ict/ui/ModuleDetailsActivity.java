package hu.edudroid.ict.ui;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import hu.edudroid.interfaces.Plugin;

public class ModuleDetailsActivity extends ActivityBase {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		return false;
	}

}
