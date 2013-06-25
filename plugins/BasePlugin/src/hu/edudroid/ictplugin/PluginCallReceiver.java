package hu.edudroid.ictplugin;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PluginCallReceiver extends BroadcastReceiver {

	private final String	INTENT_EXTRA_METHOD_NAME		= "methodname";
	private final String	INTENT_EXTRA_METHOD_PARAMETERS	= "methodparams";

	private PluginLogic	mPlugin;

	@Override
	public void onReceive(Context context, Intent intent){
		mPlugin = PluginLogic.getInstance(context);

		final String methodName = intent.getExtras()
										.getString(INTENT_EXTRA_METHOD_NAME);
		final byte[] parameters = intent.getExtras()
										.getByteArray(INTENT_EXTRA_METHOD_PARAMETERS);

		try{
			ByteArrayInputStream bis = new ByteArrayInputStream(parameters);
			ObjectInputStream ois = new ObjectInputStream(bis);

			Integer paramsCount = (Integer) ois.readObject();
			Object[] params = new Object[paramsCount];
			for (int i = 0; i < paramsCount; i++)
				params[i] = ois.readObject();
			
			mPlugin.callMethod(methodName, params);
		}
		catch (Exception ex){
			ex.printStackTrace();
			return;
		}
	}
}
