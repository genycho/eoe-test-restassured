package eoe.resttest.application;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eoe.resttest.APIPathDecl;
import eoe.resttest.APITestData;
import eoe.resttest.APITestException;
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
public class SearchApplicationNameTest extends EoEAPITestCase{
	String testApplicatonName = "ra application name for GetMyApplication";
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY;
			
	 public SearchApplicationNameTest() {
		 super();
		 this.setAPIInfo(APIPathDecl.APIPATH_SEARCHAPPLICATION, "GET");
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
	public void testSearchApplication_조회대상이있는경우() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId,
				testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.queryParam("name", "ratest");
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
		int totalCounts =  jsonResponse.getInt("totalElements");
		assertTrue(totalCounts > 0);
		assertNotNull(jsonResponse.get("content[0].id"));
		String firstAppUUID = jsonResponse.get("content[1].id");
		assertNotNull(firstAppUUID);
		assertNotEquals("", firstAppUUID);
		assertNotNull(jsonResponse.get("last"));
		assertNotNull(jsonResponse.get("numberOfElements"));
		assertNotNull(jsonResponse.get("size"));
		assertNotNull(jsonResponse.get("number"));
		assertNotNull(jsonResponse.get("first"));
	}
	
	/**
	 * 
	 * */
	@Test
	public void testSearchApplication_조회대상이없는경우() throws Exception {
		String accessToken =CommonFunctions.getInstance().getAccessToken(testAppManagerId,
				testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.queryParam("name", " no_keyword!!");
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
		assertNotNull(jsonResponse.get("totalElements"));
		int totalCounts =  jsonResponse.getInt("totalElements");
		assertEquals(0, totalCounts);
	}
	
	@Test
	public void testSearchApplication_특수문자포함검색시도() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId,
				testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.queryParam("name", " *%!@(+)# _@%}{>\"?&^%#@$+%%");
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
		assertNotNull(jsonResponse.get("totalElements"));
		int totalCounts =  jsonResponse.getInt("totalElements");
		assertEquals(0, totalCounts);
	}
}
