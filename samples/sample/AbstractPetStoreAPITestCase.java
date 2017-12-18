package eoe.resttest.sample;

import eoe.resttest.AbstractRestAPITestCase;
import io.restassured.RestAssured;

public class AbstractPetStoreAPITestCase extends AbstractRestAPITestCase {
	
	public AbstractPetStoreAPITestCase(){
		String baseURI = "";
		switch(getTestMode()){
		case AbstractRestAPITestCase.TESTMODE_LOCAL : 
			baseURI = "http://petstore.swagger.io/v2";
			break;
		case AbstractRestAPITestCase.TESTMODE_DEVSERVER : 
			baseURI = "http://petstore.swagger.io/v2";
			break;
		case AbstractRestAPITestCase.TESTMODE_TESTSERVER : 
			baseURI = "http://petstore.swagger.io/v2";
			break;
		case AbstractRestAPITestCase.TESTMODE_PRODSERVER : 
			baseURI = "http://petstore.swagger.io/v2";
			break;
		default : 
			baseURI = "http://petstore.swagger.io/v2";
			break;
		}
		
		RestAssured.baseURI = baseURI;
	}
}
