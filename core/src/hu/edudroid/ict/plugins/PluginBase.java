package hu.edudroid.ict.plugins;

import hu.edudroid.ict.PluginDetailsActivity;
import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCall;
import hu.edudroid.interfaces.PluginQuota;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PluginBase implements OnClickListener, Plugin {

	private final String					INTENT_CALL_PLUGIN_METHOD		= "hu.edudroid.ict.plugin.callmethod";
	private final String					INTENT_EXTRA_METHOD_NAME		= "methodname";
	private final String					INTENT_EXTRA_METHOD_PARAMETERS	= "methodparams";

	private final String					mName;
	private final String					mAuthor;
	private final String					mDescription;
	private final String					mVersionCode;
	private final ArrayList<PluginQuota>	mQuotas;

	private Context							mContext;

	public PluginBase(	final String name,
					final String author,
					final String description,
					final String versionCode,
					final Context context) {
		mName = name;
		mAuthor = author;
		mDescription = description;
		mVersionCode = versionCode;
		mQuotas = new ArrayList<PluginQuota>();
		
		mContext = context;
	}

	public void addQuota(PluginQuota quota){
		mQuotas.add(quota);
	}

	public void callMethod(final PluginCall pluginCall){
		// validate quotas
		final String method = pluginCall.getMethodName();
		for (int i = 0; i < mQuotas.size(); i++)
			if (!mQuotas.get(i).validateQuota(method, pluginCall.getQuotaType()))
				// TODO log failed method call
				return;

		callMethod(method, pluginCall.getParameters());
	}

	private void callMethod(final String method, ArrayList<Object> params){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream stream = null;
		try{
			stream = new ObjectOutputStream(bytes);
			stream.writeObject(Integer.valueOf(params.size()));
			for (int i = 0; i < params.size(); i++)
				stream.writeObject(params.get(i));
			byte[] parameters = bytes.toByteArray();

			Intent intent = new Intent(INTENT_CALL_PLUGIN_METHOD);
			intent.putExtra(INTENT_EXTRA_METHOD_NAME, method);
			intent.putExtra(INTENT_EXTRA_METHOD_PARAMETERS, parameters);
			mContext.sendBroadcast(intent);

			bytes.close();
			stream.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public View generateView(final View root, final Context context){
		try{
			final String author = context.getString(R.string.created_by,
													mAuthor);
			((TextView) root.findViewById(R.id.plugin_title)).setText(mName
																		+ " (version "
																		+ mVersionCode
																		+ ")");
			((TextView) root.findViewById(R.id.plugin_author)).setText(author);
			((TextView) root.findViewById(R.id.plugin_description)).setText(mDescription);

			root.setOnClickListener(this);
			return root;
		}
		catch (Exception ex){
			throw new IllegalArgumentException();
		}
	}
	
	public String getName() {
		return mName;
	}

	@Override
	public void onClick(View view){
		mContext.startActivity(PluginDetailsActivity.generateIntent(hashCode(),
																	mContext));
	}

}
