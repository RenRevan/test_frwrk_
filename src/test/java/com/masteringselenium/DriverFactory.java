package com.masteringselenium;

import com.masteringselenium.listeners.ScreenshotListener;
import com.masteringselenium.page_objects.AboutUsPage;
import com.masteringselenium.page_objects.IndexPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A factory that holds a pool of driver objects
 * This class is using WebDriverThread
 */
@Listeners(ScreenshotListener.class)
public class DriverFactory {

	// A list to store all instances of WebDriverThread
	// A thread won't be able to modify the list while another thread is currently running a method from this list.
	// The object is locked while processing method.
	private static List<WebDriverThread> webDriverThreadPool = Collections.synchronizedList(new ArrayList<>());
	private static ThreadLocal<WebDriverThread> driverThread;

	//used in tests page objects' variables to not instantiate them in the tests
	protected IndexPage indexPage;
	protected AboutUsPage aboutUsPage;

	/**
	 * Instantiates WebDriverThreads objects,
	 * to isolate each instance of WebDriver during parallel tests running
	 */
	@BeforeSuite
	public static void instantiateDriverObject() {
		driverThread = ThreadLocal.withInitial(() -> {
			WebDriverThread webDriverThread = new WebDriverThread();
			webDriverThreadPool.add(webDriverThread);
			return webDriverThread;
		});
	}

	public static WebDriver getDriver() {
		return driverThread.get().getDriver();
	}

	@AfterMethod
	public static void clearCookies() {
		getDriver().manage().deleteAllCookies();
	}

	@AfterSuite
	public static void closeDriverObjects() {
		for (WebDriverThread webDriverThread : webDriverThreadPool) {
			webDriverThread.quitDriver();
		}
	}

}