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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.ibm.watson.movieapp.dialog.fvt.webui.MovieUI;

public class Driver {

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");

public Driver() {
}
 
	/**
	 * Use this parameter for a selenium grid server if left blank use local client for GUI Automation
	 */
	public static String seleniumGridServer = Utils.readConfigProperty("seleniumgrid");
	
	/**
	 * getInstance() 
	 * @return RemoteWebDriver
	 */
	public WebDriver getInstance() {
		
		WebDriver driver = null;

		String browser = Utils.readConfigProperty("browser");

		if(seleniumGridServer.isEmpty()){
			logger.info("INFO: Using local client for GUI Automation");			
			driver = new FirefoxDriver();

		}else{
			try {
				logger.info("INFO: Using selenium grid for GUI Automation");
				DesiredCapabilities capability;
				if (browser.contains("firefox")) {
					capability = DesiredCapabilities.firefox();
				} else if (browser.contains("chrome")) {
					capability = DesiredCapabilities.chrome();
				} else {
					throw new RuntimeException("WebDriver initialisation is not defined for this BrowserType: " + browser);
				}
				driver = new RemoteWebDriver(new URL(seleniumGridServer), capability);			
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		driver.manage().window().maximize();
		return driver;
	
	}

	public MovieUI getGui(WebDriver driver){
		return MovieUI.getGui(driver);
	}
}