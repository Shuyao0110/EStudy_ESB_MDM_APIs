package com.sanhua;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ufida.eip.core.MessageContext;
import com.ufida.eip.exception.EIPException;
import com.ufida.eip.java.IContextProcessor;

import commonj.sdo.DataObject;

public class personAPI implements IContextProcessor {
	//Eѧƽ̨ɾ���ӿ�
	//��ַ���ܴ������ݿ���
//	public JSONObject deletePerson(ArrayList<String> mdm_code){
//		
//		JSONObject results = new JSONObject();
//		JSONObject data = new JSONObject();
//		JSONArray mdMappings = new JSONArray();
//		results.put("status", true);
//		String domain = "openapi-adel.yunxuetang.cn";
//		boolean success = true;
//		boolean EStudyStatus = true;
//		String message;
//        Iterator<String> iterator = mdm_code.iterator();
//        //TODO �������ݿ�
//        String entityCode = "g_pers_test";
//        int numFail = 0;
//        while(iterator.hasNext()){
//        	String uid = iterator.next();
//    		String url = "https://"+domain+"/v1/udp/public/users/"+uid+"/del";
//    		JSONObject oneResult = new JSONObject();
//    		try{
//    			
//            	URL uri = new URL(url);
//    	        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
//    	        connection.setRequestMethod("POST");
//    	        connection.setReadTimeout(50000);
//    	        connection.setConnectTimeout(100000);
//    	        connection.setDoInput(true); 
//    			connection.setDoOutput(true);
//            	String Authorization = getToken.getTokenFromDb("person");
//    			connection.setRequestProperty("Authorization", Authorization);
//    			connection.setRequestProperty("Content-type", "application/json; charset=utf-8");
//    	        connection.connect(); 
//    			InputStream is = connection.getInputStream();
//    	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
//    			String strRead;
//    			StringBuffer sbf = new StringBuffer(); 
//    			while ((strRead = reader.readLine()) != null) {
//    				System.out.println(strRead);
//    				sbf.append(strRead); 
//    				sbf.append("\n");
//    			}
//    	        //����token�ָ�״̬
//    	        AsyncAccessToken.personUsing = false;
//    			reader.close();
//    			JSONObject result = new JSONObject(sbf.toString());
//    			if(!result.getString("msg").equals("success")){
//    				success = false;
//    				oneResult.put("mdmCode", uid);
//    				oneResult.put("entityCode", entityCode);
//    				oneResult.put("success", false);
//    				oneResult.put("message", "����ɾ��ʱ����������ϢΪ��"+result.getString("subMsg"));
//    				mdMappings.put(oneResult);
//    				numFail++;
//    			}
//    			else{
//    				oneResult.put("mdmCode", uid);
//    				oneResult.put("entityCode", entityCode);
//    				oneResult.put("success", true);
//    				oneResult.put("message", "����ɾ���ɹ���");
//    				mdMappings.put(oneResult);
//    			}
//    		}
//    		catch(Exception e){
//    			if(results.getBoolean("status")){
//    				EStudyStatus = false;
//	    			results.put("status", false);
//	    			results.put("location", "����Eѧƽ̨ɾ����Ա�ӿ�ʱ���ִ���");
//	    			results.put("description", e.getMessage());
//	    			results.put("detail", e.getStackTrace());
//    			}
//    		}
//        }
//        data.put("success", success);
//        data.put("mdMappings", mdMappings);
//        if(numFail != 0){
//        	message = "ɾ����Աʱ������"+Integer.toString(numFail)+"������ɾ��ʧ��";
//        }
//        else{
//        	message = "����ϵͳ�����������ݳɹ���";
//        }
//        data.put("message", message);
//        results.put("data", data);
//        results.put("status", EStudyStatus);
//		return results;
//	}
	//��Eѧƽ̨ͬ����Ϣ�����۳ɹ������status�ֶΣ�trueΪ�ɹ���falseΪʧ�ܣ�ʧ��ʱ���д���ԭ�����ϸ�������ɹ�ʱ��������
	public JSONObject EStudySync(JSONArray data){
		//TODO ���ｫ����ƴ�ӣ���ʽƽ̨�ǵÿ�����������url
		JSONObject results = new JSONObject();
		try{
			String domain = "openapi-adel.yunxuetang.cn";
			String url = "https://"+ domain +"/v1/udp/public/users/sync/batch";
	    	URL uri = new URL(url);


	    	//TODO ������Eѧ���λ��token
	        //��װ��ѯ����
	        JSONObject queryCondition = new JSONObject();
	        queryCondition.put("datas", data);
	        //�������ӣ���������ͷ
	        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setReadTimeout(50000);
	        connection.setConnectTimeout(100000);
	        connection.setDoInput(true); 
			connection.setDoOutput(true);
			//ʹ��tokenǰ��״̬��Ϊ1
	    	String Authorization = getToken.getTokenFromDb();
			connection.setRequestProperty("Authorization", Authorization);
			connection.setRequestProperty("Content-type", "application/json; charset=utf-8");
	        connection.connect(); 
	        //TODO 
	        System.out.println(queryCondition.toString());
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 
	        writer.write(queryCondition.toString());
			writer.flush();
	        
			InputStream is = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
			String strRead;
			StringBuffer sbf = new StringBuffer(); 
			while ((strRead = reader.readLine()) != null) {
				System.out.println(strRead);
				sbf.append(strRead);
				sbf.append("\n"); 
			}
	        //����token�ָ�״̬
	        AsyncAccessToken.personUsing = false;
			reader.close();
			results.put("data", new JSONObject(sbf.toString()));
			results.put("status", true);
			
		}
		catch(Exception e){
			results.put("status", false);
			results.put("location", "����Eѧƽ̨�ӿ�ʱ���ִ���");
			results.put("description", e.getMessage());
			results.put("detail", e.getStackTrace());
		}
		System.out.println(results.toString());
		return results;
	}
	//����������ڻ�ȡԱ��ֱ������������ݱ���
    //TODO �Ƿ�������ϼ���λ����Ա����Ҫѯ�� 2023-8-5
	public JSONObject getManagerIds(ArrayList<String> pkPostCodes){
        JSONObject results = new JSONObject();
		ArrayList<String> resultArray = new ArrayList<String>();
		try{
			String url = "http://10.10.201.40/iuapmdm/cxf/mdmrs/newcenter/newCenterService/queryListMdByMdmCodes";
	    	URL uri = new URL(url);
	    	
	    	//TODO ������Eѧ���λ��token
	        String mdmtoken = "9d2eeb88-c162-4214-9e86-e9c0fea9c6b2";
	        String tenantid = "tenant";
	        String systemCode = "EStudy-position";
	        String gdCode = "g_post_test";
	        //��װ��ѯ����
	        JSONObject queryCondition = new JSONObject();
	        queryCondition.put("systemCode", systemCode);
	        queryCondition.put("gdCode", gdCode);
	        queryCondition.put("pageable", false);
	        queryCondition.put("pageIndex", 1);
	        queryCondition.put("pageSize", 30000);
	        queryCondition.put("codes", pkPostCodes);
	        //�������ӣ���������ͷ
	        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setReadTimeout(50000);
	        connection.setConnectTimeout(100000);
	        connection.setDoInput(true); 
			connection.setDoOutput(true); 
			connection.setRequestProperty("mdmtoken", mdmtoken);
			connection.setRequestProperty("tenantid", tenantid);
			connection.setRequestProperty("Content-type", "application/json");
	        connection.connect(); 
	        //TODO 
	        System.out.println(queryCondition.toString());
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 
	        writer.write(queryCondition.toString()); 
			writer.flush();
	        
			InputStream is = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
			String strRead;
			StringBuffer sbf = new StringBuffer(); 
			while ((strRead = reader.readLine()) != null) {
				System.out.println(strRead);
				sbf.append(strRead); 
				sbf.append("\n"); 
			}
			reader.close();
			JSONObject result = new JSONObject(sbf.toString());
			//�������������֣�Eѧ�����еĸ�λ���࣡ 2023-8-3 ����������������ǵ�����쳣����
            //Ĭ��ÿ���˶��ܲ�ѯ���ϼ���λ�ĸ�λ��ţ�����д��������洦��
			JSONArray resultData = result.getJSONArray("data");
			JSONObject pkMapping = new JSONObject();
			ArrayList<String> personCondition = new ArrayList<String>();
			personCondition.add("and");
			personCondition.add("in");
			for(int i = 0; i < resultData.length(); i++){
				JSONObject oneResultData = resultData.getJSONObject(i);
				String pk_mdm_code = oneResultData.getString("mdm_code");
				String pk_post_name = oneResultData.getString("code");
				pkMapping.put(pk_mdm_code, pk_post_name);
				personCondition.add("'" + pk_post_name + "'");
			}
			//�������ѯ�������е��ϼ���λ�ĸ�λ���룬�����ø�λ�����ѯ������λ�������ݱ���

			//TODO ��һ�β�ѯ��Eѧ��Ա����Ϣ
			try{
				url = "http://10.10.201.40/iuapmdm/cxf/mdmrs/newcenter/newCenterService/queryListMdByConditions";
		    	uri = new URL(url);
		    	
		    	//TODO ������Eѧ���λ��token
		        mdmtoken = "272f9d08-5b39-40d1-a53f-8b45a8b7c89a";
		        tenantid = "tenant";
		        systemCode = "EStudy-person";
		        gdCode = "g_pers_test";
		        //��װ��ѯ����
		        queryCondition = new JSONObject();
		        JSONObject conditions = new JSONObject();
		        conditions.put("pk_post#code", personCondition);
		        queryCondition.put("systemCode", systemCode);
		        queryCondition.put("gdCode", gdCode);
		        queryCondition.put("pageable", false);
		        queryCondition.put("pageIndex", 1);
		        queryCondition.put("pageSize", 30000);
		        queryCondition.put("conditions", conditions);
		        //�������ӣ���������ͷ
		        connection = (HttpURLConnection) uri.openConnection();
		        connection.setRequestMethod("POST");
		        connection.setReadTimeout(50000);
		        connection.setConnectTimeout(100000);
		        connection.setDoInput(true); 
				connection.setDoOutput(true); 
				connection.setRequestProperty("mdmtoken", mdmtoken);
				connection.setRequestProperty("tenantid", tenantid);
				connection.setRequestProperty("Content-type", "application/json");
		        connection.connect(); 
		        System.out.println(queryCondition.toString());
		        writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 
		        writer.write(queryCondition.toString()); 
				writer.flush();
		        
				is = connection.getInputStream();
		        reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
				sbf = new StringBuffer(); 
				while ((strRead = reader.readLine()) != null) {
					System.out.println(strRead);
					sbf.append(strRead); 
					sbf.append("\n"); 
				}
				reader.close();
                //�����result��Ϊ������Ա��ѯ�ӿں󷵻ص��������ݣ���Ҫע����Щ��Աû���ϼ������Է��ص����ݳ��ȿ��ܱ��������
				result = new JSONObject(sbf.toString());
			}
			catch(Exception e){
                results.put("status", false);
                results.put("location", "������������Ա�ӿ�ʱ���ִ���");
                results.put("description", e.getMessage());
                results.put("detail", e.getStackTrace());
                return results;
			}
			//�˴�try���ڴ����������쳣����������Ա���ϼ�������Ҫͬ��
            //����ѯ������Ա�����ϼ�ʱ��Ӱ��ͬ����ֻ��Ҫ�����Ӧ���ϼ���������Ϊnull����
			JSONArray personResult = new JSONArray();
			try{
				personResult = results.getJSONArray("data");
			}
			catch (Exception e){
				System.out.println("������Ա�����ϼ���");
			}
            //�����ø�λ����������ݱ����ӳ��
            //�����д���Ա�����ݱ��뵽�ϼ���λ���룬��λ���뵽�����ݱ����ӳ�䣬��Ӧ���Ҽ���
			JSONObject codeMdmCodeMapping = new JSONObject();
			for(int i = 0; i < personResult.length(); i++){
				JSONObject oneData = personResult.getJSONObject(i);
				String onePostCode = oneData.getString("pk_post_name");
				String oneMdmCode = oneData.getString("mdm_code");
				codeMdmCodeMapping.put(onePostCode, oneMdmCode);
			}
			
			Iterator<String> iterator = pkPostCodes.iterator();
			while(iterator.hasNext()){
				//�˴�try���ڴ�����Ա�ϼ�����ͬ�����쳣
				try{
					String pkPostCode = iterator.next();
					String managerMdmCode = codeMdmCodeMapping.getString(pkMapping.getString(pkPostCode));
					resultArray.add(managerMdmCode);
				}
				catch (Exception e){
					resultArray.add(null);
				}
			}
			results.put("status", true);
			results.put("data", resultArray);
		}
		catch (Exception e){
			results.put("status", false);
			results.put("location", "���������ݸ�λ��ѯ�ӿ�ʱ���ִ���");
			results.put("description", e.getMessage());
			results.put("detail", e.getStackTrace());
		}
		return results;
	}
	//���ڴ����֤��ת����
	public String getBirthDay(String id){
		String birthdayDate = id.substring(6, 14);
		String birthdayYear = birthdayDate.substring(0, 4);
		String birthdayMonth = birthdayDate.substring(4, 6);
		String birthdayDay = birthdayDate.substring(6, 8);
		String birthday = birthdayYear + "-" + birthdayMonth + "-" + birthdayDay;
		return birthday;
	}
	@Override
	public String handleMessageContext(MessageContext arg0) throws EIPException {
		// TODO Auto-generated method stub
        // TODO
		JSONObject personData = new JSONObject();
		JSONObject personMdmData = new JSONObject(arg0.getBodyData().toString());
		String entityCode = personMdmData.getString("mdType");
		JSONArray personMasterData = new JSONArray(personMdmData.getString("masterData"));
		//ͬ������
		System.out.println(personMasterData.toString());
		JSONArray dataToEStudy = new JSONArray();
		//��ְ����
		ArrayList<String> dataDelEstudy = new ArrayList<String>();
		//����������ְ��񣬲������ְ��Ա���ϼ���λ�����ݱ���
		ArrayList<String> syncMdmCodes = new ArrayList<String>();
		ArrayList<String> pkPostCodes = new ArrayList<String>();
		ArrayList<String> mdmCodes = new ArrayList<String>();
		
		Iterator<Object> iterator = personMasterData.iterator();
		//TODO ��ԱһСʱֻ����ͬ��һ�Σ���Ҫ��Ϊ�ݹ�ʽͬ�� 2023-8-4
//		//��һ��ͬ��
//		while(iterator.hasNext()){
//			JSONObject onePersonMasterData = (JSONObject) iterator.next();
//			String peopleStatus = onePersonMasterData.getJSONObject("peop_status_entity").getString("code");
//			String mdm_code = onePersonMasterData.getString("mdm_code");
//			mdmCodes.add(mdm_code);
//			if(!peopleStatus.equals("01")){
//				dataDelEstudy.add(mdm_code);
//				iterator.remove();;//���ｫ����ְ��Ա���Ƴ�
//				continue;
//			}
//			String pk_post_code = onePersonMasterData.getJSONObject("pk_post_entity").getString("pk_post");
//			syncMdmCodes.add(mdm_code);
//			pkPostCodes.add(pk_post_code);
//		}
        //�����ȡ������Ծ�ֱ�ӷ���

//		
//		for(int i = 0; i < personMasterData.length(); i++){
//			JSONObject oneData = new JSONObject();
//			JSONObject onePersonMasterData = personMasterData.getJSONObject(i);
//			
//			String thirdUserId = syncMdmCodes.get(i);
//			String username = onePersonMasterData.getString("last_code");
//			String fullname = onePersonMasterData.getString("name");
//			
//			oneData.put("thirdUserId",thirdUserId);
//			oneData.put("username",username);
//			oneData.put("fullname",fullname);
//			dataToEStudy.put(oneData);
//		}
//		JSONObject result = EStudySync(dataToEStudy);
//		System.out.println(result.toString());
//		//String successOrNot = result.getString("msg");

		//�ڶ���ͬ��
		//����Ҫ����ɾ���ӿ� 2023.8.24
		iterator = personMasterData.iterator();
		while(iterator.hasNext()){
			JSONObject onePersonMasterData = (JSONObject) iterator.next();
//			String peopleStatus = onePersonMasterData.getJSONObject("peop_status_entity").getString("code");
			String mdm_code = onePersonMasterData.getString("mdm_code");
			mdmCodes.add("mdm_code");
//			if(!peopleStatus.equals("01")){
//				dataDelEstudy.add(mdm_code);
//				iterator.remove();;//���ｫ����ְ��Ա���Ƴ�
//				continue;
//			}
			String pk_post_code = onePersonMasterData.getJSONObject("pk_post_entity").getString("pk_post");
			syncMdmCodes.add(mdm_code);
			pkPostCodes.add(pk_post_code);
		}
//		//����ɾ���ӿ�
        boolean success;
        String message = "";
//        boolean delStatus = true;
//        boolean delSuccess = true;
        JSONArray mdMappings = new JSONArray();
//		if(dataDelEstudy.size()>0){
//			JSONObject delResults = deletePerson(dataDelEstudy);
//	        delStatus = delResults.getBoolean("status");
//	        
//	        JSONObject delData = delResults.getJSONObject("data");
//	        delSuccess = delData.getBoolean("success");
//	        JSONArray delMdMappings = delData.getJSONArray("mdMappings");
//	        String delMessage = delData.getString("message");
//	
//
//	        
//	        
//			if(!delStatus){
//				message += delMessage + "\n";
//				message += "����λ�ã�" + delResults.getString("location") + "\n";
//				message += "����ԭ��" + delResults.getString("description") + "\n";
//				message += "��ϸ��Ϣ��" + delResults.getString("detail") + "\n";
//			}
//			else{
//				message += "��Աɾ���ɹ���\n";
//			}
//			for(int i = 0; i < delMdMappings.length(); i++){
//				mdMappings.put(delMdMappings.get(i));
//			}
//		}
		//ɾ�����˽���
		
		
		//JSONObject managerCodesResult = getManagerIds(pkPostCodes);

        
        
		//if(managerCodesResult.getBoolean("status")){
            
        //}
		dataToEStudy = new JSONArray();
		for(int i = 0; i < personMasterData.length(); i++){
			JSONObject oneData = new JSONObject();
			JSONObject onePersonMasterData = personMasterData.getJSONObject(i);
			String peopleStatus = onePersonMasterData.getJSONObject("peop_status_entity").getString("code");

			String thirdUserId = syncMdmCodes.get(i);
			String username = onePersonMasterData.getString("last_code");
			String fullname = onePersonMasterData.getString("name");
			String userNo = onePersonMasterData.getString("last_code");
			String hireDate = onePersonMasterData.getString("entry_time");
			String gender = onePersonMasterData.getString("sex");
			int genderInd = 0;
			if(gender.equals("01")){
				genderInd = 1;
			}
			else if(gender.equals("02")){
				genderInd = 2;
			}
			String birthday = getBirthDay(onePersonMasterData.getString("id_munb"));
			String email = onePersonMasterData.getString("mail_addr");
			String positionThirdId = onePersonMasterData.getJSONObject("pk_post_entity").getString("mdm_code");
			
			String deptThirdId = onePersonMasterData.getJSONObject("pk_post_entity").getString("pk_org");
			//��������Ҫ�������ݣ���ʱû��
			//String parttimeDeptThirdIds = "";
			//String parttimePositionThirdIds = "";
			//String managerThirdId = managerCodes.get(i);
			oneData.put("thirdUserId",thirdUserId);
			oneData.put("username",username);
			oneData.put("fullname",fullname);
			oneData.put("userNo",userNo);
			oneData.put("hireDate",hireDate);
			oneData.put("gender",genderInd);
			oneData.put("birthday",birthday);
			oneData.put("email",email);
			oneData.put("positionThirdId",positionThirdId);
			oneData.put("deptThirdId",deptThirdId);
			//oneData.put("parttimePositionThirdIds",parttimePositionThirdIds);
			//oneData.put("parttimeDeptThirdIds",parttimeDeptThirdIds);
			//oneData.put("managerThirdId",managerThirdId);
			if(!peopleStatus.equals("01")){
				oneData.put("status", 0);
			}
			dataToEStudy.put(oneData);
		}
		System.out.println(dataToEStudy.toString());
		JSONObject syncResults = EStudySync(dataToEStudy);
		boolean syncStatus = syncResults.getBoolean("status");
		boolean syncSuccess = true;
		if(!syncStatus){
			message += "����λ�ã�" + syncResults.getString("location") + "\n";
			message += "����ԭ��" + syncResults.getString("description") + "\n";
			message += "��ϸ��Ϣ��" + syncResults.getString("detail") + "\n";
			for(int i = 0; i < syncMdmCodes.size(); i++){
				JSONObject oneSyncResult = new JSONObject();
				oneSyncResult.put("mdmCode", syncMdmCodes.get(i));
				oneSyncResult.put("entityCode", entityCode);
				oneSyncResult.put("message", "��������������ʧ��");
				oneSyncResult.put("success", false);
			}
		}
		
		else{
			JSONObject syncData = syncResults.getJSONObject("data");
			System.out.println(syncData.toString());
			if(!syncData.getString("msg").equals("success")){
				syncSuccess = false;
			}
			JSONObject syncDetail = syncData.getJSONObject("data");
			JSONArray syncFails = syncDetail.getJSONArray("failThirdIds");
			ArrayList<String> syncFailIds = new ArrayList<String>();
			if(syncFails.length() == 0){
				message += "ͬ�����ݳɹ�";
			}
			else{
				message += "ͬ�����ݲ��ɹ�";
				
				for(int i = 0; i < syncFails.length(); i++){
					syncFailIds.add(syncFails.getString(i));
				}
			}
			for(int i = 0; i < syncMdmCodes.size(); i++){
				JSONObject oneSyncResult = new JSONObject();
				oneSyncResult.put("mdmCode", syncMdmCodes.get(i));
				oneSyncResult.put("entityCode", entityCode);
				if(syncFailIds.contains(syncMdmCodes.get(i))){
					oneSyncResult.put("message", "��������������ʧ�ܣ�������ϢΪ��"+syncData.get("subMsg"));
					oneSyncResult.put("success", false);
				}
				else{
					oneSyncResult.put("message", "�������������ѳɹ�");
					oneSyncResult.put("success", true);
				}
				mdMappings.put(oneSyncResult);
			}
			
		}
		//ͬ�����˽���
		success = syncStatus&syncSuccess;
		personData.put("success", success);
		personData.put("message", message);
		personData.put("mdMappings", mdMappings);
		DataObject body = arg0.getBody();
		
		body.set("personData",personData.toString());
		return null;
	}

}
