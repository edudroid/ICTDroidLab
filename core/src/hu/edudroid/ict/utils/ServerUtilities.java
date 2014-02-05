package hu.edudroid.ict.utils;

import hu.edudroid.ict.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	public static final String SERVER_URL = "http://ictdroidlab.appspot.com/";
	public static final String TAG = "Server Utilities";
	private static final int CONNECTION_TIMEOUT = 0;
	private static final int SOCKET_TIMEOUT = 0;

	/**
	 * Register this account/device pair within the server.
	 * 
	 */
	public static void register(final Context context, String imei, String gcmId, String androidVersion, Map<String, Integer> pluginVersions) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("imei", imei);
		params.put("gcm_id", gcmId);
		params.put("androidVersion", androidVersion);
		if (pluginVersions != null) {
			for (Entry<String, Integer> pluginVersion : pluginVersions.entrySet()) {
				params.put(pluginVersion.getKey(), pluginVersion.getValue().toString());
			}
		}

		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				post(SERVER_URL + "registerdevice", params);
				GCMRegistrar.setRegisteredOnServer(context, true);
				return;
			} catch (IOException e) {
				Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}
				backoff *= 2;
			}
		}
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, final String regId) {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("device_id", regId);
		try {
			post(SERVER_URL + "unregisterdevice", params);
			GCMRegistrar.setRegisteredOnServer(context, false);
			String message = context.getString(R.string.server_unregistered);
			Log.e(TAG, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param urlString
	 *            POST address.
	 * @param params
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */
	public static String post(String url, Map<String, String> params)
			throws IOException {
		HttpClient httpClient = null;
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(parameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(parameters, SOCKET_TIMEOUT);
			httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			// Set post parameters
			LinkedList<BasicNameValuePair> nameValuePairs = new LinkedList<BasicNameValuePair>();
			Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			}
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			// TODO cookies
			String result = EntityUtils.toString(httpEntity, "UTF-8");
			return result;
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().closeIdleConnections(
						CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
			}
		}
	}

	public static String get(String endpoint) {
		// TODO get cookie for site.
		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams
				.setConnectionTimeout(httpclient.getParams(), 10000); // Timeout
																		// Limit

		HttpGet httpGet = new HttpGet(endpoint);
		try {
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity ent = response.getEntity();
			return EntityUtils.toString(ent);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}