package com.restassured.testcases;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.restassured.base.TestBase;
import com.restassured.testdata.DataReader;
import com.restassured.listeners.ExtentManager;
import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;

public class RestAssuredExampleTestCases extends TestBase {

    public static String _testCaseName = null;
	public static ExtentTest _extLogger = null;
    public static Logger logger = Logger.getLogger(TestBase.class.getName());
	public static ExtentManager extManager = null;
	public static ExtentReports extent = null;

    public static HashMap<String, HashMap<String, String>> tcData = DataReader.testDataMappedToTestName(prop.getProperty("TestDataExcelFileName"), prop.getProperty("TestDataSheetName"));

    public RestAssuredExampleTestCases(){
        super();
    }

    @BeforeMethod
	public static void setUp(Method method) throws IOException {
		_testCaseName = method.getName();
        extManager = new ExtentManager(extent);
        extent = ExtentManager.getReporter();
		_extLogger = ExtentManager.getLogger(_testCaseName);
	}

    @Test
	public void extractingResponseUsingJsonPath(){
        String request = tcData.get(_testCaseName).get("RequestURL");
        int listSize = Integer.parseInt(tcData.get(_testCaseName).get("ListSize"));

		String jsonResp = 
		given().
			get(request).
		then().
			extract().asString();

		JsonPath jsonPath = new JsonPath(jsonResp).setRoot("RestResponse.result");
		
		List<String> list = jsonPath.get("name");
		assertThat(list.size(), is(listSize));

        logger.info(_testCaseName + "_ Response Details: " + jsonResp);
        logger.info(_testCaseName + "_ Total List Size: " + list.size());
	}

    @Test
    public void verifyResponsesUsingRootJSON(){
        logger.info("Executing: " + _testCaseName );
        given().
                get(tcData.get(_testCaseName).get("RequestURL")).
                then().
                root("RestResponse.result").
                body("name", equalTo(tcData.get(_testCaseName).get("Name"))). // Use equalTo
                body("id", is(Integer.parseInt(tcData.get(_testCaseName).get("Id")))).	 // Instead of using 'equalTo' we can use 'is' also
                body("country", is(tcData.get(_testCaseName).get("Country"))).
                body("area", is(tcData.get(_testCaseName).get("Area"))).
                body("capital", is(tcData.get(_testCaseName).get("Capital"))).
                body("abbr", is(tcData.get(_testCaseName).get("Abbreviation"))).
                body("largest_city", is(tcData.get(_testCaseName).get("LargestCity"))).
                log().all();
    }

    @AfterMethod
    public void getResult(ITestResult result) throws Exception{

        if(result.getStatus() == ITestResult.FAILURE){
            _extLogger.log(Status.FAIL, MarkupHelper.createLabel(result.getName()+" Test case FAILED due to below issues:", ExtentColor.RED));
            _extLogger.fail(result.getThrowable());

        }else if(result.getStatus() == ITestResult.SKIP){
            _extLogger.log(Status.SKIP, MarkupHelper.createLabel(result.getName()+" Test case SKIPPED due to below issues:", ExtentColor.GREY));
            _extLogger.skip(result.getThrowable());

        }else if(result.getStatus() == ITestResult.SUCCESS){
            _extLogger.log(Status.PASS, MarkupHelper.createLabel(result.getName()+" Test case PASSED.", ExtentColor.GREEN));
        }
    }

    @AfterTest
    public void tearDown(){
        extent.flush();
    }
}

