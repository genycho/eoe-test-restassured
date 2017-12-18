package eoe.resttest;

public abstract class AbstractRestAPITestRunner {
//	public static String defaultBaseURL = "";

	public static final int TESTMODE_LOCAL = 0;
	public static final int TESTMODE_DEVSERVER = 1;
	public static final int TESTMODE_TESTSERVER = 2;
	public static final int TESTMODE_PRODSERVER = 3;

	private static int testMode = TESTMODE_DEVSERVER;
	private static boolean isSetTestMode = false;
	
	public void readTestMode(){
//		if(isSetTestMode = false){
			System.out.println("Checking System variable(TEST_MODE) to define the restapi test's request base url. ");
			String testEnvVar = System.getProperty("TEST_MODE");
			if (testEnvVar == null || "".equals(testEnvVar)) {
				System.out.println(
						"Please add the system variable TEST_MODE among these values [ LOCAL / DEV / TEST / PROD ]");
				throw new RuntimeException(
						"Please add the system variable TEST_MODE among these values [ LOCAL / DEV / TEST / PROD ]");
			}
	
			if ("local".equalsIgnoreCase(testEnvVar)) {
				testMode = TESTMODE_LOCAL;
			} else if ("dev".equalsIgnoreCase(testEnvVar)) {
				testMode = TESTMODE_DEVSERVER;
			} else if ("test".equalsIgnoreCase(testEnvVar)) {
				testMode = TESTMODE_TESTSERVER;
			} else if ("prod".equalsIgnoreCase(testEnvVar)) {
				testMode = TESTMODE_PRODSERVER;
			} else {
				System.out.println(
						"Not Defined target server value. It should be the one of [ LOCAL / DEV / TEST / PROD ] but was "
								+ testEnvVar);
				throw new RuntimeException(
						"Not Defined target server value. It should be the one of [ LOCAL / DEV / TEST / PROD ] but was "
								+ testEnvVar);
			}
//		}
		isSetTestMode = true;
	}

	public int getTestMode() {
		if(isSetTestMode == false){
			readTestMode();
		}
		return testMode;
	}

	public void setTestMode(int testMode) {
		AbstractRestAPITestRunner.testMode = testMode;
	}

}
