package com.sanhua;

import com.ufida.eip.core.MessageContext;
import com.ufida.eip.exception.EIPException;
import com.ufida.eip.java.IContextProcessor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.HelpTools.ReadDatabaseConfigurationFile;
import com.ufida.eip.java.IContextProcessor;


public class AsyncAccessToken implements IContextProcessor {
	static String accessToken;
	static boolean beingModified;
	static boolean personUsing = false;
	static boolean deptUsing = false;
	static boolean posiUsing = false;
	static boolean positypeUsing = false;
	public static void getAccessToken() throws Exception{
//		Properties Econfig =
//				ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
//		String appId = Econfig.getProperty("appId");
//    	String appSecret = Econfig.getProperty("appSecret");
		String appId = "jr4gaftwodb";
		String appSecret = "NWE0MTkxNjRjNTll";
    	String url="https://openapi-adel.yunxuetang.cn/token?appId="+appId+"&appSecret="+appSecret;
    	System.out.println(url); 
    	URL uri = new URL(url);
    	beingModified = true;
    	System.out.println("Token updating thread start. ");
    	System.out.println("Waiting for api. ");
    	while(personUsing|deptUsing|posiUsing|positypeUsing){

    	}
    	System.out.println("API done. ");
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(50000);
        connection.setConnectTimeout(100000);
        connection.setDoInput(true); 
		connection.setDoOutput(true); 
        connection.connect(); 
//		Ω” ’
		InputStream is = connection.getInputStream(); 
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
		String strRead;
		StringBuffer sbf = new StringBuffer(); 
		while ((strRead = reader.readLine()) != null) {
			System.out.println(strRead);
			sbf.append(strRead); 
			sbf.append("\r\n"); 
		}
		reader.close();
		connection.disconnect();
		System.out.println("Requesting token");
		JSONObject results = new JSONObject(sbf.toString());
		accessToken=results.getString("accessToken");
//		Econfig.setProperty("accessToken", accessToken);
		beingModified = false;
		System.out.println("Token updating thread done. ");
		System.out.println(accessToken);
    }
	

	@Override
	public String handleMessageContext(MessageContext arg0) throws EIPException {
		try {
			getAccessToken();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		// TODO Auto-generated method stub
		
	}

}
