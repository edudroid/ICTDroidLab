package hu.edudroid.ict.ui;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.R;
import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginListener;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PluginListActivity extends ActivityBase implements PluginListener,
		OnClickListener, OnItemClickListener {

	private PluginListAdapter mAdapter = null;
	private static final String TAG = "PluginListActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_list);
		mAdapter = new PluginListAdapter(this);
		ListView listview = ((ListView) findViewById(R.id.plugin_list));
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(this);
		startService(new Intent(this, CoreService.class));
		startService(new Intent(this, AndroidPluginCollection.class));
		findViewById(R.id.btn_refresh).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshPluginlist();
	}

	private void refreshPluginlist() {
		if (service != null) {
			List<Plugin> plugins = service.getPlugins();
			if (plugins != null && plugins.size() > 0) {
				mAdapter.setPlugins(plugins);
				findViewById(R.id.no_plugins).setVisibility(View.GONE);
				findViewById(R.id.plugin_count).setVisibility(View.VISIBLE);
			} else {
				mAdapter.clearPlugins();
				findViewById(R.id.no_plugins).setVisibility(View.VISIBLE);
				findViewById(R.id.plugin_count).setVisibility(View.GONE);
			}
		} else {
			mAdapter.clearPlugins();
			findViewById(R.id.no_plugins).setVisibility(View.VISIBLE);
			findViewById(R.id.plugin_count).setVisibility(View.GONE);			
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
		case R.id.btn_refresh:
			refreshPluginlist();
			Intent mIntent = new Intent(Constants.INTENT_ACTION_PLUGIN_POLL);
			sendBroadcast(mIntent);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Plugin plugin = mAdapter.getItem(arg2);
		Log.i(TAG, "Plugin " + plugin.getName() + " selected.");
		Intent mIntent = new Intent(this, PluginDetailsActivity.class);
		mIntent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, plugin.getName());
		startActivity(mIntent);

	}
}
