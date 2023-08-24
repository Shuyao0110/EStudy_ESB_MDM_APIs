package com.sanhua;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.HelpTools.ReadDatabaseConfigurationFile;

import com.ufida.eip.core.MessageContext;
import com.ufida.eip.exception.EIPException;
import com.ufida.eip.java.IContextProcessor;

import commonj.sdo.DataObject;

public class positiontypeAPI implements IContextProcessor {
	
//	根据appid和appsecret查询accessToken
	
	public String getAccessToken() throws Exception{
		Properties EXUEconfig =
				ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
		String appId = EXUEconfig.getProperty("appId");
    	String appSecret = EXUEconfig.getProperty("appSecret");
		
    	String url="https://openapi-adel.yunxuetang.cn/token?appId="+appId+"&appSecret="+appSecret;
    	System.out.println(url); 
    	URL uri = new URL(url);
    
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(50000);
        connection.setConnectTimeout(100000);
        connection.setDoInput(true); 
		connection.setDoOutput(true); 
		connection.setRequestProperty("appId", appId);
		connection.setRequestProperty("appSecret", appSecret);
        connection.connect(); 
//		接收
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
		JSONObject results = new JSONObject(sbf.toString());
		String accessToken=results.getString("accessToken");
		System.out.println(accessToken);
    	return accessToken;
    }
	
//	根据appid和appsecret查询accessTokenbyTime
	
	public String getAccessTokenbyTime() throws Exception{
		Properties EXUEconfig =
				ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
		String appId = EXUEconfig.getProperty("appId");
    	String appSecret = EXUEconfig.getProperty("appSecret");
    	String accesstoken = EXUEconfig.getProperty("accessToken");
    	String string_time=EXUEconfig.getProperty("time");	
//		String appId="jr4gaftwodb";
//		String appSecret="NWE0MTkxNjRjNTll";
    	Long current_time=System.currentTimeMillis();
    	Long interval=0l;
    	//标志flag判断time存不存在
    	int time_flag=0;
    	if(!string_time.equals(""))
    	{
    		Long time =Long.parseLong(string_time);
    		interval=current_time-time;
    		time_flag=1;
    	}
    	if(time_flag==0||interval>100000)//6600000ms
    	{
	    	String url="https://openapi-adel.yunxuetang.cn/token?appId="+appId+"&appSecret="+appSecret;
	    	System.out.println(url); 
	    	URL uri = new URL(url);
	    	Map<String,String> map=new HashMap<String,String>();
	    
	        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setReadTimeout(50000);
	        connection.setConnectTimeout(100000);
	        connection.setDoInput(true); 
			connection.setDoOutput(true); 
			connection.setRequestProperty("appId", appId);
			connection.setRequestProperty("appSecret", appSecret);
	        connection.connect(); 
	//		接收
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
			JSONObject results = new JSONObject(sbf.toString());
			String accessToken=results.getString("accessToken");
			System.out.println(accessToken);
			map.put("accessToken",accessToken);
			map.put("time",Long.toString(current_time));
//			write.setValue(map);
	
	    	return accessToken;
	    	}
    	else return accesstoken;
    }
	

	@Override
	public String handleMessageContext(MessageContext arg0) throws EIPException {
		// TODO Auto-generated method stub
		JSONObject positiontypeMdmData = new JSONObject(arg0.getBodyData().toString()); 

		//数据放在mdm推送的json中的masterData字段下
		JSONArray masterData = new JSONArray(positiontypeMdmData.getString("masterData"));
//		JSONArray positionDataToEXUE = new JSONArray();
		JSONArray delpositiontypeDataToEXUE = new JSONArray();
		JSONArray positiontypeDataToEXUE = new JSONArray();//岗位分类数据
		JSONObject mdmCodes =new JSONObject();
		JSONObject delmdmCodes =new JSONObject();
		String entityCode = positiontypeMdmData.getString("mdType");
		
		String yes = "001";
		String no = "002";
		
		//遍历jsonArray取出mdm推送的数据
		for(int i = 0; i < masterData.length(); i++){
			JSONObject oneData = masterData.getJSONObject(i);
			//positiontype_temp存储岗位分类的body体
			JSONObject positiontype_temp = new JSONObject();
			//delposition存储禁用岗位的body体
			JSONObject delpositiontype = new JSONObject();
			//构造推送给E学的数据
			int canceled = 0;//该单位为正常状态
			int dr=oneData.getInt("dr");
			//数据状态，0：未提交，1：审批中，2：驳回，3：已发布（已解封），4：封存，5：已提交。
			if(dr==1||oneData.getInt("mdm_datastatus")!=3)
			{
				canceled = 1;//该单位状态为封存
				delpositiontype.put("thirdId",oneData.getString("mdm_code"));
				delpositiontypeDataToEXUE.put(delpositiontype);
				delmdmCodes.put(Integer.toString(i), oneData.getString("mdm_code"));
			}
			else{
			positiontype_temp.put("thirdId", oneData.getString("code"));
			positiontype_temp.put("name", oneData.getString("name"));
			mdmCodes.put(Integer.toString(i), oneData.getString("mdm_code"));
			positiontypeDataToEXUE.put(positiontype_temp);
			}
		}
			
		
		
		try 
		{
			//EXUE提供的url和token
//			Properties EXUEconfig =
//					ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
//			String url = EXUEconfig.getProperty("EXUEPositionUrl");
//			String delurl = EXUEconfig.getProperty("EXUEdelPositionUrl");
//			String positiontypeurl=EXUEconfig.getProperty("EXUEPositiontypeUrl");
			//String accessToken=getAccessTokenbyTime();
			String positiontypeurl="https://openapi-adel.yunxuetang.cn/v1/udp/public/positioncatalogs/sync";
			String deltypeurl="https://openapi-adel.yunxuetang.cn/v1/udp/public/positioncatalogs/";
			//String delurl="https://openapi-adel.yunxuetang.cn/v1/udp/public/positions/{thirdId}/del";
			URL positiontypeuri=new URL(positiontypeurl);

            //返回主数据平台的数据详情
            JSONArray dataReturnToMdm = new JSONArray();
            boolean success = true; 
            int numberOfFail = 0;
            int flag=1;//判断岗位分类是否有删除（禁用）成功，1表示删除（禁用）；
            
            //逐条发送参数并读取返回结果
            //mdm要求一数据一记录，E学接口传多数据返回一记录 
            for(int i = 0; i < positiontypeDataToEXUE.length(); i++){
                HttpURLConnection connection = (HttpURLConnection) positiontypeuri.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(50000);
                connection.setConnectTimeout(100000);
                connection.setDoInput(true); 
        		connection.setDoOutput(true); 
        		//设置请求头
        		AsyncAccessToken.positypeUsing = true;
        		String accessToken = getToken.getTokenFromDb();
        		connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", accessToken);
                
                connection.connect();
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 
                writer.write(positiontypeDataToEXUE.getJSONObject(i).toString()); 
        		writer.flush();
        		
        		InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
        		String strRead;
        		StringBuffer sbf = new StringBuffer(); 
    			while ((strRead = reader.readLine()) != null) {
    				System.out.println(strRead);
        			sbf.append(strRead); 
        			sbf.append("\r\n"); 
        		}
    			AsyncAccessToken.positypeUsing = false;
        		reader.close();
        		JSONObject results = new JSONObject(sbf.toString());
        		
        		String EXUECode = results.getString("code");
        		String EXUEMsg = results.getString("msg");
        		//String EXUESubcode = results.getString("subCode");
        		String EXUESubmsg = results.getString("subMsg");

        		boolean tempSuccess = true;
        		String tempMessage = "";
        		
        		//返回主数据平台数据详情中的一条记录
        		JSONObject oneResult = new JSONObject();
        		oneResult.put("mdmCode", mdmCodes.getString(Integer.toString(i)));
        		oneResult.put("entityCode", entityCode);
        		if(!EXUECode.equals("10000")){
        			tempSuccess = false;
        			tempMessage = String.format("E学平台接口HTTP连接错误，错误代码为%s,错误信息为%s,详细错误信息为%s", EXUECode,EXUEMsg,EXUESubmsg);
        			success = false;
        			numberOfFail++;
        		}
        		else{
      
        			tempMessage = "E学平台接口接口调用成功，业务发生成功,接口返回消息："+EXUEMsg;;
        			
        		}
        		oneResult.put("success", tempSuccess);
        		oneResult.put("message", tempMessage);
        		
        		dataReturnToMdm.put(oneResult);
        		
        		connection.disconnect();
            }
            
          //逐条发送参数并读取返回结果
            //mdm要求一数据一记录，E学接口传多数据返回一记录 
            for(int i = 0; i < delpositiontypeDataToEXUE.length(); i++){
            	JSONObject delpositiontype=delpositiontypeDataToEXUE.getJSONObject(i);
            	String thirdId = delpositiontype.getString("thirdId");
            	String delurl_position=deltypeurl+thirdId+"/del";
            	URL deluri=new URL(delurl_position);
                HttpURLConnection connection = (HttpURLConnection) deluri.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(50000);
                connection.setConnectTimeout(100000);
                connection.setDoInput(true); 
        		connection.setDoOutput(true);
        		AsyncAccessToken.posiUsing = true;
        		String accessToken = getToken.getTokenFromDb();
        		//设置请求头
        		connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", accessToken);
                
                connection.connect();
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 
                writer.write(delpositiontypeDataToEXUE.getJSONObject(i).toString()); 
        		writer.flush();
        		
        		InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
        		String strRead;
        		StringBuffer sbf = new StringBuffer(); 
    			while ((strRead = reader.readLine()) != null) {
    				System.out.println(strRead);
        			sbf.append(strRead); 
        			sbf.append("\r\n"); 
        		}
    			AsyncAccessToken.positypeUsing = false;
        		reader.close();
        		JSONObject results = new JSONObject(sbf.toString());
        		
        		String EXUECode = results.getString("code");
        		String EXUEMsg = results.getString("msg");
        		//String EXUESubcode = results.getString("subCode");
        		String EXUESubmsg = results.getString("subMsg");

        		boolean tempSuccess = true;
        		String tempMessage = "";
        		
        		//返回主数据平台数据详情中的一条记录
        		JSONObject oneResult = new JSONObject();
        		oneResult.put("mdmCode", delmdmCodes.getString(Integer.toString(i)));
        		oneResult.put("entityCode", entityCode);
        		if(!EXUECode.equals("10000")){
        			tempSuccess = false;
        			tempMessage = String.format("E学平台接口HTTP连接错误，错误代码为%s,错误信息为%s,详细错误信息为%s", EXUECode,EXUEMsg,EXUESubmsg);
        			success = false;
        			numberOfFail++;
        			flag=0;
        		}
        		else{
      
        			tempMessage = "E学平台接口调用成功，业务发生成功,接口返回消息："+EXUEMsg;;
        		}
        		oneResult.put("success", tempSuccess);
        		oneResult.put("message", tempMessage);
        		
        		dataReturnToMdm.put(oneResult);
        		
        		connection.disconnect();
            }
            
            JSONObject returnData = new JSONObject();
            if(success){
            	returnData.put("success", success);
            	returnData.put("message", "消费系统消费这批数据成功！");
            	returnData.put("mdMappings", dataReturnToMdm);
            	System.out.println(dataReturnToMdm.toString());
            }
            else{
            	returnData.put("success", success);
            	returnData.put("message", String.format("消费系统消费这批数据失败！共%d条数据分发失败", numberOfFail));
            	returnData.put("mdMappings", dataReturnToMdm);
            }

			DataObject body = arg0.getBody();
			
			body.set("positiontypeData",returnData.toString());
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
