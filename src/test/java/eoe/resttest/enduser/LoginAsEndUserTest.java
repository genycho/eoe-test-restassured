package eoe.resttest.enduser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eoe.resttest.APIPathDecl;
import eoe.resttest.APITestData;
import eoe.resttest.APITestException;
import eoe.resttest.APITestRuntimeException;
import eoe.resttest.EoEAPITestUtils;
import eoe.resttest.TestUtils;
import eoe.resttest.common.CommonFunctions;
import eoe.resttest.EoEAPITestCase;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * header 값에 Basic, 어플리케이션 아이디 / 어플리케이션의 키
 * body에 end-user의 id/pw 를 넣어서 전송
 */
public class LoginAsEndUserTest extends EoEAPITestCase {
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY;
	
	String appManagerUUID = null;
	String applicationUUID = null;
	
	String appName = "ratest_login_app";
	String createdUserUUID = null;
	
	String createdUserId = "ra_logintest_userId";
	String createdUserPw = "ra_logintest_userpw";

	public LoginAsEndUserTest() {
		super();
		setAPIInfo(APIPathDecl.APIPATH_ENDUSERLOGIN, "POST");
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		try {
			CommonFunctions.getInstance().deleteEndUser(createdUserUUID, testAppManagerId, testApiKey, applicationUUID);
			CommonFunctions.getInstance().deleteApplication(testAppManagerId, testApiKey, applicationUUID);
		}catch(Throwable forceTearDownIgnore) {
			//forceTearDownIgnore
		}
		
	}

	@Test
	public void testLoginAsEndUser_존재하는사용자() throws Exception {
		/*0.1 테스트용 어플리케이션 생성	*/
		try{
			String appUUID = CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApiKey, appName);
			CommonFunctions.getInstance().deleteApplication(testAppManagerId, testApiKey, appUUID);
		}catch(Throwable justIncase) {
			//
		}
		applicationUUID = CommonFunctions.getInstance().createApplicationId(testAppManagerId, testApiKey, appName+TestUtils.getUniqueString());
		assertNotNull(applicationUUID);
		
		/*0.2 테스트용 End-user 생성 */
		String createdUserUUID = CommonFunctions.getInstance().createAndGetEndUserUUID("ratest_login_app@eoe.com",
				createdUserId, createdUserPw, testAppManagerId, testApiKey, applicationUUID);
		assertNotNull(createdUserUUID);
		
		String[] appLoginInfo = CommonFunctions.getInstance().getApplicationWithAPPUUID(testAppManagerId, testApiKey, applicationUUID);
		
		/* 1. request setting */
		StringBuffer bodyBuffer = new StringBuffer();
		bodyBuffer.append("grant_type=password&username=");
		bodyBuffer.append(createdUserId);
		bodyBuffer.append("&password=");
		bodyBuffer.append(createdUserPw);
		RequestSpecification requestSpec = getBasicXFormRequestSpec(
				EoEAPITestUtils.generateAuth(appLoginInfo[0], appLoginInfo[1]), bodyBuffer.toString());
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();

		/* 2. running & basic assertions */
		Response response = RestAssured
				.given()
					.spec(requestSpec)
				.expect()
					.log().all()
					.statusCode(200)
				.when()
					.post(getApiPath())
					.andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("access_token"));
		assertNotNull(jsonResponse.get("token_type"));
		assertEquals("bearer", jsonResponse.get("token_type"));
		assertNotNull(jsonResponse.get("expires_in"));
		assertNotNull(jsonResponse.get("scope"));
		
	}
	
	@Test
	public void testLoginAsEndUser_존재하지않는사용자() throws Exception {
		/*0.1 테스트용 어플리케이션 생성	*/
		try{
			String appUUID = CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApiKey, appName);
			CommonFunctions.getInstance().deleteApplication(testAppManagerId, testApiKey, appUUID);
		}catch(Throwable justIncase) {
			//
		}
		applicationUUID = CommonFunctions.getInstance().createApplicationId(testAppManagerId, testApiKey, appName+TestUtils.getUniqueString());
		assertNotNull(applicationUUID);
		
		/*0.2 테스트용 End-user 생성 */
		String createdUserUUID = CommonFunctions.getInstance().createAndGetEndUserUUID("ratest_login_app@eoe.com",
				createdUserId, createdUserPw, testAppManagerId, testApiKey, applicationUUID);
		assertNotNull(createdUserUUID);
		
		String[] appLoginInfo = CommonFunctions.getInstance().getApplicationWithAPPUUID(testAppManagerId, testApiKey, applicationUUID);
		
		/* 1. request setting */
		StringBuffer bodyBuffer = new StringBuffer();
		bodyBuffer.append("grant_type=password&username=");
		/*	존재하지 않는 사용자	*/
		bodyBuffer.append("존재하지않는사용자");
		bodyBuffer.append("&password=");
		bodyBuffer.append(createdUserPw);
		RequestSpecification requestSpec = getBasicXFormRequestSpec(
				EoEAPITestUtils.generateAuth(appLoginInfo[0], appLoginInfo[1]), bodyBuffer.toString());
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();

		/* 2. running & basic assertions */
		Response response = RestAssured
				.given()
					.spec(requestSpec)
				.expect()
					.log().all()
					.statusCode(400)
				.when()
					.post(getApiPath())
					.andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("error"));
		assertEquals("invalid_grant",jsonResponse.get("error"));
		assertNotNull(jsonResponse.get("error_description"));
		assertEquals("Bad credentials", jsonResponse.get("error_description"));
	}
	
	@Test
	public void testLoginAsEndUser_맞지않는비밀번호() throws Exception {
		/*0.1 테스트용 어플리케이션 생성	*/
		try{
			String appUUID = CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApiKey, appName);
			CommonFunctions.getInstance().deleteApplication(testAppManagerId, testApiKey, appUUID);
		}catch(Throwable justIncase) {
			//
		}
		applicationUUID = CommonFunctions.getInstance().createApplicationId(testAppManagerId, testApiKey, appName+TestUtils.getUniqueString());
		assertNotNull(applicationUUID);
		
		/*0.2 테스트용 End-user 생성 */
		String createdUserUUID = CommonFunctions.getInstance().createAndGetEndUserUUID("ratest_login_app@eoe.com",
				createdUserId, createdUserPw, testAppManagerId, testApiKey, applicationUUID);
		assertNotNull(createdUserUUID);
		
		String[] appLoginInfo = CommonFunctions.getInstance().getApplicationWithAPPUUID(testAppManagerId, testApiKey, applicationUUID);
		
		/* 1. request setting */
		StringBuffer bodyBuffer = new StringBuffer();
		bodyBuffer.append("grant_type=password&username=");
		bodyBuffer.append(createdUserId);
		bodyBuffer.append("&password=");
		/*	맞지않는 비밀번호	*/
		bodyBuffer.append("wrongpw!@#$");
		RequestSpecification requestSpec = getBasicXFormRequestSpec(
				EoEAPITestUtils.generateAuth(appLoginInfo[0], appLoginInfo[1]), bodyBuffer.toString());
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();

		/* 2. running & basic assertions */
		Response response = RestAssured
				.given()
					.spec(requestSpec)
				.expect()
					.log().all()
					.statusCode(400)
				.when()
					.post(getApiPath())
					.andReturn();
		
		/* 3. response printing & detail assertions */
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("error"));
		assertEquals("invalid_grant",jsonResponse.get("error"));
		assertNotNull(jsonResponse.get("error_description"));
		assertEquals("Bad credentials", jsonResponse.get("error_description"));
		
	}
	
	@Test
	public void testLoginAsEndUser_맞지않는grant타입시도() throws Exception {
		/*0.1 테스트용 어플리케이션 생성	*/
		try{
			String appUUID = CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApiKey, appName);
			CommonFunctions.getInstance().deleteApplication(testAppManagerId, testApiKey, appUUID);
		}catch(Throwable justIncase) {
			//
		}
		applicationUUID = CommonFunctions.getInstance().createApplicationId(testAppManagerId, testApiKey, appName+TestUtils.getUniqueString());
		assertNotNull(applicationUUID);
		
		/*0.2 테스트용 End-user 생성 */
		String createdUserUUID = CommonFunctions.getInstance().createAndGetEndUserUUID("ratest_login_app@eoe.com",
				createdUserId, createdUserPw, testAppManagerId, testApiKey, applicationUUID);
		assertNotNull(createdUserUUID);
		
		String[] appLoginInfo = CommonFunctions.getInstance().getApplicationWithAPPUUID(testAppManagerId, testApiKey, applicationUUID);
		
		/* 1. request setting */
		StringBuffer bodyBuffer = new StringBuffer();
		/*	password -> client_credentials	*/
		bodyBuffer.append("grant_type=client_credentials&username=");
		/*	존재하지 않는 사용자	*/
		bodyBuffer.append(createdUserId);
		bodyBuffer.append("&password=");
		bodyBuffer.append(createdUserPw);
		RequestSpecification requestSpec = getBasicXFormRequestSpec(
				EoEAPITestUtils.generateAuth(appLoginInfo[0], appLoginInfo[1]), bodyBuffer.toString());
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();

		/* 2. running & basic assertions */
		Response response = RestAssured
				.given()
					.spec(requestSpec)
				.expect()
					.log().all()
					.statusCode(400)
				.when()
					.post(getApiPath())
					.andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("error"));
		assertEquals("invalid_grant",jsonResponse.get("error"));
		assertNotNull(jsonResponse.get("error_description"));
		assertEquals("Bad credentials", jsonResponse.get("error_description"));
	}

}
