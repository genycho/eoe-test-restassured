package eoe.resttest.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.json.simple.JSONObject;

import eoe.resttest.APIPathDecl;
import eoe.resttest.APITestException;
import eoe.resttest.AbstractRestAPITestRunner;
import eoe.resttest.EoEAPITestUtils;
import eoe.resttest.TestDataPrepareUtil;
import eoe.resttest.TestUtils;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class CommonFunctions extends AbstractRestAPITestRunner{
	private static CommonFunctions instance;
	RestAssured restAssured = new RestAssured();
	
	private CommonFunctions() {
	}
	
	public static CommonFunctions getInstance() {
		if(instance == null) {
			instance = new CommonFunctions();
			instance.readTestMode();
			instance.restAssured.baseURI = EoEAPITestUtils.getBaseURI(instance.getTestMode());
			instance.restAssured.port = EoEAPITestUtils.getPort(instance.getTestMode());
		}
		return instance;
	}
	
	public String getAccessToken(String loginId, String loginPw) throws  APITestException {
		String generatedAccessToken = null;
		String body = "grant_type=client_credentials";
		
		RequestSpecification requestSpec = null;
		try {
			requestSpec = EoEAPITestUtils.getBasicXFormRequestSpec(
					EoEAPITestUtils.generateAuth(loginId, loginPw), body);
		} catch (UnsupportedEncodingException e) {
			throw new APITestException(e);
		}
//		requestSpec.log().all();
		Response response = RestAssured.given().spec(requestSpec).expect()//.log().all()
				.statusCode(200).when().post("/oauth/token")
				.andReturn();
		JsonPath jsonResponse = new JsonPath(response.asString());
		if (jsonResponse.get("access_token") == null) {
			throw new APITestException(
					"Failed to regenerate an access token for testing with id,apikey - " + loginId + ":" + loginPw);
		}else {
			generatedAccessToken =jsonResponse.get("access_token"); 
		}
		return generatedAccessToken;
	}
	
	/**
	 * 
	 * @param apiManagerId
	 * @param apiKey
	 * @param nameSearchKeyword
	 * @return	first result's id value
	 * @throws APITestException
	 */
	public String getFirstApplicationUUIDWithNameLike(String apiManagerId, String apiKey, String nameSearchKeyword) throws APITestException {
		String uuid = null;
		String accessToken = CommonFunctions.getInstance().getAccessToken(apiManagerId, apiKey);
		
		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken,"");
		requestSpec.queryParam("name", nameSearchKeyword);
//		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200)//.log().all()
		.when()
			.get(APIPathDecl.APIPATH_SEARCHAPPLICATION).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		uuid = jsonResponse.get("content[0].id");
		if(uuid == null) {
			throw new APITestException("There is no search result with the keyword - " + nameSearchKeyword);
		}
		return uuid;
	}
	
	/**
	 * 
	 * @param apiManagerId
	 * @param apiKey
	 * @param appUUID
	 * @return	String array which has the [application's uuid, apikey]
	 * @throws APITestException
	 */
	public String[] getApplicationWithAPPUUID(String apiManagerId, String apiKey, String appUUID) throws APITestException {
		String accessToken = CommonFunctions.getInstance().getAccessToken(apiManagerId, apiKey);
		
		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id",appUUID);
//		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200)//.log().all()
		.when()
			.get(APIPathDecl.APIPATH_GETAPPLICATION).andReturn();
		
		/* 3. response printing & detail assertions */
		if(response!=null) {
			String[] appLoginInfo = new String[2];
			JsonPath jsonResponse = new JsonPath(response.asString());
			appLoginInfo[0] =jsonResponse.get("id");
			appLoginInfo[1] =jsonResponse.get("apiKey");
			return appLoginInfo;
		}else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param appManagerID
	 * @param apiKey
	 * @param email
	 * @return	UUID - the created AppManager's UUID
	 * @throws UnsupportedEncodingException
	 * @throws APITestException 
	 */
	public String createTestAppManager(String appManagerID, String apiKey, String email ) throws APITestException {
		JSONObject inputJSON = TestDataPrepareUtil.getTestAppManagerJSON(appManagerID, apiKey, email, true);
		
		RequestSpecification requestSpec;
		try {
			requestSpec = EoEAPITestUtils.getDefaultBasicRequestSpec(
					EoEAPITestUtils.generateAuth(appManagerID, apiKey),
					inputJSON.toJSONString());
		} catch (UnsupportedEncodingException e) {
			throw new APITestException("AppManager is not created properly",e);
		}
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
		.when()
			.post(APIPathDecl.APIPATH_CREATEAPPMANAGER).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		String generateAppManagerUUID = jsonResponse.getString("id");
		if(generateAppManagerUUID == null) {
			throw new APITestException("AppManager is not created properly");
		}
		return jsonResponse.getString("id");
	}
	
	public void deleteAppManager(String appManagerId , String apiKey   , String existAppManagerUUID) throws APITestException {
		String accessToken = this.getAccessToken(appManagerId, apiKey);
		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken, "");
		requestSpec.pathParam("id",existAppManagerUUID);
		Response response = RestAssured
				.given()
					.spec(requestSpec)
				.expect().statusCode(204)//.log().all()
				.when()
					.delete(APIPathDecl.APIPATH_DELETEAPPMANAGER).andReturn();
		JsonPath jsonResponse = new JsonPath(response.asString());
	}
	
	/**
	 * 
	 * @param appManagerId
	 * @param apiKey
	 * @return	UUID, if not exist, return null
	 * @throws APITestException
	 */
	public String getFirstAppManagerUUID(String appManagerId, String apiKey)
			throws APITestException {
		String accessToken = CommonFunctions.getInstance().getAccessToken(appManagerId, apiKey);

		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken, "");
		requestSpec.queryParam("account", appManagerId);
//		requestSpec.log().all();

		Response response = RestAssured
				.given()
					.spec(requestSpec)
				.expect().statusCode(200)
				//.log().all()
				.when()
					.get(APIPathDecl.APIPATH_SEARCHAPPMANAGER)
					.andReturn();

		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		String generatedAppManagerId = jsonResponse.get("id");
		if (generatedAppManagerId == null) {
			throw new APITestException("There is no search items");
		}
		return generatedAppManagerId;
	}
	
	public String getFirstAppManagerID(String appManagerId, String apiKey, String accountKeyword)
			throws APITestException {
		String accessToken = CommonFunctions.getInstance().getAccessToken(appManagerId, apiKey);

		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken, "");
		requestSpec.queryParam("account", accountKeyword);
		requestSpec.log().all();

		Response response = RestAssured.given().spec(requestSpec).expect().statusCode(200).log().all().when()
				.get(APIPathDecl.APIPATH_SEARCHAPPMANAGER).andReturn();

		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		String generatedAppManagerId = jsonResponse.get("id");
		if (generatedAppManagerId == null) {
			throw new APITestException("There is no search items for - " + accountKeyword);
		}
		return generatedAppManagerId;
	}
	
	public String createApplicationId(String appManagerId, String apiKey, String appName) throws APITestException {
		String appManagerUUID = CommonFunctions.getInstance().getFirstAppManagerUUID(appManagerId, apiKey);
		String accessToken = this.getAccessToken(appManagerId, apiKey);
		
		JSONObject inputJSON = TestDataPrepareUtil.getTestApplication(appName, appManagerId, apiKey, appManagerUUID);
		
		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken, inputJSON.toJSONString());
//		requestSpec.log().all();

		Response response = RestAssured.given().spec(requestSpec).expect().statusCode(201).log().all().when().post(APIPathDecl.APIPATH_CREATEAPPLICATION)
				.andReturn();

		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
//		if (jsonResponse.getString("id") == null || "".equals(jsonResponse.getString("id"))) {
//			throw new APITestException("Applcation did not generate properly - appName = " + appName);
//		} else {
			return jsonResponse.getString("id");
//		}
	}
	
	/**
	 * delete an Application with application's uuid
	 * @param appManagerId
	 * @param apiKey
	 * @param appUUID
	 * @throws APITestException
	 */
	public void deleteApplication(String appManagerId, String apiKey, String appUUID) throws APITestException {
		String accessToken = CommonFunctions.getInstance().getAccessToken(appManagerId, apiKey);
		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken, "");
		requestSpec.pathParam("id", appUUID);
		Response response = RestAssured.given().spec(requestSpec).expect().when()
				.delete(APIPathDecl.APIPATH_DELETEAPPLICATION).andReturn();
	}

	public void changeApplicationAttr(String appStatus, boolean disallowAddUser, String appManagerId, String apiKey,
			String appUUID) throws APITestException {
		JSONObject inputJSON = TestDataPrepareUtil.getUpdateTestApplicationStatus(appStatus, disallowAddUser);
		String accessToken = CommonFunctions.getInstance().getAccessToken(appManagerId, apiKey);
		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken, inputJSON.toJSONString());
		requestSpec.pathParam("id", appUUID);
		// requestSpec.log().all();

		Response response = RestAssured.given().spec(requestSpec).expect().statusCode(200).log().all().when()
				.put(APIPathDecl.APIPATH_UPDATEAPPLICATION).andReturn();

		/* 3. response printing & detail assertions */
//		 JsonPath jsonResponse = new JsonPath(response.asString());
//		 String chagedStatus = jsonResponse .get("status");
//				 boolean chagedUserFlag = jsonResponse .get("disabledNewUser");
//				 if(chagedUserFlag ==null ||)
	}
	
	/**
	 * 
	 * @param email
	 * @param userId
	 * @param userPw
	 * @param appManagerId
	 * @param apiKey
	 * @param appUUID
	 * @return	generated end-user UUID
	 * @throws APITestException
	 */
	public String createAndGetEndUserUUID(String email, String userId, String userPw, String appManagerId, String apiKey,	String appUUID) throws APITestException {
		JSONObject inputJSON = TestDataPrepareUtil.getTestEndUser(userId, userPw, email, "firstna" + TestUtils.getUniqueString(),
				"000-0000-0000" + TestUtils.getUniqueString(), appUUID, appManagerId, apiKey);
		
		String accessToken = CommonFunctions.getInstance().getAccessToken(appManagerId, apiKey);
		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken,inputJSON.toJSONString());
		requestSpec.queryParam("applicationId", appUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(201).log().all()
		.when()
			.post(APIPathDecl.APIPATH_CREATEENDUSER).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		String uuid = jsonResponse.getString("id");
		return uuid;
	}
	
	/**
	 * create EndUser and Get its UUID
	 * @param appManagerId
	 * @param apiKey
	 * @param appUUID
	 * @return	End-user's UUID, if not exist, return null
	 * @throws APITestException
	 */
	public String createAndGetEndUserUUID(String email, String appManagerId, String apiKey,	String appUUID) throws APITestException {
		return createAndGetEndUserUUID(email, "ratest_account" + TestUtils.getUniqueString(), "ratest_password", appManagerId, apiKey, appUUID);
	}
	
	/**
	 * get EndUser's UUID WithEmail
	 * @param appManagerId
	 * @param apiKey
	 * @param appUUID
	 * @return	End-user's UUID, if not exist, return null
	 * @throws APITestException
	 */
	public String getEndUserUUIDWithEmail(String email, String appManagerId, String apiKey,	String appUUID) throws APITestException {
		String accessToken = CommonFunctions.getInstance().getAccessToken(appManagerId, apiKey);
		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken,"");
		requestSpec.queryParam("applicationId", appUUID);
		requestSpec.queryParam("email", email);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.get(APIPathDecl.APIPATH_SEARCHENDUSEREMAIL).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		String uuid = jsonResponse.getString("id");
//		if(uuid == null) {
//			throw new APITestException("There is no end-user who has email - "+email);
//		}
		return uuid;
	}
	
	public void deleteEndUser(String endUserUUID, String appManagerId, String apiKey, String appUUID) throws APITestException {
		String accessToken = CommonFunctions.getInstance().getAccessToken(appManagerId, apiKey);
		RequestSpecification requestSpec =EoEAPITestUtils. getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id", endUserUUID);
		requestSpec.queryParam("applicationId", appUUID);
//		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(204)//.log().all()
		.when()
			.delete(APIPathDecl.APIPATH_DELETEENDUSER).andReturn();
	}
}
