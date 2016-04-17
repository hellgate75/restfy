package com.service.restfy.selenium.server.cases;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.service.restfy.selenium.server.exceptions.FrameworkException;

public class TestEngine {
	static {
		if (System.getProperty("log4j.configurationFile")==null)
			System.setProperty("log4j.configurationFile", "log4j2.xml");
	}
	private static Logger logger = LoggerFactory.getLogger("com.service.restfy.selenium.server");
	private static final String REPORT_LINE_SEPARATOR = "--------------------------------------------------------------------------------------";
	
	private WebDriver driver = null;
	private List<TestCase> caseList = new ArrayList<TestCase>(0); 
	private Map<String, String> caseMessages = new HashMap<String, String>(0); 
	private Map<String, Boolean> caseResponseStatus = new HashMap<String, Boolean>(0); 
	private int caseExecuted = 0;
	private int caseFailed = 0;
	private boolean traceRunOnLogger = true;

	public TestEngine() {
		super();
	}

	public TestEngine(WebDriver driver) {
		super();
		this.driver=driver;
	}
	
	public TestEngine(WebDriver driver, boolean traceRunOnLogger) {
		super();
		this.driver = driver;
		this.traceRunOnLogger = traceRunOnLogger;
	}

	public void addCase(TestCase testCase) {
		if (testCase!=null && !caseList.contains(testCase)) {
			caseList.add(testCase);
		}
	}

	public boolean isTraceRunOnLogger() {
		return traceRunOnLogger;
	}

	public void setTraceRunOnLogger(boolean traceRunOnLogger) {
		this.traceRunOnLogger = traceRunOnLogger;
	}

	public WebDriver getWebDriver() {
		return driver;
	}

	public void setWebDriver(WebDriver driver) {
		this.driver = driver;
	}

	public void addCaseByClassName(String className) throws FrameworkException {
		try {
			TestCase testCase = (TestCase)(Class.forName(className).newInstance());
			if (testCase!=null && !caseList.contains(testCase)) {
				caseList.add(testCase);
			}
		} catch (Throwable e) {
			error("Error loading class : " + className, e);
			throw new FrameworkException("Error loading class : " + className, e);
		}
	}

	public void addCaseByPackageName(String packageName) throws FrameworkException {
		try {
			Reflections reflections = new Reflections(packageName);
			Set<Class<? extends TestCase>> classes = reflections.getSubTypesOf(TestCase.class);
			for(Class<? extends TestCase> classMask: classes) {
				TestCase testCase = classMask.newInstance();
				if (testCase!=null && !caseList.contains(testCase)) {
					caseList.add(testCase);
				}
			}
		} catch (Throwable e) {
			error("Error loading package : " + packageName, e);
			throw new FrameworkException("Error loading package : " + packageName, e);
		}
	}
	
	public void clearCaseList() {
		caseList.clear();
	}
	
	public int getCaseNumber() {
		return caseList.size();
	}
	
	protected final void info(String message) {
		if (this.traceRunOnLogger) {
			logger.info(message);
		}
	}
	
	protected final void error(String message, Throwable exception) {
		if (this.traceRunOnLogger) {
			logger.error(message, exception);
		}
	}
	
	public void run() throws Throwable {
		caseExecuted = 0;
		caseFailed = 0;
		caseMessages.clear();
		caseResponseStatus.clear();
		for(TestCase t: caseList) {
			caseExecuted++;
			if (this.traceRunOnLogger)
				info("Executing test case [UID:"+t.getCaseUID()+"] name : " + t.getCaseName());
			try {
				if (t.connectToURL()) {
					this.driver.get(t.getCaseURL());
				}
				t.automatedTest(this.driver);
				info("Executed test case [UID:"+t.getCaseUID()+"] name : " + t.getCaseName());
				caseMessages.put(t.getCaseUID(), "[SUCCESS]: Test Case '"+t.getCaseName()+"' executed correctly.");
				caseResponseStatus.put(t.getCaseUID(), true);
			} catch (Throwable e) {
				caseFailed++;
				caseMessages.put(t.getCaseUID(), "[FAIL]: Test Case '"+t.getCaseName()+"' failed due to: "+ e.getMessage());
				caseResponseStatus.put(t.getCaseUID(), false);
				if(t.rethrowException())
					throw e;
				else
					error("Failure of test case : " + t.getCaseName(), e);
			}
		}
	}
	
	public void report(PrintStream ps) {
		if (caseExecuted==0)
			return;
		int skipped = 0;
		ps.println(REPORT_LINE_SEPARATOR);
		ps.println("Test Engine Report - web driver used : " + this.driver.getClass().getName());
		ps.println(REPORT_LINE_SEPARATOR);
		for(int i=0; i<this.getCaseNumber();i++) {
			TestCase testCase = caseList.get(i);
			String message = caseMessages.get(testCase.getCaseUID());
			if (message!=null) {
				ps.println("Case " + (i+1) + " - " + message);
			}
			else {
				ps.println("Case " + (i+1) + " - [SKIPPED]: Test Case '"+testCase.getCaseName()+"' skipped in last execution.");
				skipped++;
			}
		}
		ps.println(REPORT_LINE_SEPARATOR);
		ps.println("Total Cases :  "+this.getCaseNumber()+"  Executed : " + this.caseExecuted + "  Skipped : " + skipped + "  Success : " + this.getCaseSecceded() + "  Failed : " + caseFailed);
		ps.println(REPORT_LINE_SEPARATOR);
	}

	public String jsonReport() {
		if (caseExecuted==0)
			return "{"
					+ "\"driver\": \"" + this.driver.getClass().getName()+"\","
					+ "\"cases\": " + this.getCaseNumber()+","
					+ "\"executed\": " + this.caseExecuted+","
					+ "\"skipped\": " + 0 +","
					+ "\"failed\": " + this.caseFailed+","
					+ "\"success\": " + this.getCaseSecceded()+", \"casesResponse\": []}";
		int skipped = 0;
		String cases = "";
		for(int i=0; i<this.getCaseNumber();i++) {
			TestCase testCase = caseList.get(i);
			String message = caseMessages.get(testCase.getCaseUID());
			if (message!=null) {
				cases += (i>0 ? ", " : "") + "{"
						+"\"caseName\" : \"" + testCase.getCaseName() + "\","
						+"\"success\" : " + caseResponseStatus.get(testCase.getCaseUID()) + ","
						+"\"skipped\" : false,"
						+"\"message\" : \"" + caseMessages.get(testCase.getCaseUID()) + "\""
						+ "}";
			}
			else {
				cases += (i>0 ? ", " : "") + "{"
						+"\"caseName\" : \"" + testCase.getCaseName() + "\","
						+"\"success\" : " + caseResponseStatus.get(testCase.getCaseUID()) + ","
						+"\"skipped\" : true,"
						+"\"message\" : \"[SKIPPED]: Test Case '"+testCase.getCaseName()+"' skipped in last execution.\""
						+ "}";
				skipped++;
			}
		}
		return "{"
				+ "\"driver\": \"" + this.driver.getClass().getName()+"\","
				+ "\"cases\": " + this.getCaseNumber()+","
				+ "\"executed\": " + this.caseExecuted+","
				+ "\"skipped\": " + skipped +","
				+ "\"failed\": " + this.caseFailed+","
				+ "\"success\": " + this.getCaseSecceded()+","
				+ "\"casesResponse\": ["+cases+"]}";
	}

	public int getCaseExecuted() {
		return caseExecuted;
	}

	public int getCaseSecceded() {
		return caseExecuted-caseFailed;
	}

	public int getCaseFailed() {
		return caseFailed;
	}
	

}
