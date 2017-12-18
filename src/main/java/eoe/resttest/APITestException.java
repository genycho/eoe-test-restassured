package eoe.resttest;


/**
 * 잘못된 요리(법)에 대해 발생시키는 Exception 입니다
 *
 */
public class APITestException extends Exception {

	private static final long serialVersionUID = 1L;

	public APITestException(String internalMessage, Throwable t) {
		super(internalMessage, t);
	}

	public APITestException(String internalMessage) {
		super(internalMessage);
	}

	public APITestException(Exception e2) {
		super(e2);
	}

}
