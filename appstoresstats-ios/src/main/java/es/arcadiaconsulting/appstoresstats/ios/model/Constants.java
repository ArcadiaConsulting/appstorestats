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
package es.arcadiaconsulting.appstoresstats.ios.model;

public class Constants {
	
	public static final java.lang.String REPORT_SUBTYPE_SUMMARY_NAME= "Summary";
	public static final java.lang.String DATE_TYPE_DAILY="Daily";
	public static final java.lang.String DATE_TYPE_WEEDLY = "Weekly";
	public static final java.lang.String DATE_TYPE_MONTHLY = "Monthly";
	public static final java.lang.String DATE_TYPE_YEARLY = "Yearly";
	public static final java.lang.String REPORT_TYPE_SALES="Sales";
	
	public static final java.lang.String DATE_FORMAT ="yyyyMMdd";
	
	public static final java.lang.String HTTP_URL_APPLE_APP_GET ="https://itunes.apple.com/lookup";
	
	public static final java.lang.String ID_NAME ="id";
	
	public static final java.lang.String[] UPDATE_PRODUCT_TYPE_IDENTIFYER = new String[]{"7", "7F", "7T", "F7"};
	
	public static final java.lang.String HTTP_RSS_APP_INFO = "https://itunes.apple.com/es/rss/customerreviews/id={0}/sortBy=mostRecent/xml";
	
	public static final java.lang.String NOT_PUBLISHED_ERROR="There are no reports available to download for this selection";
	public static final java.lang.String NOT_DAILY_STATS_ERROR="Daily reports are available only for past";
	public static final java.lang.String NOT_WEEKLY_STATS_ERROR="Weekly reports are available only for past";
}
