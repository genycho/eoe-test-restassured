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
import eoe.resttest.APITestRuntimeException;
import eoe.resttest.EoEAPITestCase;
import eoe.resttest.TestDataPrepareUtil;
import eoe.resttest.TestUtils;
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
public class CreateApplicationTest extends EoEAPITestCase{
	
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY;
	
	String accessToken = null;
	String uuid= null;
	
	public CreateApplicationTest() {
		super();
		setAPIInfo(APIPathDecl.APIPATH_CREATEAPPLICATION, "POST");
	}
	
	@Before
	public void setUp() throws Exception {
		accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		uuid = CommonFunctions.getInstance().getFirstAppManagerUUID(testAppManagerId, testApiKey);
	}

	@After
	public void tearDown() throws Exception {
		try {
			CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApiKey,
					APITestData.TEST_APPLICATIONNAME);
		} catch (Throwable ignore) {
		}
		//삭제가 안 되어서 무한루프 걸림 
		//		String createdAppUUID = "";
//		try {
//			while (createdAppUUID != null) {
//				createdAppUUID = new SearchApplicationTest()
//						.getFirstApplicationUUIDWithNameLike(APITestData.TEST_APPLICATIONNAME);
//				new DeleteApplicationTest().deleteApplication(APITestData.TEST_APPMANAGERID, APITestData.TEST_APIKEY,
//						createdAppUUID);
//			}
//		} catch (Throwable ignore) {
//		}
	}
	
	/**
	 * 
	 * */
	@Test
	public void testCreateApplication_필수입력() throws Exception {
		JSONObject inputJSON = TestDataPrepareUtil.getTestApplication(APITestData.TEST_APPLICATIONNAME+TestUtils.getUniqueString(),
				testAppManagerId, testApiKey, uuid);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
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
		assertNotNull(jsonResponse	.get("createdBy"));
		assertEquals(APITestData.TEST_APPMANAGERID, jsonResponse	.get("createdBy"));
		assertNotNull(jsonResponse	.get("id"));
		assertNotNull(jsonResponse	.get("managerId"));
		assertEquals(APITestData.TEST_APPMANAGERID, jsonResponse	.get("managerId"));
		assertNotNull(jsonResponse	.get("apiKey"));
//		assertEquals(APITestData.TEST_APIKEY, jsonResponse	.get("apiKey")); 새로 생성된 API KEY 가 반환되는 것으로 보임. 
		assertNotNull(jsonResponse	.get("name"));
		assertTrue(jsonResponse	.getString("name").startsWith(APITestData.TEST_APPLICATIONNAME));
		assertNotNull(jsonResponse	.get("status"));
		assertEquals("CREATED", jsonResponse	.get("status"));
	}
	
	@Test
	public void testCreateApplication_선택입력포함() throws Exception {
		JSONObject inputJSON = 	TestDataPrepareUtil.getTestApplication(APITestData.TEST_APPLICATIONNAME+TestUtils.getUniqueString(),
						APITestData.TEST_APPMANAGERID, APITestData.TEST_APIKEY, uuid);
		
		inputJSON.put("disabledNewUser", "true");
		JSONArray scopeArray = new JSONArray();
		scopeArray.add("read,refresh_token,write");
		inputJSON.put("scopes", scopeArray);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
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
		assertNotNull(jsonResponse	.get("disabledNewUser"));
		assertEquals(true, jsonResponse	.get("disabledNewUser"));
		assertNotNull(jsonResponse	.get("scopes"));
		ArrayList scopesList = jsonResponse	.get("scopes");
		assertNotNull(scopesList.get(0));
		assertEquals("read,refresh_token,write", scopesList.get(0));
	}
	
	@Test
	public void testCreateApplication_APPNAME필수입력누락() throws Exception {
		JSONObject inputJSON = 	TestDataPrepareUtil.getTestApplication(APITestData.TEST_APPLICATIONNAME+TestUtils.getUniqueString(),
						APITestData.TEST_APPMANAGERID, APITestData.TEST_APIKEY, uuid);

		/**		*/
		inputJSON.put("name",null);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
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
	}
	
	@Test
	public void testCreateApplication_같은Application중복등록() throws Exception {
		JSONObject inputJSON = 	TestDataPrepareUtil.getTestApplication(APITestData.TEST_APPLICATIONNAME+TestUtils.getUniqueString(),
						APITestData.TEST_APPMANAGERID, APITestData.TEST_APIKEY, uuid);

		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.log().all();
		
		/* 최초 1회 등록 */
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(201).log().all()
		.when()
			.post(getApiPath()).andReturn();
//		JsonPath jsonResponse = new JsonPath(response.asString());
		
		/* 같은 정보로 중복 등록 */
		response = RestAssured
				.given()
					.spec(requestSpec)
				.expect()
					.statusCode(400).log().all()
				.when()
					.post(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("message"));
		assertTrue(jsonResponse.getString("message").startsWith("Same application name"));
		assertNotNull(jsonResponse.get("exception"));
		assertEquals("SameApplicationNameExistException", jsonResponse.get("exception"));
		assertNotNull(jsonResponse.get("status"));
		assertEquals("BAD_REQUEST", jsonResponse	.get("status"));
	}
	
}
