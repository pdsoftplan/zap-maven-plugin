package br.com.softplan.security.zap.api.exception;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class ZapAnalysisTimeoutExceptionTest {

	@Test
	public void testMessageConstructor() {
		String message = "message";
		try {
			throw new ZapAnalysisTimeoutException(message);
		} catch (ZapAnalysisTimeoutException e) {
			assertEquals(e.getMessage(), message);
		}
	}
	
	@Test
	public void testThrowableConstructor() {
		Throwable throwable = new NullPointerException("test");
		try {
			throw new ZapAnalysisTimeoutException(throwable);
		} catch (ZapAnalysisTimeoutException e) {
			assertEquals(e.getCause(), throwable);
		}
	}
	
	@Test
	public void testMessageAndThrowableConstructor() {
		String message = "message";
		Throwable throwable = new NullPointerException("test");
		try {
			throw new ZapAnalysisTimeoutException(message, throwable);
		} catch (ZapAnalysisTimeoutException e) {
			assertEquals(e.getMessage(), message);
			assertEquals(e.getCause(), throwable);
		}
	}
	
}
