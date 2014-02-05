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
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends ActivityBase implements OnClickListener {

	private static final String TAG = LoginActivity.class.getName();
	private LoginManager loginManager;
	private Button loginButton;
	private Button registerButton;
	private EditText userEdit;
	private EditText passwordEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "Created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginManager = new LoginManager(this);
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);
		registerButton = (Button) findViewById(R.id.registerButton);
		registerButton.setOnClickListener(this);
		userEdit = (EditText) findViewById(R.id.username);
		passwordEdit = (EditText) findViewById(R.id.password);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (loginManager.isUserLoggedIn()) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		} else {
			String userName = loginManager.getUserName();
			if (userName != null) {
				userEdit.setText(userName);
			} else {
				userEdit.setText("");
			}
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
			case R.id.registerButton:
				startActivity(new Intent(this, RegisterActivity.class));
				break;
			case R.id.loginButton:
				// TODO log user in: show progress dialog, start background task...
				break;
		}
	}
}