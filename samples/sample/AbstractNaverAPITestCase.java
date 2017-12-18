package eoe.resttest.sample;

import eoe.resttest.AbstractRestAPITestCase;
import io.restassured.RestAssured;

public class AbstractNaverAPITestCase extends AbstractRestAPITestCase {
	// for Authentication
	protected final static String CLIENT_ID = "IsnoU2X6hUikeU5CsoWR";
	protected final static String CLIENT_SECRET = "HDwHSfBgGG";
	
	
	public AbstractNaverAPITestCase(){
		String baseURI = "";
		switch(getTestMode()){
		case AbstractRestAPITestCase.TESTMODE_LOCAL : 
			baseURI = "https://openapi.naver.com/v1";
			break;
		case AbstractRestAPITestCase.TESTMODE_DEVSERVER : 
			baseURI = "https://openapi.naver.com/v1";
			break;
		case AbstractRestAPITestCase.TESTMODE_TESTSERVER : 
			baseURI = "https://openapi.naver.com/v1";
			break;
		case AbstractRestAPITestCase.TESTMODE_PRODSERVER : 
			baseURI = "https://openapi.naver.com/v1";
			break;
		default : 
			baseURI = "https://openapi.naver.com/v1";
			break;
		}
		
		RestAssured.baseURI = baseURI;
	}
}
