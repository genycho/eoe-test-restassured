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
public class GetAppManagerTest extends EoEAPITestCase{
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY;
	
	 public GetAppManagerTest() {
		 super();
		 this.setAPIInfo("/api/tenants/search/by-account", "GET");
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
	public void testGetAppManager_필수입력() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.queryParam("account", testAppManagerId);
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
		assertNotNull(jsonResponse.get("companyName"));
		assertNotNull(jsonResponse.get("status"));
		assertNotNull(jsonResponse.get("profile"));
		assertNotNull(jsonResponse.get("createdBy"));
		assertNotNull(jsonResponse.get("createdBy"));
	}
	
	@Test
	public void testGetAppManager_필수입력누락() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
//		requestSpec.queryParam("account", testAppManagerId);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(400).log().all()
		.when()
			.get(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertEquals("Bad Request",  jsonResponse.get("error"));
		assertEquals("org.springframework.web.bind.MissingServletRequestParameterException",  jsonResponse.get("exception"));
		assertEquals("Required String parameter 'account' is not present",  jsonResponse.get("message"));
	}
	
	@Test
	public void testGetAppManager_AccessToken누락() throws Exception {
//		String accessToken = new LoginAsAppManagerTest().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec("","");
		requestSpec.queryParam("account", testAppManagerId);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(401).log().all()
		.when()
			.get(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertEquals("invalid_token",  jsonResponse.get("error"));
		assertEquals("Invalid access token: ",  jsonResponse.get("error_description"));
	}
	
	@Test
	public void testGetAppManagerList_기본() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.queryParam("account", testAppManagerId);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.get("/api/tenants").andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("createdBy"));
	}
}
