package eoe.resttest.appmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eoe.resttest.APITestException;
import eoe.resttest.APITestRuntimeException;
import eoe.resttest.EoEAPITestUtils;
import eoe.resttest.EoEAPITestCase;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 *
 */
public class LoginAsAppManagerTest extends EoEAPITestCase {
	String tempAppManagerId = "service-portal";
	String tempApiKey = "secret";

	public LoginAsAppManagerTest() {
		super();
		setAPIInfo("/oauth/token", "POST");
	}
	
	// TODO EoEAPITestCase 상위 클래스로 리팩토링, 생성자 같은데서 수동 값 세팅하도록 하는 걸로,
//	public String toString() {
//		StringBuffer sb = new StringBuffer();
//		String apiName = "";// (자동) TODO 현재 테스트 클래스 명에서 뒤에 Test 떼고
//		String baseUrl = "";// (자동) TODO RESTAssured. baseurl 값 복사하기
//		String apiPath = "";// (수동) 입력해 주기
//		String method = "POST";// (수동) 입력해 주기
//		String requestBody = "";// (수동) 입력해 주기
//		String requestExample = "";// (수동, 옵션) 입력해 주기, 어차피 실행하면 찍힘
//		String responseExample = "";// (수동, 옵션) 입력해 주기, 어차피 실행하면 찍힘
//		for (int i = 0; i < this.getClass().getMethods().length; i++) {
//			if (this.getClass().getMethods()[i].getName().contains("test")) {
//				// add 테스트 케이스로
//			}
//		}
//		this.getClass().getPackage().getName();
//		this.getClass().getSimpleName();
//		return sb.toString();
//	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoginAsAppManager_존재하는사용자() throws Exception {
		/* 1. request setting */
		String body = "grant_type=client_credentials";
		RequestSpecification requestSpec = getBasicXFormRequestSpec(
				EoEAPITestUtils.generateAuth(tempAppManagerId, tempApiKey), body);
		// request logging(console out )
		requestSpec.log().all();

		/* 2. running & basic assertions */
		Response response = RestAssured.given().spec(requestSpec).expect().log().all().statusCode(200).when().post("/oauth/token")
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
	public void testLoginAsAppManager_존재하지않는사용자() throws Exception {
		String notExistID = "notExist";

		/* 1. request setting */
		String body = "grant_type=client_credentials";
		RequestSpecification requestSpec = getBasicXFormRequestSpec(EoEAPITestUtils.generateAuth(notExistID, tempApiKey),
				body);
		// request logging(console out )
		requestSpec.log().all();

		/* 2. running & basic assertions */
		Response response = RestAssured.given().spec(requestSpec).expect().log().all().statusCode(401).when().post("/oauth/token")
				.andReturn();
		/* 3. response printing & detail assertions */

		JsonPath jsonResponse = new JsonPath(response.asString());
		assertNotNull(jsonResponse.get("error"));
		assertEquals("Unauthorized", jsonResponse.get("error"));
		assertNotNull(jsonResponse.get("status"));
		assertEquals(401, jsonResponse.get("status"));
		assertNotNull(jsonResponse.get("message"));
		assertEquals("Bad credentials", jsonResponse.get("message"));
	}
	
	@Test
	public void testLoginAsAppManager_회원가입후바로로그인시도() throws Exception {
		String tempLoginId = "ra_app_manger";
		String tempLoginPw = "mykey";
		String tempLoginUUID = "344f9a08-11cf-4837-bee9-7b5e151acbec";

		/* 1. request setting */
		String body = "grant_type=client_credentials";
		RequestSpecification requestSpec = getBasicXFormRequestSpec(EoEAPITestUtils.generateAuth(tempLoginId, tempLoginPw),
				body);
		// request logging(console out )
		requestSpec.log().all();

		/* 2. running & basic assertions */
		Response response = RestAssured.given().spec(requestSpec).expect().log().all().statusCode(200).when().post("/oauth/token")
				.andReturn();
		/* 3. response printing & detail assertions */

		JsonPath jsonResponse = new JsonPath(response.asString());
	}

}
