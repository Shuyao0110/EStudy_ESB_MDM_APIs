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
	/*
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
		AsyncAccessToken.deptUsing = true;
    	String Authorization = getToken.getTokenFromDb();
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
	*/
	
	public static String SearchCompany(String orgMdmCode, String tokenName, String modelName, String systemCodeName) throws Exception
	{
		Properties OAconfig =
				ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
		String url =
		OAconfig.getProperty("MDMUrl");
        URL    uri = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(50000);
        connection.setConnectTimeout(100000);
        connection.setDoInput(true); 
		connection.setDoOutput(true); 
        //设置请求头
		String mdmToken = OAconfig.getProperty(tokenName);
        connection.setRequestProperty("mdmtoken", mdmToken);
        connection.setRequestProperty("tenantid", "tenant");
        connection.setRequestProperty("Content-Type", "application/json");
        //发送参数
      
        connection.connect(); 
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 

		//body参数在这里put到JSONObject中
		JSONObject parm = new JSONObject();
		String systemCode = OAconfig.getProperty(systemCodeName);
		String gdCode = OAconfig.getProperty(modelName);
		parm.put("systemCode", systemCode);
		parm.put("gdCode", gdCode);
		Collection<String> collection = new ArrayList<String>();
		
		collection.add(orgMdmCode);
		   		 
		parm.put("codes",collection);
		System.out.println(parm.toString());
		
		
		writer.write(parm.toString()); 
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
		String results = sbf.toString(); 
		
		JSONObject resultObject = new JSONObject(results);
		JSONArray orgJsonArray = new JSONArray(resultObject.get("data").toString());
		JSONObject orgObject = orgJsonArray.getJSONObject(0);
		String sg_gs = orgObject.getString("sg_gs");
		//判断该组织是否为公司
		if(sg_gs.equals("002"))
		{
			String pkOrgMdmCode = orgObject.getString("pk_code");
			return SearchCompany(pkOrgMdmCode,tokenName,modelName, systemCodeName);
		}else{
			return orgObject.getString("code");
		}				
		
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
	   		AsyncAccessToken.deptUsing = true;
	   		String accessToken = getToken.getTokenFromDb();
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
	   		AsyncAccessToken.deptUsing = false;
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
			Boolean success = true;
			for(int count = 0;count < masterDataArray.length();count++)
			{
				JSONObject deptOneJSON = masterDataArray.getJSONObject(count);
				String deptStatus = deptOneJSON.getString("org_status");
				
				
				
				//第一次构造同步部门
				//提取thirdId
				String principal_code = "";
				String thirdId = deptOneJSON.getString("mdm_code");
				//String deptCode = deptOneJSON.getString("code");//单位编号：1030802
				String deptName = deptOneJSON.getString("name");//单位名称：研究院知识产权

				
				
				JSONObject dept_first = new JSONObject();
				dept_first.put("thirdId",thirdId);
				dept_first.put("name",deptName);
				
				//将department数据填到E学接口的参数中
				//String syncUrl = apiConfig.getProperty("EDeptSyncUrl");
                //String recoveryUrl = apiConfig.getProperty("EDeptRecoveryUrl");
                //String thirdIdUrl = apiConfig.getProperty("thirdIdUrl")
				String syncTestUrl = "https://openapi-adel.yunxuetang.cn/v1/udp/public/depts/sync";
				//String returnArg = PostUrl(syncUrl,department);
				System.out.println(dept_first.toString());
				String returnArg = PostUrl(syncTestUrl,dept_first);
				System.out.println(returnArg);
				boolean first_success= true;
				
			}
			for(int count = 0;count < masterDataArray.length();count++){
				JSONObject deptOneJSON = masterDataArray.getJSONObject(count);
				//第二次提取剩余字段
				String principal_code = "";
				String thirdId = deptOneJSON.getString("mdm_code");
				String deptCode = deptOneJSON.getString("code");//单位编号：1030802
				String deptName = deptOneJSON.getString("name");//单位名称：研究院知识产权
				JSONObject dept_second = new JSONObject();
				try{
					JSONObject pk_code_entity = deptOneJSON.getJSONObject("pk_code_entity");
					String pk_mdm_code = pk_code_entity.getString("mdm_code");//上级组织
					JSONObject principal_code_entity = deptOneJSON.getJSONObject("principal_code_entity");//组织负责人编号
					String principal_mdm_code = principal_code_entity.getString("mdm_code");
					dept_second.put("deptManagerThirdId", principal_mdm_code);
					dept_second.put("parentThirdId", pk_mdm_code);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				
				dept_second.put("thirdId",thirdId);
				dept_second.put("name",deptName);
				dept_second.put("code", deptCode);
				
				
				
				
				String syncTestUrl = "https://openapi-adel.yunxuetang.cn/v1/udp/public/depts/sync";
				//String returnArg = PostUrl(syncUrl,department);
				System.out.println(dept_second.toString());
				String returnArg = PostUrl(syncTestUrl,dept_second);
				System.out.println(returnArg);
				boolean second_success= true;
				
				JSONObject returnObj = new JSONObject(returnArg);
				JSONObject mdMapping = new JSONObject();
				Boolean singleSuccess = returnObj.getString("code").toString().equals("10000")? true : false;
				if(!singleSuccess)
				{
					success = false;
				}
				mdMapping.put("mdmCode", deptOneJSON.getString("mdm_code"));
				mdMapping.put("busiDataId", "");
				mdMapping.put("entityCode", "g_org_test");
				mdMapping.put("message", returnObj.getString("msg"));
				mdMapping.put("success", singleSuccess);
				mdMappings.put(mdMapping);
			}
			JSONObject returnData = new JSONObject();
			returnData.put("success", success);
			returnData.put("message", "");
			returnData.put("mdMappings", mdMappings);
			System.out.println(returnData.toString());
			DataObject body = arg0.getBody();
			body.set("deptData",returnData.toString());
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
