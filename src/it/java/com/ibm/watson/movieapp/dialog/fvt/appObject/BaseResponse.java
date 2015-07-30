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

import java.util.List;

import com.jayway.restassured.response.Response;

public class BaseResponse implements BaseObject{

	private String responseText;
	private List<String> movieLinks;
	private Response restAssuredResp;
	

	
	public static class Builder {

		private String responseText;
		private List<String> movieLinks;
		private Response restAssuredResp;
		
		public Builder (){

		}
	
		public Builder responseText(String responseText){
			this.responseText = responseText;
			return this;
		}

		public Builder responseMovieLinks(List<String> movieLinks){
			this.movieLinks = movieLinks;
			return this;
		}
		
		public Builder restAssuredResp(Response restAssuredResp){
			this.restAssuredResp = restAssuredResp;
			return this;
		}
		
		public BaseResponse build() {
			return new BaseResponse(this);
		}

	}
	
	public BaseResponse() {
		
	}
	
	private BaseResponse(Builder b) {
			this.setResponseText(b.responseText);
			this.setResponseMovieLinks(b.movieLinks);
			this.setRestAssuredResp(b.restAssuredResp);

	 }
	
	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public List<String> getResponseMovieLinks() {
		return movieLinks;
	}

	public void setResponseMovieLinks(List<String> movieLinks) {
		this.movieLinks = movieLinks;
	}

	public Response getRestAssuredResp() {
		return restAssuredResp;
	}

	public void setRestAssuredResp(Response restAssuredResp) {
		this.restAssuredResp = restAssuredResp;
	}
	
}
