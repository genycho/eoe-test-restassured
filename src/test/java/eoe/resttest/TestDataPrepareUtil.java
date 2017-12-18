package eoe.resttest;

import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import eoe.resttest.application.CreateApplicationTest;
import eoe.resttest.appmanager.GetAppManagerTest;
import eoe.resttest.appmanager.LoginAsAppManagerTest;
import eoe.resttest.common.CommonFunctions;
import eoe.resttest.enduser.CreateEndUserTest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 *	This Class will be create and setting to test the whole eoe-API tests.	</br>
 *(1) Create App Manager (X)	 	</br>
 *(2) Setting the appmanager info, appmanager uuid		</br>
 *(3) Create an Application		</br>
 *(4) Setting the application info and its uuid	</br>
 *	(5) Create a user</br>
 *	</br>
 *	</br>
 *	</br>
 *
 */
public class TestDataPrepareUtil {
	private static boolean isPrepared = false;
	private static TestDataPrepareUtil instance;
	
	/* App Manager Information*/
	private String appManagerId = APITestData.TEST_APPMANAGERID;
	private String apiKey = APITestData.TEST_APIKEY;
	private String appManagerUUID = null;
	
	private String accessToken = "";
	
	
	/* Application  Information*/
	private String appName = "ratest app"+TestUtils.getUniqueString();
	private String appUUID = null;

	
	/* End-user Information*/
//	private String userName = "ratest username"+TestUtils.getUniqueString();
//	private String userFirstName = "username"+TestUtils.getUniqueString();
//	private String userLastName = "ratest";
	
	private String userUUID = null;

	private TestDataPrepareUtil() {
		this.isPrepared = false;
	}
	
	public void setDirty() {
		this.isPrepared = false;
	}
	
	public static TestDataPrepareUtil getInstance() {
		if(instance == null) {
			instance = new TestDataPrepareUtil();
		}
		return instance;
	}
	
	public boolean prepareTestData() {
		if(isPrepared ==false) {
			try {
//				createTestAppManager();
				setAccessToken();
				createTestApplication();
			} catch (Throwable notStartTestEx) {
				throw new APITestRuntimeException("Error occurred while setting test data with the exist APIs(Which are the target of API tests). Any test will be blocked.",notStartTestEx);
			}
		}
		return true;
	}
	
	private void createTestAppManager() throws APITestException {
//		//TODO 현재 CreateAppManager가 수행 안 되어 기 존재하는 id로 임의 설정하고 get 수행함
//		this.appManagerUUID =  CommonFunctions.getInstance().getFirstAppManagerID(appManagerId, apiKey, accountKeyword)(appManagerId, apiKey, accountKeyword);
		
		RequestSpecification requestSpec = EoEAPITestUtils.getDefaultRequestSpec(accessToken,"");
		requestSpec.queryParam("account", appManagerId);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(200).log().all()
		.when()
			.get(APIPathDecl.APIPATH_SEARCHAPPMANAGER).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		this.appManagerUUID = jsonResponse.getString("id");
	}
	
	private void setAccessToken() throws APITestException {
		this.accessToken = CommonFunctions.getInstance().getAccessToken(appManagerId, apiKey);
	}
	
	private void createTestApplication() throws APITestException  {
		this.appUUID = CommonFunctions.getInstance().createApplicationId(appManagerId, apiKey, appName);
	}
	
	public String getAppManagerId() {
		return appManagerId;
	}

	public void setAppManagerId(String appManagerId) {
		this.appManagerId = appManagerId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getAppManagerUUID() {
		return appManagerUUID;
	}

	public void setAppManagerUUID(String appManagerUUID) {
		this.appManagerUUID = appManagerUUID;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppUUID() {
		return appUUID;
	}

	public void setAppUUID(String appUUID) {
		this.appUUID = appUUID;
	}

	public String getUserUUID() {
		return userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
	}
	
	public static JSONObject getUpdateTestApplicationStatus(String status, boolean disabledNewUserFlag) {
		JSONObject topJSon = new JSONObject();
		topJSon.put("disabledNewUser",disabledNewUserFlag);
		topJSon.put("status",status);
		return topJSon;
	}
	
	public static JSONObject getTestAppManagerJSON(String appManagerId, String apiKey, boolean isUnique) {
		if(isUnique) {
			return getTestAppManagerJSON(appManagerId, apiKey, "ra_test"+TestUtils.getUniqueString()+"@eoe.com", isUnique);
		}else {
			return getTestAppManagerJSON(appManagerId, apiKey, "ra_test@eoe.com", isUnique);	
		}
	}
		
	public static JSONObject getTestAppManagerJSON(String appManagerId, String apiKey, String email, boolean userNameRandom) {
			JSONObject topJSon = new JSONObject();
			topJSon.put("email",email);//
			
			JSONObject credentialJSon = new JSONObject();
			credentialJSon.put("account", appManagerId);
			credentialJSon.put("password", apiKey);
			topJSon.put("loginCredential",credentialJSon);
			
			JSONObject profileJSon = new JSONObject();
			profileJSon.put("birthDate", "2000-12-31");
			profileJSon.put("country", "south korea");
			
			if(userNameRandom) {
				String temp = TestUtils.getUniqueString();
				profileJSon.put("firstName", "restapi"+temp);
			}else {
				profileJSon.put("firstName", "restapi");
			}
			profileJSon.put("lastName", "testname");
			profileJSon.put("middleName", "middle");
			
			profileJSon.put("gender", "M");
			profileJSon.put("language", "korean");
			profileJSon.put("locale", "ko-KR");
			profileJSon.put("mobilePhoneNo", "000-0000-0000");
			profileJSon.put("nickName", "_nickName");
			topJSon.put("profile", profileJSon);
			return topJSon;
		}
	
	/**
	 */
	public static JSONObject getTestApplication(String appName, String appManagerId, String apiKey, String appManagerUUID) {
		JSONObject topJSon = new JSONObject();
//		topJSon.put("id",appID);
//		topJSon.put("disabledNewUser","false");
		topJSon.put("managerId",appManagerId);
		topJSon.put("name",appName);
		
		JSONObject apiKeyInformationJSon = new JSONObject();
		apiKeyInformationJSon.put("apiKey", apiKey);
		apiKeyInformationJSon.put("id", appManagerUUID);
		topJSon.put("apiKeyInformation",apiKeyInformationJSon);
		
//		JSONArray authoritiesArray = new JSONArray();
//		JSONObject firstAuthority = new JSONObject();
//		firstAuthority.put("authority", "AP_MANAGER");
//		authoritiesArray.add(firstAuthority);
//		JSONObject secondAuthority = new JSONObject();
//		secondAuthority.put("authority", "ADMIN");
//		authoritiesArray.add(firstAuthority);
//		JSONObject thirdAuthority = new JSONObject();
//		thirdAuthority.put("authority", "USER");
//		authoritiesArray.add(firstAuthority);
//		topJSon.put("authorities", authoritiesArray);
		
		JSONArray authorizedGrantTypesArray = new JSONArray();
		authorizedGrantTypesArray.add("authorization_code,password,client_credentials,implicit,refresh_token");
		topJSon.put("authorizedGrantTypes", authorizedGrantTypesArray);
		return topJSon;
	}
	
	/**
	 * 
	 * email 
	 * 
	 */
	public static  JSONObject getTestEndUser(String emailId, String applicationUUID, String appManagerId, String apiKey) {
		return getTestEndUser(emailId,appManagerId, applicationUUID,apiKey,false);
	}

	public static JSONObject getTestEndUser(String email, String applicationUUID, String appManagerId,
			String testApiKey, boolean isUniqueRandom) {
		if(isUniqueRandom) {
			return getTestEndUser("ra_account"+TestUtils.getUniqueString(),"ra_password",email+TestUtils.getUniqueString(), "Im"+TestUtils.getUniqueString(),"000-0000-0000"+TestUtils.getUniqueString(),applicationUUID, appManagerId, testApiKey);
		}else {
			return getTestEndUser("ra_account","ra_password",email, "Im","000-0000-0000",applicationUUID, appManagerId, testApiKey);
		}
	}
	
	public static JSONObject getTestEndUser(String account, String password, String email, String firstName, String mobileNo, String applicationUUID, String appManagerId,
			String testApiKey) {
		JSONObject topJSon = new JSONObject();
		topJSon.put("managerId", appManagerId);
		topJSon.put("email", email);

		JSONObject credentialJSon = new JSONObject();
		credentialJSon.put("account", account);
		credentialJSon.put("password", password);
		topJSon.put("credential", credentialJSon);
		
		JSONObject profileJSon = new JSONObject();
		profileJSon.put("birthDate", "2000-12-31");
		profileJSon.put("country", "south korea");
		profileJSon.put("firstName", firstName);
		profileJSon.put("mobilePhoneNo", mobileNo);

		profileJSon.put("language","korean");
		profileJSon.put("lastName","enduser");
		profileJSon.put("locale","ko-KR");
		profileJSon.put("middleName","not use");
		
		profileJSon.put("nickName","my_nickname");
		profileJSon.put("gender","MALE");
		profileJSon.put("requiredVerifyEmail",false);
		profileJSon.put("requiredVerifyMobileNo",false);
		profileJSon.put("verifiedEmail",true);
		profileJSon.put("verifiedMobileNo",true);
		profileJSon.put("timezone","SEOUL, KOREA");
		topJSon.put("profile",profileJSon);
		
//		JSONArray applicationsArray = new JSONArray();
//		topJSon.put("applications",applicationsArray);
//		JSONObject dummyApplication = new JSONObject();
////		applicaitionsArray.add(dummyApp)
//		JSONObject applicationJSon = new JSONObject();
//		applicationsArray.add(applicationJSon);
//		applicationJSon.put("id", applicationUUID);
//		applicationJSon.put("apiKey", apiKey);
//		applicationJSon.put("managerId", appManagerId);
////		applicationJSon.put("name", "appName");
//		
//		JSONObject applicationPKDummyJSon = new JSONObject();
//		applicationJSon.put("pk", applicationPKDummyJSon);
//		JSONObject applicationPKJSon = new JSONObject();
//		applicationPKDummyJSon.put("application", applicationPKJSon);
//		
//		applicationPKJSon.put("apiKey", apiKey);
//		applicationPKJSon.put("id", applicationUUID);
//		applicationPKJSon.put("managerId", appManagerUUID);
//		applicationPKJSon.put("name", "ratest endusert appname");
		
		return topJSon;
	}
	
	
}
