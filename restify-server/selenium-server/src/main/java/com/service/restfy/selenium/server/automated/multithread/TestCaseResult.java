package com.service.restfy.selenium.server.automated.multithread;

public class TestCaseResult {

	private String caseUID = null;
	private String caseName = null;
	private long securityAccessElapsedTime = 0L;
	private long testCaseElapsedTime = 0L;
	private long testActionElapsedTime = 0L;
	private long renderingElapsedTime = 0L;
	private String message = "";
	private boolean success = false;
	private boolean skipped = false;
	public TestCaseResult(String caseUID, String caseName, long securityAccessElapsedTime,
			long testCaseElapsedTime, long testActionElapsedTime,
			long renderingElapsedTime, String message, boolean success,
			boolean skipped) {
		super();
		this.securityAccessElapsedTime = securityAccessElapsedTime;
		this.testCaseElapsedTime = testCaseElapsedTime;
		this.testActionElapsedTime = testActionElapsedTime;
		this.renderingElapsedTime = renderingElapsedTime;
		this.message = message;
		this.success = success;
		this.skipped = skipped;
		this.caseUID=caseUID;
		this.caseName = caseName;
	}
	public TestCaseResult(String caseUID, String caseName) {
		super();
		this.caseUID=caseUID;
		this.caseName = caseName;
	}
	public String getCaseUID() {
		return caseUID;
	}
	public String getCaseName() {
		return caseName;
	}
	public long getSecurityAccessElapsedTime() {
		return securityAccessElapsedTime;
	}
	public long getTestCaseElapsedTime() {
		return testCaseElapsedTime;
	}
	public long getTestActionElapsedTime() {
		return testActionElapsedTime;
	}
	public long getRenderingElapsedTime() {
		return renderingElapsedTime;
	}
	public String getMessage() {
		return message;
	}
	public boolean isSuccess() {
		return success;
	}
	public boolean isSkipped() {
		return skipped;
	}
	public void setSecurityAccessElapsedTime(long securityAccessElapsedTime) {
		if (securityAccessElapsedTime<0)
			securityAccessElapsedTime = 0;
		this.securityAccessElapsedTime = securityAccessElapsedTime;
	}
	public void setTestCaseElapsedTime(long testCaseElapsedTime) {
		if (testCaseElapsedTime<0)
			testCaseElapsedTime = 0;
		this.testCaseElapsedTime = testCaseElapsedTime;
	}
	public void setTestActionElapsedTime(long testActionElapsedTime) {
		if (testActionElapsedTime<0)
			testActionElapsedTime = 0;
		this.testActionElapsedTime = testActionElapsedTime;
	}
	public void setRenderingElapsedTime(long renderingElapsedTime) {
		if (renderingElapsedTime<0)
			renderingElapsedTime = 0;
		this.renderingElapsedTime = renderingElapsedTime;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public void setSkipped(boolean skipped) {
		this.skipped = skipped;
	}
	@Override
	public String toString() {
		return "TestCaseResult [caseUID=" + caseUID + ", caseName=" + caseName
				+ ", securityAccessElapsedTime=" + securityAccessElapsedTime
				+ ", testCaseElapsedTime=" + testCaseElapsedTime
				+ ", testActionElapsedTime=" + testActionElapsedTime
				+ ", renderingElapsedTime=" + renderingElapsedTime
				+ ", message=" + message + ", success=" + success
				+ ", skipped=" + skipped + "]";
	}

}
