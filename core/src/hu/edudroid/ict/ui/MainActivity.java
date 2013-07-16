package hu.edudroid.ict.ui;

import hu.edudroid.ict.ModuleSetListener;
import hu.edudroid.ict.R;
import hu.edudroid.interfaces.ModuleDescriptor;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.module.ModuleLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActivityBase implements OnClickListener, ModuleSetListener {

	private static final String TAG = "MainActivity";
	private Button showModules;
	private Button showPlugins;
	private Button manageLocalStorage;
	private Button stats;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showModules = (Button)findViewById(R.id.showModules);
		showModules.setOnClickListener(this);
		showPlugins = (Button)findViewById(R.id.showPlugins);
		showPlugins.setOnClickListener(this);
		stats = (Button)findViewById(R.id.statsButton);
		stats.setOnClickListener(this);
		manageLocalStorage = (Button)findViewById(R.id.manageLocalStorageButton);
		manageLocalStorage.setOnClickListener(this);
         
       
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (service != null) {
			service.registerModuleSetListener(this);
		}
		refreshUI();
	}
	
	@Override
	protected void onPause() {
		if (service != null) {
			service.unregisterModuleSetListenerListener(this);
		}
		super.onPause();
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
		service.registerModuleSetListener(this);
		refreshUI();
	}

	private void refreshUI() {
		if (service != null) {
			List<Plugin> plugins = service.getPlugins();
			if (plugins!=null && plugins.size() > 0) {
				showPlugins.setText(getString(R.string.showPlugins, plugins.size()));
			} else {
				showPlugins.setText(R.string.noPlugins);
			}
			List<ModuleDescriptor> modules = new ArrayList<ModuleDescriptor>();
			try {
				modules.addAll(ModuleUtils.processModules(service.getLoadedModules(), ModuleLoader.readModulesFromAssets(this, getAssets())));
				showModules.setText(getString(R.string.showModules, modules.size()));
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Error loading modules " + e);
				showModules.setText("Error loading modules");
			}
		} else {
			showPlugins.setText(R.string.noPlugins);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.statsButton:
				Toast.makeText(this, "Under development...", Toast.LENGTH_LONG).show();
				break;
			case R.id.manageLocalStorageButton:
				Toast.makeText(this, "Under development...", Toast.LENGTH_LONG).show();
				break;
			case R.id.showPlugins:
				startActivity(new Intent(this, PluginListActivity.class));
				break;
			case R.id.showModules:
				startActivity(new Intent(this, ModuleOverviewActivity.class));
				break;
		}
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		refreshUI();
		return false;
	}

	@Override
	public void moduleAdded(
			hu.edudroid.interfaces.ModuleDescriptor moduleDescriptor) {
		refreshUI();
	}

	@Override
	public void moduleRemoved(
			hu.edudroid.interfaces.ModuleDescriptor moduleDescriptor) {
		refreshUI();
	}

}
