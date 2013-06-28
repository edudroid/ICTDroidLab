package hu.edudroid.ictplugin;

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

public class PluginCommunicationInterface extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Plugin plugin = getPlugin();
		// Send description
		if (intent.getAction().equals(Constants.INTENT_ACTION_REQUEST_DESCRIPTION)) {
			Intent response = new Intent();
			response = new Intent(Constants.INTENT_ACTION_DESCRIBE);
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, plugin.getName());
			response.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_AUTHOR, plugin.getAuthor());
			response.putExtra(Constants.INTENT_EXTRA_KEY_DESCRIPTION, plugin.getDescription());
			response.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, plugin.getVersionCode());
			response.putStringArrayListExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_METHODS,
					new ArrayList<String>(plugin.getMethodNames()));
			context.sendBroadcast(intent);
		} else if (intent.getAction().equals(Constants.INTENT_ACTION_CALL_METHOD)) {
			final String callId = intent.getExtras().getString(Constants.INTENT_EXTRA_CALL_ID);
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

	private void reportResult(String id, String resultCode, Plugin plugin, String method, List<String> result, Context context) {
		Intent intent = new Intent(Constants.INTENT_ACTION_RESULT);
		intent.putExtra("id", id);
		intent.putExtra("action", resultCode);
		intent.putExtra("plugin", plugin.getName());
		intent.putExtra("version", plugin.getVersionCode());
		intent.putExtra("method", method);
		intent.putStringArrayListExtra("result", new ArrayList<String>(result));
		context.sendBroadcast(intent);
	}
	
	protected Plugin getPlugin() {
		return new Plugin() {
			
			private List<String> methods = Arrays.asList(new String[] {"firstMethod", "secondMethod", "thirdMethod"});
			
			@Override
			public String getVersionCode() {
				return "v0.1";
			}
			
			@Override
			public String getName() {
				return "Dummy Plugin";
			}
			
			@Override
			public List<String> getMethodNames() {
				return methods;
			}
			
			@Override
			public String getDescription() {
				return "Replace the getPlugin() method with your own code!";
			}
			
			@Override
			public String getAuthor() {
				return "BME TMIT";
			}
			
			@Override
			public List<String> callMethodSync(String method, List<Object> parameters) {
				if (methods.contains(method)){
					return Arrays.asList(new String[]{"Result", "of", "Sample", "Plugin"});
				} else {
					throw new UnsupportedOperationException("Method " + method + " is not part of the plugin.");
				}
			}
			
			@Override
			public void callMethod(String method, List<Object> parameters) {
				throw new UnsupportedOperationException("Don't call plugin in an assync way.");
			}
		};
	}
}
