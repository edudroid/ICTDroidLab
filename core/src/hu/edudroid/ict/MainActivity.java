package hu.edudroid.ict;

import hu.edudroid.ict.plugins.PluginListener;
import hu.edudroid.ict.plugins.Plugin;
import hu.edudroid.ict.plugins.PluginAdapter;
import hu.edudroid.ict.plugins.PluginMethod;
import hu.edudroid.ict.plugins.PluginPollingBroadcast;
import hu.edudroid.module.ModuleLoader;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements PluginListener,
		OnClickListener {

	private final String			FILTER_PLUGIN_POLL		= "hu.edudroid.ict.plugin_polling_question";
	private final String			FILTER_PLUGIN_ANSWER	= "hu.edudroid.ict.plugin_polling_answer";

	private PluginAdapter			mAdapter				= null;
	private PluginPollingBroadcast	mBroadcast				= null;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAdapter = new PluginAdapter(this);
		((ListView) findViewById(R.id.plugin_list)).setAdapter(mAdapter);
		mBroadcast = PluginPollingBroadcast.getInstance();
		registerReceiver(mBroadcast, new IntentFilter(FILTER_PLUGIN_ANSWER));

		refreshPluginlist();
		findViewById(R.id.btn_refresh).setOnClickListener(this);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mBroadcast.registerPluginDetailsListener(this);
		
		ModuleLoader.runModule("none", "ModuleExample.jar", this);
		
	}

	private void refreshPluginlist(){
		mAdapter.clearPlugins();
		findViewById(R.id.no_plugins).setVisibility(View.VISIBLE);
		findViewById(R.id.plugin_count).setVisibility(View.GONE);

		Intent intent = new Intent(FILTER_PLUGIN_POLL);
		intent.putExtra("action", "reportSelf");
		sendBroadcast(intent);
		
		Log.d("CORE::MainActivity:refreshPluginlist","Broadcast sent...");
	}

	@Override
	public void newPlugin(Plugin plugin){
		mAdapter.addPlugin(plugin);

		findViewById(R.id.no_plugins).setVisibility(View.GONE);
		findViewById(R.id.plugin_count).setVisibility(View.VISIBLE);

		final int pCount = mAdapter.getCount();
		final String countStr = getString(	R.string.plugins_count,
											pCount,
											(pCount > 1 ? "s" : ""));
		((TextView) findViewById(R.id.plugin_count_text)).setText(countStr);
	}

	@Override
	public void newPluginMethod(PluginMethod method){}

	@Override
	public void onClick(View view){
		switch (view.getId()){
			case R.id.btn_refresh:
				refreshPluginlist();
				break;
		}
	}
}
