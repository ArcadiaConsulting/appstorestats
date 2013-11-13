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
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQItem;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQMetaData;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQSequence;
import javax.xml.xquery.XQSequenceType;
import javax.xml.xquery.XQStaticContext;

import net.sf.saxon.xqj.SaxonXQDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.syndication.io.SAXBuilder;

import es.arcadiaconsulting.appstoresstats.ios.model.Constants;
import junit.framework.TestCase;

public class HTTPClientHelperTest extends TestCase {
	
	
	
	public void testExecuteHttpGetWithParams(){
		
		String getResponse = HTTPClientHelper.executeHttpGetWithParams(Constants.HTTP_URL_APPLE_APP_GET, new String[]{Constants.ID_NAME,"662257575"});
		assertTrue(getResponse.contains("\"resultCount\":1"));
		getResponse = HTTPClientHelper.executeHttpGetWithParams("https://itunes.apple.com/search", new String[]{"term","jack+johnson","limit","25"});
		assertTrue(getResponse.contains("\"resultCount\":25"));
	}

	public void testExecuteHttp(){
		String getResponse = HTTPClientHelper.executeHttpGet("https://itunes.apple.com/es/rss/customerreviews/id=662257575/sortBy=mostRecent/xml");
		getResponse = getResponse.replaceAll("\n", "");
		getResponse = getResponse.replaceAll("\t", "");
		getResponse = getResponse.replaceAll("\r", "");
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(getResponse.getBytes()));
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/entry/id");
			NodeList entryList = doc.getDocumentElement().getElementsByTagName("entry");
			String response = (String)expr.evaluate(entryList.item(1),XPathConstants.STRING);
			
			String str = new String(getResponse.getBytes(), "UTF-8");
			java.lang.String prueba = "test";
					
		} catch (Exception e) {
			java.lang.String prueba = "test";
		}
	}
}
