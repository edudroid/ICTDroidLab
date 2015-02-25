package hu.edudroid.ictplugin;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.Quota;
import hu.edudroid.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PluginCommunicationInterface {
	
	private static final String TAG = PluginCommunicationInterface.class.getName();
	
	private Plugin plugin;
	
	/**
	 * @param plugin
	 */
	public PluginCommunicationInterface (Plugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Processes an intent. If the intent is a framework intent, returns true.
	 * @param context The context to be used to send broadcasts
	 * @param intent The intent received
	 * @return True if the intent was processed (even if with an error) or false if the intent was not from the framework
	 */
	public boolean onReceive(Context context, Intent intent) {
		Log.d(TAG, "Intent received " + intent.getAction());
		if (intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_QUOTAS)) {
			Intent response = new Intent(Constants.INTENT_ACTION_QUOTA_DESCRIPTION);
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, plugin.getName());
			
			String quotaDescr = "";
			final List<Quota> quotas = plugin.getQuotas();
			for (int i = 0; i < quotas.size(); i++){
				quotaDescr += Quota.codeQuota(quotas.get(i));
				if (i != quotas.size() - 1)
					quotaDescr += "||";
			}
			response.putExtra(Constants.INTENT_EXTRA_KEY_QUOTAS, quotaDescr);			
			context.sendBroadcast(response);
			return true;
		}
		
		if (intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_POLL)) {
			
			Intent response = new Intent();
			response = new Intent(Constants.INTENT_ACTION_DESCRIBE);
			response.putExtra(Constants.INTENT_EXTRA_KEY_DESCRIBE_TYPE, Constants.INTENT_EXTRA_VALUE_REPORT);
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, plugin.getName());
			response.putExtra(Constants.INTENT_EXTRA_KEY_PACKAGE_NAME, plugin.getPackageName());
			response.putExtra(Constants.INTENT_EXTRA_KEY_RECEIVER_CLASS_NAME, plugin.getReceiverClassName());
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_AUTHOR, plugin.getAuthor());
			response.putExtra(Constants.INTENT_EXTRA_KEY_DESCRIPTION, plugin.getDescription());
			response.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, plugin.getVersionCode());
			response.putStringArrayListExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_METHODS,
					new ArrayList<String>(plugin.getMethodNames()));
			response.putStringArrayListExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_EVENTS,
					new ArrayList<String>(plugin.getAllEvents()));
			context.sendBroadcast(response);
			return true;
		} 
		
		if (intent.getAction().equals(Constants.INTENT_ACTION_CALL_METHOD)) {
			final long callId = intent.getExtras().getLong(Constants.INTENT_EXTRA_CALL_ID);
			final String methodName = intent.getExtras().getString(Constants.INTENT_EXTRA_METHOD_NAME);
			final byte[] bytes = intent.getExtras().getByteArray(Constants.INTENT_EXTRA_METHOD_PARAMETERS);
			final HashMap<Long, Double> quotaLimits = null; // TODO get quota limits
			Map<String, Object> params;
			try {
				params = Utils.byteArrayToMap(bytes);
				try {
					PluginResult result = plugin.callMethodSync(callId, methodName, params, quotaLimits, context);
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_RESULT, methodName, result, context);
					return true;
				} 
				catch (AsyncMethodException a){
					Log.i(TAG,"This is an async method.");
					return true;
				}
				catch (Exception e) {
					Map<String,Object> error = new HashMap<String, Object>();
					error.put(Constants.ERROR_MESSAGE_KEY, e.getMessage());
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_ERROR, methodName, new PluginResult(error, null), context);
					return true;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				Log.e(TAG, "Error parsing params.", e1);
				return true;
			}
		}
		return false;
	}
	
	public void reportResult(long callId, String resultCode, String method, PluginResult results, Context context) {
				
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER);
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, callId);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, plugin.getName());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, plugin.getVersionCode());
		intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
		byte[] resultArray = Utils.mapToByteArray(results.getResult());
		// TODO send back quota consumption
		if (resultArray != null) {
			intent.putExtra(Constants.INTENT_EXTRA_VALUE_RESULT, resultArray);
		}

		context.sendBroadcast(intent);
	}

	public void fireEvent(String eventName, Map<String, Object> result, Context context) {
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_EVENT);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, plugin.getName());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, plugin.getVersionCode());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_EVENT_NAME, eventName);
		byte[] resultArray = Utils.mapToByteArray(result);
		if (resultArray != null) {
			intent.putExtra(Constants.INTENT_EXTRA_VALUE_RESULT, resultArray);
		}
		context.sendBroadcast(intent);
	}
}