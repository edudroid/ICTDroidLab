package hu.edudroid.ict.ui;

import hu.edudroid.ict.ModuleStatsListener;
import hu.edudroid.ict.R;
import hu.edudroid.ict.logs.LogDatabaseManager;
import hu.edudroid.ict.logs.UploadService;
import hu.edudroid.ict.utils.CoreConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class LogDetailsActivity extends ActivityBase implements OnClickListener, ModuleStatsListener {
	
	private static final String TAG = LogDetailsActivity.class.getName();
	private TextView logDatabaseSizeText;
	private Button wipeDatabaseButton;
	private Button refreshButton;
	private TextView lastUpdateText;
	private ListView logListView;
	
	private ArrayAdapter<String> logFileAdapter;
	private ArrayList<String> logFileList;
	
	//private TextView updateModeText;
	private String[] updateModes; 
	private Spinner updateModeSpinner;
	private ArrayAdapter<String> spinnerAdapter;
	private boolean spinnerRepairFlag;
	private Button uploadNowButton;
	
	private LogDatabaseManager databaseManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_details_overview);
		
		databaseManager = new LogDatabaseManager(this.getApplicationContext());

		logDatabaseSizeText = (TextView) findViewById(R.id.logDetialsOverviewLogDatabaseSizeTextView);
		wipeDatabaseButton = (Button) findViewById(R.id.logDetialsOverviewWipeDatabaseButton);
		wipeDatabaseButton.setOnClickListener(this);
		refreshButton = (Button) findViewById(R.id.logDetialsOverviewRefreshButton);
		refreshButton.setOnClickListener(this);
		lastUpdateText = (TextView) findViewById(R.id.logDetialsOverviewLastUpdateDateTextView);
		
		logListView = (ListView) findViewById(R.id.logDetialsOverviewFileListView);		
		logFileList = new ArrayList<String>();		
		logFileAdapter = new MyArrayAdapter(this, logFileList);			
		logListView.setAdapter(logFileAdapter);	
		
		spinnerRepairFlag = false;
		updateModes = getResources().getStringArray(R.array.logDetialsOverviewUpdateModes);
		updateModeSpinner = (Spinner) findViewById(R.id.logDetialsOverviewUpdateModeSpinner);
		
		spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_logfiles, updateModes);
		updateModeSpinner.setAdapter(spinnerAdapter);
		
		uploadNowButton = (Button) findViewById(R.id.logDetialsOverviewUploadNowButton);
		uploadNowButton.setOnClickListener(this);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		updateModeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	if (spinnerRepairFlag) {
		    		CoreConstants.saveInt(CoreConstants.UPLOAD_MODE, position, getApplicationContext());
		    		spinnerRepairFlag=true;
		    		return;		    		
		    	}
		    	spinnerRepairFlag = !spinnerRepairFlag;
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        return;
		    }

		});
		
		//set saved user selected value of upload mode, DEFAULT IS 0
		int uploadModeChoice = CoreConstants.getInt(CoreConstants.UPLOAD_MODE, 0, getApplicationContext());
		updateModeSpinner.setSelection(uploadModeChoice);	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
	}

	private void refreshUI() {		
		runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	//database row count
		 		int databaseRowCount = -1;
		 		try {
		 			databaseRowCount = databaseManager.getDatabaseSize();
		 		} catch (Exception e) {
		 			Log.e(TAG, "Error when getting database row count!", e);
		 		}
		 		logDatabaseSizeText.setText(getString(R.string.logDetialsOverviewLogDatabaseSizeText,databaseRowCount));
		 		
		 		//last log update time
		 		String date = CoreConstants.getString(CoreConstants.LAST_LOG_UPDATE_DATE, "", getApplicationContext());		
		 		lastUpdateText.setText(getString(R.string.logDetialsOverviewLastUpdateDateText, date));			
		 		
		 		//refresh listview		
		 		refreshFileList();
		 		logFileAdapter.notifyDataSetChanged();		

		    }
		});		
	}

	@Override
	protected void onPause() {
		if (service != null) {
			service.unregisterModuleStatsListener(this);
		}
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		databaseManager.destroy();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.logDetialsOverviewWipeDatabaseButton:
			try {
				databaseManager.wipeDatabase();
				refreshUI();
			} catch (Exception e) {
				Log.e(TAG, "Error when wiping database!", e);
			}
			break;
		case R.id.logDetialsOverviewRefreshButton:
			refreshUI();			
			break;
		case R.id.logDetialsOverviewUploadNowButton:
			Intent intent = new Intent(getApplicationContext(), UploadService.class);
			getApplicationContext().startService(intent);
			refreshUI();			
			break;
		}
		
	}
	
	public ArrayList<String> refreshFileList() {
		File myDir = new File(this.getApplicationContext().getFilesDir(),"log");
		if (!myDir.exists()) {			
			return logFileList;
		}
		
		logFileList.clear(); 
		
		for (File f : myDir.listFiles()) {			
			logFileList.add(f.getName());
		}

		return logFileList;
	}
	
	public void deleteLogFile (String fileName) {	
		File fileToDelete = new File (getApplicationContext().getFilesDir()+"/log", fileName);		
		if(!fileToDelete.delete()) {
			Log.e(TAG, "Error when trying to remove selected log file!");
		}	
	}
	
	private class MyArrayAdapter extends ArrayAdapter<String>  {
		
		private final ActivityBase context;
		ArrayList<String> logFileAdapterList;
		private Button deleteButton= null;
		
		public MyArrayAdapter(ActivityBase context, ArrayList<String> logFileAdapterList) {	
	        super (context, R.layout.listitem_logfiles, logFileAdapterList);
	        this.context = context;
	        this.logFileAdapterList = logFileAdapterList;
	    }
		
		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	        LayoutInflater inflater = context.getLayoutInflater();	        
	        if (convertView == null) {
				convertView = inflater.inflate(R.layout.listitem_logfiles, null);
			}
	        
	        deleteButton = (Button) convertView.findViewById(R.id.listItemDelete);
	        deleteButton.setTag(position);
	        TextView textView = (TextView) convertView.findViewById(R.id.textView1);
	        textView.setText(logFileAdapterList.get(position));
	        deleteButton.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	String line = logFileAdapterList.get(position);
	            	deleteLogFile(line);
	            	refreshUI();
	            }
	        });
	        return convertView;
	    }
	}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
		service.registerModuleStatsListener(this);
		refreshUI();
	}
	
	@Override
	public void moduleStatsChanged(String moduleId, Map<String, String> stats) {
		refreshUI();		
	}
}
