package hu.edudroid.interfaces;

import java.io.Serializable;
import java.util.ArrayList;

public class PluginCall {

	private final String				mMethod;
	private final ArrayList<Object>		mParameters;
	private final PluginQuota.QuotaType	mType;

	public PluginCall(final String method){
		mMethod = method;
		mType = PluginQuota.QuotaType.QUOTA_LIGHT;
		mParameters = new ArrayList<Object>();
	}
	
	public PluginCall(final String method, final PluginQuota.QuotaType type) {
		mMethod = method;
		mType = type;
		mParameters = new ArrayList<Object>();
	}

	public boolean addParameter(Object parameter){
		if (parameter instanceof Serializable){
			mParameters.add(parameter);
			return true;
		}

		return false;
	}

	public String getMethodName(){
		return mMethod;
	}

	public ArrayList<Object> getParameters(){
		return mParameters;
	}
	
	public PluginQuota.QuotaType getQuotaType(){
		return mType;
	}
}
