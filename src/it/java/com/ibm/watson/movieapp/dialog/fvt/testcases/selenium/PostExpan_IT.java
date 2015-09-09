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


public class PostExpan_IT extends SetupMethod{

    @Rule
    public TestWatcherRule rule = new TestWatcherRule();

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
    
	/**
	 *<ul>
	 *<li><B>Info: </B>Ask an expansion questions before asking a movie question</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask movie question</li>
	 *<li><B>Verify: </B>Validate that we receive expected response from movie questions</li>
	 *<li><B>Step: </B>Ask expansion (chat) question</li>
	 *<li><B>Verify: </B>Validate that we receive expected response from chat questions</li>
	 *</ul>
	 */
	@Test
	public void askPostExpansion() {
		
		BaseQuestion chatQuestion = Utils.getRandomSample(1, CLOSING_QUESTION, true).get(0);
		List<BaseQuestion> movieQuestions = Utils.getRandomSample(3, MOVIE_JSON_FILE, false);
		
		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		
		MovieUI ui = test.getGui(driver);

		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		logger.info("INFO: Question - " + chatQuestion.getText());
		chatQuestion.ask(ui);
		
		//Validation
    	//Check response vs expected
        logger.info("INFO: Response - " + chatQuestion.getResponse().getResponseText());
    	Assert.assertTrue("ERROR: Response - " + chatQuestion.getResponse().getResponseText() + " does not match any of the expected response.", 
    					  Utils.parseResp(chatQuestion));
		
		Iterator<BaseQuestion> questionIterator = movieQuestions.iterator();
		while (questionIterator.hasNext()) {
			BaseQuestion question = questionIterator.next();
			
			logger.info("INFO: Question - " + question.getText());
			question.ask(ui);	
			
			//Validation
	    	//Check response vs expected
	        logger.info("INFO: Response - " + question.getResponse().getResponseText());
	    	Assert.assertTrue("ERROR: Response - " + question.getResponse().getResponseText() + " does not match any of the expected response.", 
	    					  Utils.parseResp(question));			
		}
	}
}
