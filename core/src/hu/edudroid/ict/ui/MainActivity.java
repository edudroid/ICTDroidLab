package hu.edudroid.ict.ui;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private Button showModules;
	private Button showPlugins;
	private Button toQuotas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showModules = (Button)findViewById(R.id.showModules);
		showModules.setOnClickListener(this);
		showPlugins = (Button)findViewById(R.id.showPlugins);
		showPlugins.setOnClickListener(this);
		toQuotas = (Button)findViewById(R.id.toQuotas);
		toQuotas.setOnClickListener(this);
		startService(new Intent(this, CoreService.class));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.toQuotas:
				Toast.makeText(this, "Under development...", Toast.LENGTH_LONG).show();
				break;
			case R.id.showPlugins:
				startActivity(new Intent(this, PluginListActivity.class));
				break;
			case R.id.showModules:
				startActivity(new Intent(this, ModuleOverviewActivity.class));
				break;
		}
	}

}
