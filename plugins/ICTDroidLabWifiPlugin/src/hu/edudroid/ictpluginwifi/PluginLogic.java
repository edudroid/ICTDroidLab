package hu.edudroid.ictpluginwifi;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PluginLogic {

	private static final String				INTENT_REPORT_RESULT	= "hu.edudroid.ict.plugin_polling_answer";
	private static PluginLogic				mInstance				= null;

	public final String						mTitle					= "WiFi Plugin";
	public final String						mAuthor					= "Patrik Weisz";
	public final String						mDescription			= "This plugin was created for testing purposes.";
	public final String						mVersionCode			= "1.0";

	private final ArrayList<PluginMethod>	mMethods;
	private final Context					mContext;

	private PluginLogic(Context context) {
		mContext = context;

		mMethods = new ArrayList<PluginMethod>();
		mMethods.add(new PluginMethod(	"showIPAddress",
										"Shows the device's IP Address",
										this));
		mMethods.add(new PluginMethod(	"showMACAddress",
										"Shows the device's MAC Address",
										this));
		mMethods.add(new PluginMethod(	"showNetMaskAddress",
										"Shows the device's NetMask Address",
										this));
		mMethods.add(new PluginMethod(	"showNetworkSpeed",
										"Shows the device's Network Speed",
										this));
	}

	public static PluginLogic getInstance(Context context){
		// double check locking
		if (mInstance == null){
			synchronized (PluginLogic.class){
				if (mInstance == null)
					mInstance = new PluginLogic(context);
			}
		}
		return mInstance;
	}

	public ArrayList<PluginMethod> getMethods(){
		return mMethods;
	}
	
	public ArrayList<String> getMethodsName(){
		ArrayList<String> methods=new ArrayList<String>();
		for(int i=0;i<mMethods.size();i++){
			methods.add(mMethods.get(i).mName);
		}
		return methods;
	}

	public final void callMethodSync(int id, final String methodName, final Object[] params){
		// final Method[] methods = getClass().getMethods();
		for (int i = 0; i < mMethods.size(); i++){
			if (mMethods.get(i).mName.equals(methodName)){
				try {
					mMethods.get(i).invoke(params);
					return;
				}
				catch (IllegalArgumentException e){
					reportError(id, methodName, "IllegalArgumentException", e.getMessage());
				}
				catch (InvocationTargetException e){
					reportError(id, methodName, "InvocationTargetException", e.getMessage());
				}
				catch (IllegalAccessException e){
					reportError(id, methodName, "IllegalAccessException", e.getMessage());
				}
			}
					
		}
	}

	private void reportResult(final String resultCode, final int id, final String sender, final String result, final String metadata){
		Intent intent = new Intent(INTENT_REPORT_RESULT);
		intent.putExtra("action", resultCode);
		intent.putExtra("id", id);
		intent.putExtra("plugin", mTitle);
		intent.putExtra("version", mVersionCode);
		intent.putExtra("meta", metadata);
		intent.putExtra("sender", sender);
		intent.putExtra("result", result);
		mContext.sendBroadcast(intent);
	}
	
	protected final void reportResult(final int id, final String sender, final String result, final String metadata){
		reportResult("reportResult", id, sender, result, metadata);
	}
	
	protected final void reportResult(final int id, final String sender, final String result){
		reportResult("reportResult", id, sender, result, "");
	}
	
	protected final void reportError(final int id, final String sender, final String result, final String metadata){
		reportResult("reportError", id, sender, result, metadata);
	}
	
	protected final void reportError(final int id, final String sender, final String result){
		reportResult("reportError", id, sender, result, "");
	}

	public void showIPAddress(int id, String msg1, String msg2, String msg3){
		Toast.makeText(mContext, "showIPAddress: "+msg1 + msg2 + msg3, Toast.LENGTH_LONG).show();
		reportResult(id, "showIPAddress", msg1 + " " + msg2 + " " + msg3);
	}
	
	public void showMACAddress(int id, String msg1, String msg2, String msg3){
		Toast.makeText(mContext, "showMACAddress: "+msg1 + msg2 + msg3, Toast.LENGTH_LONG).show();
		reportResult(id, "showMACAddress", msg1 + " " + msg2 + " " + msg3);
	}
	
	public void showNetMaskAddress(int id, String msg1, String msg2, String msg3){
		Toast.makeText(mContext, "showNetMaskAddress: "+msg1 + msg2 + msg3, Toast.LENGTH_LONG).show();
		reportResult(id, "showNetMaskAddress", msg1 + " " + msg2 + " " + msg3);
	}
	
	public void showNetworkSpeed(int id, String msg1, String msg2, String msg3){
		Toast.makeText(mContext, "showNetworkSpeed: "+msg1 + msg2 + msg3, Toast.LENGTH_LONG).show();
		reportResult(id, "showNetworkSpeed", msg1 + " " + msg2 + " " + msg3);
	}
}