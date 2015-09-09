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

public class BaseConversation implements BaseObject{

	private List<BaseQuestion> questions;
	private String clientId;
	private String conversationId;
	private String userName;
	
	public static class Builder {

		private List<BaseQuestion> questions;
		private String clientId;
		private String conversationId;
		private String userName;
		
		public Builder (String userName){
			this.userName = userName;
		}

		public Builder questions(List<BaseQuestion> questions){
			this.questions = questions;
			return this;
		}
		
		public Builder clientId(String clientId){
			this.clientId = clientId;
			return this;
		}

		public Builder conversationId(String conversationId){
			this.conversationId = conversationId;
			return this;
		}
		
		
		public BaseConversation build() {
			return new BaseConversation(this);
		}

	}
	
	public BaseConversation() {
		
	}
	
	private BaseConversation(Builder b) {
			this.setQuestions(b.questions);
			this.setClientId(b.clientId);
			this.setConverationId(b.conversationId);
			this.setUserName(b.userName);

	 }
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public List<BaseQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<BaseQuestion> questions) {
		this.questions = questions;
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

	public void setConverationId(String conversationId) {
		this.conversationId = conversationId;
	}
	
	public void start(){

	}
	
}
