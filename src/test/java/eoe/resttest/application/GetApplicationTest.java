package eoe.resttest.application;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eoe.resttest.APIPathDecl;
import eoe.resttest.APITestData;
import eoe.resttest.EoEAPITestCase;
import eoe.resttest.TestUtils;
import eoe.resttest.appmanager.LoginAsAppManagerTest;
import eoe.resttest.common.CommonFunctions;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * 
 */
public class GetApplicationTest extends EoEAPITestCase{
	String testApplicatonName = "ra application name for GetMyApplication";
	String appuuid = null;
	
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY;
	
	 public GetApplicationTest() {
		 super();
		 this.setAPIInfo(APIPathDecl.APIPATH_GETAPPLICATIONLIST, "GET");
	 }
	 
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		try {
			CommonFunctions.getInstance().deleteApplication(testAppManagerId,testApiKey , appuuid);
		}catch(Exception forceTearDown) {
			//don't care
		}
	}
	
	/**
	 * 
	 * */
	@Test
	public void testGetMyApplications_목록조회() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId,
				testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
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
		assertNotNull(jsonResponse.get("content"));
		assertNotNull(jsonResponse.get("totalPages"));
		assertNotNull(jsonResponse.get("totalElements"));
		assertNotNull(jsonResponse.get("last"));
		assertNotNull(jsonResponse.get("numberOfElements"));
		/**	Cancel @jiraid[DEPPJT-287]*/
//		assertNotNull(jsonResponse.get("sort"));
		assertNotNull(jsonResponse.get("size"));
		assertNotNull(jsonResponse.get("number"));
		assertNotNull(jsonResponse.get("first"));
	}
	
	/**
	 * 
	 * */
	@Test
	public void testGetMyApplications_accessToken누락() throws Exception {
		RequestSpecification requestSpec = getDefaultRequestSpec("");
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
		assertNotNull(jsonResponse.get("error"));
		assertEquals("unauthorized", jsonResponse.get("error"));
		assertNotNull(jsonResponse.get("error_description"));
		assertEquals("Full authentication is required to access this resource", jsonResponse.get("error_description"));
	}
	
	@Test
	public void testGetMyApplications_어플리케이션ID로가져오기() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId,
				APITestData.TEST_APIKEY);
		appuuid = CommonFunctions.getInstance().createApplicationId(testAppManagerId, testApiKey, testApplicatonName+TestUtils.getUniqueString());
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id",appuuid );
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.get("/api/applications/own/{id}").andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("createdBy"));
		assertNotNull(jsonResponse.get("id"));
		assertNotNull(jsonResponse.get("managerId"));
		assertEquals(APITestData.TEST_APPMANAGERID, jsonResponse.get("managerId"));
		assertNotNull(jsonResponse.get("apiKey"));
		assertNotNull(jsonResponse.get("name"));
//		assertEquals(this.testApplicatonName, jsonResponse.get("name"));		
		assertNotNull(jsonResponse.get("status"));
		assertEquals("CREATED", jsonResponse.get("status"));
		assertNotNull(jsonResponse.get("authorizedGrantTypes"));
		assertNotNull(jsonResponse.get("authorities"));
		assertNotNull(jsonResponse.get("registeredRedirectUris"));
		assertNotNull(jsonResponse.get("scopes"));
		assertNotNull(jsonResponse.get("disabledNewUser"));
	}
	
	@Test
	public void testGetMyApplications_존재하지않는ID로조회() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId,
				testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id","NOT_EXIST_APPUUID");
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(404).log().all()
		.when()
			.get("/api/applications/own/{id}").andReturn();
		
		/* 3. response printing & detail assertions */
		//Response Body 없음
//		JsonPath jsonResponse = new JsonPath(response.asString());

	}
	
}
