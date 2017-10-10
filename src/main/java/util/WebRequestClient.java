package util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class WebRequestClient {
	private static final String PART_REQUEST_HEADER = "Accept:application/json, text/javascript, */*; q=0.01";
	private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String LOGIN_USERID_KEY = "session_user_id";
	private static final String LOGIN_USERTOKEN_KEY = "session_user_token";
	private static CookieStore cs = new BasicCookieStore();


	public static String testLinkGet(String url, String loginToken, String loginUserID) throws Exception {
		setCookie(loginToken, loginUserID);
		return opera(url, null, true);
	}

	public static String testLinkPost(String url, String loginToken, String loginUserID,
									  HttpEntity paras) throws Exception {
		setCookie(loginToken, loginUserID);
		return opera(url, paras, false);
	}
	
	public static String testPostWithCookie(String url, String cookieKey, String cookieValue,
									  HttpEntity paras) throws Exception {
		HttpContext localContext = new BasicHttpContext();
		// 在本地上下问中绑定一个本地存储
		localContext.setAttribute(HttpClientContext.COOKIE_STORE, cs);
		cs.addCookie(new BasicClientCookie(cookieKey, cookieValue));
		return opera(url, paras, false);
	}

	private static void setCookie(String loginToken, String loginUserID){
		// 创建一个本地上下文信息
		HttpContext localContext = new BasicHttpContext();
		// 在本地上下问中绑定一个本地存储
		localContext.setAttribute(HttpClientContext.COOKIE_STORE, cs);
		cs.addCookie(new BasicClientCookie(LOGIN_USERTOKEN_KEY, loginToken));
		cs.addCookie(new BasicClientCookie(LOGIN_USERID_KEY, loginUserID));
	}

	private static String opera(String url, HttpEntity paras, boolean isGet) throws Exception {
		System.out.println("----------------------------------------");
		System.out.println("----------------------------------------");

		String logPath = url;

		//DefaultHttpClient httpclient = new DefaultHttpClient(); @deprecated
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(20);
		CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

		String cookieStr = "";
		List<Cookie> list = cs.getCookies();
		for (Cookie cookie : list) {
			cookieStr += cookie.getName() + "=" + cookie.getValue() + ";";
		}

		CloseableHttpResponse response;
		if (isGet){
			response = getResponse(logPath, cookieStr, httpclient);
		} else {
			response = postResponse(logPath, cookieStr, paras, httpclient);
		}

//		response.setEntity(new UrlEncodedFormEntity(params));
		HttpEntity entity = response.getEntity();


		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
		String line = null;
		StringBuilder txt = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			txt.append(line);
		}
		return txt.toString();
	}

	private static CloseableHttpResponse getResponse(String logPath, String cookieStr, CloseableHttpClient httpclient) throws IOException {
		// 目标地址
		HttpGet httpget = new HttpGet(logPath);
		httpget.setHeader("Cookie", cookieStr);
		System.out.println("请求: " + httpget.getRequestLine());
		// 设置类型
		// 执行
		CloseableHttpResponse response = httpclient.execute(httpget);
		return response;
	}

	private static CloseableHttpResponse postResponse(String logPath, String cookieStr, HttpEntity paras,
											 CloseableHttpClient httpclient) throws IOException {
		HttpPost httpPost = new HttpPost(logPath);
		httpPost.setHeader("Cookie", cookieStr);
		if (paras != null) {
			System.out.print(paras + "\n");
			httpPost.setEntity(paras);
		}
		System.out.println("请求: " + httpPost.getRequestLine());
		// 设置类型
		// 执行
		CloseableHttpResponse response = httpclient.execute(httpPost);
		return response;
	}
}
