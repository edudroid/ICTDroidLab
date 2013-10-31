package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import hu.edudroid.ict.plugins.PluginDescriptor;

import java.util.ArrayList;
import java.util.List;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

public class PluginListAdapter implements ListAdapter {

	private ArrayList<PluginDescriptor> mPlugins = new ArrayList<PluginDescriptor>();

	private PluginListActivity activity = null;
	private LayoutInflater mInflater = null;
	private DataSetObserver mObserver = null;
	

	public PluginListAdapter(PluginListActivity activity) {
		this.activity = activity;
		mInflater = activity.getLayoutInflater();
	}

	public void clearPlugins() {
		mPlugins.clear();
		onChanged();
	}

	public void setPlugins(List<PluginDescriptor> plugins) {
		mPlugins.clear();
		mPlugins.addAll(plugins);
		onChanged();
	}

	public void onChanged() {
		if (mObserver != null)
			mObserver.onChanged();
	}

	@Override
	public int getCount() {
		if (mPlugins == null)
			return 0;
		return (mPlugins.size());
	}

	@Override
	public PluginDescriptor getItem(int position) {
		if (mPlugins == null)
			return null;
		return (mPlugins.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_plugins, null);
		}
		PluginDescriptor plugin = getItem(position);
		TextView authorText = (TextView)convertView.findViewById(R.id.plugin_author);
		TextView titleView = (TextView) convertView.findViewById(R.id.plugin_title);
		TextView descView = (TextView) convertView.findViewById(R.id.plugin_description);
		Button installButton = (Button) convertView.findViewById(R.id.installPluginButton);
		if (plugin.isDownloaded()) {
			titleView.setText(plugin.getPlugin().getName() + " (version " + plugin.getPlugin().getVersionCode() + ")");
			authorText.setText(activity.getString(R.string.created_by, plugin.getPlugin().getAuthor()));
			authorText.setVisibility(View.VISIBLE);
			descView.setText(plugin.getPlugin().getDescription());
			installButton.setVisibility(View.GONE);
		} else {
			titleView.setText(plugin.getName());
			authorText.setVisibility(View.GONE);
			descView.setText(plugin.getDescription());
			installButton.setTag(plugin);
			installButton.setOnClickListener(activity);
			installButton.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return (getCount() == 0);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mObserver = observer;
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mObserver = null;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}
