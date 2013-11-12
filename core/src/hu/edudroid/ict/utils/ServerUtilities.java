package hu.edudroid.ict.utils;


import hu.edudroid.ict.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.util.Log;

public final class ServerUtilities {
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    
    public static final String SERVER_URL="http://ictdroidlab.appspot.com/";
    public static final String TAG="Server Utilities";
 
    /**
     * Register this account/device pair within the server.
     *
     */
    public static void register(final Context context, String imei, String gcmId, String sdk_version, boolean cellular, boolean wifi, boolean bluetooth, boolean gps) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("imei", imei);
        params.put("gcm_id", gcmId);
        params.put("sdk_version", sdk_version);
        params.put("cellular", (cellular) ? "1" : "0");
        params.put("wifi", (wifi) ? "1" : "0");
        params.put("bluetooth", (bluetooth) ? "1" : "0");
        params.put("gps", (gps) ? "1" : "0");
         
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                post(SERVER_URL+"registerdevice", params);
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
            post(SERVER_URL+"unregisterdevice", params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            Log.e(TAG,message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {    
         
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
    
    public static String get(String endpoint){
		String result="";
		
		HttpClient httpclient = new DefaultHttpClient();    
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000); //Timeout Limit

		HttpGet httpGet = new HttpGet(endpoint);
		try {
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity ent=response.getEntity();
			result=EntityUtils.toString(ent);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
}