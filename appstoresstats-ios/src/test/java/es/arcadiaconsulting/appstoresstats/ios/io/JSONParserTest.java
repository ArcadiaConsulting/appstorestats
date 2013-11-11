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

import java.text.SimpleDateFormat;

import es.arcadiaconsulting.appstoresstats.ios.model.AppInfo;
import junit.framework.TestCase;

public class JSONParserTest extends TestCase {
	
	public void testGetAPPInfoByID(){
		
		AppInfo appInfo = JSONParser.getAPPInfoByID("662257575");
		assertEquals("662257575",appInfo.getId());
		assertEquals("Fiestas Sampedros Burgos",appInfo.getAppName());
		assertTrue(appInfo.isDeployed());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String s = formatter.format(appInfo.getReleaseDate());
		assertEquals("2013-06-21T08:32:43Z",s);
		
		appInfo = JSONParser.getAPPInfoByID("548280988");
		assertEquals("548280988", appInfo.getId());
		assertFalse(appInfo.isDeployed());
		
		
		
	}

}
