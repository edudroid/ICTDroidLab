package hu.edudroid.ictplugin;

import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public  abstract class PluginCommunicationInterface extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Plugin plugin = getPlugin();
		
		if (intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_POLL)) {
			
			Intent response = new Intent();
			response = new Intent(Constants.INTENT_ACTION_DESCRIBE);
			response.putExtra(Constants.INTENT_EXTRA_KEY_DESCRIBE_TYPE, Constants.INTENT_EXTRA_VALUE_REPORT);
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, plugin.getName());
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_AUTHOR, plugin.getAuthor());
			response.putExtra(Constants.INTENT_EXTRA_KEY_DESCRIPTION, plugin.getDescription());
			response.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, plugin.getVersionCode());
			response.putStringArrayListExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_METHODS,
					new ArrayList<String>(plugin.getMethodNames()));
			response.putStringArrayListExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_EVENTS,
					new ArrayList<String>(plugin.getAllEvents()));
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
					List<String> result = plugin.callMethodSync(methodName, Arrays.asList(params));
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_RESULT, plugin, methodName, result, context);
				} catch (Exception e) {
					List<String> result = new ArrayList<String>();
					result.add(e.getMessage());
					reportResult(callId, Constants.INTENT_EXTRA_VALUE_ERROR, plugin, methodName, result, context);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}
	}

	private void reportResult(long callId, String resultCode, Plugin plugin, String method, List<String> result, Context context) {
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER);
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, callId);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, plugin.getName());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, plugin.getVersionCode());
		intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
		intent.putStringArrayListExtra(Constants.INTENT_EXTRA_VALUE_RESULT, new ArrayList<String>(result));
		context.sendBroadcast(intent);
	}
	
	protected abstract Plugin getPlugin();
	
	protected abstract void onEvent(String eventName, List<String> params);
}
