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
//import com.HelpTools.WriteDatabaseConfigurationFile;
import com.ufida.eip.core.MessageContext;
import com.ufida.eip.exception.EIPException;
import com.ufida.eip.java.IContextProcessor;

import commonj.sdo.DataObject;

public class positionAPI implements IContextProcessor {
	
//	根据appid和appsecret查询accessToken
	
	public String getAccessToken() throws Exception{
//		Properties EXUEconfig =
//				ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
//		String appId = EXUEconfig.getProperty("appId");
//    	String appSecret = EXUEconfig.getProperty("appSecret");
		String appId="jr4gaftwodb";
		String appSecret="NWE0MTkxNjRjNTll";
		
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
		
//		JSONArray resultData = results.getJSONArray("data");
//		System.out.println(resultData.toString());
//		JSONObject result = new JSONObject();
//		for(int i = 0; i < resultData.length(); i++){
//			JSONObject resultData0 = resultData.getJSONObject(i);
//			JSONObject oneResult = new JSONObject();
//			String oneMdmCode = resultData0.getString("mdm_code");
//			oneResult.put("sg_gs", resultData0.getString("sg_gs"));
//			oneResult.put("code", resultData0.getString("code"));
//			oneResult.put("pk_code_name", resultData0.getString("pk_code_name"));
//			result.put(oneMdmCode, oneResult);
//		}

    	return accessToken;
    }
	
//	根据appid和appsecret查询accessTokenbyTime
	
	public String getAccessTokenbyTime() throws Exception{
		Properties EXUEconfig =
				ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
//		WriteDatabaseConfigurationFile write=new WriteDatabaseConfigurationFile();
		String appId = EXUEconfig.getProperty("appId");
    	String appSecret = EXUEconfig.getProperty("appSecret");
    	String accesstoken = EXUEconfig.getProperty("accessToken");
    	String string_time=EXUEconfig.getProperty("time");	
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
		JSONObject positionMdmData = new JSONObject(arg0.getBodyData().toString()); 

		//数据放在mdm推送的json中的masterData字段下
		JSONArray masterData = new JSONArray(positionMdmData.getString("masterData"));
		JSONArray positionDataToEXUE = new JSONArray();
		JSONArray delpositionDataToEXUE = new JSONArray();
		JSONArray positiontypeDataToEXUE = new JSONArray();//岗位分类数据
		JSONObject mdmCodes =new JSONObject();
		JSONObject delmdmCodes =new JSONObject();
		String entityCode = positionMdmData.getString("mdType");
		
		String yes = "001";
		String no = "002";
		
		//遍历jsonArray取出mdm推送的数据
		System.out.println(Integer.toString(masterData.length()));
		for(int i = 0; i < masterData.length(); i++){
			JSONObject oneData = masterData.getJSONObject(i);
			//temp存储岗位的body体
			JSONObject temp = new JSONObject();
			//temp1存储岗位分类的body体
			JSONObject temp1 = new JSONObject();
			//delposition存储禁用岗位的body体
			JSONObject delposition = new JSONObject();
			//构造推送给E学的数据
			int canceled = 0;//该单位为正常状态
			int dr=oneData.getInt("dr");
			if(dr==1||oneData.getString("enablestate").toString().equals(no))
			{
				canceled = 1;//该单位状态为封存
				delposition.put("thirdId",oneData.getString("mdm_code"));
				delpositionDataToEXUE.put(delposition);
				delmdmCodes.put(Integer.toString(i), oneData.getString("mdm_code"));
			}
			else{
			JSONObject pk_type_entity = oneData.getJSONObject("pk_type_entity");
			String pk_type_name = pk_type_entity.getString("name");
			String pk_type_code=pk_type_entity.getString("mdm_code");
			temp.put("catalogThirdId",pk_type_code);
//			temp.put("jobtypename", oneData.getString("pk_type_name"));
			temp.put("code", oneData.getString("code"));
			temp.put("name", oneData.getString("name"));
			temp.put("thirdId", oneData.getString("mdm_code"));
			System.out.println(oneData.getString("mdm_code"));
			//岗位分类的thirdId
			temp1.put("thirdId", pk_type_name);
			mdmCodes.put(Integer.toString(i), oneData.getString("mdm_code"));
			positionDataToEXUE.put(temp);
			positiontypeDataToEXUE.put(temp1);
			}
		}
		System.out.println(Integer.toString(positionDataToEXUE.length()));
		
		try 
		{
			//EXUE提供的url和token
//			Properties EXUEconfig =
//					ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
//			String url = EXUEconfig.getProperty("EXUEPositionUrl");
//			String delurl = EXUEconfig.getProperty("EXUEdelPositionUrl");
//			String positiontypeurl=EXUEconfig.getProperty("EXUEPositiontypeUrl");
			//String accessToken=getAccessTokenbyTime();
			//String url = "http://172.16.99.52:20600/papi/openapi/api/open-esb/server/webhook/trigger/MTIwYWU2OWU0MWY2NDU1NmJjMjMxZGE4YWM1OTY5Y2Y=?access_token=f238d1f2274a4281a0df39cb7577d27e";
			String url="https://openapi-adel.yunxuetang.cn/v1/udp/public/positions/sync";
			String positiontypeurl="https://openapi-adel.yunxuetang.cn/v1/udp/public/positioncatalogs/sync";
			String delurl="https://openapi-adel.yunxuetang.cn/v1/udp/public/positions/";
			//String delurl="https://openapi-adel.yunxuetang.cn/v1/udp/public/positions/{thirdId}/del";
			//String access_token = "f238d1f2274a4281a0df39cb7577d27e";
			URL uri = new URL(url);
			//URL deluri=new URL(delurl);
			URL positiontypeuri=new URL(positiontypeurl);

            //返回主数据平台的数据详情
            JSONArray dataReturnToMdm = new JSONArray();
            boolean success = true; 
            int numberOfFail = 0;
            int flag=1;//判断岗位是否有删除（禁用）成功，1表示删除（禁用）；
            
            //逐条发送参数并读取返回结果
            //mdm要求一数据一记录，E学接口传多数据返回一记录 
            for(int i = 0; i < positionDataToEXUE.length(); i++){
                HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
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
                writer.write(positionDataToEXUE.getJSONObject(i).toString()); 
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
    			//用完token恢复状态
    	        AsyncAccessToken.posiUsing = false;
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
      
        			tempMessage = "E学平台接口调用成功，业务发生成功,接口返回消息："+EXUEMsg;;
        		}
        		oneResult.put("success", tempSuccess);
        		oneResult.put("message", tempMessage);
        		
        		dataReturnToMdm.put(oneResult);
        		
        		connection.disconnect();
            }
            
          //逐条发送参数并读取返回结果
            //mdm要求一数据一记录，E学接口传多数据返回一记录 
            for(int i = 0; i < delpositionDataToEXUE.length(); i++){
            	JSONObject delposition=delpositionDataToEXUE.getJSONObject(i);
            	String thirdId = delposition.getString("thirdId");
            	String delurl_position=delurl+thirdId+"/del";
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
                writer.write(delpositionDataToEXUE.getJSONObject(i).toString()); 
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
    			//用完token恢复状态
    	        AsyncAccessToken.posiUsing = false;
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
            else if(flag==1){
            	returnData.put("success", success);
            	returnData.put("message", String.format("消费系统消费这批数据失败！共%d条数据分发失败", numberOfFail));
            	returnData.put("mdMappings", dataReturnToMdm);
            }
            else{
            	returnData.put("success", success);
            	returnData.put("message", String.format("消费系统消费这批数据失败！共%d条数据分发失败,其中对于禁用的数据,该岗位下还有人员", numberOfFail));
            	returnData.put("mdMappings", dataReturnToMdm);
            }
    		

			DataObject body = arg0.getBody();
			
			body.set("positionData",returnData.toString());
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
