package eoe.resttest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class APITestUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBase64EncodeDecode()  throws Exception {
		String plainText = "service-portal:secret";
		
		String encoded = EoEAPITestUtils.encodeWithBase64(plainText);
		assertEquals("c2VydmljZS1wb3J0YWw6c2VjcmV0", encoded);
		
		String decoded = EoEAPITestUtils.decodeWithBase64(encoded);
		assertEquals(plainText, decoded);
	}

}
