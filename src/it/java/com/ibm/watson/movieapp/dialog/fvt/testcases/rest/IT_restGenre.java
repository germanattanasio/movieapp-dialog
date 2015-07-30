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

package com.ibm.watson.movieapp.dialog.fvt.testcases.rest;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseQuestion;
import com.ibm.watson.movieapp.dialog.fvt.config.API_TestWatcherRule;
import com.ibm.watson.movieapp.dialog.fvt.config.RestAssuredManager;
import com.ibm.watson.movieapp.dialog.fvt.config.SetupMethod;
import com.ibm.watson.movieapp.dialog.fvt.config.Utils;
import com.ibm.watson.movieapp.dialog.fvt.rest.RestAPI;


@RunWith(value = Parameterized.class)
public class IT_restGenre extends SetupMethod{
    
	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
	private BaseQuestion question;
	
    @ClassRule
    public static RestAssuredManager restManager = new RestAssuredManager();

    @Rule
    public API_TestWatcherRule rule = new API_TestWatcherRule();

	public IT_restGenre(BaseQuestion quest) {
		this.question = quest;
	}

	@Parameters(name = "Genre question list")
	public static Iterable<BaseQuestion> data() {	

		ArrayList<BaseQuestion> questions = new ArrayList<BaseQuestion>();		
		questions = Utils.getQuestions(GENRE_QUESTION, true);

		return questions;
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test generic movie request responds with a probing question about desired genre</li>
	 *<li><B>Step: </B>Connect to Showcase Dialog app using initchat rest api call</li>
	 *<li><B>Step: </B>Ask a question that requests the user to qualify the request with a question about genre</li>
	 *<li><B>Verify: </B>Validate that the response is a probing question (i.e. Do you prefer a particular genre?)</li>
	 *</ul>
	 */
	@Test
	public void genreQuestion() {
		
		RestAPI api = RestAPI.getAPI();
		
		//Ask questions in separate Conversation thread
		question.seperateConver();
		
        //ask question
		logger.info("INFO: Question - " + question.getText());
        question.ask(api);
        
    	//Validation
    	//Check response vs expected
		logger.info("INFO: Response - " + question.getResponse().getResponseText());
    	Assert.assertTrue("ERROR: Response " + question.getResponse().getResponseText() + " does not match expected response.", 
    					Utils.parseResp(question));
	}
}