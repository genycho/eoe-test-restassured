package eoe.resttest.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eoe.resttest.APITestData;
import eoe.resttest.EoEAPITestCase;
import eoe.resttest.appmanager.LoginAsAppManagerTest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * 
 */
public class GetCommonCodeTest extends EoEAPITestCase{
	 public GetCommonCodeTest() {
		 super();
		 this.setAPIInfo("/api/codes/end-user-status-types, /api/codes/gender-types , /api/codes/tenant-status-types ", "GET");
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
	public void testEndUserStatusCodes_기본() throws Exception {
		String accessToken =CommonFunctions.getInstance().getAccessToken(APITestData.TEST_APPMANAGERID,
				APITestData.TEST_APIKEY);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.get("/api/codes/end-user-status-types").andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		ArrayList<HashMap> result = jsonResponse.get("$");
		assertNotNull(result);
		assertEquals(5, result.size());
		assertEquals("CREATED", result.get(0).get("CREATED"));
//		assertEquals("CREATED", jsonResponse.get("CREATED"));
//		assertEquals("ACTIVE", jsonResponse.get("ACTIVE"));
//		assertEquals("SUSPENDED", jsonResponse.get("SUSPENDED"));
//		assertEquals("TERMINATED", jsonResponse.get("TERMINATED"));
//		assertEquals("UNKNOWN", jsonResponse.get("UNKNOWN"));
	}
	
	/**
	 * 
	 * */
	@Test
	public void testGenderTypeCodes_기본() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(APITestData.TEST_APPMANAGERID,
				APITestData.TEST_APIKEY);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.get(" /api/codes/gender-types").andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		ArrayList<HashMap> result = jsonResponse.get("$");
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals("MALE", result.get(0).get("MALE"));
	}
	
	/**
	 * 
	 * */
	@Test
	public void testTenantStatusTypeCodes_기본() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(APITestData.TEST_APPMANAGERID,
				APITestData.TEST_APIKEY);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.get("/api/codes/tenant-status-types").andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		ArrayList<HashMap> result = jsonResponse.get("$");
		assertNotNull(result);
		assertEquals(6, result.size());
		assertEquals("CREATED", result.get(0).get("CREATED"));
	}
	
}
