package eoe.resttest;


/**
 * 잘못된 요리(법)에 대해 발생시키는 Exception 입니다
 *
 */
public class APITestRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public APITestRuntimeException(String internalMessage, Throwable t) {
		super(internalMessage, t);
	}

	public APITestRuntimeException(String internalMessage) {
		super(internalMessage);
	}

	public APITestRuntimeException(Exception e2) {
		super(e2);
	}

}
