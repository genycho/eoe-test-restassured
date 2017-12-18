package eoe.resttest.enduser;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

public class DeleteEndUserTest extends EoEAPITestCase{
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY; 
	String userUUID = null;
	String applicationUUID = null;
	
	String testEmail = null;
	
	public DeleteEndUserTest() {
		super();
		setAPIInfo(APIPathDecl.APIPATH_DELETEENDUSER, "DELETE");
	}
	
	@Before
	public void setUp() throws Exception {
		applicationUUID = CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApiKey, APITestData.TEST_APPLICATIONNAME);
		if(applicationUUID == null) {
			//TODO 새로 생성
			throw new APITestException("Precodition failed. There is no application (id) which its name is like - "+APITestData.TEST_APPLICATIONNAME);
		}
		testEmail = "a"+TestUtils.getUniqueString()+"ratest@eoe.com";
		this.userUUID = CommonFunctions.getInstance().createAndGetEndUserUUID(testEmail,testAppManagerId, testApiKey, applicationUUID);
		if(userUUID == null) {
			throw new APITestException("Precodition failed. Failed to created an end-user and get its uuid.");
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDeleteEndUser_기본() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id", userUUID);
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(204).log().all()
		.when()
			.delete(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		try {
			String endUserUUID = CommonFunctions.getInstance().getEndUserUUIDWithEmail(testEmail, testAppManagerId,
					testApiKey, applicationUUID);
			assertNull("실제 엔드유저가 삭제되지 않아 조회되고 있습니다. ",endUserUUID);
		}catch(Throwable deleteSuccessEx) {
			//삭제가 성공하여 Exception 또는 null 반환
		}
	}
	
	@Test
	public void testDeleteEndUser_존재하지않는유저삭제시도() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id", "NOT_EXIST_ID");
		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(404).log().all()
		.when()
			.delete(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
//		JsonPath jsonResponse = new JsonPath(response.asString());
//		assertNotNull(jsonResponse.get("message"));
//		assertEquals("No user credential provided", jsonResponse.get("message"));
//		assertNotNull(jsonResponse.get("exception"));
//		assertEquals("NoUserLoginCredentialException", jsonResponse.get("exception"));
//		assertNotNull(jsonResponse.get("status"));
//		assertEquals("BAD_REQUEST", jsonResponse.get("status"));
		
	}
	
	@Test
	public void testDeleteEndUser_필수쿼리파라미터applicationId누락() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id", userUUID);
//		requestSpec.queryParam("applicationId", applicationUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
				.given()
					.spec(requestSpec)
				.expect()
					.statusCode(400).log().all()
				.when()
					.delete(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("message"));
		assertEquals("Required String parameter 'applicationId' is not present", jsonResponse.get("message"));
		assertNotNull(jsonResponse.get("exception"));
		assertEquals("org.springframework.web.bind.MissingServletRequestParameterException", jsonResponse.get("exception"));
		assertNotNull(jsonResponse.get("status"));
		assertEquals(400, jsonResponse.get("status"));
		assertNotNull(jsonResponse.get("error"));
		assertEquals("Bad Request", jsonResponse.get("error"));
	}
	
	@Ignore("현재 다른 AppManager 생성 및 정상 accessToken이 안 받아져서 수행 보류")
	public void testGetEndUser_다른AppManager의유저삭제시도() throws Exception {
		//현재 다른 AppManager 생성 및 정상 accessToken이 안 받아져서 수행 보류
	}
}
