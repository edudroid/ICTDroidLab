package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import hu.edudroid.ict.utils.ServerUtilities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActivityBase implements OnClickListener {

	private static final String TAG = LoginActivity.class.getName();
	public static final String PREFS_NAME = "preferences";
	public static final String USER_NAME = "user_name";
	private Button loginButton;
	private Button registerButton;
	private EditText userEdit;
	private EditText passwordEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
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
		Log.d(TAG, "Resuming login activity.");
		if (ServerUtilities.hasUserLoginCookie(this)) {
			Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		} else {
			String userName = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(USER_NAME, null);
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
				if (service != null) {
					final ProgressDialog progressDialog = new ProgressDialog(this);
					progressDialog.setTitle(R.string.loggingInTitle);
					progressDialog.show();
					getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(USER_NAME, userEdit.getText().toString());
					new Thread(new Runnable() {
						@Override
						public void run() {
							final boolean loginResult = ServerUtilities.login(userEdit.getText().toString(), passwordEdit.getText().toString(), getApplicationContext());
							if (loginResult) {
								service.registerWithBackend();
							}
							runOnUiThread(new Runnable() {								
								@Override
								public void run() {
									progressDialog.cancel();
									if (loginResult) {
										Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_LONG).show();
										startActivity(new Intent(LoginActivity.this, MainActivity.class));
									} else {
										Toast.makeText(LoginActivity.this, R.string.loginFailed, Toast.LENGTH_LONG).show();
									}
								}
							});
						}
					}).start();
				} else {
					Toast.makeText(this, R.string.serviceUnavailable, Toast.LENGTH_LONG).show();
				}
				break;
		}
	}
}