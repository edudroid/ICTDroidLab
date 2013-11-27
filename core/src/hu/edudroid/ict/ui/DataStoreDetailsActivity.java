package hu.edudroid.ict.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.module.FileManager;

public class DataStoreDetailsActivity extends ActivityBase implements OnClickListener {
	
	private TextView dataStoreDetailsText;
	private Button deleteButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_store_details);
		dataStoreDetailsText = (TextView) findViewById(R.id.dataStoreDetailsText);
		deleteButton = (Button) findViewById(R.id.dataStoreDeleteLogs);
		deleteButton.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
	}
	
	private void refreshUI() {
		deleteButton.setEnabled(true);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final float fileMBs = ((float)FileManager.getTotalLogFileSize()) / (1024f * 1024f);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dataStoreDetailsText.setText(getString(R.string.dataStoreTotalLogSize, fileMBs));
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
			deleteButton.setEnabled(false);
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
		}
	}

}
