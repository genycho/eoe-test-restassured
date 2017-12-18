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
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class NaverMovieSearchTest extends AbstractNaverAPITestCase {
	
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
	public void testSearchMovie_200ok() throws Exception {
		RequestSpecBuilder builder = new RequestSpecBuilder();
//		builder.addPathParam("username", "user1");
		builder.addHeader("Accept", "application/json");
		builder.addHeader("Content-Type", "application/json");
		builder.addHeader("X-Naver-Client-Id", super.CLIENT_ID);
		builder.addHeader("X-Naver-Client-Secret", super.CLIENT_SECRET);
		
		RequestSpecification requestSpec = builder.build();
		
		Response response = given().
			spec(requestSpec).
			param("query", "terminator").
		expect().
			statusCode(200).
//			body(equalTo("OK")).
		when().
			get("/search/movie.json").
		thenReturn();
		System.out.println(response.asString());
		//TODO	jsonpath 상으로 검증하는 방법 검색
		JsonPath jsonpath = JsonPath.from(response.asString());
	}
	
}
