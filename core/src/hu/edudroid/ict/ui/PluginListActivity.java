package hu.edudroid.ict.ui;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.R;
import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginListener;
import hu.edudroid.ict.plugins.PluginMethod;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PluginListActivity extends Activity implements PluginListener,
		OnClickListener, OnItemClickListener {

	private PluginListAdapter mAdapter = null;

	private AndroidPluginCollection mPluginCollection = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAdapter = new PluginListAdapter(this);

		ListView listview = ((ListView) findViewById(R.id.plugin_list));
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(this);
		startService(new Intent(this, CoreService.class));
		startService(new Intent(this, AndroidPluginCollection.class));
		mPluginCollection = AndroidPluginCollection.getInstance();
		findViewById(R.id.btn_refresh).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		refreshPluginlist();
		super.onResume();
	}

	private void refreshPluginlist() {
		mAdapter.clearPlugins();
		findViewById(R.id.no_plugins).setVisibility(View.VISIBLE);
		findViewById(R.id.plugin_count).setVisibility(View.GONE);

		Intent mIntent = new Intent(Constants.INTENT_ACTION_PLUGIN_POLL);
		sendBroadcast(mIntent);
		ArrayList<Plugin> plugins = mPluginCollection.getAllPlugins();
		Log.e("Plugin number:", String.valueOf(plugins.size()));

		for (int i = 0; i < plugins.size(); i++) {
			this.newPlugin(plugins.get(i));
		}
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		try {
			Log.e("MainActivity", "Adding new plugin " + plugin.getName());
			mAdapter.addPlugin(plugin);

			findViewById(R.id.no_plugins).setVisibility(View.GONE);
			findViewById(R.id.plugin_count).setVisibility(View.VISIBLE);

			final int pCount = mAdapter.getCount();
			final String countStr = getString(R.string.plugins_count, pCount,
					(pCount > 1 ? "s" : ""));
			((TextView) findViewById(R.id.plugin_count_text)).setText(countStr);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean newPluginMethod(PluginMethod method) {
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_refresh:
			refreshPluginlist();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Plugin plugin = mAdapter.getItem(arg2);
		List<String> methodNames = plugin.getMethodNames();
		for (String methodName : methodNames) {
			Log.e("Methods for this plugin:", methodName);
		}

		Toast.makeText(this, "PluginName:" + mAdapter.getItem(arg2).getName(),
				Toast.LENGTH_SHORT).show();
		Intent mIntent = new Intent(this, PluginDetailsActivity.class);
		mIntent.putExtra("pluginName", mAdapter.getItem(arg2).getName());
		startActivity(mIntent);

	}
}
