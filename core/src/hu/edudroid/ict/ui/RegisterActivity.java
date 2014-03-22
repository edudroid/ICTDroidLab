package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import hu.edudroid.ict.utils.CoreConstants;
import hu.edudroid.ict.utils.ServerUtilities;
import hu.edudroid.interfaces.Constants;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends ActivityBase implements OnClickListener, TextWatcher {

	private static final String TAG = RegisterActivity.class.getName();
	private Button registerButton;
	private EditText userEdit;
	private EditText passwordEdit;
	private EditText passwordAgainEdit;
	private View passwordDontMatch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_user);
		registerButton = (Button) findViewById(R.id.confirmRegistrationButton);
		registerButton.setOnClickListener(this);
		userEdit = (EditText) findViewById(R.id.username);
		passwordDontMatch = findViewById(R.id.passwordDontMatch);
		passwordEdit = (EditText) findViewById(R.id.password);
		passwordAgainEdit = (EditText) findViewById(R.id.confirmPassword);
		passwordEdit.addTextChangedListener(this);
		passwordAgainEdit.addTextChangedListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String userName = getIntent().getExtras().getString(CoreConstants.USER_NAME_KEY);
		String password = getIntent().getExtras().getString(CoreConstants.PASSWORD_KEY);
		if (userName != null) {
			Log.d(TAG, "Setting user name " + userName);
			userEdit.setText(userName);
		} else {
			userEdit.setText("");
		}
		if (password != null) {
			Log.d(TAG, "Setting password " + password);
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
			case R.id.confirmRegistrationButton:
				if (service != null) {
					final ProgressDialog progressDialog = new ProgressDialog(this);
					progressDialog.setTitle(R.string.loggingInTitle);
					progressDialog.show();
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

	private void checkFields() {
		if (passwordAgainEdit.getText().toString().equals(passwordEdit.getText().toString())) {
			Log.d(TAG, "Passwords match.");			
			passwordDontMatch.setVisibility(View.INVISIBLE);
			if (Constants.isValidPassword(passwordEdit.getText().toString())) {
				Log.d(TAG, "Valid password.");			
				registerButton.setEnabled(true);
			} else {
				Log.d(TAG, "Invalid password.");			
				registerButton.setEnabled(false);
			}
		} else {
			Log.d(TAG, "Passwords don't match.");			
			passwordDontMatch.setVisibility(View.VISIBLE);
			registerButton.setEnabled(false);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		checkFields();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}