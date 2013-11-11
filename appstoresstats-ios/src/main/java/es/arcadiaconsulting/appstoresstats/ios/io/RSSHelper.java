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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RSSHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(RSSHelper.class);
	
	
	public static List<SyndEntry> getRSSEntry(java.lang.String url){
		
		try{
        URL feedUrl = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));
        return (List<SyndEntry>) feed.getEntries();
//        for (SyndEntry entry : (List<SyndEntry>)feed.getEntries()) {
//            System.out.println(entry.getTitle());
//        }
		}catch(MalformedURLException e){
			logger.error("Malformed RSS URL", e);
			return null;
			
		} catch (IllegalArgumentException e) {
			logger.error("Getting RSS Exception", e);
			
			return null;
		} catch (FeedException e) {
			logger.error("Feed RSS Exception", e);
			return null;
		} catch (IOException e) {
			logger.error("Exception http", e);
			return null;
		}
	}

}
