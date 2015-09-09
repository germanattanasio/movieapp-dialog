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

package com.ibm.watson.movieapp.dialog.fvt.testcases.selenium;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ibm.watson.movieapp.dialog.fvt.config.Driver;
import com.ibm.watson.movieapp.dialog.fvt.config.SetupMethod;
import com.ibm.watson.movieapp.dialog.fvt.config.TestWatcherRule;
import com.ibm.watson.movieapp.dialog.fvt.rest.RestAPI;
import com.ibm.watson.movieapp.dialog.fvt.webui.MovieUI;

public class BaseGUI_IT extends SetupMethod{

    @Rule
    public TestWatcherRule rule = new TestWatcherRule();

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
		
	/**
	 *<ul>
	 *<li><B>Info: </B>Test the terms and conditions link on the landing page</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Verify: </B>Validate the terms and condition link is present</li>
	 *<li><B>Step: </B>Select the terms and condition link</li>
	 *<li><B>Verify: </B>Validate that the page navigated to title equals the expected title</li>
	 *</ul>
	 */
	@Test
	public void termsConditions() {

		RestAPI api = RestAPI.getAPI();	
		String expectedTitle = api.getJSONElem(COMMON, "terms");
		String title = "";
		
		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

		assertTrue("ERROR: Terms of service Link is not present",
					ui.fluentWaitPresent(MovieUI.termsServiceLink));
		
		logger.info("INFO: Select Terms and Conditions link");
		WebElement link = ui.findElement(MovieUI.termsServiceLink);
		link.click();

		title = driver.getTitle();
		
		logger.info("INFO: Title of Terms and Conditions page - " + title);
		
		//Validate that the title of page for Terms of Use matches expected
		assertTrue("ERROR: Title of page - " + title + " does not match expected title of the Terms and Conditions page - " + expectedTitle,
							title.contains(expectedTitle));
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Test the privacy link on the landing page</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Verify: </B>Validate the privacy link is present</li>
	 *<li><B>Step: </B>Select the privacy link</li>
	 *<li><B>Verify: </B>Validate that the page navigated to title equals the expected title</li>
	 *</ul>
	 */
	@Test
	public void privacy() {

		RestAPI api = RestAPI.getAPI();	
		String expectedTitle = api.getJSONElem(COMMON, "privacy");
		String title = "";
		
		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

		assertTrue("ERROR: Privacy Link is not present",
				   ui.fluentWaitPresent(MovieUI.privacyLink));
		
		logger.info("INFO: Select Privacy link");
		WebElement link = ui.findElement(MovieUI.privacyLink);
		link.click();
		
		title = driver.getTitle();
		
		logger.info("INFO: Title of IBM Privacy page - " + title);
		
		//Validate that the title of page for IBM Privacy matches expected
		assertTrue("ERROR: Title of page - " + title + " does not match expected title of the IBM Privacy page - " + expectedTitle,
							title.contains(expectedTitle));		
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that the back ground text message in the user input field matches expected text</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Verify: </B>Validate the back ground text in the user input field matches the expected text</li>
	 *</ul>
	 */
	@Test
	public void chatBoxGreeting() {

		RestAPI api = RestAPI.getAPI();	
		String greeting = api.getJSONElem(COMMON, "chatboxText");
		String chatBoxText = "";
		
		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);

        
		MovieUI ui = test.getGui(driver);
		
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		//wait for loading to complete
		ui.fluentWaitAttrChange(MovieUI.chatBox, "placeholder", greeting);
		
		chatBoxText = ui.findElement(MovieUI.chatBox).getAttribute("placeholder");
		logger.info("INFO: Chatbox text " + chatBoxText);
		
		//Validate that the client id is not empty
		Assert.assertTrue("ERROR: Chatbox greeting " + chatBoxText + " does not match expected greeting " + greeting,
							chatBoxText.contains(greeting));
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B></li>
	 *<li><B>Step: </B></li>
	 *<li><B>Verify: </B></li>
	 *</ul>
	 */
	//@Test
	public void nextButton() {

		RestAPI api = RestAPI.getAPI();	
		String intro = api.getJSONElem(COMMON, "dialogIntro");
		String introText = "";
		
		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);

        
		MovieUI ui = test.getGui(driver);
		//wait for loading to complete
		ui.fluentWaitNotPresent(MovieUI.chatBoxDisabled );
		
		introText = ui.findElement(MovieUI.dialogIntro).getText();
		logger.info("INFO: Dialog intro text " + introText);
		
		//Validate that the client id is not empty
		Assert.assertTrue("ERROR: Dialog Intro " + introText + " does not match expected dialog intro " + intro,
							introText.contains(intro));
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that the dialog introduction text matches expected text</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Verify: </B>Validate the dialog introduction text matches expected text</li>
	 *</ul>
	 */
	@Test
	public void dialogIntro() {

		RestAPI api = RestAPI.getAPI();	
		String intro = api.getJSONElem(COMMON, "dialogIntro");
		String introText = "";
		
		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);

        
		MovieUI ui = test.getGui(driver);
		//wait for loading to complete
		ui.fluentWaitNotPresent(MovieUI.chatBoxDisabled );
		
		introText = ui.findElement(MovieUI.dialogIntro).getText();
		logger.info("INFO: Dialog intro text " + introText);
		
		//Validate that the client id is not empty
		Assert.assertTrue("ERROR: Dialog Intro " + introText + " does not match expected dialog intro " + intro,
							introText.contains(intro));
	}
}
