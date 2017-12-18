package eoe.resttest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * 
 * //Local로 세팅하고 테스트하는 방법 //1) 테스트 실행 시 Java arguments에 -DTEST_MODE = LOCAL 설정
 * //2) 위의 static 내 변수 주석을 해제 //3) 상위 EoEAPITestCase의 디폴트 값을 현재 DEV 에서 LOCAL로 변경
 *
 */
public abstract class EoEAPITestCase extends AbstractRestAPITestRunner {
	private String apiPath = null;
	private String methodType = null;
	private String requestExample="";
	private String responseExample="";
	
	static {
		System.setProperty("TEST_MODE", "DEV");
		// Local로 세팅하고 테스트하는 방법
		// 1) 테스트 실행 시 Java arguments에 -DTEST_MODE = LOCAL 설정
		// 2) 위의 static 내 변수 주석을 해제
		// 3) 상위 EoEAPITestCase의 디폴트 값을 현재 DEV 에서 LOCAL로 변경
	}

	public EoEAPITestCase() {
		String baseURI = "";
		switch (getTestMode()) {
		case AbstractRestAPITestRunner.TESTMODE_LOCAL:
			baseURI = EoeStaticInfo.LOCAL_BASEURL;
			RestAssured.port = 8080;
			break;
		case AbstractRestAPITestRunner.TESTMODE_DEVSERVER:
			baseURI = EoeStaticInfo.DEV_BASEURL;
			/** 히스토리 기록. SSL 옵션 관련 검색하여 설정 시도 했던 내용들 */
			// RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames());
			// RestAssured.useRelaxedHTTPSValidation();
			// System.setProperty("javax.net.debug", "ssl,handshake");
			// System.setProperty("jsse.enableSNIExtension", "false");

			break;
		case AbstractRestAPITestRunner.TESTMODE_TESTSERVER:
			throw new APITestRuntimeException("Not yew supported test env value - " + getTestMode());
			// break;
		case AbstractRestAPITestRunner.TESTMODE_PRODSERVER:
			throw new APITestRuntimeException("Not yew supported test env value - " + getTestMode());
			// break;
		default:
			baseURI = "https://identity-access-management.herokuapp.com";
			break;
		}
		RestAssured.baseURI = baseURI;
		// RestAssured.port = targetPort;
	}

	/**
	 * 
	 * @param headerAuthorization
	 * @param requestBodyText
	 * @return
	 */
	public RequestSpecification getBasicXFormRequestSpec(String headerAuthorization, String requestBodyText) {
		return EoEAPITestUtils.getBasicXFormRequestSpec(headerAuthorization, requestBodyText);
	}

	public void setAPIInfo(String apiPath, String methodType) {
		this.apiPath = apiPath;
		this.methodType = methodType;
	}
	
	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public RequestSpecification getDefaultRequestSpec(String requestBodyText) {
		return getDefaultRequestSpec(null, requestBodyText);
	}
	
	public void setResponseExample(String responseExample) {
		this.responseExample = responseExample;
	}

	public RequestSpecification getDefaultBasicRequestSpec(String accessToken, String requestBodyText) {
		return EoEAPITestUtils.getDefaultBasicRequestSpec(accessToken, requestBodyText);
	}
	
	/**
	 * eoe 서비스의 기본 요청 스펙을 반환해 주는 메소드 ToRefactoring
	 * 
	 * @param headerAuthorization
	 * @param requestBodyText
	 * @return
	 */
	public RequestSpecification getDefaultRequestSpec(String accessToken, String requestBodyText) {
		return EoEAPITestUtils.getDefaultRequestSpec(accessToken, requestBodyText);
	}
}
