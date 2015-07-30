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

package com.ibm.watson.movieapp.dialog.fvt.webui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import com.google.common.base.Function;



public abstract class BaseUI {

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
	protected WebDriver driver;	
	public String testName;
	public static final int TIMEOUT = 60;
	public static final int POLL = 1;
	
	public BaseUI(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * clickLinkWait - Wait for element then click
	 * @param selector
	 */
	public void clickLinkWait(String locator){
		logger.info("INFO: click action will be performed on selector: " + locator);
		this.fluentWaitPresent(locator);
		this.findElement(locator).click();
		logger.info("INFO: clickLink was performed on: " + locator);
	}

	/**
	 * typeText - Type text into input element
	 * @param selector - the object to type text
	 * @param text - text to be typed
	 */
	public void typeText(String locator, String text){
		findElement(locator).clear();
		findElement(locator).sendKeys(text);
	}

	/**
	 * Finds a WebElement by the given locator string.
	 * @param locator
	 * 			Supported locators:
	 * 			xpath - "//"
	 * 			id - "id="
	 * 			css selector - "css="
	 * 			xpath - "xpath="
	 * @return A WebElement if found by the given locator string
	 */
	public WebElement findElement(String locator) {
		return findElement(byFromLocator(locator));
	}

	/**
	 * Finds a WebElement by the given By locator.
	 * @param by By object containing locator for the element.
	 * @return A WebElement if found by the given By locator
	 */
	public WebElement findElement(By by) {
		return driver.findElement(by);
	}

	/**
	 * Finds all WebElements matching the given locator string.
	 * 		Supported locators:
	 * 			xpath - "//"
	 * 			   id - "id="
	 * 	 css selector - "css="
	 * 			xpath - "xpath="
	 * 	 	 linktext - "link="
	 * 		     name - "name="
	 * @return By object extracted from given string locator
	 */
	public List<WebElement> findElements(String locator) {
		return findElements(byFromLocator(locator));
	}

	/**
	 * Finds all WebElements by the given By locator.
	 * @param by By object containing locator for the element(s).
	 *@return A List<WebElement> containing all matches for the given locator string
	 */
	public List<WebElement> findElements(By by) {
		return driver.findElements(by);
	}

	/**
	 * getElementsText -
	 * @param locator
	 * @return
	 */
	public List<String> getElementsText(String locator){

		List<String> listOfElementsText = new ArrayList<String>();;
		List<WebElement> listOfElements = findElements(locator);

		for(WebElement e : listOfElements) {
			listOfElementsText.add(e.getText());
		}
		return listOfElementsText;

	}

	/**
	 * Converts a locator string with a known prefix to a By object
	 * @param myLocator
	 * 		Supported locators:
	 * 			xpath - "//"
	 * 			   id - "id="
	 * 	 css selector - "css="
	 * 			xpath - "xpath="
	 * 	 	 linktext - "link="
	 * 		     name - "name="
	 *linkpartialtext - "linkpartial="
	 * @return By object extracted from given string locator
	 */
	private static By byFromLocator(String locator) {
		if (locator.startsWith("//")) {
			return By.xpath(locator);
		}
		if (locator.startsWith("id=")) {
			return By.id(locator.replaceFirst("id=", ""));
		}
		if (locator.startsWith("css=")) {
			return By.cssSelector(locator.replaceFirst("css=", ""));
		}
		if (locator.startsWith("xpath=")) {
			return By.xpath(locator.replaceFirst("xpath=", ""));
		}
		if (locator.startsWith("name=")) {
			return By.name(locator.replaceFirst("name=", ""));
		}
		if (locator.startsWith("link=")) {
			return By.linkText(locator.replaceFirst("link=", ""));
		}
		if (locator.startsWith("linkpartial=")) {
			return By.partialLinkText(locator.replaceFirst("linkpartial=", ""));
		}
		throw new IllegalArgumentException("Locator not supported: "
				+ locator);
	}

	/**
	 * useDropdownVisibleText -
	 * @param locator
	 * @param option
	 */
	public void useDropdownVisibleText(String locator, String option) {
		Select select = new Select(findElement(locator));
		select.selectByVisibleText(option);
	}

	/**
	 * isPresent - returns boolean for found web element
	 * @param locator
	 * @see isPresent(WebElement element)
	 * @return
	 */
	public boolean isPresent(String locator) {
		boolean found = false;
        try{
            findElement(locator);
            logger.info("INFO: Element " + locator + " found");
            found = true;
        }catch (NoSuchElementException err){
        	//Ignore as we use isPresent as a 'safe' way to check for an element
        }
        return found;
	}

	/**
	 * isTextPresent - Checks whether the given text is in the current page's HTML
	 * @param Text The text to search for
	 * @return true if it is found
	 */
	public boolean isTextPresent(String text) {
		boolean found = false;
		found = findElement("css=body").getText().contains(text);
		logger.info("INFO: Found '" + text + "' : " + found);
		return found;
	}

	/**
	 * isVisible - Checks whether the given locator matches a visible WebElement
	 * @param locator The locator for the element
	 * @return true if the element is found and visible
	 */
	public boolean isVisible(String locator) {
		boolean found = false;
        try{
            WebElement element = findElement(locator);
            if(element.isDisplayed()){
            	logger.info("INFO: Element " + locator + " found and is visible");
            	found = true;
            }
        }catch (NoSuchElementException err){
        	//Ignore
        }
        return found;
	}

	/**
	 * fluentWaitPresent -
	 * @param locator
	 * @see fluentWaitPresent(final WebElement element)
	 * @return
	 */
	public boolean fluentWaitPresent(final String locator){
		
		logger.info("INFO: Fluentwait for element: " + locator);
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(TIMEOUT, TimeUnit.SECONDS)
                .pollingEvery(POLL, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

    	boolean found = wait.until(new Function<WebDriver, Boolean>() {
    		public Boolean apply(WebDriver driver){
        		return isPresent(locator);
        	}
        });
    	return found;
	};

	/**
	 * fluentWaitWebElementNotPresent -
	 * @param locator
	 * @return
	 */
	public boolean fluentWaitNotPresent(final String locator){
		
		logger.info("INFO: Fluentwait for element to not be present: " + locator);
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(TIMEOUT, TimeUnit.SECONDS)
                .pollingEvery(POLL, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

    	boolean found = wait.until(new Function<WebDriver, Boolean>() {
    		public Boolean apply(WebDriver driver){
    			return !isPresent(locator);
            }
        });
    	return found;
	};

	/**
	 * fluentWaitTextPresent -
	 * @param text
	 * @return
	 */
	public boolean fluentWaitTextPresent(final String text){
		
		logger.info("INFO: FluentWait for text: " + text);
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(TIMEOUT, TimeUnit.SECONDS)
                .pollingEvery(POLL, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

    	boolean found = wait.until(new Function<WebDriver, Boolean>() {
        	public Boolean apply(WebDriver driver){
        			return isTextPresent(text);
        	}
        });
    	return found;
	};

	/**
	 * fluentWaitVisible -
	 * @param locator
	 * @return
	 */
	public boolean fluentWaitVisible(final String locator){

		logger.info("INFO: Fluentwait for visibility of element: " + locator);
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(TIMEOUT, TimeUnit.SECONDS)
                .pollingEvery(POLL, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

    	boolean found = wait.until(new Function<WebDriver, Boolean>() {
        public Boolean apply(WebDriver driver){
            	boolean elementPresent = false;
            	if(!elementPresent){
            		elementPresent = findElement(locator).isDisplayed();
            	}
            	return elementPresent;
            }
        });
    	return found;
	};

	/**
	 * fluentWaitVisibleRefresh -
	 * @param locator
	 * @return
	 */
	public boolean fluentWaitVisibleRefresh(final String locator){

		logger.info("INFO: Fluentwait for visibility of element: " + locator);
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(TIMEOUT, TimeUnit.SECONDS)
                .pollingEvery(POLL, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

    	boolean found = wait.until(new Function<WebDriver, Boolean>() {
        public Boolean apply(WebDriver driver){
            	boolean elementPresent = false;
            	if(!elementPresent){
            		elementPresent = findElement(locator).isDisplayed();
                	driver.navigate().refresh();
            	}
            	return elementPresent;
            }
        });
    	return found;
	};

	/**
	 * fluentWaitAttrChange - Method to wait for attribute change
	 * @param locator - Use .attribute()
	 * @see fluentWaitPresent(final WebElement element)
	 * @return
	 */
	public boolean fluentWaitAttrChange(final String locator, final String attrib, final String value){

		logger.info("INFO: Fluentwait for " + locator + " " + attrib + " to change to " + value);

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(TIMEOUT, TimeUnit.SECONDS)
                .pollingEvery(POLL, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

	    boolean foo = wait.until(new Function<WebDriver, Boolean>() {
	        	public Boolean apply(WebDriver driver){
	        		WebElement element = findElement(locator);
	        		String attribValue = element.getAttribute(attrib);
	        		logger.info("INFO: Current Attribute value: " + attribValue);
	        		return attribValue.contains(value);
	            }
	        });
	        return  foo;
	};


}
