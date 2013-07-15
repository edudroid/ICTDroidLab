package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class PluginListAdapter implements ListAdapter {

	private ArrayList<Plugin> mPlugins;

	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private DataSetObserver mObserver = null;

	public PluginListAdapter(Activity activity) {
		mPlugins = new ArrayList<Plugin>();
		mContext = activity.getApplicationContext();
		mInflater = activity.getLayoutInflater();
	}

	public void clearPlugins() {
		mPlugins.clear();
		onChanged();
	}

	public void setPlugins(List<Plugin> plugins) {
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
	public Plugin getItem(int position) {
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
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.view_listitem_plugins,
					parent, false);

		generateView(convertView, mContext, getItem(position));

		return convertView;
	}

	public View generateView(final View root, final Context context,
			Plugin plugin) {
		try {
			final String author = context.getString(R.string.created_by,
					plugin.getAuthor());
			((TextView) root.findViewById(R.id.plugin_title)).setText(plugin
					.getName() + " (version " + plugin.getVersionCode() + ")");
			((TextView) root.findViewById(R.id.plugin_author)).setText(author);
			((TextView) root.findViewById(R.id.plugin_description))
					.setText(plugin.getDescription());

			return root;
		} catch (Exception ex) {
			throw new IllegalArgumentException();
		}
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
