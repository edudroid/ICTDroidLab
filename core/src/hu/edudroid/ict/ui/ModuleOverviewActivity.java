package hu.edudroid.ict.ui;

import hu.edudroid.ict.ModuleSetListener;
import hu.edudroid.ict.ModuleStatsListener;
import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.module.ModuleLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ModuleOverviewActivity extends ActivityBase implements OnItemClickListener, ModuleSetListener, ModuleStatsListener {
	private static final String TAG = "ModuleOverviewActivity";
	private ListView moduleList;
	private ModuleListAdapter moduleListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modules_overview);
		moduleList = (ListView)findViewById(R.id.moduleList);
		moduleList.setOnItemClickListener(this);
		moduleListAdapter = new ModuleListAdapter(new ArrayList<ModuleDescriptor>(), getLayoutInflater(), service);
		moduleList.setAdapter(moduleListAdapter);
	}
	
	@Override
	protected void onPause() {
		if (service != null) {
			service.unregisterModuleSetListener(this);
			service.unregisterModuleStatsListener(this);
		}
		super.onPause();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.e("ModuleOverview","OnClicked");
	}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
		service.registerModuleSetListener(this);
		service.registerModuleStatsListener(this);
		moduleListAdapter.setService(service);
		refreshModuleList();
	}
	
	private void refreshModuleList() {
		final List<ModuleDescriptor> orderedModules = new ArrayList<ModuleDescriptor>();
		orderedModules.addAll(ModuleUtils.processModules(service.getLoadedModules(), ModuleLoader.getAvailableModuls(this)));
		Log.i(TAG, "Found " + orderedModules.size() + " module(s).");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				moduleListAdapter.setModules(orderedModules);
			}
		});
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		return false;
	}

	@Override
	public void moduleAdded(hu.edudroid.interfaces.ModuleDescriptor moduleDescriptor) {
		refreshModuleList();
	}

	@Override
	public void moduleRemoved(hu.edudroid.interfaces.ModuleDescriptor moduleDescriptor) {
		refreshModuleList();
	}

	@Override
	public void moduleSTatsChanged(String moduleClassName,
			Map<String, String> stats) {
		Log.e(TAG, "Stats changed");
		refreshModuleList();
	}
}