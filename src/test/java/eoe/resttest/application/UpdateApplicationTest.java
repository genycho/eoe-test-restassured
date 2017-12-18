package eoe.resttest.application;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eoe.resttest.APIPathDecl;
import eoe.resttest.APITestData;
import eoe.resttest.APITestException;
import eoe.resttest.EoEAPITestCase;
import eoe.resttest.TestDataPrepareUtil;
import eoe.resttest.appmanager.GetAppManagerTest;
import eoe.resttest.appmanager.LoginAsAppManagerTest;
import eoe.resttest.common.CommonFunctions;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * 
 * 
 *@Example
 */
public class UpdateApplicationTest extends EoEAPITestCase{
	String appUUID = null;
	String accessToken = null;
	
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY;
	
	public UpdateApplicationTest() {
		super();
		setAPIInfo(APIPathDecl.APIPATH_UPDATEAPPLICATION, "PUT");
	}
	
	@Before
	public void setUp() throws Exception {
//		this.appUUID = CommonFunctions.getInstance().createApplicationId(testAppManagerId, testApiKey, "ra first app name");
		this.appUUID = CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApiKey, APITestData.TEST_APPLICATIONNAME);
		if(this.appUUID == null) {
			throw new APITestException("There is no pre-created application(name) to test 'UPDATE APPLCIAITON', No app name like "+APITestData.TEST_APPLICATIONNAME);
		}
		accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
	}

	@After
	public void tearDown() throws Exception {
		try{
			CommonFunctions.getInstance().deleteApplication(testAppManagerId, testApiKey, appUUID);
		}catch(Exception forceDelete) {
			//not care
		}
	}
	
	/**
	 * 
	 * */
	@Test
	public void testUpdateApplication_기본() throws Exception {
		JSONObject inputJSON = 	this.getUpdateTestApplication(this.appUUID,APITestData.TEST_APPLICATIONNAME+"_modify test",
				testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.pathParam("id", this.appUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.put(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse	.get("createdBy"));
		assertNotNull(jsonResponse	.get("createdAt"));
		
		assertNotNull(jsonResponse	.get("lastModifiedAt"));
		assertNotNull(jsonResponse	.get("lastModifiedBy"));
		assertEquals(testAppManagerId, jsonResponse	.get("lastModifiedBy"));
		assertNotNull(jsonResponse	.get("id"));
		assertEquals(appUUID, jsonResponse	.get("id"));
		
		assertNotNull(jsonResponse.get("managerId"));
		assertEquals(testAppManagerId, jsonResponse	.get("managerId"));
		assertNotNull(jsonResponse	.get("apiKey"));
		assertNotNull(jsonResponse	.get("name"));
		assertEquals(inputJSON.get("name"), jsonResponse	.get("name"));
		assertNotNull(jsonResponse	.get("status"));
//		assertEquals("CREATED", jsonResponse	.get("status"));
		
		assertNotNull(jsonResponse	.get("disabledNewUser"));
		assertEquals(true, jsonResponse	.get("disabledNewUser"));
		assertNotNull(jsonResponse	.get("scopes"));
//		ArrayList scopesList = jsonResponse	.get("scopes");
//		assertNotNull(scopesList.get(0));
//		assertEquals("read,refresh_token,write", scopesList.get(0));
	}
	
	@Test
	public void testUpdateApplication_STATUS_ALLOWUSER수정() throws Exception {
		JSONObject inputJSON = 	TestDataPrepareUtil.getUpdateTestApplicationStatus("ACTIVE", false);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.pathParam("id", this.appUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.put(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse	.get("status"));
		assertEquals("ACTIVE", jsonResponse	.get("status"));
		
		assertNotNull(jsonResponse	.get("disabledNewUser"));
		assertEquals(false, jsonResponse	.get("disabledNewUser"));
	}
	
	@Test
	public void testUpdateApplication_존재하지않는APPID() throws Exception {
		JSONObject inputJSON = 	this.getUpdateTestApplication("NO_EXIST_ID","ra modified app name",
				testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.pathParam("id", "NO_EXIST_ID");
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(404).log().all()
		.when()
			.put(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
//		JsonPath jsonResponse = new JsonPath(response.asString());
	}
	
	@Test
	public void testUpdateApplication_타AP_MANAGER의어플리케이션수정시도() throws Exception {
		//TODO
//		APP MANAGER를 등록하는 기능이 될 때까지 보류
	}
	
	//TODO
//	@Test
//	public void testUpdateApplication_APPNAME필수입력누락() throws Exception {
//		
//	}
//	
//	@Test
//	public void testUpdateApplication_같은Application중복등록() throws Exception {
//		
//	}
	
	/**
	 * 
	 */
	private JSONObject getUpdateTestApplication(String appId, String appName, String appManagerId, String apiKey) {
		JSONObject topJSon = new JSONObject();
		topJSon.put("apiKey",apiKey);
		topJSon.put("name",appName);
		
		topJSon.put("disabledNewUser",true);
		topJSon.put("id",appId);
//		topJSon.put("managerId",appManagerUUID);
		
		JSONArray authorizedGrantTypesArray = new JSONArray();
		authorizedGrantTypesArray.add("authorization_code,password,client_credentials,refresh_token");
		topJSon.put("authorizedGrantTypes", authorizedGrantTypesArray);
		return topJSon;
	}
}
