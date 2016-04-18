package com.service.restfy.selenium.server.automated.multithread;

import java.util.ArrayList;
import java.util.List;

public class UserCaseResult implements Comparable<UserCaseResult> {

	private String processName = "";
	private int processId = 0;
	private String driverName = "";
	private String errorMessage = "";
	private boolean success = true;
	private long elapsedTime = 0L;
	private int caseExecuted = 0;
	private int caseFailed = 0;
	private List<TestCaseResult> testCases = new ArrayList<TestCaseResult>(0);

	public UserCaseResult(int processId, String processName) {
		super();
		this.processName = processName;
		this.processId = processId;
	}
	public String getProcessName() {
		return processName;
	}
	public List<TestCaseResult> getTestCases() {
		return testCases;
	}
	public long getElapsedTime() {
		return elapsedTime;
	}
	public int getCaseExecuted() {
		return caseExecuted;
	}
	public int getCaseFailed() {
		return caseFailed;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public String getDriverName() {
		return driverName;
	}
	public int getProcessId() {
		return processId;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		this.success = false;
	}
	public boolean isSuccess() {
		return success;
	}
	public void addTestCase(TestCaseResult result) {
		if (result!=null)
			testCases.add(result);
	}
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public void setCaseExecuted(int caseExecuted) {
		this.caseExecuted = caseExecuted;
	}
	public void setCaseFailed(int caseFailed) {
		this.caseFailed = caseFailed;
	}
	@Override
	public String toString() {
		return "UserCaseResult [processName=" + processName + ", driverName="
				+ driverName + ", errorMessage=" + errorMessage + ", success="
				+ success + ", elapsedTime=" + elapsedTime + ", caseExecuted="
				+ caseExecuted + ", caseFailed=" + caseFailed + ", testCases="
				+ testCases + "]";
	}
	@Override
	public int compareTo(UserCaseResult o) {
		if (o==null)
			return 1;
		return processId- o.getProcessId();
	}

}
