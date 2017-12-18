package eoe.resttest.sample;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matcher.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class CreatePetStoreUserTest extends AbstractPetStoreAPITestCase {
	private String testUserName = "user1234";
	
	static {
		System.setProperty("TEST_MODE","DEV");
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateUser_200ok() throws Exception {
		JSONObject reqBodyJSon = new JSONObject();
		reqBodyJSon.put("id", 2);
		reqBodyJSon.put("username", "I am Tester");
		reqBodyJSon.put("firstName", "name");
		reqBodyJSon.put("lastName", "My");
		reqBodyJSon.put("email", "abc@def.com");
		reqBodyJSon.put("password", "test1234");
		reqBodyJSon.put("phone", "010-0000-5678");
		reqBodyJSon.put("userStatus", 0);
		
		String tempRequestBodyString = reqBodyJSon.toJSONString();
		System.out.println(reqBodyJSon.toJSONString());
		Response response = 
			given().
				accept("application/json").
				header("Content-Type","application/json").
				body(reqBodyJSon.toJSONString()).
			when().
				post("/user").
			then().
				statusCode(200).
			extract().response();
		System.out.println(response.asString());
	}
	
	private RequestSpecification getDefaultRequestSpec(String requestBodyText){
		RequestSpecBuilder builder = new RequestSpecBuilder();
//		builder.addParam("parameter1", "parameterValue");
		builder.addHeader("Accept", "application/json");
		builder.addHeader("Content-Type", "application/json");
		builder.addHeader("api_key", "special-key");
		
		RequestSpecification requestSpec = builder.build();
		requestSpec.body(requestBodyText);
//		requestSpec.post("/user");
		return requestSpec;
	}
	
	@Test
	public void testCreateUser_200ok_anotherway() throws Exception {
		JSONObject reqBodyJSon = new JSONObject();
		reqBodyJSon.put("id", 2);
		reqBodyJSon.put("username", "I am Tester");
		reqBodyJSon.put("firstName", "name");
		reqBodyJSon.put("lastName", "My");
		reqBodyJSon.put("email", "abc@def.com");
		reqBodyJSon.put("password", "test1234");
		reqBodyJSon.put("phone", "010-0000-5678");
		reqBodyJSon.put("userStatus", 0);
		
		given().
		spec(getDefaultRequestSpec(reqBodyJSon.toJSONString())).
		expect().
			statusCode(200).
			body(equalTo("OK")).
		when().
			post("/user");
	}

}
