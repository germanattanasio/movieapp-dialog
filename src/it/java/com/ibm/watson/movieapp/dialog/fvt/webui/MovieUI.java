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

package com.ibm.watson.movieapp.dialog.fvt.webui;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseConversation;
import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseQuestion;
import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseResponse;
import com.ibm.watson.movieapp.dialog.fvt.config.SetupMethod;

public class MovieUI extends BaseUI{

	public MovieUI(WebDriver driver) {
		super(driver);
	}

	public static String chatBox = "css=input[id='question']";
	public static String chatBoxDisabled = "css=input[id='question'][disabled='']";
	public static String lastResponse = "css=div[class='dialog-entry row ng-scope'] > div[class='dialog-watson-row'] > div[class='dialog-segment-bkg ng-scope']";
	public static String movieResponse = "css=a[class='dialog-movie ng-scope'] > span[class='dialog-movie-link']";
	public static String conversationsWeb = "css=div[id='conversationParent'] > div[class='dialog-entry row ng-scope']";
	public static String questionParts = ".dialog-segment-bkg";
	public static String animationActive = "css=.ng-enter-active";
	public static String watsonThinkAnimation = "css=div[class='watson-thinking']";

	public static String trailerHeartSelected = "css=favorite[class='dialog-favorite-sm'] > span[class='dialog-favorite']";
	public static String trailerHeartNotSelected = "css=favorite[class='dialog-favorite-sm'] > span[class='dialog-no-favorite']";
	public static String trailerClose = "css=preview[class='dialog-preview']>div>span[class='dialog-drawer-toggle']";
	
	public static String favListHeartFavorites = "css=favorite-indicator[class='dialog-favorite-left']";
	public static String favListHeartNoFavorites = "css=favorite-indicator[class='dialog-no-favorite-left']";
	public static String leftPanelEmptyFavorite = "css=div[class='dialog-no-favorites'] > span[class='dialog-no-favorites-text']";
	public static String noFavElementHide = "css=div[class='dialog-no-favorites ng-hide']";
	public static String genThumbNailFav = "css=div[class='dialog-favorite-image ng-scope']";
	
	public static String previewPaneAnimation = "css=div[id='preview-parent'][class='dialog-preview-parent ng-scope ng-animate ng-enter ng-enter-active']";
	public static String previewPane = "css=div[id='preview-parent'][class='dialog-preview-parent ng-scope']";
	
	public static String termsServiceLink = "css=a[class='dialog-link'][href='./legal/WhatsInTheaters_TermsOfUse.html']";
	public static String privacyLink = "css=a[class='dialog-link'][href='http://www.ibm.com/privacy/us/en/']";
	public static String dialogIntro = "css=div[class='dialog-welcome']";

	public static String nextButtonLoc = "css=form[class='wform ng-pristine ng-valid'] div[class='dialog-nextbtn']";
	
	/**
	 * askQuestion
	 * @param question
	 */
	public void ask(BaseQuestion question){
		
		BaseResponse resp = new BaseResponse.Builder()
											.build();
		
		//wait for chatbox to be enabled
		fluentWaitNotPresent(chatBoxDisabled);
		
		
		//collect conversation
		List<WebElement> preQuestCount = findElements(conversationsWeb);
		logger.info("INFO: Number of total questions asked in this conversations "+ preQuestCount.size());
		
		//ensure the chatbox exists
		fluentWaitVisible(chatBox);
		WebElement chatbox = findElement(chatBox);
		chatbox.click();
		logger.info("INFO: Sending \"" + question.getText() + "\" to " + chatBox); 
		chatbox.sendKeys(question.getText());	
		chatbox.submit();
		
		//wait for thinking animation has completed
		fluentWaitNotPresent(watsonThinkAnimation);


		//collect questions and wait for new question to be added
		List<WebElement> postQuestCount = findElements(conversationsWeb);		
		while(preQuestCount.size() >= postQuestCount.size()){
			postQuestCount = findElements(conversationsWeb);		
		}
		
		//General check to ensure that we are not waiting on any animations
		fluentWaitNotPresent(animationActive);
		
		logger.info("INFO: Number of total questions asked in this conversations "+ postQuestCount.size());
	
		//collect last question answer
		WebElement thisQuestion = postQuestCount.get(postQuestCount.size()-1);
		List<WebElement> questPart = thisQuestion.findElements(By.cssSelector(questionParts));
		
		//set response to the answer
		resp.setResponseText(questPart.get(1).getText());

		//add response to question
		logger.info("INFO: Adding Response text to Question object");
		question.setResponse(resp);

		//add any movies that are part of the response to question
		List<WebElement> movies =  findElements(movieResponse);
		
		if(movies.size() > 0){
			question.setMovies(movies);
		}
	}

	/**
	 * askMovieQuest - Ask question that will return movies
	 * @param recency (either Recency.CURRENT or Recency.UPCOMING)
	 */
	public BaseConversation askMovieQuest(Recency recency){
		
		BaseConversation conversation = new BaseConversation();
		BaseQuestion question = new BaseQuestion.Builder("looking for a movie")
												.timeFrame(recency.getType())
												.genre("no")
												.rating("no")
												.build();

		//Add the question deconstructed to a conversation and ask the questions
		conversation.setQuestions(question.deconQuest(this));
		
		return conversation;
	}
	
	/**
	 * selectNextButton - 
	 */
	public void selectNextButton(){
		
		fluentWaitVisible(nextButtonLoc);
		WebElement nextButton = findElement(nextButtonLoc);

		nextButton.submit();
	}
	
	
	/**
	 * getFavorites()
	 * @return
	 */
	public List<WebElement> getFavorites(){		
		return findElements(genThumbNailFav);
	}
	
	/**
	 * getDriver()
	 * @return
	 */
	public WebDriver getDriver(){
		return this.driver;
	}
	
	/**
	 * MovieUI 
	 * @param driver
	 * @return
	 */
	public static MovieUI getGui(WebDriver driver){
		driver.get(SetupMethod.serverUnderTest() + SetupMethod.CONTEXT_ROOT);
		return new MovieUI(driver);
	}
}
