package es.arcadiaconsulting.appstoresstats.ios.io;

import java.util.List;

import es.arcadiaconsulting.appstoresstats.common.Rating;
import junit.framework.TestCase;

public class RSSHelperTest extends TestCase{

	public void testGetItunesURL(){
		assertEquals("https://itunes.apple.com/es/app/iredes-2016/id604031647?mt=8&uo=2",RSSHelper.getItunesURL("604031647"));
		assertNull(RSSHelper.getItunesURL("invalid"));
	}
	
	public void testGetItunesRating(){
		List<Rating> rating = RSSHelper.getItunesRating("604031647");
		assertEquals("1.2.0",((Rating)rating.get(0)).getAppVersion());
		assertEquals("Genial app",((Rating)rating.get(0)).getOpinion());
		assertEquals(5,((Rating)rating.get(0)).getRate());
		
	}
	
}
