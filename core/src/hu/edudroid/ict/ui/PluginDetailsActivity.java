package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginListener;
import hu.edudroid.interfaces.PluginResultListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PluginDetailsActivity extends ActivityBase implements PluginListener, OnItemClickListener, PluginResultListener, PluginEventListener {

	private ListView methodList;
	private String pluginName;
	private Plugin plugin;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_details);
		methodList = ((ListView) findViewById(R.id.details_list));
		methodList.setOnItemClickListener(this);
		pluginName = getIntent().getExtras().getString(Constants.INTENT_EXTRA_KEY_PLUGIN_ID);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshPlugin();
		refreshUI();
	}
	
	@Override
	protected void onPause() {
		if (plugin != null) {
			plugin.unregisterEventListener(this);
			plugin.cancelCallsForListener(this);
		}
		super.onPause();
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
		refreshPlugin();
		refreshUI();
	}

	private void refreshPlugin() {
		if (service != null) {
			this.plugin = null;
			List<Plugin> plugins = service.getPlugins();
			for (Plugin plugin : plugins) {
				if (plugin.getName().equals(pluginName)) {
					this.plugin = plugin;
					break;
				}
			}
			// Register to events of the displayed plugin
			if (plugin != null) {
				for (String eventName : plugin.getAllEvents()) {
					plugin.registerEventListener(eventName, this);
				}
			}
		} else {
			methodList.setVisibility(View.GONE);
		}
	}

	private void refreshUI() {
		if (this.plugin != null) {
			methodList.setVisibility(View.VISIBLE);
			final ArrayAdapter<String> methodAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.plugin.getMethodNames());
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					methodList.setAdapter(methodAdapter);
				}
			});
		} else {
			methodList.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		refreshPlugin();
		refreshUI();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String methodName = (String)arg0.getItemAtPosition(arg2);
		if (this.plugin != null) {
			long callId = plugin.callMethodAsync(methodName, new HashMap<String, Object>(), this);
			Toast.makeText(this, "Called method " + methodName + ", call id: " + callId, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, Map<String, Object> result) {
		final String message = "Method call " + id + " (" + methodName + ") returned " + result.toString();
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(PluginDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onError(long id, String plugin, String pluginVersion,
			String methodName, String errorMessage) {
		final String message = "Method call " + id + " (" + methodName + ") threw error " + errorMessage;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(PluginDetailsActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onEvent(String plugin, String version, String eventName, Map<String, Object> result) {
		final String message = "Event " + eventName + " params: " + result.toString();
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(PluginDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
}