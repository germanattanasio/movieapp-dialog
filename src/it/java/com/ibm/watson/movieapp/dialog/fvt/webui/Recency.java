package com.ibm.watson.movieapp.dialog.fvt.webui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum Recency {

	CURRENT("current"),
	UPCOMING("upcoming");

	String type = null;
		
	Recency(String type){
		this.type=type;
	}

	/**
	 * getType - return String 
	 * @return
	 */
	public String getType(){
		return this.type;
	}
	
	/**
	 * random - return either Recency.CURRENT or Recency.UPCOMING
	 * @return
	 */
	public static Recency random(){
		
		Random random = new Random();
		
		List<Recency> list = new ArrayList<Recency>();
		list.add(CURRENT);
		list.add(UPCOMING);
		
	    int index = random.nextInt(list.size());
	    return list.get(index);
	}

}
