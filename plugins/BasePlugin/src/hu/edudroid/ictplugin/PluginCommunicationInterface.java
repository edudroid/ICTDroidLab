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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public  abstract class PluginCommunicationInterface extends BroadcastReceiver implements Plugin {
	
	private static final String TAG = PluginCommunicationInterface.class.getName();
	SharedPreferences userlimits;
	SharedPreferences methodcounter;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Intent received " + intent.getAction());
		
		userlimits = context.getSharedPreferences("userlimits",Context.MODE_PRIVATE);
		methodcounter = context.getSharedPreferences("methodcounter",Context.MODE_PRIVATE);
		
		if (intent.getAction().equals(Constants.INTENT_ACTION_LIMIT_RESET)) {
			Map<String,?> methodcalls = methodcounter.getAll();
			Set<String> methods = methodcalls.keySet();
			for (Iterator<String> i = methods.iterator(); i.hasNext();){
				String method = (String) i.next();
				SharedPreferences.Editor editor = methodcounter.edit();
				editor.putInt(method, 0);
				editor.commit();
			}
			
		}
		
		if (intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_LIMITS)) {
			final String methodName = intent.getExtras().getString(Constants.INTENT_EXTRA_KEY_METHOD_NAME);
			final String methodLimit = intent.getExtras().getString(Constants.INTENT_EXTRA_KEY_METHOD_LIMIT);
			int limitnumber = Integer.parseInt(methodLimit);
			SharedPreferences.Editor editor1 = userlimits.edit();
			editor1.putInt(methodName, limitnumber);
			editor1.commit();
			if(methodcounter.contains(methodName)== false){
			SharedPreferences.Editor editor2 = methodcounter.edit();
			editor2.putInt(methodName, 0);
			editor2.commit();	
			}
	
		}
		
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
				ByteArrayInputStream bis = new ByteArrayInputStream(parameters);
				ObjectInputStream ois = new ObjectInputStream(bis);

				Integer paramsCount = (Integer) ois.readObject();
				Object[] params = new Object[paramsCount];
				for (int i = 0; i < paramsCount; i++)
					params[i] = ois.readObject();
				try {List<String> result;
					if(userlimits.contains(methodName) && userlimits.getInt(methodName, -1)== 0 || methodcounter.getInt(methodName, -1) < userlimits.getInt(methodName, -1)){
						result = callMethodSync(callId, methodName, Arrays.asList(params), context);
						int temp = methodcounter.getInt(methodName, -1);
						SharedPreferences.Editor editor = methodcounter.edit();
						editor.putInt(methodName, temp + 1);
						editor.commit();
					}else if (!userlimits.contains(methodName)){
						result = callMethodSync(callId, methodName, Arrays.asList(params), context);	
					}
					else{
						result = new ArrayList<String>();
						result.add(methodName + " methodcall reached its limit");
					}
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