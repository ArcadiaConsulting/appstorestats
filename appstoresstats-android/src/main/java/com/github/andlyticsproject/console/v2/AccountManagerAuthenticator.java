package com.github.andlyticsproject.console.v2;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.github.andlyticsproject.console.AuthenticationException;
import com.github.andlyticsproject.console.NetworkException;
import com.github.andlyticsproject.model.DeveloperConsoleAccount;

public class AccountManagerAuthenticator extends BaseAuthenticator {

	private static final boolean DEBUG = false;
    
	// includes one-time token
        private String loginUrl2 ="https://accounts.google.com/ServiceLogin";
        
	private DefaultHttpClient httpClient;

	public AccountManagerAuthenticator(String accountName, DefaultHttpClient httpClient) {
		super(accountName);
		this.httpClient = httpClient;
	}

        // as described here: http://www.slideshare.net/pickerweng/chromium-os-login
	// http://www.chromium.org/chromium-os/chromiumos-design-docs/login
	// and implemented by the Android Browser:
	// packages/apps/Browser/src/com/android/browser/GoogleAccountLogin.java
	// packages/apps/Browser/src/com/android/browser/DeviceAccountLogin.java

	@Override
	public SessionCredentials authenticate(boolean invalidate)
			throws AuthenticationException {
		return authenticateSilently(invalidate);
	}

        @Override
	public SessionCredentials authenticateSilently(boolean invalidate)
                throws AuthenticationException {
            try {
                return authenticateInternal(invalidate);
            } catch (URISyntaxException ex) {
                Logger.getLogger(AccountManagerAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
	}

	@SuppressWarnings ("deprecation")
	private SessionCredentials authenticateInternal(boolean invalidate)
		throws AuthenticationException, URISyntaxException {
            try {

                StringBuilder content = new StringBuilder();
                content.append("Email=").append(URLEncoder.encode("youraccount", "UTF-8"));
                content.append("&Passwd=").append(URLEncoder.encode("yourpassword", "UTF-8"));
                content.append("&service=").append(URLEncoder.encode("apps", "UTF-8"));
   
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://www.google.com/accounts/ClientLogin");
                post.addHeader("Content-Type", "application/x-www-form-urlencoded");

                post.setEntity(new StringEntity(content.toString(), null, null));

                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                String strResponse = EntityUtils.toString(entity);

                String sid = null;
                String lsid = null;
                                
                for(String a : strResponse.split("\\s")){
                    if(a.startsWith("SID=")){
                        sid = a.substring(4);
                    }else if (a.startsWith("LSID=")){
                        lsid = a.substring(5);
                    }
                }
                              
                String AUTH_TOKEN_URL = new StringBuilder("https://www.google.com/accounts/IssueAuthToken?")
                    .append("SID=")
                    .append(sid)
                    .append("&LSID=")
                    .append(lsid)
                    .append("&service=gaia").toString();
                
		HttpGet issueAuth= new HttpGet(AUTH_TOKEN_URL);
		HttpResponse issueAuthResponse = httpClient.execute(issueAuth);
		String auth = EntityUtils.toString(issueAuthResponse.getEntity()).trim();
		//System.out.println("Auth_Token: " + auth);
                System.out.println("Correct authentication");
                
                String TOKEN_AUTH_URL = new StringBuilder ("https://www.google.com/accounts/TokenAuth?")
                    .append("auth=")
                    .append(auth)
                    .append("&service=apps&session=false")
                    .append("&continue=")
                    .append("https://play.google.com/apps/publish/v2/").toString();
                    
                HttpGet getCookies = new HttpGet(TOKEN_AUTH_URL);
                HttpResponse response2 = httpClient.execute(getCookies);
                
                // fail if not found, otherwise get page content
                String responseStr = EntityUtils.toString(response2.getEntity(), "UTF-8");

            int status = response2.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_UNAUTHORIZED) {
                throw new AuthenticationException("Authentication token expired: " + response2.getStatusLine());
            }
            if (status != HttpStatus.SC_OK) {
                throw new AuthenticationException("Authentication error: " + response2.getStatusLine());
            }
            HttpEntity entity2 = response2.getEntity();
            if (entity2 == null) {
                throw new AuthenticationException("Authentication error: null result?");
            }

            if (DEBUG) {
                System.out.println("Response: " + responseStr);
            }

            DeveloperConsoleAccount[] developerAccounts = findDeveloperAccounts(responseStr);
            
            if (developerAccounts == null) {
                debugAuthFailure(responseStr);
                throw new AuthenticationException("Couldn't get developer account ID.");
            }

            String xsrfToken = findXsrfToken(responseStr);
            if (xsrfToken == null) {
                debugAuthFailure(responseStr);

                throw new AuthenticationException("Couldn't get XSRF token.");
            }

            List<String> whitelistedFeatures = findWhitelistedFeatures(responseStr);

            SessionCredentials result = new SessionCredentials(accountName, xsrfToken,developerAccounts);
            result.addCookies(httpClient.getCookieStore().getCookies());
            result.addWhitelistedFeatures(whitelistedFeatures);

            return result;
        } catch (IOException e) {
            throw new NetworkException(e);
        }
    }
        
	private void debugAuthFailure(String responseStr) {
        System.out.println("-------------------");
        System.out.println("debugAuthFailure on AccountManagerAuthenticator");
        System.out.println(responseStr);
        System.out.println("-------------------");
		openAuthUrlInBrowser();
	}

	private void openAuthUrlInBrowser() {
                if (loginUrl2 == null){
			System.out.println("Null webloginUrl?");
			return;
		}
		System.out.println("Opening login URL in browser: " + loginUrl2);
	}

}
