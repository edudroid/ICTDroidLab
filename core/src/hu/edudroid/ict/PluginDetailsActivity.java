package hu.edudroid.ict;

import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginListener;
import hu.edudroid.ict.plugins.PluginMethod;
import hu.edudroid.ict.plugins.PluginPollingBroadcast;
import hu.edudroid.interfaces.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PluginDetailsActivity extends Activity implements ListAdapter,
		PluginListener, OnItemClickListener {

	private Plugin					mPlugin;
	private ArrayList<PluginMethod>	mMethods;
	private LayoutInflater			mInflater;
	private DataSetObserver			mObserver;
	private PluginPollingBroadcast	mBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_details);

		mInflater = getLayoutInflater();
		mMethods = new ArrayList<PluginMethod>();
		ListView listview=((ListView) findViewById(R.id.details_list));
		listview.setAdapter(this);
		TextView textview=((TextView)findViewById(R.id.method_title));
		
		mPlugin = AndroidPluginCollection.getInstance()
									.getPluginByName((getIntent().getExtras()
																	.getString("pluginName")));
		textview.setText("Methods defined for "+mPlugin.getName());
		mBroadcast=PluginPollingBroadcast.getInstance();
		mBroadcast.registerPluginDetailsListener(this);
		refreshMethodList();
	}

	private void refreshMethodList(){
		Toast.makeText(this, "NOT IMPLEMENTED YET!", Toast.LENGTH_SHORT).show();
	}

	private void addMethod(PluginMethod method){
		boolean foundMethodInList=false;
		for(int i=0;i<mMethods.size();i++){
			if(mMethods.get(i).mName.equals(method.mName)){
				Log.e("CORE:PluginDetailsActivity:addMethod","Method already exists in list.");
				foundMethodInList=true;
			}
		}
		for(int i=0;i<mPlugin.getMethodNames().size();i++){
			if(mPlugin.getMethodNames().get(i).equals(method.mName)){
				if(!foundMethodInList){
					mMethods.add(method);
				}
			}
		}
		
		Collections.sort(mMethods);
		mObserver.onChanged();
	}

	@Override
	public int getCount(){
		return mMethods.size();
	}

	@Override
	public PluginMethod getItem(int position){
		return mMethods.get(position);
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

		final PluginMethod method = getItem(position);
		((TextView) convertView.findViewById(R.id.method_name)).setText(method.mName);
		((TextView) convertView.findViewById(R.id.method_details)).setText(method.mDescription);

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
		mObserver = observer;
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer){
		mObserver = null;
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

	@Override
	public boolean newPluginMethod(PluginMethod method){
		addMethod(method);
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
		Toast.makeText(this, mMethods.get(arg2).mName, Toast.LENGTH_SHORT).show();
	}

}
