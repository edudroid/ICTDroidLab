package hu.edudroid.module;

import org.json.JSONException;
import org.json.JSONObject;

public class QuotaLimit {
	
	private static final String QUOTA_NAME_KEY = "quota";
	private static final String LIMIT_KEY = "limit";
	public final String quota;
	public final int limit;

	public QuotaLimit(JSONObject jsonObject) throws JSONException {
		String quota = jsonObject.getString(QUOTA_NAME_KEY);
		int limit = jsonObject.getInt(LIMIT_KEY);
		this.quota = quota;
		this.limit = limit;
	}

}
