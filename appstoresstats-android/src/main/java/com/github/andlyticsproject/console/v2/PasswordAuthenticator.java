package com.github.andlyticsproject.console.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.andlyticsproject.console.AuthenticationException;
import com.github.andlyticsproject.model.DeveloperConsoleAccount;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PasswordAuthenticator extends BaseAuthenticator {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PasswordAuthenticator.class);

	private static final String TAG = PasswordAuthenticator.class.getSimpleName();

	private static final boolean DEBUG = false;

	private static final String LOGIN_PAGE_URL = "https://accounts.google.com/ServiceLogin?service=androiddeveloper";
	private static final String AUTHENTICATE_URL = "https://accounts.google.com/ServiceLoginAuth?service=androiddeveloper";
	private static final String DEV_CONSOLE_URL = "https://play.google.com/apps/publish/";

	private DefaultHttpClient httpClient;
	private String password;

	public PasswordAuthenticator(String accountName, String password, DefaultHttpClient httpClient) {
		super(accountName);
		this.httpClient = httpClient;
		this.password = password;
	}

	// 1. Get GALX from https://accounts.google.com/ServiceLogin
	// 2. Post along with auth info to
	// https://accounts.google.com/ServiceLoginAuth
	// 3. Get redirected to https://play.google.com/apps/publish/v2/ on success
	// (all needed cookies are in HttpClient's cookie jar at this point)

	@Override
	public SessionCredentials authenticate(boolean invalidate)
			throws AuthenticationException {
		return authenticate();
	}

	@Override
	public SessionCredentials authenticateSilently(boolean invalidate)
			throws AuthenticationException {
		return authenticate();
	}

	private SessionCredentials authenticate() throws AuthenticationException {
		try {
			HttpGet get = new HttpGet(LOGIN_PAGE_URL);
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new AuthenticationException("Auth error: " + response.getStatusLine());
			}

			String galxValue = null;
			CookieStore cookieStore = httpClient.getCookieStore();
			List<Cookie> cookies = cookieStore.getCookies();
			for (Cookie c : cookies) {
				if ("GALX".equals(c.getName())) {
					galxValue = c.getValue();
				}
			}

			//GET GXF
			String gxf = "";
			Document document = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Elements form = document.select("form");
			for (Element input : form.first().children()) {
				if(input.attr("name").equals("gxf")){
					gxf = input.attr("value");
					break;
				}
			}
			if (DEBUG) {
				if (logger.isDebugEnabled()) {
					logger.debug("authenticate() - {}", "GALX: " + galxValue); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			HttpPost post = new HttpPost(AUTHENTICATE_URL);
			List<NameValuePair> parameters = createAuthParameters(galxValue, gxf);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
			post.setEntity(formEntity);

			response = httpClient.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result2 = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result2.append(line);
			}
			//get info
			HttpGet getDev = new HttpGet(DEV_CONSOLE_URL);
			response = httpClient.execute(getDev);


			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new AuthenticationException("Auth error: " + response.getStatusLine());
			}

			String responseStr = EntityUtils.toString(response.getEntity());


			if (DEBUG) {
				if (logger.isDebugEnabled()) {
					logger.debug("authenticate() - {}", "Response: " + responseStr); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			DeveloperConsoleAccount[] developerAccounts = findDeveloperAccounts(responseStr);
			if (developerAccounts == null) {
				throw new AuthenticationException("Couldn't get developer account ID.");
			}

			String xsrfToken = findXsrfToken(responseStr);
			if (xsrfToken == null) {
				throw new AuthenticationException("Couldn't get XSRF token.");
			}

			List<String> whitelistedFeatures = findWhitelistedFeatures(responseStr);

			SessionCredentials result = new SessionCredentials(accountName, xsrfToken,
					developerAccounts);
			result.addCookies(cookieStore.getCookies());
			result.addWhitelistedFeatures(whitelistedFeatures);

			return result;
		} catch (ClientProtocolException e) {
			throw new AuthenticationException(e);
		} catch (IOException e) {
			throw new AuthenticationException(e);
		}
	}

	private List<NameValuePair> createAuthParameters(String galxValue, String gxfValue) {
		List<NameValuePair> result = new ArrayList<NameValuePair>();
		NameValuePair email = new BasicNameValuePair("Email", accountName);
		result.add(email);
		NameValuePair passwd = new BasicNameValuePair("Passwd", password);
		result.add(passwd);
		NameValuePair galx = new BasicNameValuePair("GALX", galxValue);
		result.add(galx);
		NameValuePair cont = new BasicNameValuePair("continue", DEV_CONSOLE_URL);
		result.add(cont);
		NameValuePair gxf = new BasicNameValuePair("gxf", gxfValue);
		result.add(gxf);
		NameValuePair bgVal = new BasicNameValuePair("bgresponse", "js_disabled");
		result.add(bgVal);
		return result;
	}
}