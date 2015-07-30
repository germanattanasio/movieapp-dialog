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

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;


	public class API_TestWatcherRule extends TestWatcher {
		protected static Logger logger = LogManager.getLogger("watson.theaters.logger");

	    public API_TestWatcherRule() {

	    }

	    @Override
	    protected void starting(Description description){
	    	String testName = description.getMethodName();	    	
	    	logger.info("INFO: ********** Beginning of test "+ testName + " at " + new Date() + " **********");  
			logger.info("INFO: Server Under Test " + SetupMethod.serverUnderTest());
	    }
	    

	    @Override
	    protected void finished(Description description) {

	    	String testName = description.getMethodName();	    	
	    	logger.info("INFO: ********** End of test " + testName + " at " + new Date() + " **********");    
	    }

	}
