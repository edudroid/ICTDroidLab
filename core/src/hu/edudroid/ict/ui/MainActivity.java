package hu.edudroid.ict.ui;

import hu.edudroid.ict.R;
import hu.edudroid.ict.gcm.ServerUtilities;
import hu.edudroid.interfaces.Plugin;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends ActivityBase implements OnClickListener{

	private Button showModules;
	private Button showPlugins;
	private Button manageLocalStorage;
	private Button stats;
	
	// Google project id
    public static final String SENDER_ID = "1017069233076";
	
	
	// Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;
     
    public static String name;
    public static String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showModules = (Button)findViewById(R.id.showModules);
		showModules.setOnClickListener(this);
		showPlugins = (Button)findViewById(R.id.showPlugins);
		showPlugins.setOnClickListener(this);
		stats = (Button)findViewById(R.id.statsButton);
		stats.setOnClickListener(this);
		manageLocalStorage = (Button)findViewById(R.id.manageLocalStorageButton);
		manageLocalStorage.setOnClickListener(this);
         
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
 
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
         
         
        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(this);
        Log.e("Reg id:",regId);
 
        // Check if regid already presents
        if (regId.equals("")) {
            Log.e("GCM:","Registration is not present, register now with GCM ");          
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            Log.e("GCM:","Device is already registered on GCM");
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                Log.e("GCM:","Skips registration.");              
                Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {
 
                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        ServerUtilities.register(context, name, email, regId);
                        return null;
                    }
 
                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
 
                };
                mRegisterTask.execute(null, null, null);
            }
        }
	}
     
    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }

	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		super.onServiceConnected(arg0, arg1);
		refreshUI();
	}

	private void refreshUI() {
		if (service != null) {
			List<Plugin> plugins = service.getPlugins();
			if (plugins!=null && plugins.size() > 0) {
				showPlugins.setText(getString(R.string.showPlugins, plugins.size()));
			} else {
				showPlugins.setText(R.string.noPlugins);
			}
		} else {
			showPlugins.setText(R.string.noPlugins);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.statsButton:
				Toast.makeText(this, "Under development...", Toast.LENGTH_LONG).show();
				break;
			case R.id.manageLocalStorageButton:
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

	@Override
	public boolean newPlugin(Plugin plugin) {
		refreshUI();
		return false;
	}

}
