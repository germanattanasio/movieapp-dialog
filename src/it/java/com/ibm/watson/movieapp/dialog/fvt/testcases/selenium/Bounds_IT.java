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

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseQuestion;
import com.ibm.watson.movieapp.dialog.fvt.config.Driver;
import com.ibm.watson.movieapp.dialog.fvt.config.SetupMethod;
import com.ibm.watson.movieapp.dialog.fvt.config.TestWatcherRule;
import com.ibm.watson.movieapp.dialog.fvt.config.Utils;
import com.ibm.watson.movieapp.dialog.fvt.webui.MovieUI;

public class Bounds_IT extends SetupMethod{

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
	
    @Rule
    public TestWatcherRule rule = new TestWatcherRule();

	/**
	 *<ul>
	 *<li><B>Info: </B>Test that when asking multiple small talk questions the users is redirected to ask a movie question</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask 3 small talk questions</li>
	 *<li><B>Verify: </B>Validate that the users receives a response asking if the user would like to ask a movie question</li>
	 *</ul>
	 */
	@Test
	public void smallTalkBounds() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
        BaseQuestion question = null;      
		MovieUI ui = test.getGui(driver);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		List<BaseQuestion> questions = Utils.getRandomSample(3, SMALL_TALK_QUESTION, true);
		Iterator<BaseQuestion> questionIterator = questions.iterator();
		while (questionIterator.hasNext()) {
			question = questionIterator.next();
			logger.info("INFO: Question - " + question.getText());
			question.ask(ui);			
			
			logger.info("INFO: Response - " + question.getResponse().getResponseText());
		}

    	//Validation
    	//Check response vs expected
		logger.info("INFO: Validate that you receive bounds message");
    	Assert.assertTrue("ERROR: Response " + question.getResponse().getResponseText() + " does not match expected bounds message", 
    					  question.getResponse().getResponseText().contains(question.getBoundsText()));	
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Test that when asking multiple out of scope questions the users is redirected to ask a movie question</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask 3 out of scope questions</li>
	 *<li><B>Verify: </B>Validate that the users receives a response asking if the user would like to ask a movie question</li>
	 *</ul>
	 */
	@Test
	public void outScopeBounds() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
        BaseQuestion question = null;        
		MovieUI ui = test.getGui(driver);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		List<BaseQuestion> questions = Utils.getRandomSample(4, OUT_SCOPE_QUESTION, true);
		Iterator<BaseQuestion> questionIterator = questions.iterator();
		while (questionIterator.hasNext()) {
			question = questionIterator.next();
			logger.info("INFO: Question - " + question.getText());
			question.ask(ui);			
			
			logger.info("INFO: Response - " + question.getResponse().getResponseText());
		}

    	//Validation
    	//Check response vs expected
		logger.info("INFO: Validate that you receive bounds message");
    	Assert.assertTrue("ERROR: Response " + question.getResponse().getResponseText() + " does not match expected bounds message", 
    					  question.getResponse().getResponseText().contains(question.getBoundsText()));	
	}
}

