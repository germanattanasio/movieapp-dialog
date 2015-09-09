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

import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseConversation;
import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseQuestion;
import com.ibm.watson.movieapp.dialog.fvt.config.Driver;
import com.ibm.watson.movieapp.dialog.fvt.config.SetupMethod;
import com.ibm.watson.movieapp.dialog.fvt.config.TestWatcherRule;
import com.ibm.watson.movieapp.dialog.fvt.config.Utils;
import com.ibm.watson.movieapp.dialog.fvt.webui.MovieUI;


public class Movie_IT extends SetupMethod{

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
	
    @Rule
    public TestWatcherRule rule = new TestWatcherRule();
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Ask a question then deconstruct the same message and ensure we get the same response</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask a movie question as one question</li>
	 *<li><B>Step: </B>Deconstruct the question and ask the question in pieces</li>
	 *<li><B>Verify: </B>Validate that the two question paths answer the same way</li>
	 *</ul>
	 */
	@Test
	public void deconQuestion() {


		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		      
		MovieUI ui = test.getGui(driver);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		//get a random movie question
		BaseQuestion question = Utils.getRandomSample(1,MOVIE_JSON_FILE, false).get(0);
		BaseConversation conversation = new BaseConversation();
		//ask the question
		question.ask(ui);
	
		//Add the question deconstructed to a conversation
		conversation.setQuestions(question.deconQuest(ui));
		
		//Get the last question of the conversation
		BaseQuestion deconQuestion = conversation.getQuestions().get(3);
 		
    	//Validation
    	//Check question response matches the last of response of the same question deconstructed 
		logger.info("INFO: Validate that you receive the same answer for deconstructed question as you would for the question");
    	Assert.assertTrue("ERROR: Response " + question.getResponse().getResponseText() + " does not match same question deconstructed " + deconQuestion.getResponse().getResponseText(), 
    					deconQuestion.getResponse().getResponseText().contains(question.getResponse().getResponseText()));
	}
}

