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
import eoe.resttest.EoEAPITestUtils;
import eoe.resttest.EoEAPITestCase;
import eoe.resttest.TestDataPrepareUtil;
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
public class CreateAppManagerTest extends EoEAPITestCase{
	String testAppManagerID = APITestData.TEST_APPMANAGERID;
	String testApiKey =  APITestData.TEST_APIKEY;
	String existAppManagerUUID = null;
	
	String newAppManagerID = APITestData.NEW_TEST_APPMANAGERID;
	String newApiKey =  APITestData.NEW_TEST_APIKEY;
	String newAPEmail = "apitest@eoe.com";
	
	String sysAdminID = "sysadmin";
	String sysAdminPW = "sysadmin";
	
	public CreateAppManagerTest() {
		super();
		setAPIInfo(APIPathDecl.APIPATH_CREATEAPPMANAGER, "POST");
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
	public void testCreateAppManager_필수입력() throws Exception {
		try {
			existAppManagerUUID =CommonFunctions.getInstance().getFirstAppManagerID(testAppManagerID, testApiKey, newAppManagerID);
			CommonFunctions.getInstance().deleteAppManager(sysAdminID,sysAdminPW, existAppManagerUUID);
		}catch(Throwable ignore) {
			ignore.printStackTrace();
		}
		
		JSONObject inputJSON = TestDataPrepareUtil.getTestAppManagerJSON(newAppManagerID,newApiKey,newAPEmail, true);
		
		RequestSpecification requestSpec = getDefaultBasicRequestSpec(
				EoEAPITestUtils.generateAuth(testAppManagerID, testApiKey),
				inputJSON.toJSONString());
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(201).log().all()
		.when()
			.post(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		
		existAppManagerUUID = jsonResponse.getString("id");
	}
	
	@Ignore
	public void testCreateAppManager_선택입력포함() throws Exception {
		//TODO	기본 케이스 성공 후 작성 
		
		
		//existAppManagerUUID = jsonResponse.getString("id");
	}
	
	/**
	 * 
	 * */
	@Ignore
	public void testCreateAppManager_같은아이디로중복등록() throws Exception {
		JSONObject inputJSON = TestDataPrepareUtil.getTestAppManagerJSON(testAppManagerID, testApiKey, true);
		
		RequestSpecification requestSpec = getDefaultBasicRequestSpec(
				EoEAPITestUtils.generateAuth(testAppManagerID, testApiKey),
				inputJSON.toJSONString());
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200)
		.when()
			.post(getApiPath()).andReturn();
		
		Response response2 = RestAssured
				.given()
					.spec(requestSpec)
				.expect()
					.statusCode(400).log().all()
				.when()
					.post(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response2.asString());
		assertNotNull(jsonResponse.get("message"));
		assertEquals("Same user name is already registered", jsonResponse.get("message"));
		assertNotNull(jsonResponse.get("exception"));
		assertEquals("SameUserNameFoundException", jsonResponse.get("exception"));
		assertNotNull(jsonResponse.get("status"));
		assertEquals("BAD_REQUEST", jsonResponse.get("status"));
	}
	
	/**
	 * 
	 * */
	@Ignore
	public void testCreateAppManager_같은이메일로중복등록() throws Exception {
		JSONObject inputJSON = TestDataPrepareUtil.getTestAppManagerJSON(newAppManagerID,newApiKey, "sameemail@eoe.com", false);
		
		RequestSpecification requestSpec = getDefaultBasicRequestSpec(
				EoEAPITestUtils.generateAuth(APITestData.NEW_TEST_APPMANAGERID, APITestData.NEW_TEST_APIKEY),
				inputJSON.toJSONString());
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.post(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
	}
	
}
