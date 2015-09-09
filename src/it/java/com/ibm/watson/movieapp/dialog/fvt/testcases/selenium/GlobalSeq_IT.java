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

public class GlobalSeq_IT extends SetupMethod{

    @Rule
    public TestWatcherRule rule = new TestWatcherRule();

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test repair global sequence question</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask Repair sequence question</li>
	 *<li><B>Verify: </B>Validate that we get an expected response</li>
	 *</ul>
	 */
	@Test
	public void askRepSeqQuestion() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

	    //pick random question
		BaseQuestion question = Utils.getRandomSample(1, REP_SEQ_QUESTION, true).get(0);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		logger.info("INFO: Question - " + question.getText());
		question.ask(ui);
		
    	//Validation
    	//Check response vs expected
        logger.info("INFO: Response - " + question.getResponse().getResponseText());
    	Assert.assertTrue("ERROR: Response - " + question.getResponse().getResponseText() + " does not match any of the expected response.", 
    					  Utils.parseResp(question));
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test out of scope global sequence question</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask Out of Scope sequence question</li>
	 *<li><B>Verify: </B>Validate that we get an expected response</li>
	 *</ul>
	 */
	@Test
	public void askOutScopeQuestion() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

	    //pick random question
		BaseQuestion question = Utils.getRandomSample(1, OUT_SCOPE_QUESTION, true).get(0);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		logger.info("INFO: Question - " + question.getText());
		question.ask(ui);
		
    	//Validation
    	//Check response vs expected
		logger.info("INFO: Response - " + question.getResponse().getResponseText());
    	Assert.assertTrue("ERROR: Response - " + question.getResponse().getResponseText() + " does not match any of the expected response.", 
    					  Utils.parseResp(question));
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test opening global sequence question</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask Opening sequence question</li>
	 *<li><B>Verify: </B>Validate that we get an expected response</li>
	 *</ul>
	 */
	@Test
	public void askOpeningQuestion() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

	    //pick random question
		BaseQuestion question = Utils.getRandomSample(1, OPENING_QUESTION, true).get(0);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		logger.info("INFO: Question - " + question.getText());
		question.ask(ui);
		
    	//Validation
    	//Check response vs expected
        logger.info("INFO: Response - " + question.getResponse().getResponseText());
    	Assert.assertTrue("ERROR: Response - " + question.getResponse().getResponseText() + " does not match any of the expected response.", 
    					  Utils.parseResp(question));
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test closing global sequence question</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask Closing sequence question</li>
	 *<li><B>Verify: </B>Validate that we get an expected response</li>
	 *</ul>
	 */
	@Test
	public void askClosingQuestion() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

	    //pick random question
		BaseQuestion question = Utils.getRandomSample(1, CLOSING_QUESTION, true).get(0);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		logger.info("INFO: Question - " + question.getText());
		question.ask(ui);
		
    	//Validation
    	//Check response vs expected
        logger.info("INFO: Response - " + question.getResponse().getResponseText());
    	Assert.assertTrue("ERROR: Response - " + question.getResponse().getResponseText() + " does not match any of the expected response.", 
    					  Utils.parseResp(question));
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test small talk global sequence question</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask Small Talk sequence question</li>
	 *<li><B>Verify: </B>Validate that we get an expected response</li>
	 *</ul>
	 */
	@Test
	public void askSmallTalkQuestion() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

	    //pick random question
		BaseQuestion question = Utils.getRandomSample(1, SMALL_TALK_QUESTION, true).get(0);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		logger.info("INFO: Question - " + question.getText());
		question.ask(ui);
		
    	//Validation
    	//Check response vs expected
		logger.info("INFO: Response - " + question.getResponse().getResponseText());
    	Assert.assertTrue("ERROR: Response - " + question.getResponse().getResponseText() + " does not match any of the expected response.", 
    					  Utils.parseResp(question));
	}
}
