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

import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.BaseExpan_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.BaseGUI_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.MovieRepair_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.Bounds_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.Favorite_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.GlobalSeq_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.InsertExpan_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.Movie_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.PostExpan_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.PreExpan_IT;
import com.ibm.watson.movieapp.dialog.fvt.testcases.selenium.Trailer_IT;

@RunWith(Suite.class)
@Suite.SuiteClasses({

	BaseExpan_IT.class,
	BaseGUI_IT.class,
	MovieRepair_IT.class,
	Bounds_IT.class,
	Favorite_IT.class,
	GlobalSeq_IT.class,
	InsertExpan_IT.class,
	Movie_IT.class,
	PostExpan_IT.class,
	PreExpan_IT.class,
	Trailer_IT.class
})

public class GUI_TestSuite {

}

