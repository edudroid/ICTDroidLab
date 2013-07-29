package hu.edudroid.ictplugin;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;

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
	
	private Context context = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
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
				try {
					List<String> result = callMethodSync(callId, methodName, Arrays.asList(params));
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_RESULT, this.getName(), this.getVersionCode(), methodName, result, context);
				} 
				catch (AsyncMethodException a){
					Log.i("Async method call","handled");
				}
				catch (Exception e) {
					List<String> result = new ArrayList<String>();
					result.add(e.getMessage());
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_ERROR, this.getName(), this.getVersionCode(), methodName, result, context);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}
		this.context = null;
	}
	
	protected Context getContext() {
		return context;
	}

	public static void reportResult(long callId, String resultCode, String pluginName, String pluginVersion, String method, List<String> result, Context context) {
				
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER);
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, callId);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, pluginName);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, pluginVersion);
		intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
		intent.putStringArrayListExtra(Constants.INTENT_EXTRA_VALUE_RESULT, new ArrayList<String>(result));
		context.sendBroadcast(intent);
	}	
}