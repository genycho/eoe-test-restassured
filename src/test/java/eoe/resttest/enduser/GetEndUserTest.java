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

public class GetEndUserTest extends EoEAPITestCase{
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY; 
	String userUUID = null;
	String applicationUUID = null;
	
	String testEmail = null;
	
	public GetEndUserTest() {
		super();
		setAPIInfo(APIPathDecl.APIPATH_GETENDUSER, "GET");
	}
	@Before
	public void setUp() throws Exception {
		applicationUUID = CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApiKey, APITestData.TEST_APPLICATIONNAME);
		if(applicationUUID == null) {
			//TODO 새로 생성
			throw new APITestException("Precodition failed. There is no application (id) which its name is like - "+APITestData.TEST_APPLICATIONNAME);
		}
		userUUID = null;
		testEmail = "a"+TestUtils.getUniqueString()+"ratest@eoe.com";
		this.userUUID = CommonFunctions.getInstance().createAndGetEndUserUUID(testEmail,testAppManagerId, testApiKey, applicationUUID);
	}

	@After
	public void tearDown() throws Exception {
		if(userUUID != null) {
			CommonFunctions.getInstance().deleteEndUser(userUUID, testAppManagerId, testApiKey, applicationUUID);
		}
	}

	@Test
	public void testGetEndUser_기본() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id", userUUID);
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
		assertNotNull(jsonResponse.get("createdAt"));
		assertNotNull(jsonResponse.get("lastModifiedAt"));
		assertNotNull(jsonResponse.get("lastModifiedBy"));
		assertNotNull(jsonResponse.get("id"));
		assertNotNull(jsonResponse.get("email"));
		assertNotNull(jsonResponse.get("status"));
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
		assertNotNull(jsonResponse.get("profile.verifiedEmail"));
		assertNotNull(jsonResponse.get("profile.verifiedMobileNo"));
		assertNotNull(jsonResponse.get("profile.requiredVerifyMobileNo"));
		
		assertNotNull(jsonResponse.get("applications"));
	}
	
	@Test
	public void testGetEndUser_존재하지않는유저조회시도() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApiKey);
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id", "NOT_EXIST_ID");
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(404).log().all()
		.when()
			.get(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
//		JsonPath jsonResponse = new JsonPath(response.asString());
//		assertNotNull(jsonResponse.get("content"));
		
	}
	
	@Ignore("현재 다른 AppManager 생성 및 정상 accessToken이 안 받아져서 수행 보류")
	public void testGetEndUser_다른AppManager의유저조회시도() throws Exception {
		//현재 다른 AppManager 생성 및 정상 accessToken이 안 받아져서 수행 보류
	}
}
