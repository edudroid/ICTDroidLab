package hu.edudroid.ict.plugins;

import hu.edudroid.ict.R;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class PluginAdapter implements ListAdapter {

	private ArrayList<PluginBase>	mPlugins;
	
	private Context				mContext	= null;
	private LayoutInflater		mInflater	= null;
	private DataSetObserver		mObserver	= null;

	public PluginAdapter(Activity activity) {
		mPlugins = AndroidPluginCollection.getInstance().mPlugins;
		mContext = activity.getApplicationContext();
		mInflater = activity.getLayoutInflater();
	}

	public void clearPlugins(){
		mPlugins.clear();
	}

	public void addPlugin(final PluginBase plugin){
		addPlugin(plugin, false);
	}
	public void addPlugin(final PluginBase plugin, final boolean notifyChange){
		mPlugins.add(plugin);
		if (notifyChange)
			onChanged();
	}

	public void onChanged(){
		if (mObserver != null)
			mObserver.onChanged();
	}

	@Override
	public int getCount(){
		if (mPlugins == null)
			return 0;
		return (mPlugins.size());
	}

	@Override
	public PluginBase getItem(int position){
		if (mPlugins == null)
			return null;
		return (mPlugins.get(position));
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
			convertView = mInflater.inflate(R.layout.view_listitem_plugins,
											parent,
											false);

		getItem(position).generateView(convertView, mContext);

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

}
