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

	private static final String LOGIN_PAGE_URL = "https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Faccounts.google.com%2FManageAccount&rip=1&nojavascript=1&service=androiddeveloper";
	private static final String AUTHENTICATE_URL = "https://accounts.google.com/signin/v1/lookup";
	private static final String FINALY_AUTHENTICATE_URL = "https://accounts.google.com/signin/challenge/sl/password";
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
			List<NameValuePair> postGetUserParams = new ArrayList<NameValuePair>();
			Document document = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Elements form = document.select("form");
			for (Element input : form.first().children()) {
				NameValuePair inputVal = null;
				if (input.attr("name").equals("Email")) {
					inputVal = new BasicNameValuePair(input.attr("name"), accountName);
				} else if (input.attr("name").equals("bgresponse")){
					inputVal = new BasicNameValuePair("bgresponse", "js_disabled");
				}else if(!input.attr("name").equals("")){
					inputVal = new BasicNameValuePair(input.attr("name"), input.attr("value"));
				}
				if(inputVal != null) {
					postGetUserParams.add(inputVal);
				}
			}
			NameValuePair inputVal = new BasicNameValuePair("Email", accountName);
			postGetUserParams.add(inputVal);

			HttpPost postGetUser = new HttpPost(AUTHENTICATE_URL);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postGetUserParams, "UTF-8");
			postGetUser.setEntity(formEntity);
			response = httpClient.execute(postGetUser);

			List<NameValuePair> postFinalAuthParams = new ArrayList<NameValuePair>();
			Document documentGetUser = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			Elements formGetUser = documentGetUser.select("form");
			for (Element input : formGetUser.first().children()) {
				NameValuePair inputVal2 = null;
				if (input.attr("name").equals("Email")) {
					inputVal2 = new BasicNameValuePair(input.attr("name"), accountName);
				} if (input.attr("name").equals("Passwd")) {
					inputVal2 = new BasicNameValuePair(input.attr("name"), password);
				} else if (input.attr("name").equals("bgresponse")){
					inputVal2 = new BasicNameValuePair("bgresponse", "js_disabled");
				}else if(!input.attr("name").equals("")){
					inputVal2 = new BasicNameValuePair(input.attr("name"), input.attr("value"));
				}
				if(inputVal2 != null) {
					postFinalAuthParams.add(inputVal2);
				}
			}
			NameValuePair inputVal3 = new BasicNameValuePair("Passwd", password);
			postFinalAuthParams.add(inputVal3);
			NameValuePair inputVal4 = new BasicNameValuePair("Email", accountName);
			postFinalAuthParams.add(inputVal4);

			HttpPost postFinalAuthen = new HttpPost(FINALY_AUTHENTICATE_URL);
			UrlEncodedFormEntity formEntityFinalAuth = new UrlEncodedFormEntity(postFinalAuthParams, "UTF-8");
			postFinalAuthen.setEntity(formEntityFinalAuth);
			response = httpClient.execute(postFinalAuthen);

			CookieStore cookieStore = httpClient.getCookieStore();


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

	private List<NameValuePair> createAuthParameters(String galxValue, String gxfValue, String gapsValue) {

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
		NameValuePair gaps = new BasicNameValuePair("GAPS", gapsValue);
		result.add(gaps);
		NameValuePair bgVal = new BasicNameValuePair("bgresponse", "js_disabled");
		result.add(bgVal);
		NameValuePair service = new BasicNameValuePair("service", "androiddeveloper");
		result.add(service);
		NameValuePair rip = new BasicNameValuePair("rip", "1");
		result.add(rip);
		NameValuePair pst = new BasicNameValuePair("pstMsg", "0");
		result.add(pst);

		return result;
	}
}