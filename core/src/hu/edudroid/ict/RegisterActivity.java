package hu.edudroid.ict;

import java.util.LinkedList;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;

public class RegisterActivity extends Activity {

	public static final String	PREFS_NAME			= "OnlabPrefsName";
	public static final String	REGISTRATION_KEY	= "RegistrationKeyOnlag";
	public static final String	GCM_KEY				= "OnlabGCMKey";
	private static final String	URL					= "http://www.innoid.hu/CellInfo/register.php";

	private static final String	SENDER_ID			= "339414602702";

	private static final int	T_MOBILE			= 0;
	private static final int	TELENOR				= 1;
	private static final int	VODAFONE			= 2;

	private static final String	PHONE_ID_KEY		= "PhoneID";
	private static final String	ANDROID_VERSION_KEY	= "AndroidID";
	private static final String	PHONE_NUMBER_KEY	= "PhoneNumber";
	private static final String	SERVICE_KEY			= "Service";
	private static final String	GCM_POST_KEY		= "Gcm";

	private TextView			phoneID;
	private TextView			androidVersion;
	private TextView			phoneNumber;
	private EditText			phoneNumberEditText;
	private RadioGroup			serviceGroup;

	private boolean				hasPhoneNumber;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		final SharedPreferences prefs = getSharedPreferences(	PREFS_NAME,
																Context.MODE_PRIVATE);

		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")){
			GCMRegistrar.register(this, SENDER_ID);
		}
		if (prefs.contains(REGISTRATION_KEY)){
			final Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}

		setContentView(R.layout.activity_register);
		phoneID = (TextView) findViewById(R.id.telephone_type);
		androidVersion = (TextView) findViewById(R.id.android_version);
		serviceGroup = (RadioGroup) findViewById(R.id.serviceGroup);
		phoneNumber = (TextView) findViewById(R.id.phone_number);
		phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);

		phoneID.setText("Telefon azonosító: " + android.os.Build.MODEL);
		androidVersion.setText("Android: " + android.os.Build.VERSION.SDK_INT);
		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (tMgr.getLine1Number() == null || tMgr.getLine1Number().equals("")){
			hasPhoneNumber = false;
			phoneNumber.setText("Telefonszám ismertelen. Ird be a telefonszámod");
			phoneNumberEditText.setVisibility(View.VISIBLE);
		}
		else
			hasPhoneNumber = true;

		findViewById(R.id.registerButton).setOnClickListener(new OnClickListener() {

			public void onClick(View v){

				if (!hasPhoneNumber
					&& phoneNumberEditText.getText().toString().equals(""))
					return;

				final int id = serviceGroup.getCheckedRadioButtonId();
				int serviceID = 0;
				switch (id){
					case R.id.radio0:
						serviceID = T_MOBILE;
						break;
					case R.id.radio1:
						serviceID = TELENOR;
						break;
					case R.id.radio2:
						serviceID = VODAFONE;
						break;
				}
				registration(	android.os.Build.MODEL,
								android.os.Build.VERSION.SDK_INT,
								phoneNumberEditText.getText().toString(),
								serviceID);

			}
		});
	}

	public void registration(	final String phoneID,
								final int versionCode,
								final String phoneNumber,
								final int serviceID){
		if (!getSharedPreferences(PREFS_NAME, MODE_PRIVATE).contains(GCM_KEY)){
			Toast.makeText(this, "Nincs GCM key", Toast.LENGTH_LONG).show();
			return;
		}

		new Thread() {

			@Override
			public void run(){

				LinkedList<BasicNameValuePair> list = new LinkedList<BasicNameValuePair>();
				final SharedPreferences prefs = getSharedPreferences(	PREFS_NAME,
																		Context.MODE_PRIVATE);
				final String GCM = prefs.getString(GCM_KEY, "");
				list.add(new BasicNameValuePair(PHONE_ID_KEY, phoneID));
				list.add(new BasicNameValuePair(ANDROID_VERSION_KEY,
												versionCode + ""));
				list.add(new BasicNameValuePair(PHONE_NUMBER_KEY, phoneNumber));
				list.add(new BasicNameValuePair(SERVICE_KEY, serviceID + ""));
				list.add(new BasicNameValuePair(GCM_POST_KEY, GCM));
				final String response = HttpUtils.post(URL, list);
				Log.e("Response", response);

				if (response.contains("Success")){
					prefs.edit().putString(REGISTRATION_KEY, GCM).commit();
					Intent intent = new Intent(	RegisterActivity.this,
												MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}.start();
	}
}
