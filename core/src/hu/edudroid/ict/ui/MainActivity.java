package hu.edudroid.ict.ui;

import java.util.List;

import hu.edudroid.ict.R;
import hu.edudroid.interfaces.ModuleDescriptor;
import hu.edudroid.interfaces.Plugin;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActivityBase implements OnClickListener{

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
		refreshUI();
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
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
			List<ModuleDescriptor> modules = service.getLoadedModules();
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

}
