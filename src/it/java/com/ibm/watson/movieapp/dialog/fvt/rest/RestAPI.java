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

package com.ibm.watson.movieapp.dialog.fvt.rest;

import static com.jayway.restassured.RestAssured.get;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseQuestion;
import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseResponse;
import com.ibm.watson.movieapp.dialog.fvt.config.SetupMethod;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class RestAPI {

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
	public static String initchat = "/initChat?firstTime=false";
	public static String getResponse = "/postConversation";
	public static String getSelectedMovieDetails = "/getSelectedMovieDetails";
	public static String restURL = "/watson-movieapp-dialog/api/bluemix";
	public static String resourceDir = "/src/it/resources/questions/";

	public RestAPI() {
	}

	/**
	 * 
	 * @param jsonFile
	 * @return
	 */
	public String getFileAsString(String jsonFile){
	
		String fileContents = "";
		try {
		    if(!jsonFile.startsWith(File.separator)){
                jsonFile = File.separator + jsonFile;
            }
            JsonElement jelmnt = new JsonParser().parse(new InputStreamReader(this.getClass().getResourceAsStream(jsonFile)));
			
			fileContents = jelmnt.toString();
		} catch (JsonIOException | JsonSyntaxException e) {
			e.printStackTrace();
		}
		
		return 	fileContents;
	}
	
	/**
	 * getJSONElem - read JSON and find the value associated with the element param
	 * @param jsonFile = JSON to read in string form
	 * @param element - element looking for
	 * @return - String value of element
	 */
	public String getJSONElem(String jsonFile, String element){
		
		String smallTalk = getFileAsString(jsonFile);

        JsonPath jp = new JsonPath(smallTalk);		
		return jp.getString(element).toString();
	}
	
	/**
	 * getJSONArray - read JSON and find the values associated with the element param
	 * @param jsonBody = JSON to read in string form
	 * @param element - element looking for
	 * @return - String value of element
	 */
	public ArrayList<String> getJSONArrayValue(String jsonBody, String field){

		ArrayList<String> values = new ArrayList<String>();
		
		try {
			JsonElement je=new JsonParser().parse(jsonBody);
	        JsonArray inner = je.getAsJsonObject().getAsJsonArray(field);
	            
		    Iterator<JsonElement> innerIter = inner.iterator();
		    while (innerIter.hasNext()){
		       	JsonElement innerEntry = innerIter.next();
		       	values.add(innerEntry.getAsString());
		     }
	            
		} catch (JsonIOException | JsonSyntaxException e) {

			e.printStackTrace();
		}
			
		return values;

	}

	/**
	 * getChatQuestions - Parse JSON file of questions and return an array list of the BaseQuestion object
	 * @param jsonFile - String JSON file name in resources
	 * @return - ArrayList of BaseQuestion objects
	 */
	public ArrayList<BaseQuestion> getChatQuestions(String jsonFile){

		ArrayList<BaseQuestion> questions = new ArrayList<BaseQuestion>();

		try {
			//parse the question json file
		    if(!jsonFile.startsWith(File.separator)){
		        jsonFile = File.separator + jsonFile;
		    }
		    jsonFile = "/questions" + jsonFile;
		        
			JsonElement jelmnt = new JsonParser().parse(new InputStreamReader(this.getClass().getResourceAsStream(jsonFile)));
			JsonArray jarray = jelmnt.getAsJsonObject().getAsJsonArray("questions");	
			
		    //iterate through the list of questions
			for (int i = 0; i < jarray.size(); i++){
				
				ArrayList<String> questOpt = getJSONArrayValue(jarray.get(i).toString(), SetupMethod.QUESTION);
				Iterator<String> questOptIterator = questOpt.iterator();
				while (questOptIterator.hasNext()) {
					
					//place the list of expected response into the question
					List<String> listExpResp = getJSONArrayValue(jarray.get(i).toString(), SetupMethod.EXPECTED);
					
					//Create the question, add question text and expected Response to question
					BaseQuestion question = new BaseQuestion.Builder(questOptIterator.next())
		    											.expectedResponse(listExpResp)
		    											.boundsText(getJSONElem(jsonFile, SetupMethod.BOUNDS))
			   											.build();  

					//add the question to the list of questions
					questions.add(question);
				}

			}
			
		} catch (JsonIOException | JsonSyntaxException e) {
			e.printStackTrace();
		}

		return questions;		
	}

	/**
	 * 
	 * @param type
	 * @param jsonFile
	 * @return
	 */
	public ArrayList<String> getMovieOptions(String type, String jsonFile){
		
		ArrayList<String> values = new ArrayList<String>();
		
		try {
		    if(!jsonFile.startsWith(File.separator)){
                jsonFile = File.separator + jsonFile;
            }
            jsonFile = "/questions" + jsonFile;
            JsonElement jelmnt = new JsonParser().parse(new InputStreamReader(this.getClass().getResourceAsStream(jsonFile)));
			JsonArray jarray = jelmnt.getAsJsonObject().getAsJsonArray(type);
			
			Iterator<JsonElement> questOpt = jarray.iterator();
			while (questOpt.hasNext()) {
				values.add(questOpt.next().getAsString());
			}
			
			
		} catch (JsonIOException | JsonSyntaxException e) {
			e.printStackTrace();
		}
		
		return values;
		
	}
	
	/**
	 * ask - Ask question using RestAPI
	 * @param question
	 */
	public void ask(BaseQuestion question){
		
		//check if question is new or part of conversation
	    if(!question.getPartOfConversation()){
	    	JsonPath initChat = new JsonPath(get(initchat).asString());
	    	question.setClientId(initChat.get(SetupMethod.CLIENTID).toString());
	    	question.setConversationId(initChat.get(SetupMethod.CONVERSATIONID).toString());
	    }
		

	    Response respQuest = 
	    		RestAssured.given()
	    					.header("X-SyncTimeout", "2000")
	    					.param(SetupMethod.CLIENTID , question.getClientId())
	    					.param(SetupMethod.CONVERSATIONID, question.getConversationId() )
	    					.param("input", question.getText())
	    					.param("firstTime", "false")
	    					.get(getResponse)						   
	    					.then()
							.statusCode(200)
							.extract()
							.response();                    

        JsonPath jp = new JsonPath(respQuest.asString());

        //capture response
		BaseResponse response = new BaseResponse.Builder()
												.responseText(jp.get(SetupMethod.WDSRESPONSE).toString())
												.restAssuredResp(respQuest)
												.build();
        //add response to question
		question.setResponse(response);
	}

	/**
	 * getAPI - instantiate an instance of RestAPI
	 * @return
	 */
	public static RestAPI getAPI(){
		return new RestAPI();

	}

}
