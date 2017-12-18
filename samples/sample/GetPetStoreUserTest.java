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

public class GetPetStoreUserTest extends AbstractPetStoreAPITestCase {
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
	public void testGetUser_200ok() throws Exception {
		RequestSpecBuilder builder = new RequestSpecBuilder();
		builder.addPathParam("username", "user1");
		builder.addHeader("Accept", "application/json");
		builder.addHeader("Content-Type", "application/json");
		builder.addHeader("api_key", "special-key");
		
		RequestSpecification requestSpec = builder.build();
		
		given().
			spec(requestSpec).
			param("username", "user1").
		expect().
			statusCode(200).
			body(equalTo("OK")).
		when().
			get("/user/{username}");
	}
	
}
