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

package com.ibm.watson.movieapp.dialog.fvt.testcases;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restBase;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restCloseSeq;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restCurrentFuture;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restGenre;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restMovie;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restNlcConfirmSeq;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restNlcDisambiguationSeq;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restOpenSeq;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restRating;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restRepairSeq;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restSmallTalkSeq;
import com.ibm.watson.movieapp.dialog.fvt.testcases.rest.IT_restZipcode;

@RunWith(Suite.class)
@Suite.SuiteClasses({

	IT_restBase.class,
	IT_restCloseSeq.class,
	IT_restCurrentFuture.class,
	IT_restGenre.class,
	IT_restMovie.class,
	IT_restOpenSeq.class,
	IT_restRating.class,
	IT_restRepairSeq.class,
	IT_restSmallTalkSeq.class,
	IT_restZipcode.class,
	IT_restNlcConfirmSeq.class,
	IT_restNlcDisambiguationSeq.class
})

public class Rest_TestSuite {

}

