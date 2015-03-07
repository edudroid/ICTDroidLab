package hu.edudroid.ict.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import hu.edudroid.ict.ModuleSetListener;
import hu.edudroid.ict.ModuleStatsListener;
import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.module.ModuleDescriptor;
import hu.edudroid.module.ModuleManager;
import hu.edudroid.module.ModuleState;

public class ModuleDetailsActivity extends ActivityBase implements OnClickListener, ModuleSetListener, ModuleStatsListener {
	
	private static final String TAG = ModuleManager.class.getName();
	public static final String INTENT_EXTRA_MODULE_ID = "Module id";
	private TextView moduleNameText;
	private TextView moduleAuthorText;
	private TextView moduleDescriptionText;
	private TextView moduleWebText;
	private TextView modulePluginsText;
	private TextView modulePermissionsText;
	private TextView moduleQuotasText;
	private Button deleteButton;
	private ModuleDescriptor moduleDescriptor;
	private TextView moduleIdText;
	private TextView moduleStartText;
	private TextView moduleEndText;
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM'-'dd HH':'mm':'ss", Locale.getDefault());
	private Button startButton;
	private TextView moduleStateText;
	
	private Button restartButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module_details);
		moduleNameText = (TextView) findViewById(R.id.moduleName);
		moduleIdText = (TextView) findViewById(R.id.moduleId);
		moduleStateText = (TextView) findViewById(R.id.moduleState);
		moduleAuthorText = (TextView) findViewById(R.id.moduleAuthor);
		moduleStartText = (TextView) findViewById(R.id.moduleStartTime);
		moduleEndText = (TextView) findViewById(R.id.moduleEndTime);
		moduleDescriptionText = (TextView) findViewById(R.id.moduleDescription);
		moduleWebText = (TextView) findViewById(R.id.moduleWebsite);
		modulePluginsText = (TextView) findViewById(R.id.modulePluginList);
		modulePermissionsText = (TextView) findViewById(R.id.modulePermissions);
		moduleQuotasText = (TextView) findViewById(R.id.moduleQuotas);
		
		
		deleteButton = (Button) findViewById(R.id.moduleDeleteButton);
		deleteButton.setOnClickListener(this);
		deleteButton.setEnabled(false);
		
		startButton = (Button) findViewById(R.id.moduleStartButton);
		startButton.setOnClickListener(this);
		startButton.setEnabled(false);
		
		restartButton = (Button) findViewById(R.id.moduleRestartButton);
		restartButton.setOnClickListener(this);
		restartButton.setEnabled(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (super.service != null) {
			refreshUI();
		}
	}
	
	private void refreshUI() {
		Intent intent = getIntent();
		String moduleId = intent.getStringExtra(INTENT_EXTRA_MODULE_ID);
		moduleDescriptor = service.getModule(moduleId);
		moduleIdText.setText(getString(R.string.moduleIdString, moduleId));
		moduleStateText.setText(moduleDescriptor.getState(this).toString(this));
		moduleNameText.setText(moduleDescriptor.moduleName);
		moduleAuthorText.setText(moduleDescriptor.author);
		moduleDescriptionText.setText(moduleDescriptor.description);
		if (moduleDescriptor.getState(this) == ModuleState.INSTALLED) {
			// Start time
			moduleStartText.setVisibility(View.VISIBLE);
			moduleEndText.setVisibility(View.GONE);
			moduleStartText.setText(getString(R.string.moduleStartedString, dateFormatter.format(new Date(moduleDescriptor.getInstallDate()))));
		} else if (moduleDescriptor.getState(this) == ModuleState.TERMINATED) {
			moduleStartText.setVisibility(View.VISIBLE);
			moduleEndText.setVisibility(View.VISIBLE);
			moduleStartText.setText(getString(R.string.moduleStartedString, dateFormatter.format(new Date(moduleDescriptor.getInstallDate()))));
			moduleEndText.setText(getString(R.string.moduleStartedString, dateFormatter.format(new Date(moduleDescriptor.getEndDate()))));
		} else {
			moduleStartText.setVisibility(View.GONE);
			moduleEndText.setVisibility(View.GONE);
		}
		moduleWebText.setText(moduleDescriptor.website);
		modulePluginsText.setText(moduleDescriptor.getPluginsText(", "));
		modulePermissionsText.setText(moduleDescriptor.getPermissionsText(", "));
		moduleQuotasText.setText(moduleDescriptor.getQuotasText(", "));
		
		if (moduleDescriptor.getState(this) == ModuleState.INSTALLED) {		
			deleteButton.setEnabled(true);
			deleteButton.setVisibility(View.VISIBLE);
			startButton.setVisibility(View.GONE);
			restartButton.setVisibility(View.GONE);
		} else if (moduleDescriptor.getState(this) == ModuleState.AVAILABLE) {		
			startButton.setEnabled(true);
			deleteButton.setVisibility(View.GONE);
			startButton.setVisibility(View.VISIBLE);
			restartButton.setVisibility(View.GONE);
		} else if (moduleDescriptor.getState(this) == ModuleState.TERMINATED) {		
			restartButton.setEnabled(true);
			restartButton.setVisibility(View.VISIBLE);
			deleteButton.setVisibility(View.GONE);
			startButton.setVisibility(View.GONE);
		} else if (moduleDescriptor.getState(this) == ModuleState.BANNED) {
			restartButton.setEnabled(true);
			restartButton.setVisibility(View.VISIBLE);
			deleteButton.setVisibility(View.GONE);
			startButton.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onPause() {
		if (service != null) {
			service.unregisterModuleSetListener(this);
			service.unregisterModuleStatsListener(this);
		}
		super.onPause();
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
		service.registerModuleSetListener(this);
		service.registerModuleStatsListener(this);
		refreshUI();
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		return false;
	}

	@Override
	public void onClick(View view) {
		if (service != null) {
			switch(view.getId()) {
			case R.id.moduleStartButton : {
				service.installModule(moduleDescriptor);
				break;
			}
			case R.id.moduleDeleteButton : {
				// TODO pop up dialog to confirm deletion
				service.removeModule(moduleDescriptor.moduleId);
				break;
			}
			//restart terminated or banned module
			case R.id.moduleRestartButton : {
				Log.e(TAG, "Restart pressed ");
				
				//remove if there is a module by that ID
				service.removeModule(moduleDescriptor.moduleId);
				
				//to enable restarting module
				moduleDescriptor.setSate(ModuleState.AVAILABLE, this);
				service.installModule(moduleDescriptor);
				Log.e(TAG, "After restart pressed ");
				break;
			}
			}
		}
	}

	@Override
	public void moduleStatsChanged(String moduleId, Map<String, String> stats) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				refreshUI();
			}
		});
	}

	@Override
	public void moduleAdded(ModuleDescriptor moduleDescriptor) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				refreshUI();
			}
		});
	}

	@Override
	public void moduleRemoved(ModuleDescriptor moduleDescriptor) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				refreshUI();
			}
		});
	}

}
