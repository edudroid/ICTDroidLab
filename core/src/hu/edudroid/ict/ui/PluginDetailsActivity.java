package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PluginDetailsActivity extends Activity implements PluginListener, OnItemClickListener {

	private Plugin mPlugin;
	private ListView methodList;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_details);
		methodList = ((ListView) findViewById(R.id.details_list));
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		return true;
	}

	public static Intent generateIntent(final int pluginHash,
										final Context context){
		Intent intent = new Intent(context, PluginDetailsActivity.class);
		intent.putExtra("pluginHash", pluginHash);
		return intent;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}
