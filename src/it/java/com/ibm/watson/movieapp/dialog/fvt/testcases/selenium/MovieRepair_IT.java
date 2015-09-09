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



public class MovieRepair_IT extends SetupMethod{

    @Rule
    public TestWatcherRule rule = new TestWatcherRule();

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");

	/**
	 *<ul>
	 *<li><B>Info: </B>Ask a movie question that requires more information about genre</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask incomplete movie question</li>
	 *<li><B>Verify: </B>Validate that when missing genre from a question that the user is prompted for desired genre</li>
	 *</ul>
	 */
	@Test
	public void askGenreQuestion() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

	    //pick random question
		BaseQuestion question = Utils.getRandomSample(1, GENRE_QUESTION, true).get(0);

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
	 *<li><B>Info: </B>Ask a movie question that requires more information about current or future</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask incomplete movie question</li>
	 *<li><B>Verify: </B>Validate that if missing when the movie is playing from a question that the user is prompted for current or future</li>
	 *</ul>
	 */
	@Test
	public void askCurFutQuestion() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

	    //pick random question
		BaseQuestion question = Utils.getRandomSample(1, CURNT_FUT_QUESTION, true).get(0);

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
	 *<li><B>Info: </B>Ask a movie question that requires more information about rating</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask incomplete movie question</li>
	 *<li><B>Verify: </B>Validate that when missing rating from a question that the user is prompted for a rating</li>
	 *</ul>
	 */
	@Test
	public void askRatingQuestion() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

	    //pick random question
		BaseQuestion question = Utils.getRandomSample(1, RATING_QUESTION, true).get(0);

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
