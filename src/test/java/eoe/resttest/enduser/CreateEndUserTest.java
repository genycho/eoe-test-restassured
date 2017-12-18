package eoe.resttest.enduser;

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
import eoe.resttest.TestUtils;
import eoe.resttest.application.CreateApplicationTest;
import eoe.resttest.application.GetApplicationTest;
import eoe.resttest.appmanager.CreateAppManagerTest;
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
public class CreateEndUserTest extends EoEAPITestCase{
	TestDataPrepareUtil dataUtil = TestDataPrepareUtil.getInstance();
	
	String appManagerUUID = null;
	String applicationUUID = null;
	
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY;
	
	String createdUserUUID = null;
			
	public CreateEndUserTest() {
		super();
		setAPIInfo(APIPathDecl.APIPATH_CREATEENDUSER, "POST");
	}
	
	@Before
	public void setUp() throws Exception {
//		dataUtil.prepareTestData();
		appManagerUUID = CommonFunctions.getInstance().getFirstAppManagerUUID(testAppManagerId, testApiKey);
		applicationUUID = CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApiKey, APITestData.TEST_APPLICATIONNAME);
		
		if(appManagerUUID == null || applicationUUID == null) {
			throw new APITestException(
					"Precondition errors!! - appManagerUUID or applicationUUID is null. -appManagerUUID:  "
							+ appManagerUUID + ", applicationUUID: " + applicationUUID);
		}
		createdUserUUID = null;
	}

	@After
	public void tearDown() throws Exception {
		if(createdUserUUID != null) {
			CommonFunctions.getInstance().deleteEndUser(createdUserUUID, testAppManagerId, testApiKey, applicationUUID);
		}
	}
	
	/**
	 * 
	 * */
	@Test
	public void testCreateEndUser_필수입력() throws Exception {
//		JSONObject inputJSON = 	TestDataPrepareUtil.getTestEndUser("ratest_"+TestUtils.getUniqueString(), dataUtil.getAppUUID(),dataUtil.getAppManagerId(), dataUtil.getApiKey(), dataUtil.getAppManagerUUID());
		JSONObject inputJSON = 	TestDataPrepareUtil.getTestEndUser("ramail@eoe.com",this.applicationUUID,testAppManagerId, testApiKey, true);
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.queryParam("applicationId", applicationUUID);
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
		assertNotNull(jsonResponse.get("createdBy"));
		assertNotNull(jsonResponse.get("createdAt"));
		assertNotNull(jsonResponse.get("lastModifiedAt"));
		assertNotNull(jsonResponse.get("lastModifiedBy"));
		assertNotNull(jsonResponse.get("id"));
		createdUserUUID = jsonResponse.get("id");
		
		assertNotNull(jsonResponse.get("email"));
		assertTrue(jsonResponse.getString("email").contains("ramail@eoe.com"));
		assertNotNull(jsonResponse.get("status"));
		assertEquals("CREATED",jsonResponse.get("status"));
		assertNotNull(jsonResponse.get("credential"));
		assertNotNull(jsonResponse.get("credential.account"));
		assertNotNull(jsonResponse.get("credential.password"));
		
		assertNotNull(jsonResponse.get("profile"));
		assertNotNull(jsonResponse.get("profile.firstName"));
		assertNotNull(jsonResponse.get("profile.lastName"));
		assertNotNull(jsonResponse.get("profile.middleName"));
		assertNotNull(jsonResponse.get("profile.nickName"));
		assertNotNull(jsonResponse.get("profile.mobilePhoneNo"));
		assertNotNull(jsonResponse.get("profile.birthDate"));
		assertNotNull(jsonResponse.get("profile.country"));
		assertNotNull(jsonResponse.get("profile.locale"));
		assertNotNull(jsonResponse.get("profile.language"));
		assertNotNull(jsonResponse.get("profile.timezone"));
		assertNotNull(jsonResponse.get("profile.gender"));
//		assertNotNull(jsonResponse.get("profile.address"));
		assertNotNull(jsonResponse.get("profile.requiredVerifyEmail"));
		assertNotNull(jsonResponse.get("profile.verifiedMobileNo"));
		assertNotNull(jsonResponse.get("profile.requiredVerifyMobileNo"));
		assertNotNull(jsonResponse.get("profile.verifiedEmail"));
		
		assertNotNull(jsonResponse.get("applications"));
	}
	
	@Test
	public void testCreateApplication_필수입력누락() throws Exception {
		JSONObject inputJSON = 	TestDataPrepareUtil.getTestEndUser("ramail@eoe.com",this.applicationUUID,testAppManagerId, testApiKey,true);
		inputJSON.put("email", null);//200 등록 됨
		inputJSON.put("managerId", null);//200 등록 됨
		
		JSONObject profileJSON = (JSONObject) inputJSON.get("profile");
		profileJSON.put("mobilePhoneNo", null);//200 등록 됨
		
		JSONObject creditJSON = (JSONObject) inputJSON.get("credential");
		creditJSON.put("account", null);
		creditJSON.put("password", null);
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(400).log().all()
		.when()
			.post(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("message"));
		assertEquals("No user credential provided", jsonResponse.get("message"));
		assertNotNull(jsonResponse.get("exception"));
		assertEquals("NoUserLoginCredentialException", jsonResponse.get("exception"));
		assertNotNull(jsonResponse.get("status"));
		assertEquals("BAD_REQUEST", jsonResponse.get("status"));
	}
	
	@Test
	public void testCreateEndUser_존재하지않는앱에가입시도() throws Exception {
		JSONObject inputJSON = 	TestDataPrepareUtil.getTestEndUser("ramail@eoe.com",this.applicationUUID,testAppManagerId, testApiKey,true);
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.queryParam("applicationId", "not_exist_id");
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(404).log().all()
		.when()
			.post(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
//		JsonPath jsonResponse = new JsonPath(response.asString());
	}
	
	@Test
	public void testCreateEndUser_같은이메일유저() throws Exception {
		JSONObject inputJSON = 	TestDataPrepareUtil.getTestEndUser("ramail@eoe.com",this.applicationUUID,testAppManagerId, testApiKey,true);
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(201).log().all()
		.when()
			.post(getApiPath()).andReturn();
		
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("email"));
		
		String firstCreatedEmail = jsonResponse.get("email");
		
		inputJSON = 	TestDataPrepareUtil.getTestEndUser("ramail@eoe.com",this.applicationUUID,testAppManagerId, testApiKey,true);
		inputJSON.put("email", firstCreatedEmail);	
		 requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();
		
		response = RestAssured
				.given()
					.spec(requestSpec)
				.expect()
					.statusCode(400).log().all()
				.when()
					.post(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("message"));
		assertEquals("Same email address is already registered", jsonResponse.get("message"));
		assertNotNull(jsonResponse.get("exception"));
		assertEquals("DuplicatedEmailUserFoundException", jsonResponse.get("exception"));
		assertNotNull(jsonResponse.get("status"));
		assertEquals("BAD_REQUEST", jsonResponse.get("status"));
	}
	
	@Test
	public void testCreateEndUser_같은모바일번호유저() throws Exception {
		//testCreateEndUser_같은이메일유저와 동일
	}

	
	@Test
	public void testCreateEndUser_같은이름유저() throws Exception {
		//testCreateEndUser_같은이메일유저와 동일
	}
	
	@Test
	public void testCreateEndUser_DISALLOWADDUSER앱에가입시도_현재미구현() throws Exception {
		CommonFunctions.getInstance().changeApplicationAttr("TERMINATED", true, this.testAppManagerId, this.testApiKey, applicationUUID);
		
		JSONObject inputJSON = 	TestDataPrepareUtil.getTestEndUser("ramail@eoe.com",this.applicationUUID,testAppManagerId, testApiKey, true);
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(201).log().all()
		.when()
			.post(getApiPath()).andReturn();
		// 현재미구현
		/* 3. response printing & detail assertions */
//		JsonPath jsonResponse = new JsonPath(response.asString());
	}
}
