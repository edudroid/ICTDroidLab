package hu.edudroid.ictplugin;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;
import hu.edudroid.interfaces.Quota;
import hu.edudroid.interfaces.QuotaFactory;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
			final byte[] parameters = intent.getExtras().getByteArray(Constants.INTENT_EXTRA_METHOD_PARAMETERS);
			try {
				List<Object> paramList = new ArrayList<Object>();
				if (parameters != null) {
					ByteArrayInputStream bis = new ByteArrayInputStream(parameters);
					ObjectInputStream ois = new ObjectInputStream(bis);
	
					Integer paramsCount = (Integer) ois.readObject();
					Object[] params = new Object[paramsCount];
					for (int i = 0; i < paramsCount; i++) {
						params[i] = ois.readObject();
					}
					paramList.addAll(Arrays.asList(params));
				}
				try {
					List<String> result = callMethodSync(callId, methodName, paramList, context);
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_RESULT, methodName, result, context);
				} 
				catch (AsyncMethodException a){
					Log.i(TAG,"This is an async method.");
				}
				catch (Exception e) {
					List<String> result = new ArrayList<String>();
					result.add(e.getMessage());
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_ERROR, methodName, result, context);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}
	}
	
	public void reportResult(long callId, String resultCode, String method, List<String> result, Context context) {
				
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER);
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, callId);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, getName());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, getVersionCode());
		intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
		intent.putStringArrayListExtra(Constants.INTENT_EXTRA_VALUE_RESULT, new ArrayList<String>(result));
		context.sendBroadcast(intent);
	}

	public void fireEvent(String eventName, List<String> result, Context context) {
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_EVENT);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, getName());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, getVersionCode());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_EVENT_NAME, eventName);
		intent.putStringArrayListExtra(Constants.INTENT_EXTRA_VALUE_RESULT, new ArrayList<String>(result));
		context.sendBroadcast(intent);
	}

	@Override
	public long callMethodAsync(String method, List<Object> parameters, PluginResultListener listener){
		throw new UnsupportedOperationException("Can't call async method on plugin.");
	}
	
	@Override
	public long callMethodAsync(String method, List<Object> parameters, PluginResultListener listener, int quotaQuantity) {
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