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
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseQuestion;
import com.ibm.watson.movieapp.dialog.fvt.config.RestAssuredManager;
import com.ibm.watson.movieapp.dialog.fvt.config.SetupMethod;
import com.ibm.watson.movieapp.dialog.fvt.config.API_TestWatcherRule;
import com.ibm.watson.movieapp.dialog.fvt.rest.RestAPI;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;


public class IT_restBase extends SetupMethod {

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
	private ArrayList<BaseQuestion> questions = new ArrayList<BaseQuestion>();
    
	@ClassRule
    public static RestAssuredManager restManager = new RestAssuredManager();

    @Rule
    public API_TestWatcherRule rule = new API_TestWatcherRule();

	@Before
	public void setUpTest(){
		RestAPI api = RestAPI.getAPI();		
		//get questions from JSON file
		questions = api.getChatQuestions(CLOSING_QUESTION);
	}	

	/**
	 *<ul>
	 *<li><B>Info: </B>Ensure that initChat records the clientId in the rest response</li>
	 *<li><B>Step: </B>Connect to Showcase Dialog app using initchat rest api call</li>
	 *<li><B>Verify: </B>Validate that the rest response JSON element clientId is not empty</li>
	 *</ul>
	 */
	@Test
	public void initChatClientId() {

		Response response = 
				RestAssured.given()
						   .contentType(ContentType.JSON)
						   .param("firstTime", "true")
						   .get(RestAPI.initchat)
						   .then()
						   .statusCode(200)
						   .extract()
						   .response();
		
        JsonPath jp = new JsonPath(response.asString());
		
		//Validate that the client id is not empty
		Assert.assertFalse("ERROR: Return clientId is empty",
							jp.get(CLIENTID).toString().isEmpty());
		
	}	

	/**
	 *<ul>
	 *<li><B>Info: </B>Ensure that initChat records the conversationId in the rest response</li>
	 *<li><B>Step: </B>Connect to Showcase Dialog app using initchat rest api call</li>
	 *<li><B>Verify: </B>Validate that the rest response JSON element conversationId is not empty</li>
	 *</ul>
	 */
	@Test
	public void initChatConversationId() {

		Response response = 
				RestAssured.given()
						   .contentType(ContentType.JSON)
						   .param("firstTime", "true")
						   .get(RestAPI.initchat)
						   .then()
						   .statusCode(200)
						   .extract()
						   .response();
		
        JsonPath jp = new JsonPath(response.asString());
		
		//Validate that the client id is not empty
		Assert.assertFalse("ERROR: Return clientId is empty",
							jp.get(CONVERSATIONID).toString().isEmpty());
		
	}	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Ensure proper welcome response from initChat</li>
	 *<li><B>Step: </B>Connect to Showcase Dialog app using initchat rest api call</li>
	 *<li><B>Verify: </B>Validate that the welcome response JSON element welcomeMessage contains the proper response</li>
	 *</ul>
	 */
	@Test
	public void initChatGreeting() {
	
		RestAPI api = RestAPI.getAPI();	
		String greeting = api.getJSONElem("/questions/" + COMMON, "GMessage");
		
		Response response = 
				RestAssured.given()
						   .contentType(ContentType.JSON)
						   .param("firstTime", "true")
						   .get(RestAPI.initchat)
						   .then()
						   .statusCode(200)
						   .extract()
						   .response();
		
        JsonPath jp = new JsonPath(response.asString());
		
		//Validate that the client id is not empty
		Assert.assertTrue("ERROR: Return Response: " + jp.get(WDSRESPONSE).toString() + " does not contain: " + greeting,
							jp.get(WDSRESPONSE).toString().contains(greeting));
		
	}	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Ensure that asking a question in all upper case characters responds the same way as normal case question</li>
	 *<li><B>Step: </B>Connect to Showcase Dialog app using initchat rest api call</li>
	 *<li><B>Step: </B>Choose random question from list of questions read in</li>
	 *<li><B>Step: </B>Ask the random question using getResponse rest api call</li>
	 *<li><B>Step: </B>Ask the random question again but this time using all upper case with getResponse rest api call</li>
	 *<li><B>Verify: </B>Validate that the random normal case character responds the same way when asked in all upper case</li>
	 *</ul>
	 */
	@Test
	public void upperCaseQuestion() {
		BaseQuestion question;
		BaseQuestion upperCaseQuestion;
		String respFromQuest;
		String respFromUpcase;		
		int index;

		//Start
		RestAPI api = RestAPI.getAPI();
		
	    //pick random question
	    index = (int)(Math.random()* questions.size());
	    question = questions.get(index);
	    
	    //ask the question
		logger.info("INFO: Question - " + question.getText());
	    question.ask(api);
	    	    
		logger.info("INFO: Response - " + question.getResponse().getResponseText());

		//get same question
	    upperCaseQuestion = question;
	    
	    //upper case the text of the question
	    upperCaseQuestion.setText(upperCaseQuestion.getText().toUpperCase());
	    
	    //ask upper case question
		logger.info("INFO: Question - " + upperCaseQuestion.getText());
	    upperCaseQuestion.ask(api);

	    respFromQuest = question.getEleFrmResp(WDSRESPONSE); 
	    respFromUpcase = upperCaseQuestion.getEleFrmResp(WDSRESPONSE); 
	    
	    //Validate that the text matches

		logger.info("INFO: Response - " + upperCaseQuestion.getResponse().getResponseText());
	    Assert.assertTrue("ERROR: Response from question " + respFromQuest + " did not match for upper case question " + respFromUpcase,
	    				  respFromQuest.contentEquals(respFromUpcase));
	
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Ensure that asking a question in all lower case characters responds the same way as normal case question</li>
	 *<li><B>Step: </B>Connect to Showcase Dialog app using initchat rest api call</li>
	 *<li><B>Step: </B>Choose random question from list of questions read in</li>
	 *<li><B>Step: </B>Ask the random question using getResponse rest api call</li>
	 *<li><B>Step: </B>Ask the random question again but this time using all lower case with getResponse rest api call</li>
	 *<li><B>Verify: </B>Validate that the random normal case character responds the same way when asked in all upper case</li>
	 *</ul>
	 */
	@Test
	public void lowerCaseQuestion() {
		BaseQuestion question;
		BaseQuestion lowerCaseQuestion;
		String respFromQuest;
		String respFromLowercase;		
		int index;

		//Start
		RestAPI api = RestAPI.getAPI();
		
	    //pick random question
	    index = (int)(Math.random()* questions.size());
	    question = questions.get(index);
	    
	    //ask the question
		logger.info("INFO: Question - " + question.getText());
	    question.ask(api);
	    
		logger.info("INFO: Response - " + question.getResponse().getResponseText());
	    
	    //get same question
	    lowerCaseQuestion = question;
	    
	    //lower case the text of the question
	    lowerCaseQuestion.setText(lowerCaseQuestion.getText().toUpperCase());
	    
	    //ask lower case question
		logger.info("INFO: Question - "+ lowerCaseQuestion.getText());
	    lowerCaseQuestion.ask(api);

	    respFromQuest = question.getEleFrmResp(WDSRESPONSE);
	    respFromLowercase = lowerCaseQuestion.getEleFrmResp(WDSRESPONSE);
	    
	    //Validate that the text matches
		logger.info("INFO: Response - " + lowerCaseQuestion.getResponse().getResponseText());
	    Assert.assertTrue("ERROR: Response from question " + respFromQuest + " did not match for lower case question " + respFromLowercase,
	    				  respFromQuest.contentEquals(respFromLowercase));	
	}	
}
