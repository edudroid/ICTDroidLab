package hu.edudroid.ict.ui;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.R;
import hu.edudroid.ict.plugins.PluginDescriptor;
import hu.edudroid.ict.plugins.PluginManager;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginListener;

import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PluginListActivity extends ActivityBase implements PluginListener,
		OnClickListener, OnItemClickListener {

	private PluginListAdapter mAdapter = null;
	private static final String TAG = PluginListActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_list);
		mAdapter = new PluginListAdapter(this);
		ListView listview = ((ListView) findViewById(R.id.plugin_list));
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(this);
		startService(new Intent(this, CoreService.class));
		findViewById(R.id.btn_refresh).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(service != null) {
			service.pollPlugins();
			refreshPluginlist();
		}
	}

	private void refreshPluginlist() {
		if (service != null) {
			List<Plugin> plugins = service.getPlugins();
			Log.d(TAG, "Loaded plugins from service " + plugins);
			List<PluginDescriptor> availablePlugins = service.getAvailablePlugins();
			Log.d(TAG, "Available plugins from service " + availablePlugins);
			List<PluginDescriptor> descriptors = PluginManager.getAvailablePlugins(availablePlugins, plugins);
			if (descriptors != null && descriptors.size() > 0) {
				mAdapter.setPlugins(descriptors);
				findViewById(R.id.no_plugins).setVisibility(View.GONE);
			} else {
				mAdapter.clearPlugins();
				findViewById(R.id.no_plugins).setVisibility(View.VISIBLE);
			}
		} else {
			mAdapter.clearPlugins();
			findViewById(R.id.no_plugins).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		refreshPluginlist();
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_refresh: {
				refreshPluginlist();
				break;
			}
			case R.id.installPluginButton: {
				PluginDescriptor descriptor = (PluginDescriptor)view.getTag();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=" + descriptor.getPackageName()));
				startActivity(intent);
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		PluginDescriptor plugin = mAdapter.getItem(arg2);
		Log.i(TAG, "Plugin " + plugin.getName() + " selected.");
		if (plugin.isDownloaded()) {
			Intent mIntent = new Intent(this, PluginDetailsActivity.class);
			mIntent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, plugin.getName());
			startActivity(mIntent);
		}
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				refreshPluginlist();
			}
		});
	}
}
