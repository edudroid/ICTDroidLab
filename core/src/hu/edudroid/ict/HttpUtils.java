package hu.edudroid.ict;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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

public class HttpUtils {

	private static final int	CONNECTION_TIMEOUT	= 30000;
	private static final int	SOCKET_TIMEOUT		= 30000;

	public static String postMultipartWithFile(	String url,
												LinkedList<BasicNameValuePair> postParameters,
												File pictureFile){
		HttpClient httpClient = null;
		try{
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(	parameters,
														CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(parameters, SOCKET_TIMEOUT);
			httpClient = new DefaultHttpClient(parameters);
			HttpPost httpPost = new HttpPost(url);
			MultipartEntity entity = new MultipartEntity();
			for (BasicNameValuePair pair : postParameters){
				FormBodyPart formBody = new FormBodyPart(	pair.getName(),
															new StringBody(	pair.getValue(),
																			Charset.forName("UTF-8")));
				entity.addPart(formBody);
			}
			if (pictureFile != null){
				entity.addPart("file", new FileBody(pictureFile));
			}

			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();

			String responseString = EntityUtils.toString(responseEntity, "UTF-8");
			return responseString;
			// return null;
		}
		catch (UnsupportedEncodingException e){
			return null;
		}
		catch (MalformedURLException e){
			return null;
		}
		catch (IOException e){
			return null;
		}
		finally{
			if (httpClient != null){
				httpClient.getConnectionManager()
							.closeIdleConnections(	CONNECTION_TIMEOUT,
													TimeUnit.MILLISECONDS);
			}
		}
	}
	
	public static String post(	String url,
								LinkedList<BasicNameValuePair> postParameters){
		HttpClient httpClient = null;
		try{
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(	parameters,
														CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(parameters, SOCKET_TIMEOUT);
			httpClient = new DefaultHttpClient(parameters);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			String downloadedXml = EntityUtils.toString(responseEntity, "UTF-8");
			return downloadedXml;
		}
		catch (UnsupportedEncodingException e){
			return null;
		}
		catch (MalformedURLException e){
			return null;
		}
		catch (IOException e){
			return null;
		}
		finally{
			if (httpClient != null){
				httpClient.getConnectionManager()
							.closeIdleConnections(	CONNECTION_TIMEOUT,
													TimeUnit.MILLISECONDS);
			}
		}
	}

}
