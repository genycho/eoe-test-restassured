package eoe.resttest;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class EoEAPITestUtils {
	
	/**
	 * JDK8 has the class java.util.Base64.
	 * 
	 * byte[] message = "hello world".getBytes(StandardCharsets.UTF_8);
	 * String encoded = Base64.getEncoder().encodeToString(message);
	 * byte[] decoded = Base64.getDecoder().decode(encoded);
	 *System.out.println(new String(decoded, StandardCharsets.UTF_8));
	 *
	 * @param message	"hello world".getBytes("UTF-8");
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String encodeWithBase64(String message) throws UnsupportedEncodingException {
		 String encoded = Base64.getEncoder().encodeToString(message.getBytes("UTF-8"));
		 return encoded;
	}
	
	/**
	 * 
	 * @param message	
	 * @return	if you want the string value, refer to this - new String(decoded, "UTF-8")
	 * @throws UnsupportedEncodingException 
	 */
	public static String decodeWithBase64(String message) throws UnsupportedEncodingException {
		byte[] decoded = Base64.getDecoder().decode(message);
		 return new String(decoded, "UTF-8");
	}

	/**
	 * 일반적으로 Authorization은 id + ':' + apiKey로 만들어진다고 함 
	 * @param appManagerId
	 * @param apiKey
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String generateAuth(String appManagerId, String apiKey) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(appManagerId);
		sb.append(":");
		sb.append(apiKey);
		return EoEAPITestUtils.encodeWithBase64(sb.toString());
	}
	
	public static RequestSpecification getBasicXFormRequestSpec(String headerAuthorization, String requestBodyText) {
		RequestSpecBuilder builder = new RequestSpecBuilder();
		builder.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

		if (headerAuthorization != null) {
			builder.addHeader("Authorization", "Basic " + headerAuthorization);
		}

		RequestSpecification requestSpec = builder.build();
		requestSpec.body(requestBodyText);
		return requestSpec;
	}
	
	public static RequestSpecification getDefaultBasicRequestSpec(String accessToken, String requestBodyText) {
		RequestSpecBuilder builder = new RequestSpecBuilder();
		builder.addHeader("Accept", "application/json");
		builder.addHeader("Content-Type", "application/json;charset=UTF-8");

		if (accessToken != null) {
			builder.addHeader("Authorization", "Basic " + accessToken);
		}
		RequestSpecification requestSpec = builder.build();
		requestSpec.body(requestBodyText);
		return requestSpec;
	}
	
	/**
	 * eoe 서비스의 기본 요청 스펙을 반환해 주는 메소드 ToRefactoring
	 * 
	 * @param headerAuthorization
	 * @param requestBodyText
	 * @return
	 */
	public static RequestSpecification getDefaultRequestSpec(String accessToken, String requestBodyText) {
		RequestSpecBuilder builder = new RequestSpecBuilder();
		builder.addHeader("Accept", "application/json");
		builder.addHeader("Content-Type", "application/json;charset=UTF-8");

		if (accessToken != null) {
			builder.addHeader("Authorization", "Bearer " + accessToken);
		}
		RequestSpecification requestSpec = builder.build();
		requestSpec.body(requestBodyText);
		return requestSpec;
	}
	
	public static String getBaseURI(int testMode) {
		String baseURI = "";
		switch (testMode) {
		case AbstractRestAPITestRunner.TESTMODE_LOCAL:
			baseURI = EoeStaticInfo.LOCAL_BASEURL;
			break;
		case AbstractRestAPITestRunner.TESTMODE_DEVSERVER:
			baseURI = EoeStaticInfo.DEV_BASEURL;
			break;
		default:
			baseURI = EoeStaticInfo.DEV_BASEURL;

		}
		return baseURI;
	}
	/**
	 * -1 will be rerurned if the specific port not needed 
	 * @param testMode
	 * @return	
	 */
	public static Integer getPort(int testMode) {
		Integer port  = null;
		switch (testMode) {
		case AbstractRestAPITestRunner.TESTMODE_LOCAL:
			port = 8080;
			break;
		case AbstractRestAPITestRunner.TESTMODE_DEVSERVER:
			port = -1;
			break;
		default:
			port = -1;
		}
		return port;
	}
}
