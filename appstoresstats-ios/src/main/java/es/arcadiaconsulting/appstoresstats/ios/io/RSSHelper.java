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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import es.arcadiaconsulting.appstoresstats.common.CommonStatsData;
import es.arcadiaconsulting.appstoresstats.common.Rating;
import es.arcadiaconsulting.appstoresstats.ios.model.Constants;

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
	
	public static String getItunesURL(String appleid){
		String getResponse = HTTPClientHelper.executeHttpGet(MessageFormat.format(Constants.HTTP_RSS_APP_INFO, new String[]{appleid}));
		getResponse = getResponse.replaceAll("\n", "");
		getResponse = getResponse.replaceAll("\t", "");
		getResponse = getResponse.replaceAll("\r", "");
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(getResponse.getBytes()));
			NodeList entryList = doc.getDocumentElement().getElementsByTagName("entry");
			if(entryList==null || entryList.getLength()<=0){
				logger.info("getItunesUrl - There are not rating comments we couldnt get the itunes url");
				return null;
			}else{
				return entryList.item(0).getChildNodes().item(1).getTextContent();
				
			}
			
			
					
		} catch (Exception e) {
			logger.error("getItunesUrl - Problem getting rss document");
			return null;
		}
		
	}
	
	public static List<Rating> getItunesRating(String appleId){
		String getResponse = HTTPClientHelper.executeHttpGet(MessageFormat.format(Constants.HTTP_RSS_APP_INFO, new String[]{appleId}));
		getResponse = getResponse.replaceAll("\n", "");
		getResponse = getResponse.replaceAll("\t", "");
		getResponse = getResponse.replaceAll("\r", "");
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(getResponse.getBytes()));
			NodeList entryList = doc.getDocumentElement().getElementsByTagName("entry");
			if(entryList==null || entryList.getLength()<=0){
				logger.info("getItunesRating - There are not rating comments we couldnt get the itunes url");
				return null;
			}else{
				List<Rating> ratingList = new Vector<Rating>();
				for (int i = 1; i<entryList.getLength();i++) {
					Rating rating = new Rating();
					NodeList entryChilds = entryList.item(i).getChildNodes();
					for (int j=0;j<entryChilds.getLength();j++){
						if(entryChilds.item(j).getNodeName().equals("title")){
							rating.setOpinion(entryChilds.item(j).getTextContent());
						}else if(entryChilds.item(j).getNodeName().contains("rating")){
							rating.setRate(Integer.valueOf(entryChilds.item(j).getTextContent()).intValue());
						}else if(entryChilds.item(j).getNodeName().equals("updated")){
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
							rating.setDate(format .parse(entryChilds.item(j).getTextContent()));
						}else if(entryChilds.item(j).getNodeName().contains("version")){
							rating.setAppVersion(entryChilds.item(j).getTextContent());
						}
					}
					ratingList.add(rating);
					
				}
				return ratingList;
				
			}
			
			
					
		} catch (Exception e) {
			logger.error("getItunesRating - Problem getting rss document");
			return null;
		}
	}
	
	

}
