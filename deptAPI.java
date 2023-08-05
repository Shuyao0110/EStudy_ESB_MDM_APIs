package com.sanhua;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.HelpTools.ReadDatabaseConfigurationFile;
import com.sanhua.search;
import com.ufida.eip.core.MessageContext;
import com.ufida.eip.exception.EIPException;
import com.ufida.eip.java.IContextProcessor;

import commonj.sdo.DataObject;

public class deptAPI implements IContextProcessor {
//	根据appid和appsecret查询accessToken
	
	public static String getAccessToken() throws Exception{
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
    	return accessToken;
    }
	
	
	public static String PostUrl (String url, JSONObject obj)
	{
		String returnarg = "";
		try{
			URL    uri = new URL(url);
	        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setReadTimeout(50000);
	        connection.setConnectTimeout(100000);
	        connection.setDoInput(true); 
	   		connection.setDoOutput(true); 
	        //设置请求头  
	   		String accessToken = getAccessToken();
	   		connection.setRequestProperty("Authorization",accessToken);
	        connection.setRequestProperty("Content-Type", "application/json");
	        //发送参数         
	        connection.connect(); 
	   		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 

	   		//body参数在这里put到JSONObject中
	   		writer.write(obj.toString()); 
	   		writer.flush();
	   		InputStream is = connection.getInputStream(); 
	   		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
	   		String strRead;
	   		StringBuffer sbf = new StringBuffer(); 
				while ((strRead = reader.readLine()) != null) { 
	   			sbf.append(strRead); 
	   			sbf.append("\r\n"); 
	   		}
	   		reader.close(); 
	   		connection.disconnect();
	   		System.out.println(sbf.toString());
	   		returnarg=sbf.toString();	   		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
   		return returnarg;
	}
	@Override
	public String handleMessageContext(MessageContext arg0) throws EIPException {
		// TODO Auto-generated method stub
		JSONObject deptMdmData = new JSONObject(arg0.getBodyData().toString()); 
		System.out.println(deptMdmData.toString());
		try 
		{
			String masterData= (String) deptMdmData.get("masterData");			
			JSONArray masterDataArray = new JSONArray(masterData);
			//Properties apiConfig =ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
			JSONArray mdMappings = new JSONArray();
			Boolean first_success = true;
			Boolean second_success = true;
			//第一次同步主数据编码
			for(int count = 0;count < masterDataArray.length();count++)
			{
				JSONObject deptOneJSON = masterDataArray.getJSONObject(count);
				String deptStatus = deptOneJSON.getString("org_status");
				//构造同步部门
				//提取所需字段
				String thirdId = deptOneJSON.getString("mdm_code");
				String deptCode = deptOneJSON.getString("code");//单位编号：1030802
				String deptName = deptOneJSON.getString("name");//单位名称：研究院知识产权
				JSONObject pk_code_entity = deptOneJSON.getJSONObject("pk_code_entity");
				String pk_mdm_code = pk_code_entity.getString("mdm_code");//上级组织
				String principal_code = deptOneJSON.getString("principal_code");//组织负责人编号？？？？？？？？？？？？？？？？？？？
				System.out.println(principal_code);
				JSONObject dept_first = new JSONObject();
				dept_first.put("thirdId",thirdId);
				//department.put("parentThirdId", pk_mdm_code);
				//department.put("deptManagerThirdId", principal_code);
				
				//将department数据填到E学接口的参数中
				//String syncUrl = apiConfig.getProperty("EDeptSyncUrl");
				//String recoveryUrl = apiConfig.getProperty("EDeptRecoveryUrl");
				//String thirdIdUrl = apiConfig.getProperty("thirdIdUrl")
				String syncTestUrl = "https://openapi-adel.yunxuetang.cn/v1/udp/public/depts/sync";
				//String returnArg = PostUrl(syncUrl,department);
				System.out.println(department.toString());
				String returnArg = PostUrl(syncTestUrl,department);
				System.out.println(returnArg);
				
				JSONObject returnObj = new JSONObject(returnArg);
				JSONObject mdMapping = new JSONObject();
				Boolean singleSuccess = returnObj.getString("code").toString().equals("10000")? true : false;
				if(!singleSuccess)
				{
					first_success = false;
				}
			}
			//第二次同步剩余字段
			if first_success.equals(true){
				for(int count = 0;count < masterDataArray.length();count++){
				JSONObject deptOneJSON = masterDataArray.getJSONObject(count);
				String deptStatus = deptOneJSON.getString("org_status");
			
				if(deptStatus.equals("001"))//如果组织被启用
				{
					//构造同步部门
					//提取所需字段
					String thirdId = deptOneJSON.getString("mdm_code");
					String deptCode = deptOneJSON.getString("code");//单位编号：1030802
					String deptName = deptOneJSON.getString("name");//单位名称：研究院知识产权
					JSONObject pk_code_entity = deptOneJSON.getJSONObject("pk_code_entity");
					String pk_mdm_code = pk_code_entity.getString("mdm_code");//上级组织
					String principal_code = deptOneJSON.getString("principal_code");//组织负责人编号？？？？？？？？？？？？？？？？？？？
					System.out.println(principal_code);
					JSONObject dept_second = new JSONObject();
					dept_second.put("thirdId",thirdId);
					dept_second.put("name",deptName);
					dept_second.put("code",deptCode);
					dept_second.put("parentThirdId", pk_mdm_code);
					dept_second.put("deptManagerThirdId", principal_code);
					
					//将department数据填到E学接口的参数中
					//String syncUrl = apiConfig.getProperty("EDeptSyncUrl");
					//String recoveryUrl = apiConfig.getProperty("EDeptRecoveryUrl");
					//String thirdIdUrl = apiConfig.getProperty("thirdIdUrl")
					String syncTestUrl = "https://openapi-adel.yunxuetang.cn/v1/udp/public/depts/sync";
					//String returnArg = PostUrl(syncUrl,department);
					System.out.println(department.toString());
					String returnArg = PostUrl(syncTestUrl,dept_second);
					System.out.println(returnArg);
					
					JSONObject returnObj = new JSONObject(returnArg);
					JSONObject mdMapping = new JSONObject();
					Boolean singleSuccess = returnObj.getString("code").toString().equals("10000")? true : false;
					if(!singleSuccess)
					{
						second_success = false;
					}
					mdMapping.put("mdmCode", deptOneJSON.getString("mdm_code"));
					mdMapping.put("entityCode", "g_org_test");
					mdMapping.put("busiDataId", "");
					mdMapping.put("message", message);
					mdMapping.put("success", singleSuccess);
					mdMappings.put(mdMapping);
					}
				}else if (deptStatus.equals("002"))//如果组织未启用
				{
					//构造部门数据
					String thirdId = deptOneJSON.getString("mdm_code");
					String updatedatetime = deptOneJSON.getString("ts");//锟斤拷锟斤拷时锟斤拷
					JSONObject dept = new JSONObject();
					dept.put("thirdId", thirdId);
					//dept.put("updatedatetime", updatedatetime);
                    //String delUrl = apiConfig.getProperty("EDeptDelUrl")
					//String returnArg = PostUrl(delUrl,dept);
					String delTestUrl = "https://openapi-adel.yunxuetang.cn/v1/udp/public/depts/{thirdId}/del";
					String returnArg = PostUrl(delTestUrl, dept);
					
                    JSONObject returnObj = new JSONObject(returnArg);
                    String message = returnObj.getString("subMsg");
					JSONObject mdMapping = new JSONObject();
					Boolean singleSuccess = returnObj.getString("code").toString().equals("10000")? true : false;
					if(!singleSuccess)
					{
						second_success = false;
					}
					mdMapping.put("mdmCode", deptOneJSON.getString("mdm_code"));
					mdMapping.put("entityCode", "g_org_test");
					mdMapping.put("busiDataId", "");
					mdMapping.put("message", message);
					mdMapping.put("success", singleSuccess);
					mdMappings.put(mdMapping);
				}	

			}
			JSONObject returnData = new JSONObject();
			returnData.put("success", second_success);
			returnData.put("message", "");
			returnData.put("mdMappings", mdMappings);
			System.out.println(returnData.toString());
			DataObject body = arg0.getBody();
			body.set("deptData",returnData.toString());
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JSONObject returnData = new JSONObject();
			returnData.put("success", false);
			returnData.put("message", e.getMessage());
			returnData.put("mdMappings", [{}]);
			DataObject body = arg0.getBody();
			body.set("deptData",returnData.toString());
		}
		return null;
	}

}
