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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SetupMethod {

	protected static Logger logger = LogManager.getLogger("watson.theaters.logger");

	//JSON Elements
	public static final String CONVERSATIONID = "conversationId";
	public static final String WDSRESPONSE = "wdsResponse";
	public static final String CLIENTID = "clientId";
	public static final String EXPECTED = "expected";
	public static final String QUESTION = "question";
	public static final String BOUNDS = "bounds";

	//JSON FILES
	public static final String CURNT_FUT_QUESTION = "currentFuture.json";
	public static final String EXT_LINK = "externalLink.json";
	public static final String GENRE_QUESTION = "genre.json";
	public static final String MOVIE_JSON_FILE = "movie.json";
	public static final String RATING_QUESTION = "rating.json";
	public static final String ZIP_JSON_FILE = "zipcode.json";
	public static final String COMMON = "common.json";

	public static final String REP_SEQ_QUESTION = "globalSeqRepair.json";
	public static final String SMALL_TALK_QUESTION = "globalSeqSmallTalk.json";
	public static final String CLOSING_QUESTION = "globalSeqClose.json";
	public static final String OPENING_QUESTION = "globalSeqOpen.json";
	public static final String OUT_SCOPE_QUESTION = "globalOutScope.json";

	public static final String NLC_CONFIRM_QUESTION = "nlcConfirmSeq.json";
	public static final String NLC_DISAMBIGUATE_QUESTION = "nlcDisambiguationSeq.json";
	
	public static final String CONTEXT_ROOT = "/watson-movieapp-dialog/dist/index.html#/";
	
	
	public SetupMethod() {
		
	}
	
	/**
	 * serverUnderTest() - Check to see if automation is being run via Maven
	 * @return - String containing baseURL to use for testing
	 */
	public static String serverUnderTest(){

		String ipAddress = "";
		String baseURL = "";
		
		String server = System.getProperty("app.url");
		
		if(server==null){		
			baseURL = Utils.readConfigProperty("serverUnderTest");
		}else{
			InetAddress localMachine = null;
			try {
				localMachine = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			ipAddress = localMachine.getHostAddress();
			baseURL = server.replace("localhost", ipAddress); 
		}
		
		return baseURL;
	}
	
	/**
	 * jsonPath - 
	 * @return
	 */
	public static String jsonPath() {
	
		String path = System.getProperty("basedir");

		if(path == null){
	        String userDirectory = FileUtils.getUserDirectoryPath();
	        path = userDirectory + "/watsonBluemix/watson-movieapp-dialog";
		}
		return path;	
	}
	

	

}
