package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import hu.edudroid.ict.utils.ServerUtilities;
import hu.edudroid.interfaces.Constants;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class RegisterActivity extends ActivityBase implements OnClickListener, OnEditorActionListener {

	private static final String TAG = RegisterActivity.class.getName();
	public static final String PREFS_NAME = "preferences";
	public static final String USER_NAME = "user_name";
	private Button registerButton;
	private EditText userEdit;
	private EditText passwordEdit;
	private EditText passwordAgainEdit;
	private View passwordDontMatch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_user);
		registerButton = (Button) findViewById(R.id.registerButton);
		registerButton.setOnClickListener(this);
		userEdit = (EditText) findViewById(R.id.username);
		passwordDontMatch = findViewById(R.id.passwordDontMatch);
		passwordEdit = (EditText) findViewById(R.id.password);
		passwordAgainEdit = (EditText) findViewById(R.id.confirmPassword);
		passwordEdit.setOnEditorActionListener(this);
		passwordAgainEdit.setOnEditorActionListener(this);
		
		String userName = getIntent().getExtras().getString(LoginActivity.USER_NAME);
		String password = getIntent().getExtras().getString(LoginActivity.PASSWORD);
		if (userName != null) {
			userEdit.setText(userName);
		} else {
			userEdit.setText("");
		}
		if (password != null) {
			passwordEdit.setText(password);
		} else {
			passwordEdit.setText("");
		}
		checkFields();
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
				if (service != null) {
					final ProgressDialog progressDialog = new ProgressDialog(this);
					progressDialog.setTitle(R.string.loggingInTitle);
					progressDialog.show();
					getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(USER_NAME, userEdit.getText().toString());
					new Thread(new Runnable() {
						@Override
						public void run() {
							Log.i(TAG, "Registering user");
							final boolean registrationResult = ServerUtilities.register(userEdit.getText().toString(), passwordEdit.getText().toString(), getApplicationContext());
							if (registrationResult) {
								Log.i(TAG, "User registration succeeded, registering device.");
								service.registerWithBackend();
							} else {
								Log.w(TAG, "User registration failed.");
							}
							// Displays a toast in both cases.
							runOnUiThread(new Runnable() {								
								@Override
								public void run() {
									progressDialog.cancel();
									if (registrationResult) {
										Toast.makeText(RegisterActivity.this, R.string.registrationSuccess, Toast.LENGTH_LONG).show();
										startActivity(new Intent(RegisterActivity.this, MainActivity.class));
									} else {
										Toast.makeText(RegisterActivity.this, R.string.registrationFailed, Toast.LENGTH_LONG).show();
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

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		checkFields();
		return false;
	}

	private void checkFields() {
		if (passwordAgainEdit.getText().toString().equals(passwordEdit.getText().toString())) {
			passwordDontMatch.setVisibility(View.GONE);
			if (Constants.isValidPassword(passwordEdit.getText().toString())) {
				registerButton.setEnabled(true);
			} else {
				registerButton.setEnabled(false);
			}
		} else {
			passwordDontMatch.setVisibility(View.VISIBLE);
			registerButton.setEnabled(false);
		}
	}
}