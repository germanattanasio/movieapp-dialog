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

package com.ibm.watson.movieapp.dialog.fvt.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseQuestion;
import com.ibm.watson.movieapp.dialog.fvt.rest.RestAPI;

public class Utils {

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");
	
	private static final String GENRE = "genre";
	private static final String TIME = "time";
	private static final String RATING = "rating";
	private static final String EXPECTED = "expected";
	
	/**
	 * Get list of chat questions
	 * @param jsonFile
	 * @return
	 */
	public static ArrayList<BaseQuestion> returnChatList(String jsonFile){
		
		ArrayList<BaseQuestion> questions = new ArrayList<BaseQuestion>();
		RestAPI api = RestAPI.getAPI();		
		questions = api.getChatQuestions(jsonFile);
	    
		return questions;
	}
	
	/**
	 * Get list of movie questions
	 * @param jsonFile
	 * @return
	 */
	public static ArrayList<BaseQuestion> returnMovieList(String jsonFile){
		
		ArrayList<BaseQuestion> questions = new ArrayList<BaseQuestion>();
		RestAPI api = RestAPI.getAPI();
		
		ArrayList<String> genre = api.getMovieOptions(GENRE, jsonFile);
		ArrayList<String> time = api.getMovieOptions(TIME, jsonFile);
		ArrayList<String> rating = api.getMovieOptions(RATING, jsonFile);
		ArrayList<String> expected = api.getMovieOptions(EXPECTED, jsonFile);
		
		Iterator<String> genreList = genre.iterator();
		while (genreList.hasNext()) {

			String genreVal = genreList.next();
			Iterator<String> timeList = time.iterator();

			while (timeList.hasNext()) {
				String timeVal = timeList.next();
				
				Iterator<String> ratingList = rating.iterator();
				while (ratingList.hasNext()) {
					
					String ratingVal = ratingList.next();
					
					BaseQuestion question = new BaseQuestion.Builder("")
															.genre(genreVal)
															.timeFrame(timeVal)
															.rating(ratingVal)
															.expectedResponse(expected)
															.build();  			
					
					//Make question from parts
					question.makeQuestion();
					
					//add question to list of questions
					questions.add(question);
				}			
			}
			
	
			
		}

		return questions;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isIntegrationTest(){
	    if(System.getProperty("automation.full.suite") == null || !System.getProperty("automation.full.suite").contains("false")){
	        return true;
	    }
	    return false;
	}
	
	
	/**
	 * Helper method which returns 5 random questions
	 * @param jsonFile
	 * @param chat
	 * @return
	 */
	public static ArrayList<BaseQuestion> getQuestions(String jsonFile, boolean chat){
	
		RestAPI api = RestAPI.getAPI();	
		ArrayList<BaseQuestion> questions = new ArrayList<BaseQuestion>();
		
		if(isIntegrationTest()){
			//Run all of the restTests
			if(chat){
				questions = api.getChatQuestions(jsonFile);
			}else{
				questions = Utils.returnMovieList(SetupMethod.MOVIE_JSON_FILE);
			}
		}else{
			//Run subset of the restTests
			questions = Utils.getRandomSample(5, jsonFile, chat);
		}
		
	    return questions;
	}
	
	/**
	 * getRandomSample
	 * @param num - number of questions
	 * @param jsonFile - jsonfile to read
	 * @param chat - chat type question
	 * @return
	 */
	public static ArrayList<BaseQuestion> getRandomSample(int num, String jsonFile, boolean chat){
		
		ArrayList<BaseQuestion> questions = new ArrayList<BaseQuestion>();
		ArrayList<BaseQuestion> list;
		int index;
		
		if(chat){
			list = returnChatList(jsonFile);
		} else {
			list = returnMovieList(jsonFile);			
		}
		
		//if the number of questions requested is greater then the number of questions reassign
		if(num > list.size()){
			num = list.size();
		}
		
		//get questions from JSON file
		Random random = new Random();
		while(num > 0){
			index = random.nextInt(list.size() - 1);		
			questions.add(list.get(index));
			num--;
		}
		
		return questions;
	}

	/**
	 * parseResp - Checks to see if the response to the question is in the expected list.
	 * @param question
	 * @return boolean found in list or not.
	 */
	public static boolean parseResp(BaseQuestion question){
		
		boolean found = false;
		
		String response = question.getResponse().getResponseText();
		
		List<String> expectedResponse = question.getExpectedResponse();
		
		Iterator<String> iterator = expectedResponse.iterator();
		while (iterator.hasNext()) {

			String expectResponse = iterator.next();
			logger.info("INFO: Expected response " + expectResponse);
			//if response is contained in the list of expected responses break out of loop and return found
			if(response.contains(expectResponse)){				
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	/**
	 * readConfigProperty - read property from configuration file
	 * @param property
	 * @return
	 */
	public static String readConfigProperty(String property){
		
		Properties prop = new Properties();
		InputStream input = null;
		String propertyVal = "";
		
		try {
		    input = Utils.class.getResourceAsStream("/testConfig.properties");
			// load a properties file
			prop.load(input);
	 
			propertyVal = prop.getProperty(property);
	 
		} catch (Exception ex) {
			logger.error("ERROR: Unable to read property", ex);
		} 
		return propertyVal;
	  }
	
}
