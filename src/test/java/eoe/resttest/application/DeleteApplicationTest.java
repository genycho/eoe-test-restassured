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
public class DeleteApplicationTest extends EoEAPITestCase{
	String uuid = null;
	String appName = "ra delete application name";
	
	String testAppManagerId = APITestData.TEST_APPMANAGERID;
	String testApikey = APITestData.TEST_APIKEY;
	
	public DeleteApplicationTest() {
		super();
		setAPIInfo(APIPathDecl.APIPATH_DELETEAPPLICATION, "DELETE");
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
	public void testDeleteMyApplication_id로삭제() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(testAppManagerId, testApikey);
		//어플리케이션 삭제가 안 되고 있는 문제로 임시로 한 건 조회 후 삭제 시도로 변경 
//		this.uuid = CommonFunctions.getInstance().createApplicationId(APITestData.TEST_APPMANAGERID,
//				APITestData.TEST_APIKEY, appName + TestUtils.getUniqueString());
		this.uuid = CommonFunctions.getInstance().getFirstApplicationUUIDWithNameLike(testAppManagerId, testApikey, APITestData.TEST_APPLICATIONNAME);
		if(this.uuid == null) {
			throw new APITestException("There is no application to test 'Delete an application', No app name like "+APITestData.TEST_APPLICATIONNAME);
		}
		
		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id", this.uuid);
		
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(204).log().all()
		.when()
			.delete(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
	}
	
	/**
	 * 
	 * @jiraid[DEPPJT-287]
	 * @throws Exception
	 */
	@Test
	public void testDeleteMyApplication_id누락() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(APITestData.TEST_APPMANAGERID, APITestData.TEST_APIKEY);

		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
//		requestSpec.pathParam("id", this.uuid); 필수입력값 누락, path 파라미터여서 다른 path로 인식 함. 
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(400).log().all()
		.when()
			.delete("/api/applications/own").andReturn();
		
		/* 3. response printing & detail assertions */
		JsonPath jsonResponse = new JsonPath(response.asString());
		
		/**	Deffered @jiraid[DEPPJT-287]	*/
//		assertNotNull(jsonResponse	.get("error"));
//		assertEquals("Bad Request", jsonResponse	.get("error"));
//		assertNotNull(jsonResponse	.get("exception"));
//		assertEquals("org.springframework.web.bind.MissingServletRequestParameterException", jsonResponse	.get("exception"));
//		assertNotNull(jsonResponse	.get("message"));
//		assertEquals("Required String parameter 'Id' is not present", jsonResponse	.get("message"));
	}
	
	@Test
	public void testDeleteMyApplication_존재하지않는id() throws Exception {
		String accessToken = CommonFunctions.getInstance().getAccessToken(APITestData.TEST_APPMANAGERID, APITestData.TEST_APIKEY);

		RequestSpecification requestSpec = getDefaultRequestSpec(accessToken,"");
		requestSpec.pathParam("id", "NOT_EXIST_ID");
		requestSpec.log().all();
		
		Response response = RestAssured
		.given()
			.spec(requestSpec)
		.expect()
			.statusCode(404).log().all()
		.when()
			.delete(getApiPath()).andReturn();
		
		/* 3. response printing & detail assertions */
		/** STATUS CODE CHANGED @jiraid[DEPPJT-284]	*/
//		JsonPath jsonResponse = new JsonPath(response.asString());
//		assertNotNull(jsonResponse	.get("error"));
//		assertEquals("Bad Request", jsonResponse	.get("error"));
	}
}
