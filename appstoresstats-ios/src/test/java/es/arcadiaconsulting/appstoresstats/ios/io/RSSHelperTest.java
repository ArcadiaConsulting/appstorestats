package es.arcadiaconsulting.appstoresstats.ios.io;

import java.util.List;

import es.arcadiaconsulting.appstoresstats.common.Rating;
import junit.framework.TestCase;

public class RSSHelperTest extends TestCase{

	public void testGetItunesURL(){
		assertEquals("https://itunes.apple.com/es/app/fiestas-sampedros-burgos/id662257575?mt=8&uo=2",RSSHelper.getItunesURL("662257575"));
		assertNull(RSSHelper.getItunesURL("731374663"));
	}
	
	public void testGetItunesRating(){
		List<Rating> rating = RSSHelper.getItunesRating("662257575");
		assertEquals("1.2.3",((Rating)rating.get(0)).getAppVersion());
		assertEquals("Muy útil",((Rating)rating.get(0)).getOpinion());
		assertEquals(5,((Rating)rating.get(0)).getRate());
		
		assertEquals(5,rating.size());
		assertNull(RSSHelper.getItunesRating("731374663"));
	}
	
}
