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
import com.ibm.watson.movieapp.dialog.fvt.webui.MovieUI;
import com.ibm.watson.movieapp.dialog.fvt.webui.Recency;

public class Trailer_IT  extends SetupMethod{

    @Rule
    public TestWatcherRule rule = new TestWatcherRule();

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that after selecting a movie that you can close the trailer pane</li>
	 *<li><B>Step: </B>Navigate to landing page</li>
	 *<li><B>Step: </B>Ask question looking for current movies</li>
	 *<li><B>Step: </B>Select the 'X' on the trailer tray to close the tray</li>
	 *<li><B>Verify: </B>Validate that the trailer tray closes</li>
	 *</ul>
	 */
	@Test
	public void closeTrailerPane() {

		Driver test = new Driver();
		WebDriver driver = test.getInstance();
        rule.setDriver(driver);
		      
		MovieUI ui = test.getGui(driver);

		//select Next action
		logger.info("INFO: Select next button");
		ui.selectNextButton();
		
		//ask a question that will return a response with movie data
		BaseConversation conversation = ui.askMovieQuest(Recency.random());

		logger.info(conversation.getQuestions().size());
		
		//Get the last question of the conversation
		BaseQuestion lastQuest = conversation.getQuestions().get(3);
		logger.info("INFO: Last question - " + lastQuest.getMovies().size());
		
		lastQuest.getMovies().get(0).click();
		logger.info("INFO: Movie - " + lastQuest.getMovies().get(0).getText());
		
		//Transition Wait for Preview Pane animation is complete
		ui.fluentWaitNotPresent(MovieUI.previewPaneAnimation);
		
		//Double checkWait for pane to be present
		ui.fluentWaitPresent(MovieUI.previewPane);

		//close preview pane
		ui.clickLinkWait(MovieUI.trailerClose);
		
		//Validation
    	//Check  preview pane disappear
        logger.info("INFO: Validate that the preview pane disappears");
    	Assert.assertTrue("ERROR: Expected that the preview pane disappeared", 
    						ui.fluentWaitNotPresent(MovieUI.previewPane));
	}
}
