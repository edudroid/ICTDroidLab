package hu.edudroid.ictplugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PluginLogic {

	private static final String				INTENT_REPORT_RESULT	= "hu.edudroid.ict.plugin_polling_answer";
	private static PluginLogic				mInstance				= null;

	public final String						mTitle					= "Test Plugin 1";
	public final String						mAuthor					= "Szabolcs Nagy";
	public final String						mDescription			= "This plugin was created for testing purposes.";
	public final String						mVersionCode			= "1.0";

	private final ArrayList<PluginMethod>	mMethods;
	private final Context					mContext;

	private PluginLogic(Context context) {
		mContext = context;

		mMethods = new ArrayList<PluginMethod>();
		mMethods.add(new PluginMethod(	"showToast",
										"Shows a toast message combined of the three parameters given.",
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

	public final void callMethod(final String methodName, final Object[] params){
		// final Method[] methods = getClass().getMethods();
		for (int i = 0; i < mMethods.size(); i++){
			if (mMethods.get(i).mName.equals(methodName)){
				try {
					mMethods.get(i).invoke(params);
					return;
				}
				catch (IllegalArgumentException e){
					reportError(methodName, "IllegalArgumentException", e.getMessage());
				}
				catch (InvocationTargetException e){
					reportError(methodName, "InvocationTargetException", e.getMessage());
				}
				catch (IllegalAccessException e){
					reportError(methodName, "IllegalAccessException", e.getMessage());
				}
			}
					
		}
	}

	private void reportResult(final String resultCode, final String sender, final String result, final String metadata){
		Intent intent = new Intent(INTENT_REPORT_RESULT);
		intent.putExtra("action", resultCode);
		intent.putExtra("plugin", mTitle);
		intent.putExtra("version", mVersionCode);
		intent.putExtra("meta", metadata);
		intent.putExtra("sender", sender);
		intent.putExtra("result", result);
		mContext.sendBroadcast(intent);
	}
	
	protected final void reportResult(final String sender, final String result, final String metadata){
		reportResult("reportResult", sender, result, metadata);
	}
	
	protected final void reportResult(final String sender, final String result){
		reportResult("reportResult", sender, result, "");
	}
	
	protected final void reportError(final String sender, final String result, final String metadata){
		reportResult("reportError", sender, result, metadata);
	}
	
	protected final void reportError(final String sender, final String result){
		reportResult("reportError", sender, result, "");
	}

	public void showToast(String msg1, String msg2, String msg3){
		Toast.makeText(mContext, "showToast: "+msg1 + msg2 + msg3, Toast.LENGTH_LONG).show();
		reportResult("showToast", msg1 + " " + msg2 + " " + msg3);
	}
}