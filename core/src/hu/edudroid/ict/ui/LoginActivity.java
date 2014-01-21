package hu.edudroid.ict.ui;

import hu.edudroid.ict.LoginManager;
import hu.edudroid.ict.R;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class LoginActivity extends ActivityBase implements OnClickListener {

	private static final String TAG = LoginActivity.class.getName();
	private LoginManager loginManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginManager = new LoginManager(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (loginManager.isUserLoggedIn()) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.showModules:
				startActivity(new Intent(this, ModuleOverviewActivity.class));
				break;
		}
	}
}
