package com.sanhua;

import java.util.concurrent.TimeUnit;

public class getToken {
	public static String getTokenFromDb(){
		while(AsyncAccessToken.beingModified == true){

		}
		return AsyncAccessToken.accessToken;
	}
		
}
