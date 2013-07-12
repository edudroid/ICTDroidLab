package hu.edudroid.ict.ui;

import hu.edudroid.ict.ModuleLoader;
import hu.edudroid.ict.R;
import hu.edudroid.interfaces.ModuleDescriptor;
import hu.edudroid.interfaces.Plugin;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ModuleOverviewActivity extends ActivityBase implements OnItemClickListener {
	private static final String TAG = "ModuleOverviewActivity";
	private ListView moduleList;
	private ModuleListAdapter moduleListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modules_overview);
		moduleList = (ListView)findViewById(R.id.moduleList);
		moduleList.setOnItemClickListener(this);
		moduleListAdapter = new ModuleListAdapter(new ArrayList<ModuleDescriptor>(), new ArrayList<Boolean>(), this, getLayoutInflater());
		moduleList.setAdapter(moduleListAdapter);
	}
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
		refreshModuleList();
	}
	
	private void refreshModuleList() {
		List<ModuleDescriptor> loadedModules = service.getLoadedModules();
		Log.e(TAG, "Loaded modules " + loadedModules.size());
		for (ModuleDescriptor descriptor : loadedModules) {
			Log.w(TAG, "Loaded " + descriptor);
		}
		List<ModuleDescriptor> modulesInAssets = new ArrayList<ModuleDescriptor>();
		// Check if there is a module available that has not been loaded already.
		try {
			modulesInAssets = ModuleLoader.readModulesFromAssets(this, getAssets());
		} catch (IOException e) {
			Log.e(TAG, "Unable to load assets.");
			e.printStackTrace();
		}
		for (ModuleDescriptor descriptor : loadedModules) {
			Log.e(TAG, "Descriptor " + descriptor);
		}
		TreeSet<ModuleDescriptor> orderer = new TreeSet<ModuleDescriptor>(loadedModules);
		orderer.addAll(modulesInAssets);
		final List<ModuleDescriptor> orderedModules = new ArrayList<ModuleDescriptor>(orderer);
		final List<Boolean> loadedStates = new ArrayList<Boolean>();
		for (ModuleDescriptor descriptor : orderedModules) {
			loadedStates.add(loadedModules.contains(descriptor));
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				moduleListAdapter.setModules(orderedModules, loadedStates);
			}
		});
	}

	public void loadModule(ModuleDescriptor moduleDescriptor) {
		if (service != null) {
			boolean success = service.addModule(moduleDescriptor);
			if (success) {
				refreshModuleList();
				Toast.makeText(this, "Module " + moduleDescriptor.getModuleName() + " loaded successfully!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Module " + moduleDescriptor.getModuleName() + " couldn't be loaded.", Toast.LENGTH_LONG).show();
			}
		}
		
	}


	@Override
	public boolean newPlugin(Plugin plugin) {
		// TODO Auto-generated method stub
		return false;
	}
}