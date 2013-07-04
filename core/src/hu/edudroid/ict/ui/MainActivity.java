package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{

	private Button showModules;
	private Button showPlugins;
	private Button toQuotas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showModules = (Button)findViewById(R.id.showModules);
		showPlugins = (Button)findViewById(R.id.showPlugins);
		toQuotas = (Button)findViewById(R.id.toQuotas);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.toQuotas: break;
			case R.id.showPlugins: break;
			case R.id.showModules: break;
		}
	}

}
