package eoe.resttest;

import java.util.Date;
import java.util.Random;

public class TestUtils {
	
	/**
	 * This unique value is generated from the currentTimeMilis long type value.
	 * 
	 * @return
	 */
	public static String getUniqueString() {
		long millis = System.currentTimeMillis();
		return String.valueOf(millis);
	}

}
