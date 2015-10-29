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
public class IT_restNlcConfirmSeq extends SetupMethod{

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
	private BaseQuestion question;
	
    @ClassRule
    public static RestAssuredManager restManager = new RestAssuredManager();

    @Rule
    public API_TestWatcherRule rule = new API_TestWatcherRule();

	public IT_restNlcConfirmSeq(BaseQuestion quest) {
		this.question = quest;
	}

	@Parameters(name = "NLC confirmation questions")
	public static Iterable<BaseQuestion> data() {

		ArrayList<BaseQuestion> questions = new ArrayList<BaseQuestion>();		
		questions = Utils.getQuestions(NLC_CONFIRM_QUESTION, true);

		return questions;
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Ask a series of questions of each type which will invoke a confirmation from WDS</li>
	 *<li><B>Step: </B>Connect to Showcase Dialog app using initchat rest api call</li>
	 *<li><B>Step: </B>Ask small talk question using getResponse rest api call</li>
	 *<li><B>Verify: </B>Validate that the response is expected response from dialog mct file</li>
	 *</ul>
	 */
	@Test
	public void maintainConversationId() {

		RestAPI api = RestAPI.getAPI();
		
		//Ask questions in separate Conversation thread
		question.seperateConver();

        //Give name->needed because name elicitation sequence does not support certain sequence types.
		String originalQues = question.getText();
		question.setText("John Doe");
		logger.info("Supplying dummy name for the name elicitation sequence.");
		logger.info("INFO: Question - " + question.getText());
        question.ask(api);

        // Ask the question.
        question.setText(originalQues);
        question.setPartOfConversation(true);
        logger.info("Asking the confirmation-type question.");
        logger.info("INFO: Question - " + question.getText());
        question.ask(api);
        
    	//Validation
    	//Check response vs expected
		logger.info("INFO: Response - " + question.getResponse().getResponseText());
    	Assert.assertTrue("ERROR: Response " + question.getResponse().getResponseText() + " does not match expected response.", 
    					Utils.parseResp(question));
	}	
}
