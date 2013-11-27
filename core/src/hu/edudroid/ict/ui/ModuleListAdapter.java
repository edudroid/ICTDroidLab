package hu.edudroid.ict.ui;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.ModuleStatsListener;
import hu.edudroid.ict.R;
import hu.edudroid.module.ModuleDescriptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ModuleListAdapter implements ListAdapter {
	
	private static final String TAG = ModuleListAdapter.class.getName();
	private List<ModuleDescriptor> moduleDescriptors;
	private List<DataSetObserver> observers = new ArrayList<DataSetObserver>();
	private LayoutInflater inflater;
	private CoreService coreService;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM'-'dd HH':'mm':'ss", Locale.getDefault());

	public ModuleListAdapter(List<ModuleDescriptor> modules, LayoutInflater inflater, CoreService coreService) {
		this.moduleDescriptors = modules;
		this.inflater = inflater;
		this.coreService = coreService;
	}
	
	public void setModules(List<ModuleDescriptor> modules){
		this.moduleDescriptors = modules;
		notifyObservers();
	}
	
	private void notifyObservers() {
		for (DataSetObserver observer : observers) {
			observer.onChanged();
		}
	}

	@Override
	public int getCount() {
		return moduleDescriptors.size();
	}

	@Override
	public Object getItem(int position) {
		return moduleDescriptors.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public int getItemViewType(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ModuleDescriptor moduleDescriptor = moduleDescriptors.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_module, null);
		}
		TextView nameLabel = (TextView)(convertView.findViewById(R.id.listItemModuleNameLable));
		TextView stateLabel = (TextView)(convertView.findViewById(R.id.listItemModuleStateLabel));
		TextView lastRunLabel = (TextView)(convertView.findViewById(R.id.listItemModuleLastRunLabel));
		TextView totalRunsLabel = (TextView)(convertView.findViewById(R.id.listItemModuleNumberOfRunsLabel));
		
		nameLabel.setText(moduleDescriptor.moduleName);
		stateLabel.setVisibility(View.VISIBLE);
		if (coreService != null) {
			lastRunLabel.setVisibility(View.VISIBLE);
			totalRunsLabel.setVisibility(View.VISIBLE);
			Map<String, String> values = coreService.getModuleStats(moduleDescriptor.className);
			String numberString = "N/A";
			if (values != null) {
				try {
					numberString = "" + Integer.parseInt(values.get(ModuleStatsListener.STAT_KEY_TIMERS_FIRED));
				} catch (Exception e) {
					Log.e(TAG, "Error rendering module list item " + e, e);
					e.printStackTrace();
				}
			}
			totalRunsLabel.setText(numberString);
			String dateString = "N/A";
			if (values != null) {
				try {
					dateString = dateFormatter.format(new Date(Long.parseLong(values.get(ModuleStatsListener.STAT_KEY_LAST_TIMER_EVENT))));
				} catch (Exception e) {
					Log.e(TAG, "Error rendering module list item " + e, e);
					e.printStackTrace();
				}
			}
			lastRunLabel.setText(dateString);
			stateLabel.setText(moduleDescriptor.getState(coreService).toString(coreService));
		} else {
			stateLabel.setVisibility(View.GONE);
			lastRunLabel.setVisibility(View.GONE);
			totalRunsLabel.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return moduleDescriptors.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
		observers.add(arg0);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		observers.remove(arg0);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int arg0) {
		return true;
	}

	public void setService(CoreService service) {
		this.coreService = service;
	}
}
