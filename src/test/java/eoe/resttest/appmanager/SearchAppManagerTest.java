package eoe.resttest.appmanager;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eoe.resttest.APIPathDecl;
import eoe.resttest.APITestData;
import eoe.resttest.APITestException;
import eoe.resttest.EoEAPITestUtils;
import eoe.resttest.common.CommonFunctions;
import eoe.resttest.EoEAPITestCase;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * 
 * 
 */
public class SearchAppManagerTest extends EoEAPITestCase{
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY;
	
	String searchKeywordAccountId = "";
	
	 public SearchAppManagerTest() {
		 super();
		 this.setAPIInfo(APIPathDecl.APIPATH_SEARCHAPPMANAGER, "GET");
	 }
	 
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * 
	 * */
	@Test
	public void testSearchAppManager_필수입력() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.queryParam("account", APITestData.TEST_APPMANAGERID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.get(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("createdBy"));
		assertNotNull(jsonResponse.get("id"));
		assertNotNull(jsonResponse.get("email"));
		assertNotNull(jsonResponse.get("loginCredential"));
//		assertNotNull(jsonResponse.get("companyName"));
		assertNotNull(jsonResponse.get("status"));
		assertNotNull(jsonResponse.get("profile"));
		assertNotNull(jsonResponse.get("createdBy"));
		assertNotNull(jsonResponse.get("createdBy"));
	}
	
}
