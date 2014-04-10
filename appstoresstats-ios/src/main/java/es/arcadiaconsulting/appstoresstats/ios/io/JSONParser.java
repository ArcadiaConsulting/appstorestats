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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.arcadiaconsulting.appstoresstats.ios.model.AppInfo;
import es.arcadiaconsulting.appstoresstats.ios.model.Constants;

public class JSONParser {
	
	private static final Logger logger = LoggerFactory.getLogger(JSONParser.class);
	
	public static AppInfo getAPPInfoByID(String appleID){
		AppInfo appInfo = new AppInfo();
		try {
			String httpResponse = HTTPClientHelper.executeHttpGetWithParams(Constants.HTTP_URL_APPLE_APP_GET, new String[]{Constants.ID_NAME,appleID});
		
			JSONObject jsonObject = new JSONObject(httpResponse);
			int resultCount = jsonObject.getInt("resultCount");
			if(resultCount==0){
				appInfo.setDeployed(false);
				appInfo.setId(appleID);
				return appInfo;
			}else if(resultCount > 1){
				logger.error("There are more than one app with same id");
				return null;
			}else{
				JSONArray result = jsonObject.getJSONArray("results");
				String releaseDate = result.getJSONObject(0).getString("releaseDate");
				String trakName = result.getJSONObject(0).getString("trackName");
				appInfo.setDeployed(true);
				appInfo.setId(appleID);
				appInfo.setAppName(trakName);
				//TODO get more parameters
				appInfo.setReleaseDate(DateHelper.buildDateFromUTCString(releaseDate));
				return appInfo;
				
				
			}
			
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception parsing");
        }
		
		
		return appInfo;
	}
	

}
