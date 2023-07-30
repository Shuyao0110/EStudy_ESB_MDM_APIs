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
	
	public static String SearchCompany(String orgMdmCode, String tokenName, String modelName, String systemCodeName) throws Exception
	{
		Properties Econfig =
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
        //��������ͷ
		String mdmToken = Econfig.getProperty(tokenName);
        connection.setRequestProperty("mdmtoken", mdmToken);
        connection.setRequestProperty("tenantid", "tenant");
        connection.setRequestProperty("Content-Type", "application/json");
        //���Ͳ���
      
        connection.connect(); 
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 

		//body����������put��JSONObject��
		JSONObject parm = new JSONObject();
		String systemCode = Econfig.getProperty(systemCodeName);
		String gdCode = Econfig.getProperty(modelName);
		Collection<String> collection = new ArrayList<String>();
		collection.add(orgMdmCode);
		parm.put("systemCode", systemCode);
		parm.put("gdCode", gdCode);
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
	}

	public static String PostUrl (String url, JSONObject obj)
	{
		try{
			String returnArg = "";
			URL    uri = new URL(url);
	        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setReadTimeout(50000);
	        connection.setConnectTimeout(100000);
	        connection.setDoInput(true); 
	   		connection.setDoOutput(true); 
	        //��������ͷ  		
	        connection.setRequestProperty("Content-Type", "application/json");
	        //���Ͳ���         
	        connection.connect(); 
	   		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 

	   		//body����������put��JSONObject��
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
	   		returnArg=sbf.toString();
			return returnArg;   		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
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
			Properties apiConfig =
					ReadDatabaseConfigurationFile.ReadDatabaseConfiguration();
			JSONArray mdMappings = new JSONArray();
			for(int count = 0;count < masterDataArray.length();count++)
			{
				JSONObject deptOneJSON = masterDataArray.getJSONObject(count);
				String deptStatus = deptOneJSON.getString("org_status");
				if(deptStatus.equals("001"))//�����֯������
				{
					//����ͬ������
					//��ȡ�����ֶ�
					String deptThirdId = deptOneJSON.getString("code");//��λ��ţ�1030802
					String deptName = deptOneJSON.getString("name");//��λ���ƣ��о�Ժ֪ʶ��Ȩ
					String pk_code = deptOneJSON.getString("pk_code");//�ϼ����ŵ�mdmcode
					String principal_code = deptOneJSON.getString("code");//��֯�����˱�ţ�������������������������������������
					
					JSONObject department = new JSONObject();
					department.put("code", deptThirdId);
					department.put("name", deptName);
					department.put("parentThirdId", pk_code);
					department.put("deptManagerThirdId", principal_code);
					
					//��department�����Eѧ�ӿڵĲ�����
					String url = apiConfig.getProperty("EDeptUpdateUrl");
					String returnArg = PostUrl(url,department);
					JSONObject returnObj = new JSONObject(returnArg);
					JSONObject mdMapping = new JSONObject();
					Boolean singleSuccess = returnObj.getString("status").toString().equals("1")? true : false;
					if(!singleSuccess)
					{
						success = false;
					}
					mdMapping.put("mdmCode", deptJsonObject.getString("mdm_code"));
					mdMapping.put("entityCode", "g_org_test");
					mdMapping.put("busiDataId", subcompanycode);
					mdMapping.put("message", returnObj.getString("msg").toString());
					mdMapping.put("success", singleSuccess);
					mdMappings.put(mdMapping);
					
				}else if(deptStatus.equals("002"))//�����֯���ǹ�˾
				{
					//����OA��λ��ѯ�ӿڣ��жϸ���֮֯ǰ�Ƿ�Ϊ��˾
					
					//����֮֯ǰ�ǹ�˾������OA��˾�ӿڸ�Ϊ����
					
					//���첿������
					String deptThirdId = deptOneJSON.getString("code");//��λ��ţ�1030802
					String updatedatetime = deptJsonObject.getString("ts");//����ʱ��
					JSONObject dept = new JSONObject();
					dept.put("thirdId", deptThirdId);
					dept.put("updatedatetime", updatedatetime);
					String url = apiConfig.getProperty("EDeptDeleteUrl");
					String returnArg = PostUrl(url,dept);
					//��װ����������ϵͳ����
					JSONObject returnObj = new JSONObject(returnArg);
					JSONObject mdMapping = new JSONObject();
					Boolean singleSuccess = returnObj.getString("code").toString().equals("10000")? true : false;
					if(!singleSuccess)
					{
						success = false;
						String returnMessageCode = returnObj.getString("subcode");
						if (returnMessageCode equals("2170112")){
							String returnMessage = "该部门存在子部门";
						}else{
							String returnMessage = "该部门或其子部门有激活用户";
						}
					}
					mdMapping.put("mdmCode", deptJsonObject.getString("mdm_code"));
					mdMapping.put("entityCode", "g_org_test");
					mdMapping.put("busiDataId", departmentcode);
					mdMapping.put("message", returnMessage);
					mdMapping.put("success", singleSuccess);
					mdMappings.put(mdMapping);
				}	
			}
			JSONObject returnData = new JSONObject();
			returnData.put("success", success);
			returnData.put("mdMappings", mdMappings);
			DataObject body = arg0.getBody();
			body.set("deptData",returnData.toString());
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
