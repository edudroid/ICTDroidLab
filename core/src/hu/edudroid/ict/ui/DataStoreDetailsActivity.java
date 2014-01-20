package hu.edudroid.ict.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.module.ModuleLoader;

public class DataStoreDetailsActivity extends ActivityBase implements OnClickListener {
	
	private TextView moduleDetailsText;
	private Button deleteModulesButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_store_details);

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
