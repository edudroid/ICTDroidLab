package hu.edudroid.ict.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
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
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtils {

	private static final int CONNECTION_TIMEOUT = 15000;
	private static final int SOCKET_TIMEOUT = 15000;

	public static String postMultipartWithFile(String url,
			LinkedList<BasicNameValuePair> postParameters, File pictureFile) {
		HttpClient httpClient = null;
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(parameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(parameters, SOCKET_TIMEOUT);
			httpClient = new DefaultHttpClient(parameters);
			HttpPost httpPost = new HttpPost(url);
			MultipartEntity entity = new MultipartEntity();
			for (BasicNameValuePair pair : postParameters) {
				FormBodyPart formBody = new FormBodyPart(pair.getName(),
						new StringBody(pair.getValue(),
								Charset.forName("UTF-8")));
				entity.addPart(formBody);
			}
			if (pictureFile != null) {
				entity.addPart("file", new FileBody(pictureFile));
			}

			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();

			String responseString = EntityUtils.toString(responseEntity,
					"UTF-8");
			return responseString;
			// return null;
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

	public static String post(String url,
			LinkedList<BasicNameValuePair> postParameters) {
		HttpClient httpClient = null;
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(parameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(parameters, SOCKET_TIMEOUT);
			httpClient = new DefaultHttpClient(parameters);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
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

	public static HttpResponse postWithHeader(String url,
			LinkedList<BasicNameValuePair> postParameters) {
		HttpClient httpClient = null;
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(parameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(parameters, SOCKET_TIMEOUT);
			httpClient = new DefaultHttpClient(parameters);
			HttpPost httpPost = new HttpPost(url);

			httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

			HttpResponse response = httpClient.execute(httpPost);

			return response;
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

	public static String post(String url,
			LinkedList<BasicNameValuePair> postParameters, String loginCookie,
			String passportCookie) {
		Log.d("HTTP POST", "To url " + url);
		HttpClient httpClient = null;
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(parameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(parameters, SOCKET_TIMEOUT);
			httpClient = new DefaultHttpClient(parameters);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			httpPost.addHeader("Cookie", "login=" + loginCookie + ";"
					+ "passport=" + passportCookie);
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

	public static String postMultipartWithFile(String url,
			LinkedList<BasicNameValuePair> postParameters,
			String fileFieldName, File file, String loginCookie,
			String passportCookie) {
		HttpClient httpClient = null;
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(parameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(parameters, SOCKET_TIMEOUT);
			httpClient = new DefaultHttpClient(parameters);
			HttpPost httpPost = new HttpPost(url);
			MultipartEntity entity = new MultipartEntity();
			for (BasicNameValuePair pair : postParameters) {
				FormBodyPart formBody = new FormBodyPart(pair.getName(),
						new StringBody(pair.getValue(),
								Charset.forName("UTF-8")));
				entity.addPart(formBody);
			}
			if (file != null) {
				entity.addPart(fileFieldName, new FileBody(file));
			}
			if (loginCookie != null && passportCookie != null) {
				httpPost.addHeader("Cookie", "login=" + loginCookie + ";"
						+ "passport=" + passportCookie);
			}
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity responseEntity = response.getEntity();

			String downloadedXml = EntityUtils
					.toString(responseEntity, "UTF-8");
			return downloadedXml;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
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
	
	public static String post(String url, Map<String, String> params)
			throws IOException {
		return post(url, params, null, null);
	}

	public static String post(String url,
			Map<String, String> postParameters, String loginCookie,
			String passportCookie) {
		Log.d("HTTP POST", "To url " + url);
		HttpClient httpClient = null;
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(parameters,
					CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(parameters, SOCKET_TIMEOUT);
			httpClient = new DefaultHttpClient(parameters);
			HttpPost httpPost = new HttpPost(url);
			LinkedList<BasicNameValuePair> nameValuePairs = new LinkedList<BasicNameValuePair>();
			Iterator<Entry<String, String>> iterator = postParameters.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			}

			httpPost.addHeader("Cookie", "login=" + loginCookie + ";"
					+ "passport=" + passportCookie);
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
	
	public static String get(String endpoint) {
		return get(endpoint, null, null);
	}
	
	public static String get(String endpoint, String loginCookie,
			String passportCookie) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), SOCKET_TIMEOUT);
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