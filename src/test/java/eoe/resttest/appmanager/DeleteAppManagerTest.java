package eoe.resttest.appmanager;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.hamcrest.Description;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eoe.resttest.APIPathDecl;
import eoe.resttest.APITestData;
import eoe.resttest.APITestException;
import eoe.resttest.EoEAPITestUtils;
import eoe.resttest.EoEAPITestCase;
import eoe.resttest.TestDataPrepareUtil;
import eoe.resttest.common.CommonFunctions;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class DeleteAppManagerTest extends EoEAPITestCase{
	TestDataPrepareUtil dataUtil = TestDataPrepareUtil.getInstance();
	
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApiKey = APITestData.TEST_APIKEY;
	
	String toCreateAppManagerId = APITestData.NEW_TEST_APPMANAGERID;
	String toCreateApiKey = APITestData.NEW_TEST_APIKEY;
	String tempAppMangerUUID = null;
	
	String sysAdminID = "sysadmin";
	String sysAdminPW = "sysadmin";
	
	public DeleteAppManagerTest() {
		super();
		setAPIInfo(APIPathDecl.APIPATH_DELETEAPPMANAGER, "DELETE");
	}
	
	@Before
	public void setUp() throws Exception {
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore	//삭제 권한이 없어서 수행 불가
	public void testDeleteAppManager_기본() throws Exception {
		/* Prepare test data - move to setUp.....	*/
		tempAppMangerUUID =  CommonFunctions.getInstance().getFirstAppManagerID(testAppManagerId, testApiKey, "ra_app_manger");
		
		/*	deletion is only run by SYSADMIN	*/
		String accessToken = CommonFunctions.getInstance().getAccessToken(sysAdminID, sysAdminPW);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken, "");
		requestSpec.pathParam("id",tempAppMangerUUID);
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(204).log().all()
		.when()
			.delete(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
	}
	
	@Ignore
	public void testDeleteAppManager_존재하지않는APPMANGER삭제() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(sysAdminID, sysAdminPW);
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken, "");
		requestSpec.pathParam("id","NOT_EXIST_APPMANAGERID");
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(404).log().all()
		.when()
			.delete(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
	}

}
