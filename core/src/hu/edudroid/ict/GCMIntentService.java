package hu.edudroid.ict;

import hu.edudroid.ict.utils.ServerUtilities;
import hu.edudroid.module.ModuleLoader;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    protected void onRegistered(Context context, String gcmId) {
        
    	TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        String imei=mngr.getDeviceId(); 
		
        String sdk_version=String.valueOf(android.os.Build.VERSION.SDK_INT);
        
		PackageManager pm = this.getPackageManager();
		
		boolean cellular = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
		boolean wifi = pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
		boolean bluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
		boolean gps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    	
        RegisterToServer regTask = new RegisterToServer(this,imei,gcmId,sdk_version,cellular,wifi,bluetooth,gps);
        Thread thread = new Thread(regTask, "RegisterToServer");
        thread.start();
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
    
    public class RegisterToServer implements Runnable {

		Context mContext;
		String mIMEI;
		String mGcmId;
		String mSdk_version;
		boolean mCellular;
		boolean mWifi;
		boolean mBluetooth;
		boolean mGps;
		
        public RegisterToServer(Context context,String imei, String gcmId, String sdk_version, boolean cellular, boolean wifi, boolean bluetooth, boolean gps) {
            mContext=context;
            mIMEI=imei;
            mGcmId=gcmId;
            mSdk_version=sdk_version;
            mCellular=cellular;
            mWifi=wifi;
            mBluetooth=bluetooth;
            mGps=gps;
        }

        public void run() {
        	ServerUtilities.register(mContext, mIMEI, mGcmId, mSdk_version, null);
        }
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