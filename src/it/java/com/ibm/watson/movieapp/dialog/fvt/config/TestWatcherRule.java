/* Copyright IBM Corp. 2015
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.ibm.watson.movieapp.dialog.fvt.config;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;


	public class TestWatcherRule extends TestWatcher {
		protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
		private WebDriver browser;

	    public TestWatcherRule() {

	    }

	    @Override
	    protected void starting(Description description){
	    	String testName = description.getMethodName();	    	
	    	logger.info("INFO: ********** Beginning of test "+ testName + " at " + new Date() + " **********");  
			logger.info("INFO: Server Under Test " + SetupMethod.serverUnderTest());

	    }
	    
	    @Override
	    protected void failed(Throwable e, Description description) {
	        TakesScreenshot takesScreenshot = (TakesScreenshot) browser;

	        File scrFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
	        File destFile = getDestinationFile(description.getMethodName());
	        try {
	            FileUtils.copyFile(scrFile, destFile);
	        } catch (IOException ioe) {
	            throw new RuntimeException(ioe);
	        }
	    }

	    @Override
	    protected void finished(Description description) {
	        browser.close();
	        browser.quit();
	    	String testName = description.getMethodName();	    	
	    	logger.info("INFO: ********** End of test " + testName + " at " + new Date() + " **********");    
	    }

	    private File getDestinationFile(String fileName) {
	        String userDirectory = FileUtils.getUserDirectoryPath();
	        String absoluteFileName = userDirectory + "/watsonBluemix/watson-movieapp-dialog/target/failsafe-reports-liberty/" + fileName + ".png";
	        return new File(absoluteFileName);
	    }
	    
	    
	    public void setDriver(WebDriver driver){
	    	this.browser = driver;
	    	
	    }
	}
