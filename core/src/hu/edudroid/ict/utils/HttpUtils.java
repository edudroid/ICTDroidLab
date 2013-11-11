package hu.edudroid.ict.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

	private static final int CONNECTION_TIMEOUT = 30000;
	private static final int SOCKET_TIMEOUT = 30000;

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

	/**
	 * Simple get method to download the contents of an URL. 
	 * @param urlString The String representation of the requested URL. Can contain properly encoded parameters.
	 * @return The content of the requested URL.
	 */
	public static String get(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			return readStream(con.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String readStream(InputStream in) {
		BufferedReader reader = null;
		StringBuilder buffer = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
			return buffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}