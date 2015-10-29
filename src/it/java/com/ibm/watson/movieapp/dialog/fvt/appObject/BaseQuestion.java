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

package com.ibm.watson.movieapp.dialog.fvt.appObject;

import static com.jayway.restassured.RestAssured.get;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.ibm.watson.movieapp.dialog.fvt.config.SetupMethod;
import com.ibm.watson.movieapp.dialog.fvt.rest.RestAPI;
import com.ibm.watson.movieapp.dialog.fvt.webui.MovieUI;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class BaseQuestion implements BaseObject{

		private String text;
		private String genre;
		private String timeFrame;
		private String rating;
		private String clientId;
		private String conversationId;
		private BaseResponse response;
		private List<String> expectedResponse;
		private String boundsText;
		private List<WebElement> movies;
		private BaseConversation conversation;
		private boolean partOfConversation;

		public static class Builder {

			private String text;
			private String genre;
			private String timeFrame;
			private String rating;
			private String clientId = "";
			private String conversationId = "";
			private List<String> expectedResponse;
			private String boundsText;
			private BaseConversation conversation;
			private boolean partOfConversation = false;

			public Builder (String text){
				this.text = text;
			}

			public Builder genre(String genre){
				this.genre = genre;
				return this;
			}
	
			public Builder timeFrame(String timeFrame){
				this.timeFrame = timeFrame;
				return this;
			}		
			
			public Builder rating(String rating){
				this.rating = rating;
				return this;
			}
			
			public Builder clientId(String clientId){
				this.clientId = clientId;
				this.partOfConversation = true;
				return this;
			}

			public Builder conversationId(String conversationId){
				this.conversationId = conversationId;
				return this;
			}
			
			public Builder expectedResponse(List<String> expectedResponse){
				this.expectedResponse = expectedResponse;
				return this;
			}
			
			public Builder conversation(BaseConversation conversation){
				this.conversation = conversation;
				return this;
			}
			
			public Builder boundsText(String boundsText){
				this.boundsText = boundsText;
				return this;
			}
			
			public BaseQuestion build() {
				return new BaseQuestion(this);
			}

		}
		
		public BaseQuestion() {
			
		}
		
		private BaseQuestion(Builder b) {
				this.setText(b.text);
				this.setGenre(b.genre);
				this.setTimeFrame(b.timeFrame);
				this.setRating(b.rating);
				this.setClientId(b.clientId);
				this.setConversationId(b.conversationId);
				this.setExpectedResponse(b.expectedResponse);
				this.setBoundsText(b.boundsText);
				this.setConversation(b.conversation);
				this.setPartOfConversation(b.partOfConversation);
		 }
		
		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getGenre() {
			return genre;
		}

		public void setGenre(String genre) {
			this.genre = genre;
		}
		
		public String getTimeFrame() {
			return timeFrame;
		}

		public void setTimeFrame(String timeFrame) {
			this.timeFrame = timeFrame;
		}
		
		public String getRating() {
			return rating;
		}

		public void setRating(String rating) {
			this.rating = rating;
		}
		
		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getConversationId() {
			return conversationId;
		}

		public void setConversationId(String conversationId) {
			this.conversationId = conversationId;
		}
		
		public BaseResponse getResponse() {
			return response;
		}

		public void setResponse(BaseResponse response) {
			this.response = response;
		}
		
		public  List<String> getExpectedResponse() {
			return expectedResponse;
		}
	
		public void setExpectedResponse( List<String>expectedResponse) {
			this.expectedResponse = expectedResponse;
		}
		
		public void setConversation(BaseConversation conversation){
			this.conversation = conversation;
		}

		public BaseConversation getConversation(){
			return conversation;
		}
		
		public void setPartOfConversation(Boolean partOfConversation){
			this.partOfConversation = partOfConversation;
		}
		
		public boolean getPartOfConversation(){
			return partOfConversation;
		}
		
		public String getBoundsText() {
			return boundsText;
		}

		public void setBoundsText(String boundsText) {
			this.boundsText = boundsText;
		}
		
		public List<WebElement> getMovies(){
			return movies;
		}
		
		public void setMovies(List<WebElement> movies){
			this.movies = movies;
		}
		
		/**
		 * ask - Ask question using RestAPI
		 * @param api
		 */
		public void ask(RestAPI api){
		
			api.ask(this);
			
		}

		/**
		 * ask - Ask question using RestAPI
		 * @param api
		 */
		public void ask(MovieUI ui){
			
			ui.ask(this);
			
		}
	
		/**
		 * 
		 * @param ui
		 * @return
		 */
		public List<BaseQuestion> deconQuest(MovieUI ui){

			List<BaseQuestion> questions = new ArrayList<BaseQuestion>();
			
			ArrayList<String> deconText = new ArrayList<String>(
				    					 Arrays.asList("Looking for a movie", this.getTimeFrame(), this.getGenre(), this.getRating()));

			Iterator<String> questIterator = deconText.iterator();
			while (questIterator.hasNext()) {			
				BaseQuestion deconQuest = new BaseQuestion.Builder(questIterator.next())
									    .build();  
				//add question to list of questions for conversation
				questions.add(deconQuest);
				
				//ask question
				deconQuest.ask(ui);			
			}

			return questions;
			
		}
		
		/**
		 * getEleFrmResp - get JSON element from response
		 * @param element
		 * @return
		 */
		public String getEleFrmResp(String element){
	        JsonPath jp = new JsonPath(this.getResponse().getRestAssuredResp().asString());
	        return jp.get(element).toString();
		}
		
		/**
		 * Assign different clientId and conversationId
		 *
		 */				
		public void seperateConver(){			
			Response init = get(RestAPI.initchat);
			JsonPath jp = new JsonPath(init.asString());
			this.setClientId(jp.get(SetupMethod.CLIENTID).toString());
			this.setConversationId(jp.get(SetupMethod.CONVERSATIONID).toString());			
		}
		
		/**
		 * makeQuestion - make question from parts (Genre, Time Frame and Rating)
		 */
		public void makeQuestion(){
			this.setText(this.getGenre() + " " + this.getTimeFrame() + " " + this.getRating());
		}

}
