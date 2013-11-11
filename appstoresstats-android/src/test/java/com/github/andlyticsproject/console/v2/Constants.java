package com.github.andlyticsproject.console.v2;



import org.apache.http.client.ResponseHandler;

import com.github.andlyticsproject.console.v2.HttpClientFactory;
import com.github.andlyticsproject.console.v2.SessionCredentials;
import com.github.andlyticsproject.model.DeveloperConsoleAccount;

public final class Constants {
    
	public static final String DEVELOPER_ACCOUNT_OK="testdev@mail.com";
	public static final String DEVELOPERID="123456";
	public static final String PACKAGE_NAME_OK="package.name.ok";
	public static final String PASSOK="pass1";
	public static final String XSRF="XLtPHQG1zVt2qgrU-hT62nSYou_8F4XhVZ:1300003600695";
	public static final String ACCOUNT_NAME="account";
	public static final String FETCH_APP_INFOS_URL="http://fetchInfos";
	public static final String FETCH_APP_INFOS_POST="fetch_infos_post";
	public static final String FETCH_APP_STATS_URL="http://fetchStats";
	public static final String FETCH_APP_STATS_POST="fetch_stats_post";
	public static DeveloperConsoleAccount accountOK=new DeveloperConsoleAccount(DEVELOPERID,DEVELOPER_ACCOUNT_OK);
	public static DeveloperConsoleAccount[] arrayAccountsOK={accountOK};
	public static final String APP_OK_NAME="Package OK";
	public static final String APP_OK_DESC="Package OK Description";
	public static final SessionCredentials CREDENTIALS_OK=new SessionCredentials(ACCOUNT_NAME,XSRF,arrayAccountsOK);
	public static final ResponseHandler<String> RESPONSE_HANDLER = HttpClientFactory.createResponseHandler();
	public static final String JSON_TEMPLATE_APPINFOS="{\"result\":{\"1\":[%s,%s,%s]},\"xsrf\":\"AHlVVVZ0XqB-JAl5sAr3VtOk4GFgVd8N1w:1300051700066\"}";
	public static final String PACKAGE1_APP="{\"1\":{\"1\":\"package.name.name1\",\"2\":{\"1\":[{\"1\":\"es-ES\",\"2\":\"Package 1\",\"3\":\"Descripción package1\"}],\"2\":{\"1\":0,\"2\":6,\"3\":1}},\"3\":{\"9\":{\"1\":\"0\",\"2\":\"USD\"}},\"4\":{\"1\":[{\"2\":{\"2\":\"package.name.name1\",\"3\":3,\"4\":\"1.3\",\"6\":{\"3\":\"https://lh5.pkg.com/123asdasdasdasd\"}},\"3\":1}]},\"6\":\"1380724576507\",\"7\":1},\"3\":{\"1\":\"12\",\"2\":\"1\",\"3\":5.0,\"4\":0,\"5\":\"21\"}}";
	public static final String PACKAGEOK_APP=String.format("{\"1\":{\"1\":\"%s\",\"2\":{\"1\":[{\"1\":\"es-ES\",\"2\":\"%s\",\"3\":\"%s\"}],\"2\":{\"1\":0,\"2\":6,\"3\":1}},\"3\":{\"9\":{\"1\":\"0\",\"2\":\"USD\"}},\"4\":{\"1\":[{\"2\":{\"2\":\"%s\",\"3\":4,\"4\":\"1.0.0\",\"6\":{\"3\":\"https://lh5.pkg.com/d658753-12312312my_nzWEboZCTsOhcV25565\"}},\"3\":1}]},\"6\":\"1371053633324\",\"7\":1},\"3\":{\"1\":\"12\",\"2\":\"1\",\"3\":5.0,\"4\":0,\"5\":\"70\"}}",PACKAGE_NAME_OK,APP_OK_NAME,APP_OK_DESC,PACKAGE_NAME_OK);
	public static final String PACKAGE3_APP="{\"1\":{\"1\":\"package.name.name3\",\"2\":{\"1\":[{\"1\":\"es-ES\",\"2\":\"Package 3\",\"3\":\"Descripción package3\"}],\"2\":{\"1\":0,\"2\":6,\"3\":1}},\"3\":{\"9\":{\"1\":\"0\",\"2\":\"USD\"}},\"4\":{\"1\":[{\"2\":{\"2\":\"package.name.name3\",\"3\":4,\"4\":\"1.0.0\",\"6\":{\"3\":\"https://lh4.pkg.com/fdsserseg_sdfrgSEfdfRhf543GFFSASDT6\"}},\"3\":1}]},\"6\":\"1360000289626\",\"7\":1},\"3\":{\"1\":\"40\",\"2\":\"3\",\"3\":4.3333335,\"4\":0,\"5\":\"113\"}}";
	public static final String APP_INFOS_JSON=String.format(JSON_TEMPLATE_APPINFOS,PACKAGE1_APP,PACKAGEOK_APP,PACKAGE3_APP);
	public static final String APP_STATS_JSON="{\"result\":{\"1\":{\"1\":{\"1\":[{\"1\":\"1371322800000\",\"2\":{\"1\":\"3\"}},{\"1\":\"1371409200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1371495600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1371582000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1371668400000\",\"2\":{\"1\":\"3\"}},{\"1\":\"1371754800000\",\"2\":{\"1\":\"4\"}},{\"1\":\"1371841200000\",\"2\":{\"1\":\"7\"}},{\"1\":\"1371927600000\",\"2\":{\"1\":\"11\"}},{\"1\":\"1372014000000\",\"2\":{\"1\":\"7\"}},{\"1\":\"1372100400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1372186800000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1372273200000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1372359600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1372446000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1372532400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1372618800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1372705200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1372791600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1372878000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1372964400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1373050800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1373137200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1373223600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1373310000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1373396400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1373482800000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1373569200000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1373655600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1373742000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1373828400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1373914800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1374001200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1374087600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1374174000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1374260400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1374346800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1374433200000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1374519600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1374606000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1374692400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1374778800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1374865200000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1374951600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1375038000000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1375124400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1375210800000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1375297200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1375383600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1375470000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1375556400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1375642800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1375729200000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1375815600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1375902000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1375988400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1376074800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1376161200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1376247600000\",\"2\":{\"1\":\"2\"}},{\"1\":\"1376334000000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1376420400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1376506800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1376593200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1376679600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1376766000000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1376852400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1376938800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1377025200000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1377111600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1377198000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1377284400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1377370800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1377457200000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1377543600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1377630000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1377716400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1377802800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1377889200000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1377975600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1378062000000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1378148400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1378234800000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1378321200000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1378407600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1378494000000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1378580400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1378666800000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1378753200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1378839600000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1378926000000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1379012400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1379098800000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1379185200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1379271600000\",\"2\":{\"1\":\"2\"}},{\"1\":\"1379358000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1379444400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1379530800000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1379617200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1379703600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1379790000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1379876400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1379962800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1380049200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1380135600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1380222000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1380308400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1380394800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1380481200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1380567600000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1380654000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1380740400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1380826800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1380913200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1380999600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1381086000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1381172400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1381258800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1381345200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1381431600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1381518000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1381604400000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1381690800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1381777200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1381863600000\",\"2\":{\"1\":\"1\"}},{\"1\":\"1381950000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382036400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382122800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382209200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382295600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382382000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382468400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382554800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382641200000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382727600000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382814000000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382900400000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1382986800000\",\"2\":{\"1\":\"0\"}},{\"1\":\"1383073200000\",\"2\":{\"1\":\"0\"}}]},\"5\":{\"2\":{\"1\":{},\"5\":\"1383159600000\",\"6\":6,\"7\":{\"1\":\"0\"},\"8\":false}},\"8\":\"Sunday Market Valladolid\"}},\"xsrf\":\"AMtNNDH1gKwZi-65LLhzCVXFV0QWm3oq5g:1383238311696\"}";
}
