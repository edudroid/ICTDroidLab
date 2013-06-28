package hu.edudroid.ictpluginwifi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PluginReportReceiver extends BroadcastReceiver {

	private final String	FILTER_NEW_PLUGIN	= "hu.edudroid.ict.plugin_polling_answer";

	@Override
	public void onReceive(Context context, Intent intent){
		Log.d("PLUGIN_2::PluginReportReceiver:onReceive","Received broadcast...");
		
		final Bundle extras = intent.getExtras();
		if (extras == null)
			return;

		PluginLogic plugin = PluginLogic.getInstance(context);
		Intent[] answer = null;
		
		try{
			final String action = extras.getString("action");
			final Method[] methods = this.getClass().getMethods();
			boolean methodCalled = false;
			for (int i = 0; (i < methods.length && !methodCalled); i++)
				if (methods[i].getName().equals(action)){
					answer = (Intent[]) methods[i].invoke(this, plugin);
					methodCalled = true;
				}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		if (answer != null){
			for (int i = 0; i < answer.length; i++){
				context.sendBroadcast(answer[i]);
			}
			Log.d("PLUGIN_1::PluginReportReceiver:onReceive","Answer sent, num of methods: " + String.valueOf(answer.length));
		}
		
		
	}

	public Intent[] reportSelf(PluginLogic plugin){
		Intent answer[] = new Intent[1];
		answer[0] = new Intent(FILTER_NEW_PLUGIN);
		answer[0].putExtra("action", "reportSelf");
		answer[0].putExtra("title", plugin.mTitle);
		answer[0].putExtra("author", plugin.mAuthor);
		answer[0].putExtra("description", plugin.mDescription);
		answer[0].putExtra("version", plugin.mVersionCode);
		answer[0].putStringArrayListExtra("pluginMethods", plugin.getMethodsName());
		
		return answer;
	}

	public Intent[] reportMethods(PluginLogic plugin){
		final ArrayList<PluginMethod> methods = plugin.getMethods();
		
		Intent answer[] = new Intent[methods.size()];
		for (int i = 0; i < answer.length; i++){
			answer[i] = new Intent(FILTER_NEW_PLUGIN);
			answer[i].putExtra("action", "reportMethods");
			answer[i].putExtra("order", i);
			answer[i].putExtra("name", methods.get(i).mName);
			answer[i].putExtra("description", methods.get(i).mDescription);
		}
		
		return answer;
	}
	
	public Intent[] onEvent(PluginLogic plugin){
		final ArrayList<PluginMethod> methods = plugin.getMethods();
		
		Intent answer[] = new Intent[methods.size()];
		for (int i = 0; i < answer.length; i++){
			answer[i] = new Intent(FILTER_NEW_PLUGIN);
			answer[i].putExtra("action", "onEvent");
			answer[i].putExtra("eventName", "EVENT NAME");
			answer[i].putStringArrayListExtra("eventParams", null);
		}
		
		return answer;
	}

}
