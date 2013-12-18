package hu.edudroid.ictplugin;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;
import hu.edudroid.interfaces.Quota;
import hu.edudroid.interfaces.QuotaFactory;
import hu.edudroid.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public  abstract class PluginCommunicationInterface extends BroadcastReceiver implements Plugin {
	
	private static final String TAG = PluginCommunicationInterface.class.getName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Intent received " + intent.getAction());
		if (intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_QUOTAS)) {
			Intent response = new Intent(Constants.INTENT_ACTION_QUOTA_DESCRIPTION);
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, getName());
			
			String quotaDescr = "";
			final List<Quota> quotas = getQuotas();
			for (int i = 0; i < quotas.size(); i++){
				quotaDescr += QuotaFactory.codeQuota(quotas.get(i));
				if (i != quotas.size() - 1)
					quotaDescr += "||";
			}
			response.putExtra(Constants.INTENT_EXTRA_KEY_QUOTAS, quotaDescr);			
			context.sendBroadcast(response);
		}
		
		if (intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_POLL)) {
			
			Intent response = new Intent();
			response = new Intent(Constants.INTENT_ACTION_DESCRIBE);
			response.putExtra(Constants.INTENT_EXTRA_KEY_DESCRIBE_TYPE, Constants.INTENT_EXTRA_VALUE_REPORT);
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, getName());
			response.putExtra(Constants.INTENT_EXTRA_KEY_PACKAGE_NAME, getPackageName());
			response.putExtra(Constants.INTENT_EXTRA_KEY_RECEIVER_CLASS_NAME, getReceiverClassName());
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_AUTHOR, getAuthor());
			response.putExtra(Constants.INTENT_EXTRA_KEY_DESCRIPTION, getDescription());
			response.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, getVersionCode());
			response.putStringArrayListExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_METHODS,
					new ArrayList<String>(getMethodNames()));
			response.putStringArrayListExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_EVENTS,
					new ArrayList<String>(getAllEvents()));
			context.sendBroadcast(response);
		} 
		
		if (intent.getAction().equals(Constants.INTENT_ACTION_CALL_METHOD)) {
			final long callId = intent.getExtras().getLong(Constants.INTENT_EXTRA_CALL_ID);
			final String methodName = intent.getExtras().getString(Constants.INTENT_EXTRA_METHOD_NAME);
			final byte[] bytes = intent.getExtras().getByteArray(Constants.INTENT_EXTRA_METHOD_PARAMETERS);
			Map<String, Object> params;
			try {
				params = Utils.byteArrayToMap(bytes);
				try {
					Map<String, Object> result = callMethodSync(callId, methodName, params, context);
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_RESULT, methodName, result, context);
				} 
				catch (AsyncMethodException a){
					Log.i(TAG,"This is an async method.");
				}
				catch (Exception e) {
					Map<String,Object> error = new HashMap<String, Object>();
					error.put(Constants.ERROR_MESSAGE_KEY, e.getMessage());
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_ERROR, methodName, error, context);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				Log.e(TAG, "Error parsing params.", e1);
			}
		}
	}
	
	public void reportResult(long callId, String resultCode, String method, Map<String, Object> results, Context context) {
				
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER);
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, callId);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, getName());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, getVersionCode());
		intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
		byte[] resultArray = Utils.mapToByteArray(results);
		if (resultArray != null) {
			intent.putExtra(Constants.INTENT_EXTRA_VALUE_RESULT, resultArray);
		}

		context.sendBroadcast(intent);
	}

	public void fireEvent(String eventName, Map<String, Object> result, Context context) {
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_EVENT);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, getName());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, getVersionCode());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_EVENT_NAME, eventName);
		byte[] resultArray = Utils.mapToByteArray(result);
		if (resultArray != null) {
			intent.putExtra(Constants.INTENT_EXTRA_VALUE_RESULT, resultArray);
		}
		context.sendBroadcast(intent);
	}

	@Override
	public long callMethodAsync(String method, Map<String, Object> parameters, PluginResultListener listener){
		throw new UnsupportedOperationException("Can't call async method on plugin.");
	}

	@Override
	public long callMethodAsync(String method, Map<String, Object> parameters, PluginResultListener listener, int quotaQuantity) {
		throw new UnsupportedOperationException("Can't call async method on plugin.");
	}

	@Override
	public void registerEventListener(String eventName, PluginEventListener listener) {
		throw new UnsupportedOperationException("You have to register the listener on PluginAdapter...");
	}

	@Override
	public void unregisterEventListener(String eventName,
			PluginEventListener listener) {
		throw new UnsupportedOperationException("You have to register the listener on PluginAdapter.");
		
	}

	@Override
	public void unregisterEventListener(PluginEventListener listener) {
		throw new UnsupportedOperationException("You have to unregister the listener on PluginAdapter.");
	}

	@Override
	public void cancelCallsForListener(PluginResultListener listener) {
		throw new UnsupportedOperationException("Can't cancel a call here.");
	}
}