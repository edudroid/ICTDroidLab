package hu.edudroid.ict.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.module.ModuleDescriptor;

public class ModuleDetailsActivity extends ActivityBase implements OnClickListener {
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module_details);
		moduleNameText = (TextView) findViewById(R.id.moduleName);
		moduleIdText = (TextView) findViewById(R.id.moduleId);
		moduleAuthorText = (TextView) findViewById(R.id.moduleAuthor);
		moduleDescriptionText = (TextView) findViewById(R.id.moduleDescription);
		moduleWebText = (TextView) findViewById(R.id.moduleWebsite);
		modulePluginsText = (TextView) findViewById(R.id.modulePluginList);
		modulePermissionsText = (TextView) findViewById(R.id.modulePermissions);
		moduleQuotasText = (TextView) findViewById(R.id.moduleQuotas);
		deleteButton = (Button) findViewById(R.id.moduleDelete);
		deleteButton.setOnClickListener(this);
		deleteButton.setEnabled(false);
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
		deleteButton.setEnabled(true);
		moduleNameText.setText(moduleDescriptor.moduleName);
		moduleAuthorText.setText(moduleDescriptor.author);
		moduleDescriptionText.setText(moduleDescriptor.description);
		moduleWebText.setText(moduleDescriptor.website);
		modulePluginsText.setText(moduleDescriptor.getPluginsText(","));
		modulePermissionsText.setText(moduleDescriptor.getPermissionsText(","));
		moduleQuotasText.setText(moduleDescriptor.getQuotasText(","));
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
		refreshUI();
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		return false;
	}

	@Override
	public void onClick(View arg0) {
		if (service != null) {
			// TODO pop up dialog to confirm deletion
			service.removeModule(moduleDescriptor.moduleId);
		}
	}

}
