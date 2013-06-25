package hu.edudroid.ict;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CoreService extends Service {

	private static final String TAG = "CoreService";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
        Log.e("CoreService","STARTED!");
    }

}
