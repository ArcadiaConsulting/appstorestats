package com.github.andlyticsproject.console.v2;

import static com.github.andlyticsproject.console.v2.Constants.FETCH_APP_INFOS_URL;
import static com.github.andlyticsproject.console.v2.Constants.FETCH_APP_STATS_URL;

import org.apache.http.client.methods.HttpPost;
import org.mockito.ArgumentMatcher;

public class CommonMatchers {
	  public static class HttpPostAppInfosArgumentMatcher extends ArgumentMatcher<HttpPost> {
	    	 
	         public boolean matches(Object o) {
	             if (o instanceof HttpPost&& o!=null) {
	                HttpPost post=(HttpPost)o;
	                if(post.getURI().toString().equals(FETCH_APP_INFOS_URL))
	                	return true;
	             }
	             return false;
	         }
	}
public static class HttpPostAppStatsArgumentMatcher extends ArgumentMatcher<HttpPost> {
 
 public boolean matches(Object o) {
     if (o instanceof HttpPost&& o!=null) {
        HttpPost post=(HttpPost)o;
        if(post.getURI().toString().equals(FETCH_APP_STATS_URL))
        	return true;
     }
     return false;
 }
}
}
