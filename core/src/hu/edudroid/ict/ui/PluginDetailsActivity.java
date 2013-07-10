package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginListener;
import hu.edudroid.ict.plugins.PluginPollingBroadcast;
import hu.edudroid.interfaces.Plugin;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PluginDetailsActivity extends Activity implements ListAdapter,
		PluginListener, OnItemClickListener {

	private Plugin					mPlugin;
	private LayoutInflater			mInflater;
	private Set<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private PluginPollingBroadcast	mBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_details);

		mInflater = getLayoutInflater();
		ListView listview=((ListView) findViewById(R.id.details_list));
		listview.setAdapter(this);
		TextView textview=((TextView)findViewById(R.id.method_title));
		
		mPlugin = AndroidPluginCollection.getInstance()
									.getPluginByName((getIntent().getExtras()
																	.getString("pluginName")));
		textview.setText("Methods defined for "+mPlugin.getName());
		mBroadcast=PluginPollingBroadcast.getInstance();
		mBroadcast.registerPluginDetailsListener(this);
	}

	@Override
	public int getCount(){
		return 0;
	}

	@Override
	public String getItem(int position){
		return null;
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public int getItemViewType(int position){
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.view_listitem_methods,
											parent,
											false);

		final String method = getItem(position);
		((TextView) convertView.findViewById(R.id.method_name)).setText(method);

		return convertView;
	}

	@Override
	public int getViewTypeCount(){
		return 1;
	}

	@Override
	public boolean hasStableIds(){
		return true;
	}

	@Override
	public boolean isEmpty(){
		return (getCount() == 0);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer){
		observers.add(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer){
		observers.remove(observer);
	}

	@Override
	public boolean areAllItemsEnabled(){
		return true;
	}

	@Override
	public boolean isEnabled(int position){
		return true;
	}

	@Override
	public boolean newPlugin(Plugin plugin){
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
		Toast.makeText(this, getItem(arg2), Toast.LENGTH_SHORT).show();
	}

}
