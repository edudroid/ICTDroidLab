package hu.edudroid.ict.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.module.FileManager;
import hu.edudroid.module.ModuleLoader;

public class DataStoreDetailsActivity extends ActivityBase implements OnClickListener {
	
	private TextView logDetailsText;
	private Button deleteLogsButton;
	private TextView zipsDetailsText;
	private Button deleteZipsButton;
	private TextView moduleDetailsText;
	private Button deleteModulesButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_store_details);
		logDetailsText = (TextView) findViewById(R.id.dataStoreLogDetailsText);
		deleteLogsButton = (Button) findViewById(R.id.dataStoreDeleteLogs);
		deleteLogsButton.setOnClickListener(this);

		zipsDetailsText = (TextView) findViewById(R.id.dataStoreZipDetailsText);
		deleteZipsButton = (Button) findViewById(R.id.dataStoreDeleteZips);
		deleteZipsButton.setOnClickListener(this);

		moduleDetailsText = (TextView) findViewById(R.id.dataStoreModuleDetailsText);
		deleteModulesButton = (Button) findViewById(R.id.dataStoreDeleteModules);
		deleteModulesButton.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
	}
	
	private void refreshUI() {
		moduleDetailsText.setText(getString(R.string.dataStoreModuleCount, ModuleLoader.getAllModules(this).size()));		
		new Thread(new Runnable() {
			@Override
			public void run() {
				final float fileMBs = ((float)FileManager.getTotalLogFileSize()) / (1024f * 1024f);
				final float zipMBs = ((float)FileManager.getTotalZipFileSize()) / (1024f * 1024f);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						logDetailsText.setText(getString(R.string.dataStoreTotalLogSize, fileMBs));
						zipsDetailsText.setText(getString(R.string.dataStoreTotalZipSize, zipMBs));
					}
				});
			}
		}).start();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public boolean newPlugin(Plugin plugin) {
		return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.dataStoreDeleteLogs: {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					FileManager.deleteAllLogs();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refreshUI();
						}
					});
				}
			}).start();
			break;
		}
		case R.id.dataStoreDeleteZips: {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					FileManager.deleteAllZip();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refreshUI();
						}
					});
				}
			}).start();
			break;
		}
		case R.id.dataStoreDeleteModules: {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					ModuleLoader.deleteAllModules(DataStoreDetailsActivity.this);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refreshUI();
						}
					});
				}
			}).start();
			break;
		}
		}
	}

}
