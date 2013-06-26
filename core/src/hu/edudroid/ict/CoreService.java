package hu.edudroid.ict;

import hu.edudroid.module.ModuleLoader;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CoreService extends Service {

	public static final String TAG = "CoreService";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
        Log.e("CoreService","STARTED!");
        Log.e("ModuleLoading","Starting...");
                
        ModuleLoader.runModule("none", "ModuleExample.jar", this);
        
        Log.e("ModuleLoading","Module has been loaded succesfully!");
        
    }

}
