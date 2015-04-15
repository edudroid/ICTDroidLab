package hu.edudroid.ict.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class HttpUtils {

	private static final String TAG = HttpUtils.class.getName();
	
	private static final int CONNECTION_TIMEOUT = 15000;
	private static final int SOCKET_TIMEOUT = 15000;

	public static String post(String url,
			Map<String,String> postParameters) {
		return post(url, postParameters, null);
	}
	
	public static String post(String url,
			Map<String, String> postParameters, Context context) {
		Log.d(TAG, "Post to url " + url + " with context.");
		HttpClient httpClient = getClient(url, context);
		try {
			HttpPost httpPost = new HttpPost(url);
			LinkedList<BasicNameValuePair> nameValuePairs = new LinkedList<BasicNameValuePair>();
			Iterator<Entry<String, String>> iterator = postParameters.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			String downloadedXml = EntityUtils
					.toString(responseEntity, "UTF-8");
			return downloadedXml;
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
	
	public static String post(String url, File uploadFile, Context context) {
		HttpClient httpClient = null;
		
		try {			
			httpClient = getClient(url, context);
			HttpPost httpPost = new HttpPost(url);
			
		    FileBody uploadFilePart = new FileBody(uploadFile);
		    MultipartEntity reqEntity = new MultipartEntity();
		    reqEntity.addPart("upload-file", uploadFilePart);
		    httpPost.setEntity(reqEntity);
		    HttpResponse response = httpClient.execute(httpPost); 
		    HttpEntity responseEntity = response.getEntity();
			String downloadedXml = EntityUtils.toString(responseEntity, "UTF-8");

			return downloadedXml;			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Something went wrong while http post",e);
			return null;
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().closeIdleConnections(
						CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
			}
		}
	}
	
	public static String get(String url) {
		return get(url, null);
	}
	
	public static String get(String url, Context context) {
		Log.d(TAG, "Get from url " + url);
		HttpClient httpClient = getClient(url, context);
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity ent = response.getEntity();
			return EntityUtils.toString(ent);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
		e.printStackTrace();
			return null;
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().closeIdleConnections(
						CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
			}
		}
	}
	
	private static HttpClient getClient(String url, Context context) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		if (context != null) {
			httpclient.setCookieStore(new PersistantCookieStore(context));
		}
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), SOCKET_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpclient.getParams(), SOCKET_TIMEOUT);
		return httpclient;
	}
}