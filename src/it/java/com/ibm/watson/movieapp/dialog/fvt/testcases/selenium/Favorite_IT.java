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

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseConversation;
import com.ibm.watson.movieapp.dialog.fvt.appObject.BaseQuestion;
import com.ibm.watson.movieapp.dialog.fvt.config.Driver;
import com.ibm.watson.movieapp.dialog.fvt.config.SetupMethod;
import com.ibm.watson.movieapp.dialog.fvt.config.TestWatcherRule;
import com.ibm.watson.movieapp.dialog.fvt.webui.MovieUI;
import com.ibm.watson.movieapp.dialog.fvt.webui.Recency;

public class Favorite_IT extends SetupMethod{

    @Rule
    public TestWatcherRule rule = new TestWatcherRule();

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that a user can favorite a movie by selecting the heart icon on the trailer tray</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask a series of questions looking for current movies</li>
	 *<li><B>Step: </B>Open a movie from the returned list by selecting the movie in the response</li>
	 *<li><B>Step: </B>Favorite movie by selecting the heart from the trailer tray</li>
	 *<li><B>Verify: </B>Validate that the web element in the trailer tray changes to favorite</li>
	 *</ul>
	 */
	@Test
	public void favMovieTrailerHeart() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		      
		MovieUI ui = test.getGui(driver);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		//ask a question that will return a response with movie data
		BaseConversation conversation = ui.askMovieQuest(Recency.random());
		
		//Get the last question of the conversation
		BaseQuestion lastQuest = conversation.getQuestions().get(3);
		logger.info("INFO: Last question - " + lastQuest.getMovies().size());
		
		lastQuest.getMovies().get(0).click();
		logger.info("INFO: Movie - " + lastQuest.getMovies().get(0).getText());
		
		//Transition Wait for Preview Pane animation is complete
		ui.fluentWaitNotPresent(MovieUI.previewPaneAnimation);
		
		//Double checkWait for pane to be present
		ui.fluentWaitPresent(MovieUI.previewPane);

		ui.clickLinkWait(MovieUI.trailerHeartNotSelected);
		
		//validation
		//Heart change to favorite
		assertTrue("ERROR: Expected heart to change when user selects heart in order to favorite a movie",
				ui.fluentWaitPresent(MovieUI.trailerHeartSelected));
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that a user can unfavorite a favorite movie by selecting the heart icon on the trailer tray</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask a series of questions looking for current movies</li>
	 *<li><B>Step: </B>Open a movie from the returned list by selecting the movie in the response</li>
	 *<li><B>Step: </B>Favorite movie by selecting the heart from the trailer tray</li>
	 *<li><B>Verify: </B>Validate that the web element  in the trailer tray changes to favorite</li>
	 *<li><B>Step: </B>Unfavorite movie by selecting the heart from the trailer tray</li>
	 *<li><B>Verify: </B>Validate that the web element in the trailer tray changes back to non favorite</li>
	 *</ul>
	 */
	@Test
	public void unfavMovieTrailerHeart() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		      
		MovieUI ui = test.getGui(driver);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		//ask a question that will return a response with movie data
		BaseConversation conversation = ui.askMovieQuest(Recency.random());
		
		//Get the last question of the conversation
		BaseQuestion lastQuest = conversation.getQuestions().get(3);
		logger.info("INFO: Last question - " + lastQuest.getMovies().size());
		
		lastQuest.getMovies().get(0).click();
		logger.info("INFO: Movie - " + lastQuest.getMovies().get(0).getText());
		
		//Transition Wait for Preview Pane animation is complete
		ui.fluentWaitNotPresent(MovieUI.previewPaneAnimation);
		
		//Double checkWait for pane to be present
		ui.fluentWaitPresent(MovieUI.previewPane);

		ui.clickLinkWait(MovieUI.trailerHeartNotSelected);
		
		//change to favorite
		ui.fluentWaitPresent(MovieUI.trailerHeartSelected);
		
		ui.clickLinkWait(MovieUI.trailerHeartSelected);
		
		//validate
		//change to not a favorite
		assertTrue("ERROR: Expected heart to change back to empty when user selects heart in order to unfavorite a movie",
				   ui.fluentWaitPresent(MovieUI.trailerHeartNotSelected));
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that when a user favorites a movie the heart icon on the right side changes</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask a series of questions looking for current movies</li>
	 *<li><B>Step: </B>Open a movie from the returned list by selecting the movie in the response</li>
	 *<li><B>Step: </B>Favorite movie by selecting the heart from the trailer tray</li>
	 *<li><B>Verify: </B>Validate that the web element in the above the favorite list on the right hand side changes to favorite</li>
	 *</ul>
	 */
	@Test
	public void favMovieRightHeart() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		      
		MovieUI ui = test.getGui(driver);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		//ask a question that will return a response with movie data
		BaseConversation conversation = ui.askMovieQuest(Recency.random());
		
		//Get the last question of the conversation
		BaseQuestion lastQuest = conversation.getQuestions().get(3);
		logger.info("INFO: Last question - " + lastQuest.getMovies().size());
		
		lastQuest.getMovies().get(0).click();
		logger.info("INFO: Movie - " + lastQuest.getMovies().get(0).getText());
		
		//Transition Wait for Preview Pane animation is complete
		ui.fluentWaitNotPresent(MovieUI.previewPaneAnimation);
		
		//Double checkWait for pane to be present
		ui.fluentWaitPresent(MovieUI.previewPane);

		ui.clickLinkWait(MovieUI.trailerHeartNotSelected);
		
		//validation
		//left side menu Heart change to favorite
		assertTrue("ERROR: Expected heart to change over favorite list when user selects heart in order to favorite a movie",
				ui.fluentWaitPresent(MovieUI.favListHeartFavorites));
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that a user can unfavorite all favorites that the heart icon changes back</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask a series of questions looking for current movies</li>
	 *<li><B>Step: </B>Open a movie from the returned list by selecting the movie in the response</li>
	 *<li><B>Step: </B>Favorite movie by selecting the heart from the trailer tray</li>
	 *<li><B>Verify: </B>Validate that the web element in the above the favorite list on the right hand side changes to favorite</li>
	 *<li><B>Step: </B>UnFavorite movie by selecting the heart from the trailer tray</li>
	 *<li><B>Verify: </B>Validate that the web element in the above the favorite list on the right hand side changes back to no favorites</li>
	 *</ul>
	 */
	@Test
	public void unfavMovieRightHeart() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		      
		MovieUI ui = test.getGui(driver);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		//ask a question that will return a response with movie data
		BaseConversation conversation = ui.askMovieQuest(Recency.random());
		
		//Get the last question of the conversation
		BaseQuestion lastQuest = conversation.getQuestions().get(3);
		logger.info("INFO: Last question - " + lastQuest.getMovies().size());
		
		lastQuest.getMovies().get(0).click();
		logger.info("INFO: Movie - " + lastQuest.getMovies().get(0).getText());
		
		//Transition Wait for Preview Pane animation is complete
		ui.fluentWaitNotPresent(MovieUI.previewPaneAnimation);
		
		//Double checkWait for pane to be present
		ui.fluentWaitPresent(MovieUI.previewPane);

		ui.clickLinkWait(MovieUI.trailerHeartNotSelected);
		
		//change to favorite
		ui.fluentWaitPresent(MovieUI.favListHeartFavorites);
		
		ui.clickLinkWait(MovieUI.trailerHeartSelected);
		
		//validate
		//left side menu Heart change to not a favorite
		assertTrue("ERROR: Expected favorite list on right goes back to no favorites when removing all from list ",
					ui.fluentWaitPresent(MovieUI.favListHeartNoFavorites));
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that when a user favorites a movie that the ThumbNail is added to the favorites menu</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask a series of questions looking for current movies</li>
	 *<li><B>Step: </B>Favorite movie by selecting the heart from the trailer tray</li>
	 *<li><B>Verify: </B>Validate that a Favorite ThumbNail for the movie is added to the favorites list</li>
	 *</ul>
	 */
	@Test
	public void favMovieLeftThumbNail() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		      
		MovieUI ui = test.getGui(driver);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		//ask a question that will return a response with movie data
		BaseConversation conversation = ui.askMovieQuest(Recency.random());
		
		//Get the last question of the conversation
		BaseQuestion lastQuest = conversation.getQuestions().get(3);
		logger.info("INFO: Last question - " + lastQuest.getMovies().size());
		
		lastQuest.getMovies().get(0).click();
		logger.info("INFO: Movie - " + lastQuest.getMovies().get(0).getText());
		
		//check to see that there are no thumb nail in the menu
		ui.fluentWaitPresent(MovieUI.leftPanelEmptyFavorite);
		
		//Transition Wait for Preview Pane animation is complete
		ui.fluentWaitNotPresent(MovieUI.previewPaneAnimation);
		
		//Double checkWait for pane to be present
		ui.fluentWaitPresent(MovieUI.previewPane);

		ui.clickLinkWait(MovieUI.trailerHeartNotSelected);
		
		//validation
		//that no favorite element hide
		ui.fluentWaitPresent(MovieUI.noFavElementHide);
		
		//validate that there is one favorite thumb nail
		List<WebElement> thumbNail = ui.findElements(MovieUI.genThumbNailFav);	
		assertTrue("ERROR: Expected one thumbnail image in left side bar but received " + thumbNail.size(),
				   thumbNail.size()==1);
	}	
}
