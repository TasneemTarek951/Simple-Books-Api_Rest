package Utiles;

import io.qameta.allure.Attachment;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class AllureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        saveFailureLog(result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        saveSkippedLog("Test Skipped: " + result.getName());
    }

    @Attachment(value = "Failure Log", type = "text/plain")
    public String saveFailureLog(String message) {
        return message;
    }

    @Attachment(value = "Skipped Log", type = "text/plain")
    public String saveSkippedLog(String message) {
        return message;
    }

    @Attachment(value = "Test Info", type = "text/plain")
    public String saveTestInfo(String message) {
        return message;
    }

    @Override
    public void onStart(ITestContext context) {
        saveTestInfo("Test Started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        saveTestInfo("Test Finished: " + context.getName());
    }
}
