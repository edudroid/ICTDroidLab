package hu.edudroid.ict;

import hu.edudroid.ict.utils.ServerUtilities;
import hu.edudroid.module.ModuleLoader;
import android.content.Context;
import android.content.Intent;
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
    protected void onRegistered(final Context context, final String gcmId) {
		Log.e(TAG, "GCM registration successful");
    	Intent registerIntent = new Intent(context, CoreService.class);
    	registerIntent.setAction(CoreService.REGISTER_DEVICE_COMMAND);
    	context.startService(registerIntent);
    }
 
    /**
     * Method called on device unregistered
     * */
    @Override
    protected void onUnregistered(final Context context, final String registrationId) {
     	new Thread(new Runnable() {
    		@Override
    		public void run() {
    			ServerUtilities.unregister(context, registrationId);
    		}
    	}).start();
    }
 
    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(final Context context, final Intent intent) {
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
    protected void onDeletedMessages(final Context context, final int total) {
        String message = getString(R.string.gcm_deleted, total);
        if(message!=null){
        	Log.d(TAG,message);
        }
    }
 
    /**
     * Method called on Error
     * */
    @Override
    public void onError(final Context context, final String errorId) {
        Log.e(TAG, "Received error: " + errorId);
    }
 
    @Override
    protected boolean onRecoverableError(final Context context, final String errorId) {
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }
 
}