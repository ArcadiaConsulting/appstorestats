/**
* Copyright 2013 Arcadia Consulting C.B.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*     http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/
package es.arcadiaconsulting.appstoresstats.ios.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPClientHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(HTTPClientHelper.class);

	public static String executeHttpGetWithParams(String url,
			String[] nameValueParam) {
		String finalURL = "";
		if (nameValueParam.length > 1) {
			finalURL = url + "?";
			for (int i = 0; i < nameValueParam.length; i++) {
				finalURL = finalURL + nameValueParam[i]+ "=" + nameValueParam[i+1];
				i=i+1;
				if(i<nameValueParam.length-1)
						finalURL = finalURL+"&";
			}
		}
		return executeHttpGet(finalURL);
	}

	public static String executeHttpGet(String url) {
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			return page;
		} catch (Exception e) {
			logger.error("http petition error");
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("Problem closing file");
				}
			}
		}
	}

}
