package hu.edudroid.ict;

import hu.edudroid.ict.utils.ServerUtilities;
import hu.edudroid.module.ModuleLoader;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
 
public class GCMIntentService extends GCMBaseIntentService {
 
    private static final String TAG = "GCMIntentService";
 
    public GCMIntentService() {
        super(CoreService.SENDER_ID);
    }
 
    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, final String gcmId) {
    	TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        final String imei=mngr.getDeviceId(); 
        final String sdkVersion=String.valueOf(android.os.Build.VERSION.SDK_INT);
        final String deviceName = "default"; // TODO get a device name
        new Thread(new Runnable() {
			@Override
			public void run() {
		    	boolean registered = ServerUtilities.registerDevice(GCMIntentService.this, imei, deviceName, gcmId, sdkVersion, null);
				Log.e("Device registered", "Success: " + registered);
			}
		}).start();
    }
 
    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        unRegisterToServer regTask = new unRegisterToServer(context,registrationId);
        Thread thread = new Thread(regTask, "unRegisterToServer");
        thread.start();
    }
 
    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        String message = intent.getExtras().getString("message");
        if(message!=null){
        	Log.i(TAG, "Message has been received: "+message);
        	ModuleLoader.downloadModule(this, message);
        }
    }
 
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        String message = getString(R.string.gcm_deleted, total);
        if(message!=null){
        	Log.d(TAG,message);
        }
    }
 
    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.e(TAG, "Received error: " + errorId);
    }
 
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }
    
    public class unRegisterToServer implements Runnable {

		Context mContext;
		String mRegistrationID;
		
        public unRegisterToServer(Context context, String regId) {
            mContext=context;
            mRegistrationID=regId;
        }

        public void run() {
        	ServerUtilities.unregister(mContext, mRegistrationID);
        }
    }
 
}